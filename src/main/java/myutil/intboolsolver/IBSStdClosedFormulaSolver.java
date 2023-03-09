package myutil.intboolsolver;

public class IBSStdClosedFormulaSolver extends IBSolver <
        IBSParamSpec,
        IBSParamComp,
        IBSParamState,
        IBSParamSpecState,
        IBSParamCompState,
        IBSClosedFormulaAttribute,
        IBSClosedFormulaAttributeClass > {
        IBSStdClosedFormulaSolver() {
                super(new IBSClosedFormulaAttributeClass());
        }
}