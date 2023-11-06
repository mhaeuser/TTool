package tmltranslator.patternhandling;
/**
 * Class MappingPatternTask
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import tmltranslator.*;

 
public class MappingPatternTask {

    public final static String MAP_TASK_IN_SAME_HW_SEPARATOR = " mapped in the same HW as ";
    public final static String MAP_TASK_IN_NEW_HW_SEPARATOR = " mapped in a new HW linked to ";
    public final static int ORIGIN_PATTERN = 0;
    public final static int ORIGIN_CLONE = 1;
    public final static int ORIGIN_MODEL = 2;

    String taskToMapName;
    String sameHwAs;
    int sameHwAsOrigin;
    String busNameForNewHw;
    int origin;

    public MappingPatternTask(String taskToMapName, int origin) {
        this.taskToMapName = taskToMapName;
        this.origin = origin;
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

    public int getOrigin() {
        return origin;
    }

    public int getSameHwAsOrigin() {
        return sameHwAsOrigin;
    }

    public void setTaskToMapName(String _taskToMapName) {
        taskToMapName = _taskToMapName;
    }

    public void setSameHwAs(String _sameHwAs, int _sameHwAsOrigin) {
        sameHwAs = _sameHwAs;
        sameHwAsOrigin = _sameHwAsOrigin;
        busNameForNewHw = null;
    }

    public void setBusNameForNewHw(String _busNameForNewHw) {
        busNameForNewHw = _busNameForNewHw;
        sameHwAs = null;
        sameHwAsOrigin = -1;
    }

    public void setOrigin(int _origin) {
        origin = _origin;
    }

    public String getStringDisplay() {
        if (sameHwAs != null) {
            return (taskToMapName + MAP_TASK_IN_SAME_HW_SEPARATOR + sameHwAs);
        } else {
            return (taskToMapName + MAP_TASK_IN_NEW_HW_SEPARATOR + busNameForNewHw);
        }
    }
    
}
