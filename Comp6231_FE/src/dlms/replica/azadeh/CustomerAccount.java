package dlms.replica.azadeh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerAccount implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String customerAccountNumber;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String password;
	private long creditLimit;
	
	public CustomerAccount()
	{
		customerLoansList = new ArrayList<Loan>();
	}
	
	//used only for printCustomerInfo() method
	private List<Loan> customerLoansList;
	
	
	public String GetAccNumber()
	{		
		return customerAccountNumber;
	}
	
	public void SetAccNumber(String accNumber)
	{
		customerAccountNumber = accNumber;
	}
	
	
	public String GetFirstName()
	{		
		return firstName;
	}
	
	public void SetFirstName(String fName)
	{
		firstName = fName;
	}
	
	public String GetLastName()
	{		
		return lastName;
	}
	
	public void SetLastName(String lName)
	{
		lastName = lName;
	}
	
	public String GetEmailAddress()
	{		
		return emailAddress;
	}
	
	public void SetEmailAddress(String emailAdd)
	{
		emailAddress = emailAdd;
	}
	
	public String GetPhoneNumber()
	{		
		return phoneNumber;
	}
	
	public void SetPhoneNumber(String phoneNum)
	{
		phoneNumber = phoneNum;
	}
	
	
	public String GetPassword()
	{		
		return password;
	}
	
	public void SetPassword(String pw)
	{
		password = pw;
	}
	
	public double GetCreditLimit()
	{		
		return creditLimit;
	}
	
	public void SetCreditLimit(long credit)
	{
		creditLimit = credit;
	}
	
	public List<Loan> GetcustomerLoansList()
	{
		return customerLoansList;
	}
	
	public void SetcustomerLoansList(List<Loan> loanList)
	{
		customerLoansList = loanList;
	}
}
