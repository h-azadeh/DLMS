package dlms.replica.sai_sun;

import java.io.Serializable;

import dlms.replica.sai_sun.ReplicaConfiguration.messageType;

/**
 * Loan protocol to be sent among servers to communicate user loan info
 * 
 * @author Sai
 *
 */
public class LoanProtocol implements Serializable
{
	private static final long serialVersionUID = 3398224621142512608L;
	//protocol id
	private String m_id;
	//host of the sender
	private String m_host;
	//udp recieving port of the sender
	private int m_port;
	//user object in the packet
	private User m_usr;
	//protocol type: request or answer
	private messageType m_type;
	
	private Loan m_loanToBeTransfered= null;
	
	private boolean result = false;

	
	public boolean getResult()
	{
		return result;
	}
	
	public void setResult(boolean r)
	{
		result = r;
	}
	public LoanProtocol(String id, String host, int port, User usr,
			messageType type)
	{
		m_id = id;
		m_host = host;
		m_port = port;
		m_usr = usr;
		m_type = type;
	}
	
   public LoanProtocol(String id, String host, int port, User usr,
            messageType type, Loan loan)
    {
        m_id = id;
        m_host = host;
        m_port = port;
        m_usr = usr;
        m_type = type;
        m_loanToBeTransfered = loan;
    }

	public String getId()
	{
		return m_id;
	}

	public void setId(String m_id)
	{
		this.m_id = m_id;
	}

	public String getHost()
	{
		return m_host;
	}

	public void setHost(String m_host)
	{
		this.m_host = m_host;
	}

	public int getPort()
	{
		return m_port;
	}

	public void setPort(int m_port)
	{
		this.m_port = m_port;
	}

	public User getUser()
	{
		return m_usr;
	}

	public void setUser(User m_usr)
	{
		this.m_usr = m_usr;
	}

	public messageType getType()
	{
		return m_type;
	}
	
	public Loan getLoanInfo()
	{
	    return m_loanToBeTransfered;
	}
	
	public void setType(messageType type)
	{
	    m_type = type;
	}

}
