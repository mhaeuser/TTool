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
 * Class IBSStdAttributeClass (should be abstract), partially
 * implementing {@link myutil.intboolsolver.IBSAttributeClass
 * IBSAttributeClass} for systems with states as boolean leaves.
 * Creation: 07/03/2023
 *
 * <p> This class (together with
 * {@link myutil.intboolsolver.IBSStdAttribute)
 * IBSStdAttribute}) is a step toward an instantiation
 * of the solver for systems with states as boolean leaves.</p>
 * The (very) partial implementation provided here is  sometime
 * provided as code in comments that must be copied in final
 * instantiation (when genericity disappear).</p>
 * <p> To instantiate the solver using this approach, a fully
 * implemented extension of this class must be provided.
 * Comments in the file say what remains to implement
 * (in the futur, abstraction, for automatic checking...).</p>
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 */

public class IBSStdAttributeClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState,
        Att extends IBSAttribute<Spec,Comp,State,SpecState,CompState>
        > extends IBSAttributeClass <
        Spec ,
        Comp ,
        State ,
        SpecState ,
        CompState,
        Att
        > {
    // to implement (cf comment below...
    // public static IBSTypedAttribute getTypedAttribute(Spec _spec, String _s);
    // public static IBSTypedAttribute getTypedAttribute(Comp _comp, String _s);
    // public static IBSTypedAttribute getTypedAttribute(Comp _comp, State _st);

    /**
     * Methods getTypedAttribute must be overrided copying the code above in the
     * subclass and replacing "SubClassName" by the name  of the subclass.
     * Indeed, returning IBSTypedAttribute that contain created instances of the
     * subclass as val is only possible at the subclass level.
     */

    /* for implementating of getTypedAttribute(Spec _spec, String _s), copy the
     * following code in your subclass, replacing "SUBCLASSNAME" by the name of
     * the subclass.
    ----------------------------------------------------------------------------------
    CODE BEGIN
    protected static IBSTypedAttribute getTypedAttribute(Spec _spec, String _s) {
	IBSTypedAttribute a = findAttribute(_spec, _s);
	if (a == null) {
	    SUBCLASSNAME x = new SUBCLASSNAME(); // replace here
	    x.classInitAttribute(_spec,_s);

	    switch (x.type) {
	    case IBSAttributeTypes.NullAttr:{
		    return IBSTypedAttribute.NullAttribute;
	    }
	    case IBSAttributeTypes.BoolConst:
	    case IBSAttributeTypes.IntConst:{
	        a = new IBSTypedAttribute(x.type,(Object) new Int(constantInt));
		    break;
	    }
	    default: {
		a = new IBSTypedAttribute(x.type,(Object) x);
	    }
	    }
	    addAttribute(_spec, _s, a);
	}
	return a;
    }
    CODE END
    ----------------------------------------------------------------------------------
    */

    /* for implementating of getTypedAttribute(Comp _comp, String _s), copy the
     * following code in your subclass, replacing "SUBCLASSNAME" by the name of
     * the subclass.
    ----------------------------------------------------------------------------------
    CODE BEGIN
    protected static IBSTypedAttribute make_getTypedAttribute(Comp _comp, String _s) {
	IBSTypedAttribute a = findAttribute(_comp, _s);
	if (a == null) {
	    SUBCLASSNAME x = new SUBCLASSNAME(); // replace here
	    x.classInitAttribute(_comp,_s);

	    switch (x.type) {
	    case IBSAttributeTypes.NullAttr:{
		    return IBSTypedAttribute.NullAttribute;
	    }
	    case IBSAttributeTypes.BoolConst:
	    case IBSAttributeTypes.IntConst:{
	        a = new IBSTypedAttribute(x.type,(Object) new Int(constantInt));
		    break;
	    }
	    default: {
		a = new IBSTypedAttribute(x.type,(Object) x);
	    }
	    }
	    addAttribute(_comp, _s, a);
	}
	return a;
    }
    CODE END
    ----------------------------------------------------------------------------------
    */

    /* for implementating of getTypedAttribute(Comp _comp, State _state), copy the
     * following code in your subclass, replacing "SUBCLASSNAME" by the name of
     * the subclass.
    ----------------------------------------------------------------------------------
    CODE BEGIN
    protected static IBSTypedAttribute getTypedAttribute(Comp _comp, State _state) {
	IBSTypedAttribute a = findAttribute(_comp, _state);
	if (a == null) {
	    SUBCLASSNAME x = new SUBCLASSNAME(); // replace here
	    x.classInitAttribute(_comp,_state);

	    switch (x.type) {
	    case IBSAttributeTypes.NullAttr:{
		    return IBSTypedAttribute.NullAttribute;
	    }
	    case IBSAttributeTypes.BoolConst:
	    case IBSAttributeTypes.IntConst:{
	        a = new IBSTypedAttribute(x.type,(Object) new Int(constantInt));
		    break;
	    }
	    default: {
		a = new IBSTypedAttribute(x.type,(Object) x);
	    }
	    }
	    addAttribute(_comp, _state, a);
	}
	return a;
    }
    CODE END
    ----------------------------------------------------------------------------------
    */

    // remains to implement
    // public static void initBuild(Spec _spec){};
    // public static void initBuild(Comp _comp){};
    // public static void initBuild(){};}

    // to implement (idea: memory of already searched attributes)
    public IBSTypedAttribute findAttribute(Spec _spec, String _s){ return null; }
    public void addAttribute(Spec _spec, String _s, IBSTypedAttribute _att){}
    public IBSTypedAttribute findAttribute(Comp _comp, String _s){return null;}
    public void addAttribute(Comp _comp, String _s, IBSTypedAttribute _att){}
    public IBSTypedAttribute findAttribute(Comp _comp, State _state){return null;}
    public void addAttribute(Comp _comp, State _state, IBSTypedAttribute _att){}
    //!! probably function to add to solver...
    public void clearAttributes(){}

}