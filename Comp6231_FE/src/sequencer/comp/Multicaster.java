package sequencer.comp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import dlms.comp.common.Configuration;
import dlms.comp.common.protocol.UDPProtocol;

public class Multicaster
{

	/**
	 * Multicast the message to MULTI_CAST_INET_ADDR
	 * 
	 * @param message
	 *            message to be multicasted
	 */
	public static void multiCastMessage(UDPProtocol message)
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(message);
			final byte[] data = outputStream.toByteArray();
			for(int i = 0; i < Configuration.MULTI_CAST_GROUP_IPS.length; i++)
			{
				Thread t = new Thread(new Runnable() {

				      public void run() {
				    	  InetAddress address;
						try
						{
							DatagramSocket serverSocket = new DatagramSocket();
							address = InetAddress.getByName(Configuration.MULTI_CAST_INET_ADDR);
						
							DatagramPacket packet = new DatagramPacket(data, data.length, address,
									Configuration.MULTI_CAST_INET_PORT);
							serverSocket.send(packet);
							serverSocket.close();
						} catch (IOException e)
						{
							e.printStackTrace();
						}
				      }});
				t.start();
			}
			
			System.out.println("Multicast message sent");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
