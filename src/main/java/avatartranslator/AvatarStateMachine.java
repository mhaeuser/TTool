/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package avatartranslator;

import avatartranslator.intboolsolver.AvatarIBSolver;
import myutil.MyMath;
import myutil.TraceManager;

import java.util.*;


/**
 * Class AvatarStateMachine
 * State machine, with composite states
 * Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/05/2010
 */
public class AvatarStateMachine extends AvatarElement {
    private static int ID_ELT = 0;
    // To be used by code generator for fast access to states
    public AvatarStateElement[] allStates;
    protected List<AvatarStateMachineElement> elements;
    protected AvatarStartState startState;
    protected List<AvatarStateMachineElement> states;
    protected AvatarStateMachineOwner block;

    public AvatarStateMachine(AvatarStateMachineOwner _block, String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        block = _block;
        elements = new LinkedList<AvatarStateMachineElement>();
    }

    public AvatarStateMachineOwner getOwner() {
        return block;
    }

    public AvatarStartState getStartState() {
        return startState;
    }

    public void setStartState(AvatarStartState _state) {
        startState = _state;
    }

    public int getNbOfStatesElement() {
        int cpt = 0;
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarStateElement) {
                cpt++;
            }
        }
        return cpt;
    }

    public void clear() {
        elements.clear();
        startState = null;
        allStates = null;
        states = null;
    }

    public void makeBasicSM(AvatarStateMachineOwner owner) {
        elements.clear();

        if (startState == null) {
            startState = new AvatarStartState("StartState", null, owner);
        } else {
            startState.removeAllNexts();
        }
        addElement(startState);

        AvatarTransition at = new AvatarTransition(owner, "Transition", startState.getReferenceObject());
        AvatarStopState stopS = new AvatarStopState("StopState", startState.getReferenceObject(), owner);
        addElement(at);
        addElement(stopS);
        startState.addNext(at);
        at.addNext(stopS);

    }

    public boolean isBasicStateMachine() {
        if (startState == null) {
            return true;
        }

        if (elements.size() > 3) {
            return false;
        }

        boolean hasStartState = false, hasStopState = false, hasBasicTransition = false;
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarStartState) {
                hasStartState = true;
            }
            if (asme instanceof AvatarStartState) {
                hasStopState = true;
            }
            if (asme instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) asme;
                if (at.isEmpty()) {
                    hasBasicTransition = true;
                }
            }
        }

        return hasStartState && hasStopState && hasBasicTransition;

    }

    /**
     * Make sure that there is a start state, a stop state and that all
     * elements apart from regular states are followed by a stop or a next
     */
    public void makeCorrect(AvatarStateMachineOwner owner) {
        if (startState == null) {
            TraceManager.addDev("Null start state");
            makeBasicSM(owner);
            return;
        }

        // Remove nexts when not in the list of elements
        for (AvatarStateMachineElement asme : getListOfElements()) {
            ArrayList<AvatarStateMachineElement> removedNext = new ArrayList<>();
            for (AvatarStateMachineElement nextElt : asme.getNexts()) {
                if (!(getListOfElements().contains(nextElt))) {
                    TraceManager.addDev("Removing: " + nextElt);
                    removedNext.add(nextElt);
                }
            }
            asme.getNexts().removeAll(removedNext);
        }

        // We check that all elements are reachable from start.
        HashSet<AvatarStateMachineElement> reachable = new HashSet<>();
        ArrayList<AvatarStateMachineElement> pending = new ArrayList<>();

        pending.add(startState);


        while (pending.size() > 0) {
            AvatarStateMachineElement current = pending.get(0);
            reachable.add(current);
            pending.remove(0);
            if (current.getNexts().size() == 0) {
                if (!((current instanceof AvatarStopState) || (current instanceof AvatarState))) {
                    // We need to add a next
                    if (current instanceof AvatarTransition) {
                        AvatarStopState stopS = new AvatarStopState("StopState", current.getReferenceObject(), owner);
                        addElement(stopS);
                        current.addNext(stopS);
                    } else {
                        AvatarTransition at = new AvatarTransition(owner, "Transition", current.getReferenceObject());
                        AvatarStopState stopS = new AvatarStopState("StopState", current.getReferenceObject(), owner);
                        addElement(at);
                        addElement(stopS);
                        current.addNext(at);
                        at.addNext(stopS);
                    }
                }
            }

            for (AvatarStateMachineElement nextElt : current.getNexts()) {
                if (!(reachable.contains(nextElt))) {
                    pending.add(nextElt);
                }
            }
        }

        // We remove all elements that are not reachable
        ArrayList<AvatarElement> toRemove = new ArrayList<>();
        for (AvatarStateMachineElement asme : getListOfElements()) {
            if (!(reachable.contains(asme))) {
                TraceManager.addDev("Not reachable: " + asme);
                toRemove.add(asme);
            }
        }
        elements.removeAll(toRemove);

    }

    public void addElement(AvatarStateMachineElement _element) {
        if (_element != null) {
            elements.add(_element);
            //TraceManager.addDev("Adding element " + _element);
            states = null;
        } else {
            TraceManager.addDev("NULL element found " + _element);
        }
    }

    public void removeElement(AvatarStateMachineElement _element) {
        elements.remove(_element);
        states = null;
    }

    public List<AvatarStateMachineElement> getListOfElements() {
        return elements;
    }

    private void makeStates() {
        states = new LinkedList<AvatarStateMachineElement>();
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarState) {
                states.add(asme);
            }
        }
    }

    public AvatarState getStateByName(String _name) {
        for (AvatarElement ae : elements) {
            if (ae instanceof AvatarState) {
                if (ae.getName().compareTo(_name) == 0) {
                    return (AvatarState) ae;
                }
            }
        }
        return null;
    }

    public void makeAllStates() {
        int cpt = 0;
        allStates = new AvatarStateElement[getNbOfStatesElement()];
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarStateElement) {
                allStates[cpt] = (AvatarStateElement) asme;
                cpt++;
            }
        }
    }

    public int stateNb() {
        if (states == null) {
            makeStates();
        }

        return states.size();
    }

    public int getNbOfASMGraphicalElements() {
        int cpt = 0;
        for (AvatarElement elt : elements) {
            if (elt.getReferenceObject() != null) {
                cpt++;
            }
        }
        return cpt;
    }

    public AvatarState getState(int index) {
        if (states == null) {
            makeStates();
        }

        try {
            return (AvatarState) (states.get(index));
        } catch (Exception e) {
        }
        return null;
    }

    public boolean isSignalUsed(AvatarSignal _sig) {
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) asme;
                if (aaos.getSignal() == _sig) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isTimerUsed(AvatarAttribute _timer) {
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarTimerOperator) {
                AvatarTimerOperator ato = (AvatarTimerOperator) asme;
                if (ato.getTimer() == _timer) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Analyze the usage of a regular attribute (int, bool)
     *
     * @param _aa
     * @return
     */
    public boolean isRegularAttributeUsed(AvatarAttribute _aa) {
        boolean ret;
        AvatarIBSolver.clearAttributes();
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarTransition) {
                // Must check the guard, the delays and all the actions
                AvatarTransition at = (AvatarTransition) asme;

                if (at.isGuarded()) {
                    ret = isInExpression(at.getGuard().toString(), _aa);
                    if (ret) {
                        return true;
                    }
                }

                if (at.hasDelay()) {
                    ret = isInExpression(at.getMinDelay().toString(), _aa);
                    if (ret) {
                        return true;
                    }
                    ret = isInExpression(at.getMaxDelay().toString(), _aa);
                    if (ret) {
                        return true;
                    }
                }

                for (AvatarAction act : at.getActions()) {
                    ret = isInExpression(act.toString(), _aa);
                    if (ret) {
                        return true;
                    }
                }

            } else if (asme instanceof AvatarActionOnSignal) {
                for (String s : ((AvatarActionOnSignal) asme).getValues()) {
                    ret = isInExpression(s, _aa);
                    if (ret) {
                        return true;
                    }
                }

            } else if (asme instanceof AvatarRandom) {
                AvatarRandom ar = (AvatarRandom) asme;
                String s = ar.getVariable();
                ret = isInExpression(s, _aa);
                if (ret) {
                    return true;
                }
                s = ar.getMinValue();
                ret = isInExpression(s, _aa);
                if (ret) {
                    return true;
                }
                s = ar.getMaxValue();
                ret = isInExpression(s, _aa);
                if (ret) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isInExpression(String expr, AvatarAttribute _aa) {
        return AvatarIBSolver.parser.indexOfVariable(expr, _aa.getName()) > -1;
    }

    private int getSimplifiedElementsAux(LinkedHashMap<AvatarStateMachineElement, Integer> simplifiedElements, Set<AvatarStateMachineElement> visited,
                                         AvatarStateMachineElement root, int counter) {
        if (visited.contains(root)) {
            Integer name = simplifiedElements.get(root);
            if (name == null) {
                if (root == this.startState)
                    simplifiedElements.put(root, Integer.valueOf(0));
                else {
                    counter++;
                    simplifiedElements.put(root, Integer.valueOf(counter));
                }
            }
        } else {
            visited.add(root);
            for (AvatarStateMachineElement asme : root.nexts)
                counter = this.getSimplifiedElementsAux(simplifiedElements, visited, asme, counter);
        }

        return counter;
    }

    public LinkedHashMap<AvatarStateMachineElement, Integer> getSimplifiedElements() {
        LinkedHashMap<AvatarStateMachineElement, Integer> simplifiedElements = new LinkedHashMap<AvatarStateMachineElement, Integer>();
        this.getSimplifiedElementsAux(simplifiedElements, new HashSet<AvatarStateMachineElement>(), startState, 0);
        return simplifiedElements;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("State machine Id=" + getID() + "\n");

        for (AvatarStateMachineElement element : elements) {
            sb.append("\t" + element.getClass() + "->" + element.toString() + "\n");
        }


        return sb.toString();
    }

    public String toStringRecursive() {
        AvatarStartState ass = getStartState();
        if (ass == null) {
            return "empty asm";
        }

        return toStringRecursiveElt(ass);
    }

    public String toStringRecursiveElt(AvatarStateMachineElement _asme) {
        String ret = "";
        ret += "* " + _asme.toStringExtendedID() + "\n";

        for (AvatarStateMachineElement asme : _asme.getNexts()) {
            ret += "\tnext: " + asme.toStringExtendedID() + "\n";

        }
        for (AvatarStateMachineElement asme : _asme.getNexts()) {
            ret += toStringRecursiveElt(asme);
        }

        return ret;

    }


    // Add missing implicit states.
    public void makeFullStates(AvatarBlock _block) {
        addStatesToEmptyNonTerminalEmptyNext(_block);
        addStateAfterActionOnSignal(_block);
        addStatesToTransitionsBetweenTwoNonStates(_block);
        addStatesToActionTransitions(_block);
        addStatesToNonEmptyTransitionsBetweenNonStateToState(_block);
    }


    private void addStatesToEmptyNonTerminalEmptyNext(AvatarBlock _b) {
        List<AvatarStateMachineElement> toConsider = new ArrayList<AvatarStateMachineElement>();
        for (AvatarStateMachineElement elt : elements) {
            if (!(elt instanceof AvatarStopState)) {
                if (elt.getNext(0) == null) {
                    // Missing state
                    toConsider.add(elt);
                }
            }
        }

        for (AvatarStateMachineElement elt : toConsider) {
            AvatarStopState stopMe = new AvatarStopState("stopCreated", elt.getReferenceObject(), _b);
            addElement(stopMe);
            AvatarTransition tr = new AvatarTransition(_b, "trForStopCreated", elt.getReferenceObject());
            addElement(tr);
            elt.addNext(tr);
            tr.addNext(stopMe);
        }

    }

    private void addStateAfterActionOnSignal(AvatarBlock _block) {
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarActionOnSignal) {
                if (elt.getNext(0) instanceof AvatarTransition) {
                    AvatarTransition tr = (AvatarTransition) elt.getNext(0);
                    // We create an intermediate state
                    AvatarState state = new AvatarState("IntermediateState4__" + id, elt.getReferenceObject(), _block);
                    state.setCommit(true);
                    toAdd.add(state);
                    AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState4__" + id, elt.getReferenceObject());
                    toAdd.add(at1);

                    elt.removeAllNexts();
                    elt.addNext(at1);
                    at1.addNext(state);
                    state.addNext(tr);

                    id++;
                }
            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }
    }


    private void addStatesToNonEmptyTransitionsBetweenNonStateToState(AvatarBlock _block) {
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;

                if (tr.hasDelay() || tr.isGuarded() || tr.hasAction()) {
                    previous = getPreviousElementOf(elt);
                    next = elt.getNext(0);

                    // If the next is a state, but not the previous one
                    if ((previous != null) && (next != null)) {
                        if ((!(previous instanceof AvatarStateElement)) && (next instanceof AvatarStateElement)) {
                            // We create an intermediate state
                            AvatarState state = new AvatarState("IntermediateState1__" + id, elt.getReferenceObject(), _block);
                            state.setCommit(true);
                            toAdd.add(state);
                            AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState1__" + id, elt.getReferenceObject());
                            toAdd.add(at1);

                            previous.removeAllNexts();
                            previous.addNext(at1);
                            at1.addNext(state);
                            state.addNext(tr);

                            id++;
                        }
                    }
                }

            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }

    }

    private void addStatesToTransitionsBetweenTwoNonStates(AvatarBlock _block) {
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;
                previous = getPreviousElementOf(elt);
                next = elt.getNext(0);

                // If the next and previous are non states
                if ((previous != null) && (next != null)) {
                    if ((!(previous instanceof AvatarStateElement)) && (!(next instanceof AvatarStateElement))) {
                        // We create an intermediate state
                        AvatarState state = new AvatarState("IntermediateState2__" + id, elt.getReferenceObject(), _block);
                        toAdd.add(state);
                        AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState2__" + id, elt.getReferenceObject());
                        toAdd.add(at1);


                        previous.removeAllNexts();
                        previous.addNext(at1);
                        at1.addNext(state);
                        state.addNext(tr);

                        id++;
                    }
                }

            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }

    }


    // Hanlding transitions with actions which have a non state
    // after

    // Then, handling transitions with actions which have a non state
    // before
    private void addStatesToActionTransitions(AvatarBlock _block) {
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;
        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;

                // tr with actions?
                if (tr.getNbOfAction() > 0) {
                    previous = getPreviousElementOf(elt);
                    next = elt.getNext(0);

                    if (!(next instanceof AvatarState)) {
                        // We create an intermediate state
                        AvatarState state = new AvatarState("IntermediateState3__" + id, elt.getReferenceObject(), _block);
                        state.setCommit(true);
                        toAdd.add(state);
                        AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState3__" + id, elt.getReferenceObject());
                        toAdd.add(at1);

                        tr.removeAllNexts();
                        tr.addNext(state);
                        state.addNext(at1);
                        at1.addNext(next);

                        id++;
                    }
                }
            }

        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }
        toAdd.clear();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;

                // tr with actions?
                if (tr.getNbOfAction() > 0) {
                    previous = getPreviousElementOf(elt);
                    if (!(previous instanceof AvatarStateElement)) {
                        // We create an intermediate state
                        AvatarState state = new AvatarState("IntermediateState__" + id, elt.getReferenceObject(), _block);
                        state.setCommit(true);
                        toAdd.add(state);
                        AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState__" + id, elt.getReferenceObject());
                        toAdd.add(at1);

                        previous.removeAllNexts();
                        previous.addNext(at1);
                        at1.addNext(state);
                        state.addNext(tr);
                        id++;
                    }
                }
            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }

    }

    public void removeRandoms(AvatarBlock _block) {
        int id = 0;
        List<AvatarStateMachineElement> toRemove = new ArrayList<AvatarStateMachineElement>();
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarRandom) {
                AvatarRandom random = (AvatarRandom) elt;
                previous = getPreviousElementOf(elt);
                next = elt.getNext(0);
                toRemove.add(elt);

                // Is the variable of random used in expression?
                // If yes, they must be removed. If not, we can directly use the expressions.
                String minExpr = random.getMinValue();
                String maxExpr = random.getMaxValue();
                String var = random.getVariable();

                String maxExprToBeUsed = "";


                // Creating elements
                AvatarTransition at1 = new AvatarTransition(_block, "Transition1ForRandom__ " + elt.getName() + "__" + id, elt.getReferenceObject());

                // If maxExpr contains var then use __tmpMax instead of var in maxExpr


                if (AvatarIBSolver.parser.indexOfVariable(maxExpr, var) > -1) {
                    AvatarAttribute aa = _block.addIntegerAttribute("tmpMaxRandom__");
                    maxExprToBeUsed = aa.getName();
                    at1.addAction(aa.getName() + " = " + maxExpr);
                } else {
                    maxExprToBeUsed = maxExpr;
                }

                at1.addAction(random.getVariable() + "=" + minExpr);
                AvatarState randomState = new AvatarState("StateForRandom__" + elt.getName() + "__" + id, elt.getReferenceObject(), _block);
                AvatarState beforeRandom = new AvatarState("StateBeforeRandom__" + elt.getName() + "__" + id, elt.getReferenceObject(), _block);
                AvatarTransition at2 = new AvatarTransition(_block, "Transition2ForRandom__" + elt.getName() + "__" + id, elt.getReferenceObject());
                at2.setGuard("[" + random.getVariable() + " < " + maxExprToBeUsed + "]");
                at2.addAction(random.getVariable() + "=" + random.getVariable() + " + 1");
                AvatarTransition at3 = new AvatarTransition(_block, "Transition3ForRandom__ " + elt.getName() + "__" + id, elt.getReferenceObject());
                AvatarState afterRandom = new AvatarState("StateAfterRandom__" + elt.getName() + "__" + id, elt.getReferenceObject(), _block);

                // Adding elements
                toAdd.add(at1);
                toAdd.add(randomState);
                toAdd.add(beforeRandom);
                toAdd.add(at2);
                toAdd.add(at3);
                toAdd.add(afterRandom);

                // Linking elements
                if (previous != null) {
                    previous.removeAllNexts();
                    previous.addNext(beforeRandom);
                }
                beforeRandom.addNext(at1);
                at1.addNext(randomState);
                randomState.addNext(at2);
                randomState.addNext(at3);
                at2.addNext(randomState);
                at3.addNext(afterRandom);
                afterRandom.addNext(next);

                id++;

            }
        }

        for (AvatarStateMachineElement trash : toRemove) {
            elements.remove(trash);
        }

        for (AvatarStateMachineElement newOnes : toAdd) {
            elements.add(newOnes);
        }
    }


    // Assumes no after clause on composite relation
    public void removeCompositeStates(AvatarBlock _block) {
        //TraceManager.addDev("\n-------------- Remove composite states ---------------\n");

        /*LinkedList<AvatarState> lists =*/
        List<AvatarStartState> compositeStateToBeRemoved = removeAllInternalStartStates();
        removeAllInternalStopStatesByRegularStates();

        AvatarTransition at = getAvatarCompositeTransition();

        if (at == null) {
            return;
        }

        // We modify all composite states with intermediate states
        for (int i = 0; i < elements.size(); i++) {
            AvatarStateMachineElement element = elements.get(i);
            if (element instanceof AvatarState) {
                modifyStateForCompositeSupport((AvatarState) element);
            }
        }

        // For each composite transition: We link it to all the subStates of the current state
        AvatarState src;

        Vector<AvatarTransition> transitions = getAvatarCompositeTransitions();

        for (AvatarTransition atc : transitions) {
            src = (AvatarState) (getPreviousElementOf(atc));
            atc.setAsVerifiable(false);
            for (int j = 0; j < elements.size(); j++) {
                AvatarStateMachineElement elt = elements.get(j);
                if ((elt instanceof AvatarState) && (elt.hasInUpperState(src))) {
                    AvatarTransition att = cloneCompositeTransition(atc);
                    elt.addNext(att);
                    att.setAsVerifiable(false);
                }
            }
        }

        /*while (((at = getAvatarCompositeTransition()) != null)) {
            src = (AvatarState) (getPreviousElementOf(at));
            elements.remove(at);

            // Link a clone of the transition  to all internal states

            for (int j = 0; j < elements.size(); j++) {
                AvatarStateMachineElement elt = elements.get(j);
                if ((elt instanceof AvatarState) && (elt.hasInUpperState(src))) {
                    AvatarTransition att = cloneCompositeTransition(at);
                    elt.addNext(att);
                    att.setAsVerifiable(false);
                    at.setAsVerifiable(false);
                }
            }

        }*/

        // Removing composite states
        /*for(AvatarStateMachineElement elt: compositeStateToBeRemoved) {
            elements.remove(elt);
        }*/

    }


    private void removeAllInternalStopStatesByRegularStates() {
        Vector<AvatarStopState> v = new Vector<>();
        Vector<AvatarState> toAdd = new Vector<>();

        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) element;
                //TraceManager.addDev("at? element=" + element);
                // Transition fully in the internal state?
                if (element.getNext(0) instanceof AvatarStopState) {
                    AvatarStopState stop = (AvatarStopState) (element.getNext(0));
                    if (stop.getState() != null) {
                        AvatarState newState = new AvatarState(stop.getName(), stop.getReferenceObject(), block);
                        element.removeNext(0);
                        element.addNext(newState);
                        toAdd.add(newState);
                        v.add(stop);
                    }

                }
            }
        }
        for (AvatarStopState s : v) {
            elements.remove(s);
        }
        for (AvatarState st : toAdd) {
            elements.add(st);
        }
    }


    private void modifyStateForCompositeSupport(AvatarState _state) {
        // Each time there is a transition with an after or more than one action, we must rework the transition
        // We first gather all transitions internal to that state

        Vector<AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) element;
                //TraceManager.addDev("at? element=" + element);
                // Transition fully in the internal state?
                if (element.getNext(0).hasInUpperState(_state) == true) {
                    AvatarStateMachineElement previous = getPreviousElementOf(element);
                    if (previous.hasInUpperState(_state) == true) {
                        if (!(at.isEmpty())) {
                            v.add(at);
                            at.setNotCheckable();
                        }
                    }
                }
            }
        }

        for (AvatarStateMachineElement element : v) {
            //TraceManager.addDev(">" + element + "<");
            splitAvatarTransition((AvatarTransition) element, _state);
        }

    }


    private void splitAvatarTransition(AvatarTransition _at, AvatarState _currentState) {
        if (_at.hasDelay()) {
            AvatarStateMachineElement element = getPreviousElementOf(_at);
            if (element.hasInUpperState(_currentState) == true) {
                if (!(element instanceof AvatarState)) {
                    // Must add an intermediate state
                    String tmp = findUniqueStateName("splitstate_after__");
                    AvatarState as = new AvatarState(tmp, _currentState.getReferenceObject(), block);
                    addElement(as);
                    as.setHidden(true);
                    as.setState(_currentState);
                    AvatarTransition atn = new AvatarTransition(_at.getBlock(), "splittransition_after", null);
                    addElement(atn);
                    element.removeNext(_at);
                    element.addNext(atn);
                    atn.addNext(as);
                    as.addNext(_at);
                    splitAvatarTransition(_at, _currentState);
                }
            }
        } else {

            if (_at.getNbOfAction() > 1) {
                //TraceManager.addDev("New split state");
                String tmp = findUniqueStateName("splitstate_action__");
                AvatarState as = new AvatarState(tmp, null, block);
                as.setHidden(true);
                as.setState(_currentState);
                AvatarTransition at = (AvatarTransition) (_at.basicCloneMe(block));
                _at.removeAllActionsButTheFirstOne();
                at.removeFirstAction();
                at.addNext(_at.getNext(0));
                _at.removeAllNexts();
                _at.addNext(as);
                as.addNext(at);
                addElement(as);
                addElement(at);

                splitAvatarTransition(_at, _currentState);
            }
        }
    }

