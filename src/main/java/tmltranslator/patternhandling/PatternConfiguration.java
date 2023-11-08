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

import myutil.TraceManager;

 
public class PatternConfiguration {

    List<PatternConnection> portsConnection; 
    List<PatternCloneTask> clonedTasks; 
    List<PatternPortsConfig> portsConfig; 
    List<MappingPatternTask> tasksMapping;
    List<MappingPatternChannel> channelsMapping;
    LinkedHashMap<String, List<AttributeTaskJsonFile>> updatedPatternAttributes;
    List<PatternChannelWithSecurity> channelsWithSecurity;

    public PatternConfiguration(List<PatternConnection> portsConnection, List<PatternCloneTask> clonedTasks, List<PatternPortsConfig> portsConfig, List<MappingPatternTask> tasksMapping, List<MappingPatternChannel> channelsMapping, LinkedHashMap<String, List<AttributeTaskJsonFile>> updatedPatternAttributes, List<PatternChannelWithSecurity> channelsWithSecurity) {
        this.portsConnection = portsConnection;
        this.clonedTasks = clonedTasks;
        this.portsConfig = portsConfig;
        this.tasksMapping = tasksMapping;
        this.channelsMapping = channelsMapping;
        this.updatedPatternAttributes = updatedPatternAttributes;
        this.channelsWithSecurity = channelsWithSecurity;
    }

    public PatternConfiguration() {
        this.portsConnection = new ArrayList<PatternConnection>();
        this.clonedTasks = new ArrayList<PatternCloneTask>();
        this.portsConfig = new ArrayList<PatternPortsConfig>();
        this.tasksMapping = new ArrayList<MappingPatternTask>();
        this.channelsMapping = new ArrayList<MappingPatternChannel>();
        this.updatedPatternAttributes = new LinkedHashMap<String, List<AttributeTaskJsonFile>>();
        this.channelsWithSecurity = new ArrayList<PatternChannelWithSecurity>();
    }

    public List<PatternConnection> getPortsConnection() {
        return portsConnection;
    }

    public List<PatternCloneTask> getClonedTasks() {
        return clonedTasks;
    }

    public List<String> getClonedTasksName() {
        List<String> cloneTasksName = new ArrayList<String>();
        for (PatternCloneTask clonedTask : clonedTasks) {
            cloneTasksName.add(clonedTask.getClonedTask());
        }
        return cloneTasksName;
    }

    public List<PatternPortsConfig> getPortsConfig() {
        return portsConfig;
    }

    public List<MappingPatternTask> getTasksMapping() {
        return tasksMapping;
    }

    public List<MappingPatternChannel> getChannelsMapping() {
        return channelsMapping;
    }

    public LinkedHashMap<String, List<AttributeTaskJsonFile>> getUpdatedPatternAttributes() {
        return updatedPatternAttributes;
    }

    public List<PatternChannelWithSecurity> getChannelsWithSecurity() {
        return channelsWithSecurity;
    }

    public void setPortsConnection(List<PatternConnection> _portsConnection) {
        portsConnection = _portsConnection;
    }

    public void setClonedTasks(List<PatternCloneTask> _clonedTasks) {
        clonedTasks = _clonedTasks;
    }

    public void setPortsConfig(List<PatternPortsConfig> _portsConfig) {
        portsConfig = _portsConfig;
    }

    public void setTasksMapping(List<MappingPatternTask> _tasksMapping) {
        tasksMapping = _tasksMapping;
    }

    public void setChannelsMapping(List<MappingPatternChannel> _channelsMapping) {
        channelsMapping = _channelsMapping;
    }

    public void setUpdatedPatternAttributes(LinkedHashMap<String, List<AttributeTaskJsonFile>> _updatedPatternAttributes) {
        updatedPatternAttributes = _updatedPatternAttributes;
    }

    public void addPortsConnection(PatternConnection _portsConnection) {
        portsConnection.add(_portsConnection);
    }

    public void addClonedTasks(PatternCloneTask _clonedTasks) {
        clonedTasks.add(_clonedTasks);
    }

    public void addPortsConfig(PatternPortsConfig _portsConfig) {
        portsConfig.add(_portsConfig);
    }

    public void addTasksMapping(MappingPatternTask _tasksMapping) {
        tasksMapping.add(_tasksMapping);
    }

    public void addChannelsMapping(MappingPatternChannel _channelsMapping) {
        channelsMapping.add(_channelsMapping);
    }

    public void setChannelsWithSecurity(List<PatternChannelWithSecurity> _channelsWithSecurity) {
        channelsWithSecurity = _channelsWithSecurity;
    }

    public void removeClonedTask(PatternCloneTask _clonedTasks) {
        clonedTasks.remove(_clonedTasks);
    }

