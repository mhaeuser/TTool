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

 
public class PatternPortsConfig {
    String taskOfChannelToConfig;
    String channelToConfig;
    Boolean isChannelToRemove;
    String mergeWith;

    public PatternPortsConfig(String taskOfChannelToConfig, String channelToConfig, Boolean isChannelToRemove, String mergeWith) {
        this.taskOfChannelToConfig = taskOfChannelToConfig;
        this.channelToConfig = channelToConfig;
        this.isChannelToRemove = isChannelToRemove;
        this.mergeWith = mergeWith;
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
    }

    public void setMergeWith(String _mergeWith) {
        mergeWith = _mergeWith;
    }
    
}