//
//    private List<AvatarTransition> getAllAvatarCompositeTransitions() {
//        List<AvatarTransition> ats = new ArrayList<AvatarTransition>();
//        for (AvatarStateMachineElement element : elements) {
//            if (element instanceof AvatarTransition) {
//                if ((isACompositeTransition((AvatarTransition) element))) {
//                    ats.add((AvatarTransition) element);
//                }
//            }
//        }
//
//        return ats;
//    }


    private void addFullInternalStates(AvatarState state, AvatarTransition _at) {
        // First:split internal transitions
        Vector<AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

        for (AvatarStateMachineElement element : elements) {
            //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
            if (element instanceof AvatarTransition) {
                //TraceManager.addDev("at? element=" + element);
                if (element.getNext(0).hasInUpperState(state) == true) {
                    if (getPreviousElementOf(element).hasInUpperState(state) == true) {
                        v.add(element);
                    }
                }
            } else if (element.hasInUpperState(state) == true) {
                // We found a candidate!
                if (element != _at) {
                    v.add(element);
                }
            }
        }

        //TraceManager.addDev("*** Analyzing components in state " + state);
        // Split avatar transitions
        for (AvatarStateMachineElement element : v) {
            //TraceManager.addDev(">" + element + "<");
            if (element instanceof AvatarTransition) {
                splitAvatarTransition((AvatarTransition) element, state);
            }
        }
    }

    private AvatarTransition getAvatarCompositeTransition() {

        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                if ((isACompositeTransition((AvatarTransition) element))) {
                    return (AvatarTransition) element;

                }
            }
        }

        return null;
    }

    private Vector<AvatarTransition> getAvatarCompositeTransitions() {

        Vector<AvatarTransition> transitions = new Vector<>();

        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                if ((isACompositeTransition((AvatarTransition) element))) {
                    transitions.add((AvatarTransition) element);
                }
            }
        }

        return transitions;
    }


    // Checks whether the previous element is a state with an internal state machine
    public boolean isACompositeTransition(AvatarTransition _at) {
        AvatarStateMachineElement element = getPreviousElementOf(_at);
        if (element == null) {
            return false;
        }

        if (!(element instanceof AvatarState)) {
            return false;
        }

        AvatarState state = (AvatarState) element;

        if (!hasInternalComponents(state)) {
            return false;
        }

        AvatarStateMachineElement next = _at.getNext(0);
        if (next.hasInUpperState(state)) {
            return false;
        }

        return true;
    }

    private boolean hasInternalComponents(AvatarState _state) {
        for (AvatarStateMachineElement element : elements) {
            if (element.getState() == _state) {
                return true;
            }
        }

        return false;
    }

    /*private void removeCompositeTransition(AvatarTransition _at, AvatarBlock _block) {
      AvatarState state = (AvatarState)(getPreviousElementOf(_at));

      removeStopStatesOf(state);

      // Remove "after" and replace them with timers
      AvatarTransition at = removeAfter(_at, _block);

      // Put state after transition
      modifyAvatarTransition(at);

      Vector <AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

      for(AvatarStateMachineElement element: elements) {
      //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
      if (element instanceof AvatarTransition) {
      //TraceManager.addDev("at? element=" + element);
      if (element.getNext(0).hasInUpperState(state) == true) {
      if (getPreviousElementOf(element).hasInUpperState(state) == true) {
      v.add(element);
      }
      }
      } else if (element.hasInUpperState(state) == true) {
      // We found a candidate!
      if (element != _at) {
      v.add(element);
      }
      }
      }

      //TraceManager.addDev("*** Analyzing components in state " + state);
      // Split avatar transitions
      for(AvatarStateMachineElement element: v) {
      TraceManager.addDev(">" + element + "<");
      if (element instanceof AvatarTransition) {
      splitAvatarTransition((AvatarTransition)element, state);
      }
      }

      //TraceManager.addDev("\nAdding new elements in state");
      v.clear();
      for(AvatarStateMachineElement element: elements) {
      //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
      if (element.hasInUpperState(state) == true) {
      // We found a candidate!
      if ((element != _at) && (element != at)) {
      v.add(element);
      }
      }
      }


      for(AvatarStateMachineElement element: v) {
      adaptCompositeTransition(at, element, 0);
      }

      removeElement(at);

      }*/


    /*private void removeCompositeTransitions(ArrayList<AvatarTransition> _ats, AvatarBlock _block) {


    // Put state after transition
    modifyAvatarTransition(at);

    Vector <AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

    for(AvatarStateMachineElement element: elements) {
    //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
    if (element instanceof AvatarTransition) {
    //TraceManager.addDev("at? element=" + element);
    if (element.getNext(0).hasInUpperState(state) == true) {
    if (getPreviousElementOf(element).hasInUpperState(state) == true) {
    v.add(element);
    }
    }
    } else if (element.hasInUpperState(state) == true) {
    // We found a candidate!
    if (element != _at) {
    v.add(element);
    }
    }
    }

    //TraceManager.addDev("*** Analyzing components in state " + state);
    // Split avatar transitions
    for(AvatarStateMachineElement element: v) {
    TraceManager.addDev(">" + element + "<");
    if (element instanceof AvatarTransition) {
    splitAvatarTransition((AvatarTransition)element, state);
    }
    }

    //TraceManager.addDev("\nAdding new elements in state");
    v.clear();
    for(AvatarStateMachineElement element: elements) {
    //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
    if (element.hasInUpperState(state) == true) {
    // We found a candidate!
    if ((element != _at) && (element != at)) {
    v.add(element);
    }
    }
    }

    for(AvatarStateMachineElement element: v) {
    adaptCompositeTransition(at, element, 0);
    }

    removeElement(at);

    }*/

