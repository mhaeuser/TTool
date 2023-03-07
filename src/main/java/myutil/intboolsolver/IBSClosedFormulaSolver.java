package myutil.intboolsolver;

public class IBSClosedFormulaSolver extends IBSolver <
        IBSSpecParam,
        IBSCompParam,
        IBSStateParam,
        IBSSpecStateParam,
        IBSCompStateParam,
        IBSClosedFormulaAttribute,
        IBSClosedFormulaAttributeClass > {
        IBSClosedFormulaSolver() {
                super(new IBSClosedFormulaAttributeClass());
        }
}