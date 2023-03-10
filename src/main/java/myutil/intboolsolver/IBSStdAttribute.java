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
package myutil.intboolsolver;
// import intboolsolver.IBSolver; // usefull in implementations

/**
 * Abstract Class IBSStdAttribute partially implementing {@link
 * myutil.intboolsolver.IBSAttribute IBSAttribute} for systems with
 * states as boolean leaves.
 * Creation: 07/03/2023
 *
 * <p> This class (together with
 * {@link myutil.intboolsolver.IBSStdAttributeClass
 * IBSStdAttributeClass}) is a (little) step toward an instantiation
 * of the solver for systems with states as boolean leaves.</p>
 * <p> To instantiate the solver using this approach, a fully
 * implemented extension of this class must be provided.
 * To make instantiation easier, functions that remain abstract are
 * grouped at the beginning of the code file and commented</p>
 * <HR>
 * <p>Subclasses must not export any constructors but provide access to
 * their default one through {@code public SubClass getNew()}). They also
 * provide initialisation functions to be called after the default
 * constructor.</p>
 * <p> More technically, the creation process of attributes is the following one:</p>
 * <ul>
 *     <li> Subclass Default Constructor (subclass provides {@code public
 *     SubClass getNew()})
 *     </li>
 *     <li> IBSStdAttribute initialisation function (implemented here)
 *     </li>
 *     <li> Subclass specific initialisation function (subclass provides
 *     {@code int initAttribute(...)})
 *     </li>
 *     <li> IBSStdtAttribute initialisation postprocessing (implemented here)
 *     </li>
 * </ul>
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 */

public abstract class IBSStdAttribute<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > implements IBSAttribute<
        Spec ,
        Comp ,
        State ,
        SpecState ,
        CompState
        >{

    // partial implementation: Variable and State attributes
    public State state =null;
    protected String s = null;
    protected boolean isState = false;
    protected int type = IBSAttributeTypes.NullAttr;
    protected int constantInt = 0;

    /** Subclass specific initialisation functions for attributes.
     *
     * <p> Part of attribute initialisation that is specific to instantiation.
     * Member "s" (attribute string), can be used as it has been set by
     * IBSStdAttribute  initialisation, which has been called just before.</p>
     *
     * <p> The returned value is among  (IBSTypedAttribute.) None, BoolConst,
     * IntConst, BoolAttr and IntAttr, according to the type of attribute that
     * has been identified.</p>
     *
     *  <p> If the returned type is BoolConst or IntConst, constantInt must contain
     *  associated constant value when returning (expected by postprocessing)</p>
     * @param _spec the specification that associates structures to attribute
     *              strings
     * @return the type of the identified attribute, among  (IBSTypedAttribute.)
     * None, BoolConst, IntConst, BoolAttr and IntAttr. (IntConst set if relevant)
     */
    // to implement
    protected abstract int initAttribute(Spec _spec);
    /** Subclass specific initialisation functions for attributes.
     *
     * <p> Part of attribute initialisation that is specific to instantiation.
     * Member "s" (attribute string), can be used as it has been set by
     * IBSStdAttribute  initialisation, which has been called just before.</p>
     *
     * <p> The returned value is among  (IBSTypedAttribute.) None, BoolConst,
     * IntConst, BoolAttr and IntAttr, according to the type of attribute that
     * has been identified.</p>
     *
     *  <p> If the returned type is BoolConst or IntConst, constantInt must contain
     *  associated constant value when returning (expected by postprocessing)</p>
     * @param _comp the component that associates structures to attribute
     *              strings
     * @return the type of the identified attribute, among  (IBSTypedAttribute.)
     * None, BoolConst, IntConst, BoolAttr and IntAttr. (IntConst set if relevant)
     */
    protected abstract int initAttribute(Comp _comp);
    /** Subclass specific initialisation functions for attributes.
     *
     * <p> Part of attribute initialisation that is specific to instantiation.
     * Members s, isState and state have already been set by IBSStdAttribute
     * initialisation, which has been called just before.</p>
     * <p> Returned type should be BoolAttr, or None if the attribute is not
     * a state attribute (error case).</p>
     */
    protected abstract int initStateAttribute(Comp _comp);

    /**
     * get the name associated to a state by the instance.
     *
     * <p> Note: although this method seems to be in the scope of the static
     * part of attributes (IBSAttributeClass), it must be here because
     * it is used here, where AttributeClass cannot be made visible
     * (genericity limits).</p>
     * @param _comp the component that associates structures to attribute
     *              strings
     * @param _state the state of which we want the name
     * @return the name associated to _state by _comp
     */
    public abstract String getStateName(Comp _comp, State _state);

    // Remaining methods to implement are documented in IBSAttribute.
    public abstract int getValue(SpecState _ss);
    public abstract int getValue(CompState sb);
    public abstract int getValue(Object _quickstate);
    public abstract void setValue(SpecState _ss, int val);
    public abstract void setValue(CompState _cs, int val);

    public abstract void linkComp(Spec _spec);
    public abstract void linkState();


    // $$$$$$$$$$$$$$$ implementation, should not be modified $$$$$$$$$$$$$$
    public final int getType(){return type;}
    // Nothing to do.
    public final int getConstant(){return constantInt;}
    public final int getAttributeType() {
	    switch (type) {
	    case IBSAttributeTypes.BoolConst:
	    case IBSAttributeTypes.BoolAttr: return IBSolver.IMMEDIATE_BOOL;
	    case IBSAttributeTypes.IntConst:
	    case IBSAttributeTypes.IntAttr: return IBSolver.IMMEDIATE_INT;
	    default: return IBSolver.IMMEDIATE_NO;
	    }
    }
    public int getValue(SpecState _ss, State _st) {
        if (isState) {
            return (state == _st) ? 1 : 0;
        }
	    return(getValue(_ss));
    }
    public boolean isState() {
        return isState;
    }
    public String toString() {
        return s;
    }
    public final void classInitAttribute(Spec _spec, String _s) {
        this.s = _s;
        type = initAttribute(_spec);
    }
    public final void classInitAttribute(Comp _comp, String _s) {
        this.s = _s;
        type = initAttribute(_comp);
    }

    //!! for the moment, _comp is useless... Reviser...
    public final void classInitAttribute(Comp _comp, State _state) {
        this.s = getStateName(_comp,_state); 
        isState = true;
        state = _state;
        type = initStateAttribute(_comp);
    }
}
