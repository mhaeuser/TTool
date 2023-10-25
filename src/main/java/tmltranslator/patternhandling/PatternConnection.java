package tmltranslator.patternhandling;
/**
 * Class PatternConnection
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

 
public class PatternConnection {
    String patternTaskname;
    String patternChannel;
    String modelTaskName;
    String modelChannelName;
    Boolean isNewPort;

    public PatternConnection(String patternTaskname, String patternChannel, String modelTaskName, String modelChannelName, Boolean isNewPort) {
        this.patternTaskname = patternTaskname;
        this.patternChannel = patternChannel;
        this.modelTaskName = modelTaskName;
        this.modelChannelName = modelChannelName;
        this.isNewPort = isNewPort;
    }

    public String getPatternTaskname() {
        return patternTaskname;
    }

    public String getPatternChannel() {
        return patternChannel;
    }

    public String getModelTaskName() {
        return modelTaskName;
    }

    public String getModelChannelName() {
        return modelChannelName;
    }

    public Boolean isNewPort() {
        return isNewPort;
    }

    public void setPatternTaskname(String _patternTaskname) {
        patternTaskname = _patternTaskname;
    }

    public void setPatternChannel(String _patternChannel) {
        patternChannel = _patternChannel;
    }

    public void setModelTaskName(String _modelTaskName) {
        modelTaskName = _modelTaskName;
    }

    public void setModelChannelName(String _modelChannelName) {
        modelChannelName = _modelChannelName;
    }

    public void setIsNewPort(Boolean _isNewPort) {
        isNewPort = _isNewPort;
    }
    
}
