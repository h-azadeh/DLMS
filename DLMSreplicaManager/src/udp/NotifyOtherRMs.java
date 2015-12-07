package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Notifying other RMs about the message received Class
 * 
 * @author Milad
 *
 */

public class NotifyOtherRMs implements Runnable {

	
	private String thisReq, anotherRep;
	private Integer rmPort;
	public NotifyOtherRMs(String req, String anotherRep, int rmPort) {
	    this.thisReq = req;
	    this.anotherRep = anotherRep;
	    this.rmPort = rmPort;
	  }
	
   @Override
   public void run() {
	// create socket at agreed port
	InetAddress anotherReplicaIP = null;
	try {
		anotherReplicaIP = InetAddress.getByName(anotherRep);
	} catch (UnknownHostException e1) {
		e1.printStackTrace();
	}
    DatagramSocket aSocket = null;
    	try{
    		aSocket = new DatagramSocket();
		    aSocket.setReuseAddress(true);
    		byte [] m = thisReq.getBytes();
    		DatagramPacket request =
    				new DatagramPacket(m,  thisReq.length(), anotherReplicaIP, rmPort);
    		aSocket.send(request);
    		aSocket.close();
    }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
	 }catch (IOException e){System.out.println("IO: " + e.getMessage());
	 }finally {if(aSocket != null) aSocket.close();}

}
	
}
