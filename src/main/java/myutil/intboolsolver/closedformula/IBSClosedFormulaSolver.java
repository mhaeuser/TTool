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
 * Static Instance of IBS parser and evaluator system to be used
 * for closed formulas.
 * Creation: 11/04/2023
 * <p>This class is a wrapper class that provides all the features
 * offered by the IBS System for the closed formula instantiation in a
 * single class. It also implements the relevant casts toward
 * the types of the closed formula instantiation, so that the user
 * don't have to worry about this.</p>
 * <p> As a "globally" available IBS System, this class is static
 * and not thread safe. </p>
 * <p> Note: for any other instantiation, a similar class can be
 * obtained by simply copying this class and global-replacing the names
 * that are specific to the instantiation by the relevant ones.
 * (IBSClosedSpec, IBSClosedComp, IBSClosedState
 *  IBSClosedFormulaAttributes, IBSClosedFormulaExpressions,
 * IBSClosedFormulaParser). Of course other wrapper methods for
 * instantiation specific features may be added.
 * </p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
 */

public class IBSClosedFormulaSolver {
    public static IBSClosedFormulaParser parser =
            new IBSClosedFormulaParser(new IBSClosedFormulaAttributes(), new IBSClosedFormulaExpressions());
    /**
     * Set the parser's attribute class.
     * @param _c the attribute class to set.
     */
    public static void setAttributes(IBSClosedFormulaAttributes _c) {
        parser.setAttributes(_c);
    }
    /**
     * Get the parser's attribute class.
     * @return the parser's attribute class.
     */
    public static IBSClosedFormulaAttributes getAttributes() {
        return (IBSClosedFormulaAttributes) parser.getAttributes();
    }
    /**
     * Set the parser's expression class.
     * @param _e the expression class to set.
     */
    public static void setExpressions(IBSClosedFormulaExpressions _e) {
        parser.setExpressions(_e);
    }
    /**
     * Get the parser's expression class.
     * @return the parser's expression class.
     */
    public static IBSClosedFormulaExpressions getExpressions() {
        return (IBSClosedFormulaExpressions) parser.getExpressions();
    }

    /**
     * Get the bad identifiers found while parsing
     * @return the set of found bad identifiers;
     */
    public static HashSet<String> getBadIdents() {
        return parser.getBadIdents();
    }

    /**
     * Empty the parser's set of found bad identifiers.
     * (this is not done automatically to allow some flexibility)
     */
    public static void clearBadIdents() { parser.clearBadIdents(); }

