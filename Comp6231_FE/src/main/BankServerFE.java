package main;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import protocol.ClientRequestContent;
import protocol.FEHeader;
import protocol.ReplicaReplyContent;
import protocol.UDPProtocol;
import util.UDPSender;

import BankServer.CustomerAccount;

public class BankServerFE extends BankServer.BankServerInterfacePOA implements Runnable{	
	
	private String serverName;
	
	private FileWriter feLogFile;
	private PrintWriter feOutputBuffer;
	private String filePath;			
	
	private int clientRequestCounter;
	private int repliedRequestCounter;
	
	private Map<String, List<ReplicaReplyContent>> repliesMap;
	private Map<String, Object> validatedRepliesMap;
	
	public BankServerFE() throws IOException
	{			
		clientRequestCounter = 0;
		filePath = Configuration.FILE_PATH;
		String fileName = filePath + "FE.txt";
		feLogFile = new FileWriter(fileName, true);
		feOutputBuffer = new PrintWriter(new BufferedWriter(feLogFile));		
		
		repliesMap = Collections.synchronizedMap( new HashMap<String, List<ReplicaReplyContent>>());
		validatedRepliesMap = Collections.synchronizedMap( new HashMap<String, Object>());
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		try
		{
			BankServerFE _feObj = new BankServerFE();
			
			Thread UDPListener = new Thread(_feObj);			
			UDPListener.start();
			
			ORB _orb = ORB.init(args, null);
			POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));
			
			byte[] _id = _rootPOA.activate_object(_feObj);
			org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
			String _ior = _orb.object_to_string(_ref);
			// Print IOR in the file
			PrintWriter _file = new PrintWriter(Configuration.FE_IOR_FILE);
			_file.println(_ior);
			_file.close();
			
			System.out.println("FE is up and running!");  
			
