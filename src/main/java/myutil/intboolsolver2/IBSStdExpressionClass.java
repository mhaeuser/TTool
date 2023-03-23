package myutil.intboolsolver2;

import java.util.ArrayList;

public class IBSStdExpressionClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState> {
    protected final int[] prios =       { 2 , 2 , 1 , 1 , 1 , 1  ,  2 ,  3 ,  3 ,  3 ,  3 , 3 , 3 ,  3 ,  3 };
    protected final String[] opString = {"+","-","*","/","%","&&","||","==","==","!=","!=","<",">","<=",">=","-","!","true","false"};
    public final int iiiPlus = 0;
    public final int iiiMinus = 1;
    public final int iiiMult = 2;
    public final int iiiDiv = 3;
    public final int iiiMod = 4;
    public final int bbbAnd = 5;
    public final int bbbOr = 6;
    public final int biiEq = 7;
    public final int bbbEq = 8;
    public final int biiDif = 9;
    public final int bbbDif = 10;
    public final int biiLt = 11;
    public final int biiGt = 12;
    public final int biiLeq = 13;
    public final int biiGeq = 14;
    public final int iNeg = 13;
    public final int bNot = 14;
    public final int btrue = 15;
    public final int bfalse = 16;
    public final int iVar = 1000; // no associated symbol
    public final int bVar = 1001; // no associated symbol
    public final int biVar = 1002; // no associated symbol
    public final int iConst = 1003; // no associated symbol
    public final int bConst = 1004; // no associated symbol
    public final int biExpr = 1005; // no associated symbol

