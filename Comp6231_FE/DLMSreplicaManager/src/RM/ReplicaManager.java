package RM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import udp.UDPfe;
import udp.UDPserverNotify;

/**
 * RM Class
 * 
 * @author Milad
 *
 */

public class ReplicaManager {

	public Integer notificationID;
	public Integer consecutiveID;
	public Integer mycounterRM;
	public String myHostIP;
	public Integer restarterID;
	public Integer restartIt;
	public volatile boolean restartCondition;
	public Integer StopIt;

	public  ReplicaManager() { //constructor
		try {
			myHostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		restartIt = notificationID = consecutiveID = mycounterRM = restarterID = 0;
		StopIt = 0;
	}
	
	
	public static void main(String[] args) {
		ReplicaManager RM = new ReplicaManager(); //Start RM
		//FE Listener Thread
		UDPfe feNotifier = new UDPfe(RM, Configuration.UDP_FE_ANOTHER_IP_1, Configuration.UDP_FE_ANOTHER_IP_2,
				Configuration.UDP_FE_LISTENING_PORT, Configuration.UDP_UPDATE_GROUP_PORT); 
        Thread thread = new Thread(feNotifier);
        thread.start();
        
		//the address of replica jar file goes here
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "replica.jar");
		Process p = null;
		try {
			p = pb.start();
			p.waitFor();
			  if (p.exitValue()==0) {
			    BufferedReader outReader=new BufferedReader(new InputStreamReader(p.getInputStream()));
			    System.out.println(outReader.readLine().trim());
			  }
			RM.waitter(p);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized void waitter(Process myProcess) throws InterruptedException {
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", "replica.jar");
		while(true){
			if(restartCondition = restartIt.equals(1))
			{
				System.out.println("Replica Should be restarted!!");
				myProcess.destroy();
				System.out.println("Replica Destroyed!!");
				restartIt = 0;
				
				try {
					myProcess = pb.start();
					} 
				catch (IOException e) {
					e.printStackTrace();
					}
				//update the hashmap of it's replica
		    	UDPserverNotify notifier1 = new UDPserverNotify(Configuration.UDP_SERVER_NOTIFY_1); //dataupdater port of each bank server1
		    	UDPserverNotify notifier2 = new UDPserverNotify(Configuration.UDP_SERVER_NOTIFY_2); //dataupdater port of each bank server2
		    	UDPserverNotify notifier3 = new UDPserverNotify(Configuration.UDP_SERVER_NOTIFY_3); //dataupdater port of each bank server3
		        Thread thread1 = new Thread(notifier1);
		        Thread thread2 = new Thread(notifier2);
		        Thread thread3 = new Thread(notifier3);
		        thread1.start();
		        thread2.start();
		        thread3.start();
				System.out.println("Replica Started Correctly!!");

			}
			
			}
		}
	
}
