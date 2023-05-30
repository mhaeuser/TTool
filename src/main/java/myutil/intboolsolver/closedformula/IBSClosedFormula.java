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

package myutil.intboolsolver.closedformula;

import java.util.HashSet;

/**
 * Instance of IBS parser and evaluator system to be used
 * for closed formulas.
 * Creation: 11/04/2023
 * <p>This class is a wrapper class that provides all the features
 * offered by the IBS System for the closed formula instantiation in a
 * single class. It also implements the relevant casts toward
 * the types of the closed formula instantiation, so that the user
 * don't have to worry about this.</p>
 * <p> This class is dynamic and must be used through instances
 * (so that it is thread safe).
 * Notice that this class's size are not neglectable (it has many methods
 * and contains a javacup parser), it is not recommanded to use too much
 * instances simultaneously.</p>
 * <p> Note: for any other instantiation, a similar class can be
 * obtained by simply copying this class and global-replacing the names
 * that are specific to the instantiation by the relevant ones.
 * (IBSClosedSpec, IBSClosedComp, IBSClosedState
 *  AvatarIBSAttributes, AvatarIBSExpressions,
 * AvatarIBSStdParser). Of course other wrapper methods for
 * instantiation specific features may be added.
 * </p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
 */

public class IBSClosedFormula {
    public final IBSClosedFormulaParser parser;
    public IBSClosedFormula(IBSClosedFormulaAttributes _a, IBSClosedFormulaExpressions _e){
        parser = new IBSClosedFormulaParser(_a, _e);
    }
    /**
     * Set the parser's attribute class.
     * @param _c the attribute class to set.
     */
    public final void setAttributes(IBSClosedFormulaAttributes _c) {
        parser.setAttributes(_c);
    }
    /**
     * Get the parser's attribute class.
     * @return the parser's attribute class.
     */
    public final IBSClosedFormulaAttributes getAttributes() {
        return (IBSClosedFormulaAttributes) parser.getAttributes();
    }
    /**
     * Set the parser's expression class.
     * @param _e the expression class to set.
     */
    public final void setExpressions(IBSClosedFormulaExpressions _e) {
        parser.setExpressions(_e);
    }
    /**
     * Get the parser's expression class.
     * @return the parser's expression class.
     */
    public final IBSClosedFormulaExpressions getExpressions() {
        return (IBSClosedFormulaExpressions) parser.getExpressions();
    }

    /**
     * Get the bad identifiers found while parsing
     * @return the set of found bad identifiers;
     */
    public final HashSet<String> getBadIdents() {
        return parser.getBadIdents();
    }

    /**
     * empty the parser's set of found bad identifiers.
     * (this is not done automatically to allow some flexibility)
     */
    public final void clearBadIdents() { parser.clearBadIdents(); }