    private ArrayList<IExpr> iExpressions = new ArrayList<IExpr>(16);
    private ArrayList<Boolean> iBusy = new ArrayList<Boolean>(16);
    private ArrayList<BExpr> bExpressions = new ArrayList<BExpr>(16);
    private ArrayList<Boolean> bBusy = new ArrayList<Boolean>(16);
    public IBSStdExpressionClass(){}
    public void clear(){
        iExpressions.clear();
        bExpressions.clear();
        iBusy.clear();
        bBusy.clear();
    }
    private int findIfree(){
        int i;
        for (i = 0; i < iBusy.size(); i++) if (!iBusy.get(i)) break;
        if (i==iBusy.size()){
            iBusy.add(Boolean.FALSE);
            iExpressions.add(null);
        }
        return i;
    }
    private int findBfree(){
        int i;
        for (i = 0; i < bBusy.size(); i++) if (!bBusy.get(i)) break;
        if (i==bBusy.size()){
            bBusy.add(Boolean.FALSE);
            bExpressions.add(null);
        }
        return i;
    }
    public void freeInt(int _toFree) {
        iBusy.set(_toFree,Boolean.FALSE);
        iExpressions.set(_toFree,null);
    }
    public void freeBool(int _toFree) {
        bBusy.set(_toFree,Boolean.FALSE);
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
    public int make_iiiPlus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
           throw new Error("IBSStdExpressionClass.make_iiiPlus called on undefined subexpression");
        iExpressions.set(tgt, new IIIPlus(iExpressions.get(_left), iExpressions.get(_right)));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiMinus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMinus called on undefined subexpression");
        iExpressions.set(tgt, new IIIMinus(iExpressions.get(_left), iExpressions.get(_right)));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiMult(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMult called on undefined subexpression");
        iExpressions.set(tgt, new IIIMult(iExpressions.get(_left), iExpressions.get(_right)));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiDiv(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiDiv called on undefined subexpression");
        iExpressions.set(tgt, new IIIDiv(iExpressions.get(_left), iExpressions.get(_right)));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiMod(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_iiiMod called on undefined subexpression");
        iExpressions.set(tgt, new IIIMod(iExpressions.get(_left), iExpressions.get(_right)));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbAnd(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbAnd called on undefined subexpression");
        bExpressions.set(tgt, new BBBAnd(bExpressions.get(_left), bExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbOr(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbOr called on undefined subexpression");
        bExpressions.set(tgt, new BBBOr(bExpressions.get(_left), bExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiEq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                    iExpressions.get(_left) == null || iExpressions.get(_right) == null)
                throw new Error("IBSStdExpressionClass.make_biiEq called on undefined subexpression");
        bExpressions.set(tgt, new BIIEq(iExpressions.get(_left), iExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbEq(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbEq called on undefined subexpression");
        bExpressions.set(tgt, new BBBEq(bExpressions.get(_left), bExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiDif(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiDif called on undefined subexpression");
        bExpressions.set(tgt, new BIIDif(iExpressions.get(_left), iExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbDif(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_bbbDif called on undefined subexpression");
        bExpressions.set(tgt, new BBBDif(bExpressions.get(_left), bExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiLt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiLt called on undefined subexpression");
        bExpressions.set(tgt, new BIILt(iExpressions.get(_left), iExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiGt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiGt called on undefined subexpression");
        bExpressions.set(tgt, new BIIGt(iExpressions.get(_left), iExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiLeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiLeq called on undefined subexpression");
        bExpressions.set(tgt, new BIILeq(iExpressions.get(_left), iExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiGeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSStdExpressionClass.make_biiGeq called on undefined subexpression");
        bExpressions.set(tgt, new BIIGeq(iExpressions.get(_left), iExpressions.get(_right)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findIfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_iVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        iExpressions.set(tgt, new IVar(_v));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findBfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_bVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.BoolAttr)
            return -1;
        bExpressions.set(tgt, new BVar(_v));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findBfree();
        if (_v == null)
            throw new Error("IBSStdExpressionClass.make_biVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        bExpressions.set(tgt, new BIVar(_v));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iConst(int _i) {
        int tgt = findIfree();
        iExpressions.set(tgt, new IConst(_i));
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bConst(boolean _b) {
        int tgt = findBfree();
        bExpressions.set(tgt, new BConst(_b));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iNeg(int _expr) {
        int tgt = findIfree();
        if (iExpressions.size()<=_expr || _expr < 0 ||iExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_iNeg called on undefined subexpression");
        iExpressions.set(tgt, iExpressions.get(_expr).negate());
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bNot(int _expr) {
        int tgt = findBfree();
        if (bExpressions.size()<=_expr || _expr < 0 || bExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_bNot called on undefined subexpression");
        bExpressions.set(tgt, bExpressions.get(_expr).negate());
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biExpr(int _expr) {
        int tgt = findBfree();
        if (iExpressions.size()<=_expr || _expr < 0 || iExpressions.get(_expr) == null)
            throw new Error("IBSStdExpressionClass.make_biExpr called on undefined subexpression");
        bExpressions.set(tgt, new BIExpr(iExpressions.get(_expr)));
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public abstract class IExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr {
        private int type;
        public final int getType() { return type; }
        public abstract int getPrio();
        public abstract IExpr negate();
    }
    public abstract class BExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr {
        private int type;
        public final int getType() { return type; }
        public abstract int getPrio();
        public abstract BExpr negate();
    }
    public abstract class IIIBinOp extends IExpr{
        protected final IExpr left;
        protected final IExpr right;
        public IIIBinOp(IExpr _l, IExpr _r){
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
    public final class IConst extends IExpr {
        private final int type = iConst;
        private final int constant;
        private IConst(int _i){ constant = _i; }
        private IConst(IConst _e){
            constant = -_e.constant;
        }
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
        private final int type = bConst;
        private final boolean constant;
        public BConst(boolean _b){ constant = _b; }
        public BConst(BConst _b){
            constant = !_b.constant;
        }
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
        private final int type = iVar;
        private final boolean isNeg;
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private IVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v){ var = _v; isNeg=false;}
        private IVar(IVar _v){
            var = _v.var;
            isNeg = !_v.isNeg;
        }
        public IVar negate(){
            return new IVar(this);
        }
        public final int getPrio(){ return 0; }
        public final int eval(){ return 0; }
        public final int eval(SpecState _ss) { return (isNeg?-var.getValue(_ss):var.getValue(_ss)); }
        public final int eval(SpecState _ss, State _st) { return (isNeg?-var.getValue(_ss,_st):var.getValue(_ss,_st)); }
        public final int eval(CompState _cs) { return (isNeg?-var.getValue(_cs):var.getValue(_cs)); }
        public final int eval(Object _qs) { return (isNeg?-var.getValue(_qs):var.getValue(_qs)); }
        public final String toString() {
            if (isNeg)
                return opString[iNeg] + var.toString();
            return var.toString();
        }
        public final boolean hasStates() { return false; }
        public final void linkStates() {var.linkState();}
        public final void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public final class BVar extends BExpr {
        private final int type = bVar;
        private final boolean isNot;
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private BVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; isNot=false;}
        private BVar(BVar _b){
            isNot = !_b.isNot;
            var = _b.var;
        }
        public BVar negate(){ return new BVar(this);}
        public final int getPrio(){ return 0; }
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
    public final class BIVar extends BExpr {
        private final int type = biVar;
        private final boolean isNot;
        private final IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        private BIVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { var = _v; isNot=false;}
        public BIVar(BIVar _b) {
            isNot = !_b.isNot;
            var =_b.var;
        }
        public BIVar negate() { return new BIVar(this); }
        public final int getPrio(){ return 0; }
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
    public final class IIIPlus extends IIIBinOp {
        private final int type = iiiPlus;
        private final boolean isNeg;
        public IIIPlus(IExpr _l, IExpr _r) { super(_l, _r); isNeg=false;}
        public IIIPlus(IIIPlus _p){
            super(_p.left, _p.right);
            isNeg = !_p.isNeg;
        }
        public final IIIPlus negate(){ return new IIIPlus(this); }
        public final int getPrio() { return prios[iiiPlus]; }
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
            String l = (left.getPrio() > prios[iiiPlus] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiPlus] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiPlus] + " " + r + ")";
            else
                return  l + " " + opString[iiiPlus] + " " + r;
        }
    }
    public final class IIIMinus extends IIIBinOp {
        protected final int type = iiiMinus;
        private final boolean isNeg;
        public IIIMinus(IExpr _l, IExpr _r) { super(_l, _r); isNeg=false;}
        public IIIMinus(IIIMinus _p){
            super(_p.left, _p.right);
            isNeg = !_p.isNeg;
        }
        public final IIIMinus negate(){ return new IIIMinus(this); }
        public final int getPrio() { return prios[iiiMinus]; }
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
            String l = (left.getPrio() > prios[iiiMinus] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMinus] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiMinus] + " " + r + ")";
            else
                return  l + " " + opString[iiiMinus] + " " + r;
        }
    }
    public final class IIIMult extends IIIBinOp {
        protected final int type = iiiMult;
        private final boolean isNeg;
        public IIIMult(IExpr _l, IExpr _r) { super(_l, _r); isNeg=false;}
        public IIIMult(IIIMult _p){
            super(_p.left, _p.right);
            isNeg = !_p.isNeg;
        }
        public final IIIMult negate(){ return new IIIMult(this); }
        public final int getPrio() { return prios[iiiMult]; }
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
            String l = (left.getPrio() > prios[iiiMult] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMult] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiMult] + " " + r + ")";
            else
                return  l + " " + opString[iiiMult] + " " + r;
        }
    }
    public final class IIIDiv extends IIIBinOp {
        protected final int type = iiiDiv;
        private final boolean isNeg;
        public IIIDiv(IExpr _l, IExpr _r) { super(_l, _r); isNeg=false;}
        public IIIDiv(IIIDiv _p){
            super(_p.left, _p.right);
            isNeg = !_p.isNeg;
        }
        public final IIIDiv negate(){ return new IIIDiv(this); }
        public final int getPrio() { return prios[iiiDiv]; }
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
            String l = (left.getPrio() > prios[iiiDiv] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiDiv] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiDiv] + " " + r + ")";
            else
                return  l + " " + opString[iiiDiv] + " " + r;
        }
    }
    public final class IIIMod extends IIIBinOp {
        protected final int type = iiiMod;
        private final boolean isNeg;
        public IIIMod(IExpr _l, IExpr _r) { super(_l, _r); isNeg=false;}
        public IIIMod(IIIMod _p){
            super(_p.left, _p.right);
            isNeg = !_p.isNeg;
        }
        public final IIIMod negate(){ return new IIIMod(this); }
        public final int getPrio() { return prios[iiiMod]; }
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
            String l = (left.getPrio() > prios[iiiMod] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMod] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiMod] + " " + r + ")";
            else
                return  l + " " + opString[iiiMod] + " " + r;
        }
    }
    public final class BBBAnd extends BBBBinOp {
        protected final int type = bbbAnd;
        private final boolean isNot;
        public BBBAnd(BExpr _l, BExpr _r) { super(_l, _r); isNot=false;}
        public BBBAnd(BBBAnd _p){
            super(_p.left, _p.right);
            isNot = !_p.isNot;
        }
        public final BBBAnd negate(){ return new BBBAnd(this); }
        public final int getPrio() { return prios[bbbAnd]; }
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
            String l = (left.getPrio() > prios[bbbAnd] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[bbbAnd] ? "(" +  right.toString() + ")" : right.toString());
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[bbbAnd] + " " + r + ")";
            else
                return  l + " " + opString[bbbAnd] + " " + r;
        }
    }
    public final class BBBOr extends BBBBinOp {
        protected final int type = bbbOr;
        private final boolean isNot;
        public BBBOr(BExpr _l, BExpr _r){ super(_l, _r); isNot=false;}
        public BBBOr(BBBOr _p){
            super(_p.left, _p.right);
            isNot = !_p.isNot;
        }
        public final BBBOr negate(){ return new BBBOr(this); }
        public final int getPrio() { return prios[bbbOr]; }
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
            String l = (left.getPrio() > prios[bbbOr] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[bbbOr] ? "(" +  right.toString() + ")" : right.toString());
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[bbbOr] + " " + r + ")";
            else
                return  l + " " + opString[bbbOr] + " " + r;
        }
    }
    public final class BIIEq extends BIIBinOp {
        protected final int type = biiEq;
        private final boolean isNot = false;
        public BIIEq(IExpr _l, IExpr _r){ super(_l, _r); }
        public BIIEq(BIIDif _p){
            super(_p.left, _p.right);
        }
        public final BIIDif negate(){ return new BIIDif(this); }
        public final int getPrio() { return prios[biiEq]; }
        public final boolean eval() {
            boolean b = left.eval() == right.eval();
            return isNot != b;
        }
        public final boolean eval(SpecState _ss) {
            boolean b = left.eval(_ss) == right.eval(_ss);
            return isNot != b;
        }
        public final boolean eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) == right.eval(_ss,_st);
            return isNot != b;
        }
        public final boolean eval(CompState _cs) {
            boolean b = left.eval(_cs) == right.eval(_cs);
            return isNot != b;
        }
        public final boolean eval(Object _qs) {
            boolean b = left.eval(_qs) == right.eval(_qs);
            return isNot != b;
        }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiEq] + " " + r + ")";
            else
                return  l + " " + opString[biiEq] + " " + r;
        }
    }
    public final class BBBEq extends BBBBinOp {
        protected final int type = bbbEq;
        private final boolean isNot = false;
        public BBBEq(BExpr _l, BExpr _r){ super(_l, _r); }
        public BBBEq(BBBDif _p){ super(_p.left, _p.right); }
        public final BBBDif negate(){ return new BBBDif(this); }
        public final int getPrio() { return prios[bbbEq]; }
        public final boolean eval() {
            boolean b = left.eval() == right.eval();
            return isNot != b;
        }
        public final boolean eval(SpecState _ss) {
            boolean b = left.eval(_ss) == right.eval(_ss);
            return isNot != b;
        }
        public final boolean eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) == right.eval(_ss,_st);
            return isNot != b;
        }
        public final boolean eval(CompState _cs) {
            boolean b = left.eval(_cs) == right.eval(_cs);
            return isNot != b;
        }
        public final boolean eval(Object _qs) {
            boolean b = left.eval(_qs) == right.eval(_qs);
            return isNot != b;
        }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[bbbEq] + " " + r + ")";
            else
                return  l + " " + opString[bbbEq] + " " + r;
        }
    }
    public final class BIIDif extends BIIBinOp {
        protected final int type = biiDif;
        private final boolean isNot = false;
        public BIIDif(IExpr _l, IExpr _r){ super(_l, _r); }
        public BIIDif(BIIEq _p){
            super(_p.left, _p.right);
        }
        public final BIIEq negate(){ return new BIIEq(this); }
        public final int getPrio() { return prios[biiDif]; }
        public final boolean eval() {
            boolean b = left.eval() != right.eval();
            return isNot != b;
        }
        public final boolean eval(SpecState _ss) {
            boolean b = left.eval(_ss) != right.eval(_ss);
            return isNot != b;
        }
        public final boolean eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) != right.eval(_ss,_st);
            return isNot != b;
        }
        public final boolean eval(CompState _cs) {
            boolean b = left.eval(_cs) != right.eval(_cs);
            return isNot != b;
        }
        public final boolean eval(Object _qs) {
            boolean b = left.eval(_qs) != right.eval(_qs);
            return isNot != b;
        }
        public final String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiDif] + " " + r + ")";
            else
                return  l + " " + opString[biiDif] + " " + r;
        }
    }
    public final class BBBDif extends BBBBinOp {
        protected final int type = bbbDif;
        private final boolean isNot = false;
        public BBBDif(BExpr _l, BExpr _r){ super(_l, _r); }
        public BBBDif(BBBEq _p){ super(_p.left, _p.right); }
        public final BBBEq negate(){ return new BBBEq(this); }
        public final int getPrio() { return prios[bbbDif]; }
        public final boolean eval() {
            boolean b = left.eval() != right.eval();
            return isNot != b;
        }
        public final boolean eval(SpecState _ss) {
            boolean b = left.eval(_ss) != right.eval(_ss);
            return isNot != b;
        }
        public final boolean eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) != right.eval(_ss,_st);
            return isNot != b;
        }
        public final boolean eval(CompState _cs) {
            boolean b = left.eval(_cs) != right.eval(_cs);
            return isNot != b;
        }
        public final boolean eval(Object _qs) {
            boolean b = left.eval(_qs) != right.eval(_qs);
            return isNot != b;
        }
        public final String toString() {
            return left.toString() + " " + opString[bbbDif] + " " + right.toString();
        }
    }
    public final class BIILt extends BIIBinOp {
        protected final int type = biiLt;
        public BIILt(IExpr _l, IExpr _r) { super(_l, _r); }
        public BIILt(BIIGeq _p){ super(_p.left, _p.right); }
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
            return  l + " " + opString[biiLt] + " " + r;
        }
    }
    public final class BIIGt extends BIIBinOp {
        protected final int type = biiGt;
        public BIIGt(IExpr _l, IExpr _r) { super(_l, _r); }
        public BIIGt(BIILeq _p){ super(_p.left, _p.right); }
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
            return  l + " " + opString[biiGt] + " " + r;
        }
    }
    public final class BIILeq extends BIIBinOp {
        protected final int type = biiLeq;
        public BIILeq(IExpr _l, IExpr _r){ super(_l, _r); }
        public BIILeq(BIIGt _p){ super(_p.left, _p.right); }
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
            return  l + " " + opString[biiLeq] + " " + r;
        }
    }
    public final class BIIGeq extends BIIBinOp {
        protected final int type = biiGeq;
        public BIIGeq(IExpr _l, IExpr _r) { super(_l, _r); }
        public BIIGeq(BIILt _p){ super(_p.left, _p.right); }
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
            return  l + " " + opString[biiGeq] + " " + r;
        }
    }
    public final class BIExpr extends BExpr {
        private final int type = biExpr;
        private final boolean isNot;
        IExpr iExpr;
        public BIExpr(IExpr _e) { iExpr=_e; isNot=false;}
        public BIExpr(BIExpr _e) {
            iExpr=_e.iExpr;
            isNot = !_e.isNot;
        }
        public final BIExpr negate() { return new BIExpr(this); }
        public final int getPrio() { return 0; }
        public final boolean eval() { return (isNot == (iExpr.eval() == 0)); }
        public final boolean eval(SpecState _ss) { return (isNot == (iExpr.eval(_ss) == 0)); }
        public final boolean eval(SpecState _ss, State _st) { return (isNot == (iExpr.eval(_ss, _st) == 0)); }
        public final boolean eval(CompState _cs) { return (isNot == (iExpr.eval(_cs) == 0)); }
        public final boolean eval(Object _qs) { return (isNot == (iExpr.eval(_qs) == 0)); }
        public final String toString() {
            String s = iExpr.toString();
            if (isNot)
                return opString[bNot] + "(" + s + ")";
            else
                return  s;
        };
        public final boolean hasStates() { return false; };
        public final void linkStates() { iExpr.linkStates(); };
        public final void linkComps(Spec _spec) { iExpr.linkComps(_spec); };
    }
}
