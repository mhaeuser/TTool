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

 
public class MappingPatternTask {
    String taskToMapName;
    String sameHwAs;
    String busNameForNewHw;

    public MappingPatternTask(String taskToMapName, String sameHwAs, String busNameForNewHw) {
        this.taskToMapName = taskToMapName;
        this.sameHwAs = sameHwAs;
        this.busNameForNewHw = busNameForNewHw;
    }

    public String getTaskToMapName() {
        return taskToMapName;
    }

    public String getSameHwAs() {
        return sameHwAs;
    }

    public String getBusNameForNewHw() {
        return busNameForNewHw;
    }


    public void setTaskToMapName(String _taskToMapName) {
        taskToMapName = _taskToMapName;
    }

    public void setSameHwAs(String _sameHwAs) {
        sameHwAs = _sameHwAs;
    }

    public void setBusNameForNewHw(String _busNameForNewHw) {
        busNameForNewHw = _busNameForNewHw;
    }

    
}
