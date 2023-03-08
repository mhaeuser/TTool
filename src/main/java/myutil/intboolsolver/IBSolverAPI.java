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
 * Class IBSolverAPI, Solver API (exported features).
 * FOR DOCUMENTATION, NOT FOR USE...
 * Creation: 08/03/2023
 *
 * <p> <b>Note:</b> Although constructors do not appear in this
 * abstract class (to keep their code private), the generic
 * solver provides two constructors: </p>
 * <PRE>
 *    public IBSolver(AtC _c);
 *    IBSolver();
 * </PRE>
 * <p>Thus an instance "Solver" may propose similar constructors.
 * (notice that the default constructor must be followed by a
 * call to Solver.setAttributeClass(AtC _c), because the solver
 * can't do the job without attribute class. Another usual
 * constructor to implement in instance is</p>
 * <PRE>
 *    public Solver(){super(new SolverAttributeClass();}
 * </PRE>
 * <p> As this depends of instantiation choice, see the
 * documentation of the solver instances</p>
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */

public abstract class IBSolverAPI<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState,
        ATT extends IBSAttribute<Spec,Comp,State,SpecState,CompState>,
        AtC extends IBSAttributeClass<Spec,Comp,State,SpecState,CompState,ATT>
        > {

    /**
     * Set the solver attribute class.
     *
     * <p> Often called only once, just
     * after solver creation, or never if solver constructor do the
     * job.</p>
     * @param _c the attribute class
     */
    public abstract void setAttributeClass(AtC _c);

    /**
     * Get the solver attribute class.
     * @return solver attribute class
     */
    public abstract AtC getAttributeClass();

    /**
     * Get free identifiers that have been encountered while
     * parsing.
     *
     * <p>(each time an expression is parsed, the encountered free
     * identifiers are added to an internal set)</p>
     * @return the set of encountered free identifiers.
     */
    public abstract HashSet<String> getFreeIdents();

    /**
     * Empty the set of encountered free identifiers.
     *
     * <p> Note that this is never done automatically.</p>
     * <p> Thus, for example, if you want to get the open leaves
     * of a formula, you must empty the set before parsing it.</p>
     */
    public abstract void clearFreeIdents();

    /**
     * The class that defines the structure of the expressions
     * the solver builds while parsing.
     *
     * <p> Note: intended to become a parameter in future
     * development.</p>
     */
    public abstract class Expr {
        /**
         * Default constructor, to use in specific case.
         *
         * <p> The default constructor is generally followed by
         * buildExpression(ATT _attr) (which does not require any
         * parsing and recovers the expression string from
         * attribute string representation (toString)).</p>
         */
        public Expr(){} // hidden implementation (in IBSolver)

        /** The most usual Constructor
         *
         * @param expression the string version of the expression
         *                   (i.e. the string to parse)
         */
        public Expr(String expression) {} // hidden implementation (in IBSolver)

        /**
         * Sets the expression to parse
         * @param expression the expression to parse
         */
        public abstract void setExpression(String expression);

        /**
         * builds the expression structure by parsing the expression string.
         *
         * <p>The expression string must have been provided, either by
         * {@code new Expr(String)} or by {@code setExpression(String)}</p>
         * <p> After this build, the expression is supposed to be at least
         * ready for {@code getResult(SpecState _ss)} and {@code
         * getResult(SpecState _ss, State _st)}. It may also be ready
         * for {@code getResult(CompState _cs)} if all open leaves can
         * get value in a single bloc state</p>
         * @param _spec the specification where to find the structures
         *             associated to leaves
         * @return false if parsing generates errors. Otherwise true.
         */
        public abstract boolean buildExpression(Spec _spec);

        /**
         * builds the expression structure by parsing the expression string.
         *
         * <p>The expression string must have been provided, either by
         * {@code new Expr(String)} or by {@code setExpression(String)}</p>
         * <p> After this build, the expression is supposed to be at least
         * ready for {@code getResult(CompState _cs)}</p>
         * @param _comp the component where to find the structures
         *             associated to leaves
         * @return false if parsing generates errors. Otherwise true.
         */
        public abstract boolean buildExpression(Comp _comp);

        /**
         * builds the expression structure by parsing the expression string.
         *
         * <p>The expression string must have been provided, either by
         * {@code new Expr(String)} or by {@code setExpression(String)}</p>
         * <p> The expression string must denote a closed expression.</p>
         * @return false if parsing generates errors. Otherwise true.
         */
        public abstract boolean buildExpression();

        /**
         * builds a leave expression structure corresponding to the
         * provided attribute.
         *
         * <p> The expression string is set to the attribute string
         * representation (toString)</p>
         * @return true.
         */
        public abstract boolean buildExpression(ATT _attr);

        /**
         * Evaluate the expression.
         *
         * <p> The expression must be closed and have been build with
         * success.</p>
         * @return
         */
        public abstract int getResult();
        /**
         * Evaluate the expression.
         *
         * <p> The expression must have been build with
         * success and obviously, {@code SpecState} must
         * have some relation with a specification associated
         * to the expression (while building or afterwards,
         * using {@code linkState} and/or {@code linkComp}.</p>
         * @param _ss the specification state in which open
         *            leaves (are expected to) have a value.
         * @return the value associated to the expression
         * by _ss.
         */
        public abstract int getResult(SpecState _ss);

        public abstract int getResult(SpecState _ss, State _st);

        /**
         * Evaluate the expression.
         *
         * <p> The expression must have been build with
         * success and obviously, {@code CompState} must
         * have some relation with a component associated
         * to the expression (while building or afterwards,
         * using {@code linkState}.</p>
         * @param _cs the component state in which open
         *            leaves (are expected to) have a value.
         * @return the value associated to the expression
         * by _cs.
         */
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
