package tmltranslator.patternhandling;
/**
 * Class TaskPorts
 * 
 * Creation: 07/09/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 07/09/2023
 */
 
import tmltranslator.*;

import java.util.*;

public class TaskPorts {
    List<String> writeChannels = new ArrayList<String>();
    List<String> readChannels = new ArrayList<String>();
    List<String> sendEvents = new ArrayList<String>();
    List<String> waitEvents = new ArrayList<String>();

    public TaskPorts(List<String> writeChannels, List<String> readChannels, List<String> sendEvents, List<String> waitEvents) {
        this.writeChannels = writeChannels;
        this.readChannels = readChannels;
        this.sendEvents = sendEvents;
        this.waitEvents = waitEvents;
    }


    public List<String> getWriteChannels() {
        return writeChannels;
    }

    public List<String> getReadChannels() {
        return readChannels;
    }

    public List<String> getSendEvents() {
        return sendEvents;
    }

    public List<String> getWaitEvents() {
        return waitEvents;
    }

    public static LinkedHashMap<String, TaskPorts> getListPortsTask(TMLModeling<?> tmlmodel) {
        LinkedHashMap<String, TaskPorts> listPortsTask = new LinkedHashMap<String, TaskPorts>();
        for (TMLTask task : tmlmodel.getTasks()) {
            List<String> writeChannels = new ArrayList<String>();
            List<String> readChannels = new ArrayList<String>();
            List<String> sendEvents = new ArrayList<String>();
            List<String> waitEvents = new ArrayList<String>();
            
            for (TMLWriteChannel wc : task.getWriteChannels()) {
                if (!writeChannels.contains(wc.getChannel(0).getName())) {
                    writeChannels.add(wc.getChannel(0).getName());
                }
            }
            for (TMLReadChannel rc : task.getReadChannels()) {
                if (!readChannels.contains(rc.getChannel(0).getName())) {
                    readChannels.add(rc.getChannel(0).getName());
                }
            }
            for (TMLSendEvent se : task.getSendEvents()) {
                if (!sendEvents.contains(se.getEvent().getName())) {
                    sendEvents.add(se.getEvent().getName());
                }
            }
            for (TMLWaitEvent we : task.getWaitEvents()) {
                if (!waitEvents.contains(we.getEvent().getName())) {
                    waitEvents.add(we.getEvent().getName());
                }
            }
            TaskPorts portTask = new TaskPorts(writeChannels, readChannels, sendEvents, waitEvents);
            listPortsTask.put(task.getName(), portTask);
        }
        return listPortsTask;
    }
}