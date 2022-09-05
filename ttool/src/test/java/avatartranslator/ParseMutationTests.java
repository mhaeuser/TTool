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

import java.util.ArrayList;
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
    public void createFromStringAddAttribute() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("add attribute bool y in block");
        AvatarMutation mutation2 = AvatarMutation.createFromString("add attribute int z = 5 in block");
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
    public void createFromStringRmAttribute() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("remove attribute x in block");
        mutation.apply(as);
        assertTrue(block.getAttributes().size() == 0);
    }

    @Test
    public void createFromStringMdAttribute() throws ParseMutationException, ApplyMutationException {
        assertTrue(block.getAttribute(0).getInitialValue().equals("10"));
        AvatarMutation mutation = AvatarMutation.createFromString("md attribute x in block to 42");
        mutation.apply(as);
        assertTrue(block.getAttribute(0).getInitialValue().equals("42"));
    }

    @Test
    public void createFromStringAddMethod() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("add method (int, bool) f(int x) in block");

        AvatarMutation mutation2 = AvatarMutation.createFromString("add method int g() in block with code");

        mutation.apply(as);
        mutation2.apply(as);

        assertTrue(block.getMethods().size() == 2);

        AvatarMethod meth = ((MethodMutation)mutation).getElement(as);
        AvatarMethod meth2 = ((MethodMutation)mutation2).getElement(as);

        TraceManager.addDev(meth.toString());
        assertTrue(meth.getName().equals("f"));
        assertTrue(meth2.isImplementationProvided());
    }

    @Test
    public void createFromStringRmMethod() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("add method (int, bool) f(int x) in block");
        mutation.apply(as);

        mutation = AvatarMutation.createFromString("rm method f in block");
        mutation.apply(as);

        assertTrue(block.getMethods().size() == 0);
    }

    @Test
    public void createFromStringMdMethod() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("add method (int, bool) f(int x) in block");
        mutation.apply(as);

        mutation = AvatarMutation.createFromString("modify method f in block with code");
        mutation.apply(as);

        AvatarMethod meth = block.getMethods().get(0);
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 2);
        TraceManager.addDev(meth.toString());

        mutation = AvatarMutation.createFromString("md method f in block to int f(bool y)");
        mutation.apply(as);

        meth = block.getMethods().get(0);
        TraceManager.addDev(meth.toString());
        assertTrue(meth.isImplementationProvided());
        assertTrue(meth.getListOfAttributes().size() == 1);
        assertTrue(meth.getListOfReturnAttributes().size() == 1);
    }

    public void addSignal() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("add input signal cin(int x, bool y) in block");
        mutation.apply(as);

        mutation = AvatarMutation.createFromString("add output signal cout() in block");
        mutation.apply(as);

    }

    @Test
    public void createFromStringAddSignal() throws ParseMutationException, ApplyMutationException {
        addSignal();

        assertTrue(block.getSignals().size() == 2);

        TraceManager.addDev(block.getSignals().get(0).toString());
        TraceManager.addDev(block.getSignals().get(1).toString());
    }

    @Test
    public void createFromStringRmSignal() throws ParseMutationException, ApplyMutationException {
        addSignal();

        AvatarMutation mutation = AvatarMutation.createFromString("rm signal cin in block");
        mutation.apply(as);
        
        assertTrue(block.getSignals().size() == 1);
    }

    @Test
    public void createFromStringMdSignal() throws ParseMutationException, ApplyMutationException {
        addSignal();

        AvatarMutation mutation = AvatarMutation.createFromString("md signal cin in block to output");
        mutation.apply(as);

        assertTrue(block.getSignals().get(0).getInOut() == SignalMutation.OUT);

        mutation = AvatarMutation.createFromString("md signal cin in block to (int x)");
        mutation.apply(as);

        assertTrue(block.getSignals().get(0).getListOfAttributes().size() == 1);
        TraceManager.addDev(block.getSignals().get(0).toString());
    }

    public AvatarBlock addBlock() throws ParseMutationException, ApplyMutationException {
        AddBlockMutation mutation = AddBlockMutation.createFromString("add block block0");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddBlock() throws ParseMutationException, ApplyMutationException {
        AvatarBlock block0 = addBlock();
        assertTrue(block0 != null);
        assertTrue(block0.getName().equals("block0"));
        assertTrue(as.getListOfBlocks().size() == 2);
    }

    @Test
    public void createFromStringRmBlock() throws ParseMutationException, ApplyMutationException {
        addBlock();
        assertTrue(as.getListOfBlocks().size() == 2);
        AvatarMutation mutation = AvatarMutation.createFromString("rm block block0");
        mutation.apply(as);
        assertTrue(as.getListOfBlocks().size() == 1);
    }

    public void attachParent() throws ParseMutationException, ApplyMutationException {
        addBlock();
        AvatarMutation mutation = AvatarMutation.createFromString("attach block to block0");
        mutation.apply(as);
    }

    @Test
    public void createFromStringAttachParent() throws ParseMutationException, ApplyMutationException {
        attachParent();
        assertTrue(block.getFather().getName().equals("block0"));
    }

    @Test
    public void createFromStringDetachParent() throws ParseMutationException, ApplyMutationException {
        attachParent();
        AvatarMutation mutation = AvatarMutation.createFromString("detach block");
        mutation.apply(as);
        assertTrue(block.getFather() == null);
    }

    public void addStates() throws ParseMutationException, ApplyMutationException {
        AvatarMutation mutation = AvatarMutation.createFromString("add state state0 in block");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("add state state1 in block");
        mutation.apply(as);
    }

    @Test
    public void createFromStringAddState() throws ParseMutationException, ApplyMutationException {
        addStates();
        assertTrue(block.getStateMachine().getNbOfStatesElement() == 2);
    }

    @Test
    public void createFromStringRmState() throws ParseMutationException, ApplyMutationException {
        addStates();
        AvatarMutation mutation = AvatarMutation.createFromString("add transition in block from state1 to state0");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("add transition in block from state0 to state1");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("rm state state0 in block");
        AvatarStateMachine asm = block.getStateMachine();
        List<AvatarStateMachineElement> transitions = asm.getElementsLeadingTo(asm.getStateWithName("state0"));
        transitions.addAll(asm.getStateWithName("state0").getNexts());
        mutation.apply(as);
        for (AvatarStateMachineElement transition : transitions){
            assertFalse(asm.getListOfElements().contains(transition));
        }
        assertTrue(asm.getNbOfStatesElement() == 1);
        assertTrue(asm.getState(0).getName().equals("state1"));
    }

    public AvatarTransition addTransitions() throws ParseMutationException, ApplyMutationException {
        addStates();
        AvatarMutation mutation = AvatarMutation.createFromString("add transition in block from state0 to state1");
        mutation.apply(as);
        TransitionMutation mutation1 = AddTransitionMutation.createFromString("add transition trans in block from state0 to state1 with [x > 1] and after(0, 5) e^(1, 6) and \"x = 1\" and probability 0.5 and computes (12, 42)");
        mutation1.apply(as);
        AvatarTransition trans = mutation1.getElement(as);
        return trans;
    }

    @Test
    public void createFromStringAddTransition1() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addTransitions();
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==2);
        TraceManager.addDev(trans.getName());
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

    @Test
    public void createFromStringRmTransition1() throws ParseMutationException, ApplyMutationException {
        addTransitions();
        AvatarMutation mutation = AvatarMutation.createFromString("rm transition trans in block");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==1);
        mutation = AvatarMutation.createFromString("rm transition in block from state0 to state1");
        mutation.apply(as);
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==0);
    }

    @Test
    public void createFromStringRmTransition2() throws ParseMutationException, ApplyMutationException {
        addTransitions();
        TransitionMutation mutation = TransitionMutation.createFromString("rm transition in block from state0 to state1 with [x > 1] and after(0, 5) e^(1, 6) and \"x = 1\" and probability 0.5 and computes (12, 42)");
        /*TraceManager.addDev(block.getStateMachine().getState(0).toString());
        TraceManager.addDev(block.getStateMachine().getState(0).getNexts().get(0).toString());
        TraceManager.addDev(block.getStateMachine().getState(0).getNexts().get(1).toString());*/
        AvatarTransition trans = mutation.getElement(as);
        TraceManager.addDev(trans.toString());
        mutation.apply(as);
        assertTrue(block.getStateMachine().getState(0).getNexts().size()==1);
        TraceManager.addDev(block.getStateMachine().getState(0).getNexts().get(0).toString());
    }

    @Test
    public void createFromStringMdTransition() throws ParseMutationException, ApplyMutationException {
        addTransitions();
        TransitionMutation mutation = TransitionMutation.createFromString("md transition in block from state0 to state1 with \"\" to \"x = x+2\"");
        AvatarTransition trans = mutation.getElement(as);
        assertTrue(trans.getNbOfAction() == 0);
        TraceManager.addDev(trans.toString());
        mutation.apply(as);
        assertTrue(trans.getNbOfAction() == 1);
        TraceManager.addDev(trans.toString());
    }

    public void addActionOnSignal() throws ParseMutationException, ApplyMutationException {
        addSignal();
        AvatarMutation mutation = AvatarMutation.createFromString("add action on signal aaos in block with cin(x, y)");
        mutation.apply(as);
    }

    @Test
    public void createFromStringAddActionOnSignal() throws ParseMutationException, ApplyMutationException {
        addActionOnSignal();
        AvatarStateMachine asm = block.getStateMachine();
        TraceManager.addDev("" + asm.getListOfElements().size());
        TraceManager.addDev("" + asm.getListOfElements().get(0).toString());
        assertTrue(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void createFromStringRmActionOnSignal1() throws ParseMutationException, ApplyMutationException {
        addActionOnSignal();
        addStates();
        AvatarMutation mutation = AvatarMutation.createFromString("add transition in block from state0 to aaos");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("add transition in block from aaos to state1");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("rm action on signal in block with cin");
        AvatarStateMachine asm = block.getStateMachine();
        List<AvatarStateMachineElement> elements = asm.getListOfElements();
        List<AvatarStateMachineElement> transitions = new ArrayList<>();
        for (AvatarStateMachineElement element :elements){
            if (element instanceof AvatarActionOnSignal){
                if (((AvatarActionOnSignal) element).getSignal().equals(block.getSignalByName("cin"))){
                    transitions = asm.getElementsLeadingTo(element);
                    transitions.addAll(element.getNexts());
                    break;
                }
            }
        }
        mutation.apply(as);
        for (AvatarStateMachineElement transition : transitions){
            assertFalse(asm.getListOfElements().contains(transition));
        }
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void createFromStringRmActionOnSignal2() throws ParseMutationException, ApplyMutationException {
        addActionOnSignal();
        addStates();
        AvatarMutation mutation = AvatarMutation.createFromString("add transition in block from state0 to aaos");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("add transition in block from aaos to state1");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("rm action on signal aaos in block");
        AvatarStateMachine asm = block.getStateMachine();
        List<AvatarStateMachineElement> elements = asm.getListOfElements();
        List<AvatarStateMachineElement> transitions = new ArrayList<>();
        for (AvatarStateMachineElement element :elements){
            if (element instanceof AvatarActionOnSignal){
                if (((AvatarActionOnSignal) element).getSignal().equals(block.getSignalByName("cin"))){
                    transitions = asm.getElementsLeadingTo(element);
                    transitions.addAll(element.getNexts());
                    break;
                }
            }
        }
        mutation.apply(as);
        for (AvatarStateMachineElement transition : transitions){
            assertFalse(asm.getListOfElements().contains(transition));
        }
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
    }

    @Test
    public void createFromStringMdActionOnSignal() throws ParseMutationException, ApplyMutationException {
        addActionOnSignal();
        AvatarMutation mutation = AvatarMutation.createFromString("md action on signal aaos in block to cout()");
        AvatarStateMachine asm = block.getStateMachine();
        assertFalse(asm.isSignalUsed(block.getSignalByName("cout")));
        mutation.apply(as);
        assertFalse(asm.isSignalUsed(block.getSignalByName("cin")));
        assertTrue(asm.isSignalUsed(block.getSignalByName("cout")));
    }

    public AvatarTransition addActions() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addTransitions();
        AvatarMutation mutation = AvatarMutation.createFromString("add action \"x = x+2\" in block transition trans");
        mutation.apply(as);
        return trans;
    }

    @Test
    public void createFromStringAddAction() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addActions();
        assertTrue(trans.getNbOfAction() == 2);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void createFromStringRmAction1() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addActions();
        AvatarMutation mutation = AvatarMutation.createFromString("rm action at 1 in block transition trans");
        mutation.apply(as);
        assertTrue(trans.getNbOfAction() == 1);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void createFromStringRmAction2() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addActions();
        AvatarMutation mutation = AvatarMutation.createFromString("rm transition in block from state0 to state1 with []");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("rm action at 1 in block transition from state0 to state1");
        mutation.apply(as);
        assertTrue(trans.getNbOfAction() == 1);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void createFromStringMdAction() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addActions();
        AvatarMutation mutation = AvatarMutation.createFromString("md action at 0 in block transition trans to \"x=-3\"");
        mutation.apply(as);
        TraceManager.addDev(trans.toString());
    }

    @Test
    public void createFromStringSwapAction() throws ParseMutationException, ApplyMutationException {
        AvatarTransition trans = addActions();
        AvatarMutation mutation = AvatarMutation.createFromString("swap action at 0 and at 1 in block transition trans");
        mutation.apply(as);
        TraceManager.addDev(trans.toString());
    }

    public AvatarRandom addRandom() throws ParseMutationException, ApplyMutationException {
        RandomMutation mutation = RandomMutation.createFromString("add random rand in block with x (5, 15)");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddRandom() throws ParseMutationException, ApplyMutationException {
        AvatarRandom rand = addRandom();
        assertTrue(rand.getVariable().equals("x"));
        TraceManager.addDev(rand.getNiceName());
    }

    @Test
    public void createFromStringRmRandom() throws ParseMutationException, ApplyMutationException {
        RandomMutation mutation0 = RandomMutation.createFromString("add random in block with x (-5, 10)");
        mutation0.apply(as);
        addRandom();
        AvatarMutation mutation = AvatarMutation.createFromString("rm random in block with x (5, 15)");
        mutation.apply(as);
        assertTrue(mutation0.getElement(as) != null);
    }

    @Test
    public void createFromStringMdRandom() throws ParseMutationException, ApplyMutationException {
        AvatarRandom rand = addRandom();
        AvatarMutation mutation = AvatarMutation.createFromString("md random rand in block to y");
        mutation.apply(as);
        assertTrue(rand.getVariable().equals("y"));
    }

    public AvatarSetTimer addSetTimer() throws ParseMutationException, ApplyMutationException {
        AvatarMutation.createFromString("add attribute timer timer in block").apply(as);
        SetTimerMutation mutation = SetTimerMutation.createFromString("add set timer in block with timer at 10");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddSetTimer() throws ParseMutationException, ApplyMutationException {
        AvatarSetTimer set = addSetTimer();
        assertTrue(set != null);
        TraceManager.addDev(set.getTimer().toString());
    }

    @Test
    public void createFromStringRmSetTimer() throws ParseMutationException, ApplyMutationException {
        addSetTimer();
        SetTimerMutation mutation = SetTimerMutation.createFromString("rm set timer in block with timer at 10");
        assertFalse(mutation.getElement(as) == null);
        TraceManager.addDev(mutation.getElement(as).toString());
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void createFromStringMdSetTimer() throws ParseMutationException, ApplyMutationException {
        AvatarSetTimer timer = addSetTimer();
        AvatarMutation mutation = AvatarMutation.createFromString("md set timer in block with timer at 10 to timer at 15");
        mutation.apply(as);
        TraceManager.addDev(timer.getTimerValue());
        assertTrue(timer.getTimerValue().equals("15"));
        
    }

    public AvatarResetTimer addResetTimer() throws ParseMutationException, ApplyMutationException {
        AvatarMutation.createFromString("add attribute timer timer in block").apply(as);
        ResetTimerMutation mutation = ResetTimerMutation.createFromString("add reset timer in block with timer");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddResetTimer() throws ParseMutationException, ApplyMutationException {
        AvatarResetTimer set = addResetTimer();
        assertTrue(set != null);
        TraceManager.addDev(set.getTimer().toString());
    }

    @Test
    public void createFromStringRmResetTimer() throws ParseMutationException, ApplyMutationException {
        addResetTimer();
        ResetTimerMutation mutation = ResetTimerMutation.createFromString("rm reset timer in block with timer");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void createFromStringMdResetTimer() throws ParseMutationException, ApplyMutationException {
        AvatarResetTimer timer = addResetTimer();
        AvatarMutation.createFromString("add attribute timer timer2 in block").apply(as);
        AvatarMutation mutation = AvatarMutation.createFromString("md reset timer in block with timer to timer2");
        mutation.apply(as);
        assertTrue(timer.getTimer().getName().equals("timer2"));
    }

    public AvatarExpireTimer addExpireTimer() throws ParseMutationException, ApplyMutationException {
        AvatarMutation.createFromString("add attribute timer timer in block").apply(as);
        ExpireTimerMutation mutation = ExpireTimerMutation.createFromString("add expire timer in block with timer");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddExpireTimer() throws ParseMutationException, ApplyMutationException {
        AvatarExpireTimer set = addExpireTimer();
        assertTrue(set != null);
        TraceManager.addDev(set.getTimer().toString());
    }

    @Test
    public void createFromStringRmExpireTimer() throws ParseMutationException, ApplyMutationException {
        addExpireTimer();
        ExpireTimerMutation mutation = ExpireTimerMutation.createFromString("rm expire timer in block with timer");
        assertFalse(mutation.getElement(as) == null);
        mutation.apply(as);
        assertTrue(mutation.getElement(as) == null);
    }

    @Test
    public void createFromStringMdExpireTimer() throws ParseMutationException, ApplyMutationException {
        AvatarExpireTimer timer = addExpireTimer();
        AvatarMutation.createFromString("add attribute timer timer2 in block").apply(as);
        AvatarMutation mutation = AvatarMutation.createFromString("md expire timer in block with timer to timer2");
        mutation.apply(as);
        assertTrue(timer.getTimer().getName().equals("timer2"));
    }

    public AvatarRelation addRelation() throws ParseMutationException, ApplyMutationException {
        addBlock();
        RelationMutation mutation = RelationMutation.createFromString("add public AMS blocking lossy broadcast link maxFIFO = 20 between block and block0");
        mutation.apply(as);
        return mutation.getElement(as);
    }

    @Test
    public void createFromStringAddRelation() throws ParseMutationException, ApplyMutationException {
        assertTrue(as.getRelations().size() == 0);
        AvatarRelation relation = addRelation();
        assertTrue(as.getRelations().size() == 1);
        assertTrue(relation.isAMS());
        assertFalse(relation.isPrivate());
        assertTrue(relation.isBlocking());
        assertTrue(relation.isLossy());
        assertTrue(relation.isBroadcast());
        assertTrue(relation.getSizeOfFIFO() == 20);
    }

    @Test
    public void createFromStringRmRelation() throws ParseMutationException, ApplyMutationException {
        addRelation();
        AvatarMutation mutation = AvatarMutation.createFromString("rm link between block and block0");
        mutation.apply(as);
        assertTrue(as.getRelations().size() == 0);
    }

    @Test
    public void createFromStringMdRelation() throws ParseMutationException, ApplyMutationException {
        AvatarRelation relation = addRelation();
        AvatarMutation mutation = AvatarMutation.createFromString("md link between block and block0 to no AMS");
        assertTrue(relation.isAMS());
        mutation.apply(as);
        assertFalse(relation.isAMS());
    }

    public AvatarRelation addConnection() throws ParseMutationException, ApplyMutationException {
        AvatarRelation relation = addRelation();
        addSignal();
        AvatarMutation mutation = AvatarMutation.createFromString("add input signal cin0() in block0");
        mutation.apply(as);
        mutation = AvatarMutation.createFromString("add connection from cout in block to cin0 in block0");
        mutation.apply(as);
        return relation;
    }

    @Test
    public void createFromStringAddConnection() throws ParseMutationException, ApplyMutationException {
        AvatarRelation relation = addConnection();
        assertTrue(relation.nbOfSignals() == 1);
    }

    @Test
    public void createFromStringRmConnection() throws ParseMutationException, ApplyMutationException {
        AvatarRelation relation = addConnection();
        AvatarMutation mutation = AvatarMutation.createFromString("rm connection from cout in block to cin0 in block0");
        mutation.apply(as);
        assertTrue(relation.nbOfSignals() == 0);
    }

    @Test
    public void testParseMutationException() throws ParseMutationException, ApplyMutationException {
        try {
            AvatarMutation mutation = AvatarMutation.createFromString("attach");
            mutation.apply(as);
        } catch (Exception e) {
            TraceManager.addDev(e.getMessage());
        }
    }

    @Test
    public void testApplyMutationException() throws ParseMutationException, ApplyMutationException {
        try {
            AvatarMutation mutation = AvatarMutation.createFromString("attach block to block0");
            mutation.apply(as);
        } catch (Exception e) {
            TraceManager.addDev(e.getMessage());
        }
    }
}
