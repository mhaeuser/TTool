package tmltranslator.patternhandling;
/**
 * Class PortTaskJsonFile
 * 
 * Creation: 28/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/08/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

 
public class PortTaskJsonFile {
    String name;
    String type;
    String mode;

    public PortTaskJsonFile(String name, String type, String mode) {
        this.name = name;
        this.type = type;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMode() {
        return mode;
    }
    
}
