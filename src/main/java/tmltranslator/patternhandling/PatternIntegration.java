package tmltranslator.patternhandling;
/**
 * Class PatternIntegration
 * Pattern Integration in separate thread
 * Creation: 28/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/08/2023
 */
 
import myutil.FileUtils;
import myutil.TraceManager;
import rationals.properties.isNormalized;
import tmltranslator.*;
import ui.window.JDialogPatternGeneration;
import ui.window.TraceData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JDialog;

import org.apache.batik.anim.timing.Trace;

 
public class PatternIntegration implements Runnable {
    String patternPath;
    String patternName;
    LinkedHashMap<String,List<String[]>> portsConnection;
    LinkedHashMap<String, String> clonedTasks;
    LinkedHashMap<String, List<Entry<String, String>>> portsConfig;
    LinkedHashMap<String, Entry<String, String>> tasksMapping;
    LinkedHashMap<String,List<String[]>> channelsMapping;
    LinkedHashMap<String, TaskPattern> patternTasks;
	TMLMapping<?> tmapModel;
	TMLMapping<?> tmapPattern;

    HashMap<String, String> renamedChannels = new HashMap<String, String>();

    public PatternIntegration(String _patternPath, String _patternName, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, String> _clonedTasks, LinkedHashMap<String, List<Entry<String, String>>> _portsConfig, LinkedHashMap<String, Entry<String, String>> _tasksMapping, LinkedHashMap<String,List<String[]>> _channelsMapping, LinkedHashMap<String, TaskPattern> _patternTasks, TMLMapping<?> _tmapModel) {
		this.patternPath = _patternPath;
		this.patternName = _patternName;
		this.portsConnection = _portsConnection;
		this.clonedTasks = _clonedTasks;
		this.portsConfig = _portsConfig;
		this.tasksMapping = _tasksMapping;
		this.channelsMapping = _channelsMapping;
		this.patternTasks = _patternTasks;
		this.tmapModel = _tmapModel;
	}
    
	public TMLMapping<?> startThread() {
		Thread t = new Thread(this);
		t.start();
		try {
			t.join();
		}
		catch (Exception e) {
			TraceManager.addDev("Error in Pattern Integration Thread");
		}
		return tmapModel;
	}

    public void run() {
    	TraceManager.addDev("Creating Pattern");
        if (tmapModel == null) {
            return;
        }

        TMLModeling<?> tmlmNew = tmapModel.getTMLModeling();
        for (TMLTask task : tmlmNew.getTasks()) {
            String[] taskNameSplit = task.getName().split("__");
            task.setName(taskNameSplit[taskNameSplit.length-1]);
        }
        for (TMLChannel ch : tmlmNew.getChannels()) {
            String[] channelNameSplit = ch.getName().split("__");
            ch.setName(channelNameSplit[channelNameSplit.length-1]);
        }
        for (TMLEvent evt : tmlmNew.getEvents()) {
            String[] eventNameSplit = evt.getName().split("__");
            evt.setName(eventNameSplit[eventNameSplit.length-1]);
        }
        for (TMLRequest req : tmlmNew.getRequests()) {
            String[] requestNameSplit = req.getName().split("__");
            req.setName(requestNameSplit[requestNameSplit.length-1]);
        }
        tmapPattern = getTMLMappingOfPattern(patternPath, patternName);
        TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        for (TMLTask task : tmlmPattern.getTasks()) {
            String[] taskNameSplit = task.getName().split("__");
            task.setName(taskNameSplit[taskNameSplit.length-1]);
        }
        for (TMLChannel ch : tmlmPattern.getChannels()) {
            String[] channelNameSplit = ch.getName().split("__");
            ch.setName(channelNameSplit[channelNameSplit.length-1]);
        }
        for (TMLEvent evt : tmlmPattern.getEvents()) {
            String[] eventNameSplit = evt.getName().split("__");
            evt.setName(eventNameSplit[eventNameSplit.length-1]);
        }
        for (TMLRequest req : tmlmPattern.getRequests()) {
            String[] requestNameSplit = req.getName().split("__");
            req.setName(requestNameSplit[requestNameSplit.length-1]);
        }

        TraceManager.addDev("Model elems init :");
        for (TMLChannel ch : tmlmNew.getChannels()) {
            TraceManager.addDev("ch=" + ch.getName()  + ", Origin Task: " + ch.getNameOfOriginTasks() + ", Destination Task: " + ch.getNameOfDestinationTasks() + ", Origin port: " + ch.getOriginPort().getName()+ ", Destination port: " + ch.getDestinationPort().getName());
        }
        for (TMLEvent evt : tmlmNew.getEvents()) {
            TraceManager.addDev("evt=" + evt.getName() + ", Origin Task: " + evt.getOriginTask().getName() + ", Destination Task: " + evt.getDestinationTask().getName() + ", Origin port: " + evt.getOriginPort().getName()+ ", Destination port: " + evt.getDestinationPort().getName());
        }

        for (TMLTask task : tmlmNew.getTasks()) {
            TraceManager.addDev("task=" + task.getName());
            for (TMLWriteChannel wr : task.getWriteChannels()) {
                TraceManager.addDev("task wr=" + wr.getName() + ", ch=" + wr.getChannel(0).getName());
            }
            for (TMLReadChannel rd : task.getReadChannels()) {
                TraceManager.addDev("task rd=" + rd.getName() + ", ch=" + rd.getChannel(0).getName());
            }
            for (TMLSendEvent sd : task.getSendEvents()) {
                TraceManager.addDev("task sd=" + sd.getName() + ", evt=" + sd.getEvent().getName());
            }
            for (TMLWaitEvent wt : task.getWaitEvents()) {
                TraceManager.addDev("task wt=" + wt.getName() + ", evt=" + wt.getEvent().getName());
            }

            for (TMLChannel wr : task.getWriteTMLChannels()) {
                TraceManager.addDev("task write=" + wr.getName());
            }
            for (TMLChannel rd : task.getReadTMLChannels()) {
                TraceManager.addDev("task read=" + rd.getName());
            }
            for (TMLChannel ch : task.getChannelSet()) {
                TraceManager.addDev("task ch=" + ch.getName());
            }
            for (TMLEvent evt : task.getTMLEvents()) {
                TraceManager.addDev("task evt=" + evt.getName());
            }
        }


        TraceManager.addDev("PATTERN elems init :");
        for (TMLChannel ch : tmlmPattern.getChannels()) {
            TraceManager.addDev("ch=" + ch.getName()  + ", Origin Task: " + ch.getNameOfOriginTasks() + ", Destination Task: " + ch.getNameOfDestinationTasks() + ", Origin port: " + ch.getOriginPort().getName()+ ", Destination port: " + ch.getDestinationPort().getName());
        }
        for (TMLEvent evt : tmlmPattern.getEvents()) {
            TraceManager.addDev("evt=" + evt.getName() + ", Origin Task: " + evt.getOriginTask().getName() + ", Destination Task: " + evt.getDestinationTask().getName());
        }

        for (TMLTask task : tmlmPattern.getTasks()) {
            TraceManager.addDev("task=" + task.getName());
            for (TMLWriteChannel wr : task.getWriteChannels()) {
                TraceManager.addDev("task wr=" + wr.getName() + ", ch=" + wr.getChannel(0).getName());
            }
            for (TMLReadChannel rd : task.getReadChannels()) {
                TraceManager.addDev("task rd=" + rd.getName() + ", ch=" + rd.getChannel(0).getName());
            }
            for (TMLSendEvent sd : task.getSendEvents()) {
                TraceManager.addDev("task sd=" + sd.getName() + ", evt=" + sd.getEvent().getName());
            }
            for (TMLWaitEvent wt : task.getWaitEvents()) {
                TraceManager.addDev("task wt=" + wt.getName() + ", evt=" + wt.getEvent().getName());
            }

            for (TMLChannel wr : task.getWriteTMLChannels()) {
                TraceManager.addDev("task write=" + wr.getName());
            }
            for (TMLChannel rd : task.getReadTMLChannels()) {
                TraceManager.addDev("task read=" + rd.getName());
            }
            for (TMLChannel ch : task.getChannelSet()) {
                TraceManager.addDev("task ch=" + ch.getName());
            }
            for (TMLEvent evt : task.getTMLEvents()) {
                TraceManager.addDev("task evt=" + evt.getName());
            }
        }

        renamePatternTasksName();
        renamePatternChannelsName();
        tmapModel = addClonedTask(tmapModel, clonedTasks);
        tmapPattern = updatePatternTasksAttributes(tmapPattern, patternTasks);
        tmapModel = addPatternTasksInModel(tmapModel, tmapPattern, patternTasks);
        tmapModel = addPatternInternalChannelsInModel(tmapModel, tmapPattern, patternTasks);
        //tmapModel = addNewPortToTasks(tmapModel, portsConnection, patternTasks);
        tmapModel = makeConnectionBetweenPatternAndModel(tmapModel, tmapPattern, portsConnection, patternTasks);        
        tmapModel = configDisconnectedChannels(tmapModel, portsConfig, renamedChannels);

        TraceManager.addDev("Model elems result :");

        for (TMLChannel ch : tmlmNew.getChannels()) {
            TraceManager.addDev("ch=" + ch.getName()  + ", Origin Task: " + ch.getNameOfOriginTasks() + ", Destination Task: " + ch.getNameOfDestinationTasks() + ", Origin port: " + ch.getOriginPort().getName()+ ", Destination port: " + ch.getDestinationPort().getName());
        }
        for (TMLEvent evt : tmlmNew.getEvents()) {
            TraceManager.addDev("evt=" + evt.getName() + ", Origin Task: " + evt.getOriginTask().getName() + ", Destination Task: " + evt.getDestinationTask().getName());
        }
        for (TMLTask task : tmlmNew.getTasks()) {
            TraceManager.addDev("task=" + task.getName());
            for (TMLWriteChannel wr : task.getWriteChannels()) {
                TraceManager.addDev("task wr=" + wr.getName() + ", ch=" + wr.getChannel(0).getName());
            }
            for (TMLReadChannel rd : task.getReadChannels()) {
                TraceManager.addDev("task rd=" + rd.getName() + ", ch=" + rd.getChannel(0).getName());
            }
            for (TMLSendEvent sd : task.getSendEvents()) {
                TraceManager.addDev("task sd=" + sd.getName() + ", evt=" + sd.getEvent().getName());
            }
            for (TMLWaitEvent wt : task.getWaitEvents()) {
                TraceManager.addDev("task wt=" + wt.getName() + ", evt=" + wt.getEvent().getName());
            }

            for (TMLChannel wr : task.getWriteTMLChannels()) {
                TraceManager.addDev("task write=" + wr.getName());
            }
            for (TMLChannel rd : task.getReadTMLChannels()) {
                TraceManager.addDev("task read=" + rd.getName());
            }
            for (TMLChannel ch : task.getChannelSet()) {
                TraceManager.addDev("task ch=" + ch.getName());
            }
            for (TMLEvent evt : task.getTMLEvents()) {
                TraceManager.addDev("task evt=" + evt.getName());
            }
        }
	}
    
