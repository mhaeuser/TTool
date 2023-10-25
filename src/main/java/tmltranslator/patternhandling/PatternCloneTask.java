package tmltranslator.patternhandling;
/**
 * Class PatternCloneTask
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

 
public class PatternCloneTask {
    String clonedTask;
    String taskToClone;

    public PatternCloneTask(String clonedTask, String taskToClone) {
        this.clonedTask = clonedTask;
        this.taskToClone = taskToClone;
    }

    public String getClonedTask() {
        return clonedTask;
    }

    public String getSameHwAs() {
        return taskToClone;
    }


    public void setClonedTask(String _clonedTask) {
        clonedTask = _clonedTask;
    }

    public void setTaskToClone(String _taskToClone) {
        taskToClone = _taskToClone;
    }
  
}
