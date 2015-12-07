package dlms.replica.sai_sun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Data Sender Class
 * 
 * @author Milad
 *
 */

public class DataSender implements Runnable
{

	private Integer receiveingPort;
	BankServer myBankImpl;
	private String anotherReplica1, anotherReplica2;

	public DataSender(BankServer aBankImpl, int aReceivingPort,
			String anotherReplica1, String anotherReplica2)
	{
		this.receiveingPort = aReceivingPort;
		this.myBankImpl = aBankImpl;
		this.anotherReplica1 = anotherReplica1;
		this.anotherReplica2 = anotherReplica2;
	}

	@Override
	public void run()
	{

		while (true)
		{
			// create socket at agreed port
			DatagramSocket aSocket = null;
			try
			{
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket = new DatagramSocket(receiveingPort);
				aSocket.setReuseAddress(true);
				aSocket.receive(request);
				String receivedReq = new String(request.getData());
				if (receivedReq.equals("done"))
				{
					aSocket.close();
					continue;
				}
				myBankImpl.weMustStop = 1;
				Random rand = new Random();
				Integer randomNumber = rand.nextInt((9999 - 1000) + 1) + 1000;
				while (true)
				{
					if (myBankImpl.isProcessing.equals(1))
					{
						// start sending hashmaps
						Hashmaper completeHashMaps = new Hashmaper(
								myBankImpl.getAccountList(), myBankImpl.getLoanList(),
								randomNumber);
						ByteArrayOutputStream bStream = new ByteArrayOutputStream();
						ObjectOutputStream out = new ObjectOutputStream(
								bStream);
						out.writeObject(completeHashMaps);
						out.flush();
						DatagramPacket reply = new DatagramPacket(
								bStream.toByteArray(), bStream.size(),
								request.getAddress(),
								Integer.parseInt(receivedReq));
						aSocket.send(reply);
						break;
					}

				}
				buffer = new byte[1000];
				DatagramPacket ack = new DatagramPacket(buffer, buffer.length);
				aSocket.setReuseAddress(true);
				aSocket.receive(ack);
				receivedReq = new String(request.getData());
				if (receivedReq.equals("done"))
				{
					InetAddress myBuddy = InetAddress
							.getByName(myBankImpl.myBuddyIP);
					byte[] m = "done".getBytes();
					DatagramPacket reply = new DatagramPacket(m,
							"done".length(), myBuddy, receiveingPort);
					aSocket.send(reply);
				}
				try
				{
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
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
