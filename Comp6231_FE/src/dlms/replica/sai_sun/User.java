package dlms.replica.sai_sun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1754768068731150480L;
	private String pri_accountId;
	private String pri_firstName;
	private String pri_lastName;
	private String pri_phone;
	private String pri_email;
	private String pri_psw;
	private double pri_creditLimit;
	private String pri_bank;
	private String m_usr;

	// flag for customer to be an admin
	protected boolean pri_isAdmin = false;

	private final String SEPERATOR = ",";

	private transient ArrayList<Loan> m_loanList = null;
	private double m_currentLoanAmount = 0;

	/**
	 * Constructor when all user infos are provided
	 * 
	 * @param account
	 * @param fn
	 * @param ln
	 * @param phone
	 * @param email
	 * @param psw
	 * @param credit
	 * @param admin
	 * @param bank
	 */
	public User(String account, String fn, String ln, String phone,
			String email, String psw, double credit, boolean admin, String bank)
	{
		pri_accountId = account;
		pri_firstName = fn;
		pri_lastName = ln;
		pri_phone = phone;
		pri_email = email;
		pri_psw = psw;
		pri_bank = bank;
		pri_isAdmin = admin;
		pri_creditLimit = credit;
		m_loanList = new ArrayList<Loan>();
		if (!admin)
		{
			m_usr = pri_firstName + "_" + pri_lastName;
		} else
		{
			m_usr = "Manager";
		}
	}

	/**
	 * Constructor for no account number is provided
	 * 
	 * @param fn
	 * @param ln
	 * @param phone
	 * @param email
	 * @param psw
	 * @param credit
	 * @param admin
	 * @param bank
	 */
	public User(String fn, String ln, String phone, String email, String psw,
			double credit, boolean admin, String bank)
	{
		pri_accountId = generateAccountId();
		pri_firstName = fn;
		pri_lastName = ln;
		pri_phone = phone;
		pri_email = email;
		pri_psw = psw;
		pri_bank = bank;
		pri_isAdmin = admin;
		pri_creditLimit = credit;
		m_loanList = new ArrayList<Loan>();
		if (!admin)
		{
			m_usr = pri_firstName + "_" + pri_lastName;
		} else
		{
			m_usr = "Manager";
		}
	}

	public User(String info, String bank)
	{
		String[] infos = info.split(",");
		pri_accountId = infos[0];
		pri_firstName = infos[1];
		pri_lastName = infos[2];
		pri_phone = infos[3];
		pri_psw = infos[4];
		pri_isAdmin = infos[7].equals("true");
		pri_email = infos[5];

		pri_bank = bank;
		pri_creditLimit = Double.parseDouble(infos[6]);
		m_loanList = new ArrayList<Loan>();
		if (!pri_isAdmin)
		{
			m_usr = pri_firstName + "_" + pri_lastName;
		} else
		{
			m_usr = "Manager";
		}
	}
	
	public User(String info)
	{
		m_usr = info.split(";;d")[0];
		pri_psw = info.split(";;d")[1];
	}

	/**
	 * Check if the provided credentials match with current user
	 * 
	 * @param usr
	 * @param psw
	 * @return true if credentials match, otherwise false
	 */
	public boolean credentialMatch(String firstName, String lastName, String psw)
	{
		return ((pri_lastName.equalsIgnoreCase(lastName))
				&& (pri_firstName.equalsIgnoreCase(firstName)) && (pri_psw
					.equalsIgnoreCase(psw)));
	}

	public boolean isAdmin()
	{
		return pri_isAdmin;
	}

	public boolean validateInstitution(String bank)
	{
		return pri_bank.equalsIgnoreCase(bank);
	}

	public String getAccount()
	{
		return pri_accountId;
	}

	public String toString()
	{
		return pri_accountId + SEPERATOR + pri_firstName + SEPERATOR
				+ pri_lastName + SEPERATOR + pri_phone + SEPERATOR + pri_email
				+ SEPERATOR + pri_creditLimit;
	}

	public String getCompareString()
	{
		return pri_firstName + SEPERATOR + pri_lastName + SEPERATOR + pri_phone
				+ SEPERATOR + pri_email;
	}

	/**
	 * Check if given user is the same as current user
	 * 
	 * @param user
	 * @return
	 */
	public boolean isSameUser(User user)
	{
		return getCompareString().equals(user.getCompareString());
	}

	public String getUsr()
	{
		return m_usr;
	}

	public ArrayList<Loan> getLoanList()
	{
		return m_loanList;
	}

	public void addLoan(Loan loan)
	{
		m_loanList.add(loan);
	}

	private String generateAccountId()
	{
		return Long.toString(Calendar.getInstance().getTime().getTime());
	}

	public String getBank()
	{
		return pri_bank;
	}

	public String toLogString(boolean isForPrint)
	{
		return pri_accountId + SEPERATOR + pri_firstName + SEPERATOR
				+ pri_lastName + SEPERATOR + pri_phone + SEPERATOR + pri_psw
				+ SEPERATOR + pri_email + SEPERATOR + pri_creditLimit
				+ SEPERATOR + isAdmin()
				+ (isForPrint ? "\nLoan info:\n" + toLoanString() : "");
	}

	public String toLoanString()
	{
		String ret = "";
		for (Loan l : m_loanList)
		{
			ret += l.toLogString() + "\n";
		}

		return ret;
	}

	public boolean isCorrectPassword(String psw)
	{
		return pri_psw.equals(psw);
	}

	/**
	 * Calculate current user loan amount
	 * 
	 * @return
	 */
	public double calculateCurrentLoanAmount()
	{
		m_currentLoanAmount = 0;
		for (Loan l : m_loanList)
		{
			m_currentLoanAmount += l.getAmount();
		}

		return m_currentLoanAmount;
	}

	public double getLoanAmount()
	{
		return m_currentLoanAmount;
	}

	public double getCreditLimit()
	{
		return pri_creditLimit;
	}

	public String getFirstName()
	{
		return pri_firstName;
	}

	public String getLastName()
	{
		return pri_lastName;
	}

	public String getEmail()
	{
		return pri_email;
	}

	public String getPhone()
	{
		return pri_phone;
	}

	public String getPassword()
	{
		return pri_psw;
	}
	
	

}
