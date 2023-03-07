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

import java.util.HashSet;

/**
 * Class IBSolverInterface
 * Integer/boolean Expression Solver features. For documentation, not for use...
 * Creation: /03_2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */

public abstract class IBSolverInterface<
        Spec extends IBSSpecParam,
        Comp extends IBSCompParam,
        State extends IBSStateParam,
        SpecState extends IBSSpecStateParam,
        CompState extends IBSCompStateParam,
        ATT extends IBSAttribute<Spec,Comp,State,SpecState,CompState>,
        AtC extends IBSAttributeClass<Spec,Comp,State,SpecState,CompState,ATT>
        > { 
    //   public abstract IBSolverInterface(AtC _c);
    //   IBSolverInterface();
    //    protected void setAttributeClass(AtC _c);
    public abstract HashSet<String> getFreeIdents();
    public abstract void clearFreeIdents();

    public abstract class Expr {
        //public abstract Expr();

        //public abstract  Expr(String expression) {}

        public abstract void setExpression(String expression);


        public abstract boolean buildExpression(Spec spec);

        public abstract boolean buildExpression(Comp _comp);

        public abstract boolean buildExpression();

        public abstract boolean buildExpression(ATT _attr);

        public abstract int getResult();

        public abstract int getResult(SpecState _ss);

        public abstract int getResult(SpecState _ss, State _st);

        public abstract int getResult(CompState _cs);

        public abstract int getResult(Object _qs);

        public abstract String toString();
        public abstract boolean hasState();
        public abstract void linkComp(Spec spec);
        public abstract void linkStates();
        public abstract int getReturnType();
    }
    //!! Only used in test
    public abstract int indexOfVariable(String expr, String variable);
    //!! Only used in test
    public abstract String replaceVariable(String expr, String oldVariable, String newVariable);
}
