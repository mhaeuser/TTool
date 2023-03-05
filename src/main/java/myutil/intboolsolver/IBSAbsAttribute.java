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

/**
 * Class IBSAbstractAttribute
 * This Class is a documented interface for implementing IBSAttribute.
 * It provides some partial implementation, tools and rules for extention.
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */

public class IBSAbsAttribute<
        Spec extends IBSSpecParam,
        Comp extends IBSCompParam,
        State extends IBSStateParam,
        SpecState extends IBSSpecStateParam,
        CompState extends IBSCompStateParam
        > implements IBSAttribute<
        Spec ,
        Comp ,
        State ,
        SpecState ,
        CompState
        >{

    // partial implementation: Variable and State attributes
    protected State state =null;
    protected String s = null;
    protected boolean isState = false;
    protected int type = IBSAttributeTypes.NullAttr;
    protected int constantInt = 0;
        
    /** Subclasses must not define any constructors but provide initialisation functions 
     *  to be called after the default constructor, which is the only one we use,
     *  following the sequence:
     *  - Subclass Default Constructor;
     *  - IBSAbstractAttribute initialisation function;
     *  - Subclass initialisation function
     *  - IBSAbstractAttribute initialisation post processing.
     *  (implementation at the end of this file)
     *
     *  The subclass initialisation functions can use "s", which is set before. 
     *
     *  They return the type computed for the attribute, among (IBSTypedAttribute.) None,
     *  BoolConst, IntConst, BoolAttr, IntAttr.
     *
     *  If returned type is BoolConst or IntConst, constantInt should contain the associated 
     *  constant value.
     *
     *  Post processing sets field "type" to this returned value and considers IntConst as
     *  up-to-date, just after subclass initialisation functions.
     */

    /** The subclass provided initialisation functions, returning values among 
     *  (IBSTypedAttribute.) None, BoolConst, IntConst, BoolAttr and IntAttr.
     */
    // to implement
    protected int initAttribute(Spec _spec){return IBSAttributeTypes.NullAttr;}
    protected int initAttribute(Comp _comp){return IBSAttributeTypes.NullAttr;}
    /** Here, s, isState and state have already been initialized. 
     *  Returned type should be BoolAttr.
     */
    protected int initStateAttribute(Comp _comp){return IBSAttributeTypes.BoolAttr;}


    // method to override by subclasses, in particular replacing
    // IBSAbstractAttribute by the name of the subclass.
    /*
    public static boolean instanceOfMe(int _type, Object _val) {
	return (_val instanceof IBSAbsAttribute && _type==(IBSAbsAttribute)_val.getType());
    }
    */

    // method to override by subclasses, in particular replacing
    // IBSAbstractAttribute by the name of the subclass.
    /*
    public static boolean instanceOfMe(IBSTypedAttribute _ta) {
        Object v = _ta.getVal();
	return (v instanceof IBSAbsAttribute && _ta.getType()==(IBSAbsAttribute)v.getType());
    }
     */






    // Nothing to do.
    public final int getType(){return type;}
    // Nothing to do.
    public final int getConstant(){return constantInt;}

    // for the solver. Returns solver type (IMMEDIATE_(BOOL,INT,NO)).
    // Nothing to do.
    public final int getAttributeType() {
	    switch (type) {
	    case IBSAttributeTypes.BoolConst:
	    case IBSAttributeTypes.BoolAttr: return IBSolver.IMMEDIATE_BOOL;
	    case IBSAttributeTypes.IntConst:
	    case IBSAttributeTypes.IntAttr: return IBSolver.IMMEDIATE_INT;
	    default: return IBSolver.IMMEDIATE_NO;
	    }
    }

    // to implement.
    public int getValue(SpecState _ss){return 0;}
    public int getValue(CompState sb){return 0;}
    public int getValue(Object _quickstate){return 0;}
    public void setValue(SpecState _ss, int val){}
    public void setValue(CompState _cs, int val){}
    // should not be modified
    // Nothing to do.
    public int getValue(SpecState _ss, State _st) {
        if (isState) {
            return (state == _st) ? 1 : 0;
        }
	return(getValue(_ss));
    }

    // Nothing to do
    public boolean isState() {
        return isState;
    }
    
    // to implement
    // Link component into containing specification
    public void linkComp(Spec _spec){}
    // Link state into containing component
    public void linkState(){}
    
    // Nothing to do
    public String toString() {
        return s;
    }
    /* **************************************************************** */
    /* LOCAL IMPLEMENTATION. Not to modify */    

    /** The enclosing initialisation functions (local, not to be overriden) */
    void classInitAttribute(Spec _spec, String _s) {
        this.s = _s;
        type = initAttribute(_spec);
    }
    void classInitAttribute(Comp _comp, String _s) {
        this.s = _s;
        type = initAttribute(_comp);
    }

    // to implement
    // should be static but must be here, as AttributeClassc is not
    // reacheable at this place (untill all genericity disappear).
    public String getStateName(Comp _comp, State _state) {
        return "";
    }
    //!! for the moment, _comp is useless...
    void classInitAttribute(Comp _comp, State _state) {
        this.s = getStateName(_comp,_state); 
        isState = true;
        state = _state;
        type = initStateAttribute(_comp);
    }
}
