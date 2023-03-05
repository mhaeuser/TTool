package myutil.intboolsolver;

public class IBSAbsAttributeClass<
        Spec extends IBSSpecParam,
        Comp extends IBSCompParam,
        State extends IBSStateParam,
        SpecState extends IBSSpecStateParam,
        CompState extends IBSCompStateParam,
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