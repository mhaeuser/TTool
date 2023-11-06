package tmltranslator.patternhandling;
/**
 * Class PatternConfig2Json
 * Transform a Pattern configuration to Json file and vice versa
 * Creation: 05/09/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 05/09/2023
 */
 
import tmltranslator.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

 
public class PatternConfig2Json {
    public final static String TASK_MAPPING_ORIGIN = "Origin";
    public final static String TASK_MAPPING_ORIGIN_PATTERN = "Pattern";
    public final static String TASK_MAPPING_ORIGIN_CLONE = "Clone";
    public final static String TASK_MAPPING_ORIGIN_MODEL = "Model";
    public final static String TASK_MAPPING = "Task_Mapping";
    public final static String TASK_TO_MAP = "Task_To_Map";
    public final static String TASK_MAPPED_IN_SAME_HW_AS = "Task_Mapped_In_Same_HW_As";
    public final static String TASK_MAPPED_IN_SAME_HW_AS_ORIGIN = "Origin_Task_Mapped_In_Same_HW_As";
    public final static String TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS = "Task_Mapped_In_New_HW";

    public final static String CHANNEL_MAPPING_ORIGIN = "Origin";
    public final static String CHANNEL_MAPPING_ORIGIN_PATTERN = "Pattern";
    public final static String CHANNEL_MAPPING_ORIGIN_CLONE = "Clone";
    public final static String CHANNEL_MAPPING_ORIGIN_MODEL = "Model";
    public final static String CHANNEL_MAPPING = "Channel_Mapping";
    public final static String TASK_OF_CHANNEL_TO_MAP = "Task_Of_Channel_To_Map";
    public final static String CHANNEL_TO_MAP = "Channel_To_Map";
    public final static String CHANNEL_MAPPED_IN_SAME_MEM_AS = "Channel_Mapped_In_Same_Mem_As";
    public final static String TASK_OF_CHANNEL_SAME_MEM = "Task_Of_Channel_Same_Mem_As";
    public final static String CHANNEL_MAPPED_IN_SAME_MEM_AS_ORIGIN = "Origin_Channel_Mapped_In_Same_Mem_As";
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

