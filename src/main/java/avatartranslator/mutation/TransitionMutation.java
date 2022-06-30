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

package avatartranslator.mutation;

import avatartranslator.*;
//import myutil.TraceManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Class TransitionMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */

public abstract class TransitionMutation extends UnnamedStateMachineElementMutation {

    protected TransitionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType) {
        super(_blockName);
        setFrom(_fromString, _fromType);
        setTo(_toString, _toType);
        initActions();
    }

    protected TransitionMutation(String _blockName, String _transitionString, int _transitionType) {
        super(_blockName);
        setTransition(_transitionString, _transitionType);
        initActions();
    }

    protected TransitionMutation(String _blockName, String _fromString, int _fromType, String _toString, int _toType, String _transitionString, int _transitionType) {
        this(_blockName, _fromString, _fromType, _toString, _toType);
        setTransition(_transitionString, _transitionType);
    }
    
    private String fromString;
    private String toString;
    private String transitionString;

    private int fromType = UNDEFINED_TYPE;
    private int toType = UNDEFINED_TYPE;
    private int transitionType = UNDEFINED_TYPE;

    private double probability;
    private boolean probabilitySet = false;

    private String guard = "";
    private boolean guardSet = false;

    private String minDelay = "", maxDelay = "";
    private boolean delaysSet = false;
    private String delayExtra1 = ""; // Used for some of the distribution law
    private String delayExtra2 = ""; // Used for some of the distribution law
    private int delayDistributionLaw;
    private boolean delayDistributionLawSet = false;

    private String minCompute = "", maxCompute = "";
    private boolean computesSet = false;

    private List<String> actions; // actions on variable, or method call
    private boolean actionsSet = false;

    private void setFrom(String _fromString) {
        fromString = _fromString;
    }

    private void setFrom(String _fromString, int _fromType) {
        setFrom(_fromString);
        fromType = _fromType;
    }

    protected String getFrom() {
        return fromString;
    }

    protected AvatarStateMachineElement getFromElement(AvatarSpecification _avspec) {
        if (isFromSet()) return getElement(_avspec, fromType, fromString);
        AvatarTransition trans = getElement(_avspec);
        AvatarStateMachine asm = getAvatarStateMachine(_avspec);
        return asm.getElementsLeadingTo(trans).get(0);
    }

    protected boolean isFromSet() {
        return fromType!=UNDEFINED_TYPE;
    }

    private void setTo(String _name) {
        toString = _name;
    }

    private void setTo(String _toString, int _toType) {
        setTo(_toString);
        fromType = _toType;
    }
    
    protected String getTo() {
        return toString;
    }

    protected AvatarStateMachineElement getToElement(AvatarSpecification _avspec) {
        if (isToSet()) return getElement(_avspec, toType, toString);
        AvatarTransition trans = getElement(_avspec);
        return trans.getNext(0);
    }

    protected boolean isToSet() {
        return toType!=UNDEFINED_TYPE;
    }

    private void setTransition(String _transitionString) {
        transitionString = _transitionString;
    }

    private void setTransition(String _transitionString, int _transitionType) {
        setTransition(_transitionString);
        transitionType = _transitionType;
    }

    protected String getTransition() {
        if (transitionType == UNDEFINED_TYPE) return "undefined";
        return transitionString;
    }

    protected boolean isTransitionSet() {
        return transitionType != UNDEFINED_TYPE;
    }

    public void setProbability(double _probability) {
        probability = _probability;
        probabilitySet = true;
    }

    protected boolean isProbabilitySet() {
        return probabilitySet;
    }

    protected double getProbability() {
        return probability;
    }

    public void setGuard(String _guard) {
        guard = _guard;
        guardSet = true;
    }

    protected boolean isGuardSet() {
        return guardSet;
    }

    protected String getGuard() {
        return guard;
    }

    protected AvatarGuard getAvatarGuard(AvatarSpecification _avspec) {
        return AvatarGuard.createFromString(getBlock(_avspec), getGuard());
    }

    public void setDelays(String _minDelay, String _maxDelay) {
        minDelay = _minDelay;
        maxDelay = _maxDelay;
        delaysSet = true;
    }

    protected boolean areDelaysSet() {
        return delaysSet;
    }

    protected String getMinDelay() {
        return minDelay;
    }

    protected String getMaxDelay() {
        if (maxDelay.trim().length() == 0) {
            return getMinDelay();
        }
        return maxDelay;
    }

    protected void setDelayExtra(String _delayExtra) {
        delayExtra1 = _delayExtra;
    }

    protected void setDelayExtras(String _delayExtra1, String _delayExtra2) {
        setDelayExtra(_delayExtra1);
        delayExtra2 = _delayExtra2;
    }

    protected String getDelayExtra1() {
        return delayExtra1;
    }

    protected String getDelayExtra2() {
        return delayExtra2;
    }

    public void setDelayDistributionLaw(int _law) {
        delayDistributionLaw = _law;
        delayDistributionLawSet = true;
    }
    
    public void setDelayDistributionLaw(int _law, String _delayExtra1) {
        setDelayDistributionLaw(_law);
        setDelayExtra(_delayExtra1);
    }

    public void setDelayDistributionLaw(int _law, String _delayExtra1, String _delayExtra2) {
        setDelayDistributionLaw(_law);
        setDelayExtras(_delayExtra1, _delayExtra2);
    }

    protected boolean isDelayDistributionLawSet() {
        return delayDistributionLawSet;
    }

    protected int getDelayDistributionLaw() {
        return delayDistributionLaw;
    }

    public void setComputes(String _minCompute, String _maxCompute) {
        minCompute = _minCompute;
        maxCompute = _maxCompute;
        computesSet = true;
    }

    protected boolean areComputesSet() {
        return computesSet;
    }

    protected String getMinCompute() {
        return minCompute;
    }

    protected String getMaxCompute() {
        if (maxCompute.trim().length() == 0) {
            return getMinCompute();
        }
        return maxCompute;
    }

    private void initActions() {
        actions = new LinkedList<>();
    }

    public void addAction(String _action) {
        actions.add(_action);
        actionsSet = true;
    }

    public void setNoActions() {
        actions.clear();
        actionsSet = true;
    }

    protected boolean areActionsSet() {
        return actionsSet;
    }

    protected List<String> getActions() {
        return actions;
    }

    protected String getAction(int _index) {
        return getActions().get(_index);
    }

    public AvatarTransition getElement(AvatarSpecification _avspec) {
        //TraceManager.addDev(String.valueOf(nameType));
        if (transitionType == UNDEFINED_TYPE) {
            AvatarStateMachineElement fromElement = getElement(_avspec, fromType, fromString);
            //TraceManager.addDev(fromElement.toString());
            AvatarStateMachineElement toElement = getElement(_avspec, toType, toString);
            //TraceManager.addDev(toElement.toString());
            List<AvatarStateMachineElement> fromNexts = fromElement.getNexts();
            for (AvatarStateMachineElement elt : fromNexts) {
                //TraceManager.addDev(elt.toString());
                if (elt instanceof AvatarTransition) {
                    AvatarTransition trans = (AvatarTransition)elt;
                    boolean flag = trans.hasNext(toElement);

                    if (flag && isProbabilitySet()) 
                        flag = (trans.getProbability() == this.getProbability());

                    if (flag && isGuardSet())
                        flag = (trans.getGuard().equals(this.getAvatarGuard(_avspec)));

                    if (flag && areDelaysSet())
                        if (trans.getMinDelay().equals(this.getMinDelay()))
                            flag = trans.getMaxDelay().equals(this.getMaxDelay());
                        else flag = false;

                    if (flag && isDelayDistributionLawSet()) {
                        if (trans.getDelayDistributionLaw() == this.getDelayDistributionLaw()) {
                            if (trans.getDelayExtra1().equals("") || trans.getDelayExtra1().equals(this.getDelayExtra1())) {
                                flag = (trans.getDelayExtra2().equals("") || trans.getDelayExtra2().equals(this.getDelayExtra2()));
                            } else flag = false;
                        } else flag = false;
                    }

                    if (flag && areComputesSet()) 
                        if (trans.getMinCompute().equals(this.getMinCompute())) {
                            flag = trans.getMaxCompute().equals(this.getMaxCompute());
                        } else flag = false;

                    if (flag && areActionsSet()) {
                        int len = trans.getNbOfAction();
                        if ( len == this.getActions().size()) {
                            for (int i = 0; flag && i < len; i++) {
                                flag = (trans.getAction(i).toString() == AvatarTerm.createActionFromString(getBlock(_avspec), this.getAction(i)).toString());
                            }
                        }
                    }

                    if (flag) return trans;
                }
            }
            return null;
        }
        AvatarStateMachineElement element = getElement(_avspec, transitionType, transitionString);
        if (element != null && element instanceof AvatarTransition) return (AvatarTransition)element;
        return null;
    }
}
