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

package myutil.intboolsolver2;

/**
 * Abstract Class IBSStdAttributeClass, partially
 * implementing {@link IBSAttributeClass
 * IBSAttributeClass} for systems with states as boolean leaves.
 * Creation: 07/03/2023
 *
 * <p> This class (together with
 * {@link IBSStdAttributeClass
 * IBSStdAttribute}) is a step toward an instantiation
 * of the solver for systems with states as boolean leaves.</p>
 * <p> To instantiate the solver using this approach, a fully
 * implemented extension of this class must be provided.
 * To make instantiation easier, functions that remain abstract are
 * grouped at the beginning of the code file and commented</p>
 * <HR>
 * <p> The proposed implementation allow instances to provides a
 * mechanism that  memorize attributes that have already
 * been build. Using this mechanism avoids creating isomorphic
 * instances of a single attribute.</p>
 *
 * <p> To allow the handling of this "memory", some methods must
 * be implemented, enumerated below. Associated comments below
 * are guided by this typical implementation semantics.</p>
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 */

public class IBSStdAttributeClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSAttributeClass<
                        Spec ,
                        Comp ,
                        State ,
                        SpecState ,
                        CompState
                        > {
    // TO OVERRIDE, Inherited from IBSAttributeClass.
    // public void initBuild(Spec _spec);
    // public void initBuild(Comp _comp);
    // public void initBuild();

    /**
     * Find a memorized typed attribute or returns null (<b> to override </b>).
     *
     * <p> Tf the attribute corresponding to _s has been memorized,
     * it is returned. Otherwise null is returned </p>
     * @param _spec the specification that associates structures to open
     *             leaves
     * @param _s the string that identifies the searched attribute.
     * @return the found or created typed attribute
     */
    public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute findAttribute(Spec _spec, String _s) { return null; }

    /**
     * Add an attribute to the memorized ones (<b> to override </b>).
     * @param _spec the specification that associates structures to open
     *             leaves
     * @param _s the string that identifies (in _spec) the attribute to add
     * @param _att the attribute to add
     */
    public void addAttribute(Spec _spec, String _s, IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute _att) {}
     /**
      * Find a memorized typed attribute or returns null (<b> to override </b>).
     *
     * <p> Tf the attribute corresponding to _s has been memorized,
     * it is returned. Otherwise null is returned </p>
     * @param _comp the component that associates structures to open
     *       leaves
     * @param _s the string that identifies the searched attribute.
     * @return the found or created typed attribute
     */
   public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute findAttribute(Comp _comp, String _s) { return null; }
    /**
     * Add an attribute to the memorized ones (<b> to override </b>).
     * @param _comp the component that associates structures to open
     *             leaves
     * @param _s the string that identifies (in _comp) the attribute to add
     * @param _att the attribute to add
     */
    public void addAttribute(Comp _comp, String _s, IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute _att) {}
    /**
     * Find a memorized attribute or returns null (<b> to override </b>).
     *
     * <p> Tf the attribute corresponding to _s has been memorized,
     * it is returned. Otherwise null is returned </p>
     * @param _comp the component that associates structures to open
     *       leaves
     * @param _state the string that identifies the searched attribute.
     * @return  the found or created typed attribute.
     */
    public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute findAttribute(Comp _comp, State _state) { return null; }
    /**
     * Add an attribute to the memorized ones (<b> to override </b>).
     * @param _comp the component that associates structures to open
     *             leaves
     * @param _state the state that identifies (in _comp) the attribute to add
     * @param _att the attribute to add
     */
    public void addAttribute(Comp _comp, State _state, IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute _att) {}

    /**
     * Clear the memorized attributes (<b> to override </b>).
     */
    public void clearAttributes() {}
    /**
     * get the name associated to a state by the instance (<b> to override </b>).
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
    public String getStateName(Comp _comp, State _state) { return ""; }

    /**
     * Call to default Attribute constructor to override in subclasses.
     *
     * <p> Overriding this method in subclasses (with exactly the same code)
     * allows to obtain instances of the subclasses when getTypedAttribute
     * is called from these subclasses.</p>
     * @return a new fresh instance of Attribute (call default constructor)
     */
    protected Attribute getAttribute() { return this.new Attribute();}
    // The three following getTypesAttribute methods are fully implemented and do not need to be overriden.
    public final IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(Spec _spec, String _s) {
        IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute a = findAttribute(_spec, _s);
        if (a == null) {
            IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute x = getAttribute();
            x.classInitAttribute(_spec,_s);
            switch (x.getType()) {
                case IBSAttributeClass.NullAttr: {
                    return this.NullTypedAttribute;
                }
                case IBSAttributeClass.BoolConst: {
                    a = new TypedAttribute(x.getConstant(),true);
                    break;
                }

                case IBSAttributeClass.IntConst: {
                    a = new TypedAttribute(x.getConstant(),false);
                    break;
                }
                case IBSAttributeClass.BoolAttr: {
                    a = new TypedAttribute(x, true);
                    break;
                }
                case IBSAttributeClass.IntAttr: {
                    a = new TypedAttribute(x, false);
                }
            }
            addAttribute(_spec, _s, a);
        }
        return a;
    }
    public final IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(Comp _comp, String _s) {
        IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute a = findAttribute(_comp, _s);
        if (a == null) {
            IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute x = getAttribute();
            x.classInitAttribute(_comp,_s);
            switch (x.getType()) {
                case IBSAttributeClass.NullAttr: {
                    return this.NullTypedAttribute;
                }
                case IBSAttributeClass.BoolConst: {
                    a = new TypedAttribute(x.getConstant(),true);
                    break;
                }

                case IBSAttributeClass.IntConst: {
                    a = new TypedAttribute(x.getConstant(),false);
                    break;
                }
                case IBSAttributeClass.BoolAttr: {
                    a = new TypedAttribute(x, true);
                    break;
                }
                case IBSAttributeClass.IntAttr: {
                    a = new TypedAttribute(x, false);
                }
            }
            addAttribute(_comp, _s, a);
        }
        return a;
    }
    public final IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(Comp _comp, State _state) {
        IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute a = findAttribute(_comp, _state);
        if (a == null) {
            IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute x = getAttribute();
            x.classInitAttribute(_comp,_state);
            switch (x.getType()) {
                case IBSAttributeClass.NullAttr: {
                    return this.NullTypedAttribute;
                }
                case IBSAttributeClass.BoolConst: {
                    a = new TypedAttribute(x.getConstant(),true);
                    break;
                }
                case IBSAttributeClass.IntConst: {
                    a = new TypedAttribute(x.getConstant(),false);
                    break;
                }
                case IBSAttributeClass.BoolAttr: {
                    a = new TypedAttribute(x, true);
                    break;
                }
                case IBSAttributeClass.IntAttr: {
                    a = new TypedAttribute(x, false);
                }
            }
            addAttribute(_comp, _state, a);
        }
        return a;
    }

    /**
     * Abstract Class IBSStdAttribute partially implementing {@link
     * Attribute IBSAttribute} for systems with
     * states as boolean leaves.
     * Creation: 07/03/2023
     *
     * <p> This class (together with
     * {@link IBSStdAttributeClass
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

    public class Attribute extends IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute {

        // partial implementation: Variable and State attributes
        public State state =null;
        protected String s = null;
        protected boolean isState = false;
        protected int type = IBSAttributeClass.NullAttr;
        protected int constantInt = 0;

        // TO OVERRIDE, Inherited from IBSAttributeClass.
        public int getValue(SpecState _ss){
            return 1;
        }
        public  int getValue(CompState sb) { return 1; }
        public int getValue(Object _qs){ return 1; }
        public void setValue(SpecState _ss, int val){}
        public void setValue(CompState _cs, int val){}

        public void linkComp(Spec _spec){}
        public void linkState(){}

        /** Subclass specific initialisation functions for attributes (<b> To override </b>).
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
        protected int initAttribute(Spec _spec) { return IBSAttributeClass.NullAttr; }
        /** Subclass specific initialisation functions for attributes (<b> To override </b>).
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
        protected int initAttribute(Comp _comp) { return IBSAttributeClass.NullAttr; }
        /** Subclass specific initialisation functions for attributes (<b> To override </b>).
         *
         * <p> Part of attribute initialisation that is specific to instantiation.
         * Members s, isState and state have already been set by IBSStdAttribute
         * initialisation, which has been called just before.</p>
         * <p> Returned type should be BoolAttr, or None if the attribute is not
         * a state attribute (error case).</p>
         */
        protected int initStateAttribute(Comp _comp) { return IBSAttributeClass.NullAttr; }


        // $$$$$$$$$$$$$$$ provided implementation, should not be modified $$$$$$$$$$$$$$
        public final int getType(){ return type; }
        // Nothing to do.
        public final int getConstant(){ return constantInt; }
        public int getValue(SpecState _ss, State _st) {
            if (isState) {
                return (state == _st) ? 1 : 0;
            }
            return(getValue(_ss));
        }
        // public boolean isState() { return isState; }
        public String toString() {  return s; }
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

}