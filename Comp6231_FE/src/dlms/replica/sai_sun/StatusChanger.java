package dlms.replica.sai_sun;

public class StatusChanger implements Runnable{
	
	BankServer myBankImpl;

	public StatusChanger(BankServer thisBankServer){
		myBankImpl = thisBankServer;
	}
	
	@Override
	   public void run() {
		
			while(true)
			{
				if(myBankImpl.weMustStop.equals(1))
				{
					myBankImpl.isProcessing = 1;
				}
				else if(myBankImpl.weMustStop.equals(0))
				{
					myBankImpl.isProcessing = 0;
				}
			}
		
	}

}
