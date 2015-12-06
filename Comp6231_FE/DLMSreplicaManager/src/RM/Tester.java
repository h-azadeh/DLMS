package RM;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Tester {

	public static void main(String[] args) throws IOException {
		
		DatagramSocket Socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		String IP = InetAddress.getLocalHost().getHostAddress();
		String Message = IP+"-12-1-0";
		byte[] data = Message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				IPAddress, 5996);
		Socket.send(sendPacket);
		Socket.close();

	}

}
