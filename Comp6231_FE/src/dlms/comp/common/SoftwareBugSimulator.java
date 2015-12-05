package dlms.comp.common;

/**
 * in windows cmd, use set SW_BUG=true 
 * to start making wrong return values for a replica
 * @author Sai
 *
 */
public class SoftwareBugSimulator
{
	public static boolean shouldGenerateWrongMessage()
	{
		String value = System.getenv("SW_BUG");
		if (value != null)
		{
			if (value.equals("true"))
			{
				return true;
			}
		}

		return false;
	}
}
