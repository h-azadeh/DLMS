package sequencer.comp;

import dlms.comp.common.protocol.UDPProtocol;

/**
 * Interface for sequencer to allow taskExecutor to take message from
 * sequencer's queue
 *
 */
public interface QueueManagementIF
{
    public UDPProtocol tryToGetQueueHead();

    public void moveToSentList(UDPProtocol message);
}
