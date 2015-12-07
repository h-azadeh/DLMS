package dlms.replica.azadeh;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Updater Coordinator Class
 * 
 * @author Milad
 *
 */

public class UpdaterReplicas implements Runnable{
	
	private Integer receiveingPort;
	private BankServer myBankImpl;
	private String anotherReplica1, anotherReplica2;
	
	public UpdaterReplicas(BankServer aBankImpl, int aReceivingPort, String anotherReplica1, String anotherReplica2){
		this.receiveingPort = aReceivingPort;
		this.myBankImpl = aBankImpl;
		this.anotherReplica1 = anotherReplica1;
		this.anotherReplica2 = anotherReplica2;
	}
	
	@Override
	   public void run() {
		   
		   while(true){
			    // create socket at agreed port
			    DatagramSocket aSocket = null;
			    try{
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			    aSocket = new DatagramSocket(receiveingPort);
			    aSocket.setReuseAddress(true);
			    aSocket.receive(request); 
			    String receivedReq = new String(request.getData());
			    //check if it's RM has sent this request
			    if(anotherReplica1.equals(receivedReq))
			    	myBankImpl.myBuddyIP = anotherReplica2;
			    else if(anotherReplica2.equals(receivedReq))
			    	myBankImpl.myBuddyIP = anotherReplica1;
	    		aSocket.close();
			    }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
			  	 }catch (IOException e){System.out.println("IO: " + e.getMessage());
			  	 }finally {if(aSocket != null) aSocket.close();}    
			    }
		   }

}
