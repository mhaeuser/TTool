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

import java.util.ArrayList;
/** Standard implementation of expressions for the IBS System.
 *
 * <p> Methods are provided to build and evaluate integer and boolean
 * expressions.</p>
 * <p> W.r.t. {@link myutil.intboolsolver.IBSExpressionClass IBSExpressionClass},
 * some additional methods are provided (to identify expression types, to access
 * subexpressions, and to put expression in internal memory in order to be able
 * to build new expressions).</p>
 * <p>Integer that are used as parameter or return value by building
 * methods are indexes in some internal memory where build expressions
 * are saved (technical choice that allow inheritance). When building expressions,
 * the index where the result is memorized is returned.
 * A returned index equal to -1 denotes an error.</p>
 * <p> There are two memories: one for integer expressions and one
 * for boolean expressions.</p>
 * <p> Expressions are instances of subclasses depending on their kind (plus,
 * minus, greater than,...). Names are prefixed by letters which denote types. For example,
 * {@code BII} or {@code bii} is used for binary operators that provide a boolean
 * expression from two integer expression (i.e. integer comparisons).</p>
 * <p> Notice that, due to implementation choices, primitives that build expressions
 * do not fully preserve original expression structure, especially when using negation
 * ({@code !true} becomes {@code false}, {@code !(a<b)} becomes {@code a>=b},...). The expressions
 * that preserve negation (or negativity) are integer/boolean binary operations (that are
 * not comparison) and variables.</p>
 * <hr>
 * <p> Due to some Java implementation choices around generic classes, the use of {@code instanceof}
 * to recover the precise subclass of an expression is not allowed (casts are allowed). Thus we
 * provide expression types to allow the user to recover this information. As explained above,
 * prefixes of type names denote the input/output type of expressions. Moreover, a {@code _n}
 * suffix denotes a negated (or negative) expression. The types are encoded by numbers in a way
 * that allow to easy recover information about them.</p>
 * <p> Encoding of expression types: considering bits from less significant bit (0) to most significant bit (15)</p>
 * <ul><li> Bit 0 is 1 iff expression type is boolean </li>
 * <li> Bit 1 is 1 iff expression is unary, i.e. negated or negative. </li>
 * <li> Bit 2 is 1 iff expression is binary (may be negated/negative or not...) </li>
 * <li> if expression is binary, then bit 3 is 1 iff arguments are booleans (otherwise they are integers)</li>
 * <li> if expression is not binary, then bit 3 is 1 iff expression is constant (otherwise it is variable)</li>
 * <li> if relevant, other bits provide the index of the associated symbol in the symbol table:
 * <code> symbol = opString[expression.getType()&gt;&gt;&gt;4] </code></li>
 * </ul>
 * <p> Some methods are provided based on this encoding that allow to recover type information without having to
 * do this manually (which of course remains possible)</p>
 * <p><code>iNeg</code> and <code>bNeg</code> are not types but simple symbol indexes (thus they do not contain
 * the 4-bit encoding and do not require the right shift 4).</p>
 * <p> Finally, note that expressions are immutable.</p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
*/

public class IBSStdExpressionClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState> {
    /** table of strings associated to operators and boolean constants */
    public final String[] opString = {"-","!","+","-","*","/","%","&&","||","==","!=","<",">","<=",">=","true","false"};
    /**
     * opposite (unary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opNeg = 0;
    /**
     * negation (unary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opNot = 1;
    /**
     * plus (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opPlus = 2;
    /**
     * minus (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opMinus = 3;
    /**
     * mult (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opMult = 4;
    /**
     * div (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opDiv = 5;
    /**
     * mod (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opMod = 6;
    /**
     * and (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opAnd = 7;
    /**
     * or (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opOr = 8;
    /**
     * equality (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opEq = 9;
    /**
     * difference (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opDif = 10;
    /**
     * "lower than"" (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opLt = 11;
    /**
     * "greater than" (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opGt = 12;
    /**
     * "lower than or equal" (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opLeq = 13;
    /**
     * "greater than or equal" (binary) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opGeq = 14;
    /**
     * true (pseudo) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opTrue = 15;
    /**
     * false (pseudo) operator symbol (index in
     * {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString})
     */
    public final byte opFalse = 16;
    /** greatest allowed operator symbol. must be lower than 128 */
    public final byte opMax = 127;

