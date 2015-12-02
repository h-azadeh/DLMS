package dlms.replica.sai_sun;

import java.io.Serializable;
import java.util.Calendar;

public class Loan implements Serializable
{
	/**
	 * Loan class represents loans
	 */
	private static final long serialVersionUID = 1L;
	private String m_id;
	private String m_accountId;
	private double m_amount;
	private String m_dueDate;
	private final String SEPARATOR = ",";

	/**
	 * Constructor
	 * @param id
	 * @param account
	 * @param amount
	 * @param date
	 */
	public Loan(int id, String account, double amount, String date)
	{
		m_id = Integer.toString(id);
		m_accountId = account;
		m_amount = amount;
		m_dueDate = date;
	}

	/**
	 * Constructor, automatically generate loan id
	 * @param id
	 * @param account
	 * @param amount
	 * @param date
	 */
	public Loan(String account, double amount, String date)
	{
		m_id = generateLoanId();
		m_accountId = account;
		m_amount = amount;
		m_dueDate = date;
	}

	public String toLogString()
	{
		return m_id + SEPARATOR + m_accountId + SEPARATOR + m_amount
				+ SEPARATOR + m_dueDate;
	}

	/**
	 * Set due date for the loan
	 * @param date
	 */
	public void setDueDate(String date)
	{
		m_dueDate = date;
	}

	/**
	 * Will return a unique id
	 * 
	 * @return
	 */
	private String generateLoanId()
	{
		return Long.toString(Calendar.getInstance().getTime().getTime());
	}

	public String getDueDate()
	{
		return m_dueDate;
	}

	public double getAmount()
	{
		return m_amount;
	}

	public String getId()
	{
		return m_id;
	}

	public String getAccount()
	{
		return m_accountId;
	}
	
	public void setAccountId(String id)
	{
		m_accountId = id;
	}

}
