package dlms.comp.common;

import java.io.File;
import java.util.Map;

/**
 * in project root directory create bugFlag.txt
 * to start making wrong return values for a replica
 * @author Sai
 *
 */
public class SoftwareBugSimulator
{
	public static boolean shouldGenerateWrongMessage()
	{
		File f = new File("bugFlag.txt");
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	
	public static void main(String[] args)
	{
		System.out.println(shouldGenerateWrongMessage());
	}
}
