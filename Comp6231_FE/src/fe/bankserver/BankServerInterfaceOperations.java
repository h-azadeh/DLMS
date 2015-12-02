package fe.bankserver;

/**
 * Interface definition: BankServerInterface.
 * 
 * @author OpenORB Compiler
 */
public interface BankServerInterfaceOperations
{
    /**
     * Operation openAccount
     */
    public String openAccount(fe.bankserver.CustomerAccount account);

    /**
     * Operation getLoan
     */
    public boolean getLoan(String bankName, String accNumber, String password, int loanAmount);

    /**
     * Operation delayPayment
     */
    public String delayPayment(String bankName, int loanId, String curDueDate, String newDueDate);

    /**
     * Operation printCustomerInfo
     */
    public fe.bankserver.CustomerAccount[] printCustomerInfo(String bankName);

    /**
     * Operation transferLoan
     */
    public String transferLoan(int loanID, String CurrentBank, String OtherBank);

}
