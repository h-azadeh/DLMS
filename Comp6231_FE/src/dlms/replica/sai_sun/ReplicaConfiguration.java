package dlms.replica.sai_sun;

public class ReplicaConfiguration
{

    /*
     * Configuration for RMI project
     * 
     */
    public static final String SERVER_1_NAME = "Bank1";
    public static final String SERVER_2_NAME = "Bank2";
    public static final String SERVER_3_NAME = "Bank3";


    public final static String HOST_NAME = "localhost";

    public enum messageType
    {
        RequestLoan, LoanAnswer, Transfer, TransferAnswer, ValidateAdmin, RollBack, Commit;
    }

    // pool of udp ports
    public final static int[] PORT_POOL =
    { 10033, 10034, 10035 };

    // bank name pool
    public final static String[] BANK_NAME_POOL =
    { ReplicaConfiguration.SERVER_1_NAME, ReplicaConfiguration.SERVER_2_NAME,
    	ReplicaConfiguration.SERVER_3_NAME };

    public final static String BANK1_CUSTOMER_DATA = "2,sai,sun,514514514,1234,saisun@test.com,200000.0,false\n3,cat,cat,cat,1234,cat,200000.0,false\n4,test1,test1,21321412123,1234,test1,200000.0,false\n1,Manager,Manager,514514514,Manager,manager@bank.com,200000.0,true";
    public final static String BANK2_CUSTOMER_DATA = "1,Manager,Manager,514514514,Manager,manager@bank.com,200000.0,true";
    public final static String BANK3_CUSTOMER_DATA = "1,Manager,Manager,514514514,Manager,manager@bank.com,200000.0,true";

    public final static String[] SERVER_CUSTOMER_DATA =
    { BANK1_CUSTOMER_DATA, BANK2_CUSTOMER_DATA, BANK3_CUSTOMER_DATA };
}
