package dlms.comp.common.protocol;

import java.io.Serializable;

public class FEHeader implements Serializable
{
	private static final long serialVersionUID = 2520248636861301968L;
	private int requestId = -1;
	private int bankId = -1;

	public FEHeader(int id, int bankid)
	{
		requestId = id;
		bankId = bankid;
	}

	public int getRequestId()
	{
		return requestId;
	}

	public void setRequestId(int requestId)
	{
		this.requestId = requestId;
	}

	public int getBankId()
	{
		return bankId;
	}

	public void setBankId(int bankId)
	{
		this.bankId = bankId;
	}
	
	
}