    /*public void loadConnectedPorts(Vector<String> connectedPortsFull) {
        for (String connectedPort : connectedPortsFull) {
            String[] splitO = connectedPort.split(JDialogPatternHandling.TASK_CHANNEL_SEPARATOR, 2);
            String patternTaskName = splitO[0];
            String[] splitCom = splitO[1].split(JDialogPatternHandling.PORT_CONNECTION_SEPARATOR, 2);
            String patternTaskPortName = splitCom[0];
            String[] splitComModel = splitCom[1].split(JDialogPatternHandling.TASK_CHANNEL_SEPARATOR, 2);
            String modelTaskName = splitComModel[0];
            String[] splitComModelPort = splitComModel[1].split(JDialogPatternHandling.NEW_PORT_OPTION, 2);
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
                    portConnecMap[3] = JDialogPatternHandling.NEW_PORT_OPTION;
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
                    portConnecMap[3] = JDialogPatternHandling.NEW_PORT_OPTION;
                    ArrayList<String[]> portConnecMapList = new ArrayList<String[]>();
                    portConnecMapList.add(portConnecMap);
                    portsConnection.put(patternTaskName, portConnecMapList);
                }
            }
        }
    }

    public void loadPortsConfig(Vector<String> portsConfigFull) {
        for (String configuredPort : portsConfigFull) {
            String[] configuredPortSplit = configuredPort.split(JDialogPatternHandling.TASK_CHANNEL_SEPARATOR);
            String[] portConfigRemoveSplit = configuredPortSplit[1].split(JDialogPatternHandling.PORT_CONFIGURATION_SEPARATOR + JDialogPatternHandling.REMOVE_PORT_OPTION);
            String[] portConfigMergeSplit = configuredPortSplit[1].split(JDialogPatternHandling.PORT_CONFIGURATION_SEPARATOR + JDialogPatternHandling.MERGE_PORT_OPTION);
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
            String[] mappedTaskSplitSameHw = mappedTask.split(JDialogPatternHandling.MAP_TASK_IN_SAME_HW_SEPARATOR);
            String[] mappedTaskSplitNewHw = mappedTask.split(JDialogPatternHandling.MAP_TASK_IN_NEW_HW_SEPARATOR);
            if (mappedTaskSplitSameHw.length > 1) {
                Entry<String,String> taskMap = new AbstractMap.SimpleEntry<>(JDialogPatternHandling.SAME_HW, mappedTaskSplitSameHw[1]);
                tasksMapping.put(mappedTaskSplitSameHw[0], taskMap);
            } else if (mappedTaskSplitNewHw.length > 1) {
                Entry<String,String> taskMap = new AbstractMap.SimpleEntry<>(JDialogPatternHandling.NEW_HW, mappedTaskSplitNewHw[1]);
                tasksMapping.put(mappedTaskSplitNewHw[0], taskMap);
            }
        }
    }

    public void loadMappedChannels(Vector<String> mappedChannels) {
        for (String mappedChannel : mappedChannels) {
            String[] mappedChannelSplit= mappedChannel.split(JDialogPatternHandling.TASK_CHANNEL_SEPARATOR, 2);
            String[] mappedChannelSplitSameMem = mappedChannelSplit[1].split(JDialogPatternHandling.MAP_CHANNEL_IN_SAME_MEMORY_SEPARATOR);
            String[] mappedChannelSplitNewMem = mappedChannelSplit[1].split(JDialogPatternHandling.MAP_CHANNEL_IN_NEW_MEMORY_SEPARATOR);
            if (channelsMapping.containsKey(mappedChannelSplit[0])) {
                if (mappedChannelSplitSameMem.length > 1) {
                    String[] channelMap = new String[4];
                    String[] mappedChannelSplitSameMemTaskChannel = mappedChannelSplitSameMem[1].split(JDialogPatternHandling.TASK_CHANNEL_SEPARATOR, 2);
                    channelMap[0] = JDialogPatternHandling.SAME_MEMORY;
                    channelMap[1] = mappedChannelSplitSameMem[0];
                    channelMap[2] = mappedChannelSplitSameMemTaskChannel[0];
                    channelMap[3] = mappedChannelSplitSameMemTaskChannel[1];
                    channelsMapping.get(mappedChannelSplit[0]).add(channelMap);
                } else if (mappedChannelSplitNewMem.length > 1) {
                    String[] channelMap = new String[3];
                    channelMap[0] = JDialogPatternHandling.NEW_MEMORY;
                    channelMap[1] = mappedChannelSplitNewMem[0];
                    channelMap[2] = mappedChannelSplitNewMem[1];
                    channelsMapping.get(mappedChannelSplit[0]).add(channelMap);
                }
            } else {
                if (mappedChannelSplitSameMem.length > 1) {
                    String[] channelMap = new String[4];
                    String[] mappedChannelSplitSameMemTaskChannel = mappedChannelSplitSameMem[1].split(JDialogPatternHandling.TASK_CHANNEL_SEPARATOR, 2);
                    channelMap[0] = JDialogPatternHandling.SAME_MEMORY;
                    channelMap[1] = mappedChannelSplitSameMem[0];
                    channelMap[2] = mappedChannelSplitSameMemTaskChannel[0];
                    channelMap[3] = mappedChannelSplitSameMemTaskChannel[1];
                    ArrayList<String[]> channelMapList = new ArrayList<String[]>();
                    channelMapList.add(channelMap);
                    channelsMapping.put(mappedChannelSplit[0], channelMapList);
                } else if (mappedChannelSplitNewMem.length > 1) {
                    String[] channelMap = new String[3];
                    channelMap[0] = JDialogPatternHandling.NEW_MEMORY;
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
            String[] clonedTaskSplit = clonedTask.split(JDialogPatternHandling.CLONE_TASK_SEPARATOR);
            if (clonedTaskSplit.length > 1) {
                clonedTasks.put(clonedTaskSplit[0], clonedTaskSplit[1]);
            }
        }
    }*/
}