//    private void splitAvatarTransitionOld(AvatarTransition _at, AvatarState _currentState) {
//        /*if (_at.hasCompute()) {
//          AvatarState as0 = new AvatarState("splitstate0", null);
//          AvatarState as1 = new AvatarState("splitstate1", null);
//
//
//
//          AvatarTransition at = _at.basicCloneMe();
//          _at.removeAllActions();
//          _at.removeAllNexts();
//          _at.addNext(as);
//          as.addNext(at);
//          addElement(as);
//          addElement(at);
//          splitAvatarTransition(at);
//          }*/
//
//        TraceManager.addDev(" - - - - - - - - - - Split transition nbofactions=" + _at.getNbOfAction());
//        if (_at.getNbOfAction() > 1) {
//            TraceManager.addDev("New split state");
//            AvatarState as = new AvatarState("splitstate", null);
//            as.setHidden(true);
//            as.setState(_currentState);
//            AvatarTransition at = (AvatarTransition) (_at.basicCloneMe(block));
//            _at.removeAllActionsButTheFirstOne();
//            at.removeFirstAction();
//            at.addNext(_at.getNext(0));
//            _at.removeAllNexts();
//            _at.addNext(as);
//            as.addNext(at);
//            addElement(as);
//            addElement(at);
//
//            splitAvatarTransition(_at, _currentState);
//        }
//
//        if (_at.hasDelay()) {
//            AvatarStateMachineElement element = getPreviousElementOf(_at);
//            if (element.hasInUpperState(_currentState) == true) {
//                if (!(element instanceof AvatarState)) {
//                    // Must add an intermediate state
//                    String tmp = findUniqueStateName("internalstate__");
//                    AvatarState as = new AvatarState(tmp, _currentState.getReferenceObject());
//                    addElement(as);
//                    as.setHidden(true);
//                    as.setState(_currentState);
//                    AvatarTransition atn = new AvatarTransition(_at.getBlock(), "internaltransition", null);
//                    addElement(atn);
//                    element.removeNext(_at);
//                    element.addNext(atn);
//                    atn.addNext(as);
//                    as.addNext(_at);
//                    splitAvatarTransition(_at, _currentState);
//                }
//            }
//        }
//    }

