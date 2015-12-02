package dlms.replica.sai_sun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dlms.comp.common.MulticastReceiver;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPNotifierIF;
import dlms.replica.sai_sun.ReplicaConfiguration.messageType;

/**
 * Bank server class contains customer and manager services, and it owns a UDP
 * listener thread
 * 
 * @author Sai
 *
 */
public class BankServer
{

	private String m_name;
	private CustomerList m_customerList;
	private Thread m_corbaThread;
	private InternalRequestUDPListener m_udpHandler;
	private int m_rmiPort;
	private int m_udpPort;
	// private int m_tcpPort;
	// lock the fe.main thread after a loan request to other two banks are sent,
	// unlock the thread once the other two banks have responded, if no one
	// respond, then request fails, if one of the bank responds, then use the
	// data received from that bank and current bank to decide if customer can
	// get the loan
	private CountDownLatch m_loanRequstLock = null;
	// private TCPListener m_tcpHandler;
	private CountDownLatch m_loanTransferLock;
	private String lastRemovedLoanId = "";
	private User oldUserData = null;

	/**
	 * Constructor , initialize data structures
	 * 
	 * @param udpPort
	 *            port of the UDP listening will be listening on
	 * @param name
	 *            name of the bank
	 * @param isCorbaServer
	 *            true if running a corba server
	 */
	public BankServer(int udpPort, String name)
	{
		m_name = name;
		m_customerList = new CustomerList(name,false);
		m_customerList.loadMap();

		m_udpHandler = new InternalRequestUDPListener(udpPort, this);
		m_udpHandler.startListening();
		m_udpPort = udpPort;
		
		System.out.println("Bank server " + m_name + " is listening on UDP port " + m_udpPort);
	}

	public int getRmiPort()
	{
		return m_rmiPort;
	}

	/**
	 * Delay a loan payment
	 * 
	 * @param bank
	 * @param loanID
	 * @param currentDueDate
	 * @param newDueDate
	 * @return
	 */
	public boolean delayPayment(String bank, String loanID, String currentDueDate, String newDueDate)
	{
		// check if given bank name matches current bank server
		if (bank.equalsIgnoreCase(m_name))
		{

			// find the user by loan id
			User u = m_customerList.getUserByLoanId(loanID);

			ArrayList<User> currentlist = m_customerList.getUserList(u.getUsr());
			synchronized (currentlist)
			{
				Logger.getInstance().log(getUserLogFileName(u),
						"Requested to delay a loan to " + newDueDate);
				Logger.getInstance().log(getManagerLogFileName(),
						"Requested to delay a loan to " + newDueDate);
				Loan loan = null;
				Loan oldLoan = null;
				// get the loan object
				for (Loan l : u.getLoanList())
				{
					if (l.getId().equals(loanID))
					{
						loan = l;
						oldLoan = l;
						break;
					}
				}

				// check if given current date matches the date on file,if not
				// then return false, otherwise, update the loan due date
				// to the new one
				if (loan.getDueDate().equals(currentDueDate))
				{
					loan.setDueDate(newDueDate);
					u.getLoanList().remove(oldLoan);
					u.getLoanList().add(loan);
					m_customerList.updateUser(u);
					m_customerList.writeAllCustomerInfoToFiles();
					Logger.getInstance().log(getUserLogFileName(u),
							"Successfully requested to delay a loan to " + newDueDate);
					return true;

				} else
				{
					Logger.getInstance().log(getUserLogFileName(u),
							"Current due date doesn't match due date on file");
				}
			}
		}
		return false;
	}

	public String printCustomerInfo(String bank)
	{
		Logger.getInstance().log(getManagerLogFileName(), "Requested to print all customer info");
		return m_customerList.getAllCustomerInfoToString(true);
	}

	public String openAccount(String bank, String firstName, String lastName, String emailAddress,
			String phoneNumber, String password)
	{
		ArrayList<User> currentlist = m_customerList.getUserList(firstName);
		if (currentlist == null)
		{
			currentlist = new ArrayList<User>();
			m_customerList.addList(currentlist, firstName.substring(0, 1).toUpperCase());
		}
		synchronized (currentlist)
		{
			return m_customerList.addCustomer(bank, firstName, lastName, emailAddress, phoneNumber,
					password);
		}
	}

