package fe.main;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import dlms.comp.common.protocol.ClientRequestContent;
import dlms.comp.common.protocol.FEHeader;
import dlms.comp.common.protocol.ReplicaReplyContent;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPSender;
import fe.bankserver.BankServerInterfacePOA;
import fe.bankserver.CustomerAccount;

public class BankServerFE extends BankServerInterfacePOA implements Runnable
{

	private String serverName;

	private FileWriter feLogFile;
	private PrintWriter feOutputBuffer;
	private String filePath;

	private int clientRequestCounter;
	private int repliedRequestCounter;

	private Map<String, List<ReplicaReplyContent>> repliesMap;
	private Map<String, Object> validatedRepliesMap;

	public BankServerFE() throws IOException
	{
		clientRequestCounter = 0;
		filePath = Configuration.FILE_PATH;
		String fileName = filePath + "FE.txt";
		feLogFile = new FileWriter(fileName, true);
		feOutputBuffer = new PrintWriter(new BufferedWriter(feLogFile));

		repliesMap = Collections.synchronizedMap(new HashMap<String, List<ReplicaReplyContent>>());
		validatedRepliesMap = Collections.synchronizedMap(new HashMap<String, Object>());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
	{
		try
		{
			BankServerFE _feObj = new BankServerFE();

			Thread UDPListener = new Thread(_feObj);
			UDPListener.start();

			ORB _orb = ORB.init(args, null);
			POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));

			byte[] _id = _rootPOA.activate_object(_feObj);
			org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
			String _ior = _orb.object_to_string(_ref);
			// Print IOR in the file
			PrintWriter _file = new PrintWriter(Configuration.FE_IOR_FILE);
			_file.println(_ior);
			_file.close();

			System.out.println("FE is up and running!");

			_rootPOA.the_POAManager().activate();
			_orb.run();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void run()
	{
		DatagramSocket bSocket = null;
		try
		{
			int udpPort = Configuration.FE_PORT;

			bSocket = new DatagramSocket(udpPort);
			// buffer size needs to be bigger, because the UDPProtocol object is
			// fairly big, I've seen some can use array size up to 1100,
			// increased to 2048 to avoid truncation of the packets
			byte[] buffer2 = new byte[2048];

			while (true)
			{
				buffer2 = new byte[2048];

				DatagramPacket reply = new DatagramPacket(buffer2, buffer2.length);
				bSocket.receive(reply);

				UDPProtocol message = processIncomingPacket(reply);
				if (message == null)
				{
					continue;
				}

				addToRepliesMap(Integer.toString(message.getFeHeader().getRequestId()),
						message.getReplicaReply());

			}

		} catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		} finally
		{
			if (bSocket != null)
				bSocket.close();
		}

	}

