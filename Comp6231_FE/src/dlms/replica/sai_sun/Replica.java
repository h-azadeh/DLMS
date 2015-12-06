package dlms.replica.sai_sun;

import java.io.IOException;
import java.util.ArrayList;

import dlms.comp.common.Configuration;
import dlms.comp.common.MulticastReceiver;
import dlms.comp.common.SoftwareBugSimulator;
import dlms.comp.common.protocol.ReplicaReplyContent;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPNotifierIF;
import dlms.comp.udp.util.UDPSender;

/**
 * Replica class
 *
 */
public class Replica implements UDPNotifierIF
{
    // list contains three bank server objects
    private ArrayList<BankServer> m_serverList;
    // multicast receiver object
    private MulticastReceiver multicastReceiver = null;
    // multicast receiver thread
    private Thread multicastReceiverThread;

    public static void main(String[] args)
    {
        // create replica object
        Replica replica = new Replica();
        // start replica
        replica.startReplica();
    }

    public Replica()
    {
        // initialize server list and add three bank servers to the list
        m_serverList = new ArrayList<BankServer>();
        for (int i = 0; i < ReplicaConfiguration.BANK_NAME_POOL.length; i++)
        {
            addServer(ReplicaConfiguration.PORT_POOL[i], ReplicaConfiguration.BANK_NAME_POOL[i]);
        }

        // create multicast receiver
        multicastReceiver = new MulticastReceiver(this,Configuration.MULTI_CAST_GROUP_PORTS[0]);
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
    private void addServer(int udpPort, String string)
    {
        BankServer server = new BankServer(udpPort, string);
        StatusChanger statusChecker = new StatusChanger(server);
        Thread t = new Thread(statusChecker);
        t.start();
        m_serverList.add(server);
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
            if (s.getBankName().equalsIgnoreCase(name))
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
                ReplicaConfiguration.BANK_NAME_POOL[bankId - 1], firstName, lastName, emailAddress,
                phoneNumber, password);
    }

    public boolean getLoan(String accountNumber, String password, double loanAmount, int bankId)
    {
        // return empty string if the bank name can't be found in the server
        // list
        return getServerById(bankId) == null ? false : !getServerById(bankId).getLoan(
                ReplicaConfiguration.BANK_NAME_POOL[bankId - 1], accountNumber, password,
                loanAmount).equals("");
    }

    public boolean delayPayment(String loanID, String currentDueDate, String newDueDate, int bankId)
    {
        // return false if the bank name can't be found in the server list
        return getServerById(bankId) == null ? false : getServerById(bankId)
                .delayPayment(ReplicaConfiguration.BANK_NAME_POOL[bankId - 1], loanID,
                        currentDueDate, newDueDate);
    }

    public String[] printCustomerInfo(int bankId)
    {
        // return empty string if the bank name can't be found in the server
        // list
        return getServerById(bankId) == null ? new String[]{} : getServerById(bankId).printCustomerInfoToArray(
                ReplicaConfiguration.BANK_NAME_POOL[bankId - 1]);
    }

    public boolean transferLoan(String LoanID, String CurrentBank, String OtherBank, int bankId)
    {
        // return empty string if the bank name can't be found in the server
        // list
        return getServerByName(CurrentBank) == null ? false : !getServerByName(CurrentBank)
                .transferLoan(LoanID, CurrentBank, OtherBank).equals("");
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
        	System.out.println("Processing open account");
            ret = openAccount(message.getClientRequest().getFirstName(), message.getClientRequest()
                    .getLastNmae(), message.getClientRequest().getEmail(), message
                    .getClientRequest().getPhoneNum(), message.getClientRequest().getPassWord(),
                    message.getFeHeader().getBankId());
            if(SoftwareBugSimulator.shouldGenerateWrongMessage())
            {
            	ret = "";
            }
            reply = new ReplicaReplyContent(ret, "replica1");
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
        	System.out.println("Processing get loan");
            ret = getLoan(Integer.toString(message.getClientRequest().getAccountId()), message
                    .getClientRequest().getPassWord(), message.getClientRequest().getLoanAmount(),
                    message.getFeHeader().getBankId());
            if(SoftwareBugSimulator.shouldGenerateWrongMessage())
            {
            	ret = false;
            }
            reply = new ReplicaReplyContent(ret, "replica1");
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
        	System.out.println("Processing delay loan");
            ret = delayPayment(Integer.toString(message.getClientRequest().getLoanId()), message
                    .getClientRequest().getCurrentDueDate(), message.getClientRequest()
                    .getNewDueDate(), message.getFeHeader().getBankId());
            if(SoftwareBugSimulator.shouldGenerateWrongMessage())
            {
            	ret = false;
            }
            reply = new ReplicaReplyContent(ret, "replica1");
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
        	System.out.println("Processing print info");
            ret = printCustomerInfo(message.getFeHeader().getBankId());
            if(SoftwareBugSimulator.shouldGenerateWrongMessage())
            {
            	ret = new String[]{};
            }
            reply = new ReplicaReplyContent(ret, "replica1");
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
        	System.out.println("Processing transfer loan");
            ret = transferLoan(Integer.toString(message.getClientRequest().getLoanId()), message
                    .getClientRequest().getCurrentBank(),
                    message.getClientRequest().getOtherBank(), message.getFeHeader().getBankId());
            if(SoftwareBugSimulator.shouldGenerateWrongMessage())
            {
            	ret = false;
            }
            reply = new ReplicaReplyContent(ret, "replica1");
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

	@Override
	public boolean pauseTimer()
	{
		return m_serverList.get(0).weMustStop == 1;
	}
}
