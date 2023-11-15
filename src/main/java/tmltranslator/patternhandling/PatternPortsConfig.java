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

    private String taskOfChannelToConfig;
    private String channelToConfig;
    private Boolean isChannelToRemove;
    private String mergeWith;

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

    public static List<PatternPortsConfig> getPortsLeftToConfig(LinkedHashMap<String, TaskPorts> _portsTaskOfModelAll, LinkedHashMap<String, TaskPorts> _portsTaskOfModelLeft, List<String> _clonedTasks, List<PatternPortsConfig> _configuredPorts, TMLModeling<?> tmlmodel) {
        List<PatternPortsConfig> portsLeftToConf = new ArrayList<PatternPortsConfig>();
        List<PatternPortsConfig> portsToConfig = PatternPortsConfig.getPortsToConfig(_portsTaskOfModelAll, _portsTaskOfModelLeft, _clonedTasks, tmlmodel);
        for (PatternPortsConfig portToConfig : portsToConfig) {
            boolean isToConfig = true;
            for (PatternPortsConfig portConfigured : _configuredPorts) {
                if (portToConfig.getTaskOfChannelToConfig().equals(portConfigured.getTaskOfChannelToConfig()) && portToConfig.getChannelToConfig().equals(portConfigured.getChannelToConfig())) {
                    isToConfig = false;
                    break;
                }
            }
            if (isToConfig) {
                portsLeftToConf.add(portToConfig);
            }
        }
        return portsLeftToConf;
    }

    public static List<String> getPortsCanBeMergedWith(LinkedHashMap<String, TaskPorts> _portsTaskOfModelAll, List<PatternPortsConfig> _allPortsToConfig, String _taskOfPortToConfig) {
        List<String> portsCanBeMergedWith = new ArrayList<String>();
        if (_portsTaskOfModelAll.containsKey(_taskOfPortToConfig)) {
            for (String wr : _portsTaskOfModelAll.get(_taskOfPortToConfig).getWriteChannels()) {
                boolean canBeMergedWith = true;
                for (PatternPortsConfig portToConfig : _allPortsToConfig) {
                    if (portToConfig.getTaskOfChannelToConfig().equals(_taskOfPortToConfig) && portToConfig.getChannelToConfig().equals(wr)) {
                        canBeMergedWith = false;
                        break;
                    }
                }
                if (canBeMergedWith) {
                    portsCanBeMergedWith.add(wr);
                }
            }
            for (String wr : _portsTaskOfModelAll.get(_taskOfPortToConfig).getWriteChannels()) {
                boolean canBeMergedWith = true;
                for (PatternPortsConfig portToConfig : _allPortsToConfig) {
                    if (portToConfig.getTaskOfChannelToConfig().equals(_taskOfPortToConfig) && portToConfig.getChannelToConfig().equals(wr)) {
                        canBeMergedWith = false;
                        break;
                    }
                }
                if (canBeMergedWith) {
                    portsCanBeMergedWith.add(wr);
                }
            }
            for (String rd : _portsTaskOfModelAll.get(_taskOfPortToConfig).getReadChannels()) {
                boolean canBeMergedWith = true;
                for (PatternPortsConfig portToConfig : _allPortsToConfig) {
                    if (portToConfig.getTaskOfChannelToConfig().equals(_taskOfPortToConfig) && portToConfig.getChannelToConfig().equals(rd)) {
                        canBeMergedWith = false;
                        break;
                    }
                }
                if (canBeMergedWith) {
                    portsCanBeMergedWith.add(rd);
                }
            }
            for (String sd : _portsTaskOfModelAll.get(_taskOfPortToConfig).getSendEvents()) {
                boolean canBeMergedWith = true;
                for (PatternPortsConfig portToConfig : _allPortsToConfig) {
                    if (portToConfig.getTaskOfChannelToConfig().equals(_taskOfPortToConfig) && portToConfig.getChannelToConfig().equals(sd)) {
                        canBeMergedWith = false;
                        break;
                    }
                }
                if (canBeMergedWith) {
                    portsCanBeMergedWith.add(sd);
                }
            }
            for (String we : _portsTaskOfModelAll.get(_taskOfPortToConfig).getWaitEvents()) {
                boolean canBeMergedWith = true;
                for (PatternPortsConfig portToConfig : _allPortsToConfig) {
                    if (portToConfig.getTaskOfChannelToConfig().equals(_taskOfPortToConfig) && portToConfig.getChannelToConfig().equals(we)) {
                        canBeMergedWith = false;
                        break;
                    }
                }
                if (canBeMergedWith) {
                    portsCanBeMergedWith.add(we);
                }
            }
        }
        return portsCanBeMergedWith;
    }
}
