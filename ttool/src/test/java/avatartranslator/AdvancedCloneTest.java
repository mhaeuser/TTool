package avatartranslator;

import org.junit.Test;
import static org.junit.Assert.*;
import avatartranslator.mutation.*;

public class AdvancedCloneTest {

    AvatarSpecification specification;

    public AvatarSpecification generateSpec(){
        AvatarSpecification avspec = new AvatarSpecification("specification", null);
        AvatarBlock myBlock = new AvatarBlock("myBlock", avspec, null);
        AvatarAttribute attr = new AvatarAttribute("attr", AvatarType.INTEGER, myBlock, null);

        avspec.addBlock(myBlock);
        myBlock.addAttribute(attr);

        return avspec;
    }

    @Test
    public void test() throws ParseMutationException, ApplyMutationException {
        specification = generateSpec();
        AvatarSpecification specificationCloned = specification.advancedClone();
        AvatarMutation mutation = AvatarMutation.createFromString("remove attribute attr in myBlock");

        mutation.apply(specificationCloned);
        assertTrue(specification.getBlockWithName("myBlock").getAttributes().size() == 1);
        assertTrue(specificationCloned.getBlockWithName("myBlock").getAttributes().size() == 0);

    }

}
