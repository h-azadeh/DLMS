package dlms.replica.milad;

public class Configuration {
	
	/*
	Configuration for Replica
	*/
		public enum requestType
	{
		OPEN_ACCOUNT, GET_LOAN, TRANSFER_LOAN, PRINT_INFO, DELAY_LOAN;
	}
	
	public static final String SERVER_1_NAME = "Bank1";
	public static final String SERVER_2_NAME = "Bank2";
	public static final String SERVER_3_NAME = "Bank3";
	
	public static final String FE_RESULT_IP = "";
	public static final Integer FE_RESULT_PORT = 4565;
		
	public final static String MULTI_CAST_INET_ADDR_1 = "224.0.0.3";
	public final static int MULTI_CAST_INET_PORT_1 = 8910;
	public final static String MULTI_CAST_INET_ADDR_2 = "224.0.0.4";
	public final static int MULTI_CAST_INET_PORT_2 = 8920;
	public final static String MULTI_CAST_INET_ADDR_3 = "224.0.0.5";
	public final static int MULTI_CAST_INET_PORT_3 = 8930;
	
	public static final Integer UDP_UPDATE_NOTIFY_1 = 9770;
	public static final Integer UDP_UPDATE_NOTIFY_2 = 9771;
	public static final Integer UDP_UPDATE_NOTIFY_3 = 9772;

	public static final Integer UDP_SENDER_PORT_1 = 9167;
	public static final Integer UDP_SENDER_PORT_2 = 9168;
	public static final Integer UDP_SENDER_PORT_3 = 9169;

	public static final String UDP_RECEIVING_PORT_1 = "9281";
	public static final String UDP_RECEIVING_PORT_2 = "9282";
	public static final String UDP_RECEIVING_PORT_3 = "9283";

	public static final Integer UDP_BUDDY_REPLICA_PORT_1 = 9360;
	public static final Integer UDP_BUDDY_REPLICA_PORT_2 = 9361;
	public static final Integer UDP_BUDDY_REPLICA_PORT_3 = 9362;


	public static final String UDP_ANOTHER_REPLICA_IP_1 = "";
	public static final String UDP_ANOTHER_REPLICA_IP_2 = "";
		
	
}