package dlms.replica.sai_sun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * List object which keeps all customer info in a hashmap File structure:
 * customerList.txt within the bank directory has all customer info for the
 * bank, and in bank directory, there are customer loan files for each
 * registered customer
 * 
 * 
 * HashMap structure ===================================== |"A": array list of
 * User objects |"B": {User1, User 2} |.. |====================================
 * 
 * @author Sai
 *
 */
public class CustomerList
{
	private String m_bankName = null;
	private HashMap<String, ArrayList<User>> m_map = null;
	private final String CUSTOMER_FILE = "customerList.txt";
	private final String LOAN_FILE_EXT = "_loan.txt";
	private final String MANAGER_ACCT = "1,Manager,Manager,514514514,Manager,manager@bank.com,200000,true";
	private final double DEFAULT_CREDIT_LIMIT = 200000;
	private boolean loadDataFromFile = false;
	private int LOAN_COUNTER = 1;
	private int ACCOUNT_COUNTER = 1;

	/**
	 * Constructor for the customer list
	 * 
	 * @param bank
	 */
	public CustomerList(String bank)
	{
		m_bankName = bank;
		m_map = new HashMap<String, ArrayList<User>>();
	}

	/**
	 * Alternative Constructor for the customer list, support the way not using
	 * txt file as data file
	 * 
	 * @param bank
	 *            name of the bank
	 * @param loadDataFromFile
	 *            read data from txt files if set to true
	 */
	public CustomerList(String bank, boolean loadDataFromFile)
	{
		this.loadDataFromFile = loadDataFromFile;
		m_bankName = bank;
		m_map = new HashMap<String, ArrayList<User>>();
		LOAN_COUNTER = 1;
		ACCOUNT_COUNTER = 1;
	}

	/**
	 * Load customer infos from txt files
	 */
	public void loadMap()
	{
		createBankDir();
		if (loadDataFromFile)
		{
			loadCustomerInfo();
		} else
		{
			loadCustomerInfoFromConfigFile();
		}
	}

	private void loadCustomerInfoFromConfigFile()
	{
		int index = Utility.getIndexFromArray(m_bankName,
				ReplicaConfiguration.BANK_NAME_POOL);
		for (String line : ReplicaConfiguration.SERVER_CUSTOMER_DATA[index].split("\n"))
		{
			// this is what the string looks like
			// 1,Manager,Manager,514514514,Manager,manager@bank.com,200000,true
			// now process the line
			processInfoString(line);
			ACCOUNT_COUNTER++;
		}
	}

	/**
	 * Create bank directory if necessary
	 * 
	 * @return
	 */
	private void createBankDir()
	{
		if (loadDataFromFile)
		{// create bank data directory
			File theDir = new File("data/" + m_bankName.toLowerCase());
			// if the directory does not exist, create it
			if (!theDir.exists())
			{
				try
				{
					theDir.mkdir();
				} catch (SecurityException se)
				{
					se.printStackTrace();
				}
				createCustomerInfoFile();
			}
		}
		// create bank log directory
		File theDir2 = new File("logs/" + m_bankName.toLowerCase());

		if (!theDir2.exists())
		{
			try
			{
				theDir2.mkdir();
			} catch (SecurityException se)
			{
				se.printStackTrace();
			}
		}
	}

