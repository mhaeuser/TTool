/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */


package tmltranslator.patternhandling;
/**
 * Class TaskPattern
 * 
 * Creation: 28/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/08/2023
 */
 
import tmltranslator.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.json.JSONArray;

 
public class TaskPattern {
    private List<AttributeTaskJsonFile> attributes;
    private List<PortTaskJsonFile> internalPorts;
    private List<PortTaskJsonFile> externalPorts;

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
        LinkedHashMap<String, TaskPattern> patternTasksLeft = new LinkedHashMap<String, TaskPattern>();
        for (String taskPattern: _patternTasksAll.keySet()) {
            for (PortTaskJsonFile portTask : _patternTasksAll.get(taskPattern).getExternalPorts()) {
                boolean isPortMapped = false;
                for (PatternConnection patternConnection: patternConnections) {
                    if (taskPattern.equals(patternConnection.getPatternTaskName()) && portTask.getName().equals(patternConnection.getPatternChannel())) {
                        isPortMapped = true;
                        break;
                    }
                }
                if (!isPortMapped) {
                    if (patternTasksLeft.containsKey(taskPattern)) {
                        patternTasksLeft.get(taskPattern).getExternalPorts().add(portTask);
                    } else {
                        List<PortTaskJsonFile> portsExternal = new ArrayList<PortTaskJsonFile>();
                        List<PortTaskJsonFile> portsInternal = new ArrayList<PortTaskJsonFile>();
                        List<AttributeTaskJsonFile> attributes = new ArrayList<AttributeTaskJsonFile>();
                        portsExternal.add(portTask);
                        TaskPattern tp = new TaskPattern(attributes, portsInternal, portsExternal);
                        patternTasksLeft.put(taskPattern, tp);
                    }
                }
            }
        }
        /*for (String taskPattern: _patternTasksAll.keySet()) {
            TaskPattern tp = new TaskPattern(_patternTasksAll.get(taskPattern).getAttributes(), _patternTasksAll.get(taskPattern).getInternalPorts(), _patternTasksAll.get(taskPattern).getExternalPorts());
            patternTasksLeft.put(taskPattern, tp);
        }
        
        for (String taskPattern: _patternTasksAll.keySet()) {
            TraceManager.addDev("_patternTasks: task=" + taskPattern);
            for (PortTaskJsonFile portTask : _patternTasksAll.get(taskPattern).getExternalPorts()) {
                TraceManager.addDev("Port=" + portTask.getName());
            }
            if (_patternTasksAll.get(taskPattern).getExternalPorts().size() == 0) {
                patternTasksLeft.remove(taskPattern);
            }
        }
        for (PatternConnection patternConnection: patternConnections) {
            TraceManager.addDev("patternConnection=" + patternConnection.getStringDisplay());
            if (patternTasksLeft.containsKey(patternConnection.getPatternTaskName())) {
                patternTasksLeft.get(patternConnection.getPatternTaskName()).removeExternalPort(patternConnection.getPatternChannel());
                if (patternTasksLeft.get(patternConnection.getPatternTaskName()).getExternalPorts().size() == 0) {
                    patternTasksLeft.remove(patternConnection.getPatternTaskName());
                }
            }
        }*/
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
