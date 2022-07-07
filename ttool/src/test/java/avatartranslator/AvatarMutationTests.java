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
    public void testAddAttribute() {
        AddAttributeMutation mutation = new AddAttributeMutation("block", "y", "bool");
        AttributeMutation mutation2 = new AddAttributeMutation("block", "z", "int", "5");
        mutation.apply(as);
        mutation2.apply(as);
        assertTrue(block.getAttributes().size() == 3);
        AvatarAttribute attr1 = block.getAttribute(1);
        AvatarAttribute attr2 = block.getAttribute(2);
        assertTrue(attr1.isBool());
        assertTrue(attr2.isInt());
        assertTrue(attr2.getInitialValueInInt() == 5);
    }
    @Test
    public void testRmAttribute() {
        AttributeMutation mutation = new RmAttributeMutation("block", "x");
        mutation.apply(as);
        assertTrue(block.getAttributes().size() == 0);
        AttributeMutation mutation2 = new RmAttributeMutation("block", "x");
        mutation.apply(as);
    }

    @Test
    public void testMdAttribute() {
        assertTrue(block.getAttribute(0).getInitialValue().equals("10"));
        AttributeMutation mutation = new MdAttributeMutation("block", "x", "42");
        mutation.apply(as);
        assertTrue(block.getAttribute(0).getInitialValue().equals("42"));
    }

    @Test
    public void testAddMethod() {
        MethodMutation mutation = new AddMethodMutation("block", "f");
        mutation.addReturnParameter("int");
        mutation.addReturnParameter("bool");
        mutation.addParameter("int", "x");

        MethodMutation mutation2 = new AddMethodMutation("block", "g", true);
        mutation2.addReturnParameter("int");

        mutation.apply(as);
        mutation2.apply(as);

        assertTrue(block.getMethods().size() == 2);

        AvatarMethod meth = mutation.getElement(as);
        AvatarMethod meth2 = mutation2.getElement(as);

        assertTrue(meth.getName().equals("f"));
        TraceManager.addDev(meth.toString());
        assertTrue(meth2.isImplementationProvided());
    }

    @Test
    public void testRmMethod() {
        MethodMutation mutation = new AddMethodMutation("block", "f");
        mutation.addReturnParameter("int");
        mutation.addReturnParameter("bool");
        mutation.addParameter("int", "x");
        mutation.apply(as);
        assertTrue(block.getMethods().size() == 1);

        MethodMutation mutation2 = new RmMethodMutation("block", "f");
        mutation2.apply(as);

        
        assertTrue(block.getMethods().size() == 0);
    }

    @Test
    public void testMdMethod() {
        MethodMutation mutation = new AddMethodMutation("block", "f");
        mutation.addReturnParameter("int");
        mutation.addReturnParameter("bool");
        mutation.addParameter("int", "x");
        mutation.apply(as);

        MethodMutation mutation2 = new MdMethodMutation("block", "f", true);
        mutation2.apply(as);

        AvatarMethod meth = block.getMethods().get(0);
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 2);
        TraceManager.addDev(meth.toString());

        mutation2 = new MdMethodMutation("block", "f");
        mutation2.addReturnParameter("int");
        mutation2.addParameter("bool", "y");
        mutation2.apply(as);

        meth = block.getMethods().get(0);
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 1);
        TraceManager.addDev(meth.toString());
    }

    public void addSignal() {
        SignalMutation mutation = new AddSignalMutation("block", "cin", SignalMutation.IN);
        mutation.addParameter("int", "x");
        mutation.addParameter("bool", "y");

        SignalMutation mutation2 = new AddSignalMutation("block", "cout", SignalMutation.OUT);

        mutation.apply(as);
        mutation2.apply(as);
    }

    @Test
    public void testAddSignal() {
        addSignal();

        assertTrue(block.getSignals().size() == 2);

        TraceManager.addDev(block.getSignals().get(0).toString());
        TraceManager.addDev(block.getSignals().get(1).toString());
    }

    @Test
    public void testRmSignal() {
        addSignal();

        SignalMutation mutation2 = new RmSignalMutation("block", "cin");
        mutation2.apply(as);

        assertTrue(block.getSignals().size() == 1);

    }

    @Test
    public void testMdSignal() {
        addSignal();

        SignalMutation mutation2 = new MdSignalMutation("block", "cin", SignalMutation.OUT);
        mutation2.apply(as);

        assertTrue(block.getSignals().get(0).getInOut() == SignalMutation.OUT);

        SignalMutation mutation3 = new MdSignalMutation("block", "cin");
        mutation3.addParameter("int", "x");
        mutation3.apply(as);

        assertTrue(block.getSignals().get(0).getListOfAttributes().size() == 1);
        TraceManager.addDev(block.getSignals().get(0).toString());
    }

    @Test
    public void testAddState() {
        StateMutation mutation = new AddStateMutation("block", "state0");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 1);
    }

    public void addStates() {
        testAddState();
        StateMutation mutation0 = new AddStateMutation("block", "state1");
        mutation0.apply(as);
    }

    @Test
    public void testRmState() {
        addStates();
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 2);
        StateMutation mutation = new RmStateMutation("block", "state0");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 1);
        assertTrue(block.getStateMachine().getState(0).getName().equals("state1"));
    }
    

    public AvatarTransition addTransitions() {
        addStates();
        TransitionMutation mutation0 = new AddTransitionMutation("block", "state0", NAME_TYPE, "state1", NAME_TYPE);
        mutation0.apply(as);
        TransitionMutation mutation = new AddTransitionMutation("block", "state0", NAME_TYPE, "state1", NAME_TYPE, "trans");
        mutation.setProbability(0.5);
        mutation.setGuard("x > 1");
        mutation.setDelays("0", "5");
        mutation.setDelayDistributionLaw("4", "1", "6");
        mutation.setComputes("12", "42");
        mutation.addAction("x = 1");
        mutation.apply(as);
        TraceManager.addDev(block.getStateMachine().getState(0).toString());
        AvatarTransition trans = mutation.getElement(as);
        TraceManager.addDev(trans.toString());
        return trans;
    }

    @Test
    public void testAddTransition1() {
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
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testAddTransition2() {
        addActionOnSignal();
        addStates();
        AddTransitionMutation mutation = new AddTransitionMutation("block", "state0", NAME_TYPE, "aaos", NAME_TYPE);
        mutation.apply(as);
        AvatarTransition trans = mutation.getElement(as);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testRmTransition() {
        addTransitions();
        //TraceManager.addDev(block.getStateMachine().getState(0).toString());
        TransitionMutation mutation = new RmTransitionMutation("block", "trans", NAME_TYPE);
        mutation.apply(as);
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==1);
        mutation = new RmTransitionMutation("block", "state0", NAME_TYPE, "state1", NAME_TYPE);
        //TraceManager.addDev(block.getStateMachine().getState(0).toString());
        mutation.apply(as);
        //TraceManager.addDev(block.getStateMachine().getState(0).toString());
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==0);
    }

    @Test
    public void testMdTransition() {
        addTransitions();
        MdTransitionMutation mutation = new MdTransitionMutation("block", "state0", NAME_TYPE, "state1", NAME_TYPE);
        mutation.setCurrentNoActions();
        mutation.addAction("x = x + 2");
        AvatarTransition trans = mutation.getElement(as);
        assertTrue(trans.getNbOfAction() == 0);
        mutation.apply(as);
        assertTrue(trans.getNbOfAction() == 1);
        TraceManager.addDev(trans.toString());
    }

    public void addActionOnSignal() {
        addSignal();
        AddActionOnSignalMutation mutation = new AddActionOnSignalMutation("block", "cin", "aaos");
        mutation.apply(as);
    }

    @Test
    public void testAddActionOnSignal() {
        addActionOnSignal();
        AvatarStateMachine asm = block.getStateMachine();
        assertTrue(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void testRmActionOnSignal1() {
        addActionOnSignal();
        RmActionOnSignalMutation mutation = new RmActionOnSignalMutation("block", "cin");
        mutation.apply(as);
        AvatarStateMachine asm = block.getStateMachine();
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
    }
    
    @Test
    public void testRmActionOnSignal2() {
        addActionOnSignal();
        RmActionOnSignalMutation mutation = new RmActionOnSignalMutation("block", "aaos", NAME_TYPE);
        mutation.apply(as);
        AvatarStateMachine asm = block.getStateMachine();
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void testMdActionOnSignal() {
        AvatarStateMachine asm = block.getStateMachine();
        addActionOnSignal();
        MdActionOnSignalMutation mutation = new MdActionOnSignalMutation("block", "aaos", NAME_TYPE, "cout");
        assertFalse(asm.isSignalUsed(block.getSignalByName("cout")));
        mutation.apply(as);
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
        assertTrue(asm.isSignalUsed(block.getSignalByName("cout")));
    }

    public AvatarRandom addRandom() {
        AddRandomMutation mutation = new AddRandomMutation("block", "x", "rand");
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
        AddRandomMutation mutation0 = new AddRandomMutation("block", "x");
        mutation0.setValues("-5", "10");
        mutation0.apply(as);
        addRandom();
        RmRandomMutation mutation = new RmRandomMutation("block", "x");
        mutation.setValues("5", "10");
        mutation.apply(as);
        assertTrue(mutation0.getElement(as) != null);
    }

    @Test
    public void testMdRandom() {
        AvatarRandom rand = addRandom();
        MdRandomMutation mutation = new MdRandomMutation("block", "x", "y");
        mutation.apply(as);
        assertTrue(rand.getVariable().equals("y"));
    }

    public AvatarSetTimer addSetTimer() {
        AvatarAttribute timer = new AvatarAttribute("timer", AvatarType.TIMER, block, null);
        block.addAttribute(timer);
        AddSetTimerMutation mutation = new AddSetTimerMutation("block", "timer", "10");
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
        RmSetTimerMutation mutation = new RmSetTimerMutation("block", "timer", "10");
        TraceManager.addDev(mutation.getElement(as).toString());
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void testMdSetTimer() {
        AvatarSetTimer timer = addSetTimer();
        MdSetTimerMutation mutation = new MdSetTimerMutation("block", "timer", "10", "15");
        mutation.apply(as);
        TraceManager.addDev(timer.getTimerValue());
        assertTrue(timer.getTimerValue().equals("15"));
    }

    public AvatarResetTimer addResetTimer() {
        AvatarAttribute timer = new AvatarAttribute("timer", AvatarType.TIMER, block, null);
        block.addAttribute(timer);
        AddResetTimerMutation mutation = new AddResetTimerMutation("block", "timer");
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
        RmResetTimerMutation mutation = new RmResetTimerMutation("block", "timer");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void testMdResetTimer() {
        AvatarResetTimer timer = addResetTimer();
        AvatarAttribute timer2 = new AvatarAttribute("timer2", AvatarType.TIMER, block, null);
        block.addAttribute(timer2);
        MdResetTimerMutation mutation = new MdResetTimerMutation("block", "timer", "timer2");
        mutation.apply(as);
        assertTrue(timer.getTimer().getName().equals("timer2"));
    }

    public AvatarExpireTimer addExpireTimer() {
        AvatarAttribute timer = new AvatarAttribute("timer", AvatarType.TIMER, block, null);
        block.addAttribute(timer);
        AddExpireTimerMutation mutation = new AddExpireTimerMutation("block", "timer");
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
        RmExpireTimerMutation mutation = new RmExpireTimerMutation("block", "timer");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void testMdExpireTimer() {
        AvatarExpireTimer timer = addExpireTimer();
        AvatarAttribute timer2 = new AvatarAttribute("timer2", AvatarType.TIMER, block, null);
        block.addAttribute(timer2);
        MdExpireTimerMutation mutation = new MdExpireTimerMutation("block", "timer", "timer2");
        mutation.apply(as);
        assertTrue(timer.getTimer().getName().equals("timer2"));
    }

    public AvatarBlock addBlock() {
        AddBlockMutation mutation = new AddBlockMutation("block0");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddBlock() {
        AvatarBlock block0 = addBlock();
        assertTrue(block0 != null);
        assertTrue(block0.getName().equals("block0"));
    }

    @Test
    public void testRmBlock() {
        addBlock();
        assertTrue(as.getListOfBlocks().size() == 2);
        RmBlockMutation mutation = new RmBlockMutation("block0");
        mutation.apply(as);
        assertTrue(as.getListOfBlocks().size() == 1);
    }

    public void attachParent() {
        addBlock();
        AttachParentMutation mutation = new AttachParentMutation("block0", "block");
        mutation.apply(as);
    }

    @Test
    public void testAttachParent() {
        attachParent();
        assertTrue(block.getFather().getName().equals("block0"));
    }

    @Test
    public void testDetachParent() {
        attachParent();
        DetachParentMutation mutation = new DetachParentMutation("block");
        mutation.apply(as);
        assertTrue(block.getFather() == null);
    }

    public AvatarRelation addRelation() {
        addBlock();
        AddRelationMutation mutation = new AddRelationMutation("block", "block0", "relation");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void testAddRelation() {
        assertTrue(as.getRelations().size() == 0);
        addRelation();
        assertTrue(as.getRelations().size() == 1);
    }

    @Test
    public void testRmRelation() {
        addRelation();
        RmRelationMutation mutation = new RmRelationMutation("block", "block0");
        mutation.apply(as);
        assertTrue(as.getRelations().size() == 0);
    }

    @Test
    public void testMdRelation() {
        AvatarRelation relation = addRelation();
        MdRelationMutation mutation = new MdRelationMutation("block", "block0");
        mutation.setAMS(true);
        assertFalse(relation.isAMS());
        mutation.apply(as);
        assertTrue(relation.isAMS());
    }

    public AvatarRelation addAssociation() {
        AvatarRelation relation = addRelation();
        addSignal();
        SignalMutation mutation0 = new AddSignalMutation("block0", "cin0", SignalMutation.IN);
        mutation0.apply(as);
        RelationMutation mutation = new AddAssociationMutation("block", "block0", "cout", "cin0");
        mutation.apply(as);
        return relation;
    }

    @Test
    public void testAddAssociation() {
        AvatarRelation relation = addAssociation();
        assertTrue(relation.nbOfSignals() == 1);
    }

    @Test
    public void testRmAssociation() {
        AvatarRelation relation = addAssociation();
        RelationMutation mutation = new RmAssociationMutation("block", "block0", "cout", "cin0");
        mutation.apply(as);
        assertTrue(relation.nbOfSignals() == 0);
    }

    public AvatarTransition add2Actions() {
        AvatarTransition trans = addTransitions();
        ActionMutation mutation = new AddActionMutation("block", "trans", NAME_TYPE, "x = x + 2");
        mutation.apply(as);
        return trans;
    }

    @Test
    public void testAddAction() {
        AvatarTransition trans = add2Actions();
        assertTrue(trans.getNbOfAction() == 2);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testRmAction() {
        AvatarTransition trans = add2Actions();
        ActionMutation mutation = new RmActionMutation("block", "trans", NAME_TYPE, 1);
        mutation.apply(as);
        assertTrue(trans.getNbOfAction() == 1);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testMdAction() {
        AvatarTransition trans = add2Actions();
        ActionMutation mutation = new MdActionMutation("block", "trans", NAME_TYPE, "x = -3", 0);
        mutation.apply(as);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void testSwapAction() {
        AvatarTransition trans = add2Actions();
        ActionMutation mutation = new SwapActionMutation("block", "trans", NAME_TYPE, 1, 0);
        mutation.apply(as);
        TraceManager.addDev(trans.toString());
    }
}