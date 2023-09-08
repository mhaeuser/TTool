package tmltranslator.patternhandling;
/**
 * Class PatternConfiguration
 * 
 * Creation: 07/09/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 07/09/2023
 */
 
import java.util.*;
import java.util.Map.Entry;

import myutil.TraceManager;
import ui.window.JDialogPatternGeneration;

 
public class PatternConfiguration {

    LinkedHashMap<String, List<String[]>> portsConnection; 
    LinkedHashMap<String, String> clonedTasks; 
    LinkedHashMap<String, List<Entry<String, String>>> portsConfig; 
    LinkedHashMap<String, Entry<String, String>> tasksMapping;
    LinkedHashMap<String, List<String[]>> channelsMapping;

    public PatternConfiguration(LinkedHashMap<String, List<String[]>> portsConnection, LinkedHashMap<String, String> clonedTasks, LinkedHashMap<String, List<Entry<String, String>>> portsConfig, LinkedHashMap<String, Entry<String, String>> tasksMapping, LinkedHashMap<String,List<String[]>> channelsMapping) {
        this.portsConnection = portsConnection;
        this.clonedTasks = clonedTasks;
        this.portsConfig = portsConfig;
        this.tasksMapping = tasksMapping;
        this.channelsMapping = channelsMapping;
    }

    public PatternConfiguration() {
        this.portsConnection = new LinkedHashMap<String, List<String[]>>();
        this.clonedTasks = new LinkedHashMap<String, String>();
        this.portsConfig = new LinkedHashMap<String, List<Entry<String, String>>>();
        this.tasksMapping = new LinkedHashMap<String, Entry<String, String>>();
        this.channelsMapping = new LinkedHashMap<String, List<String[]>>();
    }

    public LinkedHashMap<String,List<String[]>> getPortsConnection() {
        return portsConnection;
    }

    public LinkedHashMap<String, String> getClonedTasks() {
        return clonedTasks;
    }

    public LinkedHashMap<String, List<Entry<String, String>>> getPortsConfig() {
        return portsConfig;
    }

    public LinkedHashMap<String, Entry<String, String>> getTasksMapping() {
        return tasksMapping;
    }

    public LinkedHashMap<String,List<String[]>> getChannelsMapping() {
        return channelsMapping;
    }

    public void setPortsConnection(LinkedHashMap<String,List<String[]>> _portsConnection) {
        portsConnection = _portsConnection;
    }

    public void setClonedTasks(LinkedHashMap<String, String> _clonedTasks) {
        clonedTasks = _clonedTasks;
    }

    public void setPortsConfig(LinkedHashMap<String, List<Entry<String, String>>> _portsConfig) {
        portsConfig = _portsConfig;
    }

    public void setTasksMapping(LinkedHashMap<String, Entry<String, String>> _tasksMapping) {
        tasksMapping = _tasksMapping;
    }

    public void setChannelsMapping(LinkedHashMap<String,List<String[]>> _channelsMapping) {
        channelsMapping = _channelsMapping;
    }

