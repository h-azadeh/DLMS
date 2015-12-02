package fe.main;

public class Configuration {
	public enum requestType
	{
		OPEN_ACCOUNT, GET_LOAN, TRANSFER_LOAN, PRINT_INFO, DELAY_LOAN;
	}
	
	public enum notificationType
	{
		SOFTWARE_FAILURE, CRASH
	}
	
	public static final String SERVER_1_NAME = "Bank1";
	public static final String SERVER_2_NAME = "Bank2";
	public static final String SERVER_3_NAME = "Bank3";
	
	public static final String FE_IOR_FILE = "FeIOR.txt";
	
	/*
	public static final int UDP_SERVER_1_PORT = 6788;
	public static final int UDP_SERVER_2_PORT = 6789;
	public static final int UDP_SERVER_3_PORT = 6790;		
	*/
	
	//public static final String Sequencer host and port		
	
	public static final String ACCOUNT_EXISTS = "Customer already has an account!";
	
	public static final String FILE_PATH = "LogFiles/";
	public final static int FE_PORT = 8888;
	public final static String FE_IP = "localhost";
}
