package sequencer.comp;

import java.util.TimerTask;

import dlms.comp.common.protocol.UDPProtocol;

/**
 * Task executor timer will trigger invocation of run function to execute a task
 *
 */
public class SequencerTask extends TimerTask
{
    private QueueManagementIF managementIF;

    public SequencerTask(QueueManagementIF interf)
    {
        managementIF = interf;
    }

    @Override
    public void run()
    {
        completeTask();
    }

    private void completeTask()
    {
        // try to get head of the queue, if not empty, then multicast it and
        // move it to the sent list
        UDPProtocol message = managementIF.tryToGetQueueHead();
        if (message != null)
        {
            Multicaster.multiCastMessage(message);
            managementIF.moveToSentList(message);
        }
    }
}
