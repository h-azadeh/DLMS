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
	public final static String[] MULTI_CAST_GROUP_IPS= {"localhost", "localhost", "localhost"};
	public final static int[] MULTI_CAST_GROUP_PORTS= {11001, 11002, 11003};
}
