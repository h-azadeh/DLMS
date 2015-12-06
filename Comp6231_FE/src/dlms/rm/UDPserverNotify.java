package dlms.rm;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * RM send message to it's replica servers for update with this Class
 * 
 * @author Milad
 *
 */

public class UDPserverNotify implements Runnable{
	
	private Integer portNumber;
	public UDPserverNotify(int portNo) {
	    this.portNumber = portNo;
	  }
	
   @Override
   public void run() {
	// create socket at agreed port
    DatagramSocket aSocket = null;
    	try{
    		aSocket = new DatagramSocket();
		    aSocket.setReuseAddress(true);
    		byte [] m = "update".getBytes();
    		InetAddress aHost = InetAddress.getByName("localhost");
    		DatagramPacket request =
    				new DatagramPacket(m,  "update".length(), aHost, portNumber);
    		aSocket.send(request);
    		aSocket.close();
    }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
	 }catch (IOException e){System.out.println("IO: " + e.getMessage());
	 }finally {if(aSocket != null) aSocket.close();}

}

}
