package dlms.replica.sai_sun;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Singleton class to provide logging utility
 * 
 * @author Sai
 *
 */
public class Logger
{
	private static Logger m_logger = null;

	// only to make the constructor not accessible from outside of the class
	private Logger()
	{

	}

	/**
	 * Getter function to get logger instance
	 * 
	 * @return logger instance
	 */
	static synchronized public Logger getInstance()
	{
		if (m_logger == null)
		{
			m_logger = new Logger();
		}

		return m_logger;
	}

	/**
	 * Log message to a given log file within logs directory
	 * 
	 * @param fileName
	 * @param msg
	 * @return true if new log file was created
	 */
	public synchronized boolean log(String fileName, String msg)
	{
		BufferedWriter writer = null;
		boolean ret = false;
		try
		{
			// if file doesn't exist, create it, otherwise just open it for
			// write
			File logFile = new File("logs/" + fileName);
			if (!logFile.exists())
			{
				logFile.createNewFile();
				ret = true;
			}

			writer = new BufferedWriter(
					new FileWriter("logs/" + fileName, true));
			if (!msg.equals(""))
			{
				writer.write(Calendar.getInstance().getTime().toString() + "\t"
						+ msg + "\n");
			}

		} catch (IOException e)
		{
		} finally
		{
			try
			{
				if (writer != null)
					writer.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return ret;
	}
}
