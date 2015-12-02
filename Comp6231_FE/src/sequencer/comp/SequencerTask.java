package sequencer.comp;

import java.util.TimerTask;

import dlms.comp.common.protocol.UDPProtocol;

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
		UDPProtocol message = managementIF.tryToGetQueueHead();
		if (message != null)
		{
			Multicaster.multiCastMessage(message);
			managementIF.moveToSentList(message);
		}
	}
}