    public void loadConnectedPorts(Vector<String> connectedPortsFull) {
        for (String connectedPort : connectedPortsFull) {
            String[] splitO = connectedPort.split(JDialogPatternGeneration.TASK_CHANNEL_SEPARATOR, 2);
            String patternTaskName = splitO[0];
            String[] splitCom = splitO[1].split(JDialogPatternGeneration.PORT_CONNECTION_SEPARATOR, 2);
            String patternTaskPortName = splitCom[0];
            String[] splitComModel = splitCom[1].split(JDialogPatternGeneration.TASK_CHANNEL_SEPARATOR, 2);
            String modelTaskName = splitComModel[0];
            String[] splitComModelPort = splitComModel[1].split(JDialogPatternGeneration.NEW_PORT_OPTION, 2);
            String modelTaskPortName = splitComModelPort[0];
            if (portsConnection.containsKey(patternTaskName)) {
                if (splitComModel[1].equals(modelTaskPortName)) {
                    String[] portConnecMap = new String[3];
                    portConnecMap[0] = patternTaskPortName;
                    portConnecMap[1] = modelTaskName;
                    portConnecMap[2] = modelTaskPortName;
                    portsConnection.get(patternTaskName).add(portConnecMap);
                } else {
                    String[] portConnecMap = new String[4];
                    portConnecMap[0] = patternTaskPortName;
                    portConnecMap[1] = modelTaskName;
                    portConnecMap[2] = modelTaskPortName;
                    portConnecMap[3] = JDialogPatternGeneration.NEW_PORT_OPTION;
                    portsConnection.get(patternTaskName).add(portConnecMap);
                }
            } else {
                if (splitComModel[1].equals(modelTaskPortName)) {
                    String[] portConnecMap = new String[3];
                    portConnecMap[0] = patternTaskPortName;
                    portConnecMap[1] = modelTaskName;
                    portConnecMap[2] = modelTaskPortName;
                    ArrayList<String[]> portConnecMapList = new ArrayList<String[]>();
                    portConnecMapList.add(portConnecMap);
                    portsConnection.put(patternTaskName, portConnecMapList);
                } else {
                    String[] portConnecMap = new String[4];
                    portConnecMap[0] = patternTaskPortName;
                    portConnecMap[1] = modelTaskName;
                    portConnecMap[2] = modelTaskPortName;
                    portConnecMap[3] = JDialogPatternGeneration.NEW_PORT_OPTION;
                    ArrayList<String[]> portConnecMapList = new ArrayList<String[]>();
                    portConnecMapList.add(portConnecMap);
                    portsConnection.put(patternTaskName, portConnecMapList);
                }
            }
        }
    }

    public void loadPortsConfig(Vector<String> portsConfigFull) {
        for (String configuredPort : portsConfigFull) {
            String[] configuredPortSplit = configuredPort.split(JDialogPatternGeneration.TASK_CHANNEL_SEPARATOR);
            String[] portConfigRemoveSplit = configuredPortSplit[1].split(JDialogPatternGeneration.PORT_CONFIGURATION_SEPARATOR + JDialogPatternGeneration.REMOVE_PORT_OPTION);
            String[] portConfigMergeSplit = configuredPortSplit[1].split(JDialogPatternGeneration.PORT_CONFIGURATION_SEPARATOR + JDialogPatternGeneration.MERGE_PORT_OPTION);
            if (portsConfig.containsKey(configuredPortSplit[0])) {
                if (!configuredPortSplit[1].equals(portConfigRemoveSplit[0])) {
                    Entry<String,String> portConfig = new AbstractMap.SimpleEntry<>(portConfigRemoveSplit[0], "");
                    portsConfig.get(configuredPortSplit[0]).add(portConfig);
                } else if (portConfigMergeSplit.length > 1) {
                    Entry<String,String> portConfig = new AbstractMap.SimpleEntry<>(portConfigMergeSplit[0], portConfigMergeSplit[1]);
                    portsConfig.get(configuredPortSplit[0]).add(portConfig);
                }
            } else {
                if (!configuredPortSplit[1].equals(portConfigRemoveSplit[0])) {
                    Entry<String,String> portConfig = new AbstractMap.SimpleEntry<>(portConfigRemoveSplit[0], "");
                    portsConfig.put(configuredPortSplit[0], new ArrayList<Entry<String,String>>(Arrays.asList(portConfig)));
                } else if (portConfigMergeSplit.length > 1) {
                    Entry<String,String> portConfig = new AbstractMap.SimpleEntry<>(portConfigMergeSplit[0], portConfigMergeSplit[1]);
                    portsConfig.put(configuredPortSplit[0], new ArrayList<Entry<String,String>>(Arrays.asList(portConfig)));
                }
            }
        }
    }
    
    public void loadMappedTasks(Vector<String> mappedTasks) {
        for (String mappedTask : mappedTasks) {
            String[] mappedTaskSplitSameHw = mappedTask.split(JDialogPatternGeneration.MAP_TASK_IN_SAME_HW_SEPARATOR);
            String[] mappedTaskSplitNewHw = mappedTask.split(JDialogPatternGeneration.MAP_TASK_IN_NEW_HW_SEPARATOR);
            if (mappedTaskSplitSameHw.length > 1) {
                Entry<String,String> taskMap = new AbstractMap.SimpleEntry<>(JDialogPatternGeneration.SAME_HW, mappedTaskSplitSameHw[1]);
                tasksMapping.put(mappedTaskSplitSameHw[0], taskMap);
            } else if (mappedTaskSplitNewHw.length > 1) {
                Entry<String,String> taskMap = new AbstractMap.SimpleEntry<>(JDialogPatternGeneration.NEW_HW, mappedTaskSplitNewHw[1]);
                tasksMapping.put(mappedTaskSplitNewHw[0], taskMap);
            }
        }
    }

