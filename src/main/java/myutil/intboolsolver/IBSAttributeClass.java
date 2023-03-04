package myutil.intboolsolver;

public class IBSAttributeClass<
        Spec extends IBSParams.Spec,
        Comp extends IBSParams.Comp,
        State extends IBSParams.State,
        SpecState extends IBSParams.SpecState,
        CompState extends IBSParams.CompState,
        Att extends IBSAttribute<Spec,Comp,State,SpecState,CompState>
        > {
     public IBSTypedAttribute getTypedAttribute(Spec _spec, String _s) {
         return IBSTypedAttribute.NullAttribute;
     }
     public IBSTypedAttribute getTypedAttribute(Comp _comp, String _s){
         return IBSTypedAttribute.NullAttribute;
     }
     public IBSTypedAttribute getTypedAttribute(Comp _comp, State _st){
         return IBSTypedAttribute.NullAttribute;
     }
     public void initBuild(Spec _spec){}
     public void initBuild(Comp _comp){}
     public void initBuild(){}
}
