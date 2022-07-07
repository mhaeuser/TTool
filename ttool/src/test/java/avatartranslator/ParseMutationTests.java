/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 * /**
 * Class ParseMutationTests
 * Creation: 07/07/2022
 * @version 1.0 07/07/2022
 * @author Léon FRENOT
 */

package avatartranslator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import myutil.TraceManager;
import org.junit.Before;
import org.junit.Test;

import avatartranslator.*;
import avatartranslator.mutation.*;
import java.util.List;

public class ParseMutationTests {

    private AvatarSpecification as;
    private AvatarBlock block;
    public static final int UNDEFINED_TYPE = -1;
    public static final int NAME_TYPE = 0;
    public static final int UUID_TYPE = 1;

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
    public void createFromStringAddAttribute() {
        AvatarMutation mutation = AvatarMutation.createFromString("add attribute bool y to block block");
        AvatarMutation mutation2 = AvatarMutation.createFromString("add attribute int z = 5 to block block");
        mutation.apply(as);
        assertTrue(block.getAttributes().size() == 2);
        AvatarAttribute attr1 = block.getAttribute(1);
        TraceManager.addDev(attr1.toString());
        assertTrue(attr1.isBool());
        mutation2.apply(as);
        TraceManager.addDev(String.valueOf(block.getAttributes().size()));
        assertTrue(block.getAttributes().size() == 3);
        AvatarAttribute attr2 = block.getAttribute(2);
        assertTrue(attr1.isBool());
        assertTrue(attr2.isInt());
        assertTrue(attr2.getInitialValueInInt() == 5);
    }

    @Test
    public void createFromStringRmAttribute() {
        AvatarMutation mutation = AvatarMutation.createFromString("remove attribute x from block block");
        mutation.apply(as);
        assertTrue(block.getAttributes().size() == 0);
    }

    @Test
    public void createFromStringMdAttribute() {
        assertTrue(block.getAttribute(0).getInitialValue().equals("10"));
        AvatarMutation mutation = AvatarMutation.createFromString("md attribute x from block block to 42");
        mutation.apply(as);
        assertTrue(block.getAttribute(0).getInitialValue().equals("42"));
    }

    @Test
    public void createFromStringAddMethod() {
        AvatarMutation mutation = AvatarMutation.createFromString("add method (int, bool) f(int x) to block block");

        AvatarMutation mutation2 = AvatarMutation.createFromString("add method int g() to block block with code");

        mutation.apply(as);
        mutation2.apply(as);

        assertTrue(block.getMethods().size() == 2);

        AvatarMethod meth = ((MethodMutation)mutation).getElement(as);
        AvatarMethod meth2 = ((MethodMutation)mutation2).getElement(as);

        assertTrue(meth.getName().equals("f"));
        TraceManager.addDev(meth.toString());
        assertTrue(meth2.isImplementationProvided());
    }

    @Test
    public void createFromStringRmMethod() {
        AvatarMutation mutation = AvatarMutation.createFromString("add method (int, bool) f(int x) to block block");
        mutation.apply(as);

        mutation = AvatarMutation.createFromString("rm method f from block block");
        mutation.apply(as);

        assertTrue(block.getMethods().size() == 0);
    }

    @Test
    public void createFromStringMdMethod() {
        AvatarMutation mutation = AvatarMutation.createFromString("add method (int, bool) f(int x) to block block");
        mutation.apply(as);

        mutation = AvatarMutation.createFromString("modify method f from block block with code");
        mutation.apply(as);

        AvatarMethod meth = block.getMethods().get(0);
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 2);
        TraceManager.addDev(meth.toString());

        mutation = AvatarMutation.createFromString("md method f from block block to int f(bool y)");
        mutation.apply(as);

