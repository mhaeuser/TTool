package tmltranslator.patternhandling;
/**
 * Class AttributeTaskJsonFile
 * 
 * Creation: 28/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/08/2023
 */
 
import tmltranslator.*;

 
public class AttributeTaskJsonFile {
    String name;
    String type;
    String value;

    public AttributeTaskJsonFile(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String _value) {
        value = _value;
    }
    
}
