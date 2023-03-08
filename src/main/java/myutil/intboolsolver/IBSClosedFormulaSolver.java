package myutil.intboolsolver;

public class IBSClosedFormulaSolver extends IBSolver <
        IBSParamSpec,
        IBSParamComp,
        IBSParamState,
        IBSParamSpecState,
        IBSParamCompState,
        IBSClosedFormulaAttribute,
        IBSClosedFormulaAttributeClass > {
        IBSClosedFormulaSolver() {
                super(new IBSClosedFormulaAttributeClass());
        }
}