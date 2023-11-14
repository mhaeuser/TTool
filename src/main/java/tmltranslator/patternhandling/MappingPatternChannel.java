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

    public static List<MappingPatternChannel> getChannelsLeftToMap(List<MappingPatternChannel> _mappedChannels, List<MappingPatternChannel> _allChannelsToMap) {
        List<MappingPatternChannel> channelsLeftToMap = new ArrayList<MappingPatternChannel>();
        for (MappingPatternChannel channelToMap : _allChannelsToMap) {
            boolean isLeftToMap = true;
            for (MappingPatternChannel mappedChannel : _mappedChannels) {
                if (channelToMap.getChannelToMapName().equals(mappedChannel.getChannelToMapName()) && channelToMap.getTaskOfChannelToMap().equals(mappedChannel.getTaskOfChannelToMap())) {
                    isLeftToMap = false;
                    break;
                }
            }
            if (isLeftToMap) {
                channelsLeftToMap.add(channelToMap);
            }
        }
        return channelsLeftToMap;
    }
}
