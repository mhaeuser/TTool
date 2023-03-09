package myutil.intboolsolver;

public class IBSStdClosedFormulaAttribute extends IBSStdAttribute<
        IBSParamSpec,
        IBSParamComp,
        IBSParamState,
        IBSParamSpecState,
        IBSParamCompState
        > {
    IBSStdClosedFormulaAttribute(){}
    protected int initAttribute(IBSParamSpec _spec){return IBSAttributeTypes.NullAttr;}
    protected int initAttribute(IBSParamComp _comp){return IBSAttributeTypes.NullAttr;}
    /** Here, s, isState and state have already been initialized.
     *  Returned type should be BoolAttr.
     */
    protected int initStateAttribute(IBSParamComp _comp){return IBSAttributeTypes.BoolAttr;}
    public int getValue(IBSParamSpecState _ss){return 0;}
    public int getValue(IBSParamCompState sb){return 0;}
    public int getValue(Object _quickstate){return 0;}
    public void setValue(IBSParamSpecState _ss, int val){}
    public void setValue(IBSParamCompState _cs, int val){}
    public void linkComp(IBSParamSpec _spec){}
    public void linkState(){}
    public String getStateName(IBSParamComp _comp, IBSParamState _state) {
        return "";
    }
}
