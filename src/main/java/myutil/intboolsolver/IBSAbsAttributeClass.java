package myutil.intboolsolver;

public class IBSAbsAttributeClass<
        Spec extends IBSParams.Spec,
        Comp extends IBSParams.Comp,
        State extends IBSParams.State,
        SpecState extends IBSParams.SpecState,
        CompState extends IBSParams.CompState,
        Att extends IBSAttribute<Spec,Comp,State,SpecState,CompState>
        > extends IBSAttributeClass <
        Spec ,
        Comp ,
        State ,
        SpecState ,
        CompState,
        Att
        > {
}
