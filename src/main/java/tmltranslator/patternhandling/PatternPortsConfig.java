package tmltranslator.patternhandling;
/**
 * Class PatternPortsConfig
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

 
public class PatternPortsConfig {
    public final static String REMOVE_PORT_OPTION = " Remove";
    public final static String MERGE_PORT_OPTION = " Merge with ";
    public final static String TASK_CHANNEL_SEPARATOR = "::";

    String taskOfChannelToConfig;
    String channelToConfig;
    Boolean isChannelToRemove;
    String mergeWith;

    public PatternPortsConfig(String taskOfChannelToConfig, String channelToConfig) {
        this.taskOfChannelToConfig = taskOfChannelToConfig;
        this.channelToConfig = channelToConfig;
    }

    public String getTaskOfChannelToConfig() {
        return taskOfChannelToConfig;
    }

    public String getChannelToConfig() {
        return channelToConfig;
    }

    public Boolean isChannelToRemove() {
        return isChannelToRemove;
    }

    public String getMergeWith() {
        return mergeWith;
    }


    public void setTaskOfChannelToConfig(String _taskOfChannelToConfig) {
        taskOfChannelToConfig = _taskOfChannelToConfig;
    }

    public void setChannelToConfig(String _channelToConfig) {
        channelToConfig = _channelToConfig;
    }

    public void setIsChannelToRemove(Boolean _isChannelToRemove) {
        isChannelToRemove = _isChannelToRemove;
        mergeWith = null;
    }

    public void setMergeWith(String _mergeWith) {
        mergeWith = _mergeWith;
        isChannelToRemove = false;
    }
    
    public String getStringDisplay() {
        if (isChannelToRemove) {
            return (taskOfChannelToConfig + TASK_CHANNEL_SEPARATOR + channelToConfig + REMOVE_PORT_OPTION);
        } else {
            return (taskOfChannelToConfig + TASK_CHANNEL_SEPARATOR + channelToConfig + MERGE_PORT_OPTION + mergeWith);
        }
    }

    public String getTaskChannelToConfig() {
        return (taskOfChannelToConfig + TASK_CHANNEL_SEPARATOR + channelToConfig);
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

    public static List<PatternPortsConfig> getPortsToConfig(LinkedHashMap<String, TaskPorts> _portsTaskOfModelAll, LinkedHashMap<String, TaskPorts> _portsTaskOfModelLeft, List<String> clonedTasks, TMLModeling<?> tmlmodel) {
        List<PatternPortsConfig> portsToConf = new ArrayList<PatternPortsConfig>();
        for (String task : _portsTaskOfModelLeft.keySet()) {
            if (!clonedTasks.contains(task)) {
                TMLTask tmlTask = tmlmodel.getTMLTaskByName(task);
                TaskPorts  pt = _portsTaskOfModelLeft.get(task);
                if (tmlTask != null) {
                    for (String wc : pt.getWriteChannels()) {
                        for (int i = 0; i < tmlTask.getWriteChannels().size() ; i++) {
                            if (tmlTask.getWriteChannels().get(i).getChannel(0).getName().equals(wc)) {
                                for (String taskAll : _portsTaskOfModelAll.keySet()) {
                                    if (!clonedTasks.contains(taskAll) && _portsTaskOfModelAll.get(taskAll).getReadChannels().contains(wc) && !_portsTaskOfModelLeft.get(taskAll).getReadChannels().contains(wc)) {
                                        PatternPortsConfig patternPortsConfig = new PatternPortsConfig(task, wc);
                                        if (!portsToConf.contains(patternPortsConfig)) {
                                            portsToConf.add(patternPortsConfig);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (String rc : pt.getReadChannels()) {
                        for (int i = 0; i < tmlTask.getReadChannels().size() ; i++) {
                            if (tmlTask.getReadChannels().get(i).getChannel(0).getName().equals(rc)) {
                                for (String taskAll : _portsTaskOfModelAll.keySet()) {
                                    if (!clonedTasks.contains(taskAll) && _portsTaskOfModelAll.get(taskAll).getWriteChannels().contains(rc) && !_portsTaskOfModelLeft.get(taskAll).getWriteChannels().contains(rc)) {
                                        PatternPortsConfig patternPortsConfig = new PatternPortsConfig(task, rc);
                                        if (!portsToConf.contains(patternPortsConfig)) {
                                            portsToConf.add(patternPortsConfig);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (String se : pt.getSendEvents()) {
                        for (int i = 0; i < tmlTask.getSendEvents().size() ; i++) {
                            if (tmlTask.getSendEvents().get(i).getEvent().getName().equals(se)) {
                                for (String taskAll : _portsTaskOfModelAll.keySet()) {
                                    if (!clonedTasks.contains(taskAll) && _portsTaskOfModelAll.get(taskAll).getWaitEvents().contains(se) && !_portsTaskOfModelLeft.get(taskAll).getWaitEvents().contains(se)) {
                                        PatternPortsConfig patternPortsConfig = new PatternPortsConfig(task, se);
                                        if (!portsToConf.contains(patternPortsConfig)) {
                                            portsToConf.add(patternPortsConfig);
                                        }
                                    } 
                                }
                            } 
                        }
                    }
                    for (String we : pt.getWaitEvents()) {
                        for (int i = 0; i < tmlTask.getWaitEvents().size() ; i++) {
                            if (tmlTask.getWaitEvents().get(i).getEvent().getName().equals(we)) {
                                for (String taskAll : _portsTaskOfModelAll.keySet()) {
                                    if (!clonedTasks.contains(taskAll) && _portsTaskOfModelAll.get(taskAll).getSendEvents().contains(we) && !_portsTaskOfModelLeft.get(taskAll).getSendEvents().contains(we)) {
                                        PatternPortsConfig patternPortsConfig = new PatternPortsConfig(task, we);
                                        if (!portsToConf.contains(patternPortsConfig)) {
                                            portsToConf.add(patternPortsConfig);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        return portsToConf;
    }
}