	@Override
	public String openAccount(CustomerAccount account)
	{		
		ClientRequestContent clientRequest = new ClientRequestContent();

		clientRequest.setRequestType(dlms.comp.common.Configuration.requestType.OPEN_ACCOUNT);

		clientRequest.setEmail(account.emailAddress);
		clientRequest.setFirstName(account.firstName);
		clientRequest.setLastNmae(account.lastName);
		clientRequest.setPassWord(account.password);
		clientRequest.setPhoneNum(account.phoneNumber);

		String finalReply = "";
		try
		{
			int requestId = forwardRequest(account.bankName, clientRequest);
			String requestIdStr = Integer.toString(requestId);

			boolean noReply = true;
			while (noReply)
			{
				if (validatedRepliesMap.containsKey(requestIdStr))
					noReply = false;
			}

			finalReply = validatedRepliesMap.get(requestIdStr).toString();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return finalReply;
	}

	@Override
	public boolean getLoan(String bankName, String accNumber, String password, int loanAmount)
	{
		ClientRequestContent clientRequest = new ClientRequestContent();

		clientRequest.setRequestType(dlms.comp.common.Configuration.requestType.GET_LOAN);

		clientRequest.setAccountId(Integer.parseInt(accNumber));
		clientRequest.setPassWord(password);
		clientRequest.setLoanAmount(loanAmount);

		boolean finalReply = false;
		try
		{
			int requestId = forwardRequest(bankName, clientRequest);
			String requestIdStr = Integer.toString(requestId);

			boolean noReply = true;
			while (noReply)
			{
				if (validatedRepliesMap.containsKey(requestIdStr))
					noReply = false;
			}

			finalReply = (Boolean) validatedRepliesMap.get(requestIdStr);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return finalReply;
	}

	@Override
	public String delayPayment(String bankName, int loanId, String curDueDate, String newDueDate)
	{
		ClientRequestContent clientRequest = new ClientRequestContent();

		clientRequest.setRequestType(dlms.comp.common.Configuration.requestType.DELAY_LOAN);

		clientRequest.setLoanId(loanId);
		clientRequest.setCurrentDueDate(curDueDate);
		clientRequest.setNewDueDate(newDueDate);

		boolean finalReply;
		String reply = "Due date could not be updated!";

		try
		{
			int requestId = forwardRequest(bankName, clientRequest);
			String requestIdStr = Integer.toString(requestId);

			boolean noReply = true;
			while (noReply)
			{
				if (validatedRepliesMap.containsKey(requestIdStr))
					noReply = false;
			}

			finalReply = (Boolean) validatedRepliesMap.get(requestIdStr);

			if (finalReply == true)
				reply = "Due date was successfully updated!";

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return reply;
	}

	@Override
	public String[] printCustomerInfo(String bankName)
	{								
		ClientRequestContent clientRequest = new ClientRequestContent();

		clientRequest.setRequestType(dlms.comp.common.Configuration.requestType.PRINT_INFO);		

		String[] finalReply = new String[100];
		String[] reply = new String[100];				

		try
		{
			int requestId = forwardRequest(bankName, clientRequest);
			String requestIdStr = Integer.toString(requestId);

			boolean noReply = true;
			while (noReply)
			{
				if (validatedRepliesMap.containsKey(requestIdStr))
					noReply = false;
			}

			reply = (String[]) validatedRepliesMap.get(requestIdStr);	
			
			for(int i=0; i<100; i++)
			{
				if(reply[i] == null)
					finalReply[i] = "";
				else 
					finalReply[i] = reply[i];
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return finalReply;
	}

	@Override
	public String transferLoan(int loanID, String CurrentBank, String OtherBank)
	{
		ClientRequestContent clientRequest = new ClientRequestContent();

		clientRequest.setRequestType(dlms.comp.common.Configuration.requestType.TRANSFER_LOAN);

		clientRequest.setOtherBank(OtherBank);
		clientRequest.setCurrentBank(CurrentBank);

		boolean finalReply;
		String reply = "Loan could not be transfered!";

		try
		{
			int requestId = forwardRequest(CurrentBank, clientRequest);
			String requestIdStr = Integer.toString(requestId);

			boolean noReply = true;
			while (noReply)
			{
				if (validatedRepliesMap.containsKey(requestIdStr))
					noReply = false;
			}

			finalReply = (Boolean) validatedRepliesMap.get(requestIdStr);

			if (finalReply == true)
				reply = "Loan transfered successfully!";

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return reply;
	}

	/**
	 * Process incoming UDP packet, and convert it to a UDPProtocol object
	 * 
	 * @param <T>
	 * 
	 * @param receivePacket
	 * @return
	 * @throws ClassNotFoundException
	 */
	private UDPProtocol processIncomingPacket(DatagramPacket receivePacket)
			throws ClassNotFoundException
	{
		byte[] data = receivePacket.getData();
		UDPProtocol protocol = null;
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try
		{
			ObjectInputStream is = new ObjectInputStream(in);

			protocol = (UDPProtocol) is.readObject();
			in.close();
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return protocol;
	}

	private int forwardRequest(String bankName, ClientRequestContent request)
	{
		UDPProtocol msg = new UDPProtocol();
		msg.setClientRequest(request);

		final int currentRequestId = clientRequestCounter++;
		int BankId = 0;

		switch (bankName)
		{
		case Configuration.SERVER_1_NAME:
			BankId = 1;
			break;
		case Configuration.SERVER_2_NAME:
			BankId = 2;
			break;
		case Configuration.SERVER_3_NAME:
			BankId = 3;
			break;
		default:
			System.out.println("Invalid Bank name, please try again.");
		}
		FEHeader feHeader = new FEHeader(currentRequestId, BankId);

		msg.setFeHeader(feHeader);
		try
		{
			UDPSender.sendUDPPacket(dlms.comp.common.Configuration.SEQUENCER_IP,
					dlms.comp.common.Configuration.SEQUENCER_PORT, msg);

			Thread replyController = new Thread(new Runnable()
			{
				public void run()
				{
					processReplies(currentRequestId);
				}
			});

			return currentRequestId;
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	private void processReplies(int requestId)
	{
		Object finalReply = null;
		long startTime = System.currentTimeMillis();
		long elapsedTime;
		long slowestReplyTime = 0;
		boolean waitingForReplies = true;
		boolean slowestReplyTimeSet = false;

		try
		{
			String key = Integer.toString(requestId);
			List<ReplicaReplyContent> replies;

			while (waitingForReplies)
			{
				if (repliesMap.containsKey(key))
				{
					replies = repliesMap.get(key);

					if (replies.size() == 3)
					{
						if (replies.get(0).getResult() == replies.get(1).getResult()
								&& replies.get(1).getResult() == replies.get(2).getResult())
							validatedRepliesMap.put(key, replies.get(1).getResult());
						else if (replies.get(0).getResult() == replies.get(1).getResult())
						{
							validatedRepliesMap.put(key, replies.get(1).getResult());
							notifyReplicasOfBug(replies.get(2).getResultSender());
						} else if (replies.get(0).getResult() == replies.get(2).getResult())
						{
							validatedRepliesMap.put(key, replies.get(0).getResult());
							notifyReplicasOfBug(replies.get(1).getResultSender());
						} else
						// if(replies.get(1).getResult()==replies.get(2).getResult())
						{
							validatedRepliesMap.put(key, replies.get(1).getResult());
							notifyReplicasOfBug(replies.get(0).getResultSender());
						}
						waitingForReplies = false;
					} else if (replies.size() == 2 && slowestReplyTimeSet == false)
					{

						// compare 2 replies, if identical: add reply to
						// approvedRepliesMap
						if (replies.get(0).getResult() == replies.get(1).getResult())
						{
							validatedRepliesMap.put(key, replies.get(1).getResult());
						}
						slowestReplyTime = (System.currentTimeMillis() - startTime) * 2;
						slowestReplyTimeSet = true;
					} else if (slowestReplyTimeSet == true)
					{
						elapsedTime = System.currentTimeMillis() - startTime;

						if (elapsedTime >= slowestReplyTime)
						{
							// possible crash
							waitingForReplies = false;
							notifyReplicasOfCrash(replies.get(0).getResultSender(), replies.get(1)
									.getResultSender());
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void addToRepliesMap(String key, ReplicaReplyContent value)
	{
		if (repliesMap.containsKey(key))
		{
			synchronized (repliesMap.get(key))
			{
				repliesMap.get(key).add(value);
			}
		} else
		{
			List<ReplicaReplyContent> valuesList = new ArrayList<ReplicaReplyContent>();
			valuesList.add(value);
			repliesMap.put(key, valuesList);
		}
	}

	private void notifyReplicasOfBug(String faultyReplica)
	{
		// must notify all three
	}

	private void notifyReplicasOfCrash(String replica1, String replica2)
	{
		// must notify only the two correct ones
	}
}
