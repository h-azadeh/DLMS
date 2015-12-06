package dlms.replica.azadeh;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Notifying Bank Server for update it's hashmaps Class
 * 
 * @author Milad
 *
 */

public class UDPupdateNotifier implements Runnable{
	
	private Integer thisport, repPort;
	private String rep1IP, rep2IP, thisPortReceive;
	private BankServer myBankImpl;
	public UDPupdateNotifier(BankServer thisBankImpl, int thisPortNotify,String thisPortReceive, String rep1IP, String rep2IP, int repPort) {
	    this.myBankImpl = thisBankImpl;
		this.thisport = thisPortNotify;
	    this.rep1IP = rep1IP; //another replica IP1
	    this.rep2IP = rep2IP; //another replica IP2
	    this.repPort = repPort;
	    this.thisPortReceive = thisPortReceive;
	  }
	
   @Override
   public void run() {
	   
	   while(true){
		    // create socket at agreed port
		    DatagramSocket aSocket = null;
		    try{
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
		    aSocket = new DatagramSocket(thisport);
		    aSocket.setReuseAddress(true);
		    aSocket.receive(request); 
		    String receivedReq = new String(request.getData());
			if(receivedReq.equals("update"))
			{
				myBankImpl.weMustStop = 1;
				InetAddress anotherRep1IP = null;
				InetAddress anotherRep2IP = null;
				anotherRep1IP = InetAddress.getByName(rep1IP);
				anotherRep2IP = InetAddress.getByName(rep2IP);
				byte [] m = thisPortReceive.getBytes();
	    		request = new DatagramPacket(m,  thisPortReceive.length(), anotherRep1IP, repPort);
	    		aSocket.send(request);
	    		request = new DatagramPacket(m,  thisPortReceive.length(), anotherRep2IP, repPort);
	    		aSocket.send(request);
	    		aSocket.close();
			}
		    }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		  	 }catch (IOException e){System.out.println("IO: " + e.getMessage());
		  	 }finally {if(aSocket != null) aSocket.close();}    
		    }
	   }
   }

