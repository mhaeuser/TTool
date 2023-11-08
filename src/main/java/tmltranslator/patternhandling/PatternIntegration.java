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
import tmltranslator.*;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

 
public class PatternIntegration implements Runnable {
    String patternPath;
    String patternName;
    PatternConfiguration patternConfiguration;
    LinkedHashMap<String, TaskPattern> patternTasks;
	TMLMapping<?> tmapModel;
	TMLMapping<?> tmapPattern;

    HashMap<String, String> tasksClonedIntoModel = new HashMap<String, String>();
    HashMap<Entry<String, String>, String> channelsClonedIntoModel = new HashMap<Entry<String, String>, String>();

    HashMap<String, String> tasksOfPatternIntoModel = new HashMap<String, String>();
    HashMap<Entry<String, String>, String> channelsOfPatternIntoModel = new HashMap<Entry<String, String>, String>();

    HashMap<Entry<String, String>, String> renamedModelChannels = new HashMap<Entry<String, String>, String>();
    HashMap<TMLTask, List<TMLActivityElement>> clonedTasksToRemElems = new HashMap<TMLTask, List<TMLActivityElement>>();
    //List<TMLChannel> channelsFromPatternToMap = new ArrayList<TMLChannel>();
    //List<TMLChannel> channelsFromClonedToMap = new ArrayList<TMLChannel>();