	public String getLoan(String bank, String accountNumber, String password, double loanAmount)
	{
		// get user by account id
		User user = m_customerList.getUserByAccountId(accountNumber, password);
		if (user == null)
		{
			return "";
		}
		ArrayList<User> currentlist = m_customerList.getUserList(user.getUsr());
		synchronized (currentlist)
		{
			Logger.getInstance().log(getUserLogFileName(user),
					"User has requested to get a loan of " + loanAmount);

			// check user loan amount on current server, if it's already
			// exceeds the credit limit, return null;
			if (user.calculateCurrentLoanAmount() >= user.getCreditLimit())
			{
				Logger.getInstance().log(getUserLogFileName(user),
						"User doesn't have enought credit to apply " + loanAmount);
				return "";
			}

			// generate loan protocol object to send to the other 2 servers
			int port1 = Utility.getAvailablePort();
			int port2 = Utility.getAvailablePort();
			user.calculateCurrentLoanAmount();

			// set lock, so no action can be done before we get answers from the
			// other two servers
			m_loanRequstLock = new CountDownLatch(2);

			// spawn a upd thread to handle it

			InternalRequestUDPListener listner1 = new InternalRequestUDPListener(port1, this);
			InternalRequestUDPListener listner2 = new InternalRequestUDPListener(port2, this);
			Thread thread1 = null;
			Thread thread2 = null;

			int counter = 0;
			// send UDP packets
			for (int i = 0; i < ReplicaConfiguration.PORT_POOL.length; i++)
			{
				if (ReplicaConfiguration.PORT_POOL[i] != m_udpPort)
				{
					try
					{
						// creating 2 threads to send and receive from the other
						// 2 bank servers
						if (counter == 0)
						{
							LoanProtocol p = new LoanProtocol(Utility.generateRandomUniqueId(),
									ReplicaConfiguration.HOST_NAME, port1, user, messageType.RequestLoan);
							listner1.setRequestLock(m_loanRequstLock);
							thread1 = new Thread(listner1);
							thread1.start();
							// give it some time to allow UDP listener up and
							// running
							Thread.sleep(50);

							Utility.sendUDPPacket(ReplicaConfiguration.HOST_NAME,
									ReplicaConfiguration.PORT_POOL[i], p);

							counter++;
						} else if (counter == 1)
						{
							LoanProtocol p = new LoanProtocol(Utility.generateRandomUniqueId(),
									ReplicaConfiguration.HOST_NAME, port2, user, messageType.RequestLoan);
							listner2.setRequestLock(m_loanRequstLock);
							thread2 = new Thread(listner2);
							thread2.start();
							// give it some time to allow UDP listener up and
							// running
							Thread.sleep(50);

							Utility.sendUDPPacket(ReplicaConfiguration.HOST_NAME,
									ReplicaConfiguration.PORT_POOL[i], p);
						}

					} catch (IOException e)
					{
						e.printStackTrace();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			try
			{
				// wait for 60 seconds for the answers
				m_loanRequstLock.await(60, TimeUnit.SECONDS);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			// if user still have credit, then give user the loan
			// add loan objcet to user loan list
			if (user.getLoanAmount() + listner1.getLastRequestResult()
					+ listner2.getLastRequestResult() + loanAmount <= user.getCreditLimit())
			{
				String ret = m_customerList.addLoanToUser(user, loanAmount);
				Logger.getInstance().log(getUserLogFileName(user),
						"User has successfully requested to get a loan of " + loanAmount);
				listner1.stopRunning();
				listner2.stopRunning();
				try
				{
					thread1.join();
					thread2.join();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				return ret;
			} else
			{
				listner1.stopRunning();
				listner2.stopRunning();
				try
				{
					thread1.join();
					thread2.join();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			Logger.getInstance().log(getUserLogFileName(user),
					"User has failed to get a loan of " + loanAmount);
		}

		return "";
	}

	public String getCustomerServerName()
	{
		return m_name + "_customer";
	}

	public String getManagerServerName()
	{
		return m_name + "_manager";
	}

	public User lookUpUser(User usr)
	{
		return m_customerList.getUser(usr);
	}

	private String getUserLogFileName(User u)
	{
		return m_name.toLowerCase() + "/" + u.getUsr() + "_log.txt";
	}

	private String getManagerLogFileName()
	{
		return m_name.toLowerCase() + "/Manager_log.txt";
	}

	public String getBankName()
	{
		return m_name;
	}

	/**
	 * Remove a specific loan from user and update the txt files
	 * 
	 * @param loan
	 *            loan to be removed
	 * @return
	 */
	public boolean removeLoan(Loan loan)
	{
		if (lastRemovedLoanId.equals(loan.getId()))
		{
			return false;
		}
		lastRemovedLoanId = loan.getId();
		// find user by loan id
		User usr = m_customerList.getUserByLoanId(loan.getId());
		ArrayList<User> currentlist = m_customerList.getUserList(usr.getUsr());
		synchronized (currentlist)
		{

			// remove loan from user loan list
			for (int i = 0; i < usr.getLoanList().size(); i++)
			{
				if (usr.getLoanList().get(i).getId().equals(loan.getId()))
				{
					usr.getLoanList().remove(i);
				}
			}
			m_customerList.updateUser(usr);
			// write all info back to txt file
			m_customerList.writeAllCustomerInfoToFiles();
		}
		return true;
	}

	public boolean checkIfLoanIdExist(Loan loan)
	{
		if (m_customerList.getUserByLoanId(loan.getId()) != null)
		{
			return true;
		}
		return false;
	}

	/**
	 * Accept loan transfer and put loan into user object
	 * 
	 * @param user
	 *            User to transfer the loan
	 * @param loan
	 *            loan to be transfered
	 * @param lock
	 *            transfer lock which will block until reply is received or time
	 *            out
	 * @return
	 */
	public boolean acceptTransferedLoan(User user, Loan loan, CountDownLatch lock)
	{
		ArrayList<User> currentlist = m_customerList.getUserList(user.getUsr());
		if (currentlist == null)
		{
			currentlist = new ArrayList<User>();
			m_customerList.addList(currentlist, user.getUsr().substring(0, 1).toUpperCase());
		}
		synchronized (currentlist)
		{
			// first check if user exists
			oldUserData = null;
			boolean exist = m_customerList.isUserExist(user);
			if (exist)
			{
				loan.setAccountId(m_customerList.getUser(user).getAccount());
				oldUserData = m_customerList.getUser(user);
				m_customerList.getUser(user).getLoanList().add(loan);
			} else
			{
				String account = m_customerList.addCustomer(m_name, user.getFirstName(),
						user.getLastName(), user.getEmail(), user.getPhone(), user.getPassword());
				loan.setAccountId(account);
				m_customerList.getUser(user).getLoanList().add(loan);
			}

			// lock here wait for commit or rollbackUDP message arrive
			try
			{
				boolean retCode = false;
				int count = 0;
				while (count < 100)
				{
					if (lock.getCount() == 0)
					{
						retCode = true;
						break;
					} else
					{
						Thread.sleep(50);
					}
					count++;
				}
				// if we received reply and it was a commit message
				if (retCode && !m_udpHandler.shouldServerRollback())
				{
					Logger.getInstance().log(m_name.toUpperCase() + "customer_server_log.txt",
							"Loan transfer accepted");
					System.out.println("Loan transfer accepted by bank " + m_name);
					m_customerList.writeAllCustomerInfoToFiles();
				} else
				{
					// now it's the rollbak case
					rollBack(user);
					Logger.getInstance().log(m_name.toUpperCase() + "customer_server_log.txt",
							"Loan transfer has been rolled back");
					System.out.println("Loan transfer rolled back by bank " + m_name);
				}

			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean validateUser(String id, String password)
	{
		if (m_customerList.getUserByAccountId(id, password) == null
				&& m_customerList.getUserByUserName(id, password) == null)
		{
			return false;
		}

		return true;
	}

	/**
	 * Use id and password validate admin user identity
	 * 
	 * @param id
	 * @param password
	 * @return
	 */
	public boolean validateAdminUser(String id, String password)
	{
		if (m_customerList.getUserByUserName(id, password) == null)
		{
			return false;
		} else if (!m_customerList.getUserByUserName(id, password).isAdmin())
		{
			return false;
		}

		return true;
	}

	/**
	 * Transfer loan from current bank to other bank
	 * 
	 * @param loanID
	 * @param currentBank
	 * @param otherBank
	 * @return
	 */
	public String transferLoan(String loanID, String currentBank, String otherBank)
	{
		// create the lock
		m_loanTransferLock = new CountDownLatch(1);
		// if provided name doesn't match then return
		if (!currentBank.equalsIgnoreCase(m_name))
		{
			return "";
		}
		/**
		 * try to get TCP listening port number for the other bank
		 */
		int targetBankUDPPort = Utility.getUDPPortByBankName(otherBank);
		// if other bank doesn't exist, return
		if (targetBankUDPPort == -1)
		{
			return "";
		}

		// get user by loan id
		User user = m_customerList.getUserByLoanId(loanID);

		// if user doesn't exist, then return
		if (user == null)
		{
			return "";
		}

		// start real logic
		synchronized (user)
		{
			Logger.getInstance().log(getUserLogFileName(user),
					"User has requested to transfer loan from " + currentBank + " to " + otherBank);

			// locate the loan by loan id
			Loan loanToTransfer = null;
			for (Loan l : user.getLoanList())
			{
				if (l.getId().equals(loanID))
				{
					loanToTransfer = l;
					break;
				}
			}
			// loan located

			// set lock in the tcp listener
			m_udpHandler.setRequestLock(m_loanTransferLock);

			// now create the protocol to be sent to other bank server
			LoanProtocol protocol = new LoanProtocol(Utility.generateRandomUniqueId(), "localhost",
					m_udpPort, user, messageType.Transfer, loanToTransfer);
			try
			{
				// send protocol over TCP
				Utility.sendUDPPacket("localhost", targetBankUDPPort, protocol);
				// now wait for 10 seconds
				boolean result = m_loanTransferLock.await(60, TimeUnit.SECONDS);
				if (result == false)
				{
					return "";
				} else
				{
					return "SUCCESS";
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return "";

	}

	/**
	 * Roll back info in the protocol
	 * 
	 * @param user
	 *            rollbak the user data
	 */
	public void rollBack(User user)
	{
		ArrayList<User> currentlist = m_customerList.getUserList(user.getUsr());
		if (currentlist == null)
		{
			return;
		}
		synchronized (currentlist)
		{
			// if user didn't exist before transfer, then deleted
			if (oldUserData == null)
			{
				m_customerList.deleteUser(user);
			}
			// if the user exists, then just replace it with the old data
			else
			{
				m_customerList.updateUser(oldUserData);
			}
		}
		m_customerList.writeAllCustomerInfoToFiles();
	}
}
