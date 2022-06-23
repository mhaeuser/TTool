package avatartranslator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import myutil.TraceManager;
import org.junit.Before;
import org.junit.Test;

import avatartranslator.*;
import avatartranslator.mutation.*;
import java.util.List;

public class AvatarMutationTests {

    private AvatarSpecification as;
    private AvatarBlock block;

    @Before
    public void test() {
        as = new AvatarSpecification("avatarspecification", null);
        block = new AvatarBlock("block", as, null);
        as.addBlock(block);
        AvatarAttribute x1 = new AvatarAttribute("x", AvatarType.INTEGER, block, null);
        block.addAttribute(x1);
        x1.setInitialValue("10");
    }

    @Test
    public void testAddAttribute() {
        AddAttributeMutation mutation = new AddAttributeMutation("y", "bool", "block");
        assertTrue(mutation.getType() == AvatarType.BOOLEAN);
        assertTrue(mutation.getName().equals("y"));
        assertTrue(mutation.getBlock(as) == block);

        AttributeMutation mutation2 = new AddAttributeMutation("z", "int", "block");
        mutation2.setInitialValue("5");
        mutation.apply(as);
        mutation2.apply(as);
        assertTrue(block.getAttributes().size() == 3);
        AvatarAttribute attr1 = block.getAttribute(1);
        AvatarAttribute attr2 = block.getAttribute(2);
        assertTrue(attr2.getInitialValueInInt() == 5);
    }

    @Test
    public void testRmAttribute() {
        AttributeMutation mutation = new RmAttributeMutation("x", "block");
        mutation.apply(as);
        assertTrue(block.getAttributes().size() == 0);
        AttributeMutation mutation2 = new RmAttributeMutation("x", "block");
        mutation.apply(as);
    }

    @Test
    public void testMdAttribute() {
        assertTrue(block.getAttribute(0).getInitialValue().equals("10"));
        AttributeMutation mutation = new MdAttributeMutation("x", "42", "block");
        mutation.apply(as);
        assertTrue(block.getAttribute(0).getInitialValue().equals("42"));
    }
}