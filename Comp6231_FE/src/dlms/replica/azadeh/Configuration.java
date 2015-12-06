package dlms.replica.azadeh;

import dlms.replica.sai_sun.ReplicaConfiguration;

/*
 * Replica config
 */
public class Configuration {
	/*
	Configuration for web service - Servers	
	*/
	public static final String SERVER_1_NAME = "Bank1";
	public static final String SERVER_2_NAME = "Bank2";
	public static final String SERVER_3_NAME = "Bank3";		
	
	public static final int UDP_SERVER_1_PORT = 6788;
	public static final int UDP_SERVER_2_PORT = 6789;
	public static final int UDP_SERVER_3_PORT = 6790;		
	
	public static final long INITIAL_CREDIT_LIMIT = 500;
	
	public static final String ACCOUNT_EXISTS = "Customer already has an account!";
	public static final String FILE_PATH = "LogFiles/";
	
	public static final String TransferLoanUdpRequestPrefix = "Trn";
	public static final String CheckLoanUdpRequestPrefix = "Chk";
	
	public static final String TransferReject = "Reject";
	public static final String TransferFinalize = "Finalize";
	
	public final static String HOST_NAME = "localhost";

    //replica internal communication message types
    public enum messageType
    {
        RequestLoan, LoanAnswer, Transfer, TransferAnswer, ValidateAdmin, RollBack, Commit;
    }    

    // bank name pool
    public final static String[] BANK_NAME_POOL =
    { ReplicaConfiguration.SERVER_1_NAME, ReplicaConfiguration.SERVER_2_NAME,
    	ReplicaConfiguration.SERVER_3_NAME };

	
}
