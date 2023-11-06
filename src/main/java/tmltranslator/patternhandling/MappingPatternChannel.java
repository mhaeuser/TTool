package tmltranslator.patternhandling;
/**
 * Class MappingPatternChannel
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import tmltranslator.*;

 
public class MappingPatternChannel {

    public final static String TASK_CHANNEL_SEPARATOR = "::";
    public final static String MAP_CHANNEL_IN_SAME_MEMORY_SEPARATOR = " mapped in the same Memory as ";
    public final static String MAP_CHANNEL_IN_NEW_MEMORY_SEPARATOR = " mapped in a new memory linked to ";
    public final static int ORIGIN_PATTERN = 0;
    public final static int ORIGIN_CLONE = 1;
    public final static int ORIGIN_MODEL = 2;

    String taskOfChannelToMap;
    String channelToMapName;
    int origin;
    String taskOfChannelInSameMem;
    String channelInSameMemAs;
    int sameMemAsOrigin;
    String busNameForNewMem;
    

    public MappingPatternChannel(String taskOfChannelToMap, String channelToMapName, int origin) {
        this.taskOfChannelToMap = taskOfChannelToMap;
        this.channelToMapName = channelToMapName;
        this.origin = origin;
    }

    public String getTaskOfChannelToMap() {
        return taskOfChannelToMap;
    }

    public String getChannelToMapName() {
        return channelToMapName;
    }

    public String getTaskOfChannelInSameMem() {
        return taskOfChannelInSameMem;
    }

    public String getChannelInSameMemAs() {
        return channelInSameMemAs;
    }

    public String getBusNameForNewMem() {
        return busNameForNewMem;
    }

    public int getOrigin() {
        return origin;
    }

    public int getSameMemAsOrigin() {
        return sameMemAsOrigin;
    }

    public void setTaskOfChannelToMap(String _taskOfChannelToMap) {
        taskOfChannelToMap = _taskOfChannelToMap;
    }

    public void setChannelToMapName(String _channelToMapName) {
        channelToMapName = _channelToMapName;
    }

    public void setTaskAndChannelInSameMem(String _taskOfChannelInSameMem, String _channelInSameMemAs, int _sameMemAsOrigin) {
        taskOfChannelInSameMem = _taskOfChannelInSameMem;
        channelInSameMemAs = _channelInSameMemAs;
        sameMemAsOrigin = _sameMemAsOrigin;
        busNameForNewMem = null;
    }

    public void setBusNameForNewMem(String _busNameForNewMem) {
        busNameForNewMem = _busNameForNewMem;
        channelInSameMemAs = null;
        taskOfChannelInSameMem = null;
    }

    public String getStringDisplay() {
        if (channelInSameMemAs != null) {
            return (taskOfChannelToMap + TASK_CHANNEL_SEPARATOR + channelToMapName + MAP_CHANNEL_IN_SAME_MEMORY_SEPARATOR + taskOfChannelInSameMem + TASK_CHANNEL_SEPARATOR + channelInSameMemAs);
        } else {
            return (taskOfChannelToMap + TASK_CHANNEL_SEPARATOR + channelToMapName + MAP_CHANNEL_IN_NEW_MEMORY_SEPARATOR + busNameForNewMem);
        }
    }

    public void setOrigin(int _origin) {
        origin = _origin;
    }

    public String getTaskChannelToMap() {
        return (taskOfChannelToMap + TASK_CHANNEL_SEPARATOR + channelToMapName);
    }

    public String getTaskChannelInSameMem() {
        return (taskOfChannelInSameMem + TASK_CHANNEL_SEPARATOR + channelInSameMemAs);
    }

    public static String getTaskChannel(String task, String channel) {
        return (task + TASK_CHANNEL_SEPARATOR + channel);
    }

    public static String[] seperateTaskAndChannel(String st) {
        String[] split = st.split(TASK_CHANNEL_SEPARATOR);
        String task, channel;
        if (split.length == 2) {
            task = split[0];
            channel = split[1];
            return new String[]{task, channel};
        } else {
            return null;
        }
    }

    public static List<MappingPatternChannel> getChannelsToMap(List<PatternConnection> patternConnectionList, LinkedHashMap<String, TaskPattern> patternTasks, List<PatternCloneTask> patternCloneTasks) {
        List<MappingPatternChannel> mappingPatternChannels = new ArrayList<MappingPatternChannel>();
        List<String> clonedTasks = new ArrayList<String>();
        for (PatternCloneTask clonedTask: patternCloneTasks) {
            clonedTasks.add(clonedTask.getClonedTask());
        }
        for (PatternConnection patternConnection: patternConnectionList) {
            if (!patternConnection.isNewPort() && clonedTasks.contains(patternConnection.getModelTaskName())) {
                MappingPatternChannel mappingPatternChannel = new MappingPatternChannel(patternConnection.getModelTaskName(), patternConnection.getModelChannelName(), ORIGIN_CLONE);
                if (!mappingPatternChannels.contains(mappingPatternChannel)) {
                    mappingPatternChannels.add(mappingPatternChannel);
                }
            }
            if (patternConnection.isANewChannelRequired(patternConnectionList, patternTasks, patternCloneTasks)) {
                if (!patternConnection.isNewPort() && clonedTasks.contains(patternConnection.getModelTaskName())) {
                } else {
                    for (PortTaskJsonFile portTaskJsonFile : patternTasks.get(patternConnection.getPatternTaskName()).getExternalPorts()) {
                        if (portTaskJsonFile.getType().equals(PatternCreation.CHANNEL) && patternConnection.getPatternChannel().equals(portTaskJsonFile.getName())) {
                            MappingPatternChannel mappingPatternChannel = new MappingPatternChannel(patternConnection.getPatternTaskName(), patternConnection.getPatternChannel(), ORIGIN_PATTERN);
                            if (!mappingPatternChannels.contains(mappingPatternChannel)) {
                                mappingPatternChannels.add(mappingPatternChannel);
                            }
                        }
                    }
                }
            }
        }
        for (String st : patternTasks.keySet()) {
            for (PortTaskJsonFile portTaskJsonFile : patternTasks.get(st).getInternalPorts()) {
                if (portTaskJsonFile.getType().equals(PatternCreation.CHANNEL) && portTaskJsonFile.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                    MappingPatternChannel mappingPatternChannel = new MappingPatternChannel(st, portTaskJsonFile.getName(), ORIGIN_PATTERN);
                    if (!mappingPatternChannels.contains(mappingPatternChannel)) {
                        mappingPatternChannels.add(mappingPatternChannel);
                    }
                }
            }
        }

        return mappingPatternChannels;
    }
}
