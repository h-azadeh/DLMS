package dlms.comp.common.protocol;

import java.io.Serializable;

import dlms.comp.common.Configuration;

public class ClientRequestContent implements Serializable
{
	private static final long serialVersionUID = 7359779695244125794L;
	private String firstName = null;
	private String lastNmae = null;
	private int accountId = -1;
	private String phoneNum = null;
	private String email = null;
	private String userName = null;
	private String passWord = null;
	private String currentBank = null;
	private String otherBank = null;
	private int loanId = -1;
	private String currentDueDate = null;
	private String newDueDate = null;
	private int loanAmount = 0;
	private Configuration.requestType requestType = null;

	public Configuration.requestType getRequestType()
	{
		return requestType;
	}

	public void setRequestType(Configuration.requestType requestType)
	{
		this.requestType = requestType;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastNmae()
	{
		return lastNmae;
	}

	public void setLastNmae(String lastNmae)
	{
		this.lastNmae = lastNmae;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public String getPhoneNum()
	{
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum)
	{
		this.phoneNum = phoneNum;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassWord()
	{
		return passWord;
	}

	public void setPassWord(String passWord)
	{
		this.passWord = passWord;
	}

	public String getCurrentBank()
	{
		return currentBank;
	}

	public void setCurrentBank(String currentBank)
	{
		this.currentBank = currentBank;
	}

	public String getOtherBank()
	{
		return otherBank;
	}

	public void setOtherBank(String otherBank)
	{
		this.otherBank = otherBank;
	}

	public int getLoanId()
	{
		return loanId;
	}

	public void setLoanId(int loanId)
	{
		this.loanId = loanId;
	}

	public String getCurrentDueDate()
	{
		return currentDueDate;
	}

	public void setCurrentDueDate(String currentDueDate)
	{
		this.currentDueDate = currentDueDate;
	}

	public String getNewDueDate()
	{
		return newDueDate;
	}

	public void setNewDueDate(String newDueDate)
	{
		this.newDueDate = newDueDate;
	}

	public int getLoanAmount()
	{
		return loanAmount;
	}

	public void setLoanAmount(int loanAmount)
	{
		this.loanAmount = loanAmount;
	}
}
