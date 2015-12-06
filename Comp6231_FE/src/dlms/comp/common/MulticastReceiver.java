package dlms.comp.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
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

	// listening port for receiver
	private int listeningPort = 0;
	// multicast group ip address
	private String multicastGroupIp = null;
	// notify interface object, it should be replica
	private UDPNotifierIF notifyIf = null;
	// received message list, sorted by UUID
	private TreeMap<Integer, UDPProtocol> receivedList = null;
	// processed message list, sorted by UUID
	private TreeMap<Integer, Boolean> processedList = null;
	// list contains messages already casted
	private TreeMap<Integer, Boolean> multicastedList = null;

	private int lastDeliveredMessageUUID = -1;

	/**
	 * Constructor
	 * 
	 * @param interf
	 *            object which implements UDPNotifierIF, should be replica
	 */
	public MulticastReceiver(UDPNotifierIF interf, int port)
	{
		listeningPort = port;
		notifyIf = interf;
		// Guarantees that the content will be sorted by UUID
		receivedList = new TreeMap<Integer, UDPProtocol>();
		processedList = new TreeMap<Integer, Boolean>();
		multicastedList = new TreeMap<Integer, Boolean>();

		// create timer within multicast receiver to check received list every
		// 10 ms
		// if received list is not empty, then take the message in the head, and
		// multicast it
		// then move it to multicastedList and processedList
		MulticastReceiverTask task = new MulticastReceiverTask();
		Timer timer = new Timer(true);
		// schedule the timer to be triggered every 10 ms
		timer.scheduleAtFixedRate(task, 0, 10);
	}

	@Override
	public void run()
	{
		
		try
		{
			DatagramSocket clientSocket = new DatagramSocket(listeningPort);
			clientSocket.setReuseAddress(true);
			while (true)
			{
				byte[] receiveData = new byte[2048];
				// Receive the information and print it.
				DatagramPacket msgPacket = new DatagramPacket(receiveData,
						receiveData.length);
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
			if (multicastedList
					.containsKey(protocol.getSequencerHeader().getUUID()))
			{
				// it's already multicasted
			} else
			{
				if (!receivedList
						.containsKey(protocol.getSequencerHeader().getUUID())
						&& !processedList.containsKey(
								protocol.getSequencerHeader().getUUID()))
				{// if it's first time receiving this message, put it in the
					// list
					receivedList.put(protocol.getSequencerHeader().getUUID(),
							protocol);
					// and multicast it to the group
					Multicaster.multiCastMessage(protocol);
					multicastedList.put(protocol.getSequencerHeader().getUUID(),
							true);
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
			// if received list is not empty, then take the message in the head,
			// and
			// multicast it
			// then move it to multicastedList and processedList
			if (!receivedList.isEmpty())
			{
				UDPProtocol message = receivedList.get(receivedList.firstKey());
				receivedList.remove(receivedList.firstKey());
				// if it's the first message we deliver
				if (lastDeliveredMessageUUID == -1)
				{
					lastDeliveredMessageUUID = message.getSequencerHeader()
							.getUUID();
				}
				if (message != null)
				{
					notifyIf.notifyMessage(message);
					processedList.put(message.getSequencerHeader().getUUID(),
							true);
					lastDeliveredMessageUUID = message.getSequencerHeader()
							.getUUID();
				}
			}
		}

	}

}