    /** Parse an integer expression
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.IExpr parseInt(IBSClosedSpec _spec, String _s) {
        return (IBSClosedFormulaExpressions.IExpr) parser.parseInt(_spec, _s);
    }
    /** Parse a boolean expression
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr parseBool(IBSClosedSpec _spec, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseBool(_spec, _s);
    }
    /** Parse a guard
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr parseGuard(IBSClosedSpec _spec, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseGuard(_spec, _s);
    }
    /** Parse an integer expression
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.IExpr parseInt(IBSClosedComp _comp, String _s) {
        return (IBSClosedFormulaExpressions.IExpr) parser.parseInt(_comp, _s);
    }
    /** Parse a boolean expression
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr parseBool(IBSClosedComp _comp, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseBool(_comp, _s);
    }
    /** Parse a guard
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr parseGuard(IBSClosedComp _comp, String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseGuard(_comp, _s);
    }
    /** Parse a closed integer expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.IExpr parseInt(String _s) {
        return (IBSClosedFormulaExpressions.IExpr) parser.parseInt(_s);
    }
    /** Parse a closed boolean expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr parseBool(String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseBool(_s);
    }
    /** Parse a closed guard
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr parseGuard(String _s) {
        return (IBSClosedFormulaExpressions.BExpr) parser.parseGuard(_s);
    }
    /** Builds an integer expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.IExpr
    makeInt(IBSClosedFormulaAttributes.Attribute _attr) {
        return (IBSClosedFormulaExpressions.IExpr) parser.makeInt(_attr);
    }
    /** Builds a boolean expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public static IBSClosedFormulaExpressions.BExpr
    makeBool(IBSClosedFormulaAttributes.Attribute _attr) {
        return (IBSClosedFormulaExpressions.BExpr) parser.makeBool(_attr);
    }
    /** Get the unique sub-expression of an unary integer expression.
     * @param _e the expression
     * @return its sub-expression, or null if e is not unary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getArg(IBSClosedFormulaExpressions.IExpr _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getArg(_e);
    }
    /** Get the unique sub-expression of an unary boolean expression.
     * @param _e the expression
     * @return its sub-expression, or null if e is not unary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.BExpr getArg(IBSClosedFormulaExpressions.BExpr _e) {
        return (IBSClosedFormulaExpressions.BExpr) IBSClosedFormulaExpressions.getArg(_e);
    }
    /** Get the left sub-expression of an integer binary operator expression.
     * @param _e the expression
     * @return its left sub-expression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getLeftArg(IBSClosedFormulaExpressions.IIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getLeftArg(_e);
    }
    /** Get the right sub-expression of an integer binary operator expression.
     * @param _e the expression
     * @return its right sub-expression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getRightArg(IBSClosedFormulaExpressions.IIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getRightArg(_e);
    }
    /** Get the left sub-expression of an integer comparison expression.
     * @param _e the expression
     * @return its left sub-expression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getLeftArg(IBSClosedFormulaExpressions.BIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getLeftArg(_e);
    }
    /** Get the right sub-expression of an integer comparison expression.
     * @param _e the expression
     * @return its left sub-expression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.IExpr getRightArg(IBSClosedFormulaExpressions.BIIBinOp _e) {
        return (IBSClosedFormulaExpressions.IExpr) IBSClosedFormulaExpressions.getRightArg(_e);
    }
    /** Get the right sub-expression of a boolean binary operator expression.
     * @param _e the expression
     * @return its left sub-expression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.BExpr getLeftArg(IBSClosedFormulaExpressions.BBBBinOp _e) {
        return (IBSClosedFormulaExpressions.BExpr) IBSClosedFormulaExpressions.getLeftArg(_e);
    }
    /** Get the right sub-expression of a boolean binary operator expression.
     * @param _e the expression
     * @return its right sub-expression, or null if e is not binary
     */
    @SuppressWarnings("unchecked")
    public static IBSClosedFormulaExpressions.BExpr getRightArg(IBSClosedFormulaExpressions.BBBBinOp _e) {
        return (IBSClosedFormulaExpressions.BExpr) IBSClosedFormulaExpressions.getRightArg(_e);
    }
    /** Get the integer expression memorized at some index
     * @param _i the index
     * @return the requested expression (may be null)
     */
    public static IBSClosedFormulaExpressions.IExpr getIExpr(int _i) {
        return getExpressions().getIExpr(_i);
    }
    /** Get the boolean expression memorized at some index
     * @param _i the index
     * @return the requested expression (may be null)
     */
    public static IBSClosedFormulaExpressions.BExpr getBExpr(int _i) {
        return getExpressions().getBExpr(_i);
    }
    /** Put a boolean expression in internal memory
     *
     * @param _expr the expression
     * @return The index at which the expression is  memorized.
     */
    public static int putBExpr(IBSClosedFormulaExpressions.BExpr _expr) { return getExpressions().putBExpr(_expr); }
    /** Put an integer expression in internal memory
     *
     * @param _expr the expression
     * @return The index at which the expression is  memorized.
     */
    public static int putIExpr(IBSClosedFormulaExpressions.IExpr _expr) { return getExpressions().putIExpr(_expr); }
    /** Deletes a memorized integer expression, making its index free
     * @param _toFree the index to free
     */
    public static void freeInt(int _toFree) { getExpressions().freeInt(_toFree); }
    /** Deletes a memorized boolean expression, making its index free
     * @param _toFree the index to free
     */
    public static void freeBool(int _toFree) { getExpressions().freeBool(_toFree); }
    /** Clear the internal memories containing build integer and boolean expressions
     */
    public static void clear(){ getExpressions().clear(); }
    /** Binary Plus expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_iiiPlus(int _left, int _right) { return getExpressions().make_iiiPlus(_left, _right); }
    /** Binary Minus expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_iiiMinus(int _left, int _right) { return getExpressions().make_iiiMinus(_left, _right); }
    /** Binary Mult expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_iiiMult(int _left, int _right) { return getExpressions().make_iiiMult(_left, _right); }
    /** Binary Div expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_iiiDiv(int _left, int _right) { return getExpressions().make_iiiMod(_left, _right); }
    /** Binary Modulus expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_iiiMod(int _left, int _right) { return getExpressions().make_iiiPlus(_left, _right); }
    /** Binary And expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_bbbAnd(int _left, int _right) { return getExpressions().make_bbbAnd(_left, _right); }
    /** Binary Or expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_bbbOr(int _left, int _right) { return getExpressions().make_bbbOr(_left, _right); }
    /** Integer equality expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_biiEq(int _left, int _right) { return getExpressions().make_biiEq(_left, _right); }
    /** Boolean equality expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_bbbEq(int _left, int _right) { return getExpressions().make_bbbEq(_left, _right); }
    /** Integer difference expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_biiDif(int _left, int _right) { return getExpressions().make_biiDif(_left, _right); }
    /** Boolean difference expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_bbbDif(int _left, int _right) { return getExpressions().make_bbbDif(_left, _right); }
    /** Integer "lower than" expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_biiLt(int _left, int _right) { return getExpressions().make_biiLt(_left, _right); }
    /** Integer "greater than" expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_biiGt(int _left, int _right) { return getExpressions().make_biiGt(_left, _right); }
    /** Integer "lower than or equal" expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_biiLeq(int _left, int _right) { return getExpressions().make_biiLeq(_left, _right); }
    /** Integer "greater than or equal" expression constructor
     * @param _left index of left sub-expression
     * @param _right index of right sub-expression
     * @return index of the build expression
     */
    public static int make_biiGeq(int _left, int _right) { return getExpressions().make_biiGeq(_left, _right); }
    /** Integer variable expression constructor
     * @param _v attribute defining the variable
     * @return index of the build expression
     */
    public static int make_iVar(IBSClosedFormulaAttributes.Attribute _v) { return getExpressions().make_iVar(_v); }
    /** Boolean variable expression constructor
     * @param _v attribute defining the variable
     * @return index of the build expression
     */
    public static int make_bVar(IBSClosedFormulaAttributes.Attribute _v) { return getExpressions().make_bVar(_v); }
    /** Integer constant expression constructor
     * @param _i the value of the constant
     * @return index of the build expression
     */
    public static int make_iConst(int _i) { return getExpressions().make_iConst(_i); }
    /** Boolean constant expression constructor
     * @param _b the value of the constant
     * @return index of the build expression
     */
    public static int make_bConst(boolean _b) { return getExpressions().make_bConst(_b); }
    /** Build an expression denoting the opposite of the provided integer expression
     * @param _i the expression from which the opposite is requested
     * @return index of the build expression
     */
    public static int make_iNeg(int _i) { return getExpressions().make_iNeg(_i); }
    /** Build an expression denoting the negation of the provided boolean expression
     * @param _i the expression from which the negation is requested
     * @return index of the build expression
     */
    public static int make_bNot(int _i) { return getExpressions().make_bNot(_i); }
    /** As its name suggests...
     * @param _a the typed attribute
     * @return the attribute type, among NullAttr IntConst BoolConst IntAttr BoolAttr
     */
    public static int getType(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.getType(); }
    /** As its name suggests... relevant only for constant types (unchecked)
     * @param _a the typed attribute
     * @return the constant value of the attribute
     */
    public static int getConstant(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.getConstant(); }
    /** As its name suggests... relevant only for variable types (unchecked)
     * @param _a the typed attribute
     * @return the attribute that characterizes the variable
     */
    public static IBSClosedFormulaAttributes.Attribute getAttribute(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.getAttribute(); }
    /** As its name suggests...
     * @param _a the typed attribute
     * @return true iff type is IntAttr or BoolAttr
     */
    public static boolean isAttribute(IBSClosedFormulaAttributes.TypedAttribute _a) { return _a.isAttribute(); }
    /** Builds an attribute from a specification and an identifier.
     * @param _spec the specification
     * @param _s the identifier string
     * @return the build typed attribute or <code>NullTypedAttribute</code> if building fails.
     */
    public static IBSClosedFormulaAttributes.TypedAttribute getTypedAttribute(IBSClosedSpec _spec, String _s) {
        return getAttributes().getTypedAttribute(_spec, _s);
    }
    /** Builds an attribute from a component and an identifier.
     * @param _comp the component
     * @param _s the identifier string
     * @return the build typed attribute or <code>NullTypedAttribute</code> if building fails.
     */
    public static IBSClosedFormulaAttributes.TypedAttribute getTypedAttribute(IBSClosedComp _comp, String _s) {
        return getAttributes().getTypedAttribute(_comp, _s);
    }
    /** Builds a boolean attribute from a component and a (state machine) state.
     * @param _comp the component
     * @param _state the state
     * @return the build boolean attribute or <code>NullTypedAttribute</code> if building fails.
     */
    public static IBSClosedFormulaAttributes.TypedAttribute getTypedAttribute(IBSClosedComp _comp, IBSClosedState _state) {
        return getAttributes().getTypedAttribute(_comp, _state);
    }
}
