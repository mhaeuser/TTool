package avatartranslator.mutation;

import avatartranslator.*;

public abstract class BlockStructMutation extends AvatarMutation {

    private String blockName;

    public void setBlockName(String _blockName) {
        blockName = _blockName;
    }

    public AvatarBlock getBlock(AvatarSpecification _avspec) {
        return _avspec.getBlockWithName(blockName);
    }
}