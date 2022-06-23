package avatartranslator.mutation;

import avatartranslator.*;
import java.util.List;

public abstract class AttributeMutation extends BlockStructMutation {

    private String type;

    private String name;

    private String initialValue;

    private Boolean hasInitialValue = false;

    public void setName(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public void setType(String _type) {
        type = _type;
    }

    public AvatarType getType() {
        return AvatarType.getType(type);
    }

    public void setInitialValue(String _initialValue) {
        hasInitialValue = true;
        initialValue = _initialValue;
    }

    public Boolean hasInitialValue() {
        return hasInitialValue;
    }

    public String getInitialValue() {
        if(hasInitialValue()) return initialValue;
        return null;
    }

    public AvatarAttribute findElement(AvatarSpecification _avspec) {
        AvatarBlock block = getBlock(_avspec);
        List<AvatarAttribute> attr = block.getAttributes();
        for(AvatarAttribute aa : attr) {
            if(aa.getName().equals(this.getName())) return aa;
        }
        return null;
    }
}