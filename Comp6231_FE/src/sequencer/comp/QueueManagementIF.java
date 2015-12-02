package sequencer.comp;

import dlms.comp.common.protocol.UDPProtocol;

public interface QueueManagementIF
{
	public UDPProtocol tryToGetQueueHead();

	public void moveToSentList(UDPProtocol message);
}