    JSONArray addClonedTasksInJsonFile(List<PatternCloneTask> _clonedTasks) {
        JSONArray ja = new JSONArray();
        try {
            for (PatternCloneTask cloneTask : _clonedTasks) {
                JSONObject jo = new JSONObject();
                jo.put(CLONE_OF_TASK, cloneTask.getTaskToClone());
                jo.put(CLONED_TASK, cloneTask.getClonedTask());
                ja.put(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    List<PatternCloneTask> getClonedTasksFromJsonFile(JSONArray ja) {
        List<PatternCloneTask> _clonedTasks = new ArrayList<PatternCloneTask>();
        for (int j = 0; j < ja.length(); j++) {
            String cloneOfTask = ja.getJSONObject(j).getString(CLONE_OF_TASK);
            String clonedTask = ja.getJSONObject(j).getString(CLONED_TASK);
            PatternCloneTask cloneTask = new PatternCloneTask(clonedTask, cloneOfTask);
            _clonedTasks.add(cloneTask);
        }
        return _clonedTasks;
    }

    JSONArray addPortsConfigurationInJsonFile(List<PatternPortsConfig> _portsConfig) {
        JSONArray ja = new JSONArray();
        try {
            for (PatternPortsConfig portConfig : _portsConfig) {
                JSONObject jo = new JSONObject();
                jo.put(CHANNEL_TO_CONFIG, portConfig.getChannelToConfig());
                jo.put(TASK_OF_CHANNEL_TO_CONFIG, portConfig.getTaskOfChannelToConfig());
                if (portConfig.isChannelToRemove()) {
                    jo.put(CHANNEL_TO_REMOVE, "true");
                } else {
                    jo.put(CHANNEL_TO_MERGE_WITH, portConfig.getMergeWith());
                }
                ja.put(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    List<PatternPortsConfig> getPortsConfigurationFromJsonFile(JSONArray ja) {
        List<PatternPortsConfig> _portsConfig = new ArrayList<PatternPortsConfig>();
        for (int j = 0; j < ja.length(); j++) {
            String taskOfChToConfig = ja.getJSONObject(j).getString(TASK_OF_CHANNEL_TO_CONFIG);
            String chToConfig = ja.getJSONObject(j).getString(CHANNEL_TO_CONFIG);
            PatternPortsConfig portConfig = new PatternPortsConfig(taskOfChToConfig, chToConfig);
            if (ja.getJSONObject(j).has(CHANNEL_TO_MERGE_WITH)) {
                portConfig.setMergeWith(ja.getJSONObject(j).getString(CHANNEL_TO_MERGE_WITH));
            }else if (ja.getJSONObject(j).has(CHANNEL_TO_REMOVE)) {
                portConfig.setIsChannelToRemove(true);
            }
            _portsConfig.add(portConfig);
        }
        return _portsConfig;
    }

    JSONArray addChannelsMappingInJsonFile(List<MappingPatternChannel> _channelsMapping) {
        JSONArray ja = new JSONArray();
        try {
            for (MappingPatternChannel channelMapping : _channelsMapping) {
                JSONObject jo = new JSONObject();
                jo.put(CHANNEL_TO_MAP, channelMapping.getChannelToMapName());
                jo.put(TASK_OF_CHANNEL_TO_MAP, channelMapping.getTaskOfChannelToMap());
                if (channelMapping.getOrigin() == MappingPatternChannel.ORIGIN_CLONE) {
                    jo.put(CHANNEL_MAPPING_ORIGIN, CHANNEL_MAPPING_ORIGIN_CLONE);
                } else if (channelMapping.getOrigin() == MappingPatternChannel.ORIGIN_PATTERN) {
                    jo.put(CHANNEL_MAPPING_ORIGIN, CHANNEL_MAPPING_ORIGIN_PATTERN);
                } else if (channelMapping.getOrigin() == MappingPatternChannel.ORIGIN_MODEL) {
                    jo.put(CHANNEL_MAPPING_ORIGIN, CHANNEL_MAPPING_ORIGIN_MODEL);
                }
                if (channelMapping.getChannelInSameMemAs() != null && channelMapping.getTaskOfChannelInSameMem() != null) {
                    jo.put(CHANNEL_MAPPED_IN_SAME_MEM_AS, channelMapping.getChannelInSameMemAs());
                    jo.put(TASK_OF_CHANNEL_SAME_MEM, channelMapping.getTaskOfChannelInSameMem());
                    if (channelMapping.getSameMemAsOrigin() == MappingPatternChannel.ORIGIN_CLONE) {
                        jo.put(CHANNEL_MAPPED_IN_SAME_MEM_AS_ORIGIN, CHANNEL_MAPPING_ORIGIN_CLONE);
                    } else if (channelMapping.getSameMemAsOrigin() == MappingPatternChannel.ORIGIN_PATTERN) {
                        jo.put(CHANNEL_MAPPED_IN_SAME_MEM_AS_ORIGIN, CHANNEL_MAPPING_ORIGIN_PATTERN);
                    } else if (channelMapping.getSameMemAsOrigin() == MappingPatternChannel.ORIGIN_MODEL) {
                        jo.put(CHANNEL_MAPPED_IN_SAME_MEM_AS_ORIGIN, CHANNEL_MAPPING_ORIGIN_MODEL);
                    }
                }
                if (channelMapping.getBusNameForNewMem() != null) {
                    jo.put(CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS, channelMapping.getBusNameForNewMem());
                }
                ja.put(jo);
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
                    jo.put(PatternCreation.NAME, attributeTask.getName());
                    jo.put(PatternCreation.TYPE, attributeTask.getType());
                    jo.put(PatternCreation.VALUE, attributeTask.getValue());
                    ja.put(jo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    JSONArray addChannelsWithSecurityInJsonFile(List<PatternChannelWithSecurity> _channelsWithSec) {
        JSONArray ja = new JSONArray();
        try {
            for (PatternChannelWithSecurity channelWithSec : _channelsWithSec) {
                JSONObject jo = new JSONObject();
                jo.put(TASK_CHANNEL_WITH_SECURITY_NAME, channelWithSec.getChannelTaskName());
                jo.put(PatternCreation.NAME, channelWithSec.getChannelName());
                jo.put(PatternCreation.MODE, channelWithSec.getChannelMode());
                if (channelWithSec.isConfidential()) {
                    jo.put(PatternCreation.CONFIDENTIALITY, PatternCreation.WITH_CONFIDENTIALITY);
                } else {
                    jo.put(PatternCreation.CONFIDENTIALITY, PatternCreation.WITHOUT_CONFIDENTIALITY);
                }
                if (channelWithSec.getAuthenticity() == PatternChannelWithSecurity.NO_AUTHENTICITY) {
                    jo.put(PatternCreation.AUTHENTICITY, PatternCreation.WITHOUT_AUTHENTICITY);
                } else if (channelWithSec.getAuthenticity() == PatternChannelWithSecurity.WEAK_AUTHENTICITY) {
                    jo.put(PatternCreation.AUTHENTICITY, PatternCreation.WEAK_AUTHENTICITY);
                } else if (channelWithSec.getAuthenticity() == PatternChannelWithSecurity.STRONG_AUTHENTICITY) {
                    jo.put(PatternCreation.AUTHENTICITY, PatternCreation.STRONG_AUTHENTICITY);
                }
                ja.put(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    List<PatternChannelWithSecurity> getChannelsWithSecurityFromJsonFile(JSONArray ja) {
        List<PatternChannelWithSecurity> _channelsWithSec = new ArrayList<PatternChannelWithSecurity>();
        for (int j = 0; j < ja.length(); j++) {
            String taskName = ja.getJSONObject(j).getString(TASK_CHANNEL_WITH_SECURITY_NAME);
            String channelName = ja.getJSONObject(j).getString(PatternCreation.NAME);
            String channelMode = ja.getJSONObject(j).getString(PatternCreation.MODE);
            String channelConf = ja.getJSONObject(j).getString(PatternCreation.CONFIDENTIALITY);
            String channelAuth = ja.getJSONObject(j).getString(PatternCreation.AUTHENTICITY);
            PatternChannelWithSecurity channelWithSecurity = new PatternChannelWithSecurity(taskName, channelName, channelMode);
            if (channelConf.toLowerCase().equals(PatternCreation.WITHOUT_CONFIDENTIALITY.toLowerCase())) {
                channelWithSecurity.setIsConfidential(false);
            } else if (channelConf.toLowerCase().equals(PatternCreation.WITH_CONFIDENTIALITY.toLowerCase())) {
                channelWithSecurity.setIsConfidential(true);
            }
            if (channelAuth.toLowerCase().equals(PatternCreation.WITHOUT_AUTHENTICITY.toLowerCase())) {
                channelWithSecurity.setAuthenticity(PatternChannelWithSecurity.NO_AUTHENTICITY);
            } else if (channelAuth.toLowerCase().equals(PatternCreation.WEAK_AUTHENTICITY.toLowerCase())) {
                channelWithSecurity.setAuthenticity(PatternChannelWithSecurity.WEAK_AUTHENTICITY);
            } else if (channelAuth.toLowerCase().equals(PatternCreation.STRONG_AUTHENTICITY.toLowerCase())) {
                channelWithSecurity.setAuthenticity(PatternChannelWithSecurity.STRONG_AUTHENTICITY);
            }
            
            _channelsWithSec.add(channelWithSecurity);
        }
        return _channelsWithSec;
    }

    LinkedHashMap<String, List<AttributeTaskJsonFile>> getUpdatedPatternAttributesFromJsonFile(JSONArray ja) {
        LinkedHashMap<String, List<AttributeTaskJsonFile>> _updatedPatternAttributes = new LinkedHashMap<String, List<AttributeTaskJsonFile>>();
        for (int j = 0; j < ja.length(); j++) {
            String taskName = ja.getJSONObject(j).getString(TASK_ATTRIBUTE_NAME);
            String attributeName = ja.getJSONObject(j).getString(PatternCreation.NAME);
            String attributeType = ja.getJSONObject(j).getString(PatternCreation.TYPE);
            String attributeValue = ja.getJSONObject(j).getString(PatternCreation.VALUE);
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

    List<MappingPatternChannel> getChannelsMappingFromJsonFile(JSONArray ja) {
        List<MappingPatternChannel> _channelsMapping = new ArrayList<MappingPatternChannel>();
        for (int j = 0; j < ja.length(); j++) {
            String channelToMap = ja.getJSONObject(j).getString(CHANNEL_TO_MAP);
            String taskOfChannelToMap = ja.getJSONObject(j).getString(TASK_OF_CHANNEL_TO_MAP);
            int originChannel = -1;
            String channelToMapOrigin = ja.getJSONObject(j).getString(CHANNEL_MAPPING_ORIGIN);
            if (channelToMapOrigin.equals(CHANNEL_MAPPING_ORIGIN_CLONE)) {
                originChannel = MappingPatternChannel.ORIGIN_CLONE;
            } else if (channelToMapOrigin.equals(CHANNEL_MAPPING_ORIGIN_PATTERN)) {
                originChannel = MappingPatternChannel.ORIGIN_PATTERN;
            } else if (channelToMapOrigin.equals(CHANNEL_MAPPING_ORIGIN_MODEL)) {
                originChannel = MappingPatternChannel.ORIGIN_MODEL;
            }
            MappingPatternChannel mappingPatternChannel = new MappingPatternChannel(taskOfChannelToMap, channelToMap, originChannel);

            if (ja.getJSONObject(j).has(CHANNEL_MAPPED_IN_SAME_MEM_AS) && ja.getJSONObject(j).has(TASK_OF_CHANNEL_SAME_MEM)) {
                int sameMemAsOrigin = -1;
                String sameChannelMemAsOrigin =  ja.getJSONObject(j).getString(CHANNEL_MAPPED_IN_SAME_MEM_AS_ORIGIN);
                if (sameChannelMemAsOrigin.equals(CHANNEL_MAPPING_ORIGIN_CLONE)) {
                    sameMemAsOrigin = MappingPatternChannel.ORIGIN_CLONE;
                } else if (sameChannelMemAsOrigin.equals(TASK_MAPPING_ORIGIN_PATTERN)) {
                    sameMemAsOrigin = MappingPatternChannel.ORIGIN_PATTERN;
                } else if (sameChannelMemAsOrigin.equals(TASK_MAPPING_ORIGIN_MODEL)) {
                    sameMemAsOrigin = MappingPatternChannel.ORIGIN_MODEL;
                }
                mappingPatternChannel.setTaskAndChannelInSameMem(ja.getJSONObject(j).getString(TASK_OF_CHANNEL_SAME_MEM), ja.getJSONObject(j).getString(CHANNEL_MAPPED_IN_SAME_MEM_AS), sameMemAsOrigin);
            }else if (ja.getJSONObject(j).has(CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS)) {
                mappingPatternChannel.setBusNameForNewMem(ja.getJSONObject(j).getString(CHANNEL_MAPPED_IN_NEW_MEM_CONNECTED_TO_BUS));
            }
            _channelsMapping.add(mappingPatternChannel);
            
        }
        return _channelsMapping;
    }

    JSONArray addTasksMappingInJsonFile(List<MappingPatternTask> _tasksMapping) {
        JSONArray ja = new JSONArray();
        try {
            for (MappingPatternTask taskMapping : _tasksMapping) {
                JSONObject jo = new JSONObject();
                jo.put(TASK_TO_MAP, taskMapping.getTaskToMapName());
                if (taskMapping.getOrigin() == MappingPatternTask.ORIGIN_CLONE) {
                    jo.put(TASK_MAPPING_ORIGIN, TASK_MAPPING_ORIGIN_CLONE);
                } else if (taskMapping.getOrigin() == MappingPatternTask.ORIGIN_PATTERN) {
                    jo.put(TASK_MAPPING_ORIGIN, TASK_MAPPING_ORIGIN_PATTERN);
                }
                if (taskMapping.getSameHwAs() != null) {
                    jo.put(TASK_MAPPED_IN_SAME_HW_AS, taskMapping.getSameHwAs());
                    if (taskMapping.getSameHwAsOrigin() == MappingPatternTask.ORIGIN_CLONE) {
                        jo.put(TASK_MAPPED_IN_SAME_HW_AS_ORIGIN, TASK_MAPPING_ORIGIN_CLONE);
                    } else if (taskMapping.getSameHwAsOrigin() == MappingPatternTask.ORIGIN_PATTERN) {
                        jo.put(TASK_MAPPED_IN_SAME_HW_AS_ORIGIN, TASK_MAPPING_ORIGIN_PATTERN);
                    } else if (taskMapping.getSameHwAsOrigin() == MappingPatternTask.ORIGIN_MODEL) {
                        jo.put(TASK_MAPPED_IN_SAME_HW_AS_ORIGIN, TASK_MAPPING_ORIGIN_MODEL);
                    }
                    
                } else if (taskMapping.getBusNameForNewHw() != null) {
                    jo.put(TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS, taskMapping.getBusNameForNewHw());
                }
                ja.put(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    List<MappingPatternTask> getTasksMappingFromJsonFile(JSONArray ja) {
        List<MappingPatternTask> _tasksMapping = new ArrayList<MappingPatternTask>();
        for (int j = 0; j < ja.length(); j++) {
            String taskToMap = ja.getJSONObject(j).getString(TASK_TO_MAP);
            String taskToMapOrigin = ja.getJSONObject(j).getString(TASK_MAPPING_ORIGIN);
            int origin = -1;
            if (taskToMapOrigin.equals(TASK_MAPPING_ORIGIN_CLONE)) {
                origin = MappingPatternTask.ORIGIN_CLONE;
            } else if (taskToMapOrigin.equals(TASK_MAPPING_ORIGIN_PATTERN)) {
                origin = MappingPatternTask.ORIGIN_PATTERN;
            }
            MappingPatternTask mappingPatternTask = new MappingPatternTask(taskToMap, origin);
            if (ja.getJSONObject(j).has(TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS)) {
                mappingPatternTask.setBusNameForNewHw(ja.getJSONObject(j).getString(TASK_MAPPED_IN_NEW_HW_CONNECTED_TO_BUS));
            } else if (ja.getJSONObject(j).has(TASK_MAPPED_IN_SAME_HW_AS)) {
                int sameHwAsOrigin = -1;
                String sameTaskHwAsOrigin = ja.getJSONObject(j).getString(TASK_MAPPED_IN_SAME_HW_AS_ORIGIN);
                if (sameTaskHwAsOrigin.equals(TASK_MAPPING_ORIGIN_CLONE)) {
                    sameHwAsOrigin = MappingPatternTask.ORIGIN_CLONE;
                } else if (sameTaskHwAsOrigin.equals(TASK_MAPPING_ORIGIN_PATTERN)) {
                    sameHwAsOrigin = MappingPatternTask.ORIGIN_PATTERN;
                } else if (sameTaskHwAsOrigin.equals(TASK_MAPPING_ORIGIN_MODEL)) {
                    sameHwAsOrigin = MappingPatternTask.ORIGIN_MODEL;
                }
                mappingPatternTask.setSameHwAs(ja.getJSONObject(j).getString(TASK_MAPPED_IN_SAME_HW_AS), sameHwAsOrigin);
            }
            _tasksMapping.add(mappingPatternTask);
        }
        return _tasksMapping;
    }

    JSONArray addConnectionInJsonFile(List<PatternConnection> _portsConnection) {
        JSONArray ja = new JSONArray();
        try {
            for (PatternConnection portsConnection : _portsConnection) {
                JSONObject jo = new JSONObject();
                jo.put(PATTERN_TASK, portsConnection.getPatternTaskName());
                jo.put(PATTERN_PORT, portsConnection.getPatternChannel());
                jo.put(MODEL_TASK, portsConnection.getModelTaskName());
                jo.put(MODEL_PORT, portsConnection.getModelChannelName());
                
                if (portsConnection.isNewPort()) {
                    jo.put(NEW_PORT, "true");
                } else {
                    jo.put(NEW_PORT, "false");
                }
                ja.put(jo);
                
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    List<PatternConnection> getConnectionFromJsonFile(JSONArray ja) {
        List<PatternConnection> _portsConnection = new ArrayList<PatternConnection>();
        for (int j = 0; j < ja.length(); j++) {
            String patternTask = ja.getJSONObject(j).getString(PATTERN_TASK);
            String patternPort = ja.getJSONObject(j).getString(PATTERN_PORT);
            String modelTask = ja.getJSONObject(j).getString(MODEL_TASK);
            String modelPort = ja.getJSONObject(j).getString(MODEL_PORT);
            String newPort =  ja.getJSONObject(j).getString(NEW_PORT);
            boolean isNewPort = Boolean.parseBoolean(newPort);
            PatternConnection patternConnection = new PatternConnection(patternTask, patternPort, modelTask, modelPort, isNewPort);
            _portsConnection.add(patternConnection);
        }
        return _portsConnection;
    }
   
}
