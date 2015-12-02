package dlms.comp.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import sequencer.comp.Multicaster;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPNotifierIF;

/**
 * Reliable multicast receiver
 * 
 * @author Sai
 *
 */
public class MulticastReceiver implements Runnable
{

	private int listeningPort = 0;
	private String multicastGroupIp = null;
	private UDPNotifierIF notifyIf = null;
	private TreeMap<Integer, UDPProtocol> receivedList = null;
	private TreeMap<Integer, Boolean> processedList = null;
	private TreeMap<Integer, Boolean> multicastedList = null;

	public MulticastReceiver(UDPNotifierIF interf)
	{
		multicastGroupIp = Configuration.MULTI_CAST_INET_ADDR;
		listeningPort = Configuration.MULTI_CAST_INET_PORT;
		notifyIf = interf;
		// Guarantees that the content will be sorted by UUID
		receivedList = new TreeMap<Integer, UDPProtocol>();
		processedList = new TreeMap<Integer, Boolean>();
		multicastedList = new TreeMap<Integer, Boolean>();
		MulticastReceiverTask task = new MulticastReceiverTask();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(task, 10, 10);
	}

	@Override
	public void run()
	{
		MulticastSocket clientSocket;
		try
		{
			clientSocket = new MulticastSocket(listeningPort);
			clientSocket.joinGroup(InetAddress.getByName(multicastGroupIp));

			while (true)
			{
				byte[] receiveData = new byte[1024];
				// Receive the information and print it.
				DatagramPacket msgPacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(msgPacket);

				processIncomingPacket(msgPacket);
			}
		} catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
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
			// ok, let's check if this message is received before
			if (multicastedList.containsKey(protocol.getSequencerHeader().getUUID()))
			{
				// it's already multicasted
			} else
			{
				if (!receivedList.containsKey(protocol.getSequencerHeader().getUUID())
						&& !processedList.containsKey(protocol.getSequencerHeader().getUUID()))
				{// if it's first time receiving this message, put it in the
					// list
					receivedList.put(protocol.getSequencerHeader().getUUID(), protocol);
					// and multicast it to the group
					Multicaster.multiCastMessage(protocol);
					multicastedList.put(protocol.getSequencerHeader().getUUID(), true);
				}
			}
			in.close();
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return protocol;
	}

	private class MulticastReceiverTask extends TimerTask
	{
		@Override
		public void run()
		{
			completeTask();
		}

		private void completeTask()
		{
			if (!receivedList.isEmpty())
			{
				UDPProtocol message = receivedList.get(receivedList.firstKey());
				receivedList.remove(receivedList.firstKey());
				if (message != null)
				{
					notifyIf.notifyMessage(message);
					processedList.put(message.getSequencerHeader().getUUID(), true);
				}
			}
		}

	}

}
