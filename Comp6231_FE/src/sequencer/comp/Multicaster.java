package sequencer.comp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import dlms.comp.common.Configuration;
import dlms.comp.common.protocol.UDPProtocol;

public class Multicaster
{

	public static void multiCastMessage(UDPProtocol message)
	{
		try
		{
			DatagramSocket serverSocket = new DatagramSocket();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(message);
			byte[] data = outputStream.toByteArray();

			InetAddress group = InetAddress.getByName(Configuration.MULTI_CAST_INET_ADDR);
			DatagramPacket packet = new DatagramPacket(data, data.length, group, Configuration.MULTI_CAST_INET_PORT);
			serverSocket.send(packet);

			serverSocket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