    public PatternIntegration(String _patternPath, String _patternName, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks, TMLMapping<?> _tmapModel) {
		this.patternPath = _patternPath;
		this.patternName = _patternName;
		this.patternConfiguration = _patternConfiguration;
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
    	TraceManager.addDev("Integrating Pattern");
        if (tmapModel == null) {
            return;
        }
        String appTab = "";
        TMLModeling<?> tmlmNew = tmapModel.getTMLModeling();
        for (TMLTask task : tmlmNew.getTasks()) {
            String[] taskNameSplit = task.getName().split("__");
            task.setName(taskNameSplit[taskNameSplit.length-1]);
            appTab = taskNameSplit[0];
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

        tmapModel = addClonedTask(tmapModel, patternConfiguration);
        renamePatternTasksName();
        renamePatternChannelsName();
        TraceManager.addDev("%% tasksClonedIntoModel:");
        for (String tas : tasksClonedIntoModel.keySet()) {
            TraceManager.addDev("Clone task name in config:" + tas + "  Clone task name in Model:" + tasksClonedIntoModel.get(tas));
        }
        TraceManager.addDev("%% tasksOfPatternIntoModel:");
        for (String tas : tasksOfPatternIntoModel.keySet()) {
            TraceManager.addDev("Pattern task name in config:" + tas + "  Pattern task name in Model:" + tasksOfPatternIntoModel.get(tas));
        }
        TraceManager.addDev("%% channelsClonedIntoModel:");
        for (Entry<String, String> chT : channelsClonedIntoModel.keySet()) {
            TraceManager.addDev("channel name in config:" + chT.getValue() + " of task: " + chT.getKey() + "  Clone channel name in Model:" + channelsClonedIntoModel.get(chT));
        }
        TraceManager.addDev("%% channelsOfPatternIntoModel:");
        for (Entry<String, String> chT : channelsOfPatternIntoModel.keySet()) {
            TraceManager.addDev("channel Of pattern name in config:" + chT.getValue() + " of task: " + chT.getKey() + "  Pattern channel name in Model:" + channelsOfPatternIntoModel.get(chT));
        }
        tmapPattern = updatePatternTasksAttributes(tmapPattern, patternConfiguration.getUpdatedPatternAttributes());
        tmapModel = addPatternTasksInModel(tmapModel, tmapPattern, patternTasks);
        tmapModel = addPatternInternalChannelsInModel(tmapModel, tmapPattern, patternTasks);
        //tmapModel = addNewPortToTasks(tmapModel, patternConfiguration.getPortsConnection(), patternTasks);
        tmapModel = makeConnectionBetweenPatternAndModel(tmapModel, tmapPattern, patternConfiguration.getPortsConnection(), patternTasks, patternConfiguration.getClonedTasks());        
        tmapModel = configDisconnectedChannels(tmapModel, patternConfiguration.getPortsConfig());
        tmapModel = removeNotUsedChannelsInClonedTask(tmapModel, clonedTasksToRemElems);
        tmapModel = mapTasksInArch(tmapModel, patternConfiguration.getTasksMapping());
        tmapModel = mapTasksInArchAuto(tmapModel, tmapPattern, patternConfiguration.getTasksMapping(), patternConfiguration, patternTasks);
        tmapModel = mapChannelsInArch(tmapModel, patternConfiguration.getChannelsMapping());
        tmapModel = mapChannelsInArchAuto(tmapModel, tmapPattern, patternConfiguration.getTasksMapping(), patternConfiguration.getChannelsMapping(), patternConfiguration, patternTasks);
        tmapModel = generateSecurityForChannels(tmapModel, tmapPattern, patternConfiguration, patternTasks, appTab);
	}

    public TMLMapping<?> addClonedTask(TMLMapping<?> _tmapModel, PatternConfiguration _patternConfiguration) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (PatternCloneTask patternCloneTask : _patternConfiguration.getClonedTasks()) {
            try {
                String clonedTask = patternCloneTask.getClonedTask();
                String taskToClone = patternCloneTask.getTaskToClone();
                TMLModeling<?> _tmlmModelClone = _tmlmModel.deepClone();
                TMLTask taskClone = _tmlmModelClone.getTMLTaskByName(taskToClone);
                
                if (taskClone != null) {
                    taskClone.setName(clonedTask);
                    if (_tmlmModel.getTMLTaskByName(taskClone.getName()) != null || tasksClonedIntoModel.containsValue(taskClone.getName()) || tasksOfPatternIntoModel.containsValue(taskClone.getName())) {
                        int indexTask = 0;
                        String taskNameWithIndex = taskClone.getName() + indexTask;
                        while (_tmlmModel.getTMLTaskByName(taskNameWithIndex) != null || tasksClonedIntoModel.containsValue(taskNameWithIndex) || tasksOfPatternIntoModel.containsValue(taskNameWithIndex)) {
                            indexTask += 1;
                            taskNameWithIndex= taskClone.getName() + indexTask;
                        }
                        taskClone.setName(taskNameWithIndex);
                        tasksClonedIntoModel.put(clonedTask, taskNameWithIndex);
                    } else {
                        tasksClonedIntoModel.put(clonedTask, clonedTask);
                    }
                    //List<String> usedChannelsOfClonedTask = new ArrayList<String>();
                    //Map<String, String> oldNewChannelsClonedTaskName = new HashMap<String, String>();
                    //Map<String, String> oldNewEventsClonedTaskName = new HashMap<String, String>();
                    List<TMLActivityElement> aeElemsToRemove = new ArrayList<TMLActivityElement>();
                    for (PatternConnection patternConnection : _patternConfiguration.getPortsConnection()) {
                        if (patternConnection.getModelTaskName().equals(clonedTask) && !patternConnection.isNewPort()) {
                            String modelChannelName = patternConnection.getModelChannelName();
                            if (_tmlmModel.getChannelByName(modelChannelName) != null) {
                                int indexChannel = 0;
                                String channelNameWithIndex = modelChannelName + indexChannel;
                                while (_tmlmModel.getChannelByName(channelNameWithIndex) != null || channelsClonedIntoModel.containsValue(channelNameWithIndex) || channelsOfPatternIntoModel.containsValue(channelNameWithIndex)) {
                                    indexChannel += 1;
                                    channelNameWithIndex = modelChannelName + indexChannel;
                                }
                                //oldNewChannelsClonedTaskName.put(modelChannelName, channelNameWithIndex);
                                TraceManager.addDev("Channel : old =" + modelChannelName + " New=" + channelNameWithIndex);
                                //patternConnection.setModelChannelName(channelNameWithIndex);
                                channelsClonedIntoModel.put(Map.entry(clonedTask, modelChannelName), channelNameWithIndex);
                            } else if (_tmlmModel.getEventByName(modelChannelName) != null) {
                                int indexEvent = 0;
                                String eventNameWithIndex = modelChannelName + indexEvent;
                                while (_tmlmModel.getEventByName(eventNameWithIndex) != null || channelsClonedIntoModel.containsValue(eventNameWithIndex) || channelsOfPatternIntoModel.containsValue(eventNameWithIndex)) {
                                    indexEvent += 1;
                                    eventNameWithIndex = modelChannelName + indexEvent;
                                }
                                //oldNewEventsClonedTaskName.put(modelChannelName, eventNameWithIndex);
                                //patternConnection.setModelChannelName(eventNameWithIndex);
                                channelsClonedIntoModel.put(Map.entry(clonedTask, modelChannelName), eventNameWithIndex);
                            } else if (channelsClonedIntoModel.containsValue(modelChannelName) || channelsOfPatternIntoModel.containsValue(modelChannelName)){
                                int index = 0;
                                String nameWithIndex = modelChannelName + index;
                                while (channelsClonedIntoModel.containsValue(nameWithIndex) || channelsOfPatternIntoModel.containsValue(nameWithIndex)) {
                                    index += 1;
                                    nameWithIndex = modelChannelName + index;
                                }
                                channelsClonedIntoModel.put(Map.entry(clonedTask, modelChannelName), nameWithIndex);
                            } else {
                                channelsClonedIntoModel.put(Map.entry(clonedTask, modelChannelName), modelChannelName);
                            }
                        } else if (patternConnection.getModelTaskName().equals(clonedTask) && patternConnection.isNewPort()) {
                            
                        }
                    }
                    /*for (MappingPatternChannel mappingPatternChannel : _patternConfiguration.getChannelsMapping()) {
                        //for (String[] st : _patternConfiguration.getChannelsMapping().get(taskOfChannelToMap)) {
                        if (mappingPatternChannel.getTaskOfChannelToMap().equals(clonedTask)) {
                            if (oldNewChannelsClonedTaskName.keySet().contains(st[1])) {
                                st[1] = oldNewChannelsClonedTaskName.get(st[1]);
                            } else if (oldNewEventsClonedTaskName.keySet().contains(st[1])) {
                                st[1] = oldNewEventsClonedTaskName.get(st[1]);
                            }
                        }
                        if (st.length == 4) {
                            if (st[2].equals(clonedTask)) {
                                if (oldNewChannelsClonedTaskName.keySet().contains(st[3])) {
                                    st[3] = oldNewChannelsClonedTaskName.get(st[3]);
                                } else if (oldNewEventsClonedTaskName.keySet().contains(st[3])) {
                                    st[3] = oldNewEventsClonedTaskName.get(st[3]);
                                }
                            }
                        }
                        //}
                    }*/
                    for (TMLActivityElement ae : taskClone.getActivityDiagram().getElements()) {
                        if (ae instanceof TMLActivityElementChannel) {
                            TMLActivityElementChannel aeChannel = (TMLActivityElementChannel) ae;
                            for (int i=0; i < aeChannel.getNbOfChannels(); i++) {
                                if (channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeChannel.getChannel(i).getName())) == null) {
                                    //TMLActivityElement prevElem =  taskClone.getActivityDiagram().getPrevious(ae);
                                    //prevElem.setNewNext(ae, ae.getNextElement(0));
                                    aeElemsToRemove.add(ae);
                                } else {
                                    
                                    for (TMLPort port : aeChannel.getChannel(i).getOriginPorts()) {
                                        port.setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeChannel.getChannel(i).getName())));
                                    }
                                    for (TMLPort port : aeChannel.getChannel(i).getDestinationPorts()) {
                                        port.setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeChannel.getChannel(i).getName())));
                                    }
                                    if (aeChannel.getChannel(i).getOriginPort() != null) {
                                        aeChannel.getChannel(i).getOriginPort().setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeChannel.getChannel(i).getName())));
                                        TraceManager.addDev("Port Origin New Name = " + aeChannel.getChannel(i).getOriginPort().getName());
                                    }
                                    if (aeChannel.getChannel(i).getDestinationPort() != null) {
                                        aeChannel.getChannel(i).getDestinationPort().setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeChannel.getChannel(i).getName())));
                                    }
                                    TraceManager.addDev("Name Channel Clone Before =" + aeChannel.getChannel(i).getName());
                                    aeChannel.getChannel(i).setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeChannel.getChannel(i).getName())));
                                    TraceManager.addDev("Name Channel Clone After =" + aeChannel.getChannel(i).getName());
                                    if (_tmlmModel.getChannelByName(aeChannel.getChannel(i).getName()) == null) {
                                        _tmlmModel.addChannel(aeChannel.getChannel(i));
                                        //channelsFromClonedToMap.add(aeChannel.getChannel(i));
                                    }
                                }
                            }
                        } else if (ae instanceof TMLActivityElementEvent) {
                            TMLActivityElementEvent aeEvent = (TMLActivityElementEvent) ae;
                            //if (!oldNewEventsClonedTaskName.keySet().contains(aeEvent.getEvent().getName())) {
                            if (channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeEvent.getEvent().getName())) == null) {
                                //TMLActivityElement prevElem =  taskClone.getActivityDiagram().getPrevious(ae);
                                //prevElem.setNewNext(ae, ae.getNextElement(0));
                                aeElemsToRemove.add(ae);
                            } else {
                                
                                for (TMLPort port : aeEvent.getEvent().getOriginPorts()) {
                                    port.setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeEvent.getEvent().getName())));
                                }
                                for (TMLPort port : aeEvent.getEvent().getDestinationPorts()) {
                                    port.setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeEvent.getEvent().getName())));
                                }
                                if (aeEvent.getEvent().getOriginPort() != null) {
                                    aeEvent.getEvent().getOriginPort().setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeEvent.getEvent().getName())));
                                }
                                if (aeEvent.getEvent().getDestinationPort() != null) {
                                    aeEvent.getEvent().getDestinationPort().setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeEvent.getEvent().getName())));
                                }
                                aeEvent.getEvent().setName(channelsClonedIntoModel.get(Map.entry(taskClone.getName(), aeEvent.getEvent().getName())));
                                if (_tmlmModel.getEventByName(aeEvent.getEvent().getName()) == null) {
                                    _tmlmModel.addEvent(aeEvent.getEvent());
                                }
                            }
                        }
                    }
                    /*for (TMLActivityElement ae : aeElemsToRemove) {
                        taskClone.getActivityDiagram().removeElement(ae);
                    }*/
                    clonedTasksToRemElems.put(taskClone, aeElemsToRemove);
                    _tmlmModel.addTask(taskClone);
                }  
            } catch (TMLCheckingError err) {

            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> removeNotUsedChannelsInClonedTask(TMLMapping<?> _tmapModel, HashMap<TMLTask, List<TMLActivityElement>> _clonedTaskToRemoveElems) {
        for (TMLTask taskClone : _clonedTaskToRemoveElems.keySet()) {
            TraceManager.addDev("removeNotUsed: taskClone=" + taskClone.getName());
            List<TMLActivityElement> _aeElemsToRemove = _clonedTaskToRemoveElems.get(taskClone);
            for (TMLActivityElement ae : _aeElemsToRemove) {
                TMLActivityElement prevElem =  taskClone.getActivityDiagram().getPrevious(ae);
                prevElem.setNewNext(ae, ae.getNextElement(0));
            }
            for (TMLActivityElement ae : _aeElemsToRemove) {
                taskClone.getActivityDiagram().removeElement(ae);
            }
        }
        return _tmapModel;
    }
    
    public void renamePatternTasksName() {
        TMLModeling<?> tmlmModel = tmapModel.getTMLModeling();
        //TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        String[] patternTasksKeys = patternTasks.keySet().toArray(new String[patternTasks.keySet().size()]);
        //List<String> newTasksNames = new ArrayList<String>();
        //for (int i = 0; i < patternTasks.keySet().size(); i++) {
        for (String taskName : patternTasksKeys) {
            //String taskName = patternTasksKeys[i];
            if (tmlmModel.getTMLTaskByName(taskName) != null || tasksOfPatternIntoModel.containsValue(taskName) || tasksClonedIntoModel.containsValue(taskName)) {
                int indexTask = 0;
                String taskNameWithIndex = taskName + indexTask;
                while (tmlmModel.getTMLTaskByName(taskNameWithIndex) != null || tasksOfPatternIntoModel.containsValue(taskNameWithIndex) || tasksClonedIntoModel.containsValue(taskNameWithIndex)) {
                    indexTask += 1;
                    taskNameWithIndex= taskName + indexTask;
                }
                tasksOfPatternIntoModel.put(taskName, taskNameWithIndex);
                /*TMLTask taskPattern = tmlmPattern.getTMLTaskByName(taskName);
                taskPattern.setName(taskNameWithIndex);
                if (patternConfiguration.getPortsConnection().containsKey(taskName)) {
                    patternConfiguration.getPortsConnection().put(taskNameWithIndex, patternConfiguration.getPortsConnection().remove(taskName));
                }
                if (patternConfiguration.getTasksMapping().containsKey(taskName)) {
                    patternConfiguration.getTasksMapping().put(taskNameWithIndex, patternConfiguration.getTasksMapping().remove(taskName));
                }
                if (patternConfiguration.getChannelsMapping().containsKey(taskName)) {
                    patternConfiguration.getChannelsMapping().put(taskNameWithIndex, patternConfiguration.getChannelsMapping().remove(taskName));
                }
                if (patternConfiguration.getUpdatedPatternAttributes().containsKey(taskName)) {
                    patternConfiguration.getUpdatedPatternAttributes().put(taskNameWithIndex, patternConfiguration.getUpdatedPatternAttributes().remove(taskName));
                }
                if (patternConfiguration.getChannelsWithSecurity().containsKey(taskName)) {
                    patternConfiguration.getChannelsWithSecurity().put(taskNameWithIndex, patternConfiguration.getChannelsWithSecurity().remove(taskName));
                }
                if (patternTasks.containsKey(taskName)) {
                    patternTasks.put(taskNameWithIndex, patternTasks.remove(taskName));
                }*/
                
            } else {
                tasksOfPatternIntoModel.put(taskName, taskName);
            }
        }
    }

    public void renamePatternChannelsName() {
        TMLModeling<?> tmlmModel = tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        //Map<String, String> oldNewChannelName = new HashMap<String,String>();
        //Map<String, String> oldNewEventName = new HashMap<String,String>();
        for (String taskName : patternTasks.keySet()) {
            TaskPattern tp = patternTasks.get(taskName);
            for (int i = 0 ; i < tp.getInternalPorts().size() ; i++) {
                PortTaskJsonFile extPort = tp.getInternalPorts().get(i);
                if (extPort.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                    if (tmlmModel.getChannelByName(extPort.getName()) != null) {
                        int indexChannel = 0;
                        String channelNameWithIndex = extPort.getName() + indexChannel;
                        while (tmlmModel.getChannelByName(channelNameWithIndex) != null || channelsOfPatternIntoModel.containsValue(channelNameWithIndex) || channelsClonedIntoModel.containsValue(channelNameWithIndex)) {
                            indexChannel += 1;
                            channelNameWithIndex= extPort.getName() + indexChannel;
                        }
                        /*TMLChannel channelPattern = tmlmPattern.getChannelByName(extPort.name);
                        oldNewChannelName.put(extPort.name, channelNameWithIndex);
                        channelPattern.setName(channelNameWithIndex);
                        if (patternConfiguration.getChannelsMapping().containsKey(taskName)) {
                            for (String[] cm : patternConfiguration.getChannelsMapping().get(taskName)) {
                                if (cm[0].equals(extPort.name)) {
                                    cm[0] = channelNameWithIndex;
                                }
                            }
                        }
                        if (patternConfiguration.getChannelsWithSecurity().containsKey(taskName)) {
                            for (PortTaskJsonFile portTask : patternConfiguration.getChannelsWithSecurity().get(taskName)) {
                                if (portTask.getName().equals(extPort.name)) {
                                    portTask.name = channelNameWithIndex;
                                }
                            }
                        }
                        extPort.name = channelNameWithIndex;*/
                        channelsOfPatternIntoModel.put(Map.entry(taskName, extPort.getName()), channelNameWithIndex);
                        TraceManager.addDev("oldNewChannelName.containsKey(extPort.name) else  :" + extPort.getName());
                        
                    } else if (tmlmModel.getEventByName(extPort.getName()) != null) {
                        int indexEvent = 0;
                        String eventNameWithIndex = extPort.name + indexEvent;
                        while (tmlmModel.getEventByName(eventNameWithIndex) != null  || channelsOfPatternIntoModel.containsValue(eventNameWithIndex) || channelsClonedIntoModel.containsValue(eventNameWithIndex)) {
                            indexEvent += 1;
                            eventNameWithIndex= extPort.getName() + indexEvent;
                        }
                        /*TMLEvent eventPattern = tmlmPattern.getEventByName(extPort.name);
                        oldNewEventName.put(extPort.name, eventNameWithIndex);
                        eventPattern.setName(eventNameWithIndex);
                        extPort.name = eventNameWithIndex;*/
                        channelsOfPatternIntoModel.put(Map.entry(taskName, extPort.getName()), eventNameWithIndex);
                        TraceManager.addDev("else if oldNewEventName.containsKey(extPort.name)  else :" + extPort.name);
                    } else if (channelsOfPatternIntoModel.containsValue(extPort.getName()) || channelsClonedIntoModel.containsValue(extPort.getName())) {
                        int index = 0;
                        String nameWithIndex = extPort.getName() + index;
                        while (channelsOfPatternIntoModel.containsValue(nameWithIndex) || channelsClonedIntoModel.containsValue(nameWithIndex)) {
                            index += 1;
                            nameWithIndex= extPort.getName() + index;
                        }
                        channelsOfPatternIntoModel.put(Map.entry(taskName, extPort.getName()), nameWithIndex);
                    } else {
                        channelsOfPatternIntoModel.put(Map.entry(taskName, extPort.getName()), extPort.getName());
                    }
                }
            }
        }
        for (PatternConnection patternConnection : patternConfiguration.getPortsConnection()) {
            if (patternConnection.isANewChannelRequired(patternConfiguration.getPortsConnection(), patternTasks, patternConfiguration.getClonedTasks())) {
                if (tmlmModel.getChannelByName(patternConnection.getPatternChannel()) != null) {
                    int indexChannel = 0;
                    String channelNameWithIndex = patternConnection.getPatternChannel() + indexChannel;
                    while (tmlmModel.getChannelByName(channelNameWithIndex) != null || channelsOfPatternIntoModel.containsValue(channelNameWithIndex) || channelsClonedIntoModel.containsValue(channelNameWithIndex)) {
                        indexChannel += 1;
                        channelNameWithIndex= patternConnection.getPatternChannel() + indexChannel;
                    }
                    channelsOfPatternIntoModel.put(Map.entry(patternConnection.getPatternTaskName(), patternConnection.getPatternChannel()), channelNameWithIndex);
                    
                } else if (tmlmModel.getEventByName(patternConnection.getPatternChannel()) != null) {
                    int indexEvent = 0;
                    String eventNameWithIndex = patternConnection.getPatternChannel() + indexEvent;
                    while (tmlmModel.getEventByName(eventNameWithIndex) != null || channelsOfPatternIntoModel.containsValue(eventNameWithIndex) || channelsClonedIntoModel.containsValue(eventNameWithIndex)) {
                        indexEvent += 1;
                        eventNameWithIndex= patternConnection.getPatternChannel() + indexEvent;
                    }
                    channelsOfPatternIntoModel.put(Map.entry(patternConnection.getPatternTaskName(), patternConnection.getPatternChannel()), eventNameWithIndex);
                } else if (channelsOfPatternIntoModel.containsValue(patternConnection.getPatternChannel()) || channelsClonedIntoModel.containsValue(patternConnection.getPatternChannel())) {
                    int index = 0;
                    String nameWithIndex = patternConnection.getPatternChannel() + index;
                    while (channelsOfPatternIntoModel.containsValue(nameWithIndex) || channelsClonedIntoModel.containsValue(nameWithIndex)) {
                        index += 1;
                        nameWithIndex= patternConnection.getPatternChannel() + index;
                    }
                    channelsOfPatternIntoModel.put(Map.entry(patternConnection.getPatternTaskName(), patternConnection.getPatternChannel()), nameWithIndex);
                } else {
                    channelsOfPatternIntoModel.put(Map.entry(patternConnection.getPatternTaskName(), patternConnection.getPatternChannel()), patternConnection.getPatternChannel());
                }
            }
        }
    }

    public TMLMapping<?> updatePatternTasksAttributes(TMLMapping<?> _tmapPattern, LinkedHashMap<String, List<AttributeTaskJsonFile>> _updatedPatternAttributes) {
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (String taskName : _updatedPatternAttributes.keySet()) {
            TMLTask taskPattern = _tmlmPattern.getTMLTaskByName(taskName);
            if (taskPattern != null) {
                for (int i = 0; i < _updatedPatternAttributes.get(taskName).size(); i++) {
                    TMLAttribute attribTaskPattern = taskPattern.getAttributeByName(_updatedPatternAttributes.get(taskName).get(i).getName());
                    attribTaskPattern.initialValue = _updatedPatternAttributes.get(taskName).get(i).getValue();
                }
            }
        }
        return _tmapPattern;
    }

    public TMLMapping<?> addPatternTasksInModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        try {
            TMLModeling<?> _tmlmPatternClone = _tmlmPattern.deepClone();
            for (String taskName : _patternTasks.keySet()) {
                TMLTask taskPattern = _tmlmPatternClone.getTMLTaskByName(taskName);
                if (taskPattern != null) {
                    if (_tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(taskName)) == null) {
                        taskPattern.setName(tasksOfPatternIntoModel.get(taskName));
                        _tmlmModel.addTask(taskPattern);
                        TraceManager.addDev("addPatternTasksInModel: taskName=" + tasksOfPatternIntoModel.get(taskName));
                    }
                }
            }
        } catch (TMLCheckingError err) {

        }
        return _tmapModel;
    }

    public TMLMapping<?> addPatternInternalChannelsInModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        try {
            TMLModeling<?> _tmlmPatternClone = _tmlmPattern.deepClone();
            for (String taskName : _patternTasks.keySet()) {
                for (PortTaskJsonFile portTask : _patternTasks.get(taskName).internalPorts) {
                    TMLChannel channelPattern = _tmlmPatternClone.getChannelByName(portTask.getName());
                    TMLEvent eventPattern = _tmlmPatternClone.getEventByName(portTask.getName());
                    if (portTask.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                        if (channelPattern != null) {
                            if (_tmlmModel.getChannelByName(channelsOfPatternIntoModel.get(Map.entry(taskName, portTask.getName()))) == null) {
                                channelPattern.setName(channelsOfPatternIntoModel.get(Map.entry(taskName, portTask.getName())));
                                channelPattern.setTasks(_tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(channelPattern.getOriginTask().getName())), _tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(channelPattern.getDestinationTask().getName())));
                                channelPattern.setPorts(new TMLPort(channelPattern.getName(), channelPattern.getReferenceObject()), new TMLPort(channelPattern.getName(), channelPattern.getReferenceObject()));
                                _tmlmModel.addChannel(channelPattern);
                                for (TMLActivityElement elem : channelPattern.getDestinationTask().getActivityDiagram().getElements()) {
                                    if (elem instanceof TMLActivityElementChannel) {
                                        TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) elem;
                                        for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                            if (acElemChannel.getChannel(indexChannel).getName().equals(portTask.getName())) {
                                                acElemChannel.replaceChannelWith(acElemChannel.getChannel(indexChannel), channelPattern);
                                            }
                                        }
                                    }
                                }
                                for (TMLActivityElement elem : channelPattern.getOriginTask().getActivityDiagram().getElements()) {
                                    if (elem instanceof TMLActivityElementChannel) {
                                        TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) elem;
                                        for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                            if (acElemChannel.getChannel(indexChannel).getName().equals(portTask.getName())) {
                                                acElemChannel.replaceChannelWith(acElemChannel.getChannel(indexChannel), channelPattern);
                                            }
                                        }
                                    }
                                }
                                //channelsFromPatternToMap.add(channelPattern);
                            }
                        } else if (eventPattern != null) {
                            if (_tmlmModel.getEventByName(channelsOfPatternIntoModel.get(Map.entry(taskName, portTask.getName()))) == null) {
                                eventPattern.setName(channelsOfPatternIntoModel.get(Map.entry(taskName, portTask.getName())));
                                eventPattern.setTasks(_tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(eventPattern.getOriginTask().getName())), _tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(eventPattern.getDestinationTask().getName())));
                                eventPattern.setPorts(new TMLPort(eventPattern.getName(), eventPattern.getReferenceObject()), new TMLPort(eventPattern.getName(), eventPattern.getReferenceObject()));
                                _tmlmModel.addEvent(eventPattern);
                                for (TMLActivityElement elem : eventPattern.getDestinationTask().getActivityDiagram().getElements()) {
                                    if (elem instanceof TMLActivityElementEvent) {
                                        TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) elem;
                                        if (acElemEvent.getEvent().getName().equals(portTask.getName())) {
                                            acElemEvent.replaceEventWith(acElemEvent.getEvent(), eventPattern);
                                        }
                                    }
                                }
                                for (TMLActivityElement elem : eventPattern.getOriginTask().getActivityDiagram().getElements()) {
                                    if (elem instanceof TMLActivityElementEvent) {
                                        TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) elem;
                                        if (acElemEvent.getEvent().getName().equals(portTask.getName())) {
                                            acElemEvent.replaceEventWith(acElemEvent.getEvent(), eventPattern);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (TMLCheckingError err) {

        }
        return _tmapModel;
    }

    /*public TMLMapping<?> updatePatternTasksAttributes(TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
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
    }*/


    public TMLMapping<?> addNewPortToATask(TMLMapping<?> _tmapModel, LinkedHashMap<String, TaskPattern> _patternTasks, TMLTask _taskToAddPort, String _patternTaskName, String _portPatternName, String _portTolink) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TaskPattern tp = _patternTasks.get(_patternTaskName);
        TMLActivity adTaskToAddPort = _taskToAddPort.getActivityDiagram();
        List<TMLActivityElement> actElemsToAdd = new ArrayList<TMLActivityElement>();
        TMLChannel channelInModel = _tmlmModel.getChannelByName(channelsOfPatternIntoModel.get(Map.entry(_patternTaskName, _portPatternName)));
        TMLEvent eventInModel = _tmlmModel.getEventByName(channelsOfPatternIntoModel.get(Map.entry(_patternTaskName, _portPatternName)));
        for (PortTaskJsonFile pTaskJson : tp.externalPorts) {
            if (pTaskJson.name.equals(_portPatternName)) {
                if (pTaskJson.getMode().equals(PatternCreation.MODE_INPUT)) {
                    if (pTaskJson.getType().equals(PatternCreation.CHANNEL)) {
                        _taskToAddPort.addWriteTMLChannel(channelInModel); 
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(_portTolink)) {
                                        TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                        TMLWriteChannel wrChannel = new TMLWriteChannel(channelInModel.getName(), adTaskToAddPort.getReferenceObject());
                                        wrChannel.setNbOfSamples("1"); /// TO UPDATE
                                        wrChannel.addChannel(channelInModel);
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
                                    TMLWriteChannel wrChannel = new TMLWriteChannel(channelInModel.getName(), adTaskToAddPort.getReferenceObject());
                                    wrChannel.setNbOfSamples("1"); /// TO UPDATE
                                    wrChannel.addChannel(channelInModel);
                                    wrChannel.addNext(acElem);
                                    prevElem.setNewNext(acElem, wrChannel);
                                    //adTaskToAddPort.addElement(wrChannel);
                                    actElemsToAdd.add(wrChannel);
                                }
                            }
                        }
                    } else if (pTaskJson.getType().equals(PatternCreation.EVENT)) {
                        _taskToAddPort.addTMLEvent(eventInModel); 
                        eventInModel.setOriginTask(_taskToAddPort);
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (acElemEvent.getEvent().getName().equals(_portTolink)) {
                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                    TMLSendEvent sdEvent = new TMLSendEvent(eventInModel.getName(), adTaskToAddPort.getReferenceObject());
                                    sdEvent.setEvent(eventInModel);
                                    for (TMLWaitEvent we : eventInModel.getDestinationTask().getWaitEvents()) {
                                        if (we.getEvent().getName().equals(eventInModel.getName())) {
                                            for (String param : we.getVectorAllParams()) {
                                                if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                    sdEvent.addParam(param);
                                                } else {
                                                    sdEvent.addParam(param);
                                                    if (_taskToAddPort.getAttributeByName(param) == null) {
                                                        _taskToAddPort.addAttribute(eventInModel.getDestinationTask().getAttributeByName(param).deepClone(_tmlmModel));
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
                                        TMLSendEvent sdEvent = new TMLSendEvent(eventInModel.getName(), adTaskToAddPort.getReferenceObject());
                                        sdEvent.setEvent(eventInModel);
                                        for (TMLWaitEvent we : eventInModel.getDestinationTask().getWaitEvents()) {
                                            if (we.getEvent().getName().equals(eventInModel.getName())) {
                                                for (String param : we.getVectorAllParams()) {
                                                    if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                        sdEvent.addParam(param);
                                                    } else {
                                                        sdEvent.addParam(param);
                                                        if (_taskToAddPort.getAttributeByName(param) == null) {
                                                            _taskToAddPort.addAttribute(eventInModel.getDestinationTask().getAttributeByName(param).deepClone(_tmlmModel));
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
                } else if (pTaskJson.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                    if (pTaskJson.getType().equals(PatternCreation.CHANNEL)) {
                        _taskToAddPort.addReadTMLChannel(channelInModel);
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                                for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                                    if (acElemChannel.getChannel(indexChannel).getName().equals(_portTolink)) {
                                        TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                        TMLReadChannel rdChannel = new TMLReadChannel(channelInModel.getName(), adTaskToAddPort.getReferenceObject());
                                        rdChannel.setNbOfSamples("1"); /// TO UPDATE
                                        rdChannel.addChannel(channelInModel);
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
                                    TMLReadChannel rdChannel = new TMLReadChannel(channelInModel.getName(), adTaskToAddPort.getReferenceObject());
                                    rdChannel.setNbOfSamples("1"); /// TO UPDATE
                                    rdChannel.addChannel(channelInModel);
                                    rdChannel.addNext(acElem);
                                    prevElem.setNewNext(acElem, rdChannel);
                                    //adTaskToAddPort.addElement(rdChannel);
                                    actElemsToAdd.add(rdChannel);
                                }
                            }
                        }
                    } else if (pTaskJson.getType().equals(PatternCreation.EVENT)) {
                        _taskToAddPort.addTMLEvent(eventInModel);
                        eventInModel.setDestinationTask(_taskToAddPort);
                        for (TMLActivityElement acElem : adTaskToAddPort.getElements()) {
                            if (acElem instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                                if (acElemEvent.getEvent().getName().equals(_portTolink)) {
                                    TMLActivityElement prevElem =  adTaskToAddPort.getPrevious(acElem);
                                    TMLWaitEvent wtEvent = new TMLWaitEvent(eventInModel.getName(), adTaskToAddPort.getReferenceObject());
                                    wtEvent.setEvent(eventInModel);
                                    for (TMLSendEvent sd : eventInModel.getOriginTask().getSendEvents()) {
                                        if (sd.getEvent().getName().equals(eventInModel.getName())) {
                                            for (int indexParam=0; indexParam < sd.getNbOfParams(); indexParam++) {
                                                String param = sd.getParam(indexParam);
                                                if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                    String paramName = eventInModel.getName()+indexParam;
                                                    if (_taskToAddPort.getAttributeByName(paramName) == null) {
                                                        TMLAttribute attribParam = new TMLAttribute(paramName, eventInModel.getParams().get(indexParam).deepClone(_tmlmModel), "");
                                                        _taskToAddPort.addAttribute(attribParam);
                                                    }
                                                    wtEvent.addParam(paramName);
                                                } else {
                                                    wtEvent.addParam(param);
                                                    if (_taskToAddPort.getAttributeByName(param) == null) {
                                                        _taskToAddPort.addAttribute(eventInModel.getOriginTask().getAttributeByName(param).deepClone(_tmlmModel));
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
                                        TMLWaitEvent wtEvent = new TMLWaitEvent(eventInModel.getName(), adTaskToAddPort.getReferenceObject());
                                        wtEvent.setEvent(eventInModel);
                                        for (TMLSendEvent sd : eventInModel.getOriginTask().getSendEvents()) {
                                            if (sd.getEvent().getName().equals(eventInModel.getName())) {
                                                for (int indexParam=0; indexParam < sd.getNbOfParams(); indexParam++) {
                                                    String param = sd.getParam(indexParam);
                                                    if (param.matches("-?\\d+") || param.matches("(?i)^(true|false)")) {
                                                        String paramName = eventInModel.getName()+indexParam;
                                                        if (_taskToAddPort.getAttributeByName(paramName) == null) {
                                                            TMLAttribute attribParam = new TMLAttribute(paramName, eventInModel.getParams().get(indexParam).deepClone(_tmlmModel), "");
                                                            _taskToAddPort.addAttribute(attribParam);
                                                        }
                                                        wtEvent.addParam(paramName);
                                                    } else {
                                                        wtEvent.addParam(param);
                                                        if (_taskToAddPort.getAttributeByName(param) == null) {
                                                            _taskToAddPort.addAttribute(eventInModel.getOriginTask().getAttributeByName(param).deepClone(_tmlmModel));
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

    /*public TMLMapping<?> addNewPortToTasks(TMLMapping<?> _tmapModel, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (String patternTaskName : _portsConnection.keySet()) {
            for (String[] elemP : _portsConnection.get(patternTaskName)) {
                TraceManager.addDev("elemP[0]= " + elemP[0]);
                TraceManager.addDev("elemP[1]= " + elemP[1]);
                TraceManager.addDev("elemP[2]= " + elemP[2]);
                
                if (elemP.length == 4) {
                    TMLTask taskToAddPort = _tmlmModel.getTMLTaskByName(elemP[1]);
                    TaskPattern tp = _patternTasks.get(patternTaskName);
                    TMLActivity adTaskToAddPort = taskToAddPort.getActivityDiagram();
                    for (PortTaskJsonFile pTaskJson : tp.externalPorts) {
                        if (pTaskJson.name.equals(elemP[0])) {
                            if (pTaskJson.getMode().equals(PatternCreation.MODE_INPUT)) {
                                if (pTaskJson.getType().equals(PatternCreation.CHANNEL)) {
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
                                } else if (pTaskJson.getType().equals(PatternCreation.EVENT)) {
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
                            } else if (pTaskJson.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                                if (pTaskJson.getType().equals(PatternCreation.CHANNEL)) {
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
                                } else if (pTaskJson.getType().equals(PatternCreation.EVENT)) {
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
    }*/

    public TMLMapping<?> makeConnectionBetweenPatternAndModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, List<PatternConnection> _portsConnection, LinkedHashMap<String, TaskPattern> _patternTasks, List<PatternCloneTask> _patternCloneTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (PatternConnection portConnection : _portsConnection) {
            String patternTaskName = portConnection.getPatternTaskName();
            String patternTaskPortName = portConnection.getPatternChannel();
            String modelTaskName = portConnection.getModelTaskName();
            String modelTaskPortName = portConnection.getModelChannelName();

            PortTaskJsonFile relatedPortInPattern = null;

            for (PortTaskJsonFile portTaskJsonFile : _patternTasks.get(patternTaskName).getExternalPorts()) {
                if (portTaskJsonFile.getName().equals(patternTaskPortName)) {
                    relatedPortInPattern = portTaskJsonFile;
                    break;
                }
            }

            TMLTask patternTaskInModel = _tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(patternTaskName));

            if (tasksClonedIntoModel.containsKey(modelTaskName)) {
                modelTaskName = tasksClonedIntoModel.get(modelTaskName);
                if (!portConnection.isNewPort()) {
                    modelTaskPortName = channelsClonedIntoModel.get(Map.entry(modelTaskName, modelTaskPortName));
                }
            }
            TMLTask modelTask = _tmlmModel.getTMLTaskByName(modelTaskName);
            
            TMLChannel chInModel = _tmlmModel.getChannelByName(modelTaskPortName);
            TMLEvent evtInModel = _tmlmModel.getEventByName(modelTaskPortName);

            TMLChannel chInPattern = _tmlmPattern.getChannelByName(patternTaskPortName);
            TMLEvent evtInPattern = _tmlmPattern.getEventByName(patternTaskPortName);

            TraceManager.addDev("patternTaskName =  " + patternTaskName);
            TraceManager.addDev("patternTaskPortName =  " + patternTaskPortName);
            TraceManager.addDev("modelTaskName =  " + modelTaskName);
            TraceManager.addDev("modelTaskPortName =  " + modelTaskPortName);
            if (portConnection.isANewChannelRequired(_portsConnection, patternTasks, _patternCloneTasks)) {
                if (chInPattern != null) {
                    TraceManager.addDev("New channel !");
                    try {
                        TMLChannel chToAdd = chInPattern.deepClone(_tmlmPattern);
                        chToAdd.setName(channelsOfPatternIntoModel.get(Map.entry(patternTaskName, patternTaskPortName)));
                        if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_INPUT)) {
                            chToAdd.setDestinationTask(patternTaskInModel);
                            chToAdd.setOriginTask(modelTask);
                        } else if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                            chToAdd.setDestinationTask(modelTask);
                            chToAdd.setOriginTask(patternTaskInModel);
                        }
                        chToAdd.setPorts(new TMLPort(chToAdd.getName(), chToAdd.getReferenceObject()), new TMLPort(chToAdd.getName(), chToAdd.getReferenceObject()));
                        _tmlmModel.addChannel(chToAdd);
                        
                        for (TMLActivityElement ae : patternTaskInModel.getActivityDiagram().getElements()) {
                            if (ae instanceof TMLActivityElementChannel) {
                                TMLActivityElementChannel aeCh = (TMLActivityElementChannel) ae;
                                if (aeCh.getChannel(0).getName().equals(chInPattern.getName())) {
                                    aeCh.replaceChannelWith(aeCh.getChannel(0), chToAdd);
                                    TraceManager.addDev("Pattern Task : Replace channel : " + chInPattern.getName() + " with : " + chToAdd.getName());
                                }
                            }
                        }
                        if (portConnection.isNewPort()) {
                            String modelTaskNameForNewPort = modelTaskPortName;
                            TraceManager.addDev("Before : modelTaskNameForNewPort=" + modelTaskNameForNewPort);
                            if (renamedModelChannels.containsKey(Map.entry(portConnection.getModelTaskName(), portConnection.getModelChannelName()))) {
                                modelTaskNameForNewPort = renamedModelChannels.get(Map.entry(portConnection.getModelTaskName(), portConnection.getModelChannelName()));
                            }
                            TraceManager.addDev("After : modelTaskNameForNewPort=" + modelTaskNameForNewPort);
                            _tmapModel = addNewPortToATask(_tmapModel, _patternTasks, modelTask, patternTaskName, patternTaskPortName, modelTaskNameForNewPort);
                        } else {
                            for (TMLActivityElement ae : modelTask.getActivityDiagram().getElements()) {
                                if (ae instanceof TMLActivityElementChannel) {
                                    TMLActivityElementChannel aeCh = (TMLActivityElementChannel) ae;
                                    if (aeCh.getChannel(0).getName().equals(chInModel.getName())) {
                                        aeCh.replaceChannelWith(aeCh.getChannel(0), chToAdd);
                                        TraceManager.addDev("model Task : Replace channel : " + chInModel.getName() + " with : " + chToAdd.getName());
                                        renamedModelChannels.put(Map.entry(portConnection.getModelTaskName(), portConnection.getModelChannelName()), chToAdd.getName());
                                    }
                                }
                            }
                        }
                    } catch (TMLCheckingError err) {
                    }
                } else if (evtInPattern != null) {
                    TraceManager.addDev("New event !");
                    try {
                        TMLEvent evtToAdd = evtInPattern.deepClone(_tmlmPattern);
                        evtToAdd.setName(channelsOfPatternIntoModel.get(Map.entry(patternTaskName, patternTaskPortName)));
                        if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_INPUT)) {
                            evtToAdd.setDestinationTask(patternTaskInModel);
                            evtToAdd.setOriginTask(modelTask);
                        } else if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                            evtToAdd.setDestinationTask(modelTask);
                            evtToAdd.setOriginTask(patternTaskInModel);
                        }
                        evtToAdd.setPorts(new TMLPort(evtToAdd.getName(), evtToAdd.getReferenceObject()), new TMLPort(evtToAdd.getName(), evtToAdd.getReferenceObject()));
                        _tmlmModel.addEvent(evtToAdd);
                        
                        for (TMLActivityElement ae : patternTaskInModel.getActivityDiagram().getElements()) {
                            if (ae instanceof TMLActivityElementEvent) {
                                TMLActivityElementEvent aeEvt = (TMLActivityElementEvent) ae;
                                if (aeEvt.getEvent().getName().equals(evtInPattern.getName())) {
                                    aeEvt.replaceEventWith(aeEvt.getEvent(), evtToAdd);
                                    TraceManager.addDev("Pattern Task : Replace event : " + evtInPattern.getName() + " with : " + evtToAdd.getName());
                                }
                            }
                        }
                        if (portConnection.isNewPort()) {
                            String modelTaskNameForNewPort = modelTaskPortName;
                            if (renamedModelChannels.containsKey(Map.entry(portConnection.getModelTaskName(), portConnection.getModelChannelName()))) {
                                modelTaskNameForNewPort = renamedModelChannels.get(Map.entry(portConnection.getModelTaskName(), portConnection.getModelChannelName()));
                            }
                            _tmapModel = addNewPortToATask(_tmapModel, _patternTasks, modelTask, patternTaskName, patternTaskPortName, modelTaskNameForNewPort);
                        } else {
                            for (TMLActivityElement ae : modelTask.getActivityDiagram().getElements()) {
                                if (ae instanceof TMLActivityElementEvent) {
                                    TMLActivityElementEvent aeEvt = (TMLActivityElementEvent) ae;
                                    if (aeEvt.getEvent().getName().equals(evtInModel.getName())) {
                                        aeEvt.replaceEventWith(aeEvt.getEvent(), evtToAdd);
                                        TraceManager.addDev("model Task : Replace event : " + evtInModel.getName() + " with : " + evtToAdd.getName());
                                        renamedModelChannels.put(Map.entry(portConnection.getModelTaskName(), portConnection.getModelChannelName()), evtToAdd.getName());
                                    }
                                }
                            }
                        }
                    } catch (TMLCheckingError err) {
                    }
                }
            } else {
                if (chInModel != null) {
                    TraceManager.addDev("Existant channel !");
                    if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_INPUT)) {
                        chInModel.setDestinationTask(patternTaskInModel);
                        chInModel.setPorts(chInModel.getOriginPort(), new TMLPort(chInModel.getName(), chInModel.getReferenceObject()));
                    } else if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                        chInModel.setOriginTask(patternTaskInModel);
                        chInModel.setPorts(new TMLPort(chInModel.getName(), chInModel.getReferenceObject()), chInModel.getDestinationPort());
                    }
                    
                    for (TMLActivityElement ae : patternTaskInModel.getActivityDiagram().getElements()) {
                        if (ae instanceof TMLActivityElementChannel) {
                            TMLActivityElementChannel aeCh = (TMLActivityElementChannel) ae;
                            if (aeCh.getChannel(0).getName().equals(chInPattern.getName())) {
                                aeCh.replaceChannelWith(aeCh.getChannel(0), chInModel);
                                TraceManager.addDev("Replace channel : " + chInPattern.getName() + " with : " + chInModel.getName());
                            }
                        }
                    }
                }
                if (evtInModel != null) {
                    TraceManager.addDev("Existant event !");
                    if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_INPUT)) {
                        evtInModel.setDestinationTask(patternTaskInModel);
                        evtInModel.setPorts(evtInModel.getOriginPort(), new TMLPort(evtInModel.getName(), evtInModel.getReferenceObject()));
                    } else if (relatedPortInPattern.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                        evtInModel.setOriginTask(patternTaskInModel);
                        evtInModel.setPorts(new TMLPort(evtInModel.getName(), evtInModel.getReferenceObject()), evtInModel.getDestinationPort());
                    }
                    
                    for (TMLActivityElement ae : patternTaskInModel.getActivityDiagram().getElements()) {
                        if (ae instanceof TMLActivityElementEvent) {
                            TMLActivityElementEvent aeEvt = (TMLActivityElementEvent) ae;
                            if (aeEvt.getEvent().getName().equals(evtInPattern.getName())) {
                                aeEvt.replaceEventWith(aeEvt.getEvent(), evtInModel);
                                TraceManager.addDev("Replace event : " + evtInPattern.getName() + " with : " + evtInModel.getName());
                            }
                        }
                    }
                } 
            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> configDisconnectedChannels(TMLMapping<?> _tmapModel, List<PatternPortsConfig> _portsConfig) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TraceManager.addDev("configDisconnectedChannels");
        for (PatternPortsConfig portConfig : _portsConfig) {
            TMLTask task = _tmlmModel.getTMLTaskByName(portConfig.getTaskOfChannelToConfig());
            //String portName = portDecision.getKey();
            //String decision = portDecision.getValue();
            
            if (task != null) {
                TMLActivity adTask = task.getActivityDiagram();
                TMLActivityElement elemMerge = null;
                String mergeWith = portConfig.getMergeWith();
                if (mergeWith != null) {
                    if (renamedModelChannels.containsKey(Map.entry(portConfig.getTaskOfChannelToConfig(), mergeWith))) {
                        mergeWith = renamedModelChannels.get(Map.entry(portConfig.getTaskOfChannelToConfig(), mergeWith));
                    }
                    for (TMLActivityElement acElem : adTask.getElements()) {
                        if (acElem instanceof TMLActivityElementChannel) {
                            TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                            for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) {
                                if (acElemChannel.getChannel(indexChannel).getName().equals(mergeWith)) {
                                    try {
                                        elemMerge = acElem.deepClone(_tmlmModel);
                                    } catch (TMLCheckingError err) {

                                    }
                                    break;
                                }
                            }
                        } else if (acElem instanceof TMLActivityElementEvent) {
                            TMLActivityElementEvent acElemEvent = (TMLActivityElementEvent) acElem;
                            if (acElemEvent.getEvent().getName().equals(mergeWith)) {
                                try {
                                    elemMerge = acElem.deepClone(_tmlmModel);
                                } catch (TMLCheckingError err) {

                                }
                                break;
                            }
                        }
                    }   
                }

                List<TMLActivityElement> elemsToRemove = new ArrayList<TMLActivityElement>();
                for (TMLActivityElement acElem : adTask.getElements()) {
                    if (acElem instanceof TMLActivityElementChannel) {
                        TMLActivityElementChannel acElemChannel = (TMLActivityElementChannel) acElem;
                        for (int indexChannel = 0 ; indexChannel < acElemChannel.getNbOfChannels() ; indexChannel++) { 
                            if (acElemChannel.getChannel(indexChannel).getName().equals(portConfig.getChannelToConfig())) {
                                TMLActivityElement prevElem =  adTask.getPrevious(acElem);
                                TMLActivityElement nextElem =  acElem.getNextElement(0);
                                if (elemMerge == null) {
                                    while (nextElem instanceof TMLActivityElementChannel && ((TMLActivityElementChannel)nextElem).getChannel(0).getName().equals(portConfig.getChannelToConfig())) {
                                        elemsToRemove.add(nextElem);
                                        nextElem =  nextElem.getNextElement(0);
                                    }
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
                        if (acElemEvent.getEvent().getName().equals(portConfig.getChannelToConfig())) {
                            TMLActivityElement prevElem =  adTask.getPrevious(acElem);
                            TMLActivityElement nextElem =  acElem.getNextElement(0);
                            if (elemMerge == null) {
                                while (nextElem instanceof TMLActivityElementEvent && ((TMLActivityElementEvent)nextElem).getEvent().getName().equals(portConfig.getChannelToConfig())) {
                                    elemsToRemove.add(nextElem);
                                    nextElem =  nextElem.getNextElement(0);
                                }
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
                }
            } 

        }
        return _tmapModel;
    }

    public TMLMapping<?> mapTasksInArch(TMLMapping<?> _tmapModel, List<MappingPatternTask> _tasksMapping) {
        TMLArchitecture _tmlarchModel =  _tmapModel.getArch();
        for (MappingPatternTask taskMapping : _tasksMapping) {
            if (taskMapping.getOrigin() == MappingPatternTask.ORIGIN_CLONE) {
                TMLTask taskCloneToMap = _tmapModel.getTaskByName(tasksClonedIntoModel.get(taskMapping.getTaskToMapName()));
                if (taskCloneToMap != null) {
                    if (taskMapping.getSameHwAs() != null) {
                        String sameTaskName = taskMapping.getSameHwAs();
                        int sameTaskOrigin = taskMapping.getSameHwAsOrigin();
                        TMLTask inSameTask = null;
                        if (sameTaskOrigin == MappingPatternTask.ORIGIN_MODEL) {
                            inSameTask = _tmapModel.getTaskByName(sameTaskName);
                        } else if (sameTaskOrigin == MappingPatternTask.ORIGIN_CLONE) {
                            inSameTask = _tmapModel.getTaskByName(tasksClonedIntoModel.get(sameTaskName));
                        } else if (sameTaskOrigin == MappingPatternTask.ORIGIN_PATTERN) {
                            inSameTask = _tmapModel.getTaskByName(tasksOfPatternIntoModel.get(sameTaskName));
                        }
                        if (inSameTask != null) {
                            HwExecutionNode hwMapTask = _tmapModel.getHwNodeOf(inSameTask);
                            if (hwMapTask != null) {
                                _tmapModel.addTaskToHwExecutionNode(taskCloneToMap, hwMapTask);
                            }
                        }
                    } else if (taskMapping.getBusNameForNewHw() != null) {
                        HwBus bus = _tmlarchModel.getHwBusByName(taskMapping.getBusNameForNewHw());
                        if (bus != null) {
                            String nameCPU = "CPU_" + tasksClonedIntoModel.get(taskMapping.getTaskToMapName());
                            HwNode node = _tmlarchModel.getHwNodeByName(nameCPU);
                            if (node != null) {
                                int indexCPU = 0;
                                String nameCPUWithIndex = nameCPU + indexCPU;
                                while(_tmlarchModel.getHwNodeByName(nameCPUWithIndex) != null) {
                                    indexCPU += 1;
                                    nameCPUWithIndex = nameCPU + indexCPU;
                                }
                                nameCPU = nameCPUWithIndex;
                            }
                            HwCPU _newCpu = new HwCPU(nameCPU);
                            _tmapModel.addTaskToHwExecutionNode(taskCloneToMap, _newCpu);
                            _tmlarchModel.addHwNode(_newCpu);
                            HwLink linkNewCPUWithBus = new HwLink("link_" + _newCpu.getName() + "_to_" + bus.getName());
                            linkNewCPUWithBus.bus = bus;
                            linkNewCPUWithBus.hwnode = _newCpu;
                            _tmlarchModel.addHwLink(linkNewCPUWithBus);
                        }
                    }
                }
            } else if (taskMapping.getOrigin() == MappingPatternTask.ORIGIN_PATTERN) {
                TMLTask taskPatternToMap = _tmapModel.getTaskByName(tasksOfPatternIntoModel.get(taskMapping.getTaskToMapName()));
                if (taskPatternToMap != null) {
                    if (taskMapping.getSameHwAs() != null) {
                        String sameTaskName = taskMapping.getSameHwAs();
                        int sameTaskOrigin = taskMapping.getSameHwAsOrigin();
                        TMLTask inSameTask = null;
                        if (sameTaskOrigin == MappingPatternTask.ORIGIN_MODEL) {
                            inSameTask = _tmapModel.getTaskByName(sameTaskName);
                        } else if (sameTaskOrigin == MappingPatternTask.ORIGIN_CLONE) {
                            inSameTask = _tmapModel.getTaskByName(tasksClonedIntoModel.get(sameTaskName));
                        } else if (sameTaskOrigin == MappingPatternTask.ORIGIN_PATTERN) {
                            inSameTask = _tmapModel.getTaskByName(tasksOfPatternIntoModel.get(sameTaskName));
                        }
                        if (inSameTask != null) {
                            HwExecutionNode hwMapTask = _tmapModel.getHwNodeOf(inSameTask);
                            if (hwMapTask != null) {
                                _tmapModel.addTaskToHwExecutionNode(taskPatternToMap, hwMapTask);
                            }
                        }
                    } else if (taskMapping.getBusNameForNewHw() != null) {
                        HwBus bus = _tmlarchModel.getHwBusByName(taskMapping.getBusNameForNewHw());
                        if (bus != null) {
                            String nameCPU = "CPU_" + tasksOfPatternIntoModel.get(taskMapping.getTaskToMapName());
                            HwNode node = _tmlarchModel.getHwNodeByName(nameCPU);
                            if (node != null) {
                                int indexCPU = 0;
                                String nameCPUWithIndex = nameCPU + indexCPU;
                                while(_tmlarchModel.getHwNodeByName(nameCPUWithIndex) != null) {
                                    indexCPU += 1;
                                    nameCPUWithIndex = nameCPU + indexCPU;
                                }
                                nameCPU = nameCPUWithIndex;
                            }
                            HwCPU _newCpu = new HwCPU(nameCPU);
                            _tmapModel.addTaskToHwExecutionNode(taskPatternToMap, _newCpu);
                            _tmlarchModel.addHwNode(_newCpu);
                            HwLink linkNewCPUWithBus = new HwLink("link_" + _newCpu.getName() + "_to_" + bus.getName());
                            linkNewCPUWithBus.bus = bus;
                            linkNewCPUWithBus.hwnode = _newCpu;
                            _tmlarchModel.addHwLink(linkNewCPUWithBus);
                        }
                    }
                }
            }
            
            
            
        }
        return _tmapModel;
    }

    public TMLMapping<?> mapTasksInArchAuto(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, List<MappingPatternTask> _mappingPatternTasks, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = _tmapPattern.getTMLModeling();
        Map<TMLTask, TMLTask> correspTasks = correspondenceForTasks(_tmapModel, _tmapPattern, _patternConfiguration, _patternTasks);
        Set<String> mappedTasks = new HashSet<String>();
        for (MappingPatternTask mappingPatternTask : _mappingPatternTasks) {
            mappedTasks.add(mappingPatternTask.getTaskToMapName());
        }
        Map<TMLTask, TMLTask> correspondenceForClonedTasks = correspondenceForClonedTasks(_tmapModel, _tmapPattern, mappedTasks, _patternConfiguration, _patternTasks);
        for (TMLTask correspTask : correspTasks.keySet()) {
            TraceManager.addDev("correspTask: In pattern :" + correspTask.getName() + " In model : " + correspTasks.get(correspTask).getName());
        }
        for (TMLTask correspTask : correspondenceForClonedTasks.keySet()) {
            TraceManager.addDev("correspondenceForClonedTasks: In Model :" + correspTask.getName() + " In pattern : " + correspondenceForClonedTasks.get(correspTask).getName());
        } 
        LinkedHashMap<String, TaskPattern> _patternTasksSorted = new LinkedHashMap<String, TaskPattern>();
        LinkedHashMap<String, TaskPattern> _patternTasksInEnd = new LinkedHashMap<String, TaskPattern>();
        for (String taskName : _patternTasks.keySet()) {
            if (_patternTasks.get(taskName).getExternalPorts().size() == 0) {
                _patternTasksInEnd.put(taskName, _patternTasks.get(taskName));
            } else {
                _patternTasksSorted.put(taskName, _patternTasks.get(taskName));
            }
        }
        _patternTasksSorted.putAll(_patternTasksInEnd);
        for (String taskName : _patternTasksSorted.keySet()) {
            TMLTask task = tmlmPattern.getTMLTaskByName(taskName);
            boolean taskIsMapped = false;
            if (task != null && !mappedTasks.contains(taskName)) {
                HwExecutionNode hwMapTask = _tmapPattern.getHwNodeOf(task);
                HashSet<TMLTask> tasksInThisCPU = _tmapPattern.getLisMappedTasks(hwMapTask);
                for (TMLTask taskInCPU : tasksInThisCPU) {
                    if (correspTasks.containsKey(taskInCPU)) {
                        TMLTask taskInModel = correspTasks.get(taskInCPU);
                        HwExecutionNode hwMapTaskInModel = _tmapModel.getHwNodeOf(taskInModel);
                        if (hwMapTaskInModel != null) {
                            TMLTask taskToMap = tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(taskName));
                            _tmapModel.addTaskToHwExecutionNode(taskToMap, hwMapTaskInModel);
                            taskIsMapped = true;
                            break;
                        }
                    }
                }
                if (!taskIsMapped) {
                    String nameNewCPU = hwMapTask.getName();
                    HwExecutionNode newCPU = _tmapModel.getHwExecutionNodeByName(nameNewCPU);
                    int ind = 0;
                    while (newCPU != null) {
                        nameNewCPU = hwMapTask.getName() + ind;
                        newCPU = _tmapModel.getHwExecutionNodeByName(nameNewCPU);
                        ind += 1;
                    }
                    HwCPU _newCpu = new HwCPU(nameNewCPU);
                    TMLTask taskToMap = tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(taskName));
                    _tmapModel.addTaskToHwExecutionNode(taskToMap, _newCpu);
                    _tmapModel.getArch().addHwNode(_newCpu);
                    HwBus bus = null;
                    for (TMLChannel ch : tmlmModel.getChannelsFromMe(taskToMap)) {
                        if (bus == null) {
                            TMLTask taskInConn = ch.getDestinationTask();
                            HwExecutionNode hwOfTaskInConn = _tmapModel.getHwNodeOf(taskInConn);
                            if (hwOfTaskInConn != null) {
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.hwnode == hwOfTaskInConn) {
                                        bus = link.bus;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    for (TMLChannel ch : tmlmModel.getChannelsToMe(taskToMap)) {
                        if (bus == null) {
                            TMLTask taskInConn = ch.getOriginTask();
                            HwExecutionNode hwOfTaskInConn = _tmapModel.getHwNodeOf(taskInConn);
                            if (hwOfTaskInConn != null) {
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.hwnode == hwOfTaskInConn) {
                                        bus = link.bus;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    if (bus == null) {
                        int nbConnections = 0 ; 
                        for (HwNode busModel : _tmapModel.getArch().getBUSs()) {
                            if (busModel instanceof HwBus) {
                                int curNbConnections = 0 ;
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.bus == busModel) {
                                        nbConnections += 1;
                                    }
                                }
                                if (curNbConnections > nbConnections) {
                                    bus = (HwBus) busModel;
                                    nbConnections = curNbConnections;
                                }
                            }
                        }
                    }
                    
                    HwLink linkNewCPUWithBus = new HwLink("link_" + _newCpu.getName() + "_to_" + bus.getName());
                    linkNewCPUWithBus.bus = bus;
                    linkNewCPUWithBus.hwnode = _newCpu;
                    _tmapModel.getArch().addHwLink(linkNewCPUWithBus);
                }
            }
        }

        for (PatternCloneTask patternCloneTask : _patternConfiguration.getClonedTasks()) {
            String taskName = tasksClonedIntoModel.get(patternCloneTask.getClonedTask());
            TMLTask task = tmlmModel.getTMLTaskByName(taskName);
            boolean taskIsMapped = false;
            if (task != null && !mappedTasks.contains(patternCloneTask.getClonedTask())) {
                TMLTask taskInPattern = correspondenceForClonedTasks.get(task);

                HwExecutionNode hwMapTask = _tmapPattern.getHwNodeOf(taskInPattern);
                HashSet<TMLTask> tasksInThisCPU = _tmapPattern.getLisMappedTasks(hwMapTask);
                for (TMLTask taskInCPU : tasksInThisCPU) {
                    if (correspTasks.containsKey(taskInCPU)) {
                        TMLTask taskInModel = correspTasks.get(taskInCPU);
                        HwExecutionNode hwMapTaskInModel = _tmapModel.getHwNodeOf(taskInModel);
                        if (hwMapTaskInModel != null) {
                            _tmapModel.addTaskToHwExecutionNode(task, hwMapTaskInModel);
                            taskIsMapped = true;
                            break;
                        }
                    }
                }
                if (!taskIsMapped) {
                    String nameNewCPU = hwMapTask.getName();
                    HwExecutionNode newCPU = _tmapModel.getHwExecutionNodeByName(nameNewCPU);
                    int ind = 0;
                    while (newCPU != null) {
                        nameNewCPU = hwMapTask.getName() + ind;
                        newCPU = _tmapModel.getHwExecutionNodeByName(nameNewCPU);
                        ind += 1;
                    }
                    HwCPU _newCpu = new HwCPU(nameNewCPU);
                    TMLTask taskToMap = tmlmModel.getTMLTaskByName(taskName);
                    _tmapModel.addTaskToHwExecutionNode(taskToMap, _newCpu);
                    _tmapModel.getArch().addHwNode(_newCpu);

                    HwBus bus = null;
                    for (TMLChannel ch : tmlmModel.getChannelsFromMe(taskToMap)) {
                        if (bus == null) {
                            TMLTask taskInConn = ch.getDestinationTask();
                            HwExecutionNode hwOfTaskInConn = _tmapModel.getHwNodeOf(taskInConn);
                            if (hwOfTaskInConn != null) {
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.hwnode == hwOfTaskInConn) {
                                        bus = link.bus;
                                        break;
                                    }
                                }
                            }
                        }
                        
                    }
                    for (TMLChannel ch : tmlmModel.getChannelsToMe(taskToMap)) {
                        if (bus == null) {
                            TMLTask taskInConn = ch.getOriginTask();
                            HwExecutionNode hwOfTaskInConn = _tmapModel.getHwNodeOf(taskInConn);
                            if (hwOfTaskInConn != null) {
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.hwnode == hwOfTaskInConn) {
                                        bus = link.bus;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (bus == null) {
                        int nbConnections = 0 ; 
                        for (HwNode busModel : _tmapModel.getArch().getBUSs()) {
                            if (busModel instanceof HwBus) {
                                int curNbConnections = 0 ;
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.bus == busModel) {
                                        nbConnections += 1;
                                    }
                                }
                                if (curNbConnections > nbConnections) {
                                    bus = (HwBus) busModel;
                                    nbConnections = curNbConnections;
                                }
                            }
                        }
                    }
                    HwLink linkNewCPUWithBus = new HwLink("link_" + _newCpu.getName() + "_to_" + bus.getName());
                    linkNewCPUWithBus.bus = bus;
                    linkNewCPUWithBus.hwnode = _newCpu;
                    _tmapModel.getArch().addHwLink(linkNewCPUWithBus);
                }
            }
        }
        return _tmapModel;
    }

    // Find correspondence between Tasks of the pattern model and the cloned tasks of current Model 
    public Map<TMLTask, TMLTask> correspondenceForClonedTasks(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, Set<String> mappedTasks, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = _tmapPattern.getTMLModeling();
        List <String> clonedTasksToMap = new ArrayList<String>();
        // List <TMLTask> patternTasksToMap = new ArrayList<TMLTask>();
        for (PatternCloneTask patternCloneTask : _patternConfiguration.getClonedTasks()) {
            String taskName = patternCloneTask.getClonedTask();
            TMLTask task = tmlmModel.getTMLTaskByName(tasksClonedIntoModel.get(taskName));
            if (task != null && !mappedTasks.contains(taskName)) {
                clonedTasksToMap.add(task.getName());
            }
        }
        /* for (String taskName : _patternTasks.keySet()) {
            TMLTask task = tmlmModel.getTMLTaskByName(taskName);
            if (task != null && !mappedTasks.contains(taskName)) {
                patternTasksToMap.add(task);
            }
        } */
        Map<TMLTask, TMLTask> correspTasks = new HashMap<TMLTask, TMLTask>();
        for (PatternConnection patternConnection : _patternConfiguration.getPortsConnection()) {
            String taskPatternName = patternConnection.getPatternTaskName();
            if (clonedTasksToMap.contains(tasksClonedIntoModel.get(patternConnection.getModelTaskName()))) {
                String modeChInPattern = "";
                String typeChInPattern = "";
                for (PortTaskJsonFile pt : _patternTasks.get(taskPatternName).getExternalPorts()) {
                    if (pt.getName().equals(patternConnection.getPatternChannel())) {
                        modeChInPattern = pt.getMode();
                        typeChInPattern = pt.getType();
                        break;
                    }
                }
                if (typeChInPattern.equals(PatternCreation.CHANNEL)) {
                    TMLChannel chInPattern = tmlmPattern.getChannelByName(patternConnection.getPatternChannel());
                    if (chInPattern != null) {
                        if (modeChInPattern.equals(PatternCreation.MODE_INPUT)) {
                            correspTasks.put(tmlmModel.getTMLTaskByName(tasksClonedIntoModel.get(patternConnection.getModelTaskName())), chInPattern.getOriginTask());
                        } else if (modeChInPattern.equals(PatternCreation.MODE_OUTPUT)) {
                            correspTasks.put(tmlmModel.getTMLTaskByName(tasksClonedIntoModel.get(patternConnection.getModelTaskName())), chInPattern.getDestinationTask());
                        }
                        
                    }
                } else if (typeChInPattern.equals(PatternCreation.EVENT)) {
                    TMLEvent evtInPattern = tmlmPattern.getEventByName(patternConnection.getPatternChannel());
                    if (evtInPattern != null) {
                        if (modeChInPattern.equals(PatternCreation.MODE_INPUT)) {
                            correspTasks.put(tmlmModel.getTMLTaskByName(tasksClonedIntoModel.get(patternConnection.getModelTaskName())), evtInPattern.getOriginTask());
                        } else if (modeChInPattern.equals(PatternCreation.MODE_OUTPUT)) {
                            correspTasks.put(tmlmModel.getTMLTaskByName(tasksClonedIntoModel.get(patternConnection.getModelTaskName())), evtInPattern.getDestinationTask());
                        }
                    }
                }
            }
        }
        return correspTasks;
    }

    // Find correspondence between Tasks of the pattern model and the tasks of current Model 
    public Map<TMLTask, TMLTask> correspondenceForTasks(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = _tmapPattern.getTMLModeling();
        Map<TMLTask, TMLTask> correspTasks = new HashMap<TMLTask, TMLTask>();
        for (PatternConnection patternConnection : _patternConfiguration.getPortsConnection()) {
            String taskPatternName = patternConnection.getPatternTaskName();
            String modelTask = patternConnection.getModelTaskName();
            if (tasksClonedIntoModel.containsKey(modelTask)) {
                modelTask = tasksClonedIntoModel.get(modelTask);
            }
            String modeChInPattern = "";
            String typeChInPattern = "";
            for (PortTaskJsonFile pt : _patternTasks.get(taskPatternName).getExternalPorts()) {
                if (pt.getName().equals(patternConnection.getPatternChannel())) {
                    modeChInPattern = pt.getMode();
                    typeChInPattern = pt.getType();
                    break;
                }
            }
            if (typeChInPattern.equals(PatternCreation.CHANNEL)) {
                TMLChannel chInPattern = tmlmPattern.getChannelByName(patternConnection.getPatternChannel());
                if (chInPattern != null) {
                    if (modeChInPattern.equals(PatternCreation.MODE_INPUT)) {
                        correspTasks.put(chInPattern.getOriginTask(), tmlmModel.getTMLTaskByName(modelTask));
                    } else if (modeChInPattern.equals(PatternCreation.MODE_OUTPUT)) {
                        correspTasks.put(chInPattern.getDestinationTask(), tmlmModel.getTMLTaskByName(modelTask));
                    }
                }
            } else if (typeChInPattern.equals(PatternCreation.EVENT)) {
                TMLEvent evtInPattern = tmlmPattern.getEventByName(patternConnection.getPatternChannel());
                if (evtInPattern != null) {
                    if (modeChInPattern.equals(PatternCreation.MODE_INPUT)) {
                        correspTasks.put(evtInPattern.getOriginTask(), tmlmModel.getTMLTaskByName(modelTask));
                    } else if (modeChInPattern.equals(PatternCreation.MODE_OUTPUT)) {
                        correspTasks.put(evtInPattern.getDestinationTask(), tmlmModel.getTMLTaskByName(modelTask));
                    }
                }
            }
        }
        for (String patternTask: _patternTasks.keySet()) {
            correspTasks.put(tmlmPattern.getTMLTaskByName(patternTask), tmlmModel.getTMLTaskByName(tasksOfPatternIntoModel.get(patternTask)));
        }
        return correspTasks;
    }


    public TMLMapping<?> mapChannelsInArch(TMLMapping<?> _tmapModel, List<MappingPatternChannel> _channelMapping) {
        TMLArchitecture _tmlarchModel =  _tmapModel.getArch();
        for (MappingPatternChannel channelMapping : _channelMapping) {
            String channelToMapName = channelMapping.getChannelToMapName();
            String taskOfChannelToMap = channelMapping.getTaskOfChannelToMap();
            /*if (renamedChannels.containsKey(channelToMapName)) {
                channelToMapName = renamedChannels.get(channelToMapName);
            }*/
            String channelToMapNameInModel = channelToMapName;
            if (channelMapping.getOrigin() == MappingPatternChannel.ORIGIN_CLONE && channelsClonedIntoModel.containsKey(Map.entry(taskOfChannelToMap, channelToMapName))) {
                channelToMapNameInModel =  channelsClonedIntoModel.get(Map.entry(taskOfChannelToMap, channelToMapName));
            } else if (channelMapping.getOrigin() == MappingPatternChannel.ORIGIN_PATTERN && channelsOfPatternIntoModel.containsKey(Map.entry(taskOfChannelToMap, channelToMapName))) {
                channelToMapNameInModel =  channelsOfPatternIntoModel.get(Map.entry(taskOfChannelToMap, channelToMapName));
            }
            TMLChannel channelToMap = _tmapModel.getChannelByName(channelToMapNameInModel);
            if (channelToMap != null) {
                TraceManager.addDev("channelToMap != null");
                if (channelMapping.getChannelInSameMemAs() != null) {
                    String sameChannel = channelMapping.getChannelInSameMemAs();
                    String sameChannelTask = channelMapping.getTaskOfChannelInSameMem();
                    String sameChannelInModel = sameChannel;
                    if (channelMapping.getSameMemAsOrigin() == MappingPatternChannel.ORIGIN_CLONE && channelsClonedIntoModel.containsKey(Map.entry(sameChannelTask, sameChannel))) {
                        sameChannelInModel =  channelsClonedIntoModel.get(Map.entry(sameChannelTask, sameChannel));
                    } else if (channelMapping.getSameMemAsOrigin() == MappingPatternChannel.ORIGIN_PATTERN && channelsOfPatternIntoModel.containsKey(Map.entry(sameChannelTask, sameChannel))) {
                        sameChannelInModel =  channelsOfPatternIntoModel.get(Map.entry(sameChannelTask, sameChannel));
                    }
                    TraceManager.addDev("sameChannel= " + sameChannel);
                    TMLChannel inSameChannel = _tmapModel.getChannelByName(sameChannelInModel);
                    if (inSameChannel != null) {
                        TraceManager.addDev("inSameChannel != null");
                        HwMemory memoryOfSameChannel = _tmapModel.getMemoryOfChannel(inSameChannel);
                        if (memoryOfSameChannel != null) {
                            _tmapModel.addCommToHwCommNode(channelToMap, memoryOfSameChannel);
                            TraceManager.addDev("memoryOfSameChannel != null");
                        }
                        for (HwCommunicationNode mappedNode : _tmapModel.getAllCommunicationNodesOfChannel(inSameChannel)) {
                            if (mappedNode instanceof HwBus) {
                                TraceManager.addDev("mappedNode instanceof HwBus");
                                _tmapModel.addCommToHwCommNode(channelToMap, mappedNode);
                            }   
                        }
                    }
                } else if (channelMapping.getBusNameForNewMem() != null) {
                    String busName = channelMapping.getBusNameForNewMem();
                    HwBus bus = _tmlarchModel.getHwBusByName(busName);
                    if (bus != null) {
                        String nameMem = "Memory_" + channelToMapName;
                        HwNode node = _tmlarchModel.getHwNodeByName(nameMem);
                        if (node != null) {
                            int indexMem = 0;
                            String nameMemWithIndex = nameMem + indexMem;
                            while(_tmlarchModel.getHwNodeByName(nameMemWithIndex) != null) {
                                indexMem += 1;
                                nameMemWithIndex = nameMem + indexMem;
                            }
                            nameMem = nameMemWithIndex;
                        }
                        HwMemory _newMem = new HwMemory(nameMem);
                        _tmapModel.addCommToHwCommNode(channelToMap, _newMem);
                        _tmapModel.addCommToHwCommNode(channelToMap, bus);
                        _tmlarchModel.addHwNode(_newMem);
                        HwLink linkNewMemWithBus = new HwLink("link_" + _newMem.getName() + "_to_" + bus.getName());
                        linkNewMemWithBus.bus = bus;
                        linkNewMemWithBus.hwnode = _newMem;
                        _tmlarchModel.addHwLink(linkNewMemWithBus);
                    }
                }
            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> mapChannelsInArchAuto(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, List<MappingPatternTask> _mappingPatternTasks, List<MappingPatternChannel> _mappingPatternChannels, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks) {
        Map<TMLChannel, TMLChannel> correspChannels = correspondenceForChannels(_tmapModel, _tmapPattern, _patternConfiguration, _patternTasks);
        for (TMLChannel corresp : correspChannels.keySet()) {
            TraceManager.addDev("chInPattern=" + corresp.getName() + " chInModel=" + correspChannels.get(corresp).getName());
        }
        List<MappingPatternChannel> allChannelsToMap = MappingPatternChannel.getChannelsToMap(_patternConfiguration.getPortsConnection(), _patternTasks, _patternConfiguration.getClonedTasks());
        List<MappingPatternChannel> channelsToMapAuto = new ArrayList<MappingPatternChannel>();
        for (MappingPatternChannel mappingPatternChannel : allChannelsToMap) {
            TraceManager.addDev("getTaskOfChannelToMap: " + mappingPatternChannel.getTaskOfChannelToMap() + " getChannelToMapName: " + mappingPatternChannel.getChannelToMapName() + " getOrigin: " + mappingPatternChannel.getOrigin());
            boolean isMapped = false;
            for (MappingPatternChannel mappedPatternChannel : _mappingPatternChannels) {
                if (mappingPatternChannel.getChannelToMapName().equals(mappedPatternChannel.getChannelToMapName()) && mappingPatternChannel.getTaskOfChannelToMap().equals(mappedPatternChannel.getTaskOfChannelToMap())) {
                    isMapped = true;
                    break;
                }
            }
            if (!isMapped) {
                channelsToMapAuto.add(mappingPatternChannel);
            }
        }
        for (MappingPatternChannel mappingPatternChannel : channelsToMapAuto) {
            String chToMapName = mappingPatternChannel.getChannelToMapName();
            String taskOfChToMapName = mappingPatternChannel.getTaskOfChannelToMap();
            TMLChannel chToMap = null;
            TMLChannel chInPattern = null;
            TraceManager.addDev("chToMapName: " + chToMapName + " taskOfChToMapName: " + taskOfChToMapName);
            if (mappingPatternChannel.getOrigin() == MappingPatternChannel.ORIGIN_CLONE) {
                String chToMapNameInModel = channelsClonedIntoModel.get(Map.entry(taskOfChToMapName, chToMapName));
                chToMap = _tmapModel.getChannelByName(chToMapNameInModel);
                TraceManager.addDev("ORIGIN_CLONE: chToMap=" + chToMap.getName());
                chInPattern = getKeyByValue(correspChannels, chToMap);
                TraceManager.addDev("ORIGIN_CLONE: chInPattern=" + chInPattern.getName());
            } else if (mappingPatternChannel.getOrigin() == MappingPatternChannel.ORIGIN_PATTERN) {
                chInPattern = _tmapPattern.getChannelByName(chToMapName);
                chToMap = _tmapModel.getChannelByName(channelsOfPatternIntoModel.get(Map.entry(taskOfChToMapName, chToMapName)));
                TraceManager.addDev("ORIGIN_PATTERN: chInPattern=" + chInPattern.getName());
                TraceManager.addDev("ORIGIN_PATTERN: chToMap=" + chToMap.getName());
            }
            if (chToMap != null && chInPattern != null) {
                ArrayList<HwCommunicationNode> hwComsOfChannel = _tmapPattern.getAllCommunicationNodesOfChannel(chInPattern);
                TraceManager.addDev("hwComsOfChannel size=" + hwComsOfChannel.size());
                Boolean chIsMappedToMem = false;
                Boolean chIsMappedToBuses = false;
                for (HwCommunicationNode hwComOfChannel : hwComsOfChannel) {
                    if (hwComOfChannel instanceof HwMemory) {
                        if (!chIsMappedToMem) {
                            HashSet<TMLElement> channelsInThisHwCom = _tmapPattern.getLisMappedChannels(hwComOfChannel);
                            for (TMLElement elemInHwCom : channelsInThisHwCom) {
                                if (elemInHwCom instanceof TMLChannel) {
                                    TMLChannel channelInHwCom = (TMLChannel) elemInHwCom;
                                    if (channelInHwCom != chInPattern && correspChannels.containsKey(channelInHwCom)) {
                                        TMLChannel channelInModel = correspChannels.get(channelInHwCom);
                                        ArrayList<HwCommunicationNode> hwsMapChannelInModel = _tmapModel.getAllCommunicationNodesOfChannel(channelInModel);
                                        if (hwsMapChannelInModel.size() > 0) {
                                            for (HwCommunicationNode hwMapChannelInModel : hwsMapChannelInModel) {
                                                if ((!chIsMappedToMem) && (hwMapChannelInModel instanceof HwMemory) && !(_tmapModel.isCommNodeMappedOn(chToMap, hwMapChannelInModel))) {
                                                    _tmapModel.addCommToHwCommNode(chToMap, hwMapChannelInModel);
                                                    chIsMappedToMem = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!chIsMappedToBuses) {
                            HashSet<TMLElement> channelsInThisHwCom = _tmapPattern.getLisMappedChannels(hwComOfChannel);
                            for (TMLElement elemInHwCom : channelsInThisHwCom) {
                                if (elemInHwCom instanceof TMLChannel) {
                                    TMLChannel channelInHwCom = (TMLChannel) elemInHwCom;
                                    if (channelInHwCom != chInPattern && correspChannels.containsKey(channelInHwCom)) {
                                        TMLChannel channelInModel = correspChannels.get(channelInHwCom);
                                        ArrayList<HwCommunicationNode> hwsMapChannelInModel = _tmapModel.getAllCommunicationNodesOfChannel(channelInModel);
                                        if (hwsMapChannelInModel.size() > 0) {
                                            for (HwCommunicationNode hwMapChannelInModel : hwsMapChannelInModel) {
                                                if (!(hwMapChannelInModel instanceof HwMemory) && !(_tmapModel.isCommNodeMappedOn(chToMap, hwMapChannelInModel))) {
                                                    _tmapModel.addCommToHwCommNode(chToMap, hwMapChannelInModel);
                                                }
                                            }
                                            //break;
                                        }
                                    }
                                }
                            }
                        }                        
                    }    
                }
                if (!chIsMappedToMem) {
                    HwMemory memMapChannel = _tmapPattern.getMemoryOfChannel(chInPattern);
                    String nameNewMem = memMapChannel.getName();
                    HwCommunicationNode newMem = _tmapModel.getHwCommunicationNodeByName(nameNewMem);
                    int ind = 0;
                    while (newMem != null) {
                        nameNewMem = memMapChannel.getName() + ind;
                        newMem = _tmapModel.getHwCommunicationNodeByName(nameNewMem);
                        ind += 1;
                    }
                    HwMemory _newMem = new HwMemory(nameNewMem);
                    _tmapModel.addCommToHwCommNode(chToMap, _newMem);
                    _tmapModel.getArch().addHwNode(_newMem);
                    HwBus bus = null;
                    TMLTask taskInConn = chToMap.getDestinationTask();
                    HwExecutionNode hwOfTaskInConn = _tmapModel.getHwNodeOf(taskInConn);
                    if (hwOfTaskInConn != null) {
                        for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                            if (link.hwnode == hwOfTaskInConn) {
                                bus = link.bus;
                                break;
                            }
                        }
                    }
                    if (bus == null) {
                        taskInConn = chToMap.getOriginTask();
                        hwOfTaskInConn = _tmapModel.getHwNodeOf(taskInConn);
                        if (hwOfTaskInConn != null) {
                            for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                if (link.hwnode == hwOfTaskInConn) {
                                    bus = link.bus;
                                    break;
                                }
                            }
                        }
                    }
                    if (bus == null) {
                        int nbConnections = 0 ; 
                        for (HwNode busModel : _tmapModel.getArch().getBUSs()) {
                            if (busModel instanceof HwBus) {
                                int curNbConnections = 0 ;
                                for (HwLink link : _tmapModel.getArch().getHwLinks()) {
                                    if (link.bus == busModel) {
                                        nbConnections += 1;
                                    }
                                }
                                if (curNbConnections > nbConnections) {
                                    bus = (HwBus) busModel;
                                    nbConnections = curNbConnections;
                                }
                            }
                        }
                        
                    }
                    
                    HwLink linkNewMemWithBus = new HwLink("link_" + _newMem.getName() + "_to_" + bus.getName());
                    linkNewMemWithBus.bus = bus;
                    linkNewMemWithBus.hwnode = _newMem;
                    _tmapModel.addCommToHwCommNode(chToMap, bus);
                    _tmapModel.getArch().addHwLink(linkNewMemWithBus);
                }
            }
        }

        return _tmapModel;
    }

    // Find correspondence between Channels of the pattern model and the channels of cloned tasks of current Model 
    /*public Map<TMLChannel, TMLChannel> correspondenceForClonedChannels(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, Set<String> mappedTasks, PatternConfiguration _patternConfiguration) {
        TMLModeling<?> tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = _tmapPattern.getTMLModeling();
        List <String> clonedChannelsToMap = new ArrayList<String>();
        // List <TMLTask> patternTasksToMap = new ArrayList<TMLTask>();
        for (String taskName : _patternConfiguration.getClonedTasks().keySet()) {
            TMLTask task = tmlmModel.getTMLTaskByName(taskName);
            if (task != null && !mappedTasks.contains(taskName)) {
                clonedChannelsToMap.add(task.getName());
            }
        }
        Map<TMLChannel, TMLChannel> correspChannels = new HashMap<TMLChannel, TMLChannel>();
        for (String taskPatternName : _patternConfiguration.getPortsConnection().keySet()) {
            for (String[] pc : _patternConfiguration.getPortsConnection().get(taskPatternName)) {
                if (clonedChannelsToMap.contains(pc[1])) {
                    TMLChannel chInPattern = tmlmPattern.getChannelByName(pc[0]);
                    if (chInPattern != null) {
                        String nameChInModel = pc[2];
                        if (renamedChannels.containsKey(nameChInModel)) {
                            nameChInModel = renamedChannels.get(nameChInModel);
                        }
                        correspChannels.put(tmlmModel.getChannelByName(nameChInModel), chInPattern);
                    }
                }
            }
        }
        return correspChannels;
    }*/

    // Find correspondence between channels of the pattern model and the channels of current Model 
    public Map<TMLChannel, TMLChannel> correspondenceForChannels(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = _tmapPattern.getTMLModeling();
        Map<TMLChannel, TMLChannel> correspChannels = new HashMap<TMLChannel, TMLChannel>();
        for (Entry<String, String> chTask : channelsOfPatternIntoModel.keySet()) {
            TMLChannel chInPattern = tmlmPattern.getChannelByName(chTask.getValue());
            TMLChannel chInModel = tmlmModel.getChannelByName(channelsOfPatternIntoModel.get(chTask));
            if (chInModel != null && chInPattern != null) {
                correspChannels.put(chInPattern, chInModel);
            }
        }
        for (PatternConnection patternConnection : _patternConfiguration.getPortsConnection()) {
            if (!patternConnection.isANewChannelRequired(_patternConfiguration.getPortsConnection(), _patternTasks, _patternConfiguration.getClonedTasks())) {
                String chNameInModel = patternConnection.getModelChannelName();
                String taskNameInModel = patternConnection.getModelTaskName();
                if (channelsClonedIntoModel.containsKey(Map.entry(taskNameInModel, chNameInModel))) {
                    chNameInModel = channelsClonedIntoModel.get(Map.entry(taskNameInModel, chNameInModel));
                }
                
                TMLChannel chInPattern = tmlmPattern.getChannelByName(patternConnection.getPatternChannel());
                TMLChannel chInModel = tmlmModel.getChannelByName(chNameInModel);
                if (chInModel != null && chInPattern != null) {
                    correspChannels.put(chInPattern, chInModel);
                }
            }
        }
        return correspChannels;
    }

    public TMLMapping<?> generateSecurityForChannels(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, PatternConfiguration _patternConfiguration, LinkedHashMap<String, TaskPattern> _patternTasks, String _appTabName) {
        boolean hasSecChannel = false;
        Map<TMLChannel, TMLChannel> correspChannels = correspondenceForChannels(_tmapModel, _tmapPattern, _patternConfiguration, _patternTasks);
        for (PatternChannelWithSecurity channelWithSec : _patternConfiguration.getChannelsWithSecurity()) {
            TMLChannel chSec = correspChannels.get(_tmapPattern.getChannelByName(channelWithSec.getChannelName()));
            if (chSec != null) {
                TraceManager.addDev("channelsWithSec = " + chSec.getName());
                if (channelWithSec.isConfidential()) {
                    TraceManager.addDev("channelsWithSec with conf ");
                    chSec.setEnsureConf(true);
                    chSec.checkConf = true;
                }
                if (channelWithSec.getAuthenticity() == PatternChannelWithSecurity.WEAK_AUTHENTICITY) {
                    TraceManager.addDev("channelsWithSec with weak auth ");
                    chSec.setEnsureWeakAuth(true);
                    chSec.checkAuth = true;
                }
                if (channelWithSec.getAuthenticity() == PatternChannelWithSecurity.STRONG_AUTHENTICITY) {
                    TraceManager.addDev("channelsWithSec with strong auth");
                    chSec.setEnsureStrongAuth(true);
                    chSec.setEnsureWeakAuth(true);
                    chSec.checkAuth = true;
                }
                hasSecChannel = true;
            }
        }
        if (hasSecChannel) {
            TraceManager.addDev("hasSecChannel");
            if (_appTabName != "") {
                TraceManager.addDev("appName=" + _appTabName);
                for (TMLTask task : _tmapModel.getTMLModeling().getTasks()) {
                    String[] taskNameSplit = task.getName().split("__");
                    if (taskNameSplit.length == 1) {
                        task.setName(_appTabName + "__" + task.getName());
                    }
                }
                for (TMLChannel ch : _tmapModel.getTMLModeling().getChannels()) {
                    String[] channelNameSplit = ch.getName().split("__");
                    if (channelNameSplit.length == 1) {
                        ch.setName(_appTabName + "__" + ch.getName());
                    }
                }
                for (TMLEvent evt : _tmapModel.getTMLModeling().getEvents()) {
                    String[] eventNameSplit = evt.getName().split("__");
                    if (eventNameSplit.length == 1) {
                        evt.setName(_appTabName + "__" + evt.getName());
                    }
                }
                for (TMLRequest req : _tmapModel.getTMLModeling().getRequests()) {
                    String[] requestNameSplit = req.getName().split("__");
                    if (requestNameSplit.length == 1) {
                        req.setName(_appTabName + "__" + req.getName());
                    }
                }
            
                SecurityGenerationForTMAP secgen = new SecurityGenerationForTMAP(_appTabName, _tmapModel, "100", "0", "100", new HashMap<String, java.util.List<String>>());
                _tmapModel = secgen.startThread();
                _tmapModel = secgen.autoMapKeys();
                for (TMLTask task : _tmapModel.getTMLModeling().getTasks()) {
                    String[] taskNameSplit = task.getName().split("__");
                    task.setName(taskNameSplit[taskNameSplit.length-1]);
                }
                for (TMLChannel ch : _tmapModel.getTMLModeling().getChannels()) {
                    String[] channelNameSplit = ch.getName().split("__");
                    ch.setName(channelNameSplit[channelNameSplit.length-1]);
                }
                for (TMLEvent evt : _tmapModel.getTMLModeling().getEvents()) {
                    String[] eventNameSplit = evt.getName().split("__");
                    evt.setName(eventNameSplit[eventNameSplit.length-1]);
                }
                for (TMLRequest req : _tmapModel.getTMLModeling().getRequests()) {
                    String[] requestNameSplit = req.getName().split("__");
                    req.setName(requestNameSplit[requestNameSplit.length-1]);
                }
            }
        }
        return _tmapModel;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
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
