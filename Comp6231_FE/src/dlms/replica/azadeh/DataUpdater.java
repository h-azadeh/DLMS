package dlms.replica.azadeh;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * Data Updater Class
 * 
 * @author Milad
 *
 */

public class DataUpdater implements Runnable
{
	private InetAddress anotherReplica1IP;
	private InetAddress anotherReplica2IP;
	private String strAnotherReplica1IP, strAnotherReplica2IP,
			thisReceiveDataPort;
	BankServer myBankImpl;
	private Integer thisNotifyPort, senderPort, BuddyReplicaPort,
			receivedUpdateCounter;

	public DataUpdater(BankServer thisBankImpl, int aNotifyPort,
			int newSenderPort, String aReceiveDataPort, int BuddyReplicaPort,
			String rep1IP, String rep2IP)
	{
		this.myBankImpl = thisBankImpl;
		this.thisNotifyPort = aNotifyPort; // port for receiveing notification
											// from RM
		this.thisReceiveDataPort = aReceiveDataPort; // port for receiving data
														// from other replica
		this.strAnotherReplica1IP = rep1IP; // another replica port1
		this.strAnotherReplica2IP = rep2IP; // another replica port2
		this.senderPort = newSenderPort; // port for the sender to receive
											// requests and send updated data to
											// another replica(requester)
		this.BuddyReplicaPort = BuddyReplicaPort;// buddy port
		this.receivedUpdateCounter = 0;
	}

	@Override
	public void run()
	{
		// make a thread for receive notifications from the RM
		UDPupdateNotifier notifier = new UDPupdateNotifier(myBankImpl,
				thisNotifyPort, thisReceiveDataPort, strAnotherReplica1IP,
				strAnotherReplica2IP, senderPort);

		DataSender sendingNotifier = new DataSender(myBankImpl, senderPort,
				strAnotherReplica1IP, strAnotherReplica2IP);
		UpdaterReplicas aBuddyListener = new UpdaterReplicas(myBankImpl,
				BuddyReplicaPort, strAnotherReplica1IP, strAnotherReplica2IP);

		Thread thread = new Thread(notifier);
		Thread thread2 = new Thread(sendingNotifier);
		Thread thread3 = new Thread(aBuddyListener);
		thread.start();
		thread2.start();
		thread3.start();

		// Waitnig for data from other replicas
		while (true)
		{
			DatagramSocket aSocket = null;
			try
			{
				// create socket at agreed port
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket = new DatagramSocket(
						Integer.parseInt(thisReceiveDataPort));
				aSocket.setReuseAddress(true);
				aSocket.receive(request);
				ObjectInputStream iStream = null;
				iStream = new ObjectInputStream(
						new ByteArrayInputStream(buffer));
				Hashmaper newHashMapUpdate = null;
				try
				{
					newHashMapUpdate = (Hashmaper) iStream.readObject();
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				iStream.close();
				myBankImpl.setCustomerList(newHashMapUpdate.getAccountHashmap(),newHashMapUpdate.getLoanHashmap());
				byte[] m = "done".getBytes();
				DatagramPacket reply = new DatagramPacket(m, "done".length(),
						request.getAddress(), senderPort);
				aSocket.send(reply);
				if (!receivedUpdateCounter
						.equals(newHashMapUpdate.getUpdateID()))
				{
					try
					{
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				receivedUpdateCounter = newHashMapUpdate.getUpdateID();
				myBankImpl.weMustStop = 0;
				aSocket.close();
			} catch (SocketException e)
			{
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e)
			{
				System.out.println("IO: " + e.getMessage());
			} finally
			{
				if (aSocket != null)
					aSocket.close();
			}
		}
	}
}