	/**
	 * Create customer info file, and put manager account info in Manager info
	 * will be placed into the customerlist.txt file for the bank if the bank is
	 * created for the first time
	 */
	private void createCustomerInfoFile()
	{
		BufferedWriter writer = null;
		String fileName = "data/" + m_bankName.toLowerCase() + "/" + CUSTOMER_FILE;
		try
		{
			// if file doesn't exist, create it, otherwise just open it for
			// write
			File file = new File(fileName);
			if (!file.exists())
			{
				file.createNewFile();
			}

			writer = new BufferedWriter(new FileWriter(fileName));
			// write manager user for the bank
			writer.write(MANAGER_ACCT + "\n");

		} catch (IOException e)
		{
		} finally
		{
			try
			{
				if (writer != null)
					writer.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read customerList.txt line by line and create customers
	 */
	private void loadCustomerInfo()
	{
		String fileName = "data/" + m_bankName.toLowerCase() + "/" + CUSTOMER_FILE;
		File fin = new File(fileName);
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(fin);

			// Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			while ((line = br.readLine()) != null)
			{
				// this is what the string looks like
				// 1,Manager,Manager,514514514,Manager,manager@bank.com,200000,true
				// now process the line
				processInfoString(line);
				ACCOUNT_COUNTER++;
			}

			br.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Process user info string
	 * 
	 * @param line
	 *            string contains custom infos
	 */
	private void processInfoString(String line)
	{
		User usr = new User(line, m_bankName);
		if (loadDataFromFile)
		{
			readLoanFile(usr.getUsr(), usr);
		}
		checkUserLogFile(usr);
		// if the key is already there in the map
		addUserToMap(usr);
	}

	/**
	 * Read loan file for the given user
	 * 
	 * @param usr
	 *            user name
	 * @param obj
	 *            user object
	 */
	private void readLoanFile(String usr, User obj)
	{
		File file = new File("data/" + m_bankName.toLowerCase() + "/" + usr + LOAN_FILE_EXT);
		// create it if it doesn't exist
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		// read it if it's there
		{
			FileInputStream fis;
			try
			{
				fis = new FileInputStream(file);

				// Construct BufferedReader from InputStreamReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));

				String line = null;
				while ((line = br.readLine()) != null)
				{
					String[] loanInfos = line.split(",");
					obj.addLoan(new Loan(Integer.parseInt(loanInfos[0]), loanInfos[1], Double
							.parseDouble(loanInfos[2]), loanInfos[3]));
					LOAN_COUNTER++;
				}

				br.close();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add one customer into hashtable, will locate the proper key, and then add
	 * the user into the correspondent array list
	 * 
	 * @param bank
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 */
	public String addCustomer(String bank, String firstName, String lastName, String emailAddress,
			String phoneNumber, String password)
	{
		User user = new User(Integer.toString(ACCOUNT_COUNTER), firstName, lastName, phoneNumber,
				emailAddress, password, DEFAULT_CREDIT_LIMIT, false, bank);

		if (isUserExist(user))
		{
			return null;
		}
		// create user log file
		if (loadDataFromFile)
		{
			checkUserLogFile(user);

			// create user loan file
			File file = new File("data/" + m_bankName.toLowerCase() + "/" + user.getUsr()
					+ LOAN_FILE_EXT);
			// create it if it doesn't exist
			if (!file.exists())
			{
				try
				{
					file.createNewFile();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		addUserToMap(user);
		ACCOUNT_COUNTER++;
		Logger.getInstance().log(getUserLogFileName(user), "User account has been created");
		writeAllCustomerInfoToFiles();
		return user.getAccount();
	}

	/**
	 * Create user log file, and write one log message into it
	 * 
	 * @param usr
	 */
	private void checkUserLogFile(User usr)
	{
		boolean ret = Logger.getInstance().log(usr.getBank() + "/" + usr.getUsr() + "_log.txt", "");

		if (ret)
		{
			Logger.getInstance().log(usr.getBank() + "/" + usr.getUsr() + "_log.txt",
					"New Customer account created");
		}
	}

	/**
	 * Write customer info list back to files: customerList.txt and customer
	 * loan files
	 */
	public synchronized void writeAllCustomerInfoToFiles()
	{
		if (!loadDataFromFile)
		{
			return;
		}
		try
		{
			// empty the customerList.txt file
			PrintWriter pw;
			pw = new PrintWriter("data/" + m_bankName.toLowerCase() + "/" + CUSTOMER_FILE);
			pw.close();

			for (String key : m_map.keySet())
			{
				// each key corresponds to an array list of customer infos
				for (User usr : m_map.get(key))
				{
					// write user info to customerList.txt
					Utility.writeToFile("data/" + m_bankName.toLowerCase() + "/" + CUSTOMER_FILE,
							usr.toLogString(false));
					// first let's empty the load file for the user and write
					// load
					// info back
					// step 1, empty the file

					pw = new PrintWriter("data/" + m_bankName.toLowerCase() + "/" + usr.getUsr()
							+ LOAN_FILE_EXT);
					pw.close();
					// step 2 write to the file
					if (usr.getLoanList().size() > 0)
					{
						for (Loan loan : usr.getLoanList())
						{
							Utility.writeToFile(
									"data/" + m_bankName.toLowerCase() + "/" + usr.getUsr()
											+ LOAN_FILE_EXT, loan.toLogString());
						}
					} else
					{
						PrintWriter writer = new PrintWriter("data/" + m_bankName.toLowerCase()
								+ "/" + usr.getUsr() + LOAN_FILE_EXT);
						writer.print("");
						writer.close();
					}

				}
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add user object to hashmap
	 * 
	 * @param user
	 */
	private void addUserToMap(User user)
	{
		if (m_map.containsKey(user.getUsr().substring(0, 1).toUpperCase()))
		{
			m_map.get(user.getUsr().substring(0, 1).toUpperCase()).add(user);
		} else
		{
			// if we need to create this key in the map
			ArrayList<User> list = new ArrayList<User>();
			list.add(user);
			m_map.put(user.getUsr().substring(0, 1).toUpperCase(), list);
		}
	}

	/**
	 * Check if user exists in the hashmap
	 * 
	 * @param user
	 * @return
	 */
	public boolean isUserExist(User user)
	{
		boolean ret = false;
		if (m_map.containsKey(user.getUsr().substring(0, 1).toUpperCase()))
		{
			for (User u : m_map.get(user.getUsr().substring(0, 1).toUpperCase()))
			{
				if (u.isSameUser(user))
				{
					ret = true;
					break;
				}
			}
		} else
		{
			ret = false;
		}

		return ret;
	}

	/**
	 * Return user object in the hashmap
	 * 
	 * @param user
	 *            use user first/last names, phone number, email to find the
	 *            user in the hashmap
	 * @return
	 */
	public User getUser(User user)
	{
		User ret = null;
		if (m_map.containsKey(user.getUsr().substring(0, 1).toUpperCase()))
		{
			for (User u : m_map.get(user.getUsr().substring(0, 1).toUpperCase()))
			{
				if (u.isSameUser(user))
				{
					ret = u;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Get user object from the hashmap by account id
	 * 
	 * @param id
	 * @param psw
	 * @return
	 */
	public User getUserByAccountId(String id, String psw)
	{
		User ret = null;
		for (String key : m_map.keySet())
		{
			for (User u : m_map.get(key))
			{
				if (u.getAccount().equals(id) && u.isCorrectPassword(psw))
				{
					return u;
				}
			}
		}
		return ret;
	}

	/**
	 * Get user object from the hashmap by user name
	 * 
	 * @param id
	 * @param psw
	 * @return
	 */
	public User getUserByUserName(String userName, String psw)
	{
		User ret = null;
		for (String key : m_map.keySet())
		{
			for (User u : m_map.get(key))
			{
				if (u.getUsr().equals(userName) && u.isCorrectPassword(psw))
				{
					return u;
				}
			}
		}
		return ret;
	}

	/**
	 * Update existing user object with provided new user object
	 * 
	 * @param user
	 */
	public void updateUser(User user)
	{
		if (m_map.containsKey(user.getUsr().substring(0, 1).toUpperCase()))
		{
			for (User u : m_map.get(user.getUsr().substring(0, 1).toUpperCase()))
			{
				if (u.isSameUser(user))
				{
					m_map.get(user.getUsr().substring(0, 1).toUpperCase()).remove(u);
					m_map.get(user.getUsr().substring(0, 1).toUpperCase()).add(user);
					break;
				}
			}
		}
	}

	/**
	 * Add a loan object to user
	 * 
	 * @param user
	 * @param amount
	 *            amount of the loan
	 * @return
	 */
	public String addLoanToUser(User user, double amount)
	{
		Loan loan = new Loan(LOAN_COUNTER, user.getAccount(), amount, Utility.dateToString(
				Calendar.getInstance().getTime()).replace("2015", "2020"));
		user.getLoanList().add(loan);
		updateUser(user);
		writeAllCustomerInfoToFiles();
		LOAN_COUNTER++;
		return loan.getId();
	}

	/**
	 * Print all customer info to string
	 * 
	 * @return
	 */
	public String getAllCustomerInfoToString(boolean isForPrint)
	{
		String ret = "";
		for (String key : m_map.keySet())
		{
			for (User u : m_map.get(key))
			{
				ret += u.toLogString(isForPrint) + "\n";
			}
		}
		return ret;
	}

	/**
	 * Get user object by a given loan id
	 * 
	 * @param id
	 * @return
	 */
	public User getUserByLoanId(String id)
	{
		User ret = null;
		for (String key : m_map.keySet())
		{
			for (User u : m_map.get(key))
			{
				if (u.getLoanList() != null)
				{
					for (Loan l : u.getLoanList())
					{
						if (l.getId().equals(id))
							return u;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Get user log file name
	 * 
	 * @param u
	 * @return
	 */
	private String getUserLogFileName(User u)
	{
		return m_bankName.toLowerCase() + "/" + u.getUsr() + "_log.txt";
	}

	public ArrayList<User> getUserList(String user)
	{
		return m_map.get(user.substring(0, 1).toUpperCase());
	}

	public void addList(ArrayList<User> list, String key)
	{
		if (!m_map.containsKey(key))
		{
			m_map.put(key, list);
		}
	}

	public boolean deleteUser(User user)
	{
		for (String key : m_map.keySet())
		{
			for (User u : m_map.get(key))
			{
				if (u.getUsr().equals(user.getUsr()))
				{
					m_map.get(key).remove(u);
					return true;
				}
			}
		}
		return false;
	}
}
