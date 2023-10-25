package tmltranslator.patternhandling;
/**
 * Class MappingPatternTask
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

 
public class MappingPatternChannel {
    String taskOfChannelToMap;
    String channelToMapName;
    String taskOfChannelInSameMem;
    String channelInSameMemAs;
    String busNameForNewMem;

    public MappingPatternChannel(String taskOfChannelToMap, String channelToMapName, String taskOfChannelInSameMem, String channelInSameMemAs, String busNameForNewMem) {
        this.taskOfChannelToMap = taskOfChannelToMap;
        this.channelToMapName = channelToMapName;
        this.taskOfChannelInSameMem = taskOfChannelInSameMem;
        this.channelInSameMemAs = channelInSameMemAs;
        this.busNameForNewMem = busNameForNewMem;
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


    public void setTaskOfChannelToMap(String _taskOfChannelToMap) {
        taskOfChannelToMap = _taskOfChannelToMap;
    }

    public void setChannelToMapName(String _channelToMapName) {
        channelToMapName = _channelToMapName;
    }

    public void setTaskOfChannelInSameMem(String _taskOfChannelInSameMem) {
        taskOfChannelInSameMem = _taskOfChannelInSameMem;
    }

    public void setChannelInSameMemAs(String _channelInSameMemAs) {
        channelInSameMemAs = _channelInSameMemAs;
    }

    public void setBusNameForNewMem(String _busNameForNewMem) {
        busNameForNewMem = _busNameForNewMem;
    }

    
}