    public void loadMappedChannels(Vector<String> mappedChannels) {
        for (String mappedChannel : mappedChannels) {
            String[] mappedChannelSplit= mappedChannel.split(JDialogPatternGeneration.TASK_CHANNEL_SEPARATOR, 2);
            String[] mappedChannelSplitSameMem = mappedChannelSplit[1].split(JDialogPatternGeneration.MAP_CHANNEL_IN_SAME_MEMORY_SEPARATOR);
            String[] mappedChannelSplitNewMem = mappedChannelSplit[1].split(JDialogPatternGeneration.MAP_CHANNEL_IN_NEW_MEMORY_SEPARATOR);
            if (channelsMapping.containsKey(mappedChannelSplit[0])) {
                if (mappedChannelSplitSameMem.length > 1) {
                    String[] channelMap = new String[4];
                    String[] mappedChannelSplitSameMemTaskChannel = mappedChannelSplitSameMem[1].split(JDialogPatternGeneration.TASK_CHANNEL_SEPARATOR, 2);
                    channelMap[0] = JDialogPatternGeneration.SAME_MEMORY;
                    channelMap[1] = mappedChannelSplitSameMem[0];
                    channelMap[2] = mappedChannelSplitSameMemTaskChannel[0];
                    channelMap[3] = mappedChannelSplitSameMemTaskChannel[1];
                    channelsMapping.get(mappedChannelSplit[0]).add(channelMap);
                } else if (mappedChannelSplitNewMem.length > 1) {
                    String[] channelMap = new String[3];
                    channelMap[0] = JDialogPatternGeneration.NEW_MEMORY;
                    channelMap[1] = mappedChannelSplitNewMem[0];
                    channelMap[2] = mappedChannelSplitNewMem[1];
                    channelsMapping.get(mappedChannelSplit[0]).add(channelMap);
                }
            } else {
                if (mappedChannelSplitSameMem.length > 1) {
                    String[] channelMap = new String[4];
                    String[] mappedChannelSplitSameMemTaskChannel = mappedChannelSplitSameMem[1].split(JDialogPatternGeneration.TASK_CHANNEL_SEPARATOR, 2);
                    channelMap[0] = JDialogPatternGeneration.SAME_MEMORY;
                    channelMap[1] = mappedChannelSplitSameMem[0];
                    channelMap[2] = mappedChannelSplitSameMemTaskChannel[0];
                    channelMap[3] = mappedChannelSplitSameMemTaskChannel[1];
                    ArrayList<String[]> channelMapList = new ArrayList<String[]>();
                    channelMapList.add(channelMap);
                    channelsMapping.put(mappedChannelSplit[0], channelMapList);
                } else if (mappedChannelSplitNewMem.length > 1) {
                    String[] channelMap = new String[3];
                    channelMap[0] = JDialogPatternGeneration.NEW_MEMORY;
                    channelMap[1] = mappedChannelSplitNewMem[0];
                    channelMap[2] = mappedChannelSplitNewMem[1];
                    ArrayList<String[]> channelMapList = new ArrayList<String[]>();
                    channelMapList.add(channelMap);
                    channelsMapping.put(mappedChannelSplit[0], channelMapList);
                }
            }
        }
    }

    public void loadClonedTasks(Vector<String> _clonedTasks) {
        for (String clonedTask : _clonedTasks) {
            String[] clonedTaskSplit = clonedTask.split(JDialogPatternGeneration.CLONE_TASK_SEPARATOR);
            if (clonedTaskSplit.length > 1) {
                clonedTasks.put(clonedTaskSplit[0], clonedTaskSplit[1]);
            }
        }
    }
}
