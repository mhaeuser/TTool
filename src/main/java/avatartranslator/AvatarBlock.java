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
import myutil.NameChecker;
import myutil.TraceManager;
import myutil.intboolsolver.IBSParamComp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


/**
 * Class AvatarBlock
 * Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE, Raja GATGOUT
 * @version 2.0 15/06/2023
 */
public class AvatarBlock extends AvatarElement implements AvatarStateMachineOwner, NameChecker.NameStartWithUpperCase, IBSParamComp {

    private AvatarBlock father;
    private List<AvatarAttribute> attributes;
    private List<AvatarMethod> methods;
    private List<AvatarSignal> signals;
    private AvatarStateMachine asm;
    private AvatarSpecification avspec;

    private int blockIndex; //Index of block in the Avatar Specification

    private String globalCode;

    private int booleanOffset;
    private int attributeOptRatio;
    private List<AvatarAttribute> constants;


    public AvatarBlock(String _name, AvatarSpecification _avspec, Object _referenceObject) {
        super(_name, _referenceObject);
        this.avspec = _avspec;

        attributes = new LinkedList<AvatarAttribute>();
        constants = new LinkedList<AvatarAttribute>();
        methods = new LinkedList<AvatarMethod>();
        signals = new LinkedList<AvatarSignal>();
        asm = new AvatarStateMachine(this, "statemachineofblock__" + _name, _referenceObject);
        booleanOffset = -1;
        attributeOptRatio = 1;
    }

    public static boolean isAValidMethodCall(AvatarStateMachineOwner owner, String _s) {
        int i;

        //TraceManager.addDev("****** method=" + _s);
        String all = _s;

        int indexeq = _s.indexOf('=');

        if (indexeq != -1) {
            _s = _s.substring(indexeq + 1, _s.length()).trim();
            //TraceManager.addDev("****** cut method: " + _s);
        }

        int index0 = _s.indexOf("(");
        int index1 = _s.indexOf(")");
        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            //TraceManager.addDev("No parenthesis");
            return false;
        }

        String method = _s.substring(0, index0);

        AvatarMethod am = owner.getAvatarMethodWithName(method);
        if (am == null) {
            //TraceManager.addDev("Method not found");
            return false;
        }

        String[] actions;
        AvatarAttribute aa;
        String params = _s.substring(index0 + 1, index1).trim();
        //TraceManager.addDev("params=" + params);
        if (params.length() == 0) {
            if (am.getListOfAttributes().size() != 0) {
                return false;
            }
        } else {
            //TraceManager.addDev("params=" + params);
            actions = params.split(",");
            if (am.getListOfAttributes().size() != actions.length) {
                return false;
            }

            for (i = 0; i < actions.length; i++) {
                //TraceManager.addDev("params=" + params +  " actions=" + actions[i]);
                // Must check the validity of this action

                if (am.getListOfAttributes().get(i).isInt()) {
                    if (AvatarSyntaxChecker.isAValidIntExpr(null, owner, actions[i].trim()) < 0) {
                        return false;
                    }
                } else {
                    // Assume it is a bool attribute
                    if (AvatarSyntaxChecker.isAValidBoolExpr(null, owner, actions[i].trim()) < 0) {
                        return false;
                    }
                }

            /*aa = getAvatarAttributeWithName(actions[i].trim());
              if (aa == null) {
              //TraceManager.addDev("Failed for attribute " + actions[i]);
              return false;
              }*/
            }
        }

        // Checking for return attributes
        //TraceManager.addDev("Checking for return attributes");
        if (indexeq != -1) {
            //TraceManager.addDev("Checking for return params");
            String retparams = all.substring(0, indexeq).trim();
            //TraceManager.addDev("Retparam=" + retparams);

            // multiple params
            if (retparams.length() > 0) {
                if (retparams.charAt(0) == '(') {
                    if (retparams.charAt(retparams.length() - 1) != ')') {
                        //TraceManager.addDev("Bad format for return params: " + retparams);
                        return false;
                    }

                    retparams = retparams.substring(1, retparams.length() - 1).trim();
                    actions = retparams.split(",");
                    if (am.getListOfReturnAttributes().size() != actions.length) {
                        return false;
                    }

                    for (i = 0; i < actions.length; i++) {
                        //TraceManager.addDev("params=" + retparams + " actions=" + actions[i]);
                        aa = owner.getAvatarAttributeWithName(actions[i].trim());
                        if (aa == null) {
                            //TraceManager.addDev("Failed for attribute " + actions[i]);
                            return false;
                        } else if (aa.isConstant()) {
                            return false;
                        }
                    }

                } else {
                    // Only one param.
                    aa = owner.getAvatarAttributeWithName(retparams);
                    if (aa == null) {
                        //TraceManager.addDev("Failed for return attribute " + retparams);
                        return false;
                    } else if (aa.isConstant()) {
                        return false;
                    }

                    if (am.getListOfReturnAttributes().size() != 1) {
                        //TraceManager.addDev("Wrong number of return parameters in :" + retparams);
                        return false;
                    }
                }

            }
        }
        //TraceManager.addDev("Ok for method " + _s);

