package dlms.replica.azadeh;

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
	public static final String FILE_PATH = "C:\\Users\\Ajouli\\workspace2\\Comp6231LogFiles\\";
	
	public static final String TransferLoanUdpRequestPrefix = "Trn";
	public static final String CheckLoanUdpRequestPrefix = "Chk";
	
	public static final String TransferReject = "Reject";
	public static final String TransferFinalize = "Finalize";
	
}
