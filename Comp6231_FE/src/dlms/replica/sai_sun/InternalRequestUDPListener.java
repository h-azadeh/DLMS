package dlms.replica.sai_sun;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

import dlms.replica.sai_sun.ReplicaConfiguration.messageType;

/**
 * UDP listener class, which is a thread running separately from the fe.main
 * thread, it keeps listening on a given port
 * 
 * @author Sai
 *
 */
public class InternalRequestUDPListener implements Runnable
{
	private int m_listeningPort = -1;
	private boolean m_stop = false;
	private BankServer m_server = null;
	private Thread m_thread = null;
	private CountDownLatch m_lock = null;
	private double m_usedAmount = 0;
	private DatagramSocket serverSocket = null;
	private CountDownLatch transferLock = null;
	private boolean shouldRollback = true;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            port to listen on
	 * @param server
	 *            bank server object which owns this listener
	 */
	public InternalRequestUDPListener(int port, BankServer server)
	{
		m_listeningPort = port;
		m_server = server;
	}

	/**
	 * Start listener thread
	 */
	public void startListening()
	{
		m_thread = new Thread(this);
		m_thread.start();
	}

	public void stopRunning()
	{
		m_stop = true;
		if (serverSocket != null)
		{
			serverSocket.close();
		}
		try
		{
			if (m_thread != null)
			{
				m_thread.join();
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			serverSocket = new DatagramSocket(m_listeningPort);
		} catch (SocketException e1)
		{
			e1.printStackTrace();
		}

		while (!m_stop)
		{
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try
			{
				serverSocket.receive(receivePacket);

				LoanProtocol protocol = processIncomingPacket(receivePacket);
				if (protocol == null)
				{
					continue;
				}

				LoanProtocol answer = processProtocol(protocol);
				if (answer != null)
				{
					InetAddress IPAddress = receivePacket.getAddress();
					int port = protocol.getPort();
					answer.setPort(m_listeningPort);

					if (answer.getType() == messageType.TransferAnswer
							&& !answer.getUser().getBank().equalsIgnoreCase(m_server.getBankName()))
					{
						// check if this server already have this loan
						if (m_server.checkIfLoanIdExist(protocol.getLoanInfo()))
						{
							System.err.println("Loan already exists on bank server "
									+ m_server.getBankName());
							answer.setResult(false);
						} else
						{
							answer.setResult(true);
						}
					}
					sendData = generateReturnProtocol(answer);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
							IPAddress, port);

					serverSocket.send(sendPacket);
					if (answer.getType() == messageType.TransferAnswer
							&& !answer.getUser().getBank().equalsIgnoreCase(m_server.getBankName()))
					{
						// if nothing wrong happened during the reply sending
						// phase,
						// then we can add the
						// transfered loan into hashmap now
						// set lock and pass it to bank server
						transferLock = new CountDownLatch(1);
						// have to create another thread calling acceptTransfer
						// since it's a blocking call, will wait for the lock to
						// be lifted
						final LoanProtocol answerCopy = answer;
						Thread t1 = new Thread(new Runnable()
						{
							public void run()
							{
								m_server.acceptTransferedLoan(answerCopy.getUser(),
										answerCopy.getLoanInfo(), transferLock);
							}
						});
						t1.start();
					}
					answer = null;
				}
			} catch (IOException e)
			{
				System.err.println("UDP Listening thread shutdown");
			} catch (ClassNotFoundException e)
			{
				System.err.println("UDP Listening thread shutdown");
			}
		}
		serverSocket.close();
	}

