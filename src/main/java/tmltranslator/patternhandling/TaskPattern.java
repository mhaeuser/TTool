package tmltranslator.patternhandling;
/**
 * Class TaskPattern
 * 
 * Creation: 28/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/08/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.json.JSONArray;

 
public class TaskPattern {
    List<AttributeTaskJsonFile> attributes;
    List<PortTaskJsonFile> internalPorts;
    List<PortTaskJsonFile> externalPorts;

    public TaskPattern(List<AttributeTaskJsonFile> attributes, List<PortTaskJsonFile>  internalPorts, List<PortTaskJsonFile>  externalPorts) {
        this.attributes = attributes;
        this.internalPorts = internalPorts;
        this.externalPorts = externalPorts;
    }

    public List<AttributeTaskJsonFile> getAttributes() {
        return attributes;
    }

    public List<PortTaskJsonFile> getInternalPorts() {
        return internalPorts;
    }

    public List<PortTaskJsonFile> getExternalPorts() {
        return externalPorts;
    }

    public static LinkedHashMap<String, TaskPattern> parsePatternJsonFile(String pathPatternFoler, String fileName) {
        TraceManager.addDev("path=" + pathPatternFoler+"/"+fileName);
        Path jsonFilePath = Path.of(pathPatternFoler+"/"+fileName);
        String jsonFilecontent = "";
        LinkedHashMap<String, TaskPattern> tasksPattern = new LinkedHashMap<String, TaskPattern>();
        try {
            jsonFilecontent = Files.readString(jsonFilePath, Charset.defaultCharset());
        } catch (IOException ioExc) {
        } 
        
        JSONArray patternTasks = new JSONArray(jsonFilecontent);
        for (int i = 0; i < patternTasks.length(); i++) {
            String taskName = patternTasks.getJSONObject(i).getString(PatternCreation.NAME);
            
            JSONArray attributes = patternTasks.getJSONObject(i).getJSONArray(PatternCreation.ATTRIBUTES);
            List<AttributeTaskJsonFile> attributeTaskList = new ArrayList<AttributeTaskJsonFile>();
            for (int j = 0; j < attributes.length(); j++) {
                String attribName = attributes.getJSONObject(j).getString(PatternCreation.NAME);
                String attribType = attributes.getJSONObject(j).getString(PatternCreation.TYPE);
                String attribValue = attributes.getJSONObject(j).getString(PatternCreation.VALUE);
                AttributeTaskJsonFile attributeTaskJsonFile = new AttributeTaskJsonFile(attribName, attribType, attribValue);
                attributeTaskList.add(attributeTaskJsonFile);
            }
            //patternTasksAttributes.put(taskName, attributeTaskList);

            JSONArray externalPorts = patternTasks.getJSONObject(i).getJSONArray(PatternCreation.EXTERNALPORTS);
            List<PortTaskJsonFile> externalPortsTaskList = new ArrayList<PortTaskJsonFile>();
            for (int j = 0; j < externalPorts.length(); j++) {
                String externalPortName = externalPorts.getJSONObject(j).getString(PatternCreation.NAME);
                String externalPortType = externalPorts.getJSONObject(j).getString(PatternCreation.TYPE);
                String externalPortMode = externalPorts.getJSONObject(j).getString(PatternCreation.MODE);
                TraceManager.addDev("externalPortName= "+ externalPortName);
                PortTaskJsonFile externalPortTaskJsonFile = new PortTaskJsonFile(externalPortName, externalPortType, externalPortMode);
                if (externalPorts.getJSONObject(j).has(PatternCreation.CONFIDENTIALITY)) {
                    externalPortTaskJsonFile.setConfidentiality(externalPorts.getJSONObject(j).getString(PatternCreation.CONFIDENTIALITY));
                }
                if (externalPorts.getJSONObject(j).has(PatternCreation.AUTHENTICITY)) {
                    externalPortTaskJsonFile.setAuthenticity(externalPorts.getJSONObject(j).getString(PatternCreation.AUTHENTICITY));
                }
                externalPortsTaskList.add(externalPortTaskJsonFile);
            }
            //patternTasksExternalPorts.put(taskName, externalPortsTaskList);

            JSONArray internalPorts = patternTasks.getJSONObject(i).getJSONArray(PatternCreation.INTERNALPORTS);
            List<PortTaskJsonFile> internalPortsTaskList = new ArrayList<PortTaskJsonFile>();
            for (int j = 0; j < internalPorts.length(); j++) {
                String internalPortName = internalPorts.getJSONObject(j).getString(PatternCreation.NAME);
                String internalPortType = internalPorts.getJSONObject(j).getString(PatternCreation.TYPE);
                String internalPortMode = internalPorts.getJSONObject(j).getString(PatternCreation.MODE);
                PortTaskJsonFile internalPortTaskJsonFile = new PortTaskJsonFile(internalPortName, internalPortType, internalPortMode);
                if (internalPorts.getJSONObject(j).has(PatternCreation.CONFIDENTIALITY)) {
                    internalPortTaskJsonFile.setConfidentiality(internalPorts.getJSONObject(j).getString(PatternCreation.CONFIDENTIALITY));
                }
                if (internalPorts.getJSONObject(j).has(PatternCreation.AUTHENTICITY)) {
                    internalPortTaskJsonFile.setAuthenticity(internalPorts.getJSONObject(j).getString(PatternCreation.AUTHENTICITY));
                }
                internalPortsTaskList.add(internalPortTaskJsonFile);
            }
            //patternTasksInternalPorts.put(taskName, internalPortsTaskList);
            TaskPattern taskPattern = new TaskPattern(attributeTaskList, internalPortsTaskList, externalPortsTaskList);
            tasksPattern.put(taskName, taskPattern);
        }
        return tasksPattern;
    }

    public void removeExternalPort(String port) {
        PortTaskJsonFile exPortToRemove = null; 
        for (PortTaskJsonFile exPort : externalPorts) {
            if (exPort.getName().equals(port)) {
                exPortToRemove = exPort;
            }
        }
        externalPorts.remove(exPortToRemove);
    }

    public static LinkedHashMap<String, TaskPattern> getPatternTasksLeft(LinkedHashMap<String, TaskPattern> _patternTasksAll, List<PatternConnection> patternConnections) {
        LinkedHashMap<String, TaskPattern> patternTasksLeft = new LinkedHashMap<String, TaskPattern>(_patternTasksAll);
        for (String taskPattern: _patternTasksAll.keySet()) {
            if (_patternTasksAll.get(taskPattern).getExternalPorts().size() == 0) {
                patternTasksLeft.remove(taskPattern);
            }
        }
        for (PatternConnection patternConnection: patternConnections) {
            if (patternTasksLeft.containsKey(patternConnection.getPatternTaskName())) {
                patternTasksLeft.get(patternConnection.getPatternTaskName()).removeExternalPort(patternConnection.getPatternChannel());
                if (patternTasksLeft.get(patternConnection.getPatternTaskName()).getExternalPorts().size() == 0) {
                    patternTasksLeft.remove(patternConnection.getPatternTaskName());
                }
            }
        }
        return patternTasksLeft;
    }
    public PortTaskJsonFile getExternalPortByName(String portName) {
        for (PortTaskJsonFile exPort : externalPorts) {
            if (exPort.getName().equals(portName)) {
                return exPort;
            }
        }
        return null;
    }
}
