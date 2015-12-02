package dlms.comp.udp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import dlms.comp.common.protocol.UDPProtocol;

public class UDPListener implements Runnable
{
	private int listeningPort = -1;
	private boolean stop = false;
	private DatagramSocket serverSocket = null;
	private Thread thread = null;
	private UDPNotifierIF observer;

	public UDPListener(int port, UDPNotifierIF observer)
	{
		listeningPort = port;
		this.observer = observer;
	}

	/**
	 * Start listener thread
	 */
	public void startListening()
	{
		thread = new Thread(this);
		thread.setName("Sequencer UDP Thread");
		thread.start();
	}

	public void stopRunning()
	{
		stop = true;
		if (serverSocket != null)
		{
			serverSocket.close();
		}
		try
		{
			if (thread != null)
			{
				thread.join();
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			serverSocket = new DatagramSocket(listeningPort);
		} catch (SocketException e1)
		{
			e1.printStackTrace();
		}
		while (!stop)
		{
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try
			{
				serverSocket.receive(receivePacket);
				UDPProtocol message = processIncomingPacket(receivePacket);
				if (message == null)
				{
					continue;
				}
				observer.notifyMessage(message);
			} catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}

		}

	}

	/**
	 * Process incoming UDP packet, and convert it to a loanProtocol object
	 * 
	 * @param <T>
	 * 
	 * @param receivePacket
	 * @return
	 * @throws ClassNotFoundException
	 */
	private UDPProtocol processIncomingPacket(DatagramPacket receivePacket)
			throws ClassNotFoundException
	{
		byte[] data = receivePacket.getData();
		UDPProtocol protocol = null;
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try
		{
			ObjectInputStream is = new ObjectInputStream(in);

			protocol = (UDPProtocol) is.readObject();
			in.close();
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return protocol;
	}
}
