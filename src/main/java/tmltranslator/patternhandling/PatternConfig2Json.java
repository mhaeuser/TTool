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
import java.nio.file.Files;
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


    String patternConfigurationPath;
    String patternConfigurationName;
    LinkedHashMap<String,List<String[]>> portsConnection;
    LinkedHashMap<String, String> clonedTasks;
    LinkedHashMap<String, List<Entry<String, String>>> portsConfig;
    LinkedHashMap<String, Entry<String, String>> tasksMapping;
    LinkedHashMap<String,List<String[]>> channelsMapping;
    LinkedHashMap<String, TaskPattern> patternTasks;
	
    public PatternConfig2Json(String _patternPath, String _patternName, LinkedHashMap<String,List<String[]>> _portsConnection, LinkedHashMap<String, String> _clonedTasks, LinkedHashMap<String, List<Entry<String, String>>> _portsConfig, LinkedHashMap<String, Entry<String, String>> _tasksMapping, LinkedHashMap<String,List<String[]>> _channelsMapping) {
		this.patternConfigurationPath = _patternPath;
		this.patternConfigurationName = _patternName;
		this.portsConnection = _portsConnection;
		this.clonedTasks = _clonedTasks;
		this.portsConfig = _portsConfig;
		this.tasksMapping = _tasksMapping;
		this.channelsMapping = _channelsMapping;
	}
    
	
    public void patternConfiguration2Json() {
        try {
            FileWriter file = new FileWriter(patternConfigurationPath+"/"+patternConfigurationName);
            JSONObject jsonPatternConfig = new JSONObject();

            jsonPatternConfig.put(CONNECTION, addConnectionInJsonFile(portsConnection));
            jsonPatternConfig.put(TASK_CLONE, addClonedTasksInJsonFile(clonedTasks));
            jsonPatternConfig.put(CHANNEL_CONFIG, addPortsConfigurationInJsonFile(portsConfig));
            jsonPatternConfig.put(TASK_MAPPING, addTasksMappingInJsonFile(tasksMapping));
            jsonPatternConfig.put(CHANNEL_MAPPING, addChannelsMappingInJsonFile(channelsMapping));

            file.write(jsonPatternConfig.toString(1));
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        LinkedHashMap<String, List<Entry<String, String>>>  _portsConfig = new LinkedHashMap<String, List<Entry<String, String>>>();

        for (int j = 0; j < ja.length(); j++) {
            String taskOfChToConfig = ja.getJSONObject(j).getString(TASK_OF_CHANNEL_TO_CONFIG);
            String chToConfig = ja.getJSONObject(j).getString(CHANNEL_TO_CONFIG);
            String chToMergeWith = ja.getJSONObject(j).getString(CHANNEL_TO_MERGE_WITH);
            String chToRemove = ja.getJSONObject(j).getString(CHANNEL_TO_REMOVE);
            if (_portsConfig.containsKey(taskOfChToConfig)) {
                if (chToMergeWith != "") {
                    _portsConfig.get(taskOfChToConfig).add(new AbstractMap.SimpleEntry<String, String> (chToConfig, chToMergeWith));
                } else if (chToRemove != "") {
                    _portsConfig.get(taskOfChToConfig).add(new AbstractMap.SimpleEntry<String, String> (chToConfig, chToRemove));
                }
                
            } else {
                List<Entry<String, String>> portConf = new ArrayList<Entry<String, String>>();
                if (chToMergeWith != "") {
                    portConf.add(new AbstractMap.SimpleEntry<String, String> (chToConfig, chToMergeWith));
                } else if (chToRemove != "") {
                    portConf.add(new AbstractMap.SimpleEntry<String, String> (chToConfig, chToRemove));
                }
                _portsConfig.put(taskOfChToConfig, portConf);
            }
            
        }
        return _portsConfig;
    }

    JSONArray addChannelsMappingInJsonFile(LinkedHashMap<String,List<String[]>> _channelsMapping) {
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
   
}
