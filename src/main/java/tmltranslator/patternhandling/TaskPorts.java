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
    public final static int WRITE_CHANNEL = 1;
    public final static int READ_CHANNEL = 2;
    public final static int SEND_EVENT = 3;
    public final static int WAIT_EVENT = 4;

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

    public void removePort(String port) {
        writeChannels.remove(port);
        readChannels.remove(port);
        sendEvents.remove(port);
        waitEvents.remove(port);
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

    public static TaskPorts cloneTaskPort(TaskPorts taskPorts) {
        List<String> wcs = new ArrayList<String>(taskPorts.getWriteChannels());
        List<String> rcs = new ArrayList<String>(taskPorts.getReadChannels());
        List<String> ses = new ArrayList<String>(taskPorts.getSendEvents());
        List<String> wes = new ArrayList<String>(taskPorts.getWaitEvents());
        return new TaskPorts(wcs, rcs, ses, wes);
    }

    public int getPortTypeByName(String portName) {
        if (writeChannels.contains(portName)) {
            return WRITE_CHANNEL;
        } else if (readChannels.contains(portName)) {
            return READ_CHANNEL;
        } else if (sendEvents.contains(portName)) {
            return SEND_EVENT;
        } else if (waitEvents.contains(portName)) {
            return WAIT_EVENT;
        }
        return 0;
    }

    public static LinkedHashMap<String, TaskPorts> getPortsTaskOfModelLeft(LinkedHashMap<String, TaskPorts> _portsTaskOfModelAll, List<PatternConnection> patternConnections) {
        
        LinkedHashMap<String, TaskPorts> portsTaskModelLeft = new LinkedHashMap<String, TaskPorts>();
        for (String task: _portsTaskOfModelAll.keySet()) {
            List<String> wcs = new ArrayList<String>();
            List<String> rcs = new ArrayList<String>();
            List<String> ses = new ArrayList<String>();
            List<String> wes = new ArrayList<String>();
            TaskPorts tp = new TaskPorts(wcs, rcs, ses, wes);
            portsTaskModelLeft.put(task, tp);
            for (String wr : _portsTaskOfModelAll.get(task).getWriteChannels()) {
                boolean isMappedPort = false;
                for (PatternConnection patternConnection: patternConnections) {
                   if (patternConnection.getModelTaskName().equals(task) && patternConnection.getModelChannelName().equals(wr) && !patternConnection.isNewPort()) {
                        isMappedPort = true;
                        break;
                   }
                }
                if (!isMappedPort) {
                    portsTaskModelLeft.get(task).getWriteChannels().add(wr);
                }
            }
            for (String rd : _portsTaskOfModelAll.get(task).getReadChannels()) {
                boolean isMappedPort = false;
                for (PatternConnection patternConnection: patternConnections) {
                   if (patternConnection.getModelTaskName().equals(task) && patternConnection.getModelChannelName().equals(rd) && !patternConnection.isNewPort()) {
                        isMappedPort = true;
                        break;
                   }
                }
                if (!isMappedPort) {
                    portsTaskModelLeft.get(task).getReadChannels().add(rd);
                }
            }
            for (String se : _portsTaskOfModelAll.get(task).getSendEvents()) {
                boolean isMappedPort = false;
                for (PatternConnection patternConnection: patternConnections) {
                   if (patternConnection.getModelTaskName().equals(task) && patternConnection.getModelChannelName().equals(se) && !patternConnection.isNewPort()) {
                        isMappedPort = true;
                        break;
                   }
                }
                if (!isMappedPort) {
                    portsTaskModelLeft.get(task).getSendEvents().add(se);
                }
            }
            for (String we : _portsTaskOfModelAll.get(task).getWaitEvents()) {
                boolean isMappedPort = false;
                for (PatternConnection patternConnection: patternConnections) {
                   if (patternConnection.getModelTaskName().equals(task) && patternConnection.getModelChannelName().equals(we) && !patternConnection.isNewPort()) {
                        isMappedPort = true;
                        break;
                   }
                }
                if (!isMappedPort) {
                    portsTaskModelLeft.get(task).getWaitEvents().add(we);
                }
            }
        }
        /*LinkedHashMap<String, TaskPorts> portsTaskModelLeft = new LinkedHashMap<String, TaskPorts>(_portsTaskOfModelAll);
        for (PatternConnection patternConnection: patternConnections) {
            if (portsTaskModelLeft.containsKey(patternConnection.getModelTaskName())) {
                portsTaskModelLeft.get(patternConnection.getModelTaskName()).removePort(patternConnection.getModelChannelName());
            }
        }
        for (PatternConnection patternConnection: patternConnections) {
            if (portsTaskModelLeft.containsKey(patternConnection.getModelTaskName())) {
                portsTaskModelLeft.get(patternConnection.getModelTaskName()).removePort(patternConnection.getModelChannelName());
            }
        }*/
        return portsTaskModelLeft;
    }
}