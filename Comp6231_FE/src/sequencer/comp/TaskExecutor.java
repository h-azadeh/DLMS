package sequencer.comp;

import java.util.Timer;

/**
 * Task executor is simply a timer
 *
 */
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
	    //schedule it to be triggered every 10 ms
		timer.scheduleAtFixedRate(task, 10, 10);
	}
	
	public void stopExecutor()
	{
	    //cancel timer
		timer.cancel();
	}
}
