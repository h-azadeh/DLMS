package dlms.replica.milad;

public class StatusChanger implements Runnable{
	
	RemoteBankImpl myBankImpl;

	public StatusChanger(RemoteBankImpl thisBankServer){
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
