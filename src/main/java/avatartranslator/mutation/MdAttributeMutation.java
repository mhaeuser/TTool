package avatartranslator.mutation;

import avatartranslator.*;

import myutil.TraceManager;

public class MdAttributeMutation extends AttributeMutation implements MdMutation {

    public MdAttributeMutation(String _name, String _initialValue, String _blockName) {
        setName(_name);
        setInitialValue(_initialValue);
        setBlockName(_blockName);
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarAttribute aa = findElement(_avspec);
        if(aa == null) {
            TraceManager.addDev("Attribut inexistant");
            return;
        }
        aa.setInitialValue(this.getInitialValue());
    }
}