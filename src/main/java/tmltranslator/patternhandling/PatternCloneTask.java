package tmltranslator.patternhandling;
/**
 * Class PatternCloneTask
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import java.util.List;

import tmltranslator.*;

 
public class PatternCloneTask {
    public final static String CLONE_TASK_SEPARATOR = " clone of ";

    String clonedTask;
    String taskToClone;

    public PatternCloneTask(String clonedTask, String taskToClone) {
        this.clonedTask = clonedTask;
        this.taskToClone = taskToClone;
    }

    public String getClonedTask() {
        return clonedTask;
    }

    public String getTaskToClone() {
        return taskToClone;
    }


    public void setClonedTask(String _clonedTask) {
        clonedTask = _clonedTask;
    }

    public void setTaskToClone(String _taskToClone) {
        taskToClone = _taskToClone;
    }

    public String getStringDisplay() {
        return (clonedTask + CLONE_TASK_SEPARATOR + taskToClone);
        
    }
  
    public static PatternCloneTask isAClonedTask(List<PatternCloneTask> clonedTasks, String taskName) {
        for (PatternCloneTask patternCloneTask : clonedTasks) {
            if (patternCloneTask.getClonedTask().equals(taskName)) {
                return patternCloneTask;
            }
        }
        return null;
    }
}
