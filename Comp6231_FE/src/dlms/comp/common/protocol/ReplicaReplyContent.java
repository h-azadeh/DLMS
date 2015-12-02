package dlms.comp.common.protocol;

import java.io.Serializable;

public class ReplicaReplyContent implements Serializable
{
	private static final long serialVersionUID = -7191281080559132917L;
	private Object result = null;
	private String resultSender = null;

	public ReplicaReplyContent(Object result, String sender)
	{
		this.result = result;
		resultSender = sender;
	}

	public Object getResult()
	{
		return result;
	}

	public void setResult(Object result)
	{
		this.result = result;
	}

	public String getResultSender()
	{
		return resultSender;
	}

	public void setResultSender(String resultSender)
	{
		this.resultSender = resultSender;
	}
}
