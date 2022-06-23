package avatartranslator.mutation;

import avatartranslator.*;
import java.util.List;

import myutil.TraceManager;

public class RmAttributeMutation extends AttributeMutation implements RmMutation {

    public RmAttributeMutation(String _name, String _blockName) {
        setName(_name);
        setBlockName(_blockName);
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarBlock block = getBlock(_avspec);
        List<AvatarAttribute> attr = block.getAttributes();
        AvatarAttribute aa = findElement(_avspec);
        if(aa == null) {
            TraceManager.addDev("Attribut inexistant");
            return;
        }
        attr.remove(aa);
    }
}