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
        Path jsonFilePath = Path.of(pathPatternFoler+"/"+fileName);
        String jsonFilecontent = "";
        LinkedHashMap<String, TaskPattern> tasksPattern = new LinkedHashMap<String, TaskPattern>();
        try {
            jsonFilecontent = Files.readString(jsonFilePath, Charset.defaultCharset());
        } catch (IOException ioExc) {
        } 
        
        JSONArray patternTasks = new JSONArray(jsonFilecontent);
        for (int i = 0; i < patternTasks.length(); i++) {
            String taskName = patternTasks.getJSONObject(i).getString(PatternGeneration.NAME);
            
            JSONArray attributes = patternTasks.getJSONObject(i).getJSONArray(PatternGeneration.ATTRIBUTES);
            List<AttributeTaskJsonFile> attributeTaskList = new ArrayList<AttributeTaskJsonFile>();
            for (int j = 0; j < attributes.length(); j++) {
                String attribName = attributes.getJSONObject(j).getString(PatternGeneration.NAME);
                String attribType = attributes.getJSONObject(j).getString(PatternGeneration.TYPE);
                String attribValue = attributes.getJSONObject(j).getString(PatternGeneration.VALUE);
                AttributeTaskJsonFile attributeTaskJsonFile = new AttributeTaskJsonFile(attribName, attribType, attribValue);
                attributeTaskList.add(attributeTaskJsonFile);
            }
            //patternTasksAttributes.put(taskName, attributeTaskList);

            JSONArray externalPorts = patternTasks.getJSONObject(i).getJSONArray(PatternGeneration.EXTERNALPORTS);
            List<PortTaskJsonFile> externalPortsTaskList = new ArrayList<PortTaskJsonFile>();
            for (int j = 0; j < externalPorts.length(); j++) {
                String externalPortName = externalPorts.getJSONObject(j).getString(PatternGeneration.NAME);
                String externalPortType = externalPorts.getJSONObject(j).getString(PatternGeneration.TYPE);
                String externalPortMode = externalPorts.getJSONObject(j).getString(PatternGeneration.MODE);
                TraceManager.addDev("externalPortName= "+ externalPortName);
                PortTaskJsonFile externalPortTaskJsonFile = new PortTaskJsonFile(externalPortName, externalPortType, externalPortMode);
                if (externalPorts.getJSONObject(j).has(PatternGeneration.CONFIDENTIALITY)) {
                    externalPortTaskJsonFile.setConfidentiality(externalPorts.getJSONObject(j).getString(PatternGeneration.CONFIDENTIALITY));
                }
                if (externalPorts.getJSONObject(j).has(PatternGeneration.AUTHENTICITY)) {
                    externalPortTaskJsonFile.setAuthenticity(externalPorts.getJSONObject(j).getString(PatternGeneration.AUTHENTICITY));
                }
                externalPortsTaskList.add(externalPortTaskJsonFile);
            }
            //patternTasksExternalPorts.put(taskName, externalPortsTaskList);

            JSONArray internalPorts = patternTasks.getJSONObject(i).getJSONArray(PatternGeneration.INTERNALPORTS);
            List<PortTaskJsonFile> internalPortsTaskList = new ArrayList<PortTaskJsonFile>();
            for (int j = 0; j < internalPorts.length(); j++) {
                String internalPortName = internalPorts.getJSONObject(j).getString(PatternGeneration.NAME);
                String internalPortType = internalPorts.getJSONObject(j).getString(PatternGeneration.TYPE);
                String internalPortMode = internalPorts.getJSONObject(j).getString(PatternGeneration.MODE);
                PortTaskJsonFile internalPortTaskJsonFile = new PortTaskJsonFile(internalPortName, internalPortType, internalPortMode);
                if (internalPorts.getJSONObject(j).has(PatternGeneration.CONFIDENTIALITY)) {
                    internalPortTaskJsonFile.setConfidentiality(internalPorts.getJSONObject(j).getString(PatternGeneration.CONFIDENTIALITY));
                }
                if (internalPorts.getJSONObject(j).has(PatternGeneration.AUTHENTICITY)) {
                    internalPortTaskJsonFile.setAuthenticity(internalPorts.getJSONObject(j).getString(PatternGeneration.AUTHENTICITY));
                }
                internalPortsTaskList.add(internalPortTaskJsonFile);
            }
            //patternTasksInternalPorts.put(taskName, internalPortsTaskList);
            TaskPattern taskPattern = new TaskPattern(attributeTaskList, internalPortsTaskList, externalPortsTaskList);
            tasksPattern.put(taskName, taskPattern);
        }
        return tasksPattern;
    }
    
}
