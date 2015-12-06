package dlms.replica.milad;

import java.util.Date;

/**
 * Bank Loan Class
 * 
 * @author Milad
 *
 */

public class Loan  {
    private Integer accountNumber;
    private Integer loanID;
    private Integer loanAmount;
    private Date loanDueDate;
  
        
    public Loan(Integer accountNumber, Integer loanID, Integer loanAmount, java.util.Date newdueDate) {
		super();
		this.accountNumber = accountNumber;
		this.loanID = loanID;
		this.loanAmount = loanAmount;
		this.loanDueDate = newdueDate;
	}
    
    public Integer getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}
   
	public Integer getLoanID() {
		return loanID;
	}
	public void setLoanID(Integer loanID) {
		this.loanID = loanID;
	}
	
	public Integer getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Integer loanAmount) {
		this.loanAmount = loanAmount;
	}
	
	public Date getLoanDueDate() {
		return loanDueDate;
	}
	public void setLoanDueDate(Date loanDueDate) {
		this.loanDueDate = loanDueDate;
	}

}