    /** index of the unary minus symbol in {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString} */
    public final short iNeg = opNeg; // simple symbol index
    /** index of the unary not symbol in {@link myutil.intboolsolver.IBSStdExpressionClass#opString opString} */
    public final short bNot = opNot; // simple symbol index
    /** type of expression. Clear from name... */
    public final short iiiPlus = opPlus<<4 | 0b0100;
    /** type of expression. Clear from name... */
    public final short iiiPlus_n = opPlus<<4 | 0b0110;
    /** type of expression. Clear from name... */
    public final short iiiMinus = opMinus<<4 | 0b0100;
    /** type of expression. Clear from name... */
    public final short iiiMinus_n = opMinus<<4 | 0b0110;
    /** type of expression. Clear from name... */
    public final short iiiMult = opMult<<4 | 0b0100;
    /** type of expression. Clear from name... */
    public final short iiiMult_n = opMult<<4 | 0b0110;
    /** type of expression. Clear from name... */
    public final short iiiDiv = opDiv<<4 | 0b0100;
    /** type of expression. Clear from name... */
    public final short iiiDiv_n = opDiv<<4 | 0b0110;
    /** type of expression. Clear from name... */
    public final short iiiMod = opMod<<4 | 0b0100;
    /** type of expression. Clear from name... */
    public final short iiiMod_n = opMod<<4 | 0b0110;
    /** type of expression. Clear from name... */
    public final short bbbAnd = opAnd<<4 | 0b1101;
    /** type of expression. Clear from name... */
    public final short bbbAnd_n = opAnd<<4 | 0b1111;
    /** type of expression. Clear from name... */
    public final short bbbOr = opOr<<4 | 0b1101;
    /** type of expression. Clear from name... */
    public final short bbbOr_n = opOr<<4 | 0b1111;
    /** type of expression. Clear from name... */
    public final short biiEq = opEq<<4 | 0b0101 ;
    /** type of expression. Clear from name... */
    public final short bbbEq = opEq<<4 | 0b1101;
    /** type of expression. Clear from name... */
    public final short biiDif = opDif<<4 | 0b0101;
    /** type of expression. Clear from name... */
    public final short bbbDif = opDif<<4 | 0b1101;
    /** type of expression. Clear from name... */
    public final short biiLt = opLt<<4 | 0b0101;
    /** type of expression. Clear from name... */
    public final short biiGt = opGt<<4 | 0b0101;
    /** type of expression. Clear from name... */
    public final short biiLeq = opLeq<<4 | 0b0101;
    /** type of expression. Clear from name... */
    public final short biiGeq = opGeq<<4 | 0b0101;
    /** pseudo-type of expression (getType returns {@code bConst}). Clear from name... */
    public final short bTrue = opTrue<<4 | 0b1001;
    /** pseudo-type of expression (getType returns {@code bConst}). Clear from name... */
    public final short bFalse = opFalse<<4 | 0b1001;
    /** type of expression. Clear from name... */
    public final short iVar = (opMax+1)<<4 | 0b0000; // no associated symbol
    /** type of expression. Clear from name... */
    public final short iVar_n = (opMax+1)<<4 | 0b0010; // no associated symbol
    /** type of expression. Clear from name... */
    public final short bVar = (opMax+1)<<4 | 0b0001; // no associated symbol
    /** type of expression. Clear from name... */
    public final short bVar_n = (opMax+1)<<4 | 0b0011; // no associated symbol
    /** type of expression. Clear from name... */
    public final short iConst = (opMax+1)<<4 | 0b1000 ; // no associated symbol
    /** type of expression. Clear from name... */
    public final short bConst = (opMax+1)<<4 | 0b1001 ; // no associated symbol
    /** default (and single) constructor */
    public IBSStdExpressionClass(){}
    /**
     * test if an expression is boolean
     * @param _e the expression to test
     * @return true iff e is boolean (otherwise, integer)
     */
    public final boolean isBool(Expr _e) { return (_e.getType() & 0b1) == 0b1; }
    /**
     * test if expression is build on a unary operator (not or minus)
     * @param _e  the expression to test
     * @return true iff e is build on a unary operator
     */
    public final boolean isUnary(Expr _e) { return (_e.getType() & 0b10) == 0b10; }
    /**
     * test if expression is build on a binary operator (not negated nor negative)
     * @param _e  the expression to test
     * @return true iff e is build on a binary operator
     */
    public final boolean isBinary(Expr _e) { return ((_e.getType() & 0b110) == 0b100); }
    /**
     * test if expression is a constant
     * @param _e  the expression to test
     * @return true iff e is a constant expression
     */
    public final boolean isConst(Expr _e) { return ((_e.getType() & 0b1110) == 0b1000); }
    /**
     * test if expression is a variable
     * @param _e  the expression to test
     * @return true iff e is a variable
     */
    public final boolean isVar(Expr _e) { return ((_e.getType() & 0b1110) == 0b0000); }
    /** get the main operator symbol of an expression if it exists
     * @param _e the expression
     * @return the symbol or -1 if there is no symbol
     */
    public final byte getOpSymbol(Expr _e){
        short type = _e.getType();
        if ((type & 0b11) == 0b11) return opNot;
        if ((type & 0b11) == 0b10) return opNeg;
        if (type >= (opMax+1<<4)) return -1;
        return (byte)(type>>>4);
    }
    /** get the main operator symbol's string of an expression if it exists
     * @param _e the expression
     * @return the operator's string or null if there is no symbol
     */
    public final String getOpString(Expr _e){
        byte symbol = getOpSymbol(_e);
        if (symbol < 0) return null;
        return opString[symbol];
    }
    /** get the unique subexpression of an unary integer expression.
     * @param _e the expression
     * @return its subexpression, or null if e is not unary
     */
    public final IExpr getArg(IExpr _e){ return ( isUnary(_e)?_e.negate():null); }
    /** get the unique subexpression of an unary boolean expression.
     * @param _e the expression
     * @return its subexpression, or null if e is not unary
     */
    public final BExpr getArg(BExpr _e){ return ( isUnary(_e)?_e.negate():null); }
    /** get the left subexpression of an integer binary operator expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    public final IExpr getLeftArg(IIIBinOp _e){ return ( isBinary(_e)?_e.left:null); }
    /** get the right subexpression of an integer binary operator expression.
     * @param _e the expression
     * @return its right subexpression, or null if e is not binary
     */
    public final IExpr getRightArg(IIIBinOp _e){ return ( isBinary(_e)?_e.right:null); }
    /** get the left subexpression of an integer comparison expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    public final IExpr getLeftArg(BIIBinOp _e){ return ( isBinary(_e)?_e.left:null); }
    /** get the right subexpression of an integer comparison expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    public final IExpr getRightArg(BIIBinOp _e){ return ( isBinary(_e)?_e.right:null); }
    /** get the right subexpression of a boolean binary operator expression.
     * @param _e the expression
     * @return its left subexpression, or null if e is not binary
     */
    public final BExpr getLeftArg(BBBBinOp _e){ return ( isBinary(_e)?_e.left:null); }
    /** get the right subexpression of a boolean binary operator expression.
     * @param _e the expression
     * @return its right subexpression, or null if e is not binary
     */
    public final BExpr getRightArg(BBBBinOp _e){ return ( isBinary(_e)?_e.right:null); }

