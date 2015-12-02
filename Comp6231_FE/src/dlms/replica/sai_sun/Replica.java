package dlms.replica.sai_sun;

import java.io.IOException;
import java.util.ArrayList;

import dlms.comp.common.MulticastReceiver;
import dlms.comp.common.protocol.ReplicaReplyContent;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPNotifierIF;
import dlms.comp.udp.util.UDPSender;

public class Replica implements UDPNotifierIF
{
	private ArrayList<BankServer> m_serverList;
	private MulticastReceiver multicastReceiver = null;
	private Thread multicastReceiverThread;

	public static void main(String[] args)
	{
		Replica replica = new Replica();
		replica.startReplica();
	}

	public Replica()
	{
		m_serverList = new ArrayList<BankServer>();
		for (int i = 0; i < ReplicaConfiguration.BANK_NAME_POOL.length; i++)
		{
			addServer(ReplicaConfiguration.PORT_POOL[i], ReplicaConfiguration.BANK_NAME_POOL[i]);
		}
		multicastReceiver = new MulticastReceiver(this);
		multicastReceiverThread = new Thread(multicastReceiver);
	}

	public void startReplica()
	{
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
	 * get bank server object from server list by its name
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

	public String printCustomerInfo(int bankId)
	{
		// return empty string if the bank name can't be found in the server
		// list
		return getServerById(bankId) == null ? "" : getServerById(bankId).printCustomerInfo(
				ReplicaConfiguration.BANK_NAME_POOL[bankId - 1]);
	}

	public boolean transferLoan(String LoanID, String CurrentBank, String OtherBank, int bankId)
	{
		// return empty string if the bank name can't be found in the server
		// list
		return getServerByName(CurrentBank) == null ? false : !getServerByName(CurrentBank)
				.transferLoan(LoanID, CurrentBank, OtherBank).equals("");
	}

	@Override
	public void notifyMessage(UDPProtocol message)
	{
		ReplicaReplyContent reply = null;
		Object ret = null;
		switch (message.getClientRequest().getRequestType())
		{
		case OPEN_ACCOUNT:
			ret = openAccount(message.getClientRequest().getFirstName(), message.getClientRequest()
					.getLastNmae(), message.getClientRequest().getEmail(), message
					.getClientRequest().getPhoneNum(), message.getClientRequest().getPassWord(),
					message.getFeHeader().getBankId());
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

		case GET_LOAN:
			ret = getLoan(Integer.toString(message.getClientRequest().getAccountId()), message
					.getClientRequest().getPassWord(), message.getClientRequest().getLoanAmount(),
					message.getFeHeader().getBankId());
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
			ret = delayPayment(Integer.toString(message.getClientRequest().getLoanId()), message
					.getClientRequest().getCurrentDueDate(), message.getClientRequest()
					.getNewDueDate(), message.getFeHeader().getBankId());
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
			ret = printCustomerInfo(message.getFeHeader().getBankId());
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
			ret = transferLoan(Integer.toString(message.getClientRequest().getLoanId()), message
					.getClientRequest().getCurrentBank(), message.getClientRequest().getOtherBank(),message.getFeHeader().getBankId());
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
}