        return true;
    }

    // For code generation
    public void addGlobalCode(String _code) {
        if (_code == null) {
            return;
        }
        if (globalCode == null) {
            globalCode = _code;
            return;
        }
        globalCode += _code + "\n";
    }

    public String getGlobalCode() {
        if (globalCode == null) {
            return "";
        }
        return globalCode;
    }

    public AvatarBlock getFather() {
        return father;
    }

    // Relation with parent block
    public void setFather(AvatarBlock _father) {
        father = _father;
    }

    /*public void addAttribute(AvatarAttribute _attribute) {
        attributes.add(_attribute);
	}*/

    public AvatarStateMachine getStateMachine() {
        return asm;
    }

    public void addMethod(AvatarMethod _method) {
        methods.add(_method);
    }

    public void addSignal(AvatarSignal _signal) {
        signals.add(_signal);
    }

    public void clearAttributes() {
        attributes.clear();
    }

    public AvatarSignal addSignalIfApplicable(String name, int type, Object refObject) {
        AvatarSignal sig = getSignalByName(name);
        if (sig != null) {
            return sig;
        }
        sig = new AvatarSignal(name, type, refObject);
        addSignal(sig);
        return sig;

    }

    public List<AvatarAttribute> getAttributes() {
        return attributes;
    }

    public List<AvatarAttribute> getConstants() {
        return constants;
    }

    public List<AvatarMethod> getMethods() {
        return methods;
    }

    public List<AvatarSignal> getSignals() {
        return signals;
    }

    public AvatarSignal getSignalByName(String _name) {
        for (AvatarSignal sig : signals) {
            if (sig.getName().compareTo(_name) == 0) {
                return sig;
            }
        }

        if (father != null) {
            return father.getSignalByName(_name);
        }

        return null;
    }

    public int getNbOfASMGraphicalElements() {
        if (asm == null) {
            return 0;
        }

        return asm.getNbOfASMGraphicalElements();
    }

    public AvatarSpecification getAvatarSpecification() {
        return this.avspec;
    }

    public void addAttribute(AvatarAttribute _aa) {
        if (getAvatarAttributeWithName(_aa.getName()) == null) {
            //TraceManager.addDevStackTrace("Adding attribute " + _aa.getName() + " to block " + getName());
            attributes.add(_aa);
        }
    }

    public AvatarAttribute addIntAttributeIfApplicable(String _name) {
        AvatarAttribute old = getAvatarAttributeWithName(_name);
        if (old != null) {
            return old;
        }

        AvatarAttribute aa = new AvatarAttribute(_name, AvatarType.INTEGER, this, null);
        attributes.add(aa);
        return aa;
    }

    public String toString() {
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer("block:" + getName() + " ID=" + getID() + " \n");
        if (getFather() != null) {
            sb.append("  subblock of: " + getFather().getName() + " ID=" + getFather().getID() + "\n");
        } else {
            sb.append("  top level block\n");
        }
        for (AvatarAttribute attribute : attributes) {
            sb.append("  attribute: " + attribute.toString() + " ID=" + attribute.getID() + "\n");
        }
        for (AvatarMethod method : methods) {
            sb.append("  method: " + method.toString() + " ID=" + method.getID() + "\n");
        }
        for (AvatarSignal signal : signals) {
            sb.append("  signal: " + signal.toString() + " ID=" + signal.getID() + "\n");
        }
        if (asm != null) {
            sb.append(asm.toString());
        } else {
            sb.append("No state machine");
        }

        return sb.toString();
    }

    public String toStringRecursive() {
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer("block:" + getName() + " ID=" + getID() + " \n");
        if (getFather() != null) {
            sb.append("  subblock of: " + getFather().getName() + " ID=" + getFather().getID() + "\n");
        } else {
            sb.append("  top level block\n");
        }
        for (AvatarAttribute attribute : attributes) {
            sb.append("  attribute: " + attribute.toString() + " ID=" + attribute.getID() + "\n");
        }
        for (AvatarMethod method : methods) {
            sb.append("  method: " + method.toString() + " ID=" + method.getID() + "\n");
        }
        for (AvatarSignal signal : signals) {
            sb.append("  signal: " + signal.toString() + " ID=" + signal.getID() + "\n");
        }
        if (asm != null) {
            sb.append(asm.toStringRecursive());
        } else {
            sb.append("No state machine");
        }

        return sb.toString();
    }

    public String toShortString() {
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer("block:" + getName() + " ID=" + getID() + " \n");
        if (getFather() != null) {
            sb.append("  subblock of: " + getFather().getName() + " ID=" + getFather().getID() + "\n");
        } else {
            sb.append("  top level block\n");
        }
        for (AvatarAttribute attribute : attributes) {
            sb.append("  attribute: " + attribute.toString() + " ID=" + attribute.getID() + "\n");
        }
        for (AvatarMethod method : methods) {
            sb.append("  method: " + method.toString() + " ID=" + method.getID() + "\n");
        }
        for (AvatarSignal signal : signals) {
            sb.append("  signal: " + signal.toString() + " ID=" + signal.getID() + "\n");
        }


        return sb.toString();
    }

    public int attributeNb() {
        return attributes.size();
    }

    public int stateNb() {
        return asm.stateNb();
    }

    public AvatarState getState(int index) {
        return asm.getState(index);
    }

    public void putAllTimers(ArrayList<AvatarAttribute> timers) {
        for (AvatarAttribute attribute : attributes) {
            if (attribute.getType() == AvatarType.TIMER) {
                timers.add(attribute);
            }
        }
    }

    public boolean hasTimer(String _name) {
        for (AvatarAttribute attribute : attributes) {
            if (attribute.getType() == AvatarType.TIMER) {
                if (attribute.getName().compareTo(_name) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public AvatarAttribute getAttribute(int _index) {
        return attributes.get(_index);
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(int _blockIndex) {
        blockIndex = _blockIndex;
    }

    public boolean setAttributeValue(int _index, String _value) {
        AvatarAttribute aa = attributes.get(_index);
        if (aa == null) {
            return false;
        }
        aa.setInitialValue(_value);
        return true;
    }

    public int getIndexOfAvatarAttributeWithName(String _name) {
        int cpt = 0;
        for (AvatarAttribute attribute : attributes) {
            if (attribute.getName().compareTo(_name) == 0) {
                return cpt;
            }
            cpt++;
        }
        return -1;
    }

    public int getIndexOfConstantWithName(String _name) {
        int cpt = 0;

        if (constants == null) {
            return -1;
        }
        for (AvatarAttribute attribute : constants) {
            if (attribute.getName().compareTo(_name) == 0) {
                return cpt;
            }
            cpt++;
        }
        return -1;
    }

    public AvatarAttribute getConstantWithIndex(int index) {
        if (constants == null) {
            return null;
        }
        return constants.get(index);
    }

    /**
     * Look for an attribute with the provided name.
     *
     * @param _name The name of the attribute to look for.
     * @return The attribute if found, or null otherwise
     */
    public AvatarAttribute getAvatarAttributeWithName(String _name) {
        if ((attributes == null) || (_name == null)) {
            return null;
        }
        for (AvatarAttribute attribute : attributes) {
            if (attribute.getName().compareTo(_name) == 0) {
                return attribute;
            }
        }
        return null;
    }

    public AvatarMethod getAvatarMethodWithName(String _name) {
        for (AvatarMethod method : methods) {
            if (method.getName().compareTo(_name) == 0) {
                return method;
            }
        }

        if (getFather() != null) {
            return getFather().getAvatarMethodWithName(_name);
        }

        return null;
    }

    public AvatarAttribute getNTypeOfMethod(String methodName, int indexOfAttribute) {
        AvatarMethod am = getAvatarMethodWithName(methodName);
        if (am == null) {
            return null;
        }

        return am.getListOfAttributes().get(indexOfAttribute);
    }

    public AvatarBlock getBlockOfMethodWithName(String _name) {
        for (AvatarMethod method : methods) {
            if (method.getName().compareTo(_name) == 0) {
                return this;
            }
        }

        if (getFather() != null) {
            return getFather().getBlockOfMethodWithName(_name);
        }

        return null;
    }

    public AvatarSignal getAvatarSignalWithName(String _name) {
        for (AvatarSignal signal : signals) {
            if (signal.getName().compareTo(_name) == 0) {
                return signal;
            }
        }

        if (getFather() != null) {
            return getFather().getAvatarSignalWithName(_name);
        }

        return null;
    }

    public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
        return asm.getStateMachineElementFromReferenceObject(_o);
    }

    public boolean containsStateMachineElementWithReferenceObject(Object _o) {
        AvatarStateMachineElement asme = asm.getStateMachineElementFromReferenceObject(_o);
        return (asme != null);
    }

    public void removeAvatarSignal(AvatarSignal as) {
        if (signals.contains(as)) {
            signals.remove(as);
        }
    }

    public void removeTimers(AvatarSpecification _spec, List<AvatarBlock> _addedBlocks) {
        AvatarSignal asSet, asReset, asExpire;
        String name;
        AvatarAttribute value;
        HashMap<String, AvatarSignal> sets = new HashMap<>();
        HashMap<String, AvatarSignal> resets = new HashMap<>();
        HashMap<String, AvatarSignal> expires = new HashMap<>();


        // Add new timer signals
        for (AvatarAttribute aa : attributes) {
            if (aa.getType() == AvatarType.TIMER) {
                name = findUniqueSignalName("set_" + aa.getName());
                asSet = new AvatarSignal(name, AvatarSignal.OUT, aa.getReferenceObject());
                value = new AvatarAttribute("timerValue", AvatarType.INTEGER, this, aa.getReferenceObject());
                asSet.addParameter(value);
                addSignal(asSet);
                sets.put(aa.getName(), asSet);
                name = findUniqueSignalName("reset_" + aa.getName());
                asReset = new AvatarSignal(name, AvatarSignal.OUT, aa.getReferenceObject());
                addSignal(asReset);
                resets.put(aa.getName(), asReset);
                name = findUniqueSignalName("expire_" + aa.getName());
                asExpire = new AvatarSignal(name, AvatarSignal.IN, aa.getReferenceObject());
                addSignal(asExpire);
                expires.put(aa.getName(), asExpire);

                // Create a timer block, and connect signals
                String blockName = "Timer_" + aa.getName() + "_" + getName();
                while (_spec.getBlockWithName(blockName) != null) {
                    blockName += "0";
                }
                AvatarBlock ab = AvatarBlockTemplate.getTimerBlock(blockName, _spec, getReferenceObject(), null, null, null);
                _addedBlocks.add(ab);

                AvatarRelation ar;
                ar = new AvatarRelation("timerRelation", this, ab, getReferenceObject());
                ar.addSignals(getAvatarSignalWithName(asSet.getSignalName()), ab.getAvatarSignalWithName("set"));
                ar.addSignals(getAvatarSignalWithName(asReset.getSignalName()), ab.getAvatarSignalWithName("reset"));
                ar.addSignals(getAvatarSignalWithName(asExpire.getSignalName()), ab.getAvatarSignalWithName("expire"));
                _spec.addRelation(ar);
            }
        }

        name = findUniqueAttributeName("timerValue_");
        value = new AvatarAttribute(name, AvatarType.INTEGER, this, getReferenceObject());
        addAttribute(value);

        // Modify the state machine
        if (asm.removeTimers(name, sets, resets, expires)) {
            // Add an attribute for the timer value
            //value = new AvatarAttribute("__timerValue", AvatarType.INTEGER, this, getReferenceObject());
            //addAttribute(value);
        }

        // Remove Timer attribute
        //boolean hasTimerAttribute = false;
        List<AvatarAttribute> tmps = attributes;
        attributes = new LinkedList<AvatarAttribute>();
        for (AvatarAttribute aa : tmps) {
            if (aa.getType() != AvatarType.TIMER) {
                attributes.add(aa);
            } else {
                //hasTimerAttribute = true;
            }
        }
    }

    public String findUniqueSignalName(String inputName) {
        for (AvatarSignal signal : signals) {
            if (signal.getSignalName().compareTo(inputName) == 0) {
                return findUniqueSignalName(inputName + "0");
            }
        }
        return inputName;
    }

    public String findUniqueAttributeName(String inputName) {
        for (AvatarAttribute attr : attributes) {
            if (attr.getName().compareTo(inputName) == 0) {
                return findUniqueAttributeName(inputName + "0");
            }
        }
        return inputName;
    }

    public boolean hasTimerAttribute() {
        for (AvatarAttribute attr : attributes) {
            if (attr.isTimer()) {
                return true;
            }
        }
        return false;
    }

    public AvatarAttribute addTimerAttribute(String _name) {
        // Find a suitable name;
        AvatarAttribute aa;
        int cpt;

        for (cpt = 0; cpt < 50000; cpt++) {
            aa = getAvatarAttributeWithName(_name + cpt);
            if (aa == null) {
                break;
            }
        }

        aa = new AvatarAttribute(_name + cpt, AvatarType.TIMER, this, getReferenceObject());
        addAttribute(aa);

        return aa;
    }

    public AvatarAttribute addIntegerAttribute(String _name) {
        AvatarAttribute aa;
        int cpt;

        for (cpt = 0; cpt < 50000; cpt++) {
            aa = getAvatarAttributeWithName(_name + cpt);
            if (aa == null) {
                break;
            }
        }

        aa = new AvatarAttribute(_name + cpt, AvatarType.INTEGER, this, getReferenceObject());
        addAttribute(aa);

        return aa;
    }

    public boolean hasARealBehaviour() {
        if (asm == null) {
            return false;
        }

        AvatarStartState ass = asm.getStartState();
        if (ass == null) {
            return false;
        }

        AvatarStateMachineElement asme = ass.getNext(0);

        if (asme == null) {
            return false;
        }

        if (asme instanceof AvatarTransition) {
            AvatarTransition at = (AvatarTransition) asme;
            if (at.hasDelay() || at.hasCompute() || at.hasActions()) {
                return true;
            }

            if (at.getNext(0) instanceof AvatarStopState) {
                return false;
            }
        }

        return true;
    }

    public int getMaxNbOfParams() {
        if (asm == null) {
            return 0;
        }

        int cpt = 0;

        for (AvatarStateMachineElement asme : asm.getListOfElements()) {
            if (asme instanceof AvatarActionOnSignal) {
                cpt = Math.max(cpt, ((AvatarActionOnSignal) asme).getNbOfValues());
            }
        }
        return cpt;
    }

    public int getMaxNbOfMultipleBranches() {
        if (asm == null) {
            return 0;
        }

        int cpt = 1;

        for (AvatarStateMachineElement asme : asm.getListOfElements()) {
            if (asme instanceof AvatarState) {
                cpt = Math.max(cpt, asme.nbOfNexts());
            }
        }
        return cpt;
    }


    public int getIndexOfStartState() {
        if (asm == null) {
            return -1;
        }

        return asm.getIndexOfStartState();

    }

    public int getIndexOfRealStartState() {
        if (asm == null) {
            return -1;
        }

        int cpt = 0;
        AvatarStateElement ase = asm.getStartState();

        while ((ase != null) && (cpt < 50)) {
            if (ase.getNexts().size() != 1) {
                break;
            }

            AvatarTransition at = (AvatarTransition) (ase.getNext(0));
            if (!(at.isEmpty())) {
                break;
            }

            if (ase.getNexts().size() != 1) {
                break;
            }

            AvatarStateMachineElement next = at.getNext(0);
            if (!(next instanceof AvatarStateElement)) {
                break;
            }

            ase = (AvatarStateElement) next;

            cpt++;
        }

        if (ase != null) {
            return asm.getIndexOfState(ase);
        }

        return -1;

    }

    public ArrayList<AvatarElement> getAttributesOverMax(int maxV) {
        ArrayList<AvatarElement> outside = new ArrayList<>();
        for (AvatarAttribute aa : attributes) {
            if (aa.isInt()) {
                if (aa.hasInitialValue()) {
                    try {
                        int initialVal = Math.abs(Integer.decode(aa.getInitialValue()));
                        if (initialVal > maxV) {
                            outside.add(aa);
                        }
                    } catch (Exception e) {
                        outside.add(aa);
                    }
                }
            }
        }
        return outside;
    }


    //move boolean attributes to last positions in list attributes
    public void sortAttributes() {
        List<AvatarAttribute> newAttributes = new LinkedList<AvatarAttribute>();

        for (AvatarAttribute attribute : attributes) {
            if (attribute.getType() != AvatarType.BOOLEAN) {
                newAttributes.add(attribute);
            }
        }

        booleanOffset = newAttributes.size();

        for (AvatarAttribute attribute : attributes) {
            if (attribute.getType() == AvatarType.BOOLEAN) {
                newAttributes.add(attribute);
            }
        }

        attributes = newAttributes;
    }

    public int getBooleanOffset() {
        return booleanOffset;
    }


    public int getAttributeOptRatio() {
        return attributeOptRatio;
    }

    public void setAttributeOptRatio(int attributeOptRatio) {
        if (attributeOptRatio == 2 || attributeOptRatio == 4) {
            this.attributeOptRatio = attributeOptRatio;
        } else {
            this.attributeOptRatio = 1;
        }
    }


    public void removeConstantAttributes() {
        AvatarTransition at;

        if ((constants == null) || (constants.size() == 0)) {
            List<AvatarAttribute> newAttributes = new LinkedList<>();
            constants = new LinkedList<>();

            for (AvatarAttribute attr : attributes) {
                boolean toKeep = false;

                if (attr.isTimer()) {
                    toKeep = true;
                }

                for (AvatarStateMachineElement elt : asm.getListOfElements()) {

                    if (elt instanceof AvatarTransition) {
                        at = (AvatarTransition) elt;

                        for (AvatarAction aa : at.getActions()) {
                            if (aa instanceof AvatarActionAssignment) {
                                if (((AvatarActionAssignment) aa).leftHand.getName().compareTo(attr.name) == 0) {
                                    //assigned
                                    toKeep = true;
                                }
                            }
                        }
                    } else if (elt instanceof AvatarActionOnSignal) {
                        AvatarSignal sig = ((AvatarActionOnSignal) elt).getSignal();
                        if (sig != null && sig.isIn()) {
                            for (String val : ((AvatarActionOnSignal) elt).getValues()) {
                                if (val.compareTo(attr.name) == 0) {
                                    //assigned
                                    toKeep = true;
                                }
                            }
                        }
                    } else if (elt instanceof AvatarRandom) {
                        if (((AvatarRandom) elt).getVariable().compareTo(attr.name) == 0) {
                            toKeep = true;
                        }
                    } else if (elt instanceof AvatarQueryOnSignal) {
                        if (((AvatarQueryOnSignal) elt).getAttribute().getName().compareTo(attr.name) == 0) {
                            toKeep = true;
                        }
                    } else if (elt instanceof AvatarLibraryFunctionCall) {
                        if (((AvatarLibraryFunctionCall) (elt)).getReturnAttributes().contains(attr)) {
                            toKeep = true;
                        }
                    }


                    if (toKeep) {
                        break;
                    }
                }
                if (!toKeep) {
                    constants.add(attr);
                } else {
                    newAttributes.add(attr);
                }
            }
            attributes = newAttributes;
        }
    }


    public void removeUselessAttributes() {
        //AvatarTransition at;
        List<AvatarAttribute> toBeRemoved = new LinkedList<AvatarAttribute>();


        for (AvatarAttribute aa : attributes) {
            if (aa.isTimer()) {
                if (!(asm.isTimerUsed(aa))) {
                    toBeRemoved.add(aa);
                }

                // Regular attribute. We have to search where it is used
            } else {
                if (!asm.isRegularAttributeUsed(aa)) {
                    toBeRemoved.add(aa);
                }
            }
        }

        for (AvatarAttribute aa : toBeRemoved) {
            attributes.remove(aa);
        }

    }

    // Returns the number of replaced queries
    public int replaceQueriesWithReadSignal(AvatarSignal _origin, AvatarSignal _newSignal) {
        List<AvatarQueryOnSignal> elts = new LinkedList<>();
        AvatarQueryOnSignal aqos;

        // Getting all related ops
        for (AvatarStateMachineElement elt : asm.getListOfElements()) {
            if (elt instanceof AvatarQueryOnSignal) {
                aqos = (AvatarQueryOnSignal) elt;
                if (aqos.getSignal() == _origin) {
                    elts.add(aqos);
                }
            }
        }

        // Replacing ops
        for (AvatarQueryOnSignal q : elts) {
            AvatarActionOnSignal aaosQuery = new AvatarActionOnSignal("query", _newSignal, q.getReferenceObject(), asm.getOwner());
            aaosQuery.addValue(q.getAttribute().getName());
            asm.replace(q, aaosQuery);
        }

        return elts.size();


    }


    @Override
    public AvatarBlock advancedClone(AvatarSpecification avspec) {
        AvatarBlock av = new AvatarBlock(this.getName(), this.getAvatarSpecification(), this.getReferenceObject());

        cloneLinkToReferenceObjects(av);


        //Attributes, methods and signals
        for (AvatarAttribute aa : attributes) {
            av.addAttribute(aa.advancedClone(av));
        }
        for (AvatarMethod am : methods) {
            av.addMethod(am.advancedClone(av));
        }
        for (AvatarSignal as : signals) {
            av.addSignal(as.advancedClone(av));
        }

        // global code
        av.addGlobalCode(getGlobalCode());

        return av;
    }

    public NameChecker.NamedElement[] getSubNamedElements() {
        NameChecker.NamedElement[] nes = new NameChecker.NamedElement[attributes.size() + methods.size() + signals.size()];
        int index = 0;
        for (AvatarAttribute aa : attributes) {
            nes[index] = aa;
            index++;
        }
        for (AvatarMethod am : methods) {
            nes[index] = am;
            index++;
        }
        for (AvatarSignal as : signals) {
            nes[index] = as;
            index++;
        }
        return nes;
    }

    public void makeMinimalStateMachine() {
        AvatarStateMachine asm = getStateMachine();
        if (asm.getStartState() == null) {
            asm.makeBasicSM(this);
        }
    }




    // Returns errors as String
    public ArrayList<String> makeStateMachineFromJSON(String _jsonSpec, boolean forceIfIncorrectExpression) {
        if (_jsonSpec == null) {
            return null;
        }

        asm.clear();
        AvatarStartState startState = new AvatarStartState("StartState", null, asm.getOwner());
        asm.addElement(startState);
        asm.setStartState(startState);

        ArrayList<String> errors = new ArrayList<>();
        JSONObject mainObject;

        try {
            mainObject = new JSONObject(_jsonSpec);

            JSONArray statesJSON = mainObject.getJSONArray("states");

            for (int i = 0; i < statesJSON.length(); i++) {
                JSONObject state0 = statesJSON.getJSONObject(i);
                String name = AvatarSpecification.removeSpaces(state0.getString("name"));

                AvatarState sameState = asm.getStateByName(name);
                if (sameState != null) {
                    TraceManager.addDev("State " + name + " is defined several times");
                    errors.add("State " + name + " is defined several times");
                } else {
                    AvatarState newState = new AvatarState(name, this.getReferenceObject(), asm.getOwner());
                    asm.addElement(newState);
                    TraceManager.addDev("Adding state " + newState);

                    if (name.toLowerCase(Locale.ROOT).compareTo("start") == 0) {
                        TraceManager.addDev("This state is a start state: " + newState);
                        AvatarTransition at = new AvatarTransition(this, "firstTransition", this.getReferenceObject());
                        asm.addElement(at);
                        asm.getStartState().addNext(at);
                        at.addNext(newState);
                    }
                }
            }

            for (int i = 0; i < statesJSON.length(); i++) {
                JSONObject state0 = statesJSON.getJSONObject(i);
                String name = AvatarSpecification.removeSpaces(state0.getString("name"));
                if (name != null) {
                    TraceManager.addDev("Getting state: " + name);
                    AvatarState originState = asm.getStateByName(name);
                    if (originState != null) {
                        TraceManager.addDev("Non null state: " + name);
                        JSONArray transitionsJSON = state0.getJSONArray("transitions");
                        for (int j = 0; j < transitionsJSON.length(); j++) {
                            JSONObject transitions0 = transitionsJSON.getJSONObject(j);
                            String destinationState = transitions0.getString("destinationstate");
                            if (destinationState == null) {
                                //TraceManager.addDev("A transition has no destination: \"destinationstate\"");
                                // Looping on itself
                                destinationState = name;

                                //errors.add("A transition has no destination: \"destinationstate\"");
                            }
                            destinationState = AvatarSpecification.removeSpaces(destinationState);
                            AvatarState dstState = asm.getStateByName(destinationState);
                            if (dstState == null) {
                                TraceManager.addDev("A transition has a undefined destination state to state\"" + destinationState + "\"");
                                //errors.add("A transition has a undefined destination state to state\"" + destinationState + "\"");
                                AvatarState newState = new AvatarState(destinationState, this.getReferenceObject(), asm.getOwner());
                                asm.addElement(newState);
                                dstState = asm.getStateByName(destinationState);
                            }
                            AvatarTransition at =
                                    new AvatarTransition(this, "name" + "_to_" + destinationState, getReferenceObject());
                            asm.addElement(at);
                            originState.addNext(at);
                            at.addNext(dstState);

                            // Handling guard, after and action
                            String guard = transitions0.getString("guard");
                            if ((guard != null) && (guard.length() > 0)) {

                                int g = AvatarSyntaxChecker.isAValidGuard(getAvatarSpecification(), this, guard);

                                if ((g != 0) && (!forceIfIncorrectExpression)) {
                                    TraceManager.addDev("The following guard " + guard + " is incorrect");
                                    errors.add("The following guard " + guard + " is incorrect");
                                } else {
                                    at.setGuard(guard);
                                }

                                // Check if the guard is valid
                                    /*AvatarIBSolver.clearBadIdents();
                                    AvatarIBSExpressions.BExpr g = AvatarIBSolver.parseBool(this, guard);
                                    if (g == null) {
                                        HashSet<String> hs = AvatarIBSolver.getBadIdents();
                                        String badAttrib = "";
                                        for (String s : hs) {
                                            badAttrib += s + " ";
                                        }
                                        errors.add("The following elements of the guard " + guard + " are incorrect: " + badAttrib);
                                    } else {
                                        at.setGuard(guard);
                                    }*/
                            }

                            // After
                            String afterS = transitions0.getString("after");
                            if ((afterS != null) && (afterS.length() > 0)) {

                                int af = AvatarSyntaxChecker.isAValidIntExpr(getAvatarSpecification(), this, afterS);

                                if ((af != 0) && (!forceIfIncorrectExpression)){
                                    TraceManager.addDev("The following after clause \"" + afterS + "\" is incorrect (maybe the attribute does not exist?" +
                                            " In that case, directly use a numerical value)");
                                    errors.add("The following after clause \"" + afterS + "\" is incorrect (maybe the attribute does not exist?" +
                                            " In that case, directly use a numerical value)");
                                } else {
                                    at.setDelays(afterS, afterS);
                                }


                                    /*AvatarIBSolver.clearBadIdents();
                                    AvatarIBSExpressions.IExpr expr = AvatarIBSolver.parseInt(this, afterS);
                                    if (expr == null) {
                                        HashSet<String> hs = AvatarIBSolver.getBadIdents();
                                        String badAttrib = "";
                                        for (String s : hs) {
                                            badAttrib += s + " ";
                                        }
                                        errors.add("The following elements of the after " + afterS + " are incorrect: " + badAttrib);
                                    } else {
                                        at.setDelays(afterS, afterS);
                                    }*/
                            }

                            // Action
                            String actionS = transitions0.getString("action");

                            if ((actionS != null) && (actionS.length() > 0)) {
                                String actions[] = actionS.split(";");

                                try {
                                    for (String action : actions) {
                                        TraceManager.addDev("Handling action:" + action);
                                        // Affectation?
                                        if (action.contains("=")) {
                                            TraceManager.addDev("Handling affectation:" + action);
                                            int index = action.indexOf('=');
                                            String variableName = action.substring(0, index).trim();
                                            AvatarAttribute aa = getAvatarAttributeWithName(variableName);
                                            if (aa == null) {
                                                TraceManager.addDev("The following action is not valid: " + action + " because it contains an attribute " +
                                                        variableName + " which is not declared in the block " + getName());
                                                errors.add("The following action is not valid: " + action + " because it contains an attribute  " +
                                                        variableName + " which is not declared in the block " + getName());
                                            } else {
                                                String expr = action.substring(index + 1).trim();
                                                TraceManager.addDev("Analyzing expr:" + expr);
                                                AvatarIBSolver.clearBadIdents();
                                                if (aa.getType() == AvatarType.INTEGER) {
                                                    TraceManager.addDev("int expr");

                                                    int ex = AvatarSyntaxChecker.isAValidIntExpr(getAvatarSpecification(), this, expr);

                                                    if ((ex != 0) && (!forceIfIncorrectExpression)) {
                                                        TraceManager.addDev("The  action " + action + " is incorrect. Maybe it uses undeclared " +
                                                                "attributes? In that case");
                                                        errors.add("The  action " + action + " is incorrect. Maybe it uses undeclared " +
                                                                "attributes? In that case");
                                                    } else {
                                                        at.addAction(action);
                                                    }

                                                    /*AvatarIBSExpressions.IExpr iExpr = AvatarIBSolver.parseInt(this, expr);
                                                    if (iExpr == null) {
                                                        HashSet<String> hs = AvatarIBSolver.getBadIdents();
                                                        String badAttrib = "";
                                                        for (String s : hs) {
                                                            badAttrib += s + " ";
                                                        }
                                                        errors.add("The following elements of the int expr " + action + " are incorrect: " + badAttrib);
                                                    } else {
                                                        at.addAction(action);
                                                    }*/
                                                } else if (aa.getType() == AvatarType.BOOLEAN) {
                                                    TraceManager.addDev("bool expr");
                                                    int ex = AvatarSyntaxChecker.isAValidBoolExpr(getAvatarSpecification(), this, expr);

                                                    if ((ex != 0)&& (!forceIfIncorrectExpression)) {
                                                        TraceManager.addDev("The  action " + action + " is incorrect");
                                                        errors.add("The  action " + action + " is incorrect");
                                                    } else {
                                                        at.addAction(action);
                                                    }
                                                    /*AvatarIBSExpressions.BExpr bExpr = AvatarIBSolver.parseBool(this, expr);
                                                    if (bExpr == null) {
                                                        HashSet<String> hs = AvatarIBSolver.getBadIdents();
                                                        String badAttrib = "";
                                                        for (String s : hs) {
                                                            badAttrib += s + " ";
                                                        }
                                                        errors.add("The following elements of the bool expr " + action + " are incorrect: " + badAttrib);
                                                    } else {
                                                        at.addAction(action);
                                                    }*/
                                                }

                                            }


                                        }
                                        // signal sending / receiving
                                        else if (isASignalAction(action.trim())) {
                                            TraceManager.addDev("Handing communication action: " + action);

                                            int index = action.indexOf("(");
                                            String sigName = action.substring(0, index);
                                            AvatarSignal as = getAvatarSignalWithName(sigName);

                                            if (as == null) {
                                                errors.add("No signal named \"" + sigName + "\" in block \"" + getName() + "\"" );
                                            } else {
                                                AvatarActionOnSignal aaos = new AvatarActionOnSignal(
                                                        sigName + "_aaos", as, null, asm.getOwner());
                                                asm.addElement(aaos);
                                                AvatarTransition atBis =
                                                        new AvatarTransition(this, "name" + "_from_" + sigName, getReferenceObject());
                                                asm.addElement(atBis);

                                                AvatarStateMachineElement asme = at.getNext(0);
                                                at.removeAllNexts();
                                                at.addNext(aaos);
                                                aaos.addNext(atBis);
                                                atBis.addNext(asme);
                                                at = atBis;

                                            }


                                        } else {
                                            TraceManager.addDev("Other action:" + action);
                                            if (forceIfIncorrectExpression) {
                                                at.addAction(action);
                                            } else {
                                                TraceManager.addDev("The following action is not valid: " + action + ". It must contain either the affectation of a " +
                                                        "variable or a signal send/receive");
                                                errors.add("The following action is not valid: " + action + ". It must contain either the affectation of a " +
                                                        "variable or a signal send/receive");
                                            }
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }


                        }
                    }
                }

            }
        } catch (org.json.JSONException e) {
            errors.add("Invalid JSON: " + e.getMessage());
        }

        TraceManager.addDev("******************** State Machine of block: " + getName() + ":" + getStateMachine().toString());


        return errors;
    }

    public static boolean isASignalAction(String s) {
        int index = s.indexOf('(');
        if (index == -1) {
            return false;
        }

        String tmp = s.substring(0, index);

        return tmp.trim().matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    public ArrayList<String> addAttributesFromJSON(String _jsonSpec) {
        if (_jsonSpec == null) {
            return null;
        }

        ArrayList<String> errors = new ArrayList<>();
        JSONObject mainObject;

        try {
            mainObject = new JSONObject(_jsonSpec);

            JSONArray statesJSON = mainObject.getJSONArray("attributes");

            for (int i = 0; i < statesJSON.length(); i++) {
                JSONObject state0 = statesJSON.getJSONObject(i);
                String name = AvatarSpecification.removeSpaces(state0.getString("name"));
                String type = AvatarSpecification.removeSpaces(state0.getString("type"));

                if (type == null) {
                    type = "int";
                }

                AvatarType at;
                if (type.compareTo("boolean") == 0) {
                    at = AvatarType.BOOLEAN;
                } else if (type.compareTo("int") == 0){
                    at = AvatarType.INTEGER;
                } else {
                    errors.add("The following type is not valid: " + type + " in attribute " + name);
                    at = AvatarType.INTEGER;
                }

                AvatarAttribute aa = getAvatarAttributeWithName(name);
                if (aa != null){
                    if (aa.getType() != at) {
                        errors.add("Attribute " + name + " already has type: " + aa.getType().getStringType());
                    }
                } else {
                    aa = new AvatarAttribute(name, at, this, this.getReferenceObject());
                    addAttribute(aa);
                }

            }
        }  catch (org.json.JSONException e) {
            errors.add("Invalid JSON: " + e.getMessage());
        }

        return errors;
    }
}
