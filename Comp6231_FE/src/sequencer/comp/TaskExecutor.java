package sequencer.comp;

import java.util.Timer;

public class TaskExecutor
{
	private SequencerTask task = null;
	private Timer timer = null;
	public TaskExecutor(QueueManagementIF interf)
	{
		task = new SequencerTask(interf);
		timer = new Timer(true);
	}
	
	public void startExecutor()
	{
		timer.scheduleAtFixedRate(task, 10, 10);
	}
	
	public void stopExecutor()
	{
		timer.cancel();
	}
}
