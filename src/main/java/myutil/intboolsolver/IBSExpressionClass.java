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
/** Interface of expressions for the IBS System.
 *
 * <p> Methods are provided to build and evaluate integer and boolean
 * expressions.</p>
 * <p>Integer that are used as parameter or return value by building
 * methods are indexes in some internal memory where build expressions
 * are saved (technical choice that allow inheritance)</p>
 * <p> There are two memories: one for integer expressions and one
 * for boolean expressions.</p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
*/

public class IBSExpressionClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    /**
     * makes the provided index available for integer expressions.
     * The pointed expression is lost. May raise an exception for
     * unused indexes.
     * @param _toFree the index to free
     */
    public void freeInt(int _toFree) {}
    /**
     * makes the provided index available for integer expressions.
     * The pointed expression is lost. May raise an exception for
     * unused indexes.
     * @param _toFree the index to free
     */
    public void freeBool(int _toFree) {}
    /**
     * makes all indexes available for integer or boolean
     * expressions.
     * all memorized expressions are lost.
     */
    public void clear(){}

    /** obtain an integer expression from its index
     *
     * @param _expr the index
     * @return The expression memorized at the provided index.
     */
    public IExpr getIExpr(int _expr) { return null; }
    /** obtain a boolean expression from its index
     *
     * @param _expr the index
     * @return The expression memorized at the provided index.
     */
    public BExpr getBExpr(int _expr) { return null; }

    /** test weather some saved expression is constant.
     *
     * @param i index of a boolean expression
     * @return true iff the associated expression is constant
     */
    public boolean isBconstant(int i){ return false; }
    /** test weather some saved expression is constant.
     *
     * @param i index of an integer expression
     * @return true iff the associated expression is constant
     */
    public boolean isIconstant(int i){ return false; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_iiiPlus(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_iiiMinus(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_iiiMult(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_iiiDiv(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_iiiMod(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_bbbAnd(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_bbbOr(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_biiEq(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_bbbEq(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_biiDif(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_bbbDif(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_biiLt(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_biiGt(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_biiLeq(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * Parameters are indexes of previously build expressions
     */
    public int make_biiGeq(int _left, int _right) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     */
    public int make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     */
    public int make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     */
    public int make_biVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     */
    public int make_iConst(int _i) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     */
    public int make_bConst(boolean _b) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * The Parameter is the index of a previously build expression.
     */
    public int make_iNeg(int _expr) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * The Parameter is the index of a previously build expression.
     */
    public int make_bNot(int _expr) { return -1; }
    /** method for building an expression. Such a method find
     * a free index to memorize the build expression and return it.
     * The Parameter is the index of a previously build expression.
     */
    public int make_biExpr(int _expr) { return -1; }

    public abstract class Expr{
        public abstract String toString();
        public abstract boolean hasStates();
        public abstract void linkStates();
        public abstract void linkComps(Spec _spec);
    }
    public abstract class IExpr extends Expr {
        public abstract int eval();
        public abstract int eval(SpecState _ss);
        public abstract int eval(SpecState _ss, State _st);
        public abstract int eval(CompState _cs);
        public abstract int eval(Object _qs);
    }
    public abstract class BExpr extends Expr {
        public abstract boolean eval();
        public abstract boolean eval(SpecState _ss);
        public abstract boolean eval(SpecState _ss, State _st);
        public abstract boolean eval(CompState _cs);
        public abstract boolean eval(Object _qs);
    }
}
