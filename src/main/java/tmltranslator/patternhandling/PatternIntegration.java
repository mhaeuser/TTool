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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

 
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
        
	}

    public TMLMapping<?> addPatternTasksInModel(TMLMapping<?> _tmapModel, TMLMapping<?> _tmapPattern, LinkedHashMap<String, TaskPattern> _patternTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        TMLModeling<?> _tmlmPattern = _tmapPattern.getTMLModeling();
        for (String taskName : _patternTasks.keySet()) {
            TMLTask taskPattern = _tmlmPattern.getTMLTaskByName(taskName);
            if (taskPattern != null) {
                for (int i=0; i < _patternTasks.get(taskName).getAttributes().size(); i++) {
                    TMLAttribute attribTaskPattern = taskPattern.getAttributeByName(_patternTasks.get(taskName).getAttributes().get(i).getName());
                    attribTaskPattern.initialValue = _patternTasks.get(taskName).getAttributes().get(i).getValue();
                }
                _tmlmModel.addTask(taskPattern);
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


    public TMLMapping<?> addClonedTask(TMLMapping<?> _tmapModel, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, String> _clonedTasks) {
        TMLModeling<?> _tmlmModel = _tmapModel.getTMLModeling();
        for (String clonedTask : _clonedTasks.keySet()) {
            TMLTask taskToClone = _tmlmModel.getTMLTaskByName(_clonedTasks.get(clonedTask));
            if (taskToClone != null) {
                TMLTask taskClone = new TMLTask(clonedTask, taskToClone.getReferenceObject(), null);
                _tmlmModel.addTask(taskClone);
                taskClone.getAttributes().addAll(taskToClone.getAttributes());
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
        TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmapPattern);
        syntax.checkSyntax();
        return _tmapPattern;
    }
}