    public void renamePatternTasksName() {
        TMLModeling<?> tmlmModel = tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        String[] patternTasksKeys = patternTasks.keySet().toArray(new String[patternTasks.keySet().size()]);
        List<String> newTasksNames = new ArrayList<String>();
        for (int i = 0; i < patternTasks.keySet().size(); i++) {
            String taskName = patternTasksKeys[i];
            TraceManager.addDev("renamePatternTasksName: taskName="+taskName);
            if (tmlmModel.getTMLTaskByName(taskName) != null || newTasksNames.contains(taskName)) {
                int indexTask = 0;
                String taskNameWithIndex = taskName + indexTask;
                while (tmlmModel.getTMLTaskByName(taskNameWithIndex) != null || newTasksNames.contains(taskNameWithIndex)) {
                    indexTask += 1;
                    taskNameWithIndex= taskName + indexTask;
                }
                TMLTask taskPattern = tmlmPattern.getTMLTaskByName(taskName);
                TraceManager.addDev("renamePatternTasksName: taskNameWithIndex="+taskNameWithIndex);
                taskPattern.setName(taskNameWithIndex);
                newTasksNames.add(taskNameWithIndex);
                if (portsConnection.containsKey(taskName)) {
                    portsConnection.put(taskNameWithIndex, portsConnection.remove(taskName));
                }
                if (tasksMapping.containsKey(taskName)) {
                    tasksMapping.put(taskNameWithIndex, tasksMapping.remove(taskName));
                }
                if (channelsMapping.containsKey(taskName)) {
                    channelsMapping.put(taskNameWithIndex, channelsMapping.remove(taskName));
                }
                if (patternTasks.containsKey(taskName)) {
                    patternTasks.put(taskNameWithIndex, patternTasks.remove(taskName));
                }
            }
        }
    }

