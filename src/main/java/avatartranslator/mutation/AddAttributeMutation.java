package avatartranslator.mutation;

import avatartranslator.*;


/**
 * Class AddAttributeMutation
 * Mutation that adds an attribute to a block
 * Creation: 23/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 23/06/2022
 */
public class AddAttributeMutation extends AttributeMutation implements AddMutation {

    public AddAttributeMutation(String _name, String _type, String _blockName) {
        setType(_type);
        setName(_name);
        setBlockName(_blockName);
    }

    public AvatarAttribute createElement(AvatarSpecification _avspec) {
        AvatarType type = getType();
        AvatarBlock block = getBlock(_avspec);
        AvatarAttribute aa = new AvatarAttribute(getName(), type, block, null);
        if(hasInitialValue()) aa.setInitialValue(getInitialValue());
        return aa;
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarAttribute aa = createElement(_avspec);
        AvatarBlock block = getBlock(_avspec);
        block.addAttribute(aa);
    }

}