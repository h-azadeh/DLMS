package sequencer;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

import sequencer.comp.QueueManagementIF;
import sequencer.comp.TaskExecutor;
import dlms.comp.common.Configuration;
import dlms.comp.common.protocol.ClientRequestContent;
import dlms.comp.common.protocol.ReplicaReplyContent;
import dlms.comp.common.protocol.SequencerHeader;
import dlms.comp.common.protocol.UDPProtocol;
import dlms.comp.udp.util.UDPListener;
import dlms.comp.udp.util.UDPNotifierIF;
import dlms.comp.udp.util.UDPSender;

/**
 * Sequencer class
 *
 */
public class Sequencer implements UDPNotifierIF, Runnable, QueueManagementIF
{
    // UDP listener for FE messages
    private UDPListener feMessageReceiver = null;
    // FIFO queue
    private Queue<UDPProtocol> fifoQueue = new LinkedList<UDPProtocol>();
    // UUID base, it will be unique, and every UUID is generated based on it
    private long uniqueIdBase = 0;
    // message counter, UUID = uniqueIdBase + messageCounter
    private static int messageCounter = 0;

    // HashMap to remember what has been sent, not used for now
    private HashMap<Integer, UDPProtocol> sentList = null;

    // task executor
    private TaskExecutor taskExecutor = null;

    public Sequencer()
    {
        feMessageReceiver = new UDPListener(Configuration.SEQUENCER_PORT, this);
        // uuid base will be Calendar's time value in milliseconds, which will
        // be unique
        uniqueIdBase = Calendar.getInstance().getTimeInMillis();
        sentList = new HashMap<Integer, UDPProtocol>();
        taskExecutor = new TaskExecutor(this);
    }

    /**
     * function to start sequencer, it starts UDP listener and task executor
     */
    public void startSequencer()
    {
        feMessageReceiver.startListening();
        taskExecutor.startExecutor();
        System.out.println("Sequencer is up and running");
    }

    public static void main(String[] args)
    {
        // start sequencer as a thread
        Sequencer sequencer = new Sequencer();
        Thread t = new Thread(sequencer);
        t.setName("Sequencer Thread");
        t.start();

        /*
         * Following code is an example about how to send message to sequencer
         * 
         * 
         * UDPProtocol msg = new UDPProtocol(); ClientRequestContent
         * clientRequest = new ClientRequestContent();
         * clientRequest.setCurrentBank("TD");
         * clientRequest.setRequestType(Configuration.requestType.PRINT_INFO);
         * msg.setClientRequest(clientRequest); try {
         * UDPSender.sendUDPPacket(Configuration.SEQUENCER_IP,
         * Configuration.SEQUENCER_PORT, msg); Thread.sleep(24214214); } catch
         * (IOException e) { e.printStackTrace(); } catch (InterruptedException
         * e) { e.printStackTrace(); }
         */
    }

    /**
     * When UDP listener gets a message from FE, it will call this function to
     * deliver it to sequencer
     */
    @Override
    public void notifyMessage(UDPProtocol message)
    {
        //create sequencer header and add it to the FIFO queue
        SequencerHeader header = new SequencerHeader((int) uniqueIdBase + messageCounter);
        messageCounter++;
        message.setSequencerHeader(header);
        fifoQueue.add(message);
    }

    @Override
    public void run()
    {
        startSequencer();
        while (true)
        {
            try
            {
                Thread.sleep(600);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
    
    //get head of the queue
    @Override
    public UDPProtocol tryToGetQueueHead()
    {
        return fifoQueue.poll();
    }

    //this is not useful for now 
    @Override
    public void moveToSentList(UDPProtocol message)
    {
        sentList.put(message.getSequencerHeader().getUUID(), message);
    }
}
