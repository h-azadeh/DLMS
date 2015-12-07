package dlms.replica.azadeh;

import java.io.Serializable;
import java.util.Date;

public class Loan implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int loanId;
	private String customerAccountNumber;
	private double amount;
	private Date dueDate;
	
	public Loan()
	{
		
	}
	
	public int GetLoanId()
	{		
		return loanId;
	}
	
	public void SetLoanId(int id)
	{
		loanId = id;
	}
	
	public String GetAccNumber()
	{		
		return customerAccountNumber;
	}
	
	public void SetAccNumber(String accNumber)
	{
		customerAccountNumber = accNumber;
	}
	
	public double GetAmount()
	{		
		return amount;
	}
	
	public void SetAmount(double loanAmount)
	{
		amount = loanAmount;
	}
	
	public Date GetDueDate()
	{		
		return dueDate;
	}
	
	public void SetDueDate(Date date)
	{
		dueDate = date;
	}
}
