package tmltranslator.patternhandling;
/**
 * Class TaskPattern
 * 
 * Creation: 28/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/08/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;
import java.util.*;

 
public class TaskPattern {
    List<AttributeTaskJsonFile> attributes;
    List<PortTaskJsonFile> internalPorts;
    List<PortTaskJsonFile> externalPorts;

    public TaskPattern(List<AttributeTaskJsonFile> attributes, List<PortTaskJsonFile>  internalPorts, List<PortTaskJsonFile>  externalPorts) {
        this.attributes = attributes;
        this.internalPorts = internalPorts;
        this.externalPorts = externalPorts;
    }

    public List<AttributeTaskJsonFile> getAttributes() {
        return attributes;
    }

    public List<PortTaskJsonFile> getInternalPorts() {
        return internalPorts;
    }

    public List<PortTaskJsonFile> getExternalPorts() {
        return externalPorts;
    }
    
}
