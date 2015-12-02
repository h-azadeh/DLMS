package dlms.comp.common.protocol;

import java.io.Serializable;

public class UDPProtocol implements Serializable
{
	private static final long serialVersionUID = 8161694624845522195L;
	private FEHeader feHeader = null;
	private SequencerHeader sequencerHeader = null;
	private ReplicaReplyContent replicaReply = null;
	private ClientRequestContent clientRequest = null;

	public FEHeader getFeHeader()
	{
		return feHeader;
	}

	public void setFeHeader(FEHeader feHeader)
	{
		this.feHeader = feHeader;
	}

	public SequencerHeader getSequencerHeader()
	{
		return sequencerHeader;
	}

	public void setSequencerHeader(SequencerHeader sequencerHeader)
	{
		this.sequencerHeader = sequencerHeader;
	}

	public ReplicaReplyContent getReplicaReply()
	{
		return replicaReply;
	}

	public void setReplicaReply(ReplicaReplyContent replicaReply)
	{
		this.replicaReply = replicaReply;
	}

	public ClientRequestContent getClientRequest()
	{
		return clientRequest;
	}

	public void setClientRequest(ClientRequestContent clientRequest)
	{
		this.clientRequest = clientRequest;
	}

}
