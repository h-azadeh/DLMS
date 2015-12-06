package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import RM.Configuration;
import RM.ReplicaManager;

/**
 * Bank Account Class
 * 
 * @author Milad
 *
 */

public class UDPfe implements Runnable
{ 
	private InetAddress anotherReplica1IP;
	private InetAddress anotherReplica2IP;
	private String strAnotherReplica1IP;
	private String strAnotherReplica2IP;
	ReplicaManager myRM;
	private Integer thisFeport; // FE listening port
	private Integer updateGroupPort; //receive meesage from update group mate to coordiante faulty one
	public UDPfe(ReplicaManager thisRM, String  anotherIP1, String  anotherIP2, int feListeningPort,
			int updateGroupPort) {
	  	this.myRM = thisRM;
	  	this.strAnotherReplica1IP = anotherIP1;
	  	this.strAnotherReplica2IP = anotherIP2;
	  	this.thisFeport = feListeningPort;
	  	this.updateGroupPort = updateGroupPort;
	  	try {
			this.anotherReplica1IP = InetAddress.getByName(anotherIP1);
			this.anotherReplica2IP = InetAddress.getByName(anotherIP2);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	  }
@Override
   public void run() {
    while(true){
    DatagramSocket aSocket = null;
    try{
    // create socket at agreed port
	byte[] buffer = new byte[1000];
	DatagramPacket request = new DatagramPacket(buffer, buffer.length);
    aSocket = new DatagramSocket(thisFeport);
	aSocket.setReuseAddress(true);
	aSocket.receive(request); 
    String receivedReq = new String(request.getData());
	String[] splited = receivedReq.split("-");
	String reqID = splited[1]; // Parse requestID from request
	//if it's not a duplicate request, multicast it to other RMs
	if(!myRM.notificationID.equals(Integer.parseInt(reqID.trim())))
	{
		NotifyOtherRMs anotherOne1 = new NotifyOtherRMs(receivedReq, strAnotherReplica1IP, thisFeport);
		NotifyOtherRMs anotherOne2 = new NotifyOtherRMs(receivedReq, strAnotherReplica2IP, thisFeport);
		Thread aMulticasterThread1 = new Thread(anotherOne1);
		Thread aMulticasterThread2 = new Thread(anotherOne2);
		aMulticasterThread1.start();
		aMulticasterThread2.start();
	}
	Integer conID = Integer.parseInt(splited[1]);
	String whoIsFaulty = null;
	if(conID.equals(myRM.consecutiveID) && !myRM.notificationID.equals(Integer.parseInt(reqID.trim()))
			&& myRM.myHostIP.equals(splited[0]))
	{		
		
			myRM.mycounterRM++;
			if(myRM.mycounterRM.equals(3)){
				whoIsFaulty = myRM.myHostIP;
				myRM.mycounterRM = 0;
			}

		if(whoIsFaulty != null && !whoIsFaulty.isEmpty())
		{
			//sending the message to the correspondent RM to restart it's replica
			InetAddress aRestart = null;

			if(whoIsFaulty.equals(myRM.myHostIP))
			{
				myRM.restartIt = 1;
			}
			else
			{	
				//this RM(replica) should coordinate with it's replica to update the faulty one
				byte [] m = whoIsFaulty.getBytes();
				aRestart = InetAddress.getByName("localhost");
				request = new DatagramPacket(m,  whoIsFaulty.length(), aRestart, updateGroupPort);
				aSocket.send(request);
				continue;
			}
		}
	}
	else if(!conID.equals(myRM.consecutiveID) && !myRM.notificationID.equals(Integer.parseInt(reqID.trim()))
			&& myRM.myHostIP.equals(splited[0]))
	{
		myRM.mycounterRM = 0;
		myRM.consecutiveID = conID;
		myRM.mycounterRM++;
	}
	if(myRM.notificationID.equals(Integer.parseInt(reqID.trim())))
		continue;

	myRM.notificationID = Integer.parseInt(reqID.trim()); //set the notification requestID with new one
	
	
    if(splited[0].equals(myRM.myHostIP)){
    	//update the hashmap of it's replica
    	UDPserverNotify notifier1 = new UDPserverNotify(Configuration.UDP_SERVER_NOTIFY_1); //dataupdater port of  bank server1
    	UDPserverNotify notifier2 = new UDPserverNotify(Configuration.UDP_SERVER_NOTIFY_2); //dataupdater port of  bank server2
    	UDPserverNotify notifier3 = new UDPserverNotify(Configuration.UDP_SERVER_NOTIFY_3); //dataupdater port of  bank server3
        Thread thread1 = new Thread(notifier1);
        Thread thread2 = new Thread(notifier2);
        Thread thread3 = new Thread(notifier3);
        thread1.start();
        thread2.start();
        thread3.start();
        }
    else if(!splited[0].equals(myRM.myHostIP)){
    		InetAddress aNotifier = null;
    		//and also send it to it's replica
			aNotifier = InetAddress.getByName("localhost");
			byte [] m = splited[0].getBytes();
			request = new DatagramPacket(m,  splited[0].length(), aNotifier, updateGroupPort);
			aSocket.send(request);
            }
    aSocket.close();
    	}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
   	 }catch (IOException e){System.out.println("IO: " + e.getMessage());
   	 }finally {if(aSocket != null) aSocket.close();}
    }

    }
        }
