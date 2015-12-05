package dlms.replica.milad;

public class Account  {
    private Integer accountNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private Integer creditLimit;
    private String userName;
    
    
    public Account(Integer accountNumber, String firstName, String lastName, String email, String phoneNumber, String password, Integer creditLimit, String userName) {
		super();
		this.accountNumber = accountNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.creditLimit = creditLimit;
		this.userName = userName;
	}
    
    public Integer getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}
   
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Integer getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(Integer creditLimit) {
		this.creditLimit = creditLimit;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}

