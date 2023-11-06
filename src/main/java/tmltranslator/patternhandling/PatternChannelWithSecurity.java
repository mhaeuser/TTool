package tmltranslator.patternhandling;
/**
 * Class PatternChannelWithSecurity
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import tmltranslator.*;

 
public class PatternChannelWithSecurity {
    public static final int NO_AUTHENTICITY = 0; 
    public static final int WEAK_AUTHENTICITY = 1; 
    public static final int STRONG_AUTHENTICITY = 2;

    String channelTaskName;
    String channelName;
    String channelMode;
    Boolean isConf;
    int auth;

    public PatternChannelWithSecurity(String channelTaskName, String channelName, String channelMode) {
        this.channelTaskName = channelTaskName;
        this.channelName = channelName;
        this.channelMode = channelMode;
    }

    public String getChannelTaskName() {
        return channelTaskName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelMode() {
        return channelMode;
    }

    public Boolean isConfidential() {
        return isConf;
    }

    public int getAuthenticity() {
        return auth;
    }

    public void setChannelTaskName(String _channelTaskName) {
        channelTaskName = _channelTaskName;
    }

    public void setChannelName(String _channelName) {
        channelName = _channelName;
    }

    public void setChannelMode(String _channelMode) {
        channelMode = _channelMode;
    }

    public void setIsConfidential(Boolean _isConf) {
        isConf = _isConf;
    }

    public void setAuthenticity(int _auth) {
        auth = _auth;
    }

    
}
