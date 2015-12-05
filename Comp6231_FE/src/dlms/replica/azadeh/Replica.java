package dlms.replica.azadeh;

import java.io.IOException;
import java.util.ArrayList;

import dlms.comp.common.MulticastReceiver;
import dlms.comp.common.protocol.ReplicaReplyContent;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPNotifierIF;
import dlms.comp.udp.util.UDPSender;

/*
 * Replica class
 */
public class Replica implements UDPNotifierIF{

	// list contains three bank server objects
    private ArrayList<BankServer> m_serverList;
    // multicast receiver object
    private MulticastReceiver multicastReceiver = null;
    // multicast receiver thread
    private Thread multicastReceiverThread;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create replica object
        Replica replica = new Replica();
        // start replica
        replica.startReplica();

	}
	
	public Replica()
    {
		// initialize server list and add three bank servers to the list
        m_serverList = new ArrayList<BankServer>();
        for (int i = 0; i < Configuration.BANK_NAME_POOL.length; i++)
        {
            addServer(Configuration.BANK_NAME_POOL[i]);
        }

        // create multicast receiver
        multicastReceiver = new MulticastReceiver(this);
        multicastReceiverThread = new Thread(multicastReceiver);
    }		
	
	public void startReplica()
    {
        // start multicast receiver thread
        multicastReceiverThread.setName("Replica Thread");
        multicastReceiverThread.start();
    }
	
	/**
     * Create one bank server and added it to the server list
     * 
     * @param udpPort
     *            udp listening port of the bank server
     * @param string
     *            name of the bank server
     */
    private void addServer(String string)
    {
        BankServer server;
		try {
			server = new BankServer(string);
			m_serverList.add(server);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
    }

    /**
     * get bank server object from server list by its name
     * 
     * @param name
     *            name of the bank server0
     * @return bank server object
     */
    private BankServer getServerByName(String name)
    {
        for (BankServer s : m_serverList)
        {
            if (s.getName().equalsIgnoreCase(name))
            {
                return s;
            }
        }
        return null;
    }

    /**
     * get bank server object from server list by its id
     * 
     * @param name
     *            name of the bank server0
     * @return bank server object
     */
    private BankServer getServerById(int id)
    {
        return m_serverList.get(id - 1);
    }
    
    public String openAccount(String firstName, String lastName, String emailAddress,
            String phoneNumber, String password, int bankId)
    {
        // return empty string if the bank name can't be found in the server
        // list
        return getServerById(bankId) == null ? "" : getServerById(bankId).openAccount(
                firstName, lastName, emailAddress,phoneNumber, password);
    }

    public boolean getLoan(String accountNumber, String password, double loanAmount, int bankId)
    {
    	int loanAmountInt = (int) loanAmount;
        
        return getServerById(bankId).getLoan(accountNumber, password,loanAmountInt);
    }

    public boolean delayPayment(String loanID, String currentDueDate, String newDueDate, int bankId)
    {
    	int intLoanID = Integer.parseInt(loanID);
        // return false if the bank name can't be found in the server list
        return  getServerById(bankId).delayPayment(intLoanID, currentDueDate, newDueDate);
    }

    public String[] printCustomerInfo(int bankId)
    {        
        return getServerById(bankId).printCustomerInfo();
    }

    public boolean transferLoan(String LoanID, String CurrentBank, String OtherBank, int bankId)
    {
    	int intLoanID = Integer.parseInt(LoanID);
        return getServerByName(CurrentBank).transferLoan(intLoanID, CurrentBank, OtherBank);
    }

    /**
     * When message is delivered from multicast receiver, replica will processs
     * it based on request type from client request
     */
    @Override
    public void notifyMessage(UDPProtocol message)
    {
        ReplicaReplyContent reply = null;
        Object ret = null;
        //process based on request type
        switch (message.getClientRequest().getRequestType())
        {
        case OPEN_ACCOUNT:
            ret = openAccount(message.getClientRequest().getFirstName(), message.getClientRequest()
                    .getLastNmae(), message.getClientRequest().getEmail(), message
                    .getClientRequest().getPhoneNum(), message.getClientRequest().getPassWord(),
                    message.getFeHeader().getBankId());
            reply = new ReplicaReplyContent(ret, "replica2");
            message.setReplicaReply(reply);
            try
            {
                //send result back to FE
                UDPSender.sendUDPPacket(fe.main.Configuration.FE_IP, fe.main.Configuration.FE_PORT,
                        message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            break;

        case GET_LOAN:
            ret = getLoan(Integer.toString(message.getClientRequest().getAccountId()), message
                    .getClientRequest().getPassWord(), message.getClientRequest().getLoanAmount(),
                    message.getFeHeader().getBankId());
            reply = new ReplicaReplyContent(ret, "replica2");
            message.setReplicaReply(reply);
            try
            {
                UDPSender.sendUDPPacket(fe.main.Configuration.FE_IP, fe.main.Configuration.FE_PORT,
                        message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            break;
        case DELAY_LOAN:
            ret = delayPayment(Integer.toString(message.getClientRequest().getLoanId()), message
                    .getClientRequest().getCurrentDueDate(), message.getClientRequest()
                    .getNewDueDate(), message.getFeHeader().getBankId());
            reply = new ReplicaReplyContent(ret, "replica2");
            message.setReplicaReply(reply);
            try
            {
                UDPSender.sendUDPPacket(fe.main.Configuration.FE_IP, fe.main.Configuration.FE_PORT,
                        message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            break;
        case PRINT_INFO:
            ret = printCustomerInfo(message.getFeHeader().getBankId());
            reply = new ReplicaReplyContent(ret, "replica2");
            message.setReplicaReply(reply);
            try
            {
                UDPSender.sendUDPPacket(fe.main.Configuration.FE_IP, fe.main.Configuration.FE_PORT,
                        message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            break;
        case TRANSFER_LOAN:
            ret = transferLoan(Integer.toString(message.getClientRequest().getLoanId()), message
                    .getClientRequest().getCurrentBank(),
                    message.getClientRequest().getOtherBank(), message.getFeHeader().getBankId());
            reply = new ReplicaReplyContent(ret, "replica2");
            message.setReplicaReply(reply);
            try
            {
                UDPSender.sendUDPPacket(fe.main.Configuration.FE_IP, fe.main.Configuration.FE_PORT,
                        message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            break;
        default:
            System.err.println("Operation not supported");
            break;
        }
    }
}
