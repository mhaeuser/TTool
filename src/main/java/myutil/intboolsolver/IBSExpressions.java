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
/** This class defines the features expressions must provide in order
 * to be usable in the the generic IntBoolSolver System.
 *
 * <p> Methods are provided to build and evaluate integer and boolean
 * expressions.</p>
 * <p> Methods provided for building expressions use integers as
 * parameters and return values: these integers are indexes in some
 * internal memory where build expressions are saved (technical choice
 * to allow inheritance)</p>
 * <p> There are two memories: one for integer expressions and one
 * for boolean expressions.</p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
*/

public class IBSExpressions<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    /**
     * Makes the provided index available for integer expressions.
     * The pointed expression is lost. May raise an exception for
     * unused indexes.
     * @param _toFree the index to free
     */
    public void freeInt(int _toFree) {}
    /**
     * Makes the provided index available for integer expressions.
     * The pointed expression is lost. May raise an exception for
     * unused indexes.
     * @param _toFree the index to free
     */
    public void freeBool(int _toFree) {}
    /**
     * Clear internal memories for build integer or boolean
     * expressions.
     * All memorized expressions are lost.
     */
    public void clear(){}

    /** Obtain an integer expression from its index
     * @param _expr the index
     * @return The expression memorized at the provided index.
     */
    public IExpr getIExpr(int _expr) { return null; }
    /** Obtain a boolean expression from its index
     *
     * @param _expr the index
     * @return The expression memorized at the provided index.
     */
    public BExpr getBExpr(int _expr) { return null; }
    /** Test whether some saved boolean expression is constant.
     * @param _i index of a boolean expression
     * @return true iff the associated expression is constant
     */
    public boolean isBconstant(int _i){ return false; }
    /** Test weather some saved integer expression is constant.
     *
     * @param _i index of an integer expression
     * @return true iff the associated expression is constant
     */
    public boolean isIconstant(int _i){ return false; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions.
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_iiiPlus(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_iiiMinus(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_iiiMult(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_iiiDiv(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_iiiMod(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_bbbAnd(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_bbbOr(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_biiEq(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_bbbEq(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_biiDif(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_bbbDif(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_biiLt(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_biiGt(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_biiLeq(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * Parameters are indexes of previously build expressions
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of build expression, or -1 (if error)
     */
    public int make_biiGeq(int _left, int _right) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * @param _v attribute from which the expression is build
     * @return index of build expression, or -1 (if error)
     */
    public int make_iVar(IBSAttributes<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * @param _v attribute from which the expression is build
     * @return index of build expression, or -1 (if error)
     */
    public int make_bVar(IBSAttributes<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * @param _i integer value from which the constant expression is build
     * @return index of build expression, or -1 (if error)
     */
    public int make_iConst(int _i) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * @param _b boolean value from which the constant expression is build
     * @return index of build expression, or -1 (if error)
     */
    public int make_bConst(boolean _b) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * The Parameter is the index of an expression build previously.
     * @param _expr expression from which the negative version is requested
     * @return index of build expression, or -1 (if error)
     */
    public int make_iNeg(int _expr) { return -1; }
    /** Method for building an expression. Such a method finds
     * a free index to memorize the build expression and returns it.
     * The Parameter is the index of an expression build previously.
     * @param _expr expression from which the negated version is requested
     * @return index of build expression, or -1 (if error)
     */
    public int make_bNot(int _expr) { return -1; }
    /** The class of all expressions */
    public abstract class Expr{
        /** To be available  in extensions for technical reasons */
        protected short type;
        public short getType() {return type;}
        public abstract String toString();
        public abstract boolean hasStates();
        public abstract void linkStates();
        public abstract void linkComps(Spec _spec);
    }
    /** The class of integer expressions */
    public abstract class IExpr extends Expr {
        public abstract int eval();
        public abstract int eval(SpecState _ss);
        public abstract int eval(SpecState _ss, State _st);
        public abstract int eval(CompState _cs);
        public abstract int eval(Object _qs);
    }
    /** The class of boolean expressions */
    public abstract class BExpr extends Expr {
        public abstract boolean eval();
        public abstract boolean eval(SpecState _ss);
        public abstract boolean eval(SpecState _ss, State _st);
        public abstract boolean eval(CompState _cs);
        public abstract boolean eval(Object _qs);
    }
}
