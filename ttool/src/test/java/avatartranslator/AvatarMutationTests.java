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
 * Class AvatarMutationTests
 * Creation: 23/06/2022
 * @version 1.0 23/06/2022
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

    @Test
    public void testAddMethod() {
        MethodMutation mutation = new AddMethodMutation("f", "block");
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addReturnParameter("int");
        mutation.addReturnParameter("bool");
        mutation.addParameter(tmp);

        MethodMutation mutation2 = new AddMethodMutation("g", "block", true);
        mutation2.addReturnParameter("int");

        mutation.apply(as);
        mutation2.apply(as);

        assertTrue(block.getMethods().size() == 2);

        AvatarMethod meth = block.getMethods().get(0);
        AvatarMethod meth2 = block.getMethods().get(1);

        assertTrue(meth.getName().equals("f"));
        TraceManager.addDev(meth.toString());
        assertTrue(meth2.isImplementationProvided());
    }

    @Test
    public void testRmMethod() {
        MethodMutation mutation = new AddMethodMutation("f", "block");
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addReturnParameter("int");
        mutation.addReturnParameter("bool");
        mutation.addParameter(tmp);
        mutation.apply(as);

        MethodMutation mutation2 = new RmMethodMutation("f", "block");
        mutation2.apply(as);

        
        assertTrue(block.getMethods().size() == 0);
    }

    @Test
    public void testMdMethod() {
        MethodMutation mutation = new AddMethodMutation("f", "block");
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addReturnParameter("int");
        mutation.addReturnParameter("bool");
        mutation.addParameter(tmp);
        mutation.apply(as);

        MethodMutation mutation2 = new MdMethodMutation("f", "block", true);
        mutation2.apply(as);

        AvatarMethod meth = block.getMethods().get(0);
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 2);
        TraceManager.addDev(meth.toString());

        mutation2 = new MdMethodMutation("f", "block");
        mutation2.addReturnParameter("int");
        mutation2.addParameter(tmp2);
        mutation2.apply(as);

        meth = block.getMethods().get(0);
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 1);
        TraceManager.addDev(meth.toString());
    }

    @Test
    public void testAddSignal() {
        SignalMutation mutation = new AddSignalMutation("cin", "block", SignalMutation.IN);
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addParameter(tmp);
        mutation.addParameter(tmp2);

        SignalMutation mutation2 = new AddSignalMutation("cout", "block", SignalMutation.OUT);

        mutation.apply(as);
        mutation2.apply(as);

        assertTrue(block.getSignals().size() == 2);

        TraceManager.addDev(block.getSignals().get(0).toString());
        TraceManager.addDev(block.getSignals().get(1).toString());
    }

    public void addSignal() {
        SignalMutation mutation = new AddSignalMutation("cin", "block", SignalMutation.IN);
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addParameter(tmp);
        mutation.addParameter(tmp2);

        SignalMutation mutation2 = new AddSignalMutation("cout", "block", SignalMutation.OUT);

        mutation.apply(as);
        mutation2.apply(as);
    }

    @Test
    public void testRmSignal() {
        SignalMutation mutation = new AddSignalMutation("cin", "block", SignalMutation.IN);
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addParameter(tmp);
        mutation.addParameter(tmp2);
        mutation.apply(as);
        
        assertTrue(block.getSignals().size() == 1);

        SignalMutation mutation2 = new RmSignalMutation("cin", "block");
        mutation2.apply(as);

        assertTrue(block.getSignals().size() == 0);

    }

    @Test
    public void testMdSignal() {
        SignalMutation mutation = new AddSignalMutation("cin", "block", SignalMutation.IN);
        String[] tmp = {"int", "x"};
        String[] tmp2 = {"bool", "y"};
        mutation.addParameter(tmp);
        mutation.addParameter(tmp2);
        mutation.apply(as);
        
        assertTrue(block.getSignals().size() == 1);

        SignalMutation mutation2 = new MdSignalMutation("cin", "block", SignalMutation.OUT);
        mutation2.apply(as);

        assertTrue(block.getSignals().get(0).getInOut() == SignalMutation.OUT);

        SignalMutation mutation3 = new MdSignalMutation("cin", "block");
        mutation3.addParameter(tmp);
        mutation3.apply(as);

        assertTrue(block.getSignals().get(0).getListOfAttributes().size() == 1);
        TraceManager.addDev(block.getSignals().get(0).toString());
    }

    @Test
    public void testAddState() {
        StateMutation mutation = new AddStateMutation("state0", "block");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 1);
    }

    public void add2States() {
        testAddState();
        StateMutation mutation0 = new AddStateMutation("state1", "block");
        mutation0.apply(as);
    }

    @Test
    public void testRmState() {
        add2States();
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 2);
        StateMutation mutation = new RmStateMutation("state0", "block");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 1);
        assertTrue(block.getStateMachine().getState(0).getName().equals("state1"));
    }
    

    public AvatarTransition add2Trans() {
        add2States();
        TransitionMutation mutation0 = new AddTransitionMutation("block");
        mutation0.setFromWithName("state0");
        mutation0.setToWithName("state1");
        mutation0.apply(as);
        TransitionMutation mutation = new AddTransitionMutation("block");
        mutation.setFromWithName("state0");
        mutation.setToWithName("state1");
        mutation.setName("trans");
        mutation.setProbability(0.5);
        mutation.setGuard("x > 1");
        mutation.setDelays("0", "5");
        mutation.setDelayDistributionLaw(4, "1", "6");
        mutation.setComputes("12", "42");
        mutation.addAction("x = 1");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddTransition1() {
        AvatarTransition trans = add2Trans();
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
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testAddTransition2() {
        testAddActionOnSignal();
        add2States();
        AddTransitionMutation mutation = new AddTransitionMutation("block");
        mutation.setFromWithName("state0");
        mutation.setToWithName("aaos");
        mutation.apply(as);
        AvatarTransition trans = mutation.getElement(as);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testRmTransition() {
        add2Trans();
        //TraceManager.addDev(block.getStateMachine().getState(0).toString());
        TransitionMutation mutation = new RmTransitionMutation("block");
        mutation.setName("trans");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==1);
        mutation = new RmTransitionMutation("block");
        mutation.setFromWithName("state0");
        mutation.setToWithName("state1");
        //TraceManager.addDev(block.getStateMachine().getState(0).toString());
        mutation.apply(as);
        //TraceManager.addDev(block.getStateMachine().getState(0).toString());
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==0);
    }

    @Test
    public void testMdTransition() {
        add2Trans();
        MdTransitionMutation mutation = new MdTransitionMutation("block");
        mutation.setCurrentNoActions();
        mutation.addAction("x = x + 2");
        mutation.setCurrentFromWithName("state0");
        mutation.setCurrentToWithName("state1");
        AvatarTransition trans = mutation.getElement(as);
        assertTrue(trans.getNbOfAction() == 0);
        mutation.apply(as);
        assertTrue(trans.getNbOfAction() == 1);
        TraceManager.addDev(trans.toString());
    }

    public void AddActionOnSignal() {
        addSignal();
        AddActionOnSignalMutation mutation = new AddActionOnSignalMutation("cin", "block");
        mutation.setName("aaos");
        mutation.apply(as);
    }

    @Test
    public void testAddActionOnSignal() {
        AddActionOnSignal();
        AvatarStateMachine asm = block.getStateMachine();
        assertTrue(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void testRmActionOnSignal1() {
        AddActionOnSignal();
        RmActionOnSignalMutation mutation = new RmActionOnSignalMutation("cin", "block");
        mutation.apply(as);
        AvatarStateMachine asm = block.getStateMachine();
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
    }
    
    @Test
    public void testRmActionOnSignal2() {
        AddActionOnSignal();
        RmActionOnSignalMutation mutation = new RmActionOnSignalMutation("", "block");
        mutation.setName("aaos");
        mutation.apply(as);
        AvatarStateMachine asm = block.getStateMachine();
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void testMdActionOnSignal() {
        AvatarStateMachine asm = block.getStateMachine();
        AddActionOnSignal();
        MdActionOnSignalMutation mutation = new MdActionOnSignalMutation("cout", "block");
        mutation.setName("aaos");
        mutation.setCurrentSignalName("cin");
        assertFalse(asm.isSignalUsed(block.getSignalByName("cout")));
        mutation.apply(as);
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
        assertTrue(asm.isSignalUsed(block.getSignalByName("cout")));
    }

    public AvatarRandom addRandom() {
        AddRandomMutation mutation = new AddRandomMutation("x", "block");
        mutation.setName("rand");
        mutation.setValues("5", "15");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddRandom() {
        AvatarRandom rand = addRandom();
        assertTrue(rand.getVariable().equals("x"));
        TraceManager.addDev(rand.getNiceName());
    }

    @Test
    public void testRmRandom() {
        AddRandomMutation mutation0 = new AddRandomMutation("x", "block");
        mutation0.setValues("-5", "10");
        mutation0.apply(as);
        addRandom();
        RmRandomMutation mutation = new RmRandomMutation("x", "block");
        mutation.setValues("5", "10");
        mutation.apply(as);
        assertTrue(mutation0.getElement(as) != null);
    }

    @Test
    public void testMdRandom() {
        AvatarRandom rand = addRandom();
        MdRandomMutation mutation = new MdRandomMutation("y", "block");
        mutation.setCurrentVariable("x");
        mutation.apply(as);
        assertTrue(rand.getVariable().equals("y"));
    }

    public AvatarSetTimer addSetTimer() {
        AvatarAttribute timer = new AvatarAttribute("timer", AvatarType.TIMER, block, null);
        block.addAttribute(timer);
        AddSetTimerMutation mutation = new AddSetTimerMutation("timer", "10", "block");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddSetTimer() {
        AvatarSetTimer set = addSetTimer();
        assertTrue(set != null);
        TraceManager.addDev(set.getTimer().toString());
    }

    @Test
    public void testRmSetTimer() {
        addSetTimer();
        RmSetTimerMutation mutation = new RmSetTimerMutation("timer", "block");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void testMdSetTimer() {
        AvatarSetTimer timer = addSetTimer();
        MdSetTimerMutation mutation = new MdSetTimerMutation("timer", "block");
        mutation.setTimerValue("15");
        mutation.apply(as);
        assertTrue(timer.getTimerValue().equals("15"));
    }

    public AvatarResetTimer addResetTimer() {
        AvatarAttribute timer = new AvatarAttribute("timer", AvatarType.TIMER, block, null);
        block.addAttribute(timer);
        AddResetTimerMutation mutation = new AddResetTimerMutation("timer", "block");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddResetTimer() {
        AvatarResetTimer set = addResetTimer();
        assertTrue(set != null);
        TraceManager.addDev(set.getTimer().toString());
    }

    @Test
    public void testRmResetTimer() {
        addResetTimer();
        RmResetTimerMutation mutation = new RmResetTimerMutation("timer", "block");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void testMdResetTimer() {
        AvatarResetTimer timer = addResetTimer();
        AvatarAttribute timer2 = new AvatarAttribute("timer2", AvatarType.TIMER, block, null);
        block.addAttribute(timer2);
        MdResetTimerMutation mutation = new MdResetTimerMutation("timer", "timer2", "block");
        mutation.apply(as);
        assertTrue(timer.getTimer().getName().equals("timer2"));
    }

    public AvatarExpireTimer addExpireTimer() {
        AvatarAttribute timer = new AvatarAttribute("timer", AvatarType.TIMER, block, null);
        block.addAttribute(timer);
        AddExpireTimerMutation mutation = new AddExpireTimerMutation("timer", "block");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddExpireTimer() {
        AvatarExpireTimer set = addExpireTimer();
        assertTrue(set != null);
        TraceManager.addDev(set.getTimer().toString());
    }

    @Test
    public void testRmExpireTimer() {
        addExpireTimer();
        RmExpireTimerMutation mutation = new RmExpireTimerMutation("timer", "block");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void testMdExpireTimer() {
        AvatarExpireTimer timer = addExpireTimer();
        AvatarAttribute timer2 = new AvatarAttribute("timer2", AvatarType.TIMER, block, null);
        block.addAttribute(timer2);
        MdExpireTimerMutation mutation = new MdExpireTimerMutation("timer", "timer2", "block");
        mutation.apply(as);
        assertTrue(timer.getTimer().getName().equals("timer2"));
    }
}