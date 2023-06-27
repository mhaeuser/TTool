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


import graph.AUTGraph;
import graph.AUTState;
import graph.AUTTransition;
import myutil.TraceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Class AvatarCompactDependencyGraph
 * Creation: 27/06/2023
 *
 * @author Ludovic APVRILLE, Raja GATGOUT
 * @version 1.0 27/06/2023
 */
public class AvatarCompactDependencyGraph {
    private AUTGraph graph;
    private int id = 0;

    public AvatarCompactDependencyGraph() {
        //fromStates = new HashMap<>();
    }

    public AUTGraph getGraph() {
        return graph;
    }

    public void setGraph(AUTGraph _g) {
        graph = _g;
    }

    /*public void setRefs(HashMap<AvatarElement, ArrayList<AUTState>> _toStates, HashMap<AUTState, AvatarElement> _fromStates) {
        toStates = _toStates;
        fromStates = _fromStates;
    }*/

    public AUTState getFirstStateWithReference(AvatarElement _ae) {
        return graph.getFirstStateWithReference(_ae);
    }

    public void buildGraph(AvatarSpecification _avspec, boolean withID) {
        graph = new AUTGraph();
        id = 0;

        ArrayList<AUTState> states = new ArrayList<>();
        ArrayList<AUTTransition> transitions = new ArrayList<>();
        // First build state machines, and then link them on RD / WR operators

        for (AvatarBlock block : _avspec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            AvatarStartState ass = asm.getStartState();

            // Make general structure
            makeCompactDependencyGraphForAvatarElement(block, ass, null, null, states, transitions, withID, null);
        }

        ArrayList<AUTState> newStates = new ArrayList<>();
        HashSet<AUTState> oldStatesRemove = new HashSet<>();

        HashSet<AUTState> previousRead = new HashSet<>();

        // Connect everything i.e. writers to all potential readers
        // For each writing state, we draw a transition to all possible corresponding readers
        // Double direction if synchronous
        for (AUTState state : states) {
            if (state.referenceObject instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) state.referenceObject;

                // Find previous of state
                AUTTransition previousTr = state.inTransitions.get(0);
                int previousId = previousTr.origin;
                AUTState previousState = null;
                for (AUTState st : states) {
                    if (st.id == previousId) {
                        previousState = st;
                        break;
                    }
                }

                if (previousState != null) {
                    AvatarSignal signal = aaos.getSignal();
                    boolean found = false;

                    AUTTransition previousDTr = null;
                    AUTState previousStateD = null;

                    if (signal.isOut()) {
                        // Write operation
                        AvatarSignal correspondingSig = _avspec.getCorrespondingSignal(signal);
                        //TraceManager.addDev("Corresponding signal=" + correspondingSig);
                        if (correspondingSig != null) {
                            for (AUTState stateDestination : states) {
                                if (stateDestination.referenceObject instanceof AvatarActionOnSignal) {
                                    AvatarActionOnSignal aaosD = (AvatarActionOnSignal) stateDestination.referenceObject;
                                    if (aaosD.getSignal() == correspondingSig) {
                                        // Found relation
                                        //TraceManager.addDev("Found relation!");
                                        found = true;
                                        oldStatesRemove.add(state);

                                        // Create a new state dedicated to this relation
                                        AUTState newState = new AUTState(id);
                                        newState.referenceObject = state.referenceObject;
                                        newState.info = state.info;
                                        if (state.referenceObject instanceof AvatarElement) {
                                            //putState((AvatarElement) state.referenceObject, newState);
                                            //fromStates.put(newState, (AvatarElement) state.referenceObject);
                                        }

                                        newStates.add(newState);
                                        id++;

                                        AUTTransition tr = new AUTTransition(previousId, "", newState.id);
                                        transitions.add(tr);
                                        previousState.addOutTransition(tr);
                                        newState.addInTransition(tr);

                                        tr = new AUTTransition(newState.id, "", state.id);
                                        transitions.add(tr);
                                        newState.addOutTransition(tr);
                                        state.addInTransition(tr);

                                        // We must do the same for the destination : create a state before

                                        // Find previous of state
                                        previousDTr = stateDestination.inTransitions.get(0);
                                        int previousDId = previousDTr.origin;
                                        previousStateD = null;
                                        for (AUTState st : states) {
                                            if (st.id == previousDId) {
                                                previousStateD = st;
                                                break;
                                            }
                                        }

                                        if (previousStateD != null) {
                                            oldStatesRemove.add(stateDestination);
                                            previousRead.add(previousStateD);

                                            AUTState newStateD = new AUTState(id);
                                            newStateD.referenceObject = stateDestination.referenceObject;
                                            newStateD.info = stateDestination.info;
                                            newStates.add(newStateD);
                                            id++;

                                            // linking the new state to the correct states

                                            tr = new AUTTransition(previousDId, "", newStateD.id);
                                            transitions.add(tr);
                                            previousStateD.addOutTransition(tr);
                                            newStateD.addInTransition(tr);

                                            tr = new AUTTransition(newStateD.id, "", stateDestination.id);
                                            transitions.add(tr);
                                            newStateD.addOutTransition(tr);
                                            stateDestination.addInTransition(tr);


                                            // Links between the two new states

                                            tr = new AUTTransition(newState.id, "", newStateD.id);
                                            transitions.add(tr);
                                            newState.addOutTransition(tr);
                                            newStateD.addInTransition(tr);
                                            AvatarRelation ar = _avspec.getAvatarRelationWithSignal(correspondingSig);

                                            if (!(ar.isAsynchronous())) {
                                                tr = new AUTTransition(newStateD.id, "", newState.id);
                                                transitions.add(tr);
                                                newStateD.addOutTransition(tr);
                                                newState.addInTransition(tr);
                                            }

                                            // We can remove the transition between the prev and the first sttae of this read


                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (found) {
                        // We have to remove the old transition
                        if (state.inTransitions.size() > 0) {
                            TraceManager.addDev("Found: we have to remove the wrong transition tr=" + state.inTransitions.get(0).toString());
                            previousState.outTransitions.remove(0);
                            state.inTransitions.remove(0);
                            transitions.remove(previousTr);
                        } else {
                            TraceManager.addDev("No inTransition for state " + state.id);
                        }

                    }
                }
            }
        }


        // We remove all transitions from previous to the first next in read operations
        for (AUTState state : previousRead) {
            if (state.outTransitions.size() > 0) {
                AUTTransition tr = state.outTransitions.get(0);
                AUTState otherState = states.get(tr.destination);
                state.outTransitions.remove(0);
                otherState.inTransitions.remove(0);
                transitions.remove(tr);
            }
        }

        for (AUTState state : newStates) {
            states.add(state);
        }

        for (AUTState state : oldStatesRemove) {

            ArrayList<AUTTransition> newTR = new ArrayList<>();
            for (AUTTransition atPrev : state.inTransitions) {
                for (int i = 0; i < state.outTransitions.size(); i++) {
                    AUTTransition tr = new AUTTransition(atPrev.origin,
                            state.outTransitions.get(i).transition, state.outTransitions.get(i).destination);
                    newTR.add(tr);
                    state.outTransitions.get(i);
                }
            }

            AUTState sTmp;
            for (AUTTransition atPrev : state.outTransitions) {
                transitions.remove(atPrev);
                sTmp = states.get(atPrev.destination);
                sTmp.removeInTransition(atPrev);
            }

            for (AUTTransition atPrev : state.inTransitions) {
                transitions.remove(atPrev);
                sTmp = states.get(atPrev.origin);
                sTmp.removeOutTransition(atPrev);
            }

            for (AUTTransition tr : newTR) {
                transitions.add(tr);
                sTmp = states.get(tr.destination);
                sTmp.addInTransition(tr);
                sTmp = states.get(tr.origin);
                sTmp.addOutTransition(tr);
            }
        }

        for (AUTState state : oldStatesRemove) {
            states.remove(state);
        }

        int cpt = 0;

        cpt = 0;
        for (AUTState state : states) {
            if (cpt != state.id) {
                // We now have to compact state ids
                // We place the last state at index position
                // We  accordingly modify transitions
                // Nothing to do if state index is already at last position
                state.id = cpt;
                for (AUTTransition atIn : state.inTransitions) {
                    atIn.destination = cpt;
                }
                for (AUTTransition atOut : state.outTransitions) {
                    atOut.origin = cpt;
                }
            }
            cpt++;
        }

        cpt = 0;


        /*for(AUTState state: states) {
            TraceManager.addDev("" + cpt + ": state " + state.id + " / " + state.info + " / " + state);
            cpt ++;
        }*/

        // Optimization: remove states representing empty transitions
        ArrayList<AUTState> toBeRemoved = new ArrayList<>();
        for (AUTState state : states) {
            //TraceManager.addDev("Testing " + state.referenceObject.toString());
            if (state.referenceObject instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) state.referenceObject;
                TraceManager.addDev("Found  transition ID: " + at.getID());
                if (at.isEmpty()) {
                    TraceManager.addDev("Found empty transition ID: " + at.getID());
                    if (!at.isGuarded()) {
                        TraceManager.addDev("Not guarded ID: " + at.getID());
                        if (at.getNexts().size() > 0) {
                            //if (state.outTransitions.size() == 1) {
                            // State can be removed

                            // We can update the transitions
                            // We assume that there is only one out transition

                            toBeRemoved.add(state);
                            ArrayList<AUTTransition> newTR = new ArrayList<>();
                            for (AUTTransition atPrev : state.inTransitions) {
                                for (int i = 0; i < state.outTransitions.size(); i++) {
                                    AUTTransition tr = new AUTTransition(atPrev.origin,
                                            state.outTransitions.get(i).transition, state.outTransitions.get(i).destination);
                                    newTR.add(tr);
                                    state.outTransitions.get(i);
                                }
                            }

                            AUTState sTmp;
                            for (AUTTransition atPrev : state.outTransitions) {
                                transitions.remove(atPrev);
                                sTmp = states.get(atPrev.destination);
                                sTmp.removeInTransition(atPrev);
                            }

                            for (AUTTransition atPrev : state.inTransitions) {
                                transitions.remove(atPrev);
                                sTmp = states.get(atPrev.origin);
                                sTmp.removeOutTransition(atPrev);
                            }

                            for (AUTTransition tr : newTR) {
                                transitions.add(tr);
                                sTmp = states.get(tr.destination);
                                sTmp.addInTransition(tr);
                                sTmp = states.get(tr.origin);
                                sTmp.addOutTransition(tr);
                            }
                        }
                    }
                }
            }
        }


        for (AUTState state : toBeRemoved) {
            states.remove(state);
        }


        // We update all ids;
        cpt = 0;
        for (AUTState state : states) {
            if (cpt != state.id) {
                // We now have to compact state ids
                // We place the last state at index position
                // We  accordingly modify transitions
                // Nothing to do if state index is already at last position
                state.id = cpt;
                for (AUTTransition atIn : state.inTransitions) {
                    atIn.destination = cpt;
                }
                for (AUTTransition atOut : state.outTransitions) {
                    atOut.origin = cpt;
                }
            }
            cpt++;
        }

        cpt = 0;
        /*for(AUTState state: states) {
            TraceManager.addDev("" + cpt + ": state " + state.id + " / " + state.info + " / " + state);
            cpt ++;
        }*/


        // Rework Avatar Actions on Signals if multiple, synchros for the same AAOS

        // Make the graph
        graph = new AUTGraph(states, transitions);


    }


    private void addReferenceElement(AUTState _state,  AvatarStateMachineElement _elt) {
        ArrayList<AvatarStateMachineElement> refs = null;
        if (_state.referenceObject == null) {
            refs = new ArrayList<>();
            _state.referenceObject = refs;
        }
        refs.add(_elt);
    }
    @SuppressWarnings("unchecked")
    private boolean hasReferenceObject(AUTState _state,  AvatarStateMachineElement _elt) {
        if (_state.referenceObject == null) {
            return false;
        }
        ArrayList<AvatarStateMachineElement> refs = (ArrayList<AvatarStateMachineElement> )(_state.referenceObject);
        return refs.contains(_elt);
    }


    private AUTState makeCompactDependencyGraphForAvatarElement(AvatarBlock bl, AvatarStateMachineElement _elt,
                                                         AUTState _previousS, AvatarStateMachineElement _previousE,
                                                         ArrayList<AUTState> _states,
                                                         ArrayList<AUTTransition> _transitions, boolean withID,
                                                                AvatarTransition _previousAvatarTransition) {
        if (_elt == null) {
            return null;
        }

        if (_elt instanceof AvatarTransition) {
            if (_elt.getNext(0) != null) {
                return makeCompactDependencyGraphForAvatarElement(bl, _elt.getNext(0), _previousS, _elt, _states, _transitions, withID,
                        (AvatarTransition) _elt);
            } else {
                return _previousS;
            }
        }

        AUTState state = new AUTState(id);
        _states.add(state);
        if (_previousAvatarTransition != null) {
            addReferenceElement(state, _previousAvatarTransition);
        }
        addReferenceElement(state, _elt);
        if (withID) {
            state.info = _elt.toStringExtendedID();
        } else {
            state.info = bl.getName() + " / " + _elt.getExtendedName();
        }

        if (_elt.referenceObject instanceof ElementWithNew) {
            if (((ElementWithNew) (_elt.referenceObject)).isNew()) {
                state.info += " (New)";
            }
        }

        //putState(_elt, state);

        //fromStates.put(state, _elt);
        id++;

        if (_previousE != null) {
            AUTTransition tr = new AUTTransition(_previousS.id, "", state.id);
            _transitions.add(tr);
            _previousS.addOutTransition(tr);
            state.addInTransition(tr);
        } else {
            state.isOrigin = true;
        }

        // Handling all nexts
        //if (!(_elt instanceof AvatarActionOnSignal)) {
        for (AvatarStateMachineElement eltN : _elt.getNexts()) {
            // Already a state for a next?
            AUTState stateN = null;
            for (AUTState st : _states) {
                if (hasReferenceObject(st, eltN)) {
                    stateN = st;
                    break;
                }
            }
            //AUTState stateN = getFirstStateFor(eltN);
            if (stateN != null) {
                AUTTransition tr = new AUTTransition(state.id, "", stateN.id);
                _transitions.add(tr);
                state.addOutTransition(tr);
                stateN.addInTransition(tr);
            } else {
                makeCompactDependencyGraphForAvatarElement(bl, eltN, state, _elt, _states, _transitions, withID, null);
            }
        }
        //}
        return state;
    }


    @SuppressWarnings("unchecked")
    public AvatarCompactDependencyGraph clone() {
        AvatarCompactDependencyGraph adg = new AvatarCompactDependencyGraph();
        AUTGraph g = graph.cloneMe();
        adg.setGraph(g);

        //HashMap<AvatarElement, ArrayList<AUTState>> newToStates = new HashMap<>();
        //HashMap<AUTState, AvatarElement> newFromStates = new HashMap<>();

        //adg.setRefs(newToStates, newFromStates);

        // Filling states references
        /*for (AvatarElement ae : toStates.keySet()) {
            ArrayList<AUTState> list  = toStates.get(ae);

            ArrayList<AUTState> newList = new ArrayList<>();
            for(AUTState oldS: list) {
                // We must find the corresponding state in the new graph
                AUTState newState = g.getState(oldS.id);
                newList.add(newState);
                newFromStates.put(newState, ae);
            }
            if (newList.size() > 0) {
                newToStates.put(ae, newList);
            }
        }*/


        return adg;
    }

    public AvatarCompactDependencyGraph reduceGraphBefore(ArrayList<AvatarElement> eltsOfInterest) {
        AvatarCompactDependencyGraph result = clone();

        /*TraceManager.addDev("Size of original graph: s" + graph.getNbOfStates() + " t" + graph.getNbOfTransitions());
        TraceManager.addDev("Size of graph after clone: s" + result.graph.getNbOfStates() + " t" + result.graph.getNbOfTransitions());

        TraceManager.addDev("old graph:\n" + graph.toStringAll() + "\n");

        TraceManager.addDev("Cloned graph:\n" + result.graph.toStringAll() + "\n");*/

        /*TraceManager.addDev("Size of original graph toStates:" + toStates.size());
        TraceManager.addDev("Size of original graph fromStates:" + fromStates.size());
        TraceManager.addDev("Size of cloned graph toStates:" + result.toStates.size());
        TraceManager.addDev("Size of cloned graph fromStates:" + result.fromStates.size());*/

        // For each state, we figure out whether if it is linked to go to the elt states
        // or if they are after the elts.

        HashSet<AUTState> beforeStates = new HashSet<>();


        // We take each elt one after the other and we complete the after or before states
        for (AvatarElement ae : eltsOfInterest) {
            //TraceManager.addDev("Considering elt:" + ae.getName());
            Object ref = ae.getReferenceObject();
            if (ref != null) {
                // Finding the state referencing o
                AUTState stateOfInterest = null;
                for (AUTState s : graph.getStates()) {
                    AvatarElement elt = (AvatarElement) s.referenceObject;
                    if (elt.getReferenceObject() == ref) {
                        stateOfInterest = s;
                        break;
                    }
                }

                if (stateOfInterest != null) {
                    //TraceManager.addDev("Has a state of interest: " + stateOfInterest.id);
                    for (AUTState state : graph.getStates()) {
                        if (state == stateOfInterest) {
                            beforeStates.add(result.graph.getState(state.id));
                        } else {
                            /*if (graph.hasPathFromTo(state.id, stateOfInterest.id)) {
                                beforeStates.add(result.graph.getState(state.id));
                            }*/
                            if (graph.canGoFromTo(state.id, stateOfInterest.id)) {
                                beforeStates.add(result.graph.getState(state.id));
                            }
                        }
                    }
                }
            }
        }

        TraceManager.addDev("Size of before: " + beforeStates.size());

        // We now have to figure out which states have to be removed
        ArrayList<AUTState> toRemoveStates = new ArrayList<>();
        for (AUTState st : result.graph.getStates()) {
            if (!beforeStates.contains(st)) {
                toRemoveStates.add(st);
            }
        }

        TraceManager.addDev("Size of remove: " + toRemoveStates.size());

        result.graph.removeStates(toRemoveStates);
        result.removeReferencesOf(toRemoveStates);

        /*TraceManager.addDev("Size of graph after remove: s" + result.graph.getNbOfStates() + " t" + result.graph.getNbOfTransitions());
        TraceManager.addDev("New graph:\n" +result.graph.toStringAll() + "\n");*/


        // We have to update state references


        return result;

    }

    public void removeReferencesOf(Collection<AUTState> _c) {
        /*for (AUTState st : _c) {
            fromStates.remove(st);
        }

        ArrayList<AvatarElement> toBeRemoved = new ArrayList<>();
        for (AvatarElement ae : toStates.keySet()) {
            if (_c.contains(toStates.get(ae))) {
                toBeRemoved.add(ae);
            }
        }

        for (AvatarElement ae : toBeRemoved) {
            toStates.remove(ae);
        }*/
    }


}
