package dlms.replica.azadeh;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

/** 
 * The implementation of Bank server  
 */
public class BankServer extends Thread{

	private Map<String, List<CustomerAccount>> accountsMap;
	private Map<String, List<Loan>> loansMap;
	private String serverName;
	private int maxLoanId;
	private FileWriter serverLogFile;
	private PrintWriter serverOutputBuffer;
	private String filePath;
	
	private double loanAmount1 = 0;
	private double loanAmount2 = 0;		
	
	private String transferResultMessge = "";
		
	public BankServer(String name) throws IOException
	{
		serverName = name;
		accountsMap = Collections.synchronizedMap( new HashMap<String, List<CustomerAccount>>());
		loansMap = Collections.synchronizedMap( new HashMap<String, List<Loan>>());
		maxLoanId = 0;
		
		filePath = Configuration.FILE_PATH;
		String fileName = filePath + name + ".txt";
		serverLogFile = new FileWriter(fileName, true);
		serverOutputBuffer = new PrintWriter(new BufferedWriter(serverLogFile));					
		
		start();
	}		
	
	public void run() {
		DatagramSocket bSocket = null;
		try
		{
			int udpPort = 0;
			
			switch(serverName){
				case Configuration.SERVER_1_NAME:
					udpPort = Configuration.UDP_SERVER_1_PORT;
					break;
				case Configuration.SERVER_2_NAME:
					udpPort = Configuration.UDP_SERVER_2_PORT;
					break;
				case Configuration.SERVER_3_NAME:
					udpPort = Configuration.UDP_SERVER_3_PORT;
					break;
			}
			
			bSocket = new DatagramSocket(udpPort);
			byte[] buffer2;
			
			while(true)
			{
				buffer2 = new byte[2048];				
				
				DatagramPacket request2 = new DatagramPacket(buffer2, buffer2.length);
				bSocket.receive(request2);
				
				String requestContent = new String(request2.getData()).trim();
				List<String> requestParts = Arrays.asList(requestContent.split(","));
				
				switch(requestParts.get(0))
				{
				case Configuration.CheckLoanUdpRequestPrefix:
					serverOutputBuffer.println("Check loan request.");
					serverOutputBuffer.flush();	
					
					//Check if customer has an account and loan(s) with this Bank					
					String fullName = requestParts.get(1).trim();
					
					String mapKey2 = fullName.substring(0,1);
					List<CustomerAccount> accMatchlist2 = new ArrayList<CustomerAccount>(); 
					accMatchlist2 = accountsMap.get(mapKey2);
					
					String customerFullName;				
					double totalLoan = 0;
					
					if(accMatchlist2 != null)
					{
						for(int j=0; j<accMatchlist2.size(); j++)
						{		
							customerFullName = accMatchlist2.get(j).GetFirstName() + accMatchlist2.get(j).GetLastName();					 
							
							if(customerFullName.equals(fullName))				
							{
								//Check loans
								String accountNumber2 = accMatchlist2.get(j).GetAccNumber();
								
								if(loansMap.containsKey(mapKey2))
								{
									List<Loan> matchlist2 = new ArrayList<Loan>(); 
									matchlist2 = loansMap.get(mapKey2);
									
									if(matchlist2 != null)
									{
										for(int n=0; n<matchlist2.size(); n++)
										{															
											if(matchlist2.get(n).GetAccNumber().equals(accountNumber2))				
											{
												totalLoan = totalLoan + matchlist2.get(n).GetAmount();
											}								
										}	
									}									
								}
							}			
						}
						
					}				
					
					String totalLoanString = String.valueOf(totalLoan);				
					byte[] totalLoanByte = totalLoanString.getBytes();
					
					DatagramPacket reply2 = new DatagramPacket(totalLoanByte, totalLoanString.length(), request2.getAddress(), request2.getPort());
					bSocket.send(reply2);
					
					Date opDate = new Date();
					serverOutputBuffer.println(opDate + ": Reply to other server's inquiry about " + fullName + " : " + totalLoanString + " loan");
	        		serverOutputBuffer.flush();					
					
					break;
				case Configuration.TransferLoanUdpRequestPrefix:
					serverOutputBuffer.println("Transfer loan request.");
					serverOutputBuffer.flush();	
					
					//Check if customer has an account 					
					String clientFirstName = requestParts.get(1).trim();
					String clientLastName = requestParts.get(2).trim();
					String clientfullName = clientFirstName+clientLastName;
					
					String strLoanAmount = requestParts.get(3).trim();
					double loanAmount = Double.parseDouble(strLoanAmount);
					
					String strDueDate = requestParts.get(4).trim();										
					DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
					Date dueDate = format.parse(strDueDate);
					
					String mapKeyTransfer = clientFirstName.substring(0,1);
					List<CustomerAccount> accMatchlistTransfer = new ArrayList<CustomerAccount>(); 
					accMatchlistTransfer = accountsMap.get(mapKeyTransfer);
					
					CustomerAccount existingAccount = new CustomerAccount();
					existingAccount.SetAccNumber("");
					
					String loanAccNumber = "";
					
					if(accMatchlistTransfer != null)
					{
						for(int i=0; i < accMatchlistTransfer.size() ; i++)
				        {
			        		if(accMatchlistTransfer.get(i).GetFirstName().equals(clientFirstName) && accMatchlistTransfer.get(i).GetLastName().equals(clientLastName) )
			        		{
			        			existingAccount = accMatchlistTransfer.get(i);
			        			loanAccNumber = accMatchlistTransfer.get(i).GetAccNumber();
			        		}
				        }												
					}
					
					String newAccountNumber = "";
					if(existingAccount.GetAccNumber().equals(""))
					{
						serverOutputBuffer.println("Must create new account.");
						serverOutputBuffer.flush();	
						//must create account
						CustomerAccount account = new CustomerAccount();
						account.SetFirstName(clientFirstName);
						account.SetLastName(clientLastName);
						
						newAccountNumber = account.GetFirstName().substring(0, 2).concat(account.GetLastName().substring(0, 2));
						newAccountNumber = newAccountNumber.concat(serverName);
						
						String mapKey = account.GetFirstName().substring(0, 1);														
						
						account.SetAccNumber(newAccountNumber);
						account.SetCreditLimit(Configuration.INITIAL_CREDIT_LIMIT);
						
						addToAccountsMap(mapKey, account);	
						loanAccNumber = newAccountNumber;
					}
								
					
					//create loan
					serverOutputBuffer.println("Creating loan...");
					serverOutputBuffer.flush();	
					int newLoanId = maxLoanId++;										
					
					Loan newLoan = new Loan();
					newLoan.SetAccNumber(loanAccNumber);
					newLoan.SetAmount(loanAmount);
					newLoan.SetDueDate(dueDate);
					newLoan.SetLoanId(maxLoanId);
					
					addToLoansMap(mapKeyTransfer, newLoan);
					
					//send transfer request ack to 1st server
					serverOutputBuffer.println("send transfer request ack to 1st server.");
					serverOutputBuffer.flush();	
					String replyContentString = Configuration.TransferLoanUdpRequestPrefix + ",OK";				
					byte[] replyContentByte = replyContentString.getBytes();
					
					DatagramPacket transferReply1 = new DatagramPacket(replyContentByte, replyContentString.length(), request2.getAddress(), request2.getPort());
					bSocket.send(transferReply1);
					
					Date reply1Date = new Date();
					serverOutputBuffer.println(reply1Date + ": Reply to other server's transfer request for " + clientfullName);
	        		serverOutputBuffer.flush();
					
	        		//wait for other server's confirmation/rejection
	        		boolean transferComplete = false;
	        		while(!transferComplete)
	    			{
	        			DatagramPacket transferConfirmation = new DatagramPacket(buffer2, buffer2.length);
						bSocket.receive(transferConfirmation);
						
						String confirmationContent = new String(transferConfirmation.getData()).trim();
						List<String> confirmationParts = Arrays.asList(confirmationContent.split(","));
						
						switch(confirmationParts.get(0))
						{
						case Configuration.TransferLoanUdpRequestPrefix:
							switch(confirmationParts.get(1))
							{
							case Configuration.TransferReject:
								serverOutputBuffer.println("Transfer could not be completed by other server.");
				        		serverOutputBuffer.flush();
								
								//delete the loan		
								deleteLoan(mapKeyTransfer, newLoanId);			
								serverOutputBuffer.println("Delete the loan.");
				        		serverOutputBuffer.flush();
					        	
					        	//delete the account
					        	if(!newAccountNumber.equals(""))
					        	{
					        		deleteAccount(mapKeyTransfer, newAccountNumber);
					        		
					        		serverOutputBuffer.println("Delete the account.");
					        		serverOutputBuffer.flush();
					        	}					        	
								
								transferComplete = true;
								break;					
							case Configuration.TransferFinalize:
								transferComplete = true;
								serverOutputBuffer.println("Transfer finalized.");
				        		serverOutputBuffer.flush();
								break;
							}
							break;
						}																	
	    			}	        			        		
					
					break;				
				}								
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
	
	
	
	public String openAccount(String firstName, String lastName, String emailAdd, String pw, String phoneNumber)
	{		
		try
		{
			CustomerAccount account = new CustomerAccount();
			account.SetAccNumber("Z");
			account.SetCreditLimit(0);
			account.SetFirstName(firstName);
			account.SetLastName(lastName);
			account.SetEmailAddress(emailAdd);
			account.SetPhoneNumber(phoneNumber);
			account.SetPassword(pw);
			
			//Account number format: 3 first letters of first name + 3 first letter of last name + Bank name + [random number]
			Date operationDate = new Date();
			
			String newAccNumber = account.GetFirstName().substring(0, 2).concat(account.GetLastName().substring(0, 2));
			newAccNumber = newAccNumber.concat(serverName);
			
			String mapKey = account.GetFirstName().substring(0, 1);				
			
			if(accountsMap.containsKey(mapKey))
			{
				List<CustomerAccount> matchlist = new ArrayList<CustomerAccount>(); 
				matchlist = accountsMap.get(mapKey);
				
				if(matchlist != null)
				{
					for(int i=0; i<matchlist.size(); i++)
					{															
						if(matchlist.get(i).GetFirstName().equals(account.GetFirstName()) && matchlist.get(i).GetLastName().equals(account.GetLastName()))				
						{
							serverOutputBuffer.println(operationDate + ":" + "An account already exists for " + account.GetFirstName() + account.GetLastName());
							return Configuration.ACCOUNT_EXISTS + " " + matchlist.get(i).GetAccNumber();
						}
					}	
				}										
			}
			
			account.SetAccNumber(newAccNumber);
			account.SetCreditLimit(Configuration.INITIAL_CREDIT_LIMIT);
			
			addToAccountsMap(mapKey, account);
					
			serverOutputBuffer.println(operationDate + ":" + "opened account "+ newAccNumber + " for " + account.GetFirstName() + account.GetLastName());
			
			serverOutputBuffer.flush();
			
			return newAccNumber;	
		}
		catch (Exception e)
		{
			
		}
		
		return "An error occured! Please try again!";
							
	}
	
	
	public boolean getLoan(String accNumber, String password, int loanAmount) 
	{
		try
		{
			Date operationDate = new Date();
			double curLoan = 0;
			
			serverOutputBuffer.println(operationDate + ":" + "Loan request by "+ accNumber + " " +  String.valueOf(loanAmount));
							
			String mapKey = accNumber.substring(0,1);
			
			if(loansMap.containsKey(mapKey))
			{
				List<Loan> matchlist = new ArrayList<Loan>(); 
				matchlist = loansMap.get(mapKey);
				
				if(matchlist != null)
				{
					for(int i=0; i<matchlist.size(); i++)
					{															
						if(matchlist.get(i).GetAccNumber().equals(accNumber))				
						{
							curLoan = curLoan + matchlist.get(i).GetAmount();
						}								
					}					
				}									
			}
			
			//Get the account
			List<CustomerAccount> accMatchlist = new ArrayList<CustomerAccount>(); 
			accMatchlist = accountsMap.get(mapKey);
			
			if(accMatchlist != null)
			{				
				for(int i=0; i<accMatchlist.size(); i++)
				{					
					if(accMatchlist.get(i).GetAccNumber().equals(accNumber))				
					{						
						//check with other Banks
						String fullName = accMatchlist.get(i).GetFirstName() + accMatchlist.get(i).GetLastName();
						
						double totalLoanWithOtherBanks = 0;
						
						switch(serverName){
						case Configuration.SERVER_1_NAME:
							totalLoanWithOtherBanks = CheckLoanWithOtherBanks(fullName, Configuration.SERVER_2_NAME, Configuration.UDP_SERVER_2_PORT, Configuration.SERVER_3_NAME, Configuration.UDP_SERVER_3_PORT);							
							break;
						case Configuration.SERVER_2_NAME:
							totalLoanWithOtherBanks = CheckLoanWithOtherBanks(fullName, Configuration.SERVER_1_NAME, Configuration.UDP_SERVER_1_PORT, Configuration.SERVER_3_NAME, Configuration.UDP_SERVER_3_PORT);							
							break;
						case Configuration.SERVER_3_NAME:
							totalLoanWithOtherBanks = CheckLoanWithOtherBanks(fullName, Configuration.SERVER_2_NAME, Configuration.UDP_SERVER_2_PORT, Configuration.SERVER_1_NAME, Configuration.UDP_SERVER_1_PORT);							
							break;
						}												
						
						serverOutputBuffer.println("Total loans with other Banks:" + totalLoanWithOtherBanks);
						serverOutputBuffer.flush();
						
						if(accMatchlist.get(i).GetCreditLimit() < curLoan + totalLoanWithOtherBanks + loanAmount)
						{
							serverOutputBuffer.println("Request rejected.");
							serverOutputBuffer.flush();
							
							return false;					
						}
					}								
				}		
				
			}								
			
			Date dueDate = new Date();
			
			Calendar c = Calendar.getInstance(); 
			c.setTime(dueDate); 
			c.add(Calendar.DATE, 365);
			dueDate = c.getTime();						
			
			maxLoanId++;
				
			Loan l = new Loan();
			l.SetAccNumber(accNumber);
			l.SetAmount(loanAmount);
			l.SetDueDate(dueDate);
			l.SetLoanId(maxLoanId);
			
			addToLoansMap(mapKey, l);
						
			serverOutputBuffer.println("Request accepted.");
			serverOutputBuffer.flush();
			
			return true;
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			
		}
		
		return false;
	}
	
	
	public String delayPayment(int loanId, String curDueDateIdl, String newDueDateIdl)
	{			
		try				
		{			
			DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
			Date curDueDate = format.parse(curDueDateIdl);
			Date newDueDate = format.parse(newDueDateIdl);
				
			Date operationDate = new Date();
			serverOutputBuffer.println(operationDate + ":" + "Delay payment request by manager from " + curDueDate + "to " + newDueDate + " for loanId: " + loanId);
			
			Iterator it = loansMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        
		        List<Loan> loansList = (ArrayList<Loan>) pair.getValue();
		        String mapKey = (String) pair.getKey();
		        
		        if(loansList != null)
		        {
		        	for(int i=0; i < loansList.size() ; i++)
			        {
			        	if(loansList.get(i).GetLoanId() == loanId)
			        	{
			        		loansList.get(i).SetDueDate(newDueDate);
			        		
			        		synchronized (loansMap)
			        		{
			        			loansMap.put(mapKey, loansList);
			        		}			        		
			        		
			        		serverOutputBuffer.println("Due date extended for account " + loansList.get(i).GetAccNumber() + " loanId: " + loanId);
			        		serverOutputBuffer.flush();
			        		
			        		return "Due date was successfully updated!";
			        	}
			        }       			        	
		        }		             	       	              
		        
		        it.remove(); // avoids a ConcurrentModificationException
		    }			
			
		    serverOutputBuffer.println("Due date could not be extended.");
		    serverOutputBuffer.flush();
		    
			return "Due date could not be updated!";

			
		}
		catch(Exception e)
		{
			
		}
	
		return "An error occurred. Please try again!";
	}
	
	
	public String[] printCustomerInfo()
	{		
		Map<String, List<CustomerAccount>> allAccountsInfo = accountsMap;		
		int customerCounter = 0;
		List<CustomerAccount> CustomersList = new ArrayList<CustomerAccount>();
		String[] returnList;
		
		try
		{								
			Date operationDate = new Date();
			serverOutputBuffer.println(operationDate + ":" + "Customer info request by manager");
			serverOutputBuffer.flush();
			
			Iterator accountIterator = allAccountsInfo.entrySet().iterator();
			while (accountIterator.hasNext()) {
				
		        Map.Entry pair = (Map.Entry)accountIterator.next();
		        
		        List<CustomerAccount> accountsList = (ArrayList<CustomerAccount>) pair.getValue();
		        String accountMapKey = (String) pair.getKey();
		        
		        if(accountsList != null)
		        {
		        	for(int i=0; i < accountsList.size() ; i++)
			        {
		        		customerCounter++;
		        		
		        		String curAccNumber = accountsList.get(i).GetAccNumber();
		        		List<Loan> curAccountLoansList = new ArrayList<Loan>();
		        		
			        	List<Loan> keyLoansList = loansMap.get(accountMapKey);
			        	if(keyLoansList != null)
			        	{
			        		for(int k=0; k < keyLoansList.size() ; k++)
			        		{
			        			if(keyLoansList.get(k).GetAccNumber().equals(curAccNumber))
			        			{
			        				curAccountLoansList.add(keyLoansList.get(k));
			        			}
			        		}
			        		
			        		accountsList.get(i).SetcustomerLoansList(curAccountLoansList);
			        		CustomersList.add(accountsList.get(i));				        					        	
			        	}			        				        	
			        }		
		        	
		        	allAccountsInfo.remove(accountMapKey);
		        	allAccountsInfo.put(accountMapKey, accountsList);
		        }		           	       	              
		        
		        accountIterator.remove(); // avoids a ConcurrentModificationException
		    }			    	
			
		}
		catch (Exception e)
		{
			
		}
		
		serverOutputBuffer.println("Start to put customer info into array");
		serverOutputBuffer.flush();
		
		returnList = new String[customerCounter];
		for(int q=0;q<=customerCounter;q++)
		{
			returnList[q] = CustomersList.get(q).toString();
			
		}
		return returnList;
	}
	
	
	public String transferLoan(int loanID, String CurrentBank, String OtherBank) {					
		try
		{
			Date operationDate = new Date();
			serverOutputBuffer.println(operationDate + ":" + "Transfer loan request for loanId: " + loanID + " to " + OtherBank);
			serverOutputBuffer.flush();						
			
			//check if loan exists 
			Loan curLoan = LookupLoan(loanID); 
			if(curLoan == null)
			{				
				return "A loan with the given id could not be found. Please check the loanId and try again!";
			}
			
			serverOutputBuffer.println("Loan found.");
			serverOutputBuffer.flush();	
			
			//Look up client name
			CustomerAccount curAccount = LookupAccount(curLoan.GetAccNumber());
			
			if(curAccount == null)
			{
				return "An error occurred. Please try again!";
			}
			
			serverOutputBuffer.println("Account found: " + curAccount.GetAccNumber());
			serverOutputBuffer.flush();	
			
			String ClientFirstName = curAccount.GetFirstName();
			String ClientLastName = curAccount.GetLastName();
			
			String MapKey = ClientFirstName.substring(0, 1);
			
			int udpPort = 0;						
			
			switch(OtherBank){
				case Configuration.SERVER_1_NAME:
					udpPort = Configuration.UDP_SERVER_1_PORT;
					break;
				case Configuration.SERVER_2_NAME:
					udpPort = Configuration.UDP_SERVER_2_PORT;
					break;
				case Configuration.SERVER_3_NAME:
					udpPort = Configuration.UDP_SERVER_3_PORT;
					break;
			}				
			
			//udp request
			DatagramSocket transferSocket = null;
			//transferSocket = new DatagramSocket(udpPort);
			transferSocket = new DatagramSocket();
			byte[] transferBuffer = new byte[2048];			
			
			//String requestString = Configuration.TransferLoanUdpRequestPrefix + "," + ClientFirstName + "," + ClientLastName + "," + String.valueOf(curLoan.GetAmount()) + "," + curLoan.GetDueDate().toString();				
			String requestString = Configuration.TransferLoanUdpRequestPrefix + "," + ClientFirstName + "," + ClientLastName + "," + String.valueOf(curLoan.GetAmount()) + ",2016-11-01";
			byte[] requestByte = requestString.getBytes();
					
			serverOutputBuffer.println("Request string:" + requestString);			
			serverOutputBuffer.flush();	
			
			InetAddress aHost = InetAddress.getByName("localhost");			
			
			DatagramPacket transferRequest = new DatagramPacket(requestByte, requestString.length(), aHost, udpPort);
			transferSocket.send(transferRequest);
			
			Date opDate = new Date();
			serverOutputBuffer.println(opDate + ": Transfer request sent to " + OtherBank + " for loan with id: " + loanID );
    		serverOutputBuffer.flush();
			
    		//wait for reply
    		DatagramPacket transferReply = new DatagramPacket(transferBuffer, transferBuffer.length);
    		transferSocket.receive(transferReply);
    		
			//delete loan
    		try
    		{
    			deleteLoan(MapKey, loanID);    		    	
        		serverOutputBuffer.println("Deleted the loan.");
    			serverOutputBuffer.flush();
    			
    			//send confirmation
        		String finalMsgString = Configuration.TransferLoanUdpRequestPrefix + "," + Configuration.TransferFinalize;				
    			byte[] finalMsgByte = finalMsgString.getBytes();
    			
    			DatagramPacket finalMsg = new DatagramPacket(finalMsgByte, finalMsgString.length(), aHost, udpPort);
    			transferSocket.send(finalMsg);
    			
    			serverOutputBuffer.println("Final confirmation sent to other Bank.");
    			serverOutputBuffer.flush();
    			
    			transferResultMessge = "Loan transfered successfully!";
    		}
    		catch(Exception e)
    		{
    			//send rejection to other server to roll back
    			String finalMsgString = Configuration.TransferLoanUdpRequestPrefix + "," + Configuration.TransferReject;				
    			byte[] finalMsgByte = finalMsgString.getBytes();
    			
    			DatagramPacket finalMsg = new DatagramPacket(finalMsgByte, finalMsgString.length(), aHost, udpPort);
    			transferSocket.send(finalMsg);
    			
    			serverOutputBuffer.println("Roll back request sent to other Bank.");
    			serverOutputBuffer.flush();
    			
    			transferResultMessge = "An error occurred, please try again later!";
    		}   		    					
		
			transferSocket.close();			
			
			
		}catch(Exception e){
			System.out.println(e.getStackTrace());
			transferResultMessge = "An error occurred, please try again later!";
		}
		
		
		return transferResultMessge;
	}
	
	private double  CheckLoanWithOtherBanks(final String clientFullName, String BankName1, final int BankUdpPort1, String BankName2, final int BankUdpPort2) throws InterruptedException
	{
		double otherBanksLoan = 0;		
		
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
            	loanAmount1 = CheckLoan(clientFullName, BankUdpPort1);            	
            }
        });
            
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
               	loanAmount2 = CheckLoan(clientFullName, BankUdpPort2);
            }
        });       
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
		serverOutputBuffer.println("Loan with " + BankName1 + ": "+ String.valueOf(loanAmount1));
		serverOutputBuffer.println("Loan with " + BankName2 + ": "+ String.valueOf(loanAmount2));		
        
        otherBanksLoan = loanAmount1 + loanAmount2;
        
		return otherBanksLoan;
	}
	
	private double CheckLoan(String fullName, int otherUdpServerPort)
	{
		double loanValue = 0;
		DatagramSocket aSocket = null;
		try{
			String messageContent = Configuration.CheckLoanUdpRequestPrefix + "," + fullName;
			
			aSocket = new DatagramSocket();
			//byte[] m = fullName.getBytes();
			byte[] m = messageContent.getBytes();			
			InetAddress aHost = InetAddress.getByName("localhost");
			//int serverPort = otherUdpServerPort;
			DatagramPacket request = new DatagramPacket(m, messageContent.length(), aHost, otherUdpServerPort);
			aSocket.send(request);
			byte [] buffer = new byte[2048];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			
			String loanReply = new String(reply.getData()).trim();			
			loanValue = Double.parseDouble(loanReply);			
			
		}catch(SocketException e){System.out.println("Socket: " + e.getMessage());
		}
		catch(IOException e )
		{System.out.println("IO: " + e.getMessage()); }
		finally
		{
			if(aSocket != null) aSocket.close();
		}		
		
		return loanValue;
	}
	
	private void addToAccountsMap(String key, CustomerAccount value) {	    
	      if (accountsMap.containsKey(key)) {
	    	  synchronized(accountsMap.get(key))
	    	  {
	    		  accountsMap.get(key).add(value);
	    	  }	    	  
	      }
	      else {
	        List<CustomerAccount> valuesList = new ArrayList<CustomerAccount>();
	        valuesList.add(value);
	        accountsMap.put(key, valuesList);
	      }	    
	  }
	
	private void addToLoansMap(String key, Loan value) {	    
	      if (loansMap.containsKey(key)) {
	    	  synchronized(loansMap.get(key))
	    	  {
	    		  loansMap.get(key).add(value);
	    	  }	    	  
	      }
	      else {
	        List<Loan> valuesList = new ArrayList<Loan>();
	        valuesList.add(value);
	        loansMap.put(key, valuesList);
	      }	    
	  }
	
	private void deleteAccount(String mapKey, String accountNumber)
	{
		if (accountsMap.containsKey(mapKey)) {
	    	  synchronized(accountsMap.get(mapKey))
	    	  {
	    		  int accountIndexToRemove = 0;
		    		List<CustomerAccount> keyAccountsList = accountsMap.get(mapKey);
		        	if(keyAccountsList != null)
		        	{
		        		for(int k=0; k < keyAccountsList.size() ; k++)
		        		{
		        			if(keyAccountsList.get(k).GetAccNumber().equals(accountNumber))
		        			{
		        				accountIndexToRemove = k;
		        			}
		        		}
		        		
		        		keyAccountsList.remove(accountIndexToRemove);       		    
		        		
		        		accountsMap.remove(mapKey);
		        		
		        		if(keyAccountsList.size() > 0)
		        		{
		        			accountsMap.put(mapKey, keyAccountsList);
		        		}		        		
		        	}	
	    	  }	    	  
	      }
	}
	
	private void deleteLoan(String mapKey, int loanId)
	{
		if (loansMap.containsKey(mapKey)) {
			synchronized(loansMap.get(mapKey))
	    	  {
				int indexToRemove = 0;
				List<Loan> keyLoansList = loansMap.get(mapKey);
				
				if(keyLoansList != null)
		    	{
		    		for(int k=0; k < keyLoansList.size() ; k++)
		    		{
		    			if(keyLoansList.get(k).GetLoanId() == loanId)
		    			{
		    				indexToRemove = k;
		    			}
		    		}
		    		
		    		keyLoansList.remove(indexToRemove);       		    
		    		
		    		loansMap.remove(mapKey);
		    		
		    		if(keyLoansList.size() > 0)
		    		{
		    			loansMap.put(mapKey, keyLoansList);
		    		}		    		
		    	}
	    	  }	
		}				
	}
	
	private Loan LookupLoan(int loanId)
	{
		Loan loan = new Loan();
		Map<String, List<Loan>> allLoansInfo = loansMap;		
		
		Iterator loanIterator = allLoansInfo.entrySet().iterator();
		while (loanIterator.hasNext()) {						
	        Map.Entry pair = (Map.Entry)loanIterator.next();
	        
	        List<Loan> loansList = (ArrayList<Loan>) pair.getValue();
	        String loanMapKey = (String) pair.getKey();		        
	        
	        if(loansList != null)
	        {       		        		        		        	
	        	for(int i=0; i < loansList.size() ; i++)
		        {
	        		if(loansList.get(i).GetLoanId() == loanId)
	        		{
	        			loan = loansList.get(i);
	        		}	        			        		   				        	
		        }		       			        	
	        }		           	       	              
	    
	        loanIterator.remove(); // avoids a ConcurrentModificationException
	    }
		
		return loan;
	}
	
	private CustomerAccount LookupAccount(String accountNumber)
	{
		CustomerAccount account = new CustomerAccount();
		Map<String, List<CustomerAccount>> allAccountsInfo = accountsMap;	
	
		Iterator accountIterator = allAccountsInfo.entrySet().iterator();
		while (accountIterator.hasNext()) {						
	        Map.Entry pair = (Map.Entry)accountIterator.next();
	        
	        List<CustomerAccount> accountsList = (ArrayList<CustomerAccount>) pair.getValue();
	        String accountMapKey = (String) pair.getKey();		        
	        
	        if(accountsList != null)
	        {        	        		        	
	        	for(int i=0; i < accountsList.size() ; i++)
		        {
	        		if(accountsList.get(i).GetAccNumber().equals(accountNumber))
	        		{
	        			account = accountsList.get(i);
	        		}
		        }		       			        	
	        }		           	       	              
	    
	        accountIterator.remove(); // avoids a ConcurrentModificationException
	    }
		
		return account;
	}
	

}