//    private void adaptCompositeTransition(AvatarTransition _at, AvatarStateMachineElement _element, int _transitionID) {
//        AvatarState as;
//        AvatarTransition at;
//        LinkedList<AvatarStateMachineElement> ll;
//        String tmp;
//
//        // It cannot be a start / stop state since they have been previously removed ..
//        if (_element instanceof AvatarActionOnSignal) {
//            AvatarStateMachineElement element = _element.getNext(0);
//            if (element instanceof AvatarTransition) {
//                if (!(((AvatarTransition) element).isEmpty())) {
//                    //We need to create a new state
//                    tmp = findUniqueStateName("internalstate__");
//                    TraceManager.addDev("Creating state with name=" + tmp);
//                    as = new AvatarState(tmp, null);
//                    addElement(as);
//                    as.setHidden(true);
//                    at = new AvatarTransition(_at.getBlock(), "internaltransition", null);
//                    addElement(at);
//                    //_element -> at -> as -> element
//
//                    _element.removeNext(element);
//                    _element.addNext(at);
//
//                    at.addNext(as);
//                    as.addNext(element);
//
//                    at = cloneCompositeTransition(_at);
//                    //addElement(at);
//                    as.addNext(at);
//                } else {
//                    // We see if a state follows it. Otherwise, we create one
//                    if (!(element.getNext(0) instanceof AvatarState)) {
//                        //We need to create a new state
//                        tmp = findUniqueStateName("internalstate__");
//                        TraceManager.addDev("Creating state with name=" + tmp);
//                        as = new AvatarState(tmp, null);
//                        addElement(as);
//                        as.setHidden(true);
//                        at = new AvatarTransition(_at.getBlock(), "internaltransition", null);
//                        addElement(at);
//                        //_element -> at -> as -> element
//
//                        _element.removeNext(element);
//                        _element.addNext(at);
//
//                        at.addNext(as);
//                        as.addNext(element);
//
//                        at = cloneCompositeTransition(_at);
//                        //addElement(at);
//                        as.addNext(at);
//
//                    } else {
//                        //We link to this state-> will be done later
//                    }
//                }
//            }
//            /*ll = getPreviousElementsOf(_element);
//              for(AvatarStateMachineElement element: ll) {
//              if (element instanceof AvatarTransition) {
//              // if empty transition: we do just nothing
//              if (!(((AvatarTransition)element).isEmpty())) {
//              tmp = findUniqueStateName("internalstate__");
//              TraceManager.addDev("Creating state with name=" + tmp);
//              as = new AvatarState(tmp, null);
//              as.setHidden(true);
//              element.removeNext(_element);
//              element.addNext(as);
//              at = new AvatarTransition("internaltransition", null);
//              addElement(at);
//              at.addNext(_element);
//              as.addNext(at);
//              addElement(as);
//
//              at = cloneCompositeTransition(_at);
//              addElement(at);
//              as.addNext(at);
//              }
//
//              } else {
//              // Badly formed machine!
//              TraceManager.addError("Badly formed sm (removing composite transition)");
//              }
//              }*/
//
//        } else if (_element instanceof AvatarState) {
//            at = cloneCompositeTransition(_at);
//            //addElement(at);
//            _element.addNext(at);
//        } else if (_element instanceof AvatarTransition) {
//            // Nothing to do since they shall have been split before
//        } else {
//            // Nothing to do either
//        }
//    }


    // Return the first previous element met. Shall be used preferably only for transitions
    private AvatarStateMachineElement getPreviousElementOf(AvatarStateMachineElement _elt) {
        for (AvatarStateMachineElement element : elements) {
            if (element.hasNext(_elt)) {
                return element;
            }
        }

        return null;
    }

    private List<AvatarStateMachineElement> getPreviousElementsOf(AvatarStateMachineElement _elt) {
        List<AvatarStateMachineElement> ll = new LinkedList<AvatarStateMachineElement>();
        for (AvatarStateMachineElement element : elements) {
            if (element.hasNext(_elt)) {
                ll.add(element);
            }
        }

        return ll;
    }

    public AvatarState getStateWithName(String _name) {
        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarState) {
                if (element.getName().compareTo(_name) == 0) {
                    return (AvatarState) element;
                }
            }
        }
        return null;
    }


    public List<AvatarActionOnSignal> getAllAOSWithName(String _name) {
        List<AvatarActionOnSignal> list = new ArrayList<AvatarActionOnSignal>();
        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) element;
                if (aaos.getSignal().getName().compareTo(_name) == 0) {
                    list.add(aaos);
                }
            }
        }
        return list;
    }

    // All transitions reaching a state that has an internal start state
    // shall in fact go directly to the nexts of the start state
    public List<AvatarStartState> removeAllInternalStartStates() {
        // identify allstart state
        List<AvatarStartState> ll = new LinkedList<AvatarStartState>();

        List<AvatarStartState> removedinfos = new LinkedList<>();

        for (AvatarStateMachineElement element : elements) {
            if ((element instanceof AvatarStartState) && (element.getState() != null)) {
                //TraceManager.addDev("-> -> found an internal state state");
                ll.add((AvatarStartState) element);
            }
        }

        AvatarState as0;
        List<AvatarStateMachineElement> le;
        for (AvatarStartState as : ll) {
            AvatarState astate = as.getState();
            if (as != null) {
                elements.remove(as);
                AvatarStateMachineElement elt = as.getNext(0);
                astate.addNext(elt);
                removedinfos.add(as);
                /*le = getPreviousElementsOf(astate);
                if (le.size() > 0) {
                    as0 = new AvatarState("entrance__" + astate.getName(), astate.getReferenceObject());
                    as0.addNext(as.getNext(0));
                    as0.setHidden(true);
                    as0.setState(astate);
                    for (AvatarStateMachineElement element : le) {
                        if (element instanceof AvatarTransition) {
                            element.removeAllNexts();
                            element.addNext(as0);
                        } else {
                            TraceManager.addDev("Badly formed state machine");
                        }
                    }

                    removedinfos.add(as.getState());
                    removedinfos.add(as0);

                    // Remove the start state and its next transition
                    removeElement(as);
                    addElement(as0);

                    //TraceManager.addDev("-> -> removed an internal state state!");
                } else {
                    TraceManager.addDev("Badly formed state machine");
                }*/
            }
        }

        return removedinfos;

    }

    public void removeAllSuperStates() {
        for (AvatarStateMachineElement element : elements) {
            element.setState(null);
        }
    }

    public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
        for (AvatarStateMachineElement element : elements) {
            if (element.hasReferenceObject(_o)) {
                return element;
            }
        }
        return null;
    }

    public Object getReferenceObjectFromID(int _ID) {
        for (AvatarStateMachineElement element : elements) {
            if (element.getID() == _ID) {
                return element.getReferenceObject();
            }
        }
        return null;
    }

    // Return true iff at least one timer was removed
    public boolean removeTimers(String timerAttributeName, HashMap<String, AvatarSignal> sets,
                                HashMap<String, AvatarSignal> resets, HashMap<String, AvatarSignal> expires) {
        AvatarSetTimer ast;
        AvatarTimerOperator ato;

        List<AvatarStateMachineElement> olds = new LinkedList<AvatarStateMachineElement>();
        List<AvatarStateMachineElement> news = new LinkedList<AvatarStateMachineElement>();

        AvatarSignal as;


        for (AvatarStateMachineElement elt : elements) {
            // Set timer...
            if (elt instanceof AvatarSetTimer) {
                ast = (AvatarSetTimer) elt;
                as = sets.get(ast.getTimer().getName());
                if (as != null) {
                    AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), as, elt.getReferenceObject(), block);
                    aaos.addValue(timerAttributeName);
                    olds.add(elt);
                    news.add(aaos);


                    // Modifying the transition just before
                    List<AvatarStateMachineElement> previous = getPreviousElementsOf(ast);
                    if (previous.size() == 1) {
                        if (previous.get(0) instanceof AvatarTransition) {
                            AvatarTransition at = (AvatarTransition) (previous.get(0));
                            TraceManager.addDev("Timer value setting=" + ast.getTimerValue());
                            at.addAction(timerAttributeName + " = " + ast.getTimerValue());
                        } else {
                            TraceManager.addError("The element before a set time is not a transition!");
                        }
                    } else {
                        TraceManager.addError("More than one transition before a set time!");
                    }
                }

                // Reset timer
            } else if (elt instanceof AvatarResetTimer) {
                ato = (AvatarTimerOperator) elt;
                as = resets.get(ato.getTimer().getName());
                if (as != null) {
                    AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), as, elt.getReferenceObject(), block);
                    olds.add(elt);
                    news.add(aaos);
                }

                // Expire timer
            } else if (elt instanceof AvatarExpireTimer) {
                ato = (AvatarTimerOperator) elt;
                as = expires.get(ato.getTimer().getName());
                if (as != null) {
                    AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), as, elt.getReferenceObject(), block);
                    olds.add(elt);
                    news.add(aaos);
                }
            }
        }

        // Replacing old elements with new ones
        AvatarStateMachineElement oldelt, newelt;
        for (int i = 0; i < olds.size(); i++) {
            oldelt = olds.get(i);
            newelt = news.get(i);
            replace(oldelt, newelt);
        }

        return (olds.size() > 0);
    }

    public void replace(AvatarStateMachineElement oldone, AvatarStateMachineElement newone) {

        //TraceManager.addDev("Replacing " + oldone + " with " + newone);

        addElement(newone);
        removeElement(oldone);

        // Previous elements
        List<AvatarStateMachineElement> previous = getPreviousElementsOf(oldone);
        for (AvatarStateMachineElement elt : previous) {
            elt.replaceAllNext(oldone, newone);
        }

        // Next elements
        for (int i = 0; i < oldone.nbOfNexts(); i++) {
            AvatarStateMachineElement elt = oldone.getNext(i);
            newone.addNext(elt);
        }
    }

    public AvatarTransition removeAfter(AvatarTransition _at, AvatarBlock _block) {
        String delay = _at.getMinDelay();
        if ((delay == null) || (delay.trim().length() == 0)) {
            return _at;
        }


        // We have to use a timer for this transition
        AvatarAttribute aa = _block.addTimerAttribute("timer__");
        AvatarAttribute val = _block.addIntegerAttribute(aa.getName() + "_val");

        //TraceManager.addDev("ADDING TIMER: " + aa.getName());

        // Timer is set at the entrance in the composite state
        List<AvatarTransition> ll = findEntranceTransitionElements((AvatarState) (getPreviousElementOf(_at)));

        AvatarTransition newat0, newat1;
        AvatarSetTimer ast;
        AvatarRandom ar;
        AvatarState as;
        for (AvatarTransition att : ll) {
            //TraceManager.addDev(" ------------------ Dealing with an entrance transition");
            ar = new AvatarRandom("randomfortimer", _block.getReferenceObject(), _block);
            ar.setVariable(val.getName());
            ar.setValues(_at.getMinDelay(), _at.getMaxDelay());

            ast = new AvatarSetTimer("settimer_" + aa.getName(), _block.getReferenceObject(), _block);
            ast.setTimerValue(val.getName());
            ast.setTimer(aa);

            newat0 = new AvatarTransition(_block, "transition_settimer_" + aa.getName(), _block.getReferenceObject());
            newat1 = new AvatarTransition(_block, "transition_settimer_" + aa.getName(), _block.getReferenceObject());

            elements.add(ar);
            elements.add(ast);
            elements.add(newat0);
            elements.add(newat1);

            newat1.addNext(att.getNext(0));
            att.removeAllNexts();
            att.addNext(ar);
            ar.addNext(newat0);
            newat0.addNext(ast);
            ast.addNext(newat1);

        }

        // Wait for timer expiration on the transition
        AvatarExpireTimer aet = new AvatarExpireTimer("expiretimer_" + aa.getName(), _block.getReferenceObject(), _block);
        aet.setTimer(aa);
        newat0 = new AvatarTransition(_block, "transition0_expiretimer_" + aa.getName(), _block.getReferenceObject());
        newat1 = new AvatarTransition(_block, "transition1_expiretimer_" + aa.getName(), _block.getReferenceObject());
        as = new AvatarState("state1_expiretimer_" + aa.getName(), _block.getReferenceObject(), _block);
        addElement(aet);
        addElement(newat0);
        addElement(newat1);
        addElement(as);

        newat0.addNext(aet);
        aet.addNext(newat1);
        newat1.addNext(as);
        _at.setDelays("", "");

        List<AvatarStateMachineElement> elts = getElementsLeadingTo(_at);

        for (AvatarStateMachineElement elt : elts) {
            elt.removeNext(_at);
            elt.addNext(newat0);
        }

        as.addNext(_at);

        return newat0;
    }

    public List<AvatarTransition> findEntranceTransitionElements(AvatarState _state) {
        //TraceManager.addDev("Searching for transitions entering:" + _state.getName());
        List<AvatarTransition> ll = new LinkedList<AvatarTransition>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarStateMachineElement element = getPreviousElementOf(elt);
                if (elt.getNext(0) == _state) {
                    ll.add((AvatarTransition) elt);
                } else if (element.inAnUpperStateOf(_state) && (elt.getNext(0).getState() == _state)) {
                    ll.add((AvatarTransition) elt);
                }
            }
        }
        //TraceManager.addDev("Nb of elements found:" + ll.size());
        return ll;
    }

    public List<AvatarStateMachineElement> getElementsLeadingTo(AvatarStateMachineElement _elt) {
        List<AvatarStateMachineElement> elts = new LinkedList<AvatarStateMachineElement>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt.hasNext(_elt)) {
                elts.add(elt);
            }
        }

        return elts;
    }

    public void modifyAvatarTransition(AvatarTransition _at) {
        /*if ((_at.getNbOfAction() > 0) || (_at.hasCompute())) {
          return;
          }*/

        AvatarStateMachineElement next = _at.getNext(0);

        if (next instanceof AvatarState) {
            return;
        } else if ((next instanceof AvatarTimerOperator) || (next instanceof AvatarActionOnSignal)) {

            TraceManager.addDev("-> Timer modification");

            AvatarState myState = new AvatarState("statefortransition__" + ID_ELT, _at.getReferenceObject(), block);
            myState.setHidden(true);
            AvatarTransition at2 = new AvatarTransition(_at.getBlock(), "transitionfortransition__" + ID_ELT, _at.getReferenceObject());
            ID_ELT++;
            AvatarTransition at1 = (AvatarTransition) (next.getNext(0));

            next.removeAllNexts();
            next.addNext(at2);
            at2.addNext(myState);
            myState.addNext(at1);

            addElement(myState);
            addElement(at2);

            return;
        } else {
            AvatarState myState = new AvatarState("statefortransition__" + ID_ELT, _at.getReferenceObject(), block);
            AvatarTransition at = new AvatarTransition(_at.getBlock(), "transitionfortransition__", _at.getReferenceObject());
            at.addNext(_at.getNext(0));
            _at.removeAllNexts();
            _at.addNext(myState);
            myState.addNext(at);
            addElement(myState);
            addElement(at);
            return;
        }
    }

    public AvatarTransition cloneCompositeTransition(AvatarTransition _at) {
        //TraceManager.addDev("Must clone: " + _at);
        // We clone elements until we find a state!
        AvatarStateMachineElement tomake, current;
        AvatarStateMachineElement tmp;
        AvatarTransition at = (AvatarTransition) (_at.basicCloneMe(block));
        addElement(at);

        current = _at.getNext(0);
        tomake = at;

        while ((current != null) && !(current instanceof AvatarState)) {
            //TraceManager.addDev("Cloning: " + current);
            current.setNotCheckable();
            current.setAsVerifiable(false);
            tmp = current.basicCloneMe(block);
            addElement(tmp);
            tomake.addNext(tmp);
            tomake = tmp;
            current = current.getNext(0);
            if (current == null) {
                break;
            }
        }

        if (current == null) {
            TraceManager.addDev("NULL CURRENT !!! NULL CURRENT");
        }

        tomake.addNext(current);

        return at;

        /*if ((_at.getNbOfAction() > 0) || (_at.hasCompute())) {
          return _at.basicCloneMe();
          }

          AvatarStateMachineElement next = _at.getNext(0);

          if (next instanceof AvatarActionOnSignal) {
          AvatarTransition at = _at.
          AvatarActionOnSignal aaos = ((AvatarActionOnSignal)next).basicCloneMe();
          addElement(at);
          addElement(aaos);
          at.addNext(aaos);
          aaos.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          if (next instanceof AvatarExpireTimer) {
          AvatarTransition at = _at.basicCloneMe();
          AvatarExpireTimer aet = ((AvatarExpireTimer)next).basicCloneMe();
          AvatarTransition
          addElement(at);
          addElement(aet);
          at.addNext(aet);
          aet.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          if (next instanceof AvatarResetTimer) {
          AvatarTransition at = _at.basicCloneMe();
          AvatarResetTimer art = ((AvatarResetTimer)next).basicCloneMe();
          addElement(at);
          addElement(art);
          at.addNext(art);
          art.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          if (next instanceof AvatarSetTimer) {
          AvatarTransition at = _at.basicCloneMe();
          AvatarSetTimer ast = ((AvatarSetTimer)next).basicCloneMe();
          addElement(at);
          addElement(ast);
          at.addNext(ast);
          ast.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          return _at.basicCloneMe();*/

    }

    public void removeStopStatesOf(AvatarState _as) {
        List<AvatarStopState> ll = new LinkedList<AvatarStopState>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarStopState) {
                if (elt.getState() == _as) {
                    ll.add((AvatarStopState) elt);
                }
            }
        }

        for (AvatarStopState ass : ll) {
            TraceManager.addDev("Removed a stop state");
            AvatarState astate = new AvatarState("OldStopState", ass.getReferenceObject(), block);
            astate.setState(ass.getState());
            replace(ass, astate);
        }
    }

    public String findUniqueStateName(String name) {
        int id = 0;
        boolean found;

        while (id < 10000) {
            found = false;
            for (AvatarStateMachineElement elt : elements) {
                if (elt instanceof AvatarState) {
                    if (elt.getName().compareTo(name + id) == 0) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return name + id;
            }
            id++;
        }
        return name + id;
    }

    public void handleUnfollowedStartState(AvatarStateMachineOwner _block) {
        if (startState.nbOfNexts() == 0) {
            AvatarStopState stopState = new AvatarStopState("__StopState", startState.getReferenceObject(), _block);
            AvatarTransition at = new AvatarTransition(_block, "__toStop", startState.getReferenceObject());
            addElement(stopState);
            addElement(at);
            startState.addNext(at);
            at.addNext(stopState);
        }
    }

    /**
     * Removes all function calls by inlining them.
     *
     * @param block The block from which library function calls should be removed.
     */
    public void removeLibraryFunctionCalls(AvatarBlock block) {
        /* Perform BFS for AvatarLibraryFunctionCall elements. When one is found, replace it by the state machine and fix the links */
        LinkedList<AvatarStateMachineElement> toVisit = new LinkedList<AvatarStateMachineElement>();
        toVisit.add(this.startState);
        Set<AvatarStateMachineElement> visited = new HashSet<AvatarStateMachineElement>();
        Map<AvatarLibraryFunctionCall, AvatarStateMachineElement> callsTranslated = new HashMap<AvatarLibraryFunctionCall, AvatarStateMachineElement>();

        while (!toVisit.isEmpty()) {
            /* Get the first element of the queue */
            AvatarStateMachineElement curAsme = toVisit.remove();
            if (visited.contains(curAsme))
                continue;

            if (curAsme instanceof AvatarLibraryFunctionCall) {
                AvatarLibraryFunctionCall alfc = (AvatarLibraryFunctionCall) curAsme;

                /* Create a state that will be used as an entry point for the sub-state machine */
                AvatarState firstState = new AvatarState("entry_" + alfc.getLibraryFunction().getName() + "_" + alfc.getCounter(),
                        curAsme.getReferenceObject(), block);
                elements.add(firstState);

                /* Add this state to the mapping so that future state can use it to replace their next element */
                callsTranslated.put(alfc, firstState);

                /* inline the function call */
                AvatarStateMachineElement lastState = alfc.inlineFunctionCall(block, firstState);
                TraceManager.addDev("LAST STATE IS " + lastState + " class=" + lastState.getClass().getCanonicalName());

                /* Add the next elements to the newly created last state */
                for (AvatarStateMachineElement asme : curAsme.getNexts())
                    lastState.addNext(asme);

                /* Remove the call in the list of elements */
                elements.remove(curAsme);


                /* Use the translated function call's first element as current element */
                curAsme = firstState;


            }

            /* Add current element to the visited set */
            visited.add(curAsme);

            /* Loop through the next elements */
            int i = 0;
            if (curAsme.getNexts() != null) {
                for (AvatarStateMachineElement asme : curAsme.getNexts()) {
                    /* Check if it is a function call */
                    if (asme instanceof AvatarLibraryFunctionCall) {
                        AvatarStateMachineElement replaceBy = callsTranslated.get(asme);
                        /* Check if function call has already been translated */
                        if (replaceBy != null) {
                            /* replace by the translated function call */
                            curAsme.removeNext(i);
                            curAsme.addNext(replaceBy);

                            /* new next element has been added at the end of the list so we need to fix i */
                            i--;
                        } else {
                            /* mark the function call and the current state to be visited */
                            toVisit.add(asme);
                            toVisit.add(curAsme);
                            visited.remove(curAsme);
                        }
                    } else
                        toVisit.add(asme);

                    i++;
                }
            }
        }
    }

    /**
     * Removes all empty transitions between two states.
     * This concerns also the start state, and end states.
     * DO NOT take into account code of states, and start states
     *
     * @param block        The block containing the state machine
     * @param _canOptimize boolean data
     */
    public void removeEmptyTransitions(AvatarBlock block, boolean _canOptimize) {

        //TraceManager.addDev("Remove empty transitions with optimize=" + _canOptimize);

        // Look for such a transition
        // states -> tr -> state with tr is empty
        // a tr is empty when it has no action or guard
        AvatarStateElement foundState1 = null, foundState2 = null;
        AvatarTransition foundAt = null;


        for (AvatarStateMachineElement elt : elements) {
            if ((elt instanceof AvatarStateElement) && (!(elt instanceof AvatarStartState))) {
                if (elt.getNexts().size() == 1) {
                    if ((elt.getNext(0) instanceof AvatarTransition)) {
                        AvatarTransition at = (AvatarTransition) (elt.getNext(0));
                        if (at.getNext(0) instanceof AvatarStateElement) {
                            if (at.isEmpty() && at.hasNonDeterministicGuard()) {
                                if ((_canOptimize) && (!(elt.isCheckable()))) {
                                    //TraceManager.addDev("State found:" + elt);
                                    foundState1 = (AvatarStateElement) elt;
                                    foundAt = at;
                                    foundState2 = (AvatarStateElement) (at.getNext(0));
                                    break;
                                }

                            }
                        }
                    } else {
                        TraceManager.addDev("Malformed spec: " + elt.getName() + " is followed by " + elt.getName());

                    }
                }
            }
        }

        // Found?
        if (foundState1 != null) {
            if (foundState1 == foundState2) {
                // We simply remove the transition
                //TraceManager.addDev("Found same state -> removing the transitions");
                removeElement(foundAt);
                // removing from the next of foundState1
                foundState1.removeNext(foundAt);

            } else {
                // Must remove state1 and at, and link all previous of state 1 to state2
                //TraceManager.addDev("Found 2 states state1=" + foundState1.getName() + " state2=" + foundState2.getName());
                for (AvatarStateMachineElement elt : getPreviousElementsOf(foundState1)) {
                    elt.replaceAllNext(foundState1, foundState2);
                }
                removeElement(foundAt);
                removeElement(foundState1);
                foundState2.addReferenceObjectFrom(foundState1);

            }
            removeEmptyTransitions(block, _canOptimize);
        }
    }

    public void removeEmptyTransitionsOnSameState(AvatarBlock _block) {
        ArrayList<AvatarStateMachineElement> toBeRemoved = new ArrayList<>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition)elt;
                if (at.isEmpty()) {
                    AvatarStateMachineElement previous = getPreviousElementOf(at);
                    AvatarStateMachineElement next = at.getNext(0);
                    if (( previous != null) && (next != null)) {
                        if ((previous == next) && (previous instanceof AvatarState)) {
                            toBeRemoved.add(at);
                        }
                    }
                }
            }
        }

        for(AvatarStateMachineElement elt : toBeRemoved) {
            AvatarStateMachineElement previous = getPreviousElementOf(elt);
            if (previous != null) {
                previous.removeNext(elt);
            }
            elements.remove(elt);
        }
    }

    // groups together transitions to avoid extra states
    public void groupUselessTransitions(AvatarBlock _block) {
        ArrayList<AvatarState> toBeRemoved = new ArrayList<>();
        AvatarTransition atBefore, atAfter;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarState) {
                if ((elt.nexts.size() == 1) && (getPreviousElementsOf(elt).size() == 1)) {
                    // We need to check that the follower is only a transition with an action
                    AvatarStateMachineElement follower = elt.nexts.get(0);
                    if ((follower instanceof AvatarTransition) && (follower.nexts.size() == 1)) {
                        if (follower.nexts.get(0) instanceof AvatarState) {

                            AvatarStateMachineElement previous = getPreviousElementsOf(elt).get(0);
                            if (previous instanceof AvatarTransition) {
                                toBeRemoved.add((AvatarState) elt);
                            }
                        }
                    }
                }
            }
        }
        for (AvatarState st : toBeRemoved) {
            atBefore = (AvatarTransition) (getPreviousElementsOf(st).get(0));
            atAfter = (AvatarTransition) (st.nexts.get(0));

            // Adding guard, delays and actions from after to before
            if (atAfter.getGuard().isGuarded()) {
                if (atBefore.getGuard().isGuarded()) {
                    atBefore.setGuard("[(" + atAfter.getGuard() + ") && (" + atBefore.getGuard() + ")]");
                } else {
                    atBefore.setGuard(atAfter.getGuard());
                }
            }

            if (atAfter.hasDelay()) {
                if (atBefore.hasDelay()) {
                    atBefore.setDelays(atAfter.getMinDelay() + "+" + atBefore.getMinDelay(),
                            atAfter.getMaxDelay() + "+" + atBefore.getMaxDelay());

                }
            }

            for (AvatarAction a : atAfter.getActions()) {
                atBefore.addAction(a);
            }


            // Updating links
            atBefore.nexts.clear();
            atBefore.nexts.add(atAfter.getNext(0));

            // Removing state and next transition
            elements.remove(atAfter);
            elements.remove(st);
        }

    }

    public int getIndexOfStartState() {
        if (allStates == null) {
            return -1;
        }

        for (int i = 0; i < allStates.length; i++) {
            if (allStates[i] instanceof AvatarStartState) {
                return i;
            }
        }

        return -1;
    }

    public int getIndexOfState(AvatarStateElement _ase) {
        if (allStates == null) {
            return -1;
        }

        for (int i = 0; i < allStates.length; i++) {
            if (allStates[i] == _ase) {
                return i;
            }
        }

        return -1;
    }


    // Fills the current state machine by cloning the current one

    public void advancedClone(AvatarStateMachine _newAsm, AvatarStateMachineOwner _newBlock) {
        // Elements
        HashMap<AvatarStateMachineElement, AvatarStateMachineElement> correspondenceMap = new HashMap<AvatarStateMachineElement, AvatarStateMachineElement>();
        for (AvatarStateMachineElement elt : elements) {
            AvatarStateMachineElement ae;
            ae = elt.basicCloneMe(_newBlock);

            /*if (ae instanceof AvatarState) {
                TraceManager.addDev("New state: ");
            }
            TraceManager.addDev("elt: " +  ae.toString());*/

            if (ae == null) {
                TraceManager.addDev("Null AE");
            }

            _newAsm.addElement(ae);

            if (ae instanceof AvatarStartState) {
                _newAsm.setStartState((AvatarStartState) ae);
            }
            correspondenceMap.put(elt, ae);
        }

        // Other attributes
        for (AvatarStateMachineElement elt : elements) {
            AvatarStateMachineElement ae = correspondenceMap.get(elt);
            if (ae != null) {
                elt.fillAdvancedValues(ae, correspondenceMap, this);
            } else {
                TraceManager.addDev("Null correspondance ae");
            }
        }
    }

    public AvatarTransition findEmptyTransition(final AvatarStateMachineElement elementSource,
                                                final AvatarStateMachineElement elementTarget) {
        for (final AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                final AvatarTransition transition = (AvatarTransition) element;

                if (transition.isEmpty() && !transition.getNexts().isEmpty()) {
                    if (getPreviousElementOf(transition) == elementSource && transition.getNexts().get(0) == elementTarget) {
                        return transition;
                    }
                }
            }
        }

        return null;
    }


    public void removeAllDelays() {
        for (final AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                final AvatarTransition transition = (AvatarTransition) element;
                if (transition.hasDelay()) {
                    transition.setDelays("0", "0");
                }
            } else if (element instanceof AvatarSetTimer) {
                ((AvatarSetTimer) element).setTimerValue("0");
            }
        }
    }

    /**
     * Looks for all numerical values over
     * the provided max
     *
     * @param maxV Maximum value.
     */
    public ArrayList<AvatarElement> elementsWithNumericalValueOver(int maxV) {
        String val;

        ArrayList<AvatarElement> invalids = new ArrayList<AvatarElement>();

        for (AvatarStateMachineElement asme : elements) {

            // Action on signals
            if (asme instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) asme;
                for (int i = 0; i < aaos.getNbOfValues(); i++) {
                    val = aaos.getValue(i);
                    if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                        invalids.add(this);
                        break;
                    }
                }

            }

            if (asme instanceof AvatarRandom) {
                AvatarRandom arand = (AvatarRandom) asme;
                val = arand.getMinValue();
                if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                    invalids.add(this);
                } else {
                    val = arand.getMaxValue();
                    if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                        invalids.add(this);
                    }
                }
            }

            if (asme instanceof AvatarSetTimer) {
                AvatarSetTimer atop = (AvatarSetTimer) asme;
                val = atop.getTimerValue();
                if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                    invalids.add(this);
                }
            }

            if (asme instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) asme;

                // Guard
                val = at.getGuard().toString();
                if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                    invalids.add(this);
                }

                // Delays
                val = at.getMinDelay();
                if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                    invalids.add(this);
                }
                val = at.getMaxDelay();
                if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                    invalids.add(this);
                }

                // Actions
                for (AvatarAction aa : at.getActions()) {
                    val = aa.toString();
                    if (MyMath.hasIntegerValueOverMax(val, maxV)) {
                        invalids.add(this);
                    }
                }

            }


        }

        return invalids;
    }


    public List<ArrayList<AvatarTransition>> checkStaticInternalLoops() {
        if (allStates == null) {
            return null;
        }

        List<ArrayList<AvatarTransition>> loops = new ArrayList<ArrayList<AvatarTransition>>();
        List<AvatarTransition> trace = new ArrayList<AvatarTransition>();
        Set<AvatarStateMachineElement> visited = new HashSet<AvatarStateMachineElement>();

        for (AvatarStateElement state : allStates) {
            checkStaticInternalLoopsRec(state, state, trace, visited, loops, 0);
            visited.add(state); //avoid cycles permutations
        }
        return loops;
    }


    public void checkStaticInternalLoopsRec(AvatarStateMachineElement node, AvatarStateMachineElement arrival, List<AvatarTransition> trace, Set<AvatarStateMachineElement> visited, List<ArrayList<AvatarTransition>> loops, int depth) {
        if (visited.contains(node)) {
            if (node == arrival) {
                //valid loop, copy trace to loops
                loops.add(new ArrayList<AvatarTransition>(trace));
                return;
            } else {
                //not valid loop
                return;
            }
        } else if (node.nexts == null) {
            return;
        }

        visited.add(node);
        for (AvatarStateMachineElement next : node.nexts) {
            if (next instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) next;
                //the state machine should alternate states and transitions
                if (!(at.getNext(0) instanceof AvatarActionOnSignal)) {
                    //Choose internal action paths
                    trace.add(depth, at);
                    checkStaticInternalLoopsRec(at.getNext(0), arrival, trace, visited, loops, depth + 1);
                    trace.remove(depth);
                }
            }
        }
        visited.remove(node);

        return;
    }

    // Returns the list of elements non reachabable from the start state
    public List<AvatarStateMachineElement> getUnusedElements() {
        if (startState == null) {
            return null;
        }

        List<AvatarStateMachineElement> elts = new LinkedList<>();

        HashSet<AvatarStateMachineElement> found = new HashSet<>();
        findAvatarElements(startState, found);

        for (AvatarStateMachineElement asme : elements) {
            if (!(found.contains(asme))) {
                elts.add(asme);
            }
        }

        return elts;
    }

    private void findAvatarElements(AvatarStateMachineElement first, HashSet<AvatarStateMachineElement> found) {
        if (found.contains(first)) {
            return;
        }

        found.add(first);

        if (first instanceof AvatarState) {
            // Find all internal start states of this state
            for (AvatarStateMachineElement internal : elements) {
                if (internal instanceof AvatarStartState) {
                    if (internal.getState() == first) {
                        findAvatarElements(internal, found);
                    }
                }
            }
        }


        for (AvatarStateMachineElement asme : first.getNexts()) {
            findAvatarElements(asme, found);


        }
    }

    public boolean removeDuplicatedTransitions() {

        ArrayList<AvatarStateMachineElement> toBeRemoved = new ArrayList<>();
        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarState) {
                // We look at the nexts
                // If transition -> state duplicated, or transition -> aaos duplicated -> state : we remove  the duplicate
                toBeRemoved.addAll(removeDuplicatedTransitionsFromState((AvatarState) elt));
            }
        }

        for (AvatarStateMachineElement asme : toBeRemoved) {
            elements.remove(asme);
        }

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarState) {
                toBeRemoved.clear();
                for (AvatarStateMachineElement asmeState: elt.getNexts()) {
                    if (!(elements.contains(asmeState))) {
                        toBeRemoved.add(asmeState);
                    }
                }
                for (AvatarStateMachineElement asme : toBeRemoved) {
                    elt.getNexts().remove(asme);
                }
            }


        }


        return toBeRemoved.size() != 0;
    }

    public ArrayList<AvatarStateMachineElement> removeDuplicatedTransitionsFromState(AvatarState _st) {
        ArrayList<AvatarStateMachineElement> toBeRemoved = new ArrayList<>();

        // At least two exiting transitions
        if (_st.getNexts().size() < 2) {
            return toBeRemoved;
        }

        // We check is the at least two transitions are equivalent

        for (int i = 0; i < _st.getNexts().size(); i++) {
            AvatarTransition at1 = (AvatarTransition) (_st.getNexts().get(i));
            for (int j = i + 1; j < _st.getNexts().size(); j++) {
                AvatarTransition at2 = (AvatarTransition) (_st.getNexts().get(j));
                if (at1.equals(at2)) {
                    //TraceManager.addDev("\tTwo equal transitions in state " + _st.getName());
                    // We have to consider the next of at1 and at2
                    AvatarStateMachineElement next1, next2;
                    next1 = at1.getNext(0);
                    next2 = at2.getNext(0);
                    if (next1 instanceof AvatarState && next1 == next2) {
                        toBeRemoved.add(at2);
                    } else if ((next1 instanceof AvatarActionOnSignal) && (next2 instanceof AvatarActionOnSignal)) {
                        //TraceManager.addDev("\tChecking for equal AAOS " + _st.getName());
                        AvatarActionOnSignal aaos1 = (AvatarActionOnSignal) next1;
                        AvatarActionOnSignal aaos2 = (AvatarActionOnSignal) next2;
                        if (next1.getNexts().get(0) instanceof AvatarTransition && next2.getNexts().get(0) instanceof AvatarTransition) {
                            AvatarTransition at11 = (AvatarTransition) next1.getNexts().get(0);
                            AvatarTransition at21 = (AvatarTransition) next2.getNexts().get(0);
                            if (at11.equals(at21)) {
                                if (at11.getNexts().get(0) instanceof AvatarState && (at11.getNexts().get(0) == at21.getNexts().get(0))) {
                                    // We need to compare the two AvatarActionOnSignal
                                    //TraceManager.addDev("\tComparing aaos1 and aaos2 in " + _st.getName());
                                    if (aaos1.equals(aaos2)) {
                                        toBeRemoved.add(at2);
                                        toBeRemoved.add(aaos2);
                                        toBeRemoved.add(at21);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        TraceManager.addDev("\ttoBeRemoved of size " + toBeRemoved.size() + " for state " + _st.getName());


        return toBeRemoved;
    }

}