    /** parse an integer expression
     * @param _spec the specification in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.IExpr parseInt(IBSClosedSpec _spec, String _s) {
        return (IBSClosedFormulaExpressions.IExpr) parser.parseInt(_spec,_s);
    }
    /** parse a boolean expression
     * @param _spec the specification in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr parseBool(IBSClosedSpec _spec, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseBool(_spec,_s);
    }
    /** parse a guard
     * @param _spec the specification in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr parseGuard(IBSClosedSpec _spec, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseGuard(_spec,_s);
    }
    /** parse an integer expression
     * @param _comp the component in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.IExpr parseInt(IBSClosedComp _comp, String _s) {
        return (IBSClosedFormulaExpressions.IExpr) parser.parseInt(_comp,_s);
    }
    /** parse a boolean expression
     * @param _comp the component in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr parseBool(IBSClosedComp _comp, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseBool(_comp,_s);
    }
    /** parse a guard
     * @param _comp the component in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr parseGuard(IBSClosedComp _comp, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseGuard(_comp,_s);
    }
    /** parse a closed integer expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.IExpr parseInt(String _s) {
        return (IBSClosedFormulaExpressions.IExpr) parser.parseInt(_s);
    }
    /** parse a closed boolean expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr parseBool(String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseBool(_s);
    }
    /** parse a closed guard
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr parseGuard(String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseGuard(_s);
    }
    /** builds an integer expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.IExpr
    makeInt(IBSClosedFormulaAttributes.Attribute _attr) {
        return (IBSClosedFormulaExpressions.IExpr) parser.makeInt(_attr);
    }
    /** builds a boolean expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public final IBSClosedFormulaExpressions.BExpr
    makeBool(IBSClosedFormulaAttributes.Attribute _attr) {
        return (IBSClosedFormulaExpressions.BExpr) parser.makeBool(_attr);
    }
    /** get the unique subexpression of an unary integer expression.
     * @param _e the expression
     * @return its subexpression, or null if e is not unary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getArg(IBSClosedFormulaExpressions.IExpr _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getArg(_e);
    }
    /** get the unique subexpression of an unary boolean expression.
     * @param _e the expression
     * @return its subexpression, or null if e is not unary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.BExpr getArg(IBSClosedFormulaExpressions.BExpr _e) {
        return (IBSClosedFormulaExpressions.BExpr) IBSClosedFormulaExpressions.getArg(_e);
    }
    /** get the left subexpression of an integer binary operator expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getLeftArg(IBSClosedFormulaExpressions.IIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getLeftArg(_e);
    }
    /** get the right subexpression of an integer binary operator expression.
     * @param _e the expression
     * @return its right subexpression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getRightArg(IBSClosedFormulaExpressions.IIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getRightArg(_e);
    }
    /** get the left subexpression of an integer comparison expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getLeftArg(IBSClosedFormulaExpressions.BIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getLeftArg(_e);
    }
    /** get the right subexpression of an integer comparison expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getRightArg(IBSClosedFormulaExpressions.BIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getRightArg(_e);
    }
    /** get the right subexpression of a boolean binary operator expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.BExpr getLeftArg(IBSClosedFormulaExpressions.BBBBinOp _e) {
        return (IBSClosedFormulaExpressions.BExpr) IBSClosedFormulaExpressions.getLeftArg(_e);
    }
    /** get the right subexpression of a boolean binary operator expression.
     * @param _e the expression
     * @return its right subexpression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.BExpr getRightArg(IBSClosedFormulaExpressions.BBBBinOp _e) {
        return (IBSClosedFormulaExpressions.BExpr) IBSClosedFormulaExpressions.getRightArg(_e);
    }
    /** get the integer expression memorized at some index
     * @param _i the index
     * @return the requested expression (may be null)
     */
    public final IBSClosedFormulaExpressions.IExpr getIExpr(int _i) {
        return getExpressions().getIExpr(_i);
    }
    /** get the boolean expression memorized at some index
     * @param _i the index
     * @return the requested expression (may be null)
     */
    public final IBSClosedFormulaExpressions.BExpr getBExpr(int _i) {
        return getExpressions().getBExpr(_i);
    }
    /** put a boolean expression in internal memory
     *
     * @param _expr the expression
     * @return The index at which the expression is  memorized.
     */
    public final int putBExpr(IBSClosedFormulaExpressions.BExpr _expr) { return getExpressions().putBExpr(_expr); }
    /** put an integer expression in internal memory
     *
     * @param _expr the expression
     * @return The index at which the expression is  memorized.
     */
    public final int putIExpr(IBSClosedFormulaExpressions.IExpr _expr) { return getExpressions().putIExpr(_expr); }
    /** deletes a memorized integer expression, making its index free
     * @param _toFree the index to free
     */
    public final void freeInt(int _toFree) { getExpressions().freeInt(_toFree); }
    /** deletes a memorized boolean expression, making its index free
     * @param _toFree the index to free
     */
    public final void freeBool(int _toFree) { getExpressions().freeBool(_toFree); }
    /**
     * clear the internal memories containing build integer and boolean expressions
     */
    public final void clear(){ getExpressions().clear(); }
    /** binary Plus expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_iiiPlus(int _left, int _right) { return getExpressions().make_iiiPlus(_left,_right); }
    /** binary Minus expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_iiiMinus(int _left, int _right) { return getExpressions().make_iiiMinus(_left,_right); }
    /** binary Mult expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_iiiMult(int _left, int _right) { return getExpressions().make_iiiMult(_left,_right); }
    /** binary Div expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_iiiDiv(int _left, int _right) { return getExpressions().make_iiiMod(_left,_right); }
    /** binary Modulus expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_iiiMod(int _left, int _right) { return getExpressions().make_iiiPlus(_left,_right); }
    /** binary And expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_bbbAnd(int _left, int _right) { return getExpressions().make_bbbAnd(_left,_right); }
    /** binary Or expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_bbbOr(int _left, int _right) { return getExpressions().make_bbbOr(_left,_right); }
    /** integer equality expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_biiEq(int _left, int _right) { return getExpressions().make_biiEq(_left,_right); }
    /** boolean equality expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_bbbEq(int _left, int _right) { return getExpressions().make_bbbEq(_left,_right); }
    /** integer difference expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_biiDif(int _left, int _right) { return getExpressions().make_biiDif(_left,_right); }
    /** boolean difference expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_bbbDif(int _left, int _right) { return getExpressions().make_bbbDif(_left,_right); }
    /** integer "lower than" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_biiLt(int _left, int _right) { return getExpressions().make_biiLt(_left,_right); }
    /** integer "greater than" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_biiGt(int _left, int _right) { return getExpressions().make_biiGt(_left,_right); }
    /** integer "lower than or equal" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_biiLeq(int _left, int _right) { return getExpressions().make_biiLeq(_left,_right); }
    /** integer "greater than or equal" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public final int make_biiGeq(int _left, int _right) { return getExpressions().make_biiGeq(_left,_right); }
    /** integer variable expression constructor
     * @param _v attribute defining the variable
     * @return index of the build expression
     */
    public final int make_iVar(IBSClosedFormulaAttributes.Attribute _v) { return getExpressions().make_iVar(_v); }
    /** boolean variable expression constructor
     * @param _v attribute defining the variable
     * @return index of the build expression
     */
    public final int make_bVar(IBSClosedFormulaAttributes.Attribute _v) { return getExpressions().make_bVar(_v); }
    /** integer constant expression constructor
     * @param _i the value of the constant
     * @return index of the build expression
     */
    public final int make_iConst(int _i) { return getExpressions().make_iConst(_i); }
    /** boolean constant expression constructor
     * @param _b the value of the constant
     * @return index of the build expression
     */
    public final int make_bConst(boolean _b) { return getExpressions().make_bConst(_b); }
    /** build an expression denoting the opposite of the provided integer expression
     * @param _i the expression from which the opposite is requested
     * @return index of the build expression
     */
    public final int make_iNeg(int _i) { return getExpressions().make_iNeg(_i); }
    /** build an expression denoting the negation of the provided boolean expression
     * @param _i the expression from which the negation is requested
     * @return index of the build expression
     */
    public final int make_bNot(int _i) { return getExpressions().make_bNot(_i); }
    /** As its name suggests...
     * @return the attribute type, among NullAttr IntConst BoolConst IntAttr BoolAttr
     */
    public static int getType(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.getType(); }
    /** As its name suggests... relevant only for constant types (unchecked)
     * @return the constant value of the attribute
     */
    public static int getConstant(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.getConstant(); }
    /** As its name suggests... relevant only for variable types (unchecked)
     * @return the attribute that characterizes the variable
     */
    public static IBSClosedFormulaAttributes.Attribute getAttribute(IBSClosedFormulaAttributes.TypedAttribute _a) {
        return _a.getAttribute();
    }
    /** As its name suggests...
     * @return true iff type is IntAttr or BoolAttr
     */
    public static boolean isAttribute(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.isAttribute(); }
    /**
     * builds an attribute from a specification and an identifier.
     * @param _spec the specification
     * @param _s the identifier string
     * @return the build typed attribute or <code>NullTypedAttribute</code> if building fails.
     */
    public final IBSClosedFormulaAttributes.TypedAttribute getTypedAttribute(IBSClosedSpec _spec, String _s) {
        return getAttributes().getTypedAttribute(_spec, _s);
    }
    /**
     * builds an attribute from a component and an identifier.
     * @param _comp the component
     * @param _s the identifier string
     * @return the build typed attribute or <code>NullTypedAttribute</code> if building fails.
     */
    public final IBSClosedFormulaAttributes.TypedAttribute getTypedAttribute(IBSClosedComp _comp, String _s) {
        return getAttributes().getTypedAttribute(_comp, _s);
    }
    /**
     * Builds a boolean attribute from a component and a (state machine) state.
     * @param _comp the component
     * @param _state the state
     * @return the build boolean attribute or <code>NullTypedAttribute</code> if building fails.
     */
    public final IBSClosedFormulaAttributes.TypedAttribute getTypedAttribute(IBSClosedComp _comp, IBSClosedState _state) {
        return getAttributes().getTypedAttribute(_comp, _state);
    }
}
