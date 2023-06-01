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
 * This class is a partial implementation of {@link IBSAttributes
 * IBSAttributes} for systems that not only handle usual variables
 * at leaves but also (state machine's) states as boolean variables.
 * Creation: 04/11/2023
 *
 * <p> To instantiate the solver using this approach, a fully
 * implemented extension of this class must be provided.
 * To make instantiation easier, functions that must be overridden
 * are grouped at the beginning of the code file. The default
 * implementation (without override) that is provided targets
 * closed formulas. </p>
 * <HR>
 * <p> The exported interface allow instances to provides a
 * mechanism that  memorize attributes that have already
 * been build. Using this mechanism avoids creating isomorphic
 * instances of a single attribute.</p>
 *
 * <p> To allow the handling of this "memory", some methods must
 * be implemented, enumerated below.</p>
 *
 * <p> Note that the standard interpretation of getValue w.r.t. a state
 * (i.e IBSStdAttributes.Attribute.getValue(SpecState _ss, State _st))
 * partially induces the interpretation of integers by boolean.</p>
 * @version 1.0 04/11/2023
 * @author Sophie Coudert
 */

public class IBSStdAttributes<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSAttributes<
                                Spec ,
                                Comp ,
                                State ,
                                SpecState ,
                                CompState
                                > {
    // TO OVERRIDE, Inherited from IBSAttributes.
    // public void initBuild(Spec _spec);
    // public void initBuild(Comp _comp);
    // public void initBuild();

    /**
     * Find a memorized typed attribute or returns null.
     *
     * <p> Tf the typed attribute corresponding to _s has been memorized,
     * it is returned. Otherwise null is returned </p>
     * @param _spec the specification that associates structures to variables
     * @param _s the string that identifies the searched attribute.
     * @return the found or created typed attribute
     */
    /* TO OVERRIDE */
    public IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute findAttribute(Spec _spec, String _s) { return null; }

    /**
     * Add an attribute to the memorized ones.
     * @param _spec the specification that associates structures to variables
     * @param _s the string that identifies (in _spec) the attribute to add
     * @param _att the attribute to add
     */
    /* TO OVERRIDE */
    public void addAttribute(Spec _spec, String _s, IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute _att) {}
     /**
      * Find a memorized typed attribute or returns null.
     *
     * <p> Tf the attribute corresponding to _s has been memorized,
     * it is returned. Otherwise null is returned </p>
     * @param _comp the component that associates structures to variables
     * @param _s the string that identifies the searched attribute.
     * @return the found or created typed attribute
     */
     /* TO OVERRIDE */
   public IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute findAttribute(Comp _comp, String _s) { return null; }
    /**
     * Add an attribute to the memorized ones.
     * @param _comp the component that associates structures to variables
     * @param _s the string that identifies (in _comp) the attribute to add
     * @param _att the attribute to add
     */
    /* TO OVERRIDE */
    public void addAttribute(Comp _comp, String _s, IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute _att) {}
    /**
     * Find a memorized attribute or returns null.
     *
     * <p> Tf the attribute corresponding to _s has been memorized,
     * it is returned. Otherwise null is returned </p>
     * @param _comp the component that associates structures to variables
     * @param _state the string that identifies the searched attribute.
     * @return  the found or created typed attribute.
     */
    /* TO OVERRIDE */
    public IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute findAttribute(Comp _comp, State _state) { return null; }
    /**
     * Add an attribute to the memorized ones.
     * @param _comp the component that associates structures to variables
     * @param _state the state that identifies (in _comp) the attribute to add
     * @param _att the attribute to add
     */
    /* TO OVERRIDE */
    public void addAttribute(Comp _comp, State _state, IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute _att) {}

    /**
     * Clear the memorized attributes.
     */
    /* TO OVERRIDE */
    public void clearAttributes() {}
    /**
     * get the name associated to a state by the instance.
     *
     * @param _comp the component that associates structures to attribute
     *              strings
     * @param _state the state of which we want the name
     * @return the name associated to _state by _comp
     */
    /* TO OVERRIDE */
    public String getStateName(Comp _comp, State _state) { return ""; }

    /**
     * Call to default Attribute constructor (wrapper).
     *
     * <p> Overriding this method in subclasses (with exactly the same code)
     * allows to obtain instances of the subclasses when getTypedAttribute
     * is called from these subclasses.</p>
     * @return a new fresh instance of Attribute (call default constructor)
     */
    /* TO OVERRIDE in order to obtain instance of the subclasses */
    protected Attribute getAttribute() { return this.new Attribute();}


    /* DO NOT OVERRIDE THE THREE FOLLOWING "getTypedAttribute". They are fully implemented and do not need any modifying.
     */
    public final IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(Spec _spec, String _s) {
        IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute a = findAttribute(_spec, _s);
        if (a == null) {
            IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.Attribute x = getAttribute();
            x.classInitAttribute(_spec,_s);
            switch (x.getType()) {
                case IBSAttributes.NullAttr: {
                    return this.NullTypedAttribute;
                }
                case IBSAttributes.BoolConst: {
                    a = new TypedAttribute(x.getConstant(),true);
                    break;
                }

                case IBSAttributes.IntConst: {
                    a = new TypedAttribute(x.getConstant(),false);
                    break;
                }
                case IBSAttributes.BoolAttr: {
                    a = new TypedAttribute(x, true);
                    break;
                }
                case IBSAttributes.IntAttr: {
                    a = new TypedAttribute(x, false);
                }
            }
            addAttribute(_spec, _s, a);
        }
        return a;
    }
    public final IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(Comp _comp, String _s) {
        IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute a = findAttribute(_comp, _s);
        if (a == null) {
            IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.Attribute x = getAttribute();
            x.classInitAttribute(_comp,_s);
            switch (x.getType()) {
                case IBSAttributes.NullAttr: {
                    return this.NullTypedAttribute;
                }
                case IBSAttributes.BoolConst: {
                    a = new TypedAttribute(x.getConstant(),true);
                    break;
                }

                case IBSAttributes.IntConst: {
                    a = new TypedAttribute(x.getConstant(),false);
                    break;
                }
                case IBSAttributes.BoolAttr: {
                    a = new TypedAttribute(x, true);
                    break;
                }
                case IBSAttributes.IntAttr: {
                    a = new TypedAttribute(x, false);
                }
            }
            addAttribute(_comp, _s, a);
        }
        return a;
    }
    public final IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(Comp _comp, State _state) {
        IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.TypedAttribute a = findAttribute(_comp, _state);
        if (a == null) {
            IBSStdAttributes<Spec,Comp,State,SpecState,CompState>.Attribute x = getAttribute();
            x.classInitAttribute(_comp,_state);
            switch (x.getType()) {
                case IBSAttributes.NullAttr: {
                    return this.NullTypedAttribute;
                }
                case IBSAttributes.BoolConst: {
                    a = new TypedAttribute(x.getConstant(),true);
                    break;
                }
                case IBSAttributes.IntConst: {
                    a = new TypedAttribute(x.getConstant(),false);
                    break;
                }
                case IBSAttributes.BoolAttr: {
                    a = new TypedAttribute(x, true);
                    break;
                }
                case IBSAttributes.IntAttr: {
                    a = new TypedAttribute(x, false);
                }
            }
            addAttribute(_comp, _state, a);
        }
        return a;
    }

    /**
     * Partial implementation of {@link
     * IBSAttributes.Attribute
     * IBSAttributes.IBSAttribute} for systems with usual
     * integer/boolean variables and states as boolean variables.
     *
     * <p> To make instantiation easier, functions that must be overridden
     * are grouped at the beginning of the class code and commented.
     * Instantiation for closed formulas do not require any overriding.</p>
     * <HR>
     * <p> Subclasses must not export any constructors except the  default one
     * which is always called through {@link #getAttribute() IBSStdAttributes.getAttribute}.
     * They also provide initialisation functions to be called after
     * {@link #getAttribute()   IBSStdAttributes.getAttribute}.</p>
     * <p> More technically, the implemented creation process of attributes is the following one:</p>
     * <ul>
     *     <li> IBSStdAttributes.Attribute's Subclass Default Constructor
     *     (called by {@link #getAttribute() public
     *     Attribute's SubClass getAttribute()} provided by IBSStdAttributes's subclass)
     *     </li>
     *     <li> IBSStdAttributes common initialisation function
     *     (not to override, implementation provided <a href="#common_init">here</a>)
     *     </li>
     *     <li> Subclass specific initialisation function (subclass provides
     *     {@link #initAttribute(IBSParamSpec)  int initAttribute(...)}, to override)
     *     </li>
     *     <li> IBSStdtAttribute initialisation postprocessing (not to override,
     *     implementation provided <a href="#common_init">here</a>)
     *     </li>
     * </ul>
     * <hr>
     * methods inherited from {@link IBSAttributes.Attribute
     * IBSAttributes.Attribute} that <b>must be overridden</b>:
     * <PRE>
     *         public int getValue(SpecState _ss);
     *         public  int getValue(CompState sb);
     *         public int getValue(Object _qs);
     *         public void setValue(SpecState _ss, int val);
     *         public void setValue(CompState _cs, int val);
     *         public void linkComp(Spec _spec){}
     *         public void linkState(){}
     * </PRE>
     *
     * @version 0.1 07/03/2023
     * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
     */

    public class Attribute extends IBSAttributes<Spec,Comp,State,SpecState,CompState>.Attribute {

        // partial implementation: Variable and State attributes
        /** Associated state. Relevant for boolean variables interpreted by a state.
         * Set by getTypedAttribute */
        public State state =null;
        /** Attribute's name */
        protected String s = null;
        /** True if the attribute is a boolean variables interpreted by a state.
         * Set by getTypedAttribute */
        protected boolean isState = false;
        /** Among NullAttr, BoolAttr, IntAttr, BoolConst, IntConst.
         * Set by getTypedAttribute */
        protected int type = IBSAttributes.NullAttr;
        /** Constant value of the attribute. Relevant if type is BoolConst or IntConst.
         * Set by getTypedAttribute */
        protected int constantInt = 0;

        // TO OVERRIDE, Inherited from IBSAttributes.
        // public int getValue(SpecState _ss);
        // public  int getValue(CompState sb);
        // public int getValue(Object _qs);
        // public void setValue(SpecState _ss, int val);
        // public void setValue(CompState _cs, int val);
        // public void linkComp(Spec _spec){}
        // public void linkState(){}

        /** Subclass specific initialisation functions for attributes.
         *
         * <p> Part of attribute initialisation that is specific to instantiation.
         * Member "s" (attribute string), can be used as it has been set by
         * IBSStdAttributes common initialisation, which has been called
         * just before.</p>
         *
         * <p> The returned value is among {@link IBSStdAttributes
         * IBSStdAttributes} attribute types (NullAttr, BoolConst,
         * IntConst, BoolAttr and IntAttr,) according to the type of attribute that
         * has been identified.</p>
         *
         *  <p> If the returned type is BoolConst or IntConst, constantInt must contain
         *  the associated constant value when returning (expected by postprocessing)</p>
         * @param _spec the specification that associates structures to attribute
         *              strings
         * @return the type of the identified attribute, among  (IBSTypedAttribute.)
         * NullAttr, BoolConst, IntConst, BoolAttr and IntAttr. (IntConst set if relevant)
         */
        // to implement
        /* TO OVERRIDE */
        protected int initAttribute(Spec _spec) { return IBSAttributes.NullAttr; }
        /** Subclass specific initialisation functions for attributes.
         *
         * <p> Part of attribute initialisation that is specific to instantiation.
         * Member "s" (attribute string), can be used as it has been set by
         * IBSStdAttributes common initialisation, which has been called
         * just before.</p>
         *
         * <p> The returned value is among {@link IBSStdAttributes
         * IBSStdAttributes} attribute types (NullAttr, BoolConst,
         * IntConst, BoolAttr and IntAttr) according to the type of attribute that
         * has been identified.</p>
         *
         *  <p> If the returned type is BoolConst or IntConst, constantInt must contain
         *  the associated constant value when returning (expected by postprocessing)</p>
         * @param _comp the component that associates structures to attribute
         *              strings
         * @return the type of the identified attribute, among  (IBSTypedAttribute.)
         * NullAttr, BoolConst, IntConst, BoolAttr and IntAttr. (IntConst set if relevant)
         */
        /* TO OVERRIDE */
        protected int initAttribute(Comp _comp) { return IBSAttributes.NullAttr; }
        /** Subclass specific initialisation functions for attributes
         *
         * <p> Part of attribute initialisation that is specific to instantiation.
         * Members s, isState and state have already been set by
         * IBSStdAttributes common initialisation, which has been called
         * just before.</p>
         * <p> Returned type should be BoolAttr, or NullAttr if the attribute is not
         * a state attribute (error case).</p>
         * @param _comp the component that associates structures to attribute
         * @return the type of the identified attribute, i.e. BoolAttr or NullAttr
         */
        /* TO OVERRIDE */
        protected int initStateAttribute(Comp _comp) { return IBSAttributes.NullAttr; }


        // $$$$$$$$$$$$$$$ provided implementation, should not be modified $$$$$$$$$$$$$$

        /** Test if the attribute is a boolean variables interpreted by a state.
         * @return true iff the attribute is interpreted by a state.
         */
        /* DO NOT OVERRIDE */
        public boolean isState() { return isState; }
        /**
         * Get the type of the attribute.
         * @return a type among NullAttr, BoolConst, IntConst,
         * BoolAttr and IntAttr. <b>do not override</b>
         */
        /* DO NOT OVERRIDE */
        public final int getType(){ return type; }
        // Nothing to do.
        /**
         * Get the constant value of the attribute.
         * Relevant only if the attribute is a constant.
         * @return the constant value of the attribute.
         */
        /* DO NOT OVERRIDE */
        public final int getConstant(){ return constantInt; }

        /** Standard interpretation of getValue w.r.t. a state.
         * If the variable is not a state, returns {@code getValue(_ss)}.
         * Otherwise, this state is compared to _st and 1 is returned
         * in case of an equality (otherwise 0 is returned).
         *
         * @param _ss specification state to use if the variable is not a state
         * @param _st
         * @return the value
         */
        public int getValue(SpecState _ss, State _st) {
            if (isState) {
                return (state == _st) ? 1 : 0;
            }
            return(getValue(_ss));
        }
        // public boolean isState() { return isState; }
        /* DO NOT OVERRIDE */
        public String toString() {  return s; }
        /** <a id="common_init"></a>Attribute initialisation that is common to all instances
         * @param _spec the specification that associates structures to attribute
         *              strings
         * @param _s the string that identifies the attribute
         */
        /* DO NOT OVERRIDE */
        public final void classInitAttribute(Spec _spec, String _s) {
            this.s = _s;
            type = initAttribute(_spec);
        }
        /** Attribute initialisation that is common to all instances
         * @param _comp the component that associates structures to attribute
         *              strings
         * @param _s the string that identifies the attribute
         */
        /* DO NOT OVERRIDE */
        public final void classInitAttribute(Comp _comp, String _s) {
            this.s = _s;
            type = initAttribute(_comp);
        }

        //!! for the moment, _comp is useless... Reviser...
        /** Attribute initialisation that is common to all instances
         * @param _comp the component that associates structures to attribute
         *              strings
         * @param _state the state that identifies the state attribute
         */
        /* DO NOT OVERRIDE */
        public final void classInitAttribute(Comp _comp, State _state) {
            this.s = getStateName(_comp,_state);
            isState = true;
            state = _state;
            type = initStateAttribute(_comp);
        }
    }

}