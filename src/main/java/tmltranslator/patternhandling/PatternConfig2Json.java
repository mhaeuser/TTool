package tmltranslator.patternhandling;
/**
 * Class PatternConfig2Json
 * Transform a Pattern configuration to Json file and vice versa
 * Creation: 05/09/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 05/09/2023
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JDialog;

import org.apache.batik.anim.timing.Trace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

 
public class PatternConfig2Json {
    public final static String TASK_MAPPING = "Task_Mapping";
    public final static String TASK_TO_MAP = "Task_To_Map";
    public final static String TASK_MAPPED_IN_SAME_HW_AS = "Task_Mapped_In_Same_HW_As";
    public final static String TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS = "Task_Mapped_In_New_HW";

    public final static String CHANNEL_MAPPING = "Channel_Mapping";
    public final static String TASK_OF_CHANNEL_TO_MAP = "Task_Of_Channel_To_Map";
    public final static String CHANNEL_TO_MAP = "Channel_To_Map";
    public final static String CHANNEL_MAPPED_IN_SAME_MEM_AS = "Channel_Mapped_In_Same_Mem_As";
    public final static String TASK_OF_CHANNEL_SAME_MEM = "Task_Of_Channel_Same_Mem_As";
    public final static String CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS = "Channel_Mapped_In_New_Mem";

    public final static String CHANNEL_CONFIG = "Channel_Configuration";
    public final static String TASK_OF_CHANNEL_TO_CONFIG = "Task_Of_Channel_To_Config";
    public final static String CHANNEL_TO_CONFIG = "Channel_To_Config";
    public final static String CHANNEL_TO_MERGE_WITH = "Channel_To_Merge_With";
    public final static String CHANNEL_TO_REMOVE = "Channel_To_Remove";

    public final static String TASK_CLONE = "Task_Clone";
    public final static String CLONE_OF_TASK = "Clone_Of_Task";
    public final static String CLONED_TASK = "Cloned_Task";
    
    public final static String CONNECTION = "Connection";
    public final static String PATTERN_TASK = "Pattern_Task";
    public final static String PATTERN_PORT = "Pattern_Port";
    public final static String MODEL_TASK = "Model_Task";
    public final static String MODEL_PORT = "Model_Port";
    public final static String NEW_PORT = "New_Port";

    public final static String UPDATED_ATTRIBUTES_IN_PATTERN = "Updated_Attributes_In_Pattern";
    public final static String TASK_ATTRIBUTE_NAME = "Attribute_Task_Name";

    public final static String CHANNELS_WITH_SECURITY_IN_PATTERN = "Channels_With_Security_In_Pattern";
    public final static String TASK_CHANNEL_WITH_SECURITY_NAME = "Task_Name_Channel";

    String patternConfigurationPathName;
    PatternConfiguration patternConfiguration;  


    public PatternConfiguration getPaternConfiguration() {
        return patternConfiguration;
    }
	
    public PatternConfig2Json(String _patternPathName, PatternConfiguration _patternConfiguration) {
		this.patternConfigurationPathName = _patternPathName;
		this.patternConfiguration = _patternConfiguration;
	}
    
    public PatternConfig2Json(String _patternPathName) {
		this.patternConfigurationPathName = _patternPathName;
        this.patternConfiguration = new PatternConfiguration();
	}

	
    public void patternConfiguration2Json() {
        try {
            FileWriter file = new FileWriter(patternConfigurationPathName);
            JSONObject jsonPatternConfig = new JSONObject();

            jsonPatternConfig.put(CONNECTION, addConnectionInJsonFile(patternConfiguration.getPortsConnection()));
            jsonPatternConfig.put(TASK_CLONE, addClonedTasksInJsonFile(patternConfiguration.getClonedTasks()));
            jsonPatternConfig.put(CHANNEL_CONFIG, addPortsConfigurationInJsonFile(patternConfiguration.getPortsConfig()));
            jsonPatternConfig.put(TASK_MAPPING, addTasksMappingInJsonFile(patternConfiguration.getTasksMapping()));
            jsonPatternConfig.put(CHANNEL_MAPPING, addChannelsMappingInJsonFile(patternConfiguration.getChannelsMapping()));
            jsonPatternConfig.put(UPDATED_ATTRIBUTES_IN_PATTERN, addUpdatedPatternAttributesInJsonFile(patternConfiguration.getUpdatedPatternAttributes()));
            jsonPatternConfig.put(CHANNELS_WITH_SECURITY_IN_PATTERN, addChannelsWithSecurityInJsonFile(patternConfiguration.getChannelsWithSecurity()));

            file.write(jsonPatternConfig.toString(1));
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void json2patternConfiguration() {
        Path jsonFilePath = Path.of(patternConfigurationPathName);
        String jsonFilecontent = "";
        
        try {
            jsonFilecontent = Files.readString(jsonFilePath, Charset.defaultCharset());
        } catch (IOException ioExc) {
        } 
        
        JSONObject patternConfigurationJson = new JSONObject(jsonFilecontent);
        JSONArray jsonConnection = patternConfigurationJson.getJSONArray(CONNECTION);
        JSONArray jsonTaskClone = patternConfigurationJson.getJSONArray(TASK_CLONE);
        JSONArray jsonChannelConfig = patternConfigurationJson.getJSONArray(CHANNEL_CONFIG);
        JSONArray jsonTaskMapping = patternConfigurationJson.getJSONArray(TASK_MAPPING);
        JSONArray jsonChannelMapping = patternConfigurationJson.getJSONArray(CHANNEL_MAPPING);
        JSONArray jsonUpdatedAttributesPattern = patternConfigurationJson.getJSONArray(UPDATED_ATTRIBUTES_IN_PATTERN);
        JSONArray jsonChannelWithSecurity = patternConfigurationJson.getJSONArray(CHANNELS_WITH_SECURITY_IN_PATTERN);
        patternConfiguration.setClonedTasks(getClonedTasksFromJsonFile(jsonTaskClone));
        patternConfiguration.setPortsConnection(getConnectionFromJsonFile(jsonConnection));
        patternConfiguration.setPortsConfig(getPortsConfigurationFromJsonFile(jsonChannelConfig));
        patternConfiguration.setTasksMapping(getTasksMappingFromJsonFile(jsonTaskMapping));
        patternConfiguration.setChannelsMapping(getChannelsMappingFromJsonFile(jsonChannelMapping));
        patternConfiguration.setUpdatedPatternAttributes(getUpdatedPatternAttributesFromJsonFile(jsonUpdatedAttributesPattern));
        patternConfiguration.setChannelsWithSecurity(getChannelsWithSecurityFromJsonFile(jsonChannelWithSecurity));

    }

    JSONArray addClonedTasksInJsonFile(LinkedHashMap<String, String> _clonedTasks) {
        JSONArray ja = new JSONArray();
        try {
            for (String clone : _clonedTasks.keySet()) {
                JSONObject jo = new JSONObject();
                jo.put(CLONE_OF_TASK, clone);
                jo.put(CLONED_TASK, _clonedTasks.get(clone));
                ja.put(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    LinkedHashMap<String, String> getClonedTasksFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, String> _clonedTasks = new LinkedHashMap<String, String>();

        for (int j = 0; j < ja.length(); j++) {
            String cloneOfTask = ja.getJSONObject(j).getString(CLONE_OF_TASK);
            String clonedTask = ja.getJSONObject(j).getString(CLONED_TASK);
            _clonedTasks.put(cloneOfTask, clonedTask);
        }
        return _clonedTasks;
    }

    JSONArray addPortsConfigurationInJsonFile(LinkedHashMap<String, List<Entry<String, String>>> _portsConfig) {
        JSONArray ja = new JSONArray();
        try {
            for (String task : _portsConfig.keySet()) {
                for (Entry<String, String> portConf : _portsConfig.get(task)) {
                    JSONObject jo = new JSONObject();
                    jo.put(CHANNEL_TO_CONFIG, portConf.getKey());
                    jo.put(TASK_OF_CHANNEL_TO_CONFIG, task);
                    if (portConf.getValue().equals("")) {
                        jo.put(CHANNEL_TO_REMOVE, "true");
                    } else {
                        jo.put(CHANNEL_TO_REMOVE, portConf.getValue());
                    }
                    ja.put(jo);
                } 
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    LinkedHashMap<String, List<Entry<String, String>>> getPortsConfigurationFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, List<Entry<String, String>>> _portsConfig = new LinkedHashMap<String, List<Entry<String, String>>>();
        for (int j = 0; j < ja.length(); j++) {
            String taskOfChToConfig = ja.getJSONObject(j).getString(TASK_OF_CHANNEL_TO_CONFIG);
            String chToConfig = ja.getJSONObject(j).getString(CHANNEL_TO_CONFIG);
            String chToMergeWith = "";
            if (ja.getJSONObject(j).has(CHANNEL_TO_MERGE_WITH)) {
                chToMergeWith = ja.getJSONObject(j).getString(CHANNEL_TO_MERGE_WITH);
            }
            String chToRemove = "";
            if (ja.getJSONObject(j).has(CHANNEL_TO_REMOVE)) {
                chToRemove = ja.getJSONObject(j).getString(CHANNEL_TO_REMOVE);
            }
            if (_portsConfig.containsKey(taskOfChToConfig)) {
                if (chToMergeWith != "") {
                    _portsConfig.get(taskOfChToConfig).add(new AbstractMap.SimpleEntry<String, String>(chToConfig, chToMergeWith));
                } else if (chToRemove != "") {
                    _portsConfig.get(taskOfChToConfig).add(new AbstractMap.SimpleEntry<String, String>(chToConfig, ""));
                }
            } else {
                List<Entry<String, String>> portConf = new ArrayList<Entry<String, String>>();
                if (chToMergeWith != "") {
                    portConf.add(new AbstractMap.SimpleEntry<String, String>(chToConfig, chToMergeWith));
                } else if (chToRemove != "") {
                    portConf.add(new AbstractMap.SimpleEntry<String, String>(chToConfig, ""));
                }
                _portsConfig.put(taskOfChToConfig, portConf);
            }
        }
        return _portsConfig;
    }

    JSONArray addChannelsMappingInJsonFile(LinkedHashMap<String, List<String[]>> _channelsMapping) {
        JSONArray ja = new JSONArray();
        try {
            for (String task : _channelsMapping.keySet()) {
                for (String[] chMapping : _channelsMapping.get(task)) {
                    JSONObject jo = new JSONObject();
                    jo.put(CHANNEL_TO_MAP, chMapping[1]);
                    jo.put(TASK_OF_CHANNEL_TO_MAP, task);
                    if (chMapping[0].equals(JDialogPatternGeneration.SAME_MEMORY)) {
                        jo.put(CHANNEL_MAPPED_IN_SAME_MEM_AS,chMapping[3]);
                        jo.put(TASK_OF_CHANNEL_SAME_MEM, chMapping[2]);
                    }
                    if (chMapping[0].equals(JDialogPatternGeneration.NEW_MEMORY)) {
                        jo.put(CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS,chMapping[2]);
                    }
                    ja.put(jo);
                }
                
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    JSONArray addUpdatedPatternAttributesInJsonFile(LinkedHashMap<String, List<AttributeTaskJsonFile>> _updatedPatternAttributes) {
        JSONArray ja = new JSONArray();
        try {
            for (String task : _updatedPatternAttributes.keySet()) {
                for (AttributeTaskJsonFile attributeTask : _updatedPatternAttributes.get(task)) {
                    JSONObject jo = new JSONObject();
                    jo.put(TASK_ATTRIBUTE_NAME, task);
                    jo.put(PatternGeneration.NAME, attributeTask.getName());
                    jo.put(PatternGeneration.TYPE, attributeTask.getType());
                    jo.put(PatternGeneration.VALUE, attributeTask.getValue());
                    ja.put(jo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    JSONArray addChannelsWithSecurityInJsonFile(LinkedHashMap<String, List<PortTaskJsonFile>> _channelsWithSec) {
        JSONArray ja = new JSONArray();
        try {
            for (String task : _channelsWithSec.keySet()) {
                for (PortTaskJsonFile portTask : _channelsWithSec.get(task)) {
                    JSONObject jo = new JSONObject();
                    jo.put(TASK_CHANNEL_WITH_SECURITY_NAME, task);
                    jo.put(PatternGeneration.NAME, portTask.getName());
                    jo.put(PatternGeneration.TYPE, portTask.getType());
                    jo.put(PatternGeneration.MODE, portTask.getMode());
                    jo.put(PatternGeneration.CONFIDENTIALITY, portTask.getConfidentiality());
                    jo.put(PatternGeneration.AUTHENTICITY, portTask.getAuthenticity());
                    ja.put(jo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    LinkedHashMap<String, List<PortTaskJsonFile>> getChannelsWithSecurityFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, List<PortTaskJsonFile>> _channelsWithSec = new LinkedHashMap<String, List<PortTaskJsonFile>>();
        for (int j = 0; j < ja.length(); j++) {
            String taskName = ja.getJSONObject(j).getString(TASK_CHANNEL_WITH_SECURITY_NAME);
            String channelName = ja.getJSONObject(j).getString(PatternGeneration.NAME);
            String channelType = ja.getJSONObject(j).getString(PatternGeneration.TYPE);
            String channelMode = ja.getJSONObject(j).getString(PatternGeneration.MODE);
            String channelConf = ja.getJSONObject(j).getString(PatternGeneration.CONFIDENTIALITY);
            String channelAuth = ja.getJSONObject(j).getString(PatternGeneration.AUTHENTICITY);
            if (_channelsWithSec.containsKey(taskName)) {
                PortTaskJsonFile portTask = new PortTaskJsonFile(channelName, channelType, channelMode);
                portTask.setConfidentiality(channelConf);
                portTask.setAuthenticity(channelAuth);
                _channelsWithSec.get(taskName).add(portTask);
            } else {
                List<PortTaskJsonFile> listPortTask = new ArrayList<PortTaskJsonFile>();
                PortTaskJsonFile portTask = new PortTaskJsonFile(channelName, channelType, channelMode);
                portTask.setConfidentiality(channelConf);
                portTask.setAuthenticity(channelAuth);
                listPortTask.add(portTask);
                _channelsWithSec.put(taskName, listPortTask);
            }
        }
        return _channelsWithSec;
    }

    LinkedHashMap<String, List<AttributeTaskJsonFile>> getUpdatedPatternAttributesFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, List<AttributeTaskJsonFile>> _updatedPatternAttributes = new LinkedHashMap<String, List<AttributeTaskJsonFile>>();
        for (int j = 0; j < ja.length(); j++) {
            String taskName = ja.getJSONObject(j).getString(TASK_ATTRIBUTE_NAME);
            String attributeName = ja.getJSONObject(j).getString(PatternGeneration.NAME);
            String attributeType = ja.getJSONObject(j).getString(PatternGeneration.TYPE);
            String attributeValue = ja.getJSONObject(j).getString(PatternGeneration.VALUE);
            if (_updatedPatternAttributes.containsKey(taskName)) {
                AttributeTaskJsonFile attrib = new AttributeTaskJsonFile(attributeName, attributeType, attributeValue);
                _updatedPatternAttributes.get(taskName).add(attrib);
            } else {
                List<AttributeTaskJsonFile> listAttrib = new ArrayList<AttributeTaskJsonFile>();
                AttributeTaskJsonFile attrib = new AttributeTaskJsonFile(attributeName, attributeType, attributeValue);
                listAttrib.add(attrib);
                _updatedPatternAttributes.put(taskName, listAttrib);
            }
        }
        return _updatedPatternAttributes;
    }

    LinkedHashMap<String, List<String[]>> getChannelsMappingFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, List<String[]>> _channelsMapping = new LinkedHashMap<String, List<String[]>>();
        for (int j = 0; j < ja.length(); j++) {
            String channelToMap = ja.getJSONObject(j).getString(CHANNEL_TO_MAP);
            String taskOfChannelToMap = ja.getJSONObject(j).getString(TASK_OF_CHANNEL_TO_MAP);
            String channelMappedInSameMem = "";
            if (ja.getJSONObject(j).has(CHANNEL_MAPPED_IN_SAME_MEM_AS)) {
                channelMappedInSameMem = ja.getJSONObject(j).getString(CHANNEL_MAPPED_IN_SAME_MEM_AS);
            }
            String taskOfChannelSameMem = "";
            if (ja.getJSONObject(j).has(TASK_OF_CHANNEL_SAME_MEM)) {
                taskOfChannelSameMem = ja.getJSONObject(j).getString(TASK_OF_CHANNEL_SAME_MEM);
            }
            String channelMappedInNewMem = "";
            if (ja.getJSONObject(j).has(CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS)) {
                channelMappedInNewMem = ja.getJSONObject(j).getString(CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS);
            }
            if (_channelsMapping.containsKey(taskOfChannelToMap)) {
                if (channelMappedInSameMem != "") {
                    String[] channelMap = new String[4];
                    channelMap[0] = JDialogPatternGeneration.SAME_MEMORY;
                    channelMap[1] = channelToMap;
                    channelMap[2] = taskOfChannelSameMem;
                    channelMap[3] = channelMappedInSameMem;
                    _channelsMapping.get(taskOfChannelToMap).add(channelMap);
                } else if (channelMappedInNewMem != "") {
                    String[] channelMap = new String[3];
                    channelMap[0] = JDialogPatternGeneration.NEW_MEMORY;
                    channelMap[1] = channelToMap;
                    channelMap[2] = channelMappedInNewMem;
                    _channelsMapping.get(taskOfChannelToMap).add(channelMap);
                }
            } else {
                List<String[]> channelMapList = new ArrayList<String[]>();
                if (channelMappedInSameMem != "") {
                    String[] channelMap = new String[4];
                    channelMap[0] = JDialogPatternGeneration.SAME_MEMORY;
                    channelMap[1] = channelToMap;
                    channelMap[2] = taskOfChannelSameMem;
                    channelMap[3] = channelMappedInSameMem;
                    channelMapList.add(channelMap);
                } else if (channelMappedInNewMem != "") {
                    String[] channelMap = new String[3];
                    channelMap[0] = JDialogPatternGeneration.NEW_MEMORY;
                    channelMap[1] = channelToMap;
                    channelMap[2] = channelMappedInNewMem;
                    channelMapList.add(channelMap);
                }
                _channelsMapping.put(taskOfChannelToMap, channelMapList);
            }
        }
        return _channelsMapping;
    }

    JSONArray addTasksMappingInJsonFile(LinkedHashMap<String, Entry<String, String>> _tasksMapping) {
        JSONArray ja = new JSONArray();
        try {
            for (String task : _tasksMapping.keySet()) {
                JSONObject jo = new JSONObject();
                jo.put(TASK_TO_MAP, task);
                if (_tasksMapping.get(task).getKey().equals(JDialogPatternGeneration.SAME_HW)) {
                    jo.put(TASK_MAPPED_IN_SAME_HW_AS, _tasksMapping.get(task).getValue());
                }
                if (_tasksMapping.get(task).getKey().equals(JDialogPatternGeneration.NEW_HW)) {
                    jo.put(TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS, _tasksMapping.get(task).getValue());
                }
                ja.put(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    LinkedHashMap<String, Entry<String, String>> getTasksMappingFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, Entry<String, String>> _tasksMapping = new LinkedHashMap<String, Entry<String, String>>();
        for (int j = 0; j < ja.length(); j++) {
            String taskToMap = ja.getJSONObject(j).getString(TASK_TO_MAP);
            String taskMappedInNewHw = "";
            if (ja.getJSONObject(j).has(TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS)) {
                taskMappedInNewHw = ja.getJSONObject(j).getString(TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS);
            }
            String taskMappedInSameHw = "";
            if (ja.getJSONObject(j).has(TASK_MAPPED_IN_SAME_HW_AS)) {
                taskMappedInSameHw = ja.getJSONObject(j).getString(TASK_MAPPED_IN_SAME_HW_AS);
            }
            if (taskMappedInSameHw != "") {
                _tasksMapping.put(taskToMap, new AbstractMap.SimpleEntry<String, String>(JDialogPatternGeneration.SAME_HW, taskMappedInSameHw));
            } else if (taskMappedInNewHw != "") {
                _tasksMapping.put(taskToMap, new AbstractMap.SimpleEntry<String, String>(JDialogPatternGeneration.NEW_HW, taskMappedInNewHw));
            }
        }
        return _tasksMapping;
    }

    JSONArray addConnectionInJsonFile(LinkedHashMap<String,List<String[]>> _portsConnection) {
        JSONArray ja = new JSONArray();
        try {
            for (String taskPattern : _portsConnection.keySet()) {
                for (String[] conn : _portsConnection.get(taskPattern)) {
                    JSONObject jo = new JSONObject();
                    jo.put(PATTERN_TASK, taskPattern);
                    jo.put(PATTERN_PORT, conn[0]);
                    jo.put(MODEL_TASK, conn[1]);
                    jo.put(MODEL_PORT, conn[2]);
                    
                    if (conn.length>=4 && conn[3].equals(JDialogPatternGeneration.NEW_PORT_OPTION)) {
                        jo.put(NEW_PORT, "true");
                    }
                    ja.put(jo);
                }
                
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    LinkedHashMap<String, List<String[]>> getConnectionFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, List<String[]>> _portsConnection = new LinkedHashMap<String, List<String[]>>();
        for (int j = 0; j < ja.length(); j++) {
            String patternTask = ja.getJSONObject(j).getString(PATTERN_TASK);
            String patternPort = ja.getJSONObject(j).getString(PATTERN_PORT);
            String modelTask = ja.getJSONObject(j).getString(MODEL_TASK);
            String modelPort = ja.getJSONObject(j).getString(MODEL_PORT);
            String newPort = "";
            if (ja.getJSONObject(j).has(NEW_PORT)) {
                newPort = ja.getJSONObject(j).getString(NEW_PORT);
            }
            if (_portsConnection.containsKey(patternTask)) {
                if (newPort != "") {
                    String[] conn = new String[4];
                    conn[0] = patternPort;
                    conn[1] = modelTask;
                    conn[2] = modelPort;
                    conn[3] = newPort;
                    _portsConnection.get(patternTask).add(conn);
                } else {
                    String[] conn = new String[3];
                    conn[0] = patternPort;
                    conn[1] = modelTask;
                    conn[2] = modelPort;
                    _portsConnection.get(patternTask).add(conn);
                }
            } else {
                List<String[]> portConnec = new ArrayList<String[]>();
                if (newPort != "") {
                    String[] conn = new String[4];
                    conn[0] = patternPort;
                    conn[1] = modelTask;
                    conn[2] = modelPort;
                    conn[3] = newPort;
                    portConnec.add(conn);
                } else {
                    String[] conn = new String[3];
                    conn[0] = patternPort;
                    conn[1] = modelTask;
                    conn[2] = modelPort;
                    portConnec.add(conn);
                }
                _portsConnection.put(patternTask, portConnec);
            }
        }
        return _portsConnection;
    }
   
}
