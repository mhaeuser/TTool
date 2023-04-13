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

package myutil.intboolsolver.history;

import myutil.intboolsolver.*;

import java.util.ArrayList;

/** Attempt to optimize expressions for the IBS System.
 *
 * <p> This file is kept for further investigations. Although
 * this implementation suppress some tests, it does not seem to
 * be more efficient (perhaps a bit less...) </p>
 *
 * @version 1.0 13/04/2023
 * @author Sophie Coudert
*/

public class IBSOptExpressionClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState> {
    private final int[] prios =       { 2 , 2 , 1 , 1 , 1 , 1  ,  2 ,  3 ,  3 ,  3 ,  3 , 3 , 3 ,  3 ,  3 };
    private final String[] opString = {"+","-","*","/","%","&&","||","==","==","!=","!=","<",">","<=",">=","-","!","true","false"};
    private final short iiiPlus = 0;
    private final short iiiMinus = 1;
    private final short iiiMult = 2;
    private final short iiiDiv = 3;
    private final short iiiMod = 4;
    private final short bbbAnd = 5;
    private final short bbbOr = 6;
    private final short biiEq = 7;
    private final short bbbEq = 8;
    private final short biiDif = 9;
    private final short bbbDif = 10;
    private final short biiLt = 11;
    private final short biiGt = 12;
    private final short biiLeq = 13;
    private final short biiGeq = 14;
    private final short iNeg = 15;
    private final short bNot = 16;
    private final short btrue = 17;
    private final short bfalse = 18;
    private final short iVar = 100; // no associated symbol
    private final short bVar = 101; // no associated symbol
    private final short biVar = 102; // no associated symbol
    private final short iConst = 103; // no associated symbol
    private final short bConst = 104; // no associated symbol
    private final short biExpr = 105; // no associated symbol
    private final short neg_mask = (short) 0x8000;

