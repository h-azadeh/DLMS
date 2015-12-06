package dlms.comp.common;

/**
 * Configuration class for sequencer
 *
 */
public class Configuration
{
	public enum requestType
	{
		OPEN_ACCOUNT, GET_LOAN, TRANSFER_LOAN, PRINT_INFO, DELAY_LOAN;
	}

	public final static String MULTI_CAST_INET_ADDR = "224.2.2.3";
	public final static String SEQUENCER_IP = "localhost";
	public final static int MULTI_CAST_INET_PORT = 9966;
	public final static int SEQUENCER_PORT = 9999;
	public final static String LOCAL_HOST_NAME = "mascouten";
	public final static String[] MULTI_CAST_GROUP_IPS= {"192.168.2.14", "192.168.2.34", "localhost"};
	public final static int[] MULTI_CAST_GROUP_PORTS= {11001, 11002, 11003};
	
	public final static String Replica1_Name = "Replica1"; //Sai
	public final static String Replica2_Name = "Replica2"; //Azadeh
	public final static String Replica3_Name = "Replica3"; //Milad
	
	public final static String Replica_1_Host = "localhost";
	public final static String Replica_2_Host = "localhost";
	public final static String Replica_3_Host = "localhost";
	
	public final static int Replica_1_PORT = 9007;
	public final static int Replica_2_PORT = 9008;
	public final static int Replica_3_PORT = 9009;
}