	/**
	 * Process incoming UDP packet, and convert it to a loanProtocol object
	 * 
	 * @param receivePacket
	 * @return
	 * @throws ClassNotFoundException
	 */
	private LoanProtocol processIncomingPacket(DatagramPacket receivePacket)
			throws ClassNotFoundException
	{
		byte[] data = receivePacket.getData();
		LoanProtocol protocol = null;
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try
		{
			ObjectInputStream is = new ObjectInputStream(in);

			protocol = (LoanProtocol) is.readObject();
			in.close();
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return protocol;
	}

	/**
	 * Convert a loanProtocol object to byte array for UDP transmission
	 * 
	 * @param protocol
	 * @return
	 * @throws IOException
	 */
	private byte[] generateReturnProtocol(LoanProtocol protocol) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ObjectOutputStream os = new ObjectOutputStream(outputStream);
		os.writeObject(protocol);
		byte[] data = outputStream.toByteArray();
		return data;
	}

	/**
	 * Analyze the loan protocol object and behave accordingly
	 * 
	 * @param protocol
	 * @return
	 */
	private LoanProtocol processProtocol(LoanProtocol protocol)
	{
		switch (protocol.getType())
		{
		// if it's a requesting protocol, then generate answer for it
		case RequestLoan:
			return generateAnswer(protocol);
			// if it's an answer from another server, then calculate
			// how many loan the user has on the other server
		case LoanAnswer:
			if (protocol.getUser() != null)
			{
				m_usedAmount += protocol.getUser().getLoanAmount();
			}
			m_lock.countDown();
			return null;
		case ValidateAdmin:
			boolean result = m_server.validateAdminUser(protocol.getUser().getUsr(), protocol
					.getUser().getPassword());
			LoanProtocol l = new LoanProtocol("", protocol.getHost(), protocol.getPort(), null,
					null);
			l.setResult(result);
			return l;
		case Transfer:
			return ProcessTransferRequest(protocol);

		case TransferAnswer:
			return ProcessTransferAnswer(protocol);
		case RollBack:
			return processRollback(protocol);
			// commit that everything is ok, so now we can release the transfer
			// lock
		case Commit:
			transferLock.countDown();
			shouldRollback = false;
			return null;
		default:
			return null;
		}
	}

	/**
	 * rollback the info in the loan protocol
	 * 
	 * @param protocol
	 *            contains info to be rolled back
	 * @return
	 */
	private LoanProtocol processRollback(LoanProtocol protocol)
	{
		transferLock.countDown();
		shouldRollback = true;
		return null;
	}

	/**
	 * Generate answer for loan transfer
	 * 
	 * @param protocol
	 * @return
	 */
	private LoanProtocol ProcessTransferRequest(LoanProtocol protocol)
	{
		// only change the type of the protocol to answer
		protocol.setType(messageType.TransferAnswer);
		return protocol;
	}

	/**
	 * Process answer for loan transfer operation
	 * 
	 * @param protocol
	 * @return
	 */
	private LoanProtocol ProcessTransferAnswer(LoanProtocol protocol)
	{
		// if result in the protocol indicates transfer is accepted by target
		// bank, then remove loan from the current bank
		if (protocol.getResult())
		{
			if (m_server.removeLoan(protocol.getLoanInfo()))
			{
				m_lock.countDown();
				m_lock = null;
				protocol.setType(messageType.Commit);
			}
			// if something wrong happened during remove loan... then we send
			// roll back message to the other server
			else
			{
				protocol.setType(messageType.RollBack);
			}
		}

		return protocol;
	}

	/**
	 * Generate answer to the request
	 * 
	 * @param request
	 * @return
	 */
	private LoanProtocol generateAnswer(LoanProtocol request)
	{
		User user = m_server.lookUpUser(request.getUser());
		if (user != null)
		{
			user.calculateCurrentLoanAmount();
		}
		LoanProtocol answer = new LoanProtocol(request.getId(), request.getHost(),
				request.getPort(), user, messageType.LoanAnswer);
		return answer;
	}

	public void setRequestLock(CountDownLatch latch)
	{
		m_lock = latch;
		m_usedAmount = 0;
	}

	public double getLastRequestResult()
	{
		return m_usedAmount;
	}

	public boolean shouldServerRollback()
	{
		boolean ret = shouldRollback;
		shouldRollback = true;
		return ret;
	}
}