        meth = block.getMethods().get(0);
        TraceManager.addDev(meth.toString());
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 1);
    }

    public void addSignal() {
        AvatarMutation mutation = AvatarMutation.createFromString("add input signal cin(int x, bool y) to block block");
        mutation.apply(as);

        mutation = AvatarMutation.createFromString("add output signal cout() to block block");
        mutation.apply(as);

    }

    @Test
    public void createFromStringAddSignal() {
        addSignal();

        assertTrue(block.getSignals().size() == 2);

        TraceManager.addDev(block.getSignals().get(0).toString());
        TraceManager.addDev(block.getSignals().get(1).toString());
    }

    @Test
    public void createFromStringRmSignal() {
        addSignal();

        AvatarMutation mutation = AvatarMutation.createFromString("rm signal cin from block block");
        mutation.apply(as);
        
        assertTrue(block.getSignals().size() == 1);
    }

    @Test
    public void createFromStringMdSignal() {
        addSignal();

        AvatarMutation mutation = AvatarMutation.createFromString("md signal cin from block block to output");
        mutation.apply(as);

        assertTrue(block.getSignals().get(0).getInOut() == SignalMutation.OUT);

        mutation = AvatarMutation.createFromString("md signal cin from block block to (int x)");
        mutation.apply(as);

        assertTrue(block.getSignals().get(0).getListOfAttributes().size() == 1);
        TraceManager.addDev(block.getSignals().get(0).toString());
    }

    public AvatarBlock addBlock() {
        AddBlockMutation mutation = AddBlockMutation.createFromString("add block block0");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddBlock() {
        AvatarBlock block0 = addBlock();
        assertTrue(block0 != null);
        assertTrue(block0.getName().equals("block0"));
    }

    @Test
    public void createFromStringRmBlock() {
        addBlock();
        assertTrue(as.getListOfBlocks().size() == 2);
        AvatarMutation mutation = AvatarMutation.createFromString("rm block block0");
        mutation.apply(as);
        assertTrue(as.getListOfBlocks().size() == 1);
    }

    public void attachParent() {
        addBlock();
        AvatarMutation mutation = AvatarMutation.createFromString("attach block to block0");
        mutation.apply(as);
    }

    @Test
    public void createFromStringAttachParent() {
        attachParent();
        assertTrue(block.getFather().getName().equals("block0"));
    }

    @Test
    public void createFromStringDetachParent() {
        attachParent();
        AvatarMutation mutation = AvatarMutation.createFromString("detach block");
        mutation.apply(as);
        assertTrue(block.getFather() == null);
    }

    public void addStates() {
        AvatarMutation mutation = AvatarMutation.createFromString("add state state0 to block block");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("add state state1 to block block");
        mutation.apply(as);
    }

    @Test
    public void createFromStringAddState() {
        addStates();
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 2);
    }

    @Test
    public void createFromStringRmState() {
        addStates();
        AvatarMutation mutation = AvatarMutation.createFromString("rm state state0 from block block");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 1);
        assertTrue(block.getStateMachine().getState(0).getName().equals("state1"));
    }

    public AvatarTransition addTransitions() {
        addStates();
        AvatarMutation mutation = AvatarMutation.createFromString("add transition from state0 to state1 to block block");
        mutation.apply(as);
        TransitionMutation mutation1 = AddTransitionMutation.createFromString("add transition trans from state0 to state1 to block block with [x > 1] and after(0, 5) e^(1, 6) and \"x = 1\" and probability 0.5 and computes (12, 42)");
        mutation1.apply(as);
        AvatarTransition trans = mutation1.getElement(as);
        return trans;
    }

    @Test
    public void createFromStringAddTransition1() {
        AvatarTransition trans = addTransitions();
        assertTrue(trans.getName().equals("trans"));
        assertTrue(trans.getGuard() != null);
        assertTrue(trans.getProbability() == 0.5);
        assertTrue(trans.getNbOfAction() == 1);
        assertTrue(trans.getMinDelay().equals("0"));
        assertTrue(trans.getMaxDelay().equals("5"));
        assertTrue(trans.getDelayDistributionLaw() == 4);
        assertTrue(trans.getDelayExtra1().equals("1"));
        assertTrue(trans.getDelayExtra2().equals("6"));
        assertTrue(trans.getMinCompute().equals("12"));
        assertTrue(trans.getMaxCompute().equals("42"));
        TraceManager.addDev(block.getStateMachine().getState(0).toString());
        TraceManager.addDev(trans.toString());

    }
}