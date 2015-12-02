package fe.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import org.omg.CORBA.ORB;

import fe.bankserver.BankServerInterface;
import fe.bankserver.BankServerInterfaceHelper;
import fe.bankserver.CustomerAccount;

public class ManagerClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			FileWriter managersLogFile;
			PrintWriter managersOutputBuffer;
			String filePath;
			
			filePath = Configuration.FILE_PATH;
			
			String fileName = filePath + "managers.txt";
			managersLogFile = new FileWriter(fileName, true);
			managersOutputBuffer = new PrintWriter(new BufferedWriter(managersLogFile));
			
			ORB orb = ORB.init(args,null);
			
			BufferedReader br = new BufferedReader (new FileReader(Configuration.FE_IOR_FILE));
			String ior = br.readLine();
			br.close();		
			org.omg.CORBA.Object o = orb.string_to_object(ior);			
			BankServerInterface FE = BankServerInterfaceHelper.narrow(o);
			
			int userChoice=0;
			String userInput="";		
			String userInputBankName = "";
			String userInputLoanId="";
			Scanner keyboard = new Scanner(System.in);
			
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
						System.out.println("Please enter the Bank name:");
						userInputBankName=keyboard.next();
						
						System.out.println("Please enter the loan id:");
						userInputLoanId=keyboard.next();
						int loanId = Integer.parseInt(userInputLoanId);												
						
						System.out.println("Please enter the current due date:");
						userInput=keyboard.next();
						String curDueDate = userInput;
						
						DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
						java.util.Date curDate =  format.parse(curDueDate);
						
						System.out.println("Please enter the new due date:");
						userInput=keyboard.next();
						String newDueDate = userInput;						
						
						java.util.Date newDate =  format.parse(newDueDate);
						
						Date operationDate = new Date();
						managersOutputBuffer.println(operationDate + ":" + "Request sent to " + userInputBankName + " to extend due date for loan "+ userInputLoanId + " from " + curDueDate + " to " + newDueDate);
						
						try{
							String serverResponse = "";
							
							switch (userInputBankName)
							{
								case Configuration.SERVER_1_NAME:									
								case Configuration.SERVER_2_NAME:											
								case Configuration.SERVER_3_NAME:
									serverResponse = FE.delayPayment(userInputBankName, loanId, curDueDate, newDueDate);
									break;		
								default:
									System.out.println("Invalid Bank name, please try again.");
							}
														
							System.out.println(serverResponse);			
							managersOutputBuffer.println(serverResponse);
						}catch(Exception e){
							e.printStackTrace();
						}						

						managersOutputBuffer.flush();
						
						showMenu();
						break;	
					case 2:
						System.out.println("Please enter the Bank name:");
						userInputBankName=keyboard.next();
						
						Date requestDate = new Date();
						managersOutputBuffer.println(requestDate + ":" + "Request customer info from "+ userInputBankName);
						
						try{							
							CustomerAccount [] accountsList;
							int count = 0;
							
							switch (userInputBankName)
							{
								case Configuration.SERVER_1_NAME:								
								case Configuration.SERVER_2_NAME:									
								case Configuration.SERVER_3_NAME:
									accountsList = FE.printCustomerInfo(userInputBankName);
									count = accountsList.length;
									PrintInfo(accountsList, count);
									break;			
								default:
									System.out.println("Invalid Bank name, please try again.");									
							}																				
							
							managersOutputBuffer.flush();
							
						}catch(Exception e){
							e.printStackTrace();
						}
						
						showMenu();
						break;	
					case 3:
						System.out.println("Have a nice day!");
						keyboard.close();
						managersOutputBuffer.close();
						managersLogFile.close();
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
	
	private static void showMenu()
	{
		System.out.println("\n****Welcome to DLMS****\n");
		System.out.println("Please select an option (1-3)");
		System.out.println("1. Delay Payment");
		System.out.println("2. Print Customer Info");		
		System.out.println("3. Exit");
	}	

	private static void PrintInfo(CustomerAccount [] accountsList, int count)
	{
		for(int k = 0; k < count; k++)
		{
			System.out.println(accountsList[k].customerAccountNumber);
			System.out.println(accountsList[k].firstName);
			System.out.println(accountsList[k].lastName);
			System.out.println(accountsList[k].emailAddress);
			System.out.println(accountsList[k].phoneNumber);
			System.out.println(accountsList[k].creditLimit);
			System.out.println(accountsList[k].loansInfo);			
		}		
		
	}

}