			_rootPOA.the_POAManager().activate();
			_orb.run();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		
	}

	@Override
	public void run() {
		DatagramSocket bSocket = null;
		try
		{
			int udpPort = Configuration.FE_PORT;						
			
			bSocket = new DatagramSocket(udpPort);
			byte[] buffer2 = new byte[1000];
			
			while(true)
			{
				buffer2 = new byte[1000];				
				
				DatagramPacket reply = new DatagramPacket(buffer2, buffer2.length);
				bSocket.receive(reply);
				
				UDPProtocol message = processIncomingPacket(reply);
				if (message == null)
				{
					continue;
				}
							
				addToRepliesMap(Integer.toString(message.getFeHeader().getRequestId()), message.getReplicaReply());				
				
			}														        										
								
		}
		catch(SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch(IOException e )
		{System.out.println("IO: " + e.getMessage()); }
		catch(Exception e)
		{
			System.out.println("Exception: " + e.getMessage()); 
		}
		finally
		{
			if(bSocket != null) bSocket.close();
		}
		
	}
	
	@Override
	public String openAccount(CustomerAccount account) {		
		ClientRequestContent clientRequest = new ClientRequestContent();				
		
		clientRequest.setRequestType(Configuration.requestType.OPEN_ACCOUNT);
			
		clientRequest.setEmail(account.emailAddress);
		clientRequest.setFirstName(account.firstName);
		clientRequest.setLastNmae(account.lastName);
		clientRequest.setPassWord(account.password);
		clientRequest.setPhoneNum(account.phoneNumber);
				
		String finalReply = "";
		try
		{
			int requestId = forwardRequest(account.bankName, clientRequest);	
			String requestIdStr = Integer.toString(requestId);
			
			boolean noReply = true;
			while(noReply)
			{
				if(validatedRepliesMap.containsKey(requestIdStr))				
					noReply = false;								
			}
						
			finalReply = validatedRepliesMap.get(requestIdStr).toString();			
		
		}catch (Exception e)
		{
			e.printStackTrace();
		} 		
		
		return finalReply;
	}

	@Override
	public boolean getLoan(String bankName, String accNumber, String password, int loanAmount) {
		ClientRequestContent clientRequest = new ClientRequestContent();				
		
		clientRequest.setRequestType(Configuration.requestType.GET_LOAN);
		
		clientRequest.setAccountId(Integer.parseInt(accNumber));
		clientRequest.setPassWord(password);
		clientRequest.setLoanAmount(loanAmount);
		
		boolean finalReply = false;
		try
		{
			int requestId = forwardRequest(bankName, clientRequest);					
			String requestIdStr = Integer.toString(requestId);
			
			boolean noReply = true;
			while(noReply)
			{
				if(validatedRepliesMap.containsKey(requestIdStr))				
					noReply = false;								
			}
			
			finalReply = (Boolean) validatedRepliesMap.get(requestIdStr);			
		
		}catch (Exception e)
		{
			e.printStackTrace();
		} 		
		
		return finalReply;
	}

	@Override
	public String delayPayment(String bankName, int loanId, String curDueDate, String newDueDate) {
		ClientRequestContent clientRequest = new ClientRequestContent();				
		
		clientRequest.setRequestType(Configuration.requestType.DELAY_LOAN);
		
		clientRequest.setLoanId(loanId);
		clientRequest.setCurrentDueDate(curDueDate);
		clientRequest.setNewDueDate(newDueDate);
		
		boolean finalReply;
		String reply = "Due date could not be updated!" ;
		
		try
		{
			int requestId = forwardRequest(bankName, clientRequest);		
			String requestIdStr = Integer.toString(requestId);
			
			boolean noReply = true;
			while(noReply)
			{
				if(validatedRepliesMap.containsKey(requestIdStr))				
					noReply = false;								
			}
			
			finalReply = (Boolean) validatedRepliesMap.get(requestIdStr);			
			
			if(finalReply == true)
				reply = "Due date was successfully updated!";
		
		}catch (Exception e)
		{
			e.printStackTrace();
		}					
		
		return reply;
	}

	@Override
	public CustomerAccount[] printCustomerInfo(String bankName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferLoan(int loanID, String CurrentBank, String OtherBank) {
		ClientRequestContent clientRequest = new ClientRequestContent();				
		
		clientRequest.setRequestType(Configuration.requestType.TRANSFER_LOAN);
		
		clientRequest.setOtherBank(OtherBank);
		clientRequest.setCurrentBank(CurrentBank);
		
		boolean finalReply;
		String reply = "Loan could not be transfered!" ;
		
		try
		{
			int requestId = forwardRequest(CurrentBank, clientRequest);			
			String requestIdStr = Integer.toString(requestId);
			
			boolean noReply = true;
			while(noReply)
			{
				if(validatedRepliesMap.containsKey(requestIdStr))				
					noReply = false;								
			}
			
			finalReply = (Boolean) validatedRepliesMap.get(requestIdStr);	
			
			if(finalReply == true)
				reply = "Loan transfered successfully!";
		
		}catch (Exception e)
		{
			e.printStackTrace();
		}					
		
		return reply;
	}

	
	/**
	 * Process incoming UDP packet, and convert it to a UDPProtocol object
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
			in.close();
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return protocol;
	}
	
	private int forwardRequest(String bankName, ClientRequestContent request)
	{
		UDPProtocol msg = new UDPProtocol();
		msg.setClientRequest(request);		
		
		int currentRequestId = clientRequestCounter++;
		int BankId = 0;
		
		switch (bankName)
		{
			case Configuration.SERVER_1_NAME:			
				BankId = 1;
				break;
			case Configuration.SERVER_2_NAME:
				BankId = 2;
				break;
			case Configuration.SERVER_3_NAME:
				BankId = 3;
				break;
			default:
				System.out.println("Invalid Bank name, please try again.");
		}	
		FEHeader feHeader = new FEHeader(currentRequestId, BankId); 
		
		msg.setFeHeader(feHeader);
		try
		{
			UDPSender.sendUDPPacket(Configuration.SEQUENCER_IP, Configuration.SEQUENCER_PORT, msg);
			return currentRequestId;
		}catch (IOException e)
		{
			e.printStackTrace();
		} 
		
		return 0;
	}
	
	private void processReplies(int requestId)
	{
		Object finalReply = null;		
		long startTime = System.currentTimeMillis();		
		boolean waitingForReplies = true;
		
		try
		{
			DatagramSocket feSocket = new DatagramSocket(Configuration.FE_PORT);
			byte[] buffer2;
			List<ReplicaReplyContent> replies = new ArrayList<ReplicaReplyContent>();
			while(waitingForReplies)
			{
				buffer2 = new byte[1000];				
				
				DatagramPacket reply = new DatagramPacket(buffer2, buffer2.length);
				feSocket.receive(reply);
				
				UDPProtocol message = processIncomingPacket(reply);
				if (message == null)
				{
					continue;
				}
				
				if (message.getFeHeader().getRequestId() != requestId)
				{
					continue;
				}
				
				replies.add(message.getReplicaReply());
				
				if(replies.size() == 2)
				{
					waitingForReplies = false;
					
					//compare 2 replies, if identical: add reply to approvedRepliesMap
									
					int elapsedTime = (int) (System.currentTimeMillis() - startTime);
					
					feSocket.setSoTimeout(elapsedTime * 2);
					//wait for 3rd reply twice the slowest
					try {
						buffer2 = new byte[1000];	
						reply = new DatagramPacket(buffer2, buffer2.length);
						feSocket.receive(reply);
		                
						//compare replies
						//if 3 identical => continue
						//if 2 identical
							//first 2 not identical: => add reply to approvedRepliesMap
							//report possible software failure
		            }
		            catch (SocketTimeoutException e) {
		                // timeout exception.
		              
		            	//possible crash
		            	//notify replicas
		            }														
					
				}											
				
			}		
			
			feSocket.close();
	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
					
	}

	private void addToRepliesMap(String key, ReplicaReplyContent value) {	    
	      if (repliesMap.containsKey(key)) {
	    	  synchronized(repliesMap.get(key))
	    	  {
	    		  repliesMap.get(key).add(value);
	    	  }	    	  
	      }
	      else {
	        List<ReplicaReplyContent> valuesList = new ArrayList<ReplicaReplyContent>();
	        valuesList.add(value);
	        repliesMap.put(key, valuesList);
	      }	    
	  }
}
