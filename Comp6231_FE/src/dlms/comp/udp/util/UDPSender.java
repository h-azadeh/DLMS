package dlms.comp.udp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender
{
	/**
	 * Send UDP packet to a host and port
	 * 
	 * @param host
	 * @param port
	 * @param content
	 * @throws IOException
	 */
	public static <T> void sendUDPPacket(String host, int port, T content)
			throws IOException
	{
		DatagramSocket Socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(host);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(outputStream);
		os.writeObject(content);
		byte[] data = outputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				IPAddress, port);
		Socket.send(sendPacket);
		Socket.close();
	}
}
