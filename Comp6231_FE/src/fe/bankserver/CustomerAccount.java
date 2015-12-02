package fe.bankserver;

/**
 * Struct definition: CustomerAccount.
 * 
 * @author OpenORB Compiler
*/
public final class CustomerAccount implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member bankName
     */
    public String bankName;

    /**
     * Struct member customerAccountNumber
     */
    public String customerAccountNumber;

    /**
     * Struct member firstName
     */
    public String firstName;

    /**
     * Struct member lastName
     */
    public String lastName;

    /**
     * Struct member emailAddress
     */
    public String emailAddress;

    /**
     * Struct member phoneNumber
     */
    public String phoneNumber;

    /**
     * Struct member password
     */
    public String password;

    /**
     * Struct member creditLimit
     */
    public int creditLimit;

    /**
     * Struct member loansInfo
     */
    public String loansInfo;

    /**
     * Default constructor
     */
    public CustomerAccount()
    { }

    /**
     * Constructor with fields initialization
     * @param bankName bankName struct member
     * @param customerAccountNumber customerAccountNumber struct member
     * @param firstName firstName struct member
     * @param lastName lastName struct member
     * @param emailAddress emailAddress struct member
     * @param phoneNumber phoneNumber struct member
     * @param password password struct member
     * @param creditLimit creditLimit struct member
     * @param loansInfo loansInfo struct member
     */
    public CustomerAccount(String bankName, String customerAccountNumber, String firstName, String lastName, String emailAddress, String phoneNumber, String password, int creditLimit, String loansInfo)
    {
        this.bankName = bankName;
        this.customerAccountNumber = customerAccountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.creditLimit = creditLimit;
        this.loansInfo = loansInfo;
    }

}
