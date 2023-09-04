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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JDialog;

 
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
        renamePatternTasksName();
        renamePatternChannelsName();
        tmapModel = addClonedTask(tmapModel, clonedTasks);
        tmapPattern = updatePatternTasksAttributes(tmapPattern, patternTasks);
        tmapModel = addPatternTasksInModel(tmapModel, tmapPattern, patternTasks);
        

	}
    
    public void renamePatternTasksName() {
        TMLModeling<?> tmlmModel = tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        String[] patternTasksKeys = patternTasks.keySet().toArray(new String[patternTasks.keySet().size()]);
        for (int i = 0; i < patternTasks.keySet().size(); i++) {
            String taskName = patternTasksKeys[i];
            if (tmlmModel.getTMLTaskByName(taskName) != null) {
                int indexTask = 0;
                String taskNameWithIndex = taskName + indexTask;
                while (tmlmModel.getTMLTaskByName(taskNameWithIndex) != null) {
                    indexTask += 1;
                    taskNameWithIndex= taskName + indexTask;
                }
                TMLTask taskPattern = tmlmPattern.getTMLTaskByName(taskName);
                taskPattern.setName(taskNameWithIndex);
                portsConnection.put(taskNameWithIndex, portsConnection.remove(taskName));
                tasksMapping.put(taskNameWithIndex, tasksMapping.remove(taskName));
                channelsMapping.put(taskNameWithIndex, channelsMapping.remove(taskName));
                patternTasks.put(taskNameWithIndex, patternTasks.remove(taskName));
            }
        }
    }

    public void renamePatternChannelsName() {
        TMLModeling<?> tmlmModel = tmapModel.getTMLModeling();
        TMLModeling<?> tmlmPattern = tmapPattern.getTMLModeling();
        for (String taskName : patternTasks.keySet()) {
            TaskPattern tp = patternTasks.get(taskName);
            for (int i = 0 ; i < tp.getExternalPorts().size() ; i++) {
                PortTaskJsonFile extPort = tp.getExternalPorts().get(i);
                if (tmlmModel.getChannelByName(extPort.name) != null) {
                    int indexChannel = 0;
                    String channelNameWithIndex = extPort.name + indexChannel;
                    while (tmlmModel.getChannelByName(channelNameWithIndex) != null) {
                        indexChannel += 1;
                        channelNameWithIndex= extPort.name + indexChannel;
                    }
                    TMLChannel channelPattern = tmlmPattern.getChannelByName(extPort.name);
                    channelPattern.setName(channelNameWithIndex);
                    for (String[] pc : portsConnection.get(taskName)) {
                        if (pc[0].equals(extPort.name)) {
                            pc[0] = channelNameWithIndex;
                        }
                    }

                    for (String[] cm : channelsMapping.get(taskName)) {
                        if (cm[0].equals(extPort.name)) {
                            cm[0] = channelNameWithIndex;
                        }
                    }
                    extPort.name = channelNameWithIndex;
                } else if (tmlmModel.getEventByName(extPort.name) != null) {
                    int indexEvent = 0;
                    String eventNameWithIndex = extPort.name + indexEvent;
                    while (tmlmModel.getEventByName(eventNameWithIndex) != null) {
                        indexEvent += 1;
                        eventNameWithIndex= extPort.name + indexEvent;
                    }
                    TMLEvent eventPattern = tmlmPattern.getEventByName(extPort.name);
                    eventPattern.setName(eventNameWithIndex);
                    for (String[] pc : portsConnection.get(taskName)) {
                        if (pc[0].equals(extPort.name)) {
                            pc[0] = eventNameWithIndex;
                        }
                    }
                    extPort.name = eventNameWithIndex;
                }
            }

            for (int i = 0 ; i < tp.getInternalPorts().size() ; i++) {
                PortTaskJsonFile extPort = tp.getInternalPorts().get(i);
                if (tmlmModel.getChannelByName(extPort.name) != null) {
                    int indexChannel = 0;
                    String channelNameWithIndex = extPort.name + indexChannel;
                    while (tmlmModel.getChannelByName(channelNameWithIndex) != null) {
                        indexChannel += 1;
                        channelNameWithIndex= extPort.name + indexChannel;
                    }
                    TMLChannel channelPattern = tmlmPattern.getChannelByName(extPort.name);
                    channelPattern.setName(channelNameWithIndex);

                    for (String[] cm : channelsMapping.get(taskName)) {
                        if (cm[0].equals(extPort.name)) {
                            cm[0] = channelNameWithIndex;
                        }
                    }
                    extPort.name = channelNameWithIndex;
                } else if (tmlmModel.getEventByName(extPort.name) != null) {
                    int indexEvent = 0;
                    String eventNameWithIndex = extPort.name + indexEvent;
                    while (tmlmModel.getEventByName(eventNameWithIndex) != null) {
                        indexEvent += 1;
                        eventNameWithIndex= extPort.name + indexEvent;
                    }
                    TMLEvent eventPattern = tmlmPattern.getEventByName(extPort.name);
                    eventPattern.setName(eventNameWithIndex);
                    extPort.name = eventNameWithIndex;
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
                int indexTask = 0;
                String taskNameWithIndex = taskName + indexTask;
                while (_tmlmModel.getTMLTaskByName(taskNameWithIndex) != null) {
                    indexTask += 1;
                    taskNameWithIndex= taskName + indexTask;
                }
                taskPattern.setName(taskNameWithIndex);
                _tmlmModel.addTask(taskPattern);
                
            }
        }
        return _tmapModel;
    }

    public TMLMapping<?> addPatternInternalChannelsInModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        /*for (String taskName : _patternTasks.keySet()) {
            TMLTask taskPattern = _tmlmPattern.getTMLTaskByName(taskName);
            if (taskPattern != null) {
                if (_tmlmModel.getTMLTaskByName(taskName) != null) {
                    int indexTask = 0;
                    String taskNameWithIndex = taskName + indexTask;
                    while (_tmlmModel.getTMLTaskByName(taskNameWithIndex) != null) {
                        indexTask += 1;
                        taskNameWithIndex= taskName + indexTask;
                    }
                    taskPattern.setName(taskNameWithIndex);
                    _patternTasks.put( taskNameWithIndex, _patternTasks.remove(taskName));
                    
                }
                //_tmlmModel.addChannel(channelPattern);
                
            }
        }*/
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

    public TMLMapping<?> addNewPortToATask(TMLMapping<?> _tmapModel, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (String patternTaskName : _portsConnection.keySet()) {
            for (String[] elemP : _portsConnection.get(patternTaskName)) {
                if (elemP.length > 3 && elemP[3].equals(JDialogPatternGeneration.NEW_PORT_OPTION)) {
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

    public TMLMapping<?> makeConnectionBetweenPatternAndModel(TMLMapping<?> _tmapModel, LinkedHashMap<String,List<String[]>> _portsConnection) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (String patternTaskName : _portsConnection.keySet()) {
        
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
        TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmapPattern);
        syntax.checkSyntax();
        return _tmapPattern;
    }
}