    private final ArrayList<IExpr> iExpressions = new ArrayList<IExpr>(16);
    private final ArrayList<BExpr> bExpressions = new ArrayList<BExpr>(16);
    public IBSOptExpressionClass(){}
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
    public void freeInt(int _toFree) {
        iExpressions.set(_toFree,null);
    }
    public void freeBool(int _toFree) {
        bExpressions.set(_toFree,null);
    }
    public IExpr getIExpr(int _expr) {
        if (_expr >= iExpressions.size() || _expr < 0)
            throw new Error("IBSStdExpressionClass.getIExpr: out of bounds");
        else
            return iExpressions.get(_expr);
    }
    public BExpr getBExpr(int _expr) {
        if (_expr >= bExpressions.size() || _expr < 0)
            throw new Error("IBSStdExpressionClass.getBExpr: out of bounds");
        else
            return bExpressions.get(_expr);
    }
    public boolean isBconstant(int i){ return bExpressions.get(i).getType()==bConst; }
    public boolean isIconstant(int i){ return iExpressions.get(i).getType()==iConst; }
    public int make_iiiPlus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
           throw new Error("IBSStdExpressionClass.make_iiiPlus called on undefined subexpression");
        iExpressions.set(tgt, new IIIPlus(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_iiiMinus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMinus called on undefined subexpression");
        iExpressions.set(tgt, new IIIMinus(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_iiiMult(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMult called on undefined subexpression");
        iExpressions.set(tgt, new IIIMult(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_iiiDiv(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiDiv called on undefined subexpression");
        iExpressions.set(tgt, new IIIDiv(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_iiiMod(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMod called on undefined subexpression");
        iExpressions.set(tgt, new IIIMod(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_bbbAnd(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbAnd called on undefined subexpression");
        bExpressions.set(tgt, new BBBAnd(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    public int make_bbbOr(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbOr called on undefined subexpression");
        bExpressions.set(tgt, new BBBOr(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    public int make_biiEq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                    iExpressions.get(_left) == null || iExpressions.get(_right) == null)
                throw new Error("IBSStdExpressionClass.make_biiEq called on undefined subexpression");
        bExpressions.set(tgt, new BIIEq(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_bbbEq(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbEq called on undefined subexpression");
        bExpressions.set(tgt, new BBBEq(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    public int make_biiDif(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiDif called on undefined subexpression");
        bExpressions.set(tgt, new BIIDif(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_bbbDif(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbDif called on undefined subexpression");
        bExpressions.set(tgt, new BBBDif(bExpressions.get(_left), bExpressions.get(_right)));
        return tgt;
    }
    public int make_biiLt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiLt called on undefined subexpression");
        bExpressions.set(tgt, new BIILt(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_biiGt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiGt called on undefined subexpression");
        bExpressions.set(tgt, new BIIGt(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_biiLeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiLeq called on undefined subexpression");
        bExpressions.set(tgt, new BIILeq(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_biiGeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiGeq called on undefined subexpression");
        bExpressions.set(tgt, new BIIGeq(iExpressions.get(_left), iExpressions.get(_right)));
        return tgt;
    }
    public int make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findIfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_iVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        iExpressions.set(tgt, new IVar(_v));
        return tgt;
    }
    public int make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findBfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_bVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.BoolAttr)
            return -1;
        bExpressions.set(tgt, new BVar(_v));
        return tgt;
    }
    public int make_biVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findBfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_biVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        bExpressions.set(tgt, new BIVar(_v));
        return tgt;
    }
    public int make_iConst(int _i) {
        int tgt = findIfree();
        iExpressions.set(tgt, new IConst(_i));
        return tgt;
    }
    public int make_bConst(boolean _b) {
        int tgt = findBfree();
        if(_b)
            bExpressions.set(tgt, bTrue);
        else
            bExpressions.set(tgt, bFalse);
        return tgt;
    }
    public int make_iNeg(int _expr) {
        int tgt = findIfree();
        if (iExpressions.size()<=_expr || _expr < 0 ||iExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_iNeg called on undefined subexpression");
        iExpressions.set(tgt, iExpressions.get(_expr).negate());
        return tgt;
    }
    public int make_bNot(int _expr) {
        int tgt = findBfree();
        if (bExpressions.size()<=_expr || _expr < 0 || bExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_bNot called on undefined subexpression");
        bExpressions.set(tgt, bExpressions.get(_expr).negate());
        return tgt;
    }
    public int make_biExpr(int _expr) {
        int tgt = findBfree();
        if (iExpressions.size()<=_expr || _expr < 0 || iExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_biExpr called on undefined subexpression");
        bExpressions.set(tgt, new BIExpr(iExpressions.get(_expr)));
        return tgt;
    }
    private final BExpr bTrue = new BConst(true);
    private final BExpr bFalse = new BConst(false);
    public abstract class IExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr {
        protected short type;
        public final int getType() { return type; }
        public abstract int getPrio();
        public abstract IExpr negate();
    }
    public abstract class BExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr {
        protected short type;
        public final int getType() { return type; }
        public abstract int getPrio();
        public abstract BExpr negate();
    }
    public abstract class IIIBinOp extends IExpr{
        protected final IExpr left;
        protected final IExpr right;
        public IIIBinOp(IExpr _l, IExpr _r){ left  = _l; right = _r; }
        public final boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public final void linkStates(){ left.linkStates(); right.linkStates(); }
        public final void linkComps(Spec _spec) { left.linkComps(_spec); right.linkComps(_spec); }
    }
    public abstract class BIIBinOp extends BExpr{
        protected final IExpr left;
        protected final IExpr right;
        public BIIBinOp(IExpr _l, IExpr _r){ left  = _l; right = _r; }
        public final boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public final void linkStates(){ left.linkStates(); right.linkStates(); }
        public final void linkComps(Spec _spec) { left.linkComps(_spec); right.linkComps(_spec); }
    }
    public abstract class BBBBinOp extends BExpr{
        protected final BExpr left;
        protected final BExpr right;
        public BBBBinOp(BExpr _l, BExpr _r){ left  = _l; right = _r; }
        public final boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public final void linkStates(){ left.linkStates(); right.linkStates(); }
        public final void linkComps(Spec _spec) { left.linkComps(_spec); right.linkComps(_spec); }
    }
    public final class IConst extends IExpr {
        private final int constant;
        private IConst(int _i){ constant = _i; type = iConst;}
        private IConst(IConst _e){ constant = -_e.constant; type = iConst; }
        public final IConst negate(){
            return new IConst(this);
        }
        public final int getPrio(){ return 0; }
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
    public final class BConst extends BExpr {
        private final boolean constant;
        public BConst(boolean _b){ constant = _b; type = bConst;}
        public BConst(BConst _b){ constant = !_b.constant; type = bConst; }
        public final BConst negate() {
            return new BConst(this);
        }
        public final int getPrio(){ return 0; }
        public final boolean eval() { return constant; }
        public final boolean eval(SpecState _ss) { return constant; }
        public final boolean eval(SpecState _ss, State _st) { return constant; }
        public final boolean eval(CompState _cs) { return constant; }
        public final boolean eval(Object _qs) { return constant; }
        public final String toString() { return (constant ? opString[btrue] : opString[bfalse]); }
        public final boolean hasStates() { return false; }
        public final void linkStates() {}
        public final void linkComps(Spec _spec) {}
    }
    public final class IVar extends IExpr {
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private IVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; type = iVar; }
        private IVar(IVar_n _v){ var = _v.var; type = iVar; }
        public IVar_n negate(){
            return new IVar_n(this);
        }
        public final int getPrio(){ return 0; }
        public final int eval(){ return 0; }
        public final int eval(SpecState _ss) { return var.getValue(_ss); }
        public final int eval(SpecState _ss, State _st) { return var.getValue(_ss,_st); }
        public final int eval(CompState _cs) { return var.getValue(_cs); }
        public final int eval(Object _qs) { return var.getValue(_qs); }
        public final String toString() { return var.toString(); }
        public final boolean hasStates() { return false; }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public final class IVar_n extends IExpr {
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        //private IVar_n(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; type = iVar | neg_mask; }
        private IVar_n(IVar _v) { var = _v.var; type = iVar | neg_mask; }
        public IVar negate(){  return new IVar(this); }
        public final int getPrio(){ return 0; }
        public final int eval(){ return 0; }
        public final int eval(SpecState _ss) { return -var.getValue(_ss); }
        public final int eval(SpecState _ss, State _st) { return -var.getValue(_ss,_st); }
        public final int eval(CompState _cs) { return -var.getValue(_cs); }
        public final int eval(Object _qs) { return -var.getValue(_qs); }
        public final String toString() { return  opString[iNeg] + "(" + var.toString() + ")"; }
        public final boolean hasStates() { return false; }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public final class BVar extends BExpr {
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private BVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; type = bVar; }
        private BVar(BVar_n _b){ var = _b.var; type = bVar; }
        public BVar_n negate(){ return new BVar_n(this);}
        public final int getPrio(){ return 0; }
        public final boolean eval(){ return false; }
        public final boolean eval(SpecState _ss) { return var.getValue(_ss) != 0; }
        public final boolean eval(SpecState _ss, State _st) { return var.getValue(_ss, _st) != 0; }
        public final boolean eval(CompState _cs) { return  var.getValue(_cs) != 0; }
        public final boolean eval(Object _qs) { return var.getValue(_qs) != 0; }
        public final String toString() { return var.toString(); }
        public final boolean hasStates() { return var.isState(); }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public final class BVar_n extends BExpr {
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        //private BVar_n(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; type = bVar | neg_mask; }
        private BVar_n(BVar _b){ var = _b.var; type = bVar | neg_mask; }
        public BVar negate(){ return new BVar(this);}
        public final int getPrio(){ return 0; }
        public final boolean eval(){ return false; }
        public final boolean eval(SpecState _ss) {
            return var.getValue(_ss) == 0;
        }
        public final boolean eval(SpecState _ss, State _st) { return var.getValue(_ss, _st) == 0; }
        public final boolean eval(CompState _cs) { return var.getValue(_cs) == 0; }
        public final boolean eval(Object _qs) { return var.getValue(_qs) == 0; }
        public final String toString() { return opString[bNot] + "(" + var.toString() + ")"; }
        public final boolean hasStates() { return var.isState(); }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
     public final class BIVar extends BExpr {
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private BIVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; type = biVar; }
        public BIVar(BIVar_n _b) { var =_b.var; type = biVar; }
        public BIVar_n negate() { return new BIVar_n(this); }
        public final int getPrio(){ return 0; }
        public final boolean eval(){ return false; }
        public final boolean eval(SpecState _ss) { return var.getValue(_ss) != 0; }
        public final boolean eval(SpecState _ss, State _st) { return var.getValue(_ss, _st) != 0; }
        public final boolean eval(CompState _cs) { return  var.getValue(_cs) != 0; }
        public final boolean eval(Object _qs) { return var.getValue(_qs) != 0; }
        public final String toString() { return var.toString(); }
        public final boolean hasStates() { return var.isState(); }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
     public final class BIVar_n extends BExpr {
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        //private BIVar_n(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; type = biVar | neg_mask; }
        public BIVar_n(BIVar _b) { var =_b.var; type = biVar | neg_mask; }
        public BIVar negate() { return new BIVar(this); }
        public final int getPrio(){ return 0; }
        public final boolean eval(){ return false; }
        public final boolean eval(SpecState _ss) { return var.getValue(_ss) == 0; }
        public final boolean eval(SpecState _ss, State _st) { return var.getValue(_ss, _st) == 0; }
        public final boolean eval(CompState _cs) { return var.getValue(_cs) == 0; }
        public final boolean eval(Object _qs) { return var.getValue(_qs) == 0; }
        public final String toString() { return opString[bNot] + "(" + var.toString() + ")"; }
        public final boolean hasStates() { return var.isState(); }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public final class IIIPlus extends IIIBinOp {
        public IIIPlus(IExpr _l, IExpr _r) { super(_l, _r); type = iiiPlus; }
        public IIIPlus(IIIPlus_n _p){ super(_p.left, _p.right); type = iiiPlus; }
        public final IIIPlus_n negate(){ return new IIIPlus_n(this); }
        public final int getPrio() { return prios[iiiPlus]; }
        public final int eval() { return (left.eval() + right.eval()); }
        public final int eval(SpecState _ss) { return (left.eval(_ss) + right.eval(_ss)); }
        public final int eval(SpecState _ss, State _st) { return (left.eval(_ss,_st) + right.eval(_ss,_st)); }
        public final int eval(CompState _cs) { return (left.eval(_cs) + right.eval(_cs)); }
        public final int eval(Object _qs){ return (left.eval(_qs) + right.eval(_qs)); }
        public final String toString() { return "(" +  left.toString() + ")" + opString[iiiPlus] + "(" +  right.toString() + ")"; }
    }
    public final class IIIPlus_n extends IIIBinOp {
        //public IIIPlus_n(IExpr _l, IExpr _r) { super(_l, _r); type = iiiPlus | neg_mask; }
        public IIIPlus_n(IIIPlus _p){ super(_p.left, _p.right); type = iiiPlus | neg_mask; }
        public final IIIPlus negate(){ return new IIIPlus(this); }
        public final int getPrio() { return prios[iiiPlus]; }
        public final int eval() { return -(left.eval() + right.eval()); }
        public final int eval(SpecState _ss) { return -(left.eval(_ss) + right.eval(_ss)); }
        public final int eval(SpecState _ss, State _st) { return -(left.eval(_ss,_st) + right.eval(_ss,_st)); }
        public final int eval(CompState _cs) { return -(left.eval(_cs) + right.eval(_cs)); }
        public final int eval(Object _qs){ return -(left.eval(_qs) + right.eval(_qs)); }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiPlus] + "(" +  right.toString() + ")";
            return  opString[iNeg] + "(" + s + ")";
        }
    }
    public final class IIIMinus extends IIIBinOp {
        public IIIMinus(IExpr _l, IExpr _r) { super(_l, _r); type = iiiMinus; }
        public IIIMinus(IIIMinus _p){ super(_p.right, _p.left); type = iiiMinus; }
        public final IIIMinus negate(){ return new IIIMinus(this); }
        public final int getPrio() { return prios[iiiMinus]; }
        public final int eval() { return left.eval() - right.eval(); }
        public final int eval(SpecState _ss) { return left.eval(_ss) - right.eval(_ss); }
        public final int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) - right.eval(_ss,_st); }
        public final int eval(CompState _cs) { return left.eval(_cs) - right.eval(_cs); }
        public final int eval(Object _qs){ return left.eval(_qs) - right.eval(_qs); }
        public final String toString() { return  "(" +  left.toString() + ")" + opString[iiiMinus] + "(" +  right.toString() + ")"; }
    }
    public final class IIIMult extends IIIBinOp {
        public IIIMult(IExpr _l, IExpr _r) { super(_l, _r); type = iiiMult; }
        public IIIMult(IIIMult_n _p){ super(_p.left, _p.right); type = iiiMult; }
        public final IIIMult_n negate(){ return new IIIMult_n(this); }
        public final int getPrio() { return prios[iiiMult]; }
        public final int eval() { return left.eval() * right.eval(); }
        public final int eval(SpecState _ss) { return left.eval(_ss) * right.eval(_ss); }
        public final int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) * right.eval(_ss,_st); }
        public final int eval(CompState _cs) { return left.eval(_cs) * right.eval(_cs); }
        public final int eval(Object _qs){ return left.eval(_qs) * right.eval(_qs); }
        public final String toString() { return  "(" +  left.toString() + ")" + opString[iiiMult] + "(" +  right.toString() + ")"; }
    }
    public final class IIIMult_n extends IIIBinOp {
        //public IIIMult_n(IExpr _l, IExpr _r) { super(_l, _r); type = iiiMult | neg_mask; }
        public IIIMult_n(IIIMult _p){ super(_p.left, _p.right); type = iiiMult | neg_mask; }
        public final IIIMult negate(){ return new IIIMult(this); }
        public final int getPrio() { return prios[iiiMult]; }
        public final int eval() { return -(left.eval() * right.eval()); }
        public final int eval(SpecState _ss) { return -(left.eval(_ss) * right.eval(_ss)); }
        public final int eval(SpecState _ss, State _st) { return -(left.eval(_ss,_st) * right.eval(_ss,_st)); }
        public final int eval(CompState _cs) { return -(left.eval(_cs) * right.eval(_cs)); }
        public final int eval(Object _qs){ return -(left.eval(_qs) * right.eval(_qs)); }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiMult] + "(" +  right.toString() + ")";
            return   opString[iNeg] + "(" + s + ")";
        }
    }
    public final class IIIDiv extends IIIBinOp {
        public IIIDiv(IExpr _l, IExpr _r) { super(_l, _r); type = iiiDiv; }
        public IIIDiv(IIIDiv_n _p){ super(_p.left, _p.right); type = iiiDiv; }
        public final IIIDiv_n negate(){ return new IIIDiv_n(this); }
        public final int getPrio() { return prios[iiiDiv]; }
        public final int eval() { return left.eval() / right.eval(); }
        public final int eval(SpecState _ss) { return left.eval(_ss) / right.eval(_ss); }
        public final int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) / right.eval(_ss,_st); }
        public final int eval(CompState _cs) { return left.eval(_cs) / right.eval(_cs); }
        public final int eval(Object _qs){ return left.eval(_qs) / right.eval(_qs); }
        public final String toString() { return  "(" +  left.toString() + ")" + opString[iiiDiv] + "(" +  right.toString() + ")"; }
    }
     public final class IIIDiv_n extends IIIBinOp {
        //public IIIDiv_n(IExpr _l, IExpr _r) { super(_l, _r); type = iiiDiv | neg_mask; }
        public IIIDiv_n(IIIDiv _p){ super(_p.left, _p.right); type = iiiDiv | neg_mask; }
        public final IIIDiv negate(){ return new IIIDiv(this); }
        public final int getPrio() { return prios[iiiDiv]; }
        public final int eval() { return -(left.eval() / right.eval()); }
        public final int eval(SpecState _ss) { return -(left.eval(_ss) / right.eval(_ss)); }
        public final int eval(SpecState _ss, State _st) { return -(left.eval(_ss,_st) / right.eval(_ss,_st)); }
        public final int eval(CompState _cs) { return -(left.eval(_cs) / right.eval(_cs)); }
        public final int eval(Object _qs){ return -(left.eval(_qs) / right.eval(_qs)); }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiDiv] + "(" +  right.toString() + ")";
            return   opString[iNeg] + "(" + s + ")" ;
        }
    }
   public final class IIIMod extends IIIBinOp {
        public IIIMod(IExpr _l, IExpr _r) { super(_l, _r); type = iiiMod; }
        public IIIMod(IIIMod_n _p){ super(_p.left, _p.right); type = iiiMod; }
        public final IIIMod_n negate(){ return new IIIMod_n(this); }
        public final int getPrio() { return prios[iiiMod]; }
        public final int eval() { return left.eval() % right.eval(); }
        public final int eval(SpecState _ss) { return left.eval(_ss) % right.eval(_ss); }
        public final int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) % right.eval(_ss,_st); }
        public final int eval(CompState _cs) { return left.eval(_cs) % right.eval(_cs); }
        public final int eval(Object _qs){ return left.eval(_qs) % right.eval(_qs); }
        public final String toString() { return "(" +  left.toString() + ")" + opString[iiiMod] + "(" +  right.toString() + ")"; }
    }
   public final class IIIMod_n extends IIIBinOp {
        //public IIIMod_n(IExpr _l, IExpr _r) { super(_l, _r); type = iiiMod | neg_mask; }
        public IIIMod_n(IIIMod _p){ super(_p.left, _p.right); type = iiiMod | neg_mask; }
        public final IIIMod negate(){ return new IIIMod(this); }
        public final int getPrio() { return prios[iiiMod]; }
        public final int eval() { return -(left.eval() % right.eval()); }
        public final int eval(SpecState _ss) { return -(left.eval(_ss) % right.eval(_ss)); }
        public final int eval(SpecState _ss, State _st) { return -(left.eval(_ss,_st) % right.eval(_ss,_st)); }
        public final int eval(CompState _cs) { return -(left.eval(_cs) % right.eval(_cs)); }
        public final int eval(Object _qs){ return -(left.eval(_qs) % right.eval(_qs)); }
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[iiiMod] + "(" +  right.toString() + ")";
            return  opString[iNeg] + "(" + s + ")";
        }
    }
    public final class BBBAnd extends BBBBinOp {
        public BBBAnd(BExpr _l, BExpr _r) { super(_l, _r); type = bbbAnd;}
        public BBBAnd(BBBAnd_n _p){
            super(_p.left, _p.right); type = bbbAnd;
        }
        public final BBBAnd_n negate(){ return new BBBAnd_n(this); }
        public final int getPrio() { return prios[bbbAnd]; }
        public final boolean eval() { return left.eval() && right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) && right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) && right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) && right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) && right.eval(_qs); }
        public final String toString() { return  "(" +  left.toString() + ")" + opString[bbbAnd] + "(" +  right.toString() + ")"; }
    }
    public final class BBBAnd_n extends BBBBinOp {
        //public BBBAnd_n(BExpr _l, BExpr _r) { super(_l, _r); type = bbbAnd | neg_mask; }
        public BBBAnd_n(BBBAnd _p){ super(_p.left, _p.right); type = bbbAnd | neg_mask; }
        public final BBBAnd negate(){ return new BBBAnd(this); }
        public final int getPrio() { return prios[bbbAnd]; }
        public final boolean eval() { return !(left.eval() && right.eval()); }
        public final boolean eval(SpecState _ss) { return !(left.eval(_ss) && right.eval(_ss)); }
        public final boolean eval(SpecState _ss, State _st) { return !(left.eval(_ss,_st) && right.eval(_ss,_st)); }
        public final boolean eval(CompState _cs) { return !(left.eval(_cs) && right.eval(_cs)); }
        public final boolean eval(Object _qs) { return !(left.eval(_qs) && right.eval(_qs)); }
        public final String toString() {
            String s =  "(" +  left.toString() + ")" + opString[bbbAnd] + "(" +  right.toString() + ")";
            return  opString[bNot] + "(" + s + ")";
        }
    }
    public final class BBBOr extends BBBBinOp {
        public BBBOr(BExpr _l, BExpr _r){ super(_l, _r); type = bbbOr;}
        public BBBOr(BBBOr_n _p){ super(_p.left, _p.right); type = bbbOr; }
        public final BBBOr_n negate(){ return new BBBOr_n(this); }
        public final int getPrio() { return prios[bbbOr]; }
        public final boolean eval() { return left.eval() || right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) || right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) || right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) || right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) || right.eval(_qs); }
        public final String toString() { return "(" +  left.toString() + ")" + opString[bbbOr] + "(" +  right.toString() + ")"; }
    }
    public final class BBBOr_n extends BBBBinOp {
        //public BBBOr_n(BExpr _l, BExpr _r){ super(_l, _r); type = bbbOr | neg_mask;}
        public BBBOr_n(BBBOr _p){ super(_p.left, _p.right); type = bbbOr | neg_mask; }
        public final BBBOr negate(){ return new BBBOr(this); }
        public final int getPrio() { return prios[bbbOr]; }
        public final boolean eval() { return !(left.eval() || right.eval());}
        public final boolean eval(SpecState _ss) { return !(left.eval(_ss) || right.eval(_ss));}
        public final boolean eval(SpecState _ss, State _st) { return !(left.eval(_ss,_st) || right.eval(_ss,_st));}
        public final boolean eval(CompState _cs) { return !(left.eval(_cs) || right.eval(_cs));}
        public final boolean eval(Object _qs) { return !(left.eval(_qs) || right.eval(_qs));}
        public final String toString() {
            String s = "(" +  left.toString() + ")" + opString[bbbOr] + "(" +  right.toString() + ")";
            return  opString[bNot] + "(" + s + ")";
        }
    }
    public final class BIIEq extends BIIBinOp {
        public BIIEq(IExpr _l, IExpr _r){ super(_l, _r); type = biiEq;}
        public BIIEq(BIIDif _p){
            super(_p.left, _p.right);
            type = biiEq;
        }
        public final BIIDif negate(){ return new BIIDif(this); }
        public final int getPrio() { return prios[biiEq]; }
        public final boolean eval() { return left.eval() == right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) == right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) == right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) == right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) == right.eval(_qs); }
        public final String toString() {
            return  "(" + left.toString() + ")" + opString[biiEq] + " (" + right.toString() + ")";
        }
    }
    public final class BBBEq extends BBBBinOp {
        public BBBEq(BExpr _l, BExpr _r){ super(_l, _r); type = bbbEq; }
        public BBBEq(BBBDif _p){ super(_p.left, _p.right); type = bbbEq; }
        public final BBBDif negate(){ return new BBBDif(this); }
        public final int getPrio() { return prios[bbbEq]; }
        public final boolean eval() { return left.eval() == right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) == right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) == right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) == right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) == right.eval(_qs); }
        public final String toString() {
            return  "(" + left.toString() + ")" + opString[bbbEq] + " (" + right.toString() + ")";
        }
    }
    public final class BIIDif extends BIIBinOp {
        public BIIDif(IExpr _l, IExpr _r){ super(_l, _r); type = biiDif; }
        public BIIDif(BIIEq _p){
            super(_p.left, _p.right);
            type = biiDif;
        }
        public final BIIEq negate(){ return new BIIEq(this); }
        public final int getPrio() { return prios[biiDif]; }
        public final boolean eval() { return left.eval() != right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) != right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) != right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) != right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) != right.eval(_qs); }
        public final String toString() {
            return  "(" + left.toString() + ")" + opString[biiDif] + " (" + right.toString() + ")";
        }
    }
    public final class BBBDif extends BBBBinOp {
        public BBBDif(BExpr _l, BExpr _r){ super(_l, _r); type = bbbDif; }
        public BBBDif(BBBEq _p){ super(_p.left, _p.right); type = bbbDif; }
        public final BBBEq negate(){ return new BBBEq(this); }
        public final int getPrio() { return prios[bbbDif]; }
        public final boolean eval() { return left.eval() != right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) != right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss,_st) != right.eval(_ss,_st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) != right.eval(_cs); }
        public final boolean eval(Object _qs) {
            return left.eval(_qs) != right.eval(_qs);
        }
        public final String toString() {
            return "(" + left.toString() + ")" + opString[bbbDif] + " (" + right.toString() + ")";
        }
    }
    public final class BIILt extends BIIBinOp {
        public BIILt(IExpr _l, IExpr _r) { super(_l, _r); type = biiLt; }
        public BIILt(BIIGeq _p){ super(_p.left, _p.right); type = biiLt; }
        public final BIIGeq negate(){ return new BIIGeq(this); }
        public final int getPrio() { return prios[biiLt]; }
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
            return  "(" +l + ")" + opString[biiLt] + "(" + r + ")";
        }
    }
    public final class BIIGt extends BIIBinOp {
        public BIIGt(IExpr _l, IExpr _r) { super(_l, _r); type = biiGt; }
        public BIIGt(BIILeq _p){ super(_p.left, _p.right); type = biiGt; }
        public final BIILeq negate(){ return new BIILeq(this); }
        public final int getPrio() { return prios[biiGt]; }
        public final boolean eval() { return left.eval() > right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) > right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) > right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) > right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) > right.eval(_qs); }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return   "(" +l + ")" + opString[biiGt] + "(" + r + ")";
        }
    }
    public final class BIILeq extends BIIBinOp {
        public BIILeq(IExpr _l, IExpr _r){ super(_l, _r); type = biiLeq; }
        public BIILeq(BIIGt _p){ super(_p.left, _p.right); type = biiLeq; }
        public final BIIGt negate(){ return new BIIGt(this); }
        public final int getPrio() { return prios[biiLeq]; }
        public final boolean eval() { return left.eval() <= right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) <= right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) <= right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) <= right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) <= right.eval(_qs); }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return   "(" +l + ")" + opString[biiLeq] + "(" + r + ")";
        }
    }
    public final class BIIGeq extends BIIBinOp {
        public BIIGeq(IExpr _l, IExpr _r) { super(_l, _r); type = biiGeq; }
        public BIIGeq(BIILt _p){ super(_p.left, _p.right); type = biiGeq; }
        public final BIILt negate(){ return new BIILt(this); }
        public final int getPrio() { return prios[biiGeq]; }
        public final boolean eval() { return left.eval() >= right.eval(); }
        public final boolean eval(SpecState _ss) { return left.eval(_ss) >= right.eval(_ss); }
        public final boolean eval(SpecState _ss, State _st) { return left.eval(_ss, _st) >= right.eval(_ss, _st); }
        public final boolean eval(CompState _cs) { return left.eval(_cs) >= right.eval(_cs); }
        public final boolean eval(Object _qs) { return left.eval(_qs) >= right.eval(_qs); }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            return   "(" +l + ")" + opString[biiGeq] + "(" + r + ")";
        }
    }
    public final class BIExpr extends BExpr {
        IExpr iExpr;
        public BIExpr(IExpr _e) { iExpr=_e; type = biExpr; }
        public BIExpr(BIExpr_n _e) { iExpr=_e.iExpr; type = biExpr; }
        public final BIExpr_n negate() { return new BIExpr_n(this); }
        public final int getPrio() { return 0; }
        public final boolean eval() { return iExpr.eval() != 0; }
        public final boolean eval(SpecState _ss) { return iExpr.eval(_ss) != 0; }
        public final boolean eval(SpecState _ss, State _st) { return iExpr.eval(_ss, _st) != 0; }
        public final boolean eval(CompState _cs) { return iExpr.eval(_cs) != 0; }
        public final boolean eval(Object _qs) { return iExpr.eval(_qs) != 0; }
        public final String toString() { return iExpr.toString(); }
        public final boolean hasStates() { return false; }
        public final void linkStates() { iExpr.linkStates(); }
        public final void linkComps(Spec _spec) { iExpr.linkComps(_spec); }
    }
    public final class BIExpr_n extends BExpr {
        IExpr iExpr;
        //public BIExpr_n(IExpr _e) { iExpr=_e; type = biExpr | neg_mask; }
        public BIExpr_n(BIExpr _e) { iExpr=_e.iExpr; type = biExpr | neg_mask; }
        public final BIExpr negate() { return new BIExpr(this); }
        public final int getPrio() { return 0; }
        public final boolean eval() { return iExpr.eval() == 0; }
        public final boolean eval(SpecState _ss) { return iExpr.eval(_ss) == 0; }
        public final boolean eval(SpecState _ss, State _st) { return iExpr.eval(_ss, _st) == 0; }
        public final boolean eval(CompState _cs) { return iExpr.eval(_cs) == 0; }
        public final boolean eval(Object _qs) { return iExpr.eval(_qs) == 0; }
        public final String toString() { return opString[bNot] + "(" + iExpr.toString() + ")"; };
        public final boolean hasStates() { return false; };
        public final void linkStates() { iExpr.linkStates(); };
        public final void linkComps(Spec _spec) { iExpr.linkComps(_spec); };
    }
}