    private final ArrayList<IExpr> iExpressions = new ArrayList<IExpr>(16);
    private final ArrayList<BExpr> bExpressions = new ArrayList<BExpr>(16);

    /**
     * clear the internal memories containing build integer and boolean expressions
     */
    public void clear(){
        iExpressions.clear();
        bExpressions.clear();
    }
    private int findIfree(){
        int i;
        for (i = 0; i < iExpressions.size(); i++) if (iExpressions.get(i)==null) break;
        if (i==iExpressions.size()){
            iExpressions.add(null);
        }
        return i;
    }
    private int findBfree(){
        int i;
        for (i = 0; i < bExpressions.size(); i++) if (bExpressions.get(i)==null) break;
        if (i==bExpressions.size()){
            bExpressions.add(null);
        }
        return i;
    }

    /** deletes a memorized integer expression, making its index free
     * @param _toFree the index to free
     */
    public void freeInt(int _toFree) {
        iExpressions.set(_toFree,null);
    }
    /** deletes a memorized boolean expression, making its index free
     * @param _toFree the index to free
     */
    public void freeBool(int _toFree) {
        bExpressions.set(_toFree,null);
    }

    /** get the integer expression memorized at some index
     * @param _i the index
     * @return the requested expression (may be null)
     */
    public IExpr getIExpr(int _i) {
        if (_i >= iExpressions.size() || _i < 0)
            return null;
        else
            return iExpressions.get(_i);
    }
    /** get the boolean expression memorized at some index
     * @param _i the index
     * @return the requested expression (may be null)
     */
    public BExpr getBExpr(int _i) {
        if (_i >= bExpressions.size() || _i < 0)
            return null;
        else
            return bExpressions.get(_i);
    }
    /** put a boolean expression in internal memory
     *
     * @param _expr the expression
     * @return The index at which the expression is  memorized.
     */
    public int putBExpr(BExpr _expr) {
        int tgt = findBfree();
        bExpressions.set(tgt, _expr);
        return tgt;
    }
    /** put an integer expression in internal memory
     *
     * @param _expr the expression
     * @return The index at which the expression is  memorized.
     */
    public int putIExpr(IExpr _expr) {
        int tgt = findIfree();
        iExpressions.set(tgt, _expr);
        return tgt;
    }
    /** test if a memorized boolean expression is constant
     * @param _i index of a (not null) boolean expression
     * @return true iff boolean expression at index _i is constant
     */
    public boolean isBconstant(int _i){ return bExpressions.get(_i).getType()==bConst; }
    /** test if a memorized integer expression is constant
     * @param _i index of a (not null) boolean expression
     * @return true iff boolean expression at index _i is constant
     */
    public boolean isIconstant(int _i){ return iExpressions.get(_i).getType()==iConst; }
    /** binary Plus expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_iiiPlus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
           throw new Error("IBSStdExpressionClass.make_iiiPlus called on undefined subexpression");
        iExpressions.set(tgt, new IIIPlus(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** binary Minus expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_iiiMinus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMinus called on undefined subexpression");
        iExpressions.set(tgt, new IIIMinus(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** binary Mult expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_iiiMult(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMult called on undefined subexpression");
        iExpressions.set(tgt, new IIIMult(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** binary Div expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_iiiDiv(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiDiv called on undefined subexpression");
        iExpressions.set(tgt, new IIIDiv(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** binary Modulus expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_iiiMod(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMod called on undefined subexpression");
        iExpressions.set(tgt, new IIIMod(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** binary And expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_bbbAnd(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbAnd called on undefined subexpression");
        bExpressions.set(tgt, new BBBAnd(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    /** binary Or expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_bbbOr(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbOr called on undefined subexpression");
        bExpressions.set(tgt, new BBBOr(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    /** integer equality expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_biiEq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                    iExpressions.get(_left) == null || iExpressions.get(_right) == null)
                throw new Error("IBSStdExpressionClass.make_biiEq called on undefined subexpression");
        bExpressions.set(tgt, new BIIEq(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** boolean equality expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_bbbEq(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbEq called on undefined subexpression");
        bExpressions.set(tgt, new BBBEq(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    /** integer difference expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_biiDif(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiDif called on undefined subexpression");
        bExpressions.set(tgt, new BIIDif(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** boolean difference expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_bbbDif(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbDif called on undefined subexpression");
        bExpressions.set(tgt, new BBBDif(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    /** integer "lower than" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_biiLt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiLt called on undefined subexpression");
        bExpressions.set(tgt, new BIILt(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** integer "greater than" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_biiGt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiGt called on undefined subexpression");
        bExpressions.set(tgt, new BIIGt(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** integer "lower than or equal" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_biiLeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiLeq called on undefined subexpression");
        bExpressions.set(tgt, new BIILeq(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** integer "greater than or equal" expression constructor
     * @param _left index of left subexpression
     * @param _right index of right subexpression
     * @return index of the build expression
     */
    public int make_biiGeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiGeq called on undefined subexpression");
        bExpressions.set(tgt, new BIIGeq(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    /** integer variable expression constructor
     * @param _v attribute defining the variable
     * @return index of the build expression
     */
    public int make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findIfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_iVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        iExpressions.set(tgt, new IVar(_v));
        return tgt;
    }
    /** boolean variable expression constructor
     * @param _v attribute defining the variable
     * @return index of the build expression
     */
    public int make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findBfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_bVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.BoolAttr)
            return -1;
        bExpressions.set(tgt, new BVar(_v));
        return tgt;
    }
    /** integer constant expression constructor
     * @param _i the value of the constant
     * @return index of the build expression
     */
    public int make_iConst(int _i) {
        int tgt = findIfree();
        iExpressions.set(tgt, new IConst(_i));
        return tgt;
    }
    /** boolean constant expression constructor
     * @param _b the value of the constant
     * @return index of the build expression
     */
    public int make_bConst(boolean _b) {
        int tgt = findBfree();
        bExpressions.set(tgt, new BConst(_b));
        return tgt;
    }
    /** build an expression denoting the opposite of the provided integer expression
     * @param _expr the expression from which the opposite is requested
     * @return index of the build expression
     */
    public int make_iNeg(int _expr) {
        int tgt = findIfree();
        if (iExpressions.size()<=_expr || _expr < 0 ||iExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_iNeg called on undefined subexpression");
        iExpressions.set(tgt, iExpressions.get(_expr).negate());
        return tgt;
    }
    /** build an expression denoting the negation of the provided boolean expression
     * @param _expr the expression from which the negation is requested
     * @return index of the build expression
     */
    public int make_bNot(int _expr) {
        int tgt = findBfree();
        if (bExpressions.size()<=_expr || _expr < 0 || bExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_bNot called on undefined subexpression");
        bExpressions.set(tgt, bExpressions.get(_expr).negate());
        return tgt;
    }
    /** Integer expressions */
    public abstract class IExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr {
        public abstract IExpr negate();
    }
    /** Boolean expressions */
    public abstract class BExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr {
        public abstract BExpr negate();
    }
    /** Integer binary expressions (and opposites of such expressions) */
    public abstract class IIIBinOp extends IExpr{
        protected final IExpr left;
        protected final IExpr right;
        protected final boolean isNeg;
        public IIIBinOp(IExpr _l, IExpr _r, boolean _isNeg){
            left  = _l;
            right = _r;
            isNeg = _isNeg;
        }
        public final boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public final void linkStates(){
            left.linkStates();
            right.linkStates();
        }
        public final void linkComps(Spec _spec) {
            left.linkComps(_spec);
            right.linkComps(_spec);
        }
    }
    /** Integer comparison expressions (and negations of such expressions)*/
    public abstract class BIIBinOp extends BExpr{
        protected final IExpr left;
        protected final IExpr right;
        public BIIBinOp(IExpr _l, IExpr _r){
            left  = _l;
            right = _r;
        }
        public final boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public final void linkStates(){
            left.linkStates();
            right.linkStates();
        }
        public final void linkComps(Spec _spec) {
            left.linkComps(_spec);
            right.linkComps(_spec);
        }
    }
    /** Boolean binary expressions (and negations of such expressions, except for comparisons)*/
    public abstract class BBBBinOp extends BExpr{
        protected final BExpr left;
        protected final BExpr right;
        public BBBBinOp(BExpr _l, BExpr _r){
            left  = _l;
            right = _r;
        }
        public final boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public final void linkStates(){
            left.linkStates();
            right.linkStates();
        }
        public final void linkComps(Spec _spec) {
            left.linkComps(_spec);
            right.linkComps(_spec);
        }
    }
    /** Integer constant expressions */
    public final class IConst extends IExpr {
        private final int constant;
        private IConst(int _i){ constant = _i; type = iConst;}
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _e the expression from which the opposite is required
         */
        public IConst(IConst _e){
            constant = -_e.constant;
            type = iConst;
        }
        public final IConst negate(){
            return new IConst(this);
        }
        public final int eval() { return constant; }
        public final int eval(SpecState _ss) { return constant; }
        public final int eval(SpecState _ss, State _st) { return constant; }
        public final int eval(CompState _cs) { return constant; }
        public final int eval(Object _qs) { return constant; }
        public final String toString() { return ""+constant; }
        public final boolean hasStates() { return false; }
        public final void linkStates() {}
        public final void linkComps(Spec _spec) {}
    }
    /** Boolean constant expressions */
    public final class BConst extends BExpr {
        private final boolean constant;
        public BConst(boolean _b){ constant = _b; type = bConst;}
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _b the expression from which the negation is required
         */
        public BConst(BConst _b){
            constant = !_b.constant;
            type = bConst;
        }
        public final BConst negate() {
            return new BConst(this);
        }
        public final boolean eval() { return constant; }
        public final boolean eval(SpecState _ss) { return constant; }
        public final boolean eval(SpecState _ss, State _st) { return constant; }
        public final boolean eval(CompState _cs) { return constant; }
        public final boolean eval(Object _qs) { return constant; }
        public final String toString() { return (constant ? opString[bTrue>>>4] : opString[bFalse>>>4]); }
        public final boolean hasStates() { return false; }
        public final void linkStates() {}
        public final void linkComps(Spec _spec) {}
    }
    /** integer variable expressions  (and opposites of such expressions) */
    public final class IVar extends IExpr {
        private final boolean isNeg;
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        public IVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v){
            var = _v;
            isNeg=false;
            type = iVar;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _v the expression from which the opposite is required
         */
        private IVar(IVar _v){
            var = _v.var;
            isNeg = !_v.isNeg;
            type = (isNeg?iVar_n:iVar);
        }
        public IVar negate(){
            return new IVar(this);
        }
        public final int eval(){ return 0; }
        public final int eval(SpecState _ss) { return (isNeg?-var.getValue(_ss):var.getValue(_ss)); }
        public final int eval(SpecState _ss, State _st) { return (isNeg?-var.getValue(_ss,_st):var.getValue(_ss,_st)); }
        public final int eval(CompState _cs) { return (isNeg?-var.getValue(_cs):var.getValue(_cs)); }
        public final int eval(Object _qs) { return (isNeg?-var.getValue(_qs):var.getValue(_qs)); }
        public final String toString() {
            if (isNeg)
                return opString[iNeg] + "(" + var.toString() + ")";
            return var.toString();
        }
        public final boolean hasStates() { return false; }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    /** Boolean variable expressions */
    public final class BVar extends BExpr {
        private final boolean isNot;
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private BVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
            var = _v;
            isNot=false;
            type = bVar;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _b the expression from which the negation is required
         */
        public BVar(BVar _b){
            isNot = !_b.isNot;
            var = _b.var;
            type = (isNot?bVar_n:bVar);
        }
        public BVar negate(){ return new BVar(this);}
        public final boolean eval(){ return false; }
        public final boolean eval(SpecState _ss) {
            return (isNot == (var.getValue(_ss) == 0));
        }
        public final boolean eval(SpecState _ss, State _st) {
            return (isNot == (var.getValue(_ss, _st) == 0));
        }
        public final boolean eval(CompState _cs) {
            return (isNot == (var.getValue(_cs) == 0));
        }
        public final boolean eval(Object _qs) {
            return (isNot == (var.getValue(_qs) == 0));
        }
        public final String toString() {
            if (isNot)
                return opString[bNot] + "(" + var.toString() + ")";
            return var.toString();
        }
        public final boolean hasStates() { return var.isState(); }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    /** Integer binary Plus expressions  (and opposites of such expressions) */
    public final class IIIPlus extends IIIBinOp {
        public IIIPlus(IExpr _l, IExpr _r) {
            super(_l, _r,false);
            type = iiiPlus;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _p the expression from which the opposite is required
         */
        public IIIPlus(IIIPlus _p){
            super(_p.left, _p.right,!_p.isNeg);
            type = (isNeg?iiiPlus_n:iiiPlus);
        }
        public final IIIPlus negate(){ return new IIIPlus(this); }
        public final int eval() { return (isNeg?-(left.eval() + right.eval()):(left.eval() + right.eval())); }
        public final int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) + right.eval(_ss)):(left.eval(_ss) + right.eval(_ss)));
        }
        public final int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) + right.eval(_ss,_st)):(left.eval(_ss,_st) + right.eval(_ss,_st)));
        }
        public final int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) + right.eval(_cs)):(left.eval(_cs) + right.eval(_cs)));
        }
        public final int eval(Object _qs){ return (isNeg?-(left.eval(_qs) + right.eval(_qs)):(left.eval(_qs) + right.eval(_qs))); }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiPlus>>>4] + "(" +  right.toString() + ")";
            return  (isNeg? opString[iNeg] + "(" + s + ")" : s);
        }
    }
    /** Integer binary Minus expressions  (and opposites of such expressions) */
    public final class IIIMinus extends IIIBinOp {
        public IIIMinus(IExpr _l, IExpr _r) {
            super(_l, _r,false);
            type = iiiMinus;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _p the expression from which the opposite is required
         */
        public IIIMinus(IIIMinus _p){
            super(_p.left, _p.right,!_p.isNeg);
            type = (isNeg?iiiMinus_n:iiiMinus);
        }
        public final IIIMinus negate(){ return new IIIMinus(this); }
        public final int eval() { return (isNeg?-(left.eval() - right.eval()):(left.eval() - right.eval())); }
        public final int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) - right.eval(_ss)):(left.eval(_ss) - right.eval(_ss)));
        }
        public final int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) - right.eval(_ss,_st)):(left.eval(_ss,_st) - right.eval(_ss,_st)));
        }
        public final int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) - right.eval(_cs)):(left.eval(_cs) - right.eval(_cs)));
        }
        public final int eval(Object _qs){
            return (isNeg?-(left.eval(_qs) - right.eval(_qs)):(left.eval(_qs) - right.eval(_qs)));
        }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiMinus>>>4] + "(" +  right.toString() + ")";
            return  (isNeg? opString[iNeg] + "(" + s + ")" : s);
        }
    }
    /** Integer binary Mult expressions  (and opposites of such expressions) */
    public final class IIIMult extends IIIBinOp {
        public IIIMult(IExpr _l, IExpr _r) {
            super(_l, _r,false);
            type = iiiMult;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _p the expression from which the opposite is required
         */
        public IIIMult(IIIMult _p){
            super(_p.left, _p.right,!_p.isNeg);
            type = (isNeg?iiiMult_n:iiiMult);
        }
        public final IIIMult negate(){ return new IIIMult(this); }
        public final int eval() { return (isNeg?-(left.eval() * right.eval()):(left.eval() * right.eval())); }
        public final int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) * right.eval(_ss)):(left.eval(_ss) * right.eval(_ss)));
        }
        public final int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) * right.eval(_ss,_st)):(left.eval(_ss,_st) * right.eval(_ss,_st)));
        }
        public final int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) * right.eval(_cs)):(left.eval(_cs) * right.eval(_cs)));
        }
        public final int eval(Object _qs){
            return (isNeg?-(left.eval(_qs) * right.eval(_qs)):(left.eval(_qs) * right.eval(_qs)));
        }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiMult>>>4] + "(" +  right.toString() + ")";
            return  (isNeg? opString[iNeg] + "(" + s + ")" : s);
        }
    }
    /** Integer binary Div expressions  (and opposites of such expressions) */
    public final class IIIDiv extends IIIBinOp {
        public IIIDiv(IExpr _l, IExpr _r) {
            super(_l, _r,false);
            type = iiiDiv;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _p the expression from which the opposite is required
         */
        public IIIDiv(IIIDiv _p){
            super(_p.left, _p.right,!_p.isNeg);
            type = (isNeg?iiiDiv_n:iiiDiv);
        }
        public final IIIDiv negate(){ return new IIIDiv(this); }
        public final int eval() { return (isNeg?-(left.eval() / right.eval()):(left.eval() / right.eval())); }
        public final int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) / right.eval(_ss)):(left.eval(_ss) / right.eval(_ss)));
        }
        public final int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) / right.eval(_ss,_st)):(left.eval(_ss,_st) / right.eval(_ss,_st)));
        }
        public final int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) / right.eval(_cs)):(left.eval(_cs) / right.eval(_cs)));
        }
        public final int eval(Object _qs){
            return (isNeg?-(left.eval(_qs) / right.eval(_qs)):(left.eval(_qs) / right.eval(_qs)));
        }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiDiv>>>4] + "(" +  right.toString() + ")";
            return  (isNeg? opString[iNeg] + "(" + s + ")" : s);
        }
    }
    /** Integer binary Modulus expressions  (and opposites of such expressions) */
    public final class IIIMod extends IIIBinOp {
        public IIIMod(IExpr _l, IExpr _r) {
            super(_l, _r,false);
            type = iiiMod;
        }
        /**
         * Constructor for implementing negate. The build expression
         * is the opposite of the parameter.
         * @param _p the expression from which the opposite is required
         */
        public IIIMod(IIIMod _p){
            super(_p.left, _p.right,!_p.isNeg);
            type = (isNeg?iiiMod_n:iiiMod);
        }
        public final IIIMod negate(){ return new IIIMod(this); }
        public final int eval() { return (isNeg?-(left.eval() % right.eval()):(left.eval() % right.eval())); }
        public final int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) % right.eval(_ss)):(left.eval(_ss) % right.eval(_ss)));
        }
        public final int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) % right.eval(_ss,_st)):(left.eval(_ss,_st) % right.eval(_ss,_st)));
        }
        public final int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) % right.eval(_cs)):(left.eval(_cs) % right.eval(_cs)));
        }
        public final int eval(Object _qs){
            return (isNeg?-(left.eval(_qs) % right.eval(_qs)):(left.eval(_qs) % right.eval(_qs)));
        }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiMod>>>4] + "(" +  right.toString() + ")";
            return  (isNeg? opString[iNeg] + "(" + s + ")" : s);
        }
    }
    /** Boolean binary And expressions (and negations of such expressions)*/
    public final class BBBAnd extends BBBBinOp {
        private final boolean isNot;
        public BBBAnd(BExpr _l, BExpr _r) { super(_l, _r); isNot=false; type = bbbAnd;}
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BBBAnd(BBBAnd _p){
            super(_p.left, _p.right);
            isNot = !_p.isNot;
            type = (isNot?bbbAnd_n:bbbAnd);
        }
        public final BBBAnd negate(){ return new BBBAnd(this); }
        public final boolean eval() {
            boolean b = left.eval() && right.eval();
            return isNot != b;
        }
        public final boolean eval(SpecState _ss) {
            boolean b = left.eval(_ss) && right.eval(_ss);
            return isNot != b;
        }
        public final boolean eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) && right.eval(_ss,_st);
            return isNot != b;
        }
        public final boolean eval(CompState _cs) {
            boolean b = left.eval(_cs) && right.eval(_cs);
            return isNot != b;
        }
        public final boolean eval(Object _qs) {
            boolean b = left.eval(_qs) && right.eval(_qs);
            return isNot != b;
        }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[bbbAnd>>>4] + "(" +  right.toString() + ")";
            return  (isNot? opString[bNot] + "(" + s + ")" : s);
        }
    }
    /** Boolean binary Or expressions (and negations of such expressions)*/
    public final class BBBOr extends BBBBinOp {
        private final boolean isNot;
        public BBBOr(BExpr _l, BExpr _r){ super(_l, _r); isNot=false; type = bbbOr;}
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BBBOr(BBBOr _p){
            super(_p.left, _p.right);
            isNot = !_p.isNot;
            type = (isNot?bbbOr_n:bbbOr);
        }
        public final BBBOr negate(){ return new BBBOr(this); }
        public final boolean eval() {
            boolean b = left.eval() || right.eval();
            return isNot != b;
        }
        public final boolean eval(SpecState _ss) {
            boolean b = left.eval(_ss) || right.eval(_ss);
            return isNot != b;
        }
        public final boolean eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) || right.eval(_ss,_st);
            return isNot != b;
        }
        public final boolean eval(CompState _cs) {
            boolean b = left.eval(_cs) || right.eval(_cs);
            return isNot != b;
        }
        public final boolean eval(Object _qs) {
            boolean b = left.eval(_qs) || right.eval(_qs);
            return isNot != b;
        }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[bbbOr>>>4] + "(" +  right.toString() + ")";
            return  (isNot? opString[bNot] + "(" + s + ")" : s);
        }
    }
    /** Integer Equality expressions */
    public final class BIIEq extends BIIBinOp {
        public BIIEq(IExpr _l, IExpr _r){ super(_l, _r); type = biiEq;}
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BIIEq(BIIDif _p){
            super(_p.left, _p.right);
            type = biiEq;
        }
        public final BIIDif negate(){ return new BIIDif(this); }
        public final boolean eval() { return left.eval() == right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) == right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) == right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) == right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) == right.eval(_qs); }
        public final String toString() {
            return  "(" + left.toString() + ")" + opString[biiEq>>>4] + " (" + right.toString() + ")";
        }
    }
    /** Boolean Equality expressions */
    public final class BBBEq extends BBBBinOp {
        public BBBEq(BExpr _l, BExpr _r){ super(_l, _r); type = bbbEq; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BBBEq(BBBDif _p){ super(_p.left, _p.right); type = bbbEq; }
        public final BBBDif negate(){ return new BBBDif(this); }
        public final boolean eval() { return left.eval() == right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) == right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) == right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) == right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) == right.eval(_qs); }
        public final String toString() {
            return  "(" + left.toString() + ")" + opString[bbbEq>>>4] + " (" + right.toString() + ")";
        }
    }
    /** Integer Difference expressions */
    public final class BIIDif extends BIIBinOp {
        public BIIDif(IExpr _l, IExpr _r){ super(_l, _r); type = biiDif; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BIIDif(BIIEq _p){
            super(_p.left, _p.right);
            type = biiDif;
        }
        public final BIIEq negate(){ return new BIIEq(this); }
        public final boolean eval() { return left.eval() != right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) != right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) != right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) != right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) != right.eval(_qs); }
        public final String toString() {
            return  "(" + left.toString() + ")" + opString[biiDif>>>4] + " (" + right.toString() + ")";
        }
    }
    /** Boolean Difference expressions */
    public final class BBBDif extends BBBBinOp {
        public BBBDif(BExpr _l, BExpr _r){ super(_l, _r); type = bbbDif; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BBBDif(BBBEq _p){ super(_p.left, _p.right); type = bbbDif; }
        public final BBBEq negate(){ return new BBBEq(this); }
        public final boolean eval() { return left.eval() != right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) != right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) != right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) != right.eval(_cs); }
        public final boolean eval(Object _qs) {
            return left.eval(_qs) != right.eval(_qs);
        }
        public final String toString() {
            return "(" + left.toString() + ")" + opString[bbbDif>>>4] + " (" + right.toString() + ")";
        }
    }
    /** Integer "Lower Than" expressions */
    public final class BIILt extends BIIBinOp {
        public BIILt(IExpr _l, IExpr _r) { super(_l, _r); type = biiLt; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BIILt(BIIGeq _p){ super(_p.left, _p.right); type = biiLt; }
        public final BIIGeq negate(){ return new BIIGeq(this); }
        public final boolean eval() { return  left.eval() < right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) < right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) < right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) < right.eval(_cs); }
        public final boolean eval(Object _qs) {
            return left.eval(_qs) < right.eval(_qs);
        }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return  "(" +l + ")" + opString[biiLt>>>4] + "(" + r + ")";
        }
    }
    /** Integer "Greater Than" expressions */
    public final class BIIGt extends BIIBinOp {
        public BIIGt(IExpr _l, IExpr _r) { super(_l, _r); type = biiGt; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BIIGt(BIILeq _p){ super(_p.left, _p.right); type = biiGt; }
        public final BIILeq negate(){ return new BIILeq(this); }
        public final boolean eval() { return left.eval() > right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) > right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) > right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) > right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) > right.eval(_qs); }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return   "(" +l + ")" + opString[biiGt>>>4] + "(" + r + ")";
        }
    }
    /** Integer "Lower Than or Equal" expressions */
    public final class BIILeq extends BIIBinOp {
        public BIILeq(IExpr _l, IExpr _r){ super(_l, _r); type = biiLeq; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BIILeq(BIIGt _p){ super(_p.left, _p.right); type = biiLeq; }
        public final BIIGt negate(){ return new BIIGt(this); }
        public final boolean eval() { return left.eval() <= right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) <= right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) <= right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) <= right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) <= right.eval(_qs); }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return   "(" +l + ")" + opString[biiLeq>>>4] + "(" + r + ")";
        }
    }
    /** Integer "Greater Than or Equal" expressions */
    public final class BIIGeq extends BIIBinOp {
        public BIIGeq(IExpr _l, IExpr _r) { super(_l, _r); type = biiGeq; }
        /**
         * Constructor for implementing negate. The build expression
         * is the negation of the parameter.
         * @param _p the expression from which the negation is required
         */
        public BIIGeq(BIILt _p){ super(_p.left, _p.right); type = biiGeq; }
        public final BIILt negate(){ return new BIILt(this); }
        public final boolean eval() { return left.eval() >= right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) >= right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) >= right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) >= right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) >= right.eval(_qs); }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return   "(" +l + ")" + opString[biiGeq>>>4] + "(" + r + ")";
        }
    }
}
