package myutil.intboolsolver;
import java.util.HashSet;

public interface IBSScanner< Spec extends IBSParamSpec, Comp extends IBSParamComp, State extends IBSParamState, SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState > extends java_cup.runtime.Scanner {
    public void setAttributeClass( IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _attrC);
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass();
    public void setExpressionClass( IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _exprC);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass();
    public HashSet<String> getBadIdents();
    public void clearBadIdents();
    public void init(String _s) throws java.io.IOException;
    public void init(Spec _spec, String _s) throws java.io.IOException;
    public void init(Comp _comp, String _s) throws java.io.IOException;
}
