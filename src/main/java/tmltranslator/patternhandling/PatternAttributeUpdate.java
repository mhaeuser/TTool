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

 
public class PatternAttributeUpdate {
    String taskOfAttributeToUpdate;
    String attributeName;
    String newAttributeValue;

    public PatternAttributeUpdate(String taskOfAttributeToUpdate, String attributeName, String newAttributeValue) {
        this.taskOfAttributeToUpdate = taskOfAttributeToUpdate;
        this.attributeName = attributeName;
        this.newAttributeValue = newAttributeValue;
    }

    public String getTaskOfAttributeToUpdate() {
        return taskOfAttributeToUpdate;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getNewAttributeValue() {
        return newAttributeValue;
    }


    public void setTaskOfAttributeToUpdate(String _taskOfAttributeToUpdate) {
        taskOfAttributeToUpdate = _taskOfAttributeToUpdate;
    }

    public void setAttributeName(String _attributeName) {
        attributeName = _attributeName;
    }

    public void setNewAttributeValue(String _newAttributeValue) {
        newAttributeValue = _newAttributeValue;
    }

    
}
