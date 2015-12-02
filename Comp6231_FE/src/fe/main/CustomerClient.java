package fe.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Scanner;

import org.omg.CORBA.ORB;

import fe.bankserver.BankServerInterface;
import fe.bankserver.BankServerInterfaceHelper;
import fe.bankserver.CustomerAccount;

public class CustomerClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			ORB orb = ORB.init(args,null);
			
			BufferedReader br = new BufferedReader (new FileReader(Configuration.FE_IOR_FILE));
			String ior = br.readLine();
			br.close();		
			org.omg.CORBA.Object o = orb.string_to_object(ior);			
			BankServerInterface FE = BankServerInterfaceHelper.narrow(o);
			
			int userChoice=0;
			String userInput="";		
			String userInputBankName = "";
			Scanner keyboard = new Scanner(System.in);
			
			//log file
			String filePath = Configuration.FILE_PATH;
			String fileName;
			String logFileName;
			FileWriter logFile;                
	        PrintWriter outputBuffer;   
			
			showMenu();
			
			while(true)
			{
				Boolean valid = false;
				
				// Enforces a valid integer input.
				while(!valid)
				{
					try{
						userChoice=keyboard.nextInt();
						valid=true;
					}
					catch(Exception e)
					{
						System.out.println("Invalid Input, please enter an Integer");
						valid=false;
						keyboard.nextLine();
					}
				}
				
				// Manage user selection.
				switch(userChoice)
				{
				case 1:					
					CustomerAccount account = new CustomerAccount(); 
					account.customerAccountNumber = "Z";
					account.creditLimit = 0;
					account.loansInfo = "";
					
					System.out.println("Please enter the Bank name:");
					userInputBankName=keyboard.next();	
					account.bankName = userInputBankName;
					
					System.out.println("Please enter your first name:");
					userInput=keyboard.next();
					account.firstName = userInput;
					
					System.out.println("Please enter your last name:");
					userInput=keyboard.next();
					account.lastName = userInput;
					
					System.out.println("Please enter your email address:");
					userInput=keyboard.next();
					account.emailAddress = userInput;
					
					System.out.println("Please enter your phone number:");
					userInput=keyboard.next();
					account.phoneNumber = userInput;
					
					System.out.println("Please enter your password:");
					userInput=keyboard.next();
					account.password = userInput;
					
					try{
						Date d = new Date();
						
						String serverResponse = "";
						switch (userInputBankName)
						{
							case Configuration.SERVER_1_NAME:								
							case Configuration.SERVER_2_NAME:								
							case Configuration.SERVER_3_NAME:
								serverResponse = FE.openAccount(account);
								break;
							default:
								System.out.println("Invalid Bank name, please try again.");
						}										
						
						fileName = serverResponse + ".txt";
						logFileName = filePath + fileName;
						logFile = new FileWriter(logFileName, true);											
				        outputBuffer = new PrintWriter(new BufferedWriter(logFile)); 			        			        						
						Date responseDate = new Date();
						outputBuffer.println(responseDate + ": opened account "+ serverResponse);						
						System.out.println(serverResponse);
						
						outputBuffer.close();
				        logFile.close();
					}catch(RemoteException e){
						e.printStackTrace();
					}
					
					showMenu();
					break;
				case 2:
					System.out.println("Please enter the Bank name:");
					userInputBankName=keyboard.next();	
					
					System.out.println("Please enter your account number:");
					String userInputAccNumber = keyboard.next();
					
					System.out.println("Please enter your password:");
					String userInputPW = keyboard.next();
					
					System.out.println("Please enter the desired loan amount:");
					String userInputAmount = keyboard.next();
					
					//double requestedAmount = Double.parseDouble(userInputAmount);
					int requestedAmount = Integer.parseInt(userInputAmount);
					//Long.parseLong(userInputAmount);
					
					try{
						fileName = userInputAccNumber + ".txt";
						logFileName = filePath + fileName;
						logFile = new FileWriter(logFileName, true);											
				        outputBuffer = new PrintWriter(new BufferedWriter(logFile)); 	
						
						Date loanRequestDate = new Date();
						outputBuffer.println(loanRequestDate + ":" + "Request loan " + userInputAmount);
						
						Date loanResponseDate = new Date();
						
						switch (userInputBankName)
						{
							case Configuration.SERVER_1_NAME:								
							case Configuration.SERVER_2_NAME:								
							case Configuration.SERVER_3_NAME:
								if(FE.getLoan(userInputBankName, userInputAccNumber, userInputPW, requestedAmount) == true){
									System.out.println("Loan request was accepted successfully.");
									outputBuffer.println(loanResponseDate + ": " +"Loan request was accepted successfully.");
								}
								else{
									System.out.println("The loan exceeds your credit limit.");
									outputBuffer.println(loanResponseDate + ": " + "The loan exceeds your credit limit.");
								}
								break;
							default:
								System.out.println("Invalid Bank name, please try again.");			
						}												
						
						outputBuffer.close();
				        logFile.close();
					}catch(RemoteException e){
						e.printStackTrace();
					}
					showMenu();
					break;				
				case 3:				
					System.out.println("Please enter the current Bank name:");
					String userInputCurrentBankName=keyboard.next();	
					
					System.out.println("Please enter the destination Bank name:");
					String userInputDestBankName=keyboard.next();										
					
					System.out.println("Please enter the loan id:");
					String userInputloanId = keyboard.next();
					
					int loanId = Integer.parseInt(userInputloanId);
					
					String transferResult = "";
					
					switch (userInputCurrentBankName)
					{
						case Configuration.SERVER_1_NAME:														
							switch (userInputDestBankName)
							{								
								case Configuration.SERVER_1_NAME:		
									transferResult = "Destination Bank must be different from current Bank, please try again.";
									break;
								case Configuration.SERVER_2_NAME:									
								case Configuration.SERVER_3_NAME:
									transferResult = FE.transferLoan(loanId, userInputCurrentBankName, userInputDestBankName);
									break;
								default:
									transferResult = "Invalid destination Bank name, please try again.";			
							}																											
							break;
						case Configuration.SERVER_2_NAME:
							switch (userInputDestBankName)
							{								
								case Configuration.SERVER_1_NAME:	
								case Configuration.SERVER_3_NAME:
									transferResult = FE.transferLoan(loanId, userInputCurrentBankName, userInputDestBankName);									
									break;
								case Configuration.SERVER_2_NAME:
									transferResult = "Destination Bank must be different from current Bank, please try again.";
									break;								
								default:
									transferResult = "Invalid destination Bank name, please try again.";			
							}																					
							break;
						case Configuration.SERVER_3_NAME:
							switch (userInputDestBankName)
							{								
								case Configuration.SERVER_1_NAME:									
								case Configuration.SERVER_2_NAME:
									transferResult = FE.transferLoan(loanId, userInputCurrentBankName, userInputDestBankName);
									break;
								case Configuration.SERVER_3_NAME:
									transferResult = "Destination Bank must be different from current Bank, please try again.";									
									break;
								default:
									transferResult = "Invalid destination Bank name, please try again.";			
							}																			
							break;
						default:
							System.out.println("Invalid current Bank name, please try again.");			
					}
					
					System.out.println(transferResult);
					
					showMenu();
					break;
				case 4:
					System.out.println("Have a nice day!");
					keyboard.close();					
					System.exit(0);
				default:
					System.out.println("Invalid Input, please try again.");				
				}


			
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static void showMenu()
	{
		System.out.println("\n****Welcome to DLMS****\n");
		System.out.println("Please select an option (1-3)");
		System.out.println("1. Open an account");
		System.out.println("2. Get a loan");	
		System.out.println("3. Transfer a loan");
		System.out.println("4. Exit");
	}	

}