    public void renamePatternChannelsName() {
        TMLModeling<?> tmlmModel = tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        Map<String, String> oldNewChannelName = new HashMap<String,String>();
        Map<String, String> oldNewEventName = new HashMap<String,String>();
        for (String taskName : patternTasks.keySet()) {
            TaskPattern tp = patternTasks.get(taskName);
            for (int i = 0 ; i < tp.getExternalPorts().size() ; i++) {
                PortTaskJsonFile extPort = tp.getExternalPorts().get(i);
                if (tmlmModel.getChannelByName(extPort.name) != null || oldNewChannelName.values().contains(extPort.name)) {
                    if (oldNewChannelName.containsKey(extPort.name)) {
                        TMLChannel channelPattern = tmlmPattern.getChannelByName(extPort.name);
                        channelPattern.setName(oldNewChannelName.get(extPort.name));
                        if (portsConnection.containsKey(taskName)) {
                            for (String[] pc : portsConnection.get(taskName)) {
                                if (pc[0].equals(extPort.name)) {
                                    pc[0] = oldNewChannelName.get(extPort.name);
                                }
                            }
                        }
                        
                        if (channelsMapping.containsKey(taskName)) {
                            for (String[] cm : channelsMapping.get(taskName)) {
                                if (cm[0].equals(extPort.name)) {
                                    cm[0] = oldNewChannelName.get(extPort.name);
                                }
                            }
                        }
                        extPort.name = oldNewChannelName.get(extPort.name);
                        TraceManager.addDev("renamePatternChannelsName-0 : extPort.name= " + extPort.name);
                    } else {
                        boolean toRename = true;
                        if (portsConnection.containsKey(taskName)) {
                            for (String[] portConnection : portsConnection.get(taskName)) {
                                
                                TraceManager.addDev("renamePatternChannelsName-1 : extPort.name= " + extPort.name);
                                TraceManager.addDev("renamePatternChannelsName-1 : portConnection[0]= " + portConnection[0]);
                                TraceManager.addDev("renamePatternChannelsName-1 : portConnection[1]= " + portConnection[1]);
                                TraceManager.addDev("renamePatternChannelsName-1 : portConnection[2]= " + portConnection[2]);
                                if (portConnection[0].equals(extPort.getName()) && portConnection[2].equals(extPort.getName()) && portConnection.length == 3) {
                                    if (tmlmModel.getChannelByName(extPort.name).getOriginTask().getName().equals(portConnection[1]) && extPort.mode.equals(PatternGeneration.MODE_INPUT)) {
                                        toRename = false;
                                        break;
                                    }
                                    if (tmlmModel.getChannelByName(extPort.name).getDestinationTask().getName().equals(portConnection[1]) && extPort.mode.equals(PatternGeneration.MODE_OUTPUT)) {
                                        toRename = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (toRename) {
                            int indexChannel = 0;
                            String channelNameWithIndex = extPort.name + indexChannel;
                            while (tmlmModel.getChannelByName(channelNameWithIndex) != null || oldNewChannelName.values().contains(extPort.name)) {
                                indexChannel += 1;
                                channelNameWithIndex= extPort.name + indexChannel;
                            }
                            TMLChannel channelPattern = tmlmPattern.getChannelByName(extPort.name);
                            oldNewChannelName.put(extPort.name, channelNameWithIndex);
                            channelPattern.setName(channelNameWithIndex);
                            if (portsConnection.containsKey(taskName)) {
                                for (String[] pc : portsConnection.get(taskName)) {
                                    if (pc[0].equals(extPort.name)) {
                                        pc[0] = channelNameWithIndex;
                                    }
                                }
                            }
                            
                            if (channelsMapping.containsKey(taskName)) {
                                for (String[] cm : channelsMapping.get(taskName)) {
                                    if (cm[0].equals(extPort.name)) {
                                        cm[0] = channelNameWithIndex;
                                    }
                                }
                            }
                            extPort.name = channelNameWithIndex;
                            TraceManager.addDev("renamePatternChannelsName-2 : extPort.name= " + extPort.name);
                        }
                    }
                } else if (tmlmModel.getEventByName(extPort.name) != null || oldNewEventName.values().contains(extPort.name)) {
                    if (oldNewEventName.containsKey(extPort.name)) {
                        if (portsConnection.containsKey(taskName)) {
                            for (String[] pc : portsConnection.get(taskName)) {
                                if (pc[0].equals(extPort.name)) {
                                    pc[0] = oldNewEventName.get(extPort.name);
                                }
                            }
                        }
                        extPort.name = oldNewEventName.get(extPort.name);
                    } else {
                        boolean toRename = true;
                        if (portsConnection.containsKey(taskName)) {
                            for (String[] portConnection : portsConnection.get(taskName)) {
                                if (portConnection[0].equals(extPort.getName()) && portConnection[2].equals(extPort.getName()) && portConnection.length == 3) {
                                    if (tmlmModel.getEventByName(extPort.name).getOriginTask().getName().equals(portConnection[1]) && extPort.mode.equals(PatternGeneration.MODE_INPUT)) {
                                        toRename = false;
                                        break;
                                    }
                                    if (tmlmModel.getEventByName(extPort.name).getDestinationTask().getName().equals(portConnection[1]) && extPort.mode.equals(PatternGeneration.MODE_OUTPUT)) {
                                        toRename = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (toRename) {
                            int indexEvent = 0;
                            String eventNameWithIndex = extPort.name + indexEvent;
                            while (tmlmModel.getEventByName(eventNameWithIndex) != null || oldNewEventName.values().contains(extPort.name)) {
                                indexEvent += 1;
                                eventNameWithIndex= extPort.name + indexEvent;
                            }
                            TMLEvent eventPattern = tmlmPattern.getEventByName(extPort.name);
                            eventPattern.setName(eventNameWithIndex);
                            oldNewEventName.put(extPort.name, eventNameWithIndex);
                            if (portsConnection.containsKey(taskName)) {
                                for (String[] pc : portsConnection.get(taskName)) {
                                    if (pc[0].equals(extPort.name)) {
                                        pc[0] = eventNameWithIndex;
                                    }
                                }
                            }
                            extPort.name = eventNameWithIndex;
                        }
                    }
                } 
            }

            for (int i = 0 ; i < tp.getInternalPorts().size() ; i++) {
                PortTaskJsonFile extPort = tp.getInternalPorts().get(i);
                if (tmlmModel.getChannelByName(extPort.name) != null) {
                    TraceManager.addDev("tmlmModel.getChannelByName(extPort.name) != null  :"  + extPort.name);
                    if (oldNewChannelName.containsKey(extPort.name)) {
                        if (channelsMapping.containsKey(taskName)) {
                            for (String[] cm : channelsMapping.get(taskName)) {
                                if (cm[0].equals(extPort.name)) {
                                    cm[0] = oldNewChannelName.get(extPort.name);
                                }
                            }
                        }
                        extPort.name = oldNewChannelName.get(extPort.name);
                        TraceManager.addDev("oldNewChannelName.containsKey(extPort.name)  :" + extPort.name);
                    } else {
                        int indexChannel = 0;
                        String channelNameWithIndex = extPort.name + indexChannel;
                        while (tmlmModel.getChannelByName(channelNameWithIndex) != null) {
                            indexChannel += 1;
                            channelNameWithIndex= extPort.name + indexChannel;
                        }
                        TMLChannel channelPattern = tmlmPattern.getChannelByName(extPort.name);
                        oldNewChannelName.put(extPort.name, channelNameWithIndex);
                        channelPattern.setName(channelNameWithIndex);
                        if (channelsMapping.containsKey(taskName)) {
                            for (String[] cm : channelsMapping.get(taskName)) {
                                if (cm[0].equals(extPort.name)) {
                                    cm[0] = channelNameWithIndex;
                                }
                            }
                        }
                        extPort.name = channelNameWithIndex;
                        TraceManager.addDev("oldNewChannelName.containsKey(extPort.name) else  :" + extPort.name);
                    }
                    
                } else if (tmlmModel.getEventByName(extPort.name) != null) {
                    if (oldNewEventName.containsKey(extPort.name)) {
                        extPort.name = oldNewEventName.get(extPort.name);
                        TraceManager.addDev("else if oldNewEventName.containsKey(extPort.name)  :" + extPort.name);
                    } else {
                        int indexEvent = 0;
                        String eventNameWithIndex = extPort.name + indexEvent;
                        while (tmlmModel.getEventByName(eventNameWithIndex) != null) {
                            indexEvent += 1;
                            eventNameWithIndex= extPort.name + indexEvent;
                        }
                        TMLEvent eventPattern = tmlmPattern.getEventByName(extPort.name);
                        oldNewEventName.put(extPort.name, eventNameWithIndex);
                        eventPattern.setName(eventNameWithIndex);
                        extPort.name = eventNameWithIndex;
                        TraceManager.addDev("else if oldNewEventName.containsKey(extPort.name)  else :" + extPort.name);
                    }
                }
            }
        }
    }

    public TMLMapping<?> addPatternTasksInModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (String taskName : _patternTasks.keySet()) {
            
            TMLTask taskPattern = _tmlmPattern.getTMLTaskByName(taskName);
            if (taskPattern != null) {
                /*int indexTask = 0;
                String taskNameWithIndex = taskName + indexTask;
                while (_tmlmModel.getTMLTaskByName(taskNameWithIndex) != null) {
                    indexTask += 1;
                    taskNameWithIndex= taskName + indexTask;
                }
                taskPattern.setName(taskNameWithIndex);*/
                if (_tmlmModel.getTMLTaskByName(taskName) == null) {
                    _tmlmModel.addTask(taskPattern);
                    TraceManager.addDev("addPatternTasksInModel: taskName="+taskName);
                }
            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> addPatternInternalChannelsInModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (String taskName : _patternTasks.keySet()) {
            for (PortTaskJsonFile portTask : _patternTasks.get(taskName).internalPorts) {
                TMLChannel channelPattern = _tmlmPattern.getChannelByName(portTask.name);
                TMLEvent eventPattern = _tmlmPattern.getEventByName(portTask.name);
                if (channelPattern != null) {
                    /*if (_tmlmModel.getChannelByName(portTask.name) != null) {
                        int indexChannel = 0;
                        String channelNameWithIndex = portTask.name + indexChannel;
                        while (_tmlmModel.getChannelByName(channelNameWithIndex) != null) {
                            indexChannel += 1;
                            channelNameWithIndex = portTask.name + indexChannel;
                        }
                    }*/
                    if (_tmlmModel.getChannelByName(portTask.name) == null) {
                        _tmlmModel.addChannel(channelPattern);
                        TraceManager.addDev("addPatternInternalChannelsInModel: channelName="+portTask.name);
                    }
                } else if (eventPattern != null) {
                    if (_tmlmModel.getEventByName(portTask.name) == null) {
                        _tmlmModel.addEvent(eventPattern);
                        TraceManager.addDev("addPatternInternalChannelsInModel: eventName="+portTask.name);
                    }
                }
            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> updatePatternTasksAttributes(TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (String taskName : _patternTasks.keySet()) {
            TMLTask taskPattern = _tmlmPattern.getTMLTaskByName(taskName);
            if (taskPattern != null) {
                for (int i=0; i < _patternTasks.get(taskName).getAttributes().size(); i++) {
                    TMLAttribute attribTaskPattern = taskPattern.getAttributeByName(_patternTasks.get(taskName).getAttributes().get(i).getName());
                    attribTaskPattern.initialValue = _patternTasks.get(taskName).getAttributes().get(i).getValue();
                }
            }
        }
        return _tmapPattern;
    }


    public TMLMapping<?> addClonedTask(TMLMapping<?> _tmapModel, LinkedHashMap<String, String> _clonedTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (String clonedTask : _clonedTasks.keySet()) {
            try {
                TMLModeling<?> _tmlmModelClone = _tmlmModel.deepClone();
                TMLTask taskClone = _tmlmModelClone.getTMLTaskByName(_clonedTasks.get(clonedTask));
                
                if (taskClone != null) {
                    taskClone.setName(clonedTask);
                    if (_tmlmModel.getTMLTaskByName(taskClone.getName()) != null) {
                        int indexTask = 0;
                        String taskNameWithIndex = taskClone.getName() + indexTask;
                        while (_tmlmModel.getTMLTaskByName(taskNameWithIndex) != null) {
                            indexTask += 1;
                            taskNameWithIndex= taskClone.getName() + indexTask;
                        }
                        taskClone.setName(taskNameWithIndex);
                        _clonedTasks.put(taskNameWithIndex, _clonedTasks.remove(clonedTask));
                    }
                    _tmlmModel.addTask(taskClone);
                }  
            } catch (TMLCheckingError err) {

            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> addNewPortToATask(TMLMapping<?> _tmapModel, LinkedHashMap<String, TaskPattern> _patternTasks, TMLTask _taskToAddPort, String _patternTaskName, String _portPatternName, String _portTolink) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TaskPattern tp = _patternTasks.get(_patternTaskName);
        TMLActivity adTaskToAddPort = _taskToAddPort.getActivityDiagram();
        List<TMLActivityElement> actElemsToAdd = new ArrayList<TMLActivityElement>();
        for (PortTaskJsonFile pTaskJson : tp.externalPorts) {
            if (pTaskJson.name.equals(_portPatternName)) {
                if (pTaskJson.getMode().equals(PatternGeneration.MODE_INPUT)) {
                    if (pTaskJson.getType().equals(PatternGeneration.CHANNEL)) {
                        _taskToAddPort.addWriteTMLChannel(_tmlmModel.getChannelByName(_portPatternName)); 
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(_portTolink)) {
                                        TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                        TMLWriteChannel wrChannel = new TMLWriteChannel(_tmlmModel.getChannelByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                        wrChannel.setNbOfSamples("1"); /// TO UPDATE
                                        wrChannel.addChannel(_tmlmModel.getChannelByName(_portPatternName));
                                        wrChannel.addNext(acElem);
                                        prevElem.setNewNext(acElem, wrChannel);
                                        //adTaskToAddPort.addElement(wrChannel);
                                        actElemsToAdd.add(wrChannel);
                                    }
                                }
                            } else if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (acElemEvent.getEvent().getName().equals(_portTolink)) {
                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                    TMLWriteChannel wrChannel = new TMLWriteChannel(_tmlmModel.getChannelByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                    wrChannel.setNbOfSamples("1"); /// TO UPDATE
                                    wrChannel.addChannel(_tmlmModel.getChannelByName(_portPatternName));
                                    wrChannel.addNext(acElem);
                                    prevElem.setNewNext(acElem, wrChannel);
                                    //adTaskToAddPort.addElement(wrChannel);
                                    actElemsToAdd.add(wrChannel);
                                }
                            }
                        }
                    } else if (pTaskJson.getType().equals(PatternGeneration.EVENT)) {
                        _taskToAddPort.addTMLEvent(_tmlmModel.getEventByName(_portPatternName)); 
                        _tmlmModel.getEventByName(_portPatternName).setOriginTask(_taskToAddPort);
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (acElemEvent.getEvent().getName().equals(_portTolink)) {
                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                    TMLSendEvent sdEvent = new TMLSendEvent(_tmlmModel.getEventByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                    sdEvent.setEvent(_tmlmModel.getEventByName(_portPatternName));
                                    for (TMLWaitEvent we : _tmlmModel.getEventByName(_portPatternName).getDestinationTask().getWaitEvents()) {
                                        if (we.getEvent().getName().equals(_tmlmModel.getEventByName(_portPatternName).getName())) {
                                            for (String param : we.getVectorAllParams()) {
                                                if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                    sdEvent.addParam(param);
                                                } else {
                                                    sdEvent.addParam(param);
                                                    if (_taskToAddPort.getAttributeByName(param) == null) {
                                                        _taskToAddPort.addAttribute(_tmlmModel.getEventByName(_portPatternName).getDestinationTask().getAttributeByName(param).deepClone(_tmlmModel));
                                                    }
                                                }
                                                
                                            }
                                        } 
                                    }
                                    sdEvent.addNext(acElem);
                                    prevElem.setNewNext(acElem, sdEvent);
                                    //adTaskToAddPort.addElement(sdEvent);
                                    actElemsToAdd.add(sdEvent);
                                }
                            } else if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(_portTolink)) {
                                        TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                        TMLSendEvent sdEvent = new TMLSendEvent(_tmlmModel.getEventByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                        sdEvent.setEvent(_tmlmModel.getEventByName(_portPatternName));
                                        for (TMLWaitEvent we : _tmlmModel.getEventByName(_portPatternName).getDestinationTask().getWaitEvents()) {
                                            if (we.getEvent().getName().equals(_tmlmModel.getEventByName(_portPatternName).getName())) {
                                                for (String param : we.getVectorAllParams()) {
                                                    if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                        sdEvent.addParam(param);
                                                    } else {
                                                        sdEvent.addParam(param);
                                                        if (_taskToAddPort.getAttributeByName(param) == null) {
                                                            _taskToAddPort.addAttribute(_tmlmModel.getEventByName(_portPatternName).getDestinationTask().getAttributeByName(param).deepClone(_tmlmModel));
                                                        }
                                                    }
                                                    
                                                }
                                            }
                                            
                                        }
                                        sdEvent.addNext(acElem);
                                        prevElem.setNewNext(acElem, sdEvent);
                                        //adTaskToAddPort.addElement(sdEvent);
                                        actElemsToAdd.add(sdEvent);
                                    }
                                }
                            }
                        }
                    }
                } else if (pTaskJson.getMode().equals(PatternGeneration.MODE_OUTPUT)) {
                    if (pTaskJson.getType().equals(PatternGeneration.CHANNEL)) {
                        _taskToAddPort.addReadTMLChannel(_tmlmModel.getChannelByName(_portPatternName));
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(_portTolink)) {
                                        TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                        TMLReadChannel rdChannel = new TMLReadChannel(_tmlmModel.getChannelByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                        rdChannel.setNbOfSamples("1"); /// TO UPDATE
                                        rdChannel.addChannel(_tmlmModel.getChannelByName(_portPatternName));
                                        rdChannel.addNext(acElem);
                                        prevElem.setNewNext(acElem, rdChannel);
                                        //adTaskToAddPort.addElement(rdChannel);
                                        actElemsToAdd.add(rdChannel);
                                    }
                                }
                            } else if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (acElemEvent.getEvent().getName().equals(_portTolink)) {
                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                    TMLReadChannel rdChannel = new TMLReadChannel(_tmlmModel.getChannelByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                    rdChannel.setNbOfSamples("1"); /// TO UPDATE
                                    rdChannel.addChannel(_tmlmModel.getChannelByName(_portPatternName));
                                    rdChannel.addNext(acElem);
                                    prevElem.setNewNext(acElem, rdChannel);
                                    //adTaskToAddPort.addElement(rdChannel);
                                    actElemsToAdd.add(rdChannel);
                                }
                            }
                        }
                    } else if (pTaskJson.getType().equals(PatternGeneration.EVENT)) {
                        _taskToAddPort.addTMLEvent(_tmlmModel.getEventByName(_portPatternName));
                        _tmlmModel.getEventByName(_portPatternName).setDestinationTask(_taskToAddPort);
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (acElemEvent.getEvent().getName().equals(_portTolink)) {
                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                    TMLWaitEvent wtEvent = new TMLWaitEvent(_tmlmModel.getEventByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                    wtEvent.setEvent(_tmlmModel.getEventByName(_portPatternName));
                                    for (TMLSendEvent sd : _tmlmModel.getEventByName(_portPatternName).getOriginTask().getSendEvents()) {
                                        if (sd.getEvent().getName().equals(_tmlmModel.getEventByName(_portPatternName).getName())) {
                                            for (int indexParam=0; indexParam < sd.getNbOfParams(); indexParam++) {
                                                String param = sd.getParam(indexParam);
                                                if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                    String paramName = _tmlmModel.getEventByName(_portPatternName).getName()+indexParam;
                                                    if (_taskToAddPort.getAttributeByName(paramName) == null) {
                                                        TMLAttribute attribParam = new TMLAttribute(paramName, _tmlmModel.getEventByName(_portPatternName).getParams().get(indexParam).deepClone(_tmlmModel), "");
                                                        _taskToAddPort.addAttribute(attribParam);
                                                    }
                                                    wtEvent.addParam(paramName);
                                                } else {
                                                    wtEvent.addParam(param);
                                                    if (_taskToAddPort.getAttributeByName(param) == null) {
                                                        _taskToAddPort.addAttribute(_tmlmModel.getEventByName(_portPatternName).getOriginTask().getAttributeByName(param).deepClone(_tmlmModel));
                                                    }
                                                }
                                            }
                                        }   
                                    }
                                    wtEvent.addNext(acElem);
                                    prevElem.setNewNext(acElem, wtEvent);
                                    //adTaskToAddPort.addElement(wtEvent);
                                    actElemsToAdd.add(wtEvent);
                                }
                            } else if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(_portTolink)) {
                                        TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                        TMLWaitEvent wtEvent = new TMLWaitEvent(_tmlmModel.getEventByName(_portPatternName).getName(), adTaskToAddPort.getReferenceObject());
                                        wtEvent.setEvent(_tmlmModel.getEventByName(_portPatternName));
                                        for (TMLSendEvent sd : _tmlmModel.getEventByName(_portPatternName).getOriginTask().getSendEvents()) {
                                            if (sd.getEvent().getName().equals(_tmlmModel.getEventByName(_portPatternName).getName())) {
                                                for (int indexParam=0; indexParam < sd.getNbOfParams(); indexParam++) {
                                                    String param = sd.getParam(indexParam);
                                                    if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                        String paramName = _tmlmModel.getEventByName(_portPatternName).getName()+indexParam;
                                                        if (_taskToAddPort.getAttributeByName(paramName) == null) {
                                                            TMLAttribute attribParam = new TMLAttribute(paramName, _tmlmModel.getEventByName(_portPatternName).getParams().get(indexParam).deepClone(_tmlmModel), "");
                                                            _taskToAddPort.addAttribute(attribParam);
                                                        }
                                                        wtEvent.addParam(paramName);
                                                    } else {
                                                        wtEvent.addParam(param);
                                                        if (_taskToAddPort.getAttributeByName(param) == null) {
                                                            _taskToAddPort.addAttribute(_tmlmModel.getEventByName(_portPatternName).getOriginTask().getAttributeByName(param).deepClone(_tmlmModel));
                                                        }
                                                    }       
                                                }
                                            }
                                        }
                                        wtEvent.addNext(acElem);
                                        prevElem.setNewNext(acElem, wtEvent);
                                        //adTaskToAddPort.addElement(wtEvent);
                                        actElemsToAdd.add(wtEvent);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        for (TMLActivityElement actElem : actElemsToAdd) {
            adTaskToAddPort.addElement(actElem);
        }
        return _tmapModel;
    }

    public TMLMapping<?> addNewPortToTasks(TMLMapping<?> _tmapModel, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (String patternTaskName : _portsConnection.keySet()) {
            for (String[] elemP : _portsConnection.get(patternTaskName)) {
                TraceManager.addDev("elemP[0]= " + elemP[0]);
                TraceManager.addDev("elemP[1]= " + elemP[1]);
                TraceManager.addDev("elemP[2]= " + elemP[2]);
                
                if (elemP.length > 3 && elemP[3].equals(JDialogPatternGeneration.NEW_PORT_OPTION)) {
                    TraceManager.addDev("elemP[3]= " + elemP[3]);
                    TMLTask taskToAddPort = _tmlmModel.getTMLTaskByName(elemP[1]);
                    TaskPattern tp = _patternTasks.get(patternTaskName);
                    TMLActivity adTaskToAddPort = taskToAddPort.getActivityDiagram();
                    for (PortTaskJsonFile pTaskJson : tp.externalPorts) {
                        if (pTaskJson.name.equals(elemP[0])) {
                            if (pTaskJson.getMode().equals(PatternGeneration.MODE_INPUT)) {
                                if (pTaskJson.getType().equals(PatternGeneration.CHANNEL)) {
                                    taskToAddPort.addWriteTMLChannel(_tmlmModel.getChannelByName(elemP[0])); 
                                    for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                                        if (acElem instanceof TMLActivityElementChannel) {
                                            TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                            for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                                if (acElemChannel.getChannel(indexChannel).getName().equals(elemP[2])) {
                                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                                    TMLWriteChannel wrChannel = new TMLWriteChannel(_tmlmModel.getChannelByName(elemP[0]).getName(), adTaskToAddPort.getReferenceObject());
                                                    wrChannel.setNbOfSamples("1"); /// TO UPDATE
                                                    wrChannel.addChannel(_tmlmModel.getChannelByName(elemP[0]));
                                                    wrChannel.addNext(acElem);
                                                    prevElem.setNewNext(acElem, wrChannel);
                                                    adTaskToAddPort.addElement(wrChannel);
                                                }
                                            }
                                        }
                                    }
                                } else if (pTaskJson.getType().equals(PatternGeneration.EVENT)) {
                                    taskToAddPort.addTMLEvent(_tmlmModel.getEventByName(elemP[0])); 
                                    _tmlmModel.getEventByName(elemP[0]).setOriginTask(taskToAddPort);
                                    for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                                        if (acElem instanceof TMLActivityElementEvent) {
                                            TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                            if (acElemEvent.getEvent().getName().equals(elemP[2])) {
                                                TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                                TMLSendEvent sdEvent = new TMLSendEvent(_tmlmModel.getEventByName(elemP[0]).getName(), adTaskToAddPort.getReferenceObject());
                                                sdEvent.setEvent(_tmlmModel.getEventByName(elemP[0]));
                                                sdEvent.addNext(acElem);
                                                prevElem.setNewNext(acElem, sdEvent);
                                                adTaskToAddPort.addElement(sdEvent);
                                            }
                                        }
                                    }
                                }
                            } else if (pTaskJson.getMode().equals(PatternGeneration.MODE_OUTPUT)) {
                                if (pTaskJson.getType().equals(PatternGeneration.CHANNEL)) {
                                    taskToAddPort.addReadTMLChannel(_tmlmModel.getChannelByName(elemP[0]));
                                    for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                                        if (acElem instanceof TMLActivityElementChannel) {
                                            TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                            for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                                if (acElemChannel.getChannel(indexChannel).getName().equals(elemP[2])) {
                                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                                    TMLReadChannel rdChannel = new TMLReadChannel(_tmlmModel.getChannelByName(elemP[0]).getName(), adTaskToAddPort.getReferenceObject());
                                                    rdChannel.setNbOfSamples("1"); /// TO UPDATE
                                                    rdChannel.addChannel(_tmlmModel.getChannelByName(elemP[0]));
                                                    rdChannel.addNext(acElem);
                                                    prevElem.setNewNext(acElem, rdChannel);
                                                    adTaskToAddPort.addElement(rdChannel);
                                                }
                                            }
                                        }
                                    }
                                } else if (pTaskJson.getType().equals(PatternGeneration.EVENT)) {
                                    taskToAddPort.addTMLEvent(_tmlmModel.getEventByName(elemP[0]));
                                    _tmlmModel.getEventByName(elemP[0]).setDestinationTask(taskToAddPort);
                                    for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                                        if (acElem instanceof TMLActivityElementEvent) {
                                            TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                            if (acElemEvent.getEvent().getName().equals(elemP[2])) {
                                                TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                                TMLWaitEvent wtEvent = new TMLWaitEvent(_tmlmModel.getEventByName(elemP[0]).getName(), adTaskToAddPort.getReferenceObject());
                                                wtEvent.setEvent(_tmlmModel.getEventByName(elemP[0]));
                                                wtEvent.addNext(acElem);
                                                prevElem.setNewNext(acElem, wtEvent);
                                                adTaskToAddPort.addElement(wtEvent);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            } 
        }
        return _tmapModel;
    }

    public TMLMapping<?> makeConnectionBetweenPatternAndModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (String patternTaskName : _portsConnection.keySet()) {
            for (String[] portConn : _portsConnection.get(patternTaskName)) {
                String patternTaskPortName = portConn[0];
                String modelTaskName = portConn[1];
                String modelTaskPortName = portConn[2];
                TMLTask patternTask = _tmlmModel.getTMLTaskByName(patternTaskName);
                TMLTask modelTask = _tmlmModel.getTMLTaskByName(modelTaskName);
                TMLChannel chInModel = _tmlmModel.getChannelByName(modelTaskPortName);
                TMLEvent evtInModel = _tmlmModel.getEventByName(modelTaskPortName);
                TMLChannel chInPattern = _tmlmPattern.getChannelByName(patternTaskPortName);
                TMLEvent evtInPattern = _tmlmPattern.getEventByName(patternTaskPortName);
                TraceManager.addDev("patternTaskName =  " + patternTaskName);
                TraceManager.addDev("patternTaskPortName =  " + patternTaskPortName);
                TraceManager.addDev("modelTaskName =  " + modelTaskName);
                TraceManager.addDev("modelTaskPortName =  " + modelTaskPortName);
                TraceManager.addDev("portConn.length =  " + portConn.length);
                if (chInModel == null) {
                    TraceManager.addDev("chInModel == null");
                }
                
                if (portConn.length == 3) {
                    if (chInModel != null) {
                        if (chInPattern.getOriginTask().equals(patternTask)) {
                            chInPattern.setDestinationTask(modelTask);
                            chInPattern.setPorts(chInPattern.getOriginPort(), new TMLPort(chInPattern.getName(), chInPattern.getReferenceObject()));
                            _tmlmModel.addChannel(chInPattern);
                            renamedChannels.put(chInModel.getName(), chInPattern.getName());
                            for (TMLActivityElement ae : modelTask.getActivityDiagram().getElements()) {
                                if (ae instanceof TMLReadChannel) {
                                    TMLReadChannel aeRead = (TMLReadChannel) ae;
                                    if (aeRead.getChannel(0).getName().equals(chInModel.getName())) {
                                        TraceManager.addDev("replace: " + chInModel.getName() + " with " + chInModel.getName());
                                        aeRead.replaceChannelWith(chInModel, chInPattern);
                                    }
                                }
                            }
                            //chInModel.setOriginTask(patternTask);
                            //chInModel.setPorts(new TMLPort(chInModel.getName(), chInModel.getReferenceObject()), chInModel.getDestinationPort());
                        } else if (chInModel.getOriginTask().equals(modelTask)) {
                            chInModel.setDestinationTask(patternTask);
                            chInModel.setPorts(chInModel.getOriginPort(), new TMLPort(chInModel.getName(), chInModel.getReferenceObject()));
                            for (TMLActivityElement ae : patternTask.getActivityDiagram().getElements()) {
                                if (ae instanceof TMLReadChannel) {
                                    TMLReadChannel aeRead = (TMLReadChannel) ae;
                                    if (aeRead.getChannel(0).getName().equals(chInPattern.getName())) {
                                        aeRead.replaceChannelWith(chInPattern, chInModel);
                                    }
                                }
                            }
                        }
                    } else if (evtInModel != null) {
                        if (evtInPattern.getOriginTask().equals(patternTask)) {
                            evtInPattern.setDestinationTask(modelTask);
                            evtInPattern.setPorts(evtInPattern.getOriginPort(), new TMLPort(evtInPattern.getName(), evtInPattern.getReferenceObject()));
                            _tmlmModel.addEvent(evtInPattern);
                            renamedChannels.put(evtInModel.getName(), evtInModel.getName());
                            for (TMLActivityElement ae : modelTask.getActivityDiagram().getElements()) {
                                if (ae instanceof TMLWaitEvent) {
                                    TMLWaitEvent aeWait = (TMLWaitEvent) ae;
                                    if (aeWait.getEvent().getName().equals(evtInModel.getName())) {
                                        aeWait.replaceEventWith(evtInModel, evtInModel);
                                    }
                                }
                            }
                            //evtInModel.setOriginTask(patternTask);
                            //evtInModel.setPorts(new TMLPort(evtInModel.getName(), evtInModel.getReferenceObject()), evtInModel.getDestinationPort());
                        } else if (evtInModel.getOriginTask().equals(modelTask)) {
                            evtInModel.setDestinationTask(patternTask);
                            evtInModel.setPorts(evtInModel.getOriginPort(), new TMLPort(evtInModel.getName(), evtInModel.getReferenceObject()));
                            for (TMLActivityElement ae : patternTask.getActivityDiagram().getElements()) {
                                if (ae instanceof TMLWaitEvent) {
                                    TMLWaitEvent aeWait = (TMLWaitEvent) ae;
                                    if (aeWait.getEvent().getName().equals(evtInPattern.getName())) {
                                        aeWait.replaceEventWith(evtInPattern, evtInModel);
                                    }
                                }
                            }
                        }
                    }
                } else if (portConn.length == 4 && portConn[3].equals(JDialogPatternGeneration.NEW_PORT_OPTION)) {
                    if (chInPattern != null) {
                        if (chInPattern.getDestinationTask().equals(patternTask)) {
                            chInPattern.setOriginTask(modelTask);
                            chInPattern.setPorts(new TMLPort(chInPattern.getName(), chInPattern.getReferenceObject()), chInPattern.getDestinationPort());
                            _tmlmModel.addChannel(chInPattern);
                            _tmapModel = addNewPortToATask(_tmapModel, _patternTasks, modelTask, patternTaskName, patternTaskPortName, modelTaskPortName);
                        } else if (chInPattern.getOriginTask().equals(patternTask)) {
                            chInPattern.setDestinationTask(modelTask);
                            chInPattern.setPorts(chInPattern.getOriginPort(), new TMLPort(chInPattern.getName(), chInPattern.getReferenceObject()));
                            _tmlmModel.addChannel(chInPattern);
                            _tmapModel = addNewPortToATask(_tmapModel, _patternTasks, modelTask, patternTaskName, patternTaskPortName, modelTaskPortName);
                        }
                    } else if (evtInPattern != null) {
                        if (evtInPattern.getDestinationTask().equals(patternTask)) {
                            evtInPattern.setOriginTask(modelTask);
                            evtInPattern.setPorts(new TMLPort(evtInPattern.getName(), evtInPattern.getReferenceObject()), evtInPattern.getDestinationPort());
                            _tmlmModel.addEvent(evtInPattern);
                            _tmapModel = addNewPortToATask(_tmapModel, _patternTasks, modelTask, patternTaskName, patternTaskPortName, modelTaskPortName);
                        } else if (evtInPattern.getOriginTask().equals(patternTask)) {
                            evtInPattern.setDestinationTask(modelTask);
                            evtInPattern.setPorts(evtInPattern.getOriginPort(), new TMLPort(evtInPattern.getName(), evtInPattern.getReferenceObject()));
                            _tmlmModel.addEvent(evtInPattern);
                            _tmapModel = addNewPortToATask(_tmapModel, _patternTasks, modelTask, patternTaskName, patternTaskPortName, modelTaskPortName);
                        }
                    }
                }
    
            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> configDisconnectedChannels(TMLMapping<?> _tmapModel, LinkedHashMap<String, List<Entry<String, String>>> _portsConfig, HashMap<String, String> _renamedChannels) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TraceManager.addDev("configDisconnectedChannels");
        for (String taskName  : _portsConfig.keySet()) {
            TraceManager.addDev("taskName = " + taskName);
            for (Entry<String, String> portDecision : _portsConfig.get(taskName)) {
                String portName = portDecision.getKey();
                String decision = portDecision.getValue();
                TraceManager.addDev("decision = " + decision);
                TraceManager.addDev("portName = " + portName);
                TMLTask task = _tmlmModel.getTMLTaskByName(taskName);
                if (task != null) {
                    TMLActivity adTask = task.getActivityDiagram();
                    TMLActivityElement elemMerge = null;
                    if (decision != "") {
                        for (TMLActivityElement acElem : adTask.getElements()) {
                            if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (_renamedChannels.containsKey(decision)) {
                                        decision = _renamedChannels.get(decision);
                                    }
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(decision)) {
                                        try {
                                            elemMerge = acElem.deepClone(_tmlmModel);
                                        } catch (TMLCheckingError err) {

                                        }
                                    }
                                }
                            } else if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (_renamedChannels.containsKey(decision)) {
                                    decision = _renamedChannels.get(decision);
                                }
                                if (acElemEvent.getEvent().getName().equals(decision)) {
                                    try {
                                            elemMerge = acElem.deepClone(_tmlmModel);
                                    } catch (TMLCheckingError err) {

                                    }
                                }
                            }
                        }
                    }

                    List<TMLActivityElement> elemsToRemove = new ArrayList<TMLActivityElement>();
                    for (TMLActivityElement acElem : adTask.getElements()) {
                        if (acElem instanceof TMLActivityElementChannel) {
                            TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                            for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                if (acElemChannel.getChannel(indexChannel).getName().equals(portName)) {
                                    TMLActivityElement prevElem =  adTask.getPrevious(acElem);
                                    TMLActivityElement nextElem =  acElem.getNextElement(0);
                                    if (decision.equals("")) {
                                        prevElem.setNewNext(acElem, nextElem);
                                        elemsToRemove.add(acElem);
                                    } else {
                                        try {
                                            TMLActivityElement newNextElem =  elemMerge.deepClone(_tmlmModel);
                                            prevElem.setNewNext(acElem, newNextElem);
                                            newNextElem.clearNexts();
                                            newNextElem.addNext(nextElem);
                                            elemsToRemove.add(acElem);
                                        } catch (TMLCheckingError err) {

                                        }
                                        
                                    }
                                }
                            }
                        } else if (acElem instanceof TMLActivityElementEvent) {
                            TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                            if (acElemEvent.getEvent().getName().equals(portName)) {
                                TMLActivityElement prevElem =  adTask.getPrevious(acElem);
                                TMLActivityElement nextElem =  acElem.getNextElement(0);
                                if (decision.equals("")) {
                                    prevElem.setNewNext(acElem, nextElem);
                                    elemsToRemove.add(acElem);
                                } else {
                                    try {
                                        TMLActivityElement newNextElem =  elemMerge.deepClone(_tmlmModel);
                                        prevElem.setNewNext(acElem, newNextElem);
                                        newNextElem.clearNexts();
                                        newNextElem.addNext(nextElem);
                                        elemsToRemove.add(acElem);
                                    } catch (TMLCheckingError err) {

                                    }
                                }
                            }
                        }
                    }
                    for (TMLActivityElement elemToRemove : elemsToRemove) {
                        TraceManager.addDev("elemToRemove = " + elemToRemove.getName());
                        adTask.removeElement(elemToRemove);
                        /*task.getReadTMLChannelSet().remove(elemToRemove);
                        task.getReadTMLChannels().remove(elemToRemove);
                        task.getReadChannels().remove(elemToRemove);
                        task.getChannelSet().remove(elemToRemove);*/
                    }

                    TraceManager.addDev("task.getReadTMLChannelSet().size() = " + task.getReadTMLChannelSet().size());
                    TraceManager.addDev("task.getReadTMLChannels().size() = " + task.getReadTMLChannels().size());
                    TraceManager.addDev("task.getReadChannels().size() = " + task.getReadChannels().size());
                    TraceManager.addDev("task.getChannels().size() = " + task.getChannelSet().size());
                    TraceManager.addDev("task.getTMLEvents().size() = " + task.getTMLEvents().size());
                    TraceManager.addDev("task.getEventSet().size() = " + task.getEventSet().size());
                    TraceManager.addDev("task.getSendEvents().size() = " + task.getSendEvents().size());
                    TraceManager.addDev("task.getWaitEvents().size() = " + task.getWaitEvents().size());
                    for (TMLActivityElement acElem : adTask.getElements()) {
                        TraceManager.addDev("acElem = " + acElem.getName());
                    }
                }
                
            }
            

        }
        return _tmapModel;
    }


    @SuppressWarnings("unchecked")
    public TMLMapping<?> getTMLMappingOfPattern(String _path, String fileName) {
        TMLMappingTextSpecification<Class<?>> tmts = new TMLMappingTextSpecification<Class<?>>(fileName);
        File f = new File(_path + fileName + ".tmap");
        String spec = null;
        try {
            spec = FileUtils.loadFileData(f);
        } catch (Exception e) {
            System.out.println("Exception executing: loading " + fileName);
        }
        boolean parsed = tmts.makeTMLMapping(spec, _path);
        TMLMapping<?> _tmapPattern = tmts.getTMLMapping();
        // Checking syntax
        TMLSyntaxChecking syntax = new TMLSyntaxChecking(_tmapPattern);
        syntax.checkSyntax();
        return _tmapPattern;
    }
}
