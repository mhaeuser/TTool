package myutil.intboolsolver;

/** most of the methods of this class are implemented because they
 * are required for instantiation, but not used as no call to
 * attribute features is required to handle closed formula.
 */

public class IBSStdClosedFormulaAttributeClass extends IBSStdAttributeClass<
        IBSParamSpec,
        IBSParamComp,
        IBSParamState,
        IBSParamSpecState,
        IBSParamCompState,
        IBSClosedFormulaAttribute
        > {
    IBSStdClosedFormulaAttributeClass(){}

    public IBSStdClosedFormulaAttribute getNewAttribute() {
        return null;
    }

    public IBSTypedAttribute getTypedAttribute(IBSParamSpec _spec, String _s) {
        return IBSTypedAttribute.NullAttribute;
    }

    public IBSTypedAttribute getTypedAttribute(IBSParamComp _comp, String _s) {
        return IBSTypedAttribute.NullAttribute;
    }
    public IBSTypedAttribute getTypedAttribute(IBSParamComp _comp, IBSParamState _st) {
        return IBSTypedAttribute.NullAttribute;
    }

    public void initBuild(IBSParamSpec _spec){};
    public void initBuild(IBSParamComp _comp){};
    public void initBuild(){}

    public IBSTypedAttribute findAttribute(IBSParamSpec _spec, String _s){ return null; }
    public void addAttribute(IBSParamSpec _spec, String _s, IBSTypedAttribute _att){}
    public IBSTypedAttribute findAttribute(IBSParamComp _comp, String _s){return null;}
    public void addAttribute(IBSParamComp _comp, String _s, IBSTypedAttribute _att){}
    public IBSTypedAttribute findAttribute(IBSParamComp _comp, IBSParamState _state){return null;}
    public void addAttribute(IBSParamComp _comp, IBSParamState _state, IBSTypedAttribute _att){}
    public void clearAttributes(){}
}
