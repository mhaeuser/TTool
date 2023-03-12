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
import static myutil.intboolsolver.IBSolver.IMMEDIATE_NO;

/**
 * class IBSStdClosedFormulaAttributeClass is a complete implementation
 * and instantiation of {@link myutil.intboolsolver.IBSStdAttributeClass
 * IBSStdAttributeClass} for closed Formulas.
 *
 * <p> It is provided for documentation together with
 * {@link myutil.intboolsolver.IBSStdClosedFormulaAttribute
 * IBSStdClosedFormulaAttribute} and
 * {@link myutil.intboolsolver.IBSStdClosedFormulaSolver
 * IBSStdClosedFormulaSolver}}</p>
 *
 * <p>These three
 * classes provides the same features as
 * {@link myutil.intboolsolver.IBSClosedFormulaAttribute
 * IBSClosedFormulaAttribute},
 * {@link myutil.intboolsolver.IBSClosedFormulaAttributeClass
 * IBSClosedFormulaAttributeClass} and
 * {@link myutil.intboolsolver.IBSClosedFormulaSolver
 * IBSClosedFormulaSolver} (together).</p>
 *
 * Creation: 07/03/2023
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert
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
