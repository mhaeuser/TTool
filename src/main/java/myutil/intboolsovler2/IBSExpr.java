package myutil.intboolsovler2;

public class IBSExpr <
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSExpression{
    protected final int[] prios =        { 2 , 2 , 1 , 1 , 1 , 1  ,  2 ,  3 ,  3 ,  3 ,  3 , 3 , 3 ,  3 ,  3 };
    protected final String[] opString = {"+","-","*","/","%","&&","||","==","==","!=","!=","<",">","<=",">=","-","!"};
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
    public final int iVar = 1000; // no associated symbol
    public final int bVar = 1001; // no associated symbol
    public final int iConst = 1002; // no associated symbol
    public final int bConst = 1003; // no associated symbol
    public final int biExpr = 1004; // no associated symbol

    public IExpr make_iiiPlus(IExpr left, IExpr right) { return new IIIPlus(left, right); }
    public IExpr make_iiiMinus(IExpr left, IExpr right) { return new IIIMinus(left, right); }
    public IExpr make_iiiMult(IExpr left, IExpr right) { return new IIIMult(left, right); }
    public IExpr make_iiiDiv(IExpr left, IExpr right) { return new IIIDiv(left, right); }
    public IExpr make_iiiMod(IExpr left, IExpr right) { return new IIIMod(left, right); }
    public BExpr make_bbbAnd(BExpr left, BExpr right) { return new BBBAnd(left, right); }
    public BExpr make_bbbOr(BExpr left, BExpr right) { return new BBBOr(left, right); }
    public BExpr make_biiEq(IExpr left, IExpr right) { return new BIIEq(left, right); }
    public BExpr make_bbbEq(BExpr left, BExpr right) { return new BBBEq(left, right); }
    public BExpr make_biiDif(IExpr left, IExpr right) { return new BIIDif(left, right); }
    public BExpr make_bbbDif(BExpr left, BExpr right) { return new BBBDif(left, right); }
    public BExpr make_biiLt(IExpr left, IExpr right) { return new BIILt(left, right); }
    public BExpr make_biiGt(IExpr left, IExpr right) { return new BIIGt(left, right); }
    public BExpr make_biiLeq(IExpr left, IExpr right) { return new BIILeq(left, right); }
    public BExpr make_biiGeq(IExpr left, IExpr right) { return new BIIGeq(left, right); }
    private IExpr make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return new IVar(_v); }
    private BExpr make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return new BVar(_v); }
    private IExpr make_iConst(int _i) { return new IConst(_i); }
    private BExpr make_bConst(int _i) { return new BConst(_i); }
    public IExpr make_iNeg(IExpr _expr) throws CloneNotSupportedException { return _expr.negate(); }
    public BExpr make_bNot(BExpr _expr) throws CloneNotSupportedException { return _expr.negate(); }
    public BExpr make_biExpr(IExpr _expr) { return new BIExpr(_expr); }

    public abstract class Expr implements Cloneable {
        protected int type;
        public Expr clone() throws CloneNotSupportedException {
            return (Expr) super.clone();
        }
        public int getType() { return type; }
        public abstract int getPrio();
        public abstract int eval();
        public abstract int eval(SpecState _ss);
        public abstract int eval(SpecState _ss, State _st);
        public abstract int eval(CompState _cs);
        public abstract int eval(Object _qs);
        public abstract String toString();
        public abstract boolean hasStates();
        public abstract void linkStates();
        public abstract void linkComps(Spec _spec);
    }
    public abstract class IExpr extends Expr{
         public boolean isNeg;
         public IExpr clone() throws CloneNotSupportedException {
            return (IExpr) super.clone();
        }
        public IExpr negate() throws CloneNotSupportedException {
            IExpr e = this.clone();
            e.isNeg = !isNeg;
            return e;
        }
    }
    public abstract class BExpr extends Expr{
        public boolean isNot;
        public BExpr clone() throws CloneNotSupportedException {
            return (BExpr) super.clone();
        }
        public BExpr negate() throws CloneNotSupportedException {
            BExpr e = this.clone();
            e.isNot = !isNot;
            return e;
        }
    }
    public abstract class IBinOp extends IExpr{
        public Expr left;
        public Expr right;
        public IBinOp clone() throws CloneNotSupportedException {
            return (IBinOp) super.clone();
        }
        public boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public void linkStates(){
            left.linkStates();
            right.linkStates();
        }
        public void linkComps(Spec _spec) {
            left.linkComps(_spec);
            right.linkComps(_spec);
        }
    }
    public abstract class BBinOp extends BExpr{
        public Expr left;
        public Expr right;
        public BBinOp clone() throws CloneNotSupportedException {
            return (BBinOp) super.clone();
        }
        public boolean hasStates(){
            return left.hasStates() || right.hasStates();
        }
        public void linkStates(){
            left.linkStates();
            right.linkStates();
        }
        public void linkComps(Spec _spec){
            left.linkComps(_spec);
            right.linkComps(_spec);
        }
    }
    public abstract class IIIBinOp extends IBinOp{
        public IExpr left;
        public IExpr right;
        public IIIBinOp(IExpr _l, IExpr _r){
            left  = _l;
            right = _r;
        }
        public IIIBinOp clone() throws CloneNotSupportedException {
            return (IIIBinOp) super.clone();
        }
    }
    public abstract class BIIBinOp extends BBinOp{
        public IExpr left;
        public IExpr right;
        public BIIBinOp(IExpr _l, IExpr _r){
            left  = _l;
            right = _r;
        }
        public BIIBinOp clone() throws CloneNotSupportedException {
            return (BIIBinOp) super.clone();
        }
    }
    public abstract class BBBBinOp extends BBinOp{
        public BExpr left;
        public BExpr right;
        public BBBBinOp(BExpr _l, BExpr _r){
            left  = _l;
            right = _r;
        }
        public BBBBinOp clone() throws CloneNotSupportedException {
            return (BBBBinOp) super.clone();
        }
    }
    public class IConst extends IExpr {
        protected final int type = iConst;
        public final boolean isNeg = false;
        public int constant;
        public IConst(int _i){ constant = _i; }
        public IConst clone() throws CloneNotSupportedException {
            return (IConst) super.clone();
        }
        public IConst negate() throws CloneNotSupportedException {
            IConst e = clone();
            e.constant = -constant;
            return e;
        }
        public int getPrio(){ return 0; }
        public int eval() { return (isNeg?(constant==0?1:0):constant); }
        public int eval(SpecState _ss) { return (isNeg?(constant==0?1:0):constant); }
        public int eval(SpecState _ss, State _st) { return (isNeg?(constant==0?1:0):constant); }
        public int eval(CompState _cs) { return (isNeg?(constant==0?1:0):constant); }
        public int eval(Object _qs) { return (isNeg?(constant==0?1:0):constant); }
        public String toString() { return ""+constant; }
        public boolean hasStates() { return false; }
        public void linkStates() {}
        public void linkComps(Spec _spec) {}
    }
    public class BConst extends BExpr {
        protected final int type = bConst;
        public final boolean isNot = false;
        public int constant;
        public BConst(int _i){ constant = _i; }
        public BConst clone() throws CloneNotSupportedException {
            return (BConst) super.clone();
        }
        public BConst negate() throws CloneNotSupportedException {
            BConst e = clone();
            e.constant = (constant==0?1:0);
            return e;
        }
        public int getPrio(){ return 0; }
        public int eval() { return (isNot?-constant:constant); }
        public int eval(SpecState _ss) { return (isNot?-constant:constant); }
        public int eval(SpecState _ss, State _st) { return (isNot?constant:-constant); }
        public int eval(CompState _cs) { return (isNot?-constant:constant); }
        public int eval(Object _qs) { return (isNot?-constant:constant); }
        public String toString() { return ""+constant; }
        public boolean hasStates() { return false; }
        public void linkStates() {}
        public void linkComps(Spec _spec) {}
    }
    public class IVar extends IExpr {
        protected final int type = iVar;
        public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        public IVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v)
        throws Error {
            if (_v.getType() == IBSAttributeClass.BoolAttr)
                var = _v;
            else throw new Error("IVar: Bad Attribute Type");
        }
        public IVar clone() throws CloneNotSupportedException {
            return (IVar) super.clone();
        }
        public int getPrio(){ return 0; }
        public int eval(){ return 0; }
        public int eval(SpecState _ss) { return (isNeg?-var.getValue(_ss):var.getValue(_ss)); }
        public int eval(SpecState _ss, State _st) { return (isNeg?-var.getValue(_ss,_st):var.getValue(_ss,_st)); }
        public int eval(CompState _cs) { return (isNeg?-var.getValue(_cs):var.getValue(_cs)); }
        public int eval(Object _qs) { return (isNeg?-var.getValue(_qs):var.getValue(_qs)); }
        public String toString() {
            if (isNeg)
                return opString[iNeg] + var.toString();
            return var.toString();
        }
        public boolean hasStates() { return false; }
        public void linkStates() {var.linkState();}
        public void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public class BVar extends BExpr {
        protected final int type = bVar;
        public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        public BVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v)
                throws Error {
            if (_v.getType() == IBSAttributeClass.BoolAttr)
                var = _v;
            else throw new Error("IVar: Bad Attribute Type");
        }
        public BVar clone() throws CloneNotSupportedException {
            return (BVar) super.clone();
        }
        public int getPrio(){ return 0; }
        public int eval(){ return 0; }
        public int eval(SpecState _ss) { return (isNot?(var.getValue(_ss)==0?1:0):var.getValue(_ss)); }
        public int eval(SpecState _ss, State _st) { return (isNot?(var.getValue(_ss,_st)==0?1:0):var.getValue(_ss,_st)); }
        public int eval(CompState _cs) { return (isNot?(var.getValue(_cs)==0?1:0):var.getValue(_cs)); }
        public int eval(Object _qs) { return (isNot?(var.getValue(_qs)==0?1:0):var.getValue(_qs)); }
        public String toString() {
            if (isNot)
                return opString[bNot] + var.toString();
            return var.toString();
        }
        public boolean hasStates() { return var.isState(); }
        public void linkStates() {var.linkState();}
        public void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public class IIIPlus extends IIIBinOp {
        protected final int type = iiiPlus;
        public IIIPlus(IExpr _l, IExpr _r) { super(_l, _r); }
        public IIIPlus clone() throws CloneNotSupportedException {
            return (IIIPlus) super.clone();
        }
        public int getPrio() { return prios[iiiPlus]; }
        public int eval() { return (isNeg?-(left.eval() + right.eval()):(left.eval() + right.eval())); }
        public int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) + right.eval(_ss)):(left.eval(_ss) + right.eval(_ss)));
        }
        public int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) + right.eval(_ss,_st)):(left.eval(_ss,_st) + right.eval(_ss,_st)));
        }
        public int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) + right.eval(_cs)):(left.eval(_cs) + right.eval(_cs)));
        }
        public int eval(Object _qs){
            return (isNeg?-(left.eval() + right.eval()):(left.eval() + right.eval()));
        }
        public String toString() {
            String l = (left.getPrio() > prios[iiiPlus] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiPlus] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiPlus] + " " + r + ")";
            else
                return  l + " " + opString[iiiPlus] + " " + r;
        }
    }
    public class IIIMinus extends IIIBinOp {
        protected final int type = iiiMinus;
        public IIIMinus(IExpr _l, IExpr _r) { super(_l, _r); }
        public IIIMinus clone() throws CloneNotSupportedException {
            return (IIIMinus) super.clone();
        }
        public int getPrio() { return prios[iiiMinus]; }
        public int eval() { return (isNeg?-(left.eval() - right.eval()):(left.eval() - right.eval())); }
        public int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) - right.eval(_ss)):(left.eval(_ss) - right.eval(_ss)));
        }
        public int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) - right.eval(_ss,_st)):(left.eval(_ss,_st) - right.eval(_ss,_st)));
        }
        public int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) - right.eval(_cs)):(left.eval(_cs) - right.eval(_cs)));
        }
        public int eval(Object _qs){
            return (isNeg?-(left.eval() - right.eval()):(left.eval() - right.eval()));
        }
        public String toString() {
            String l = (left.getPrio() > prios[iiiMinus] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMinus] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiMinus] + " " + r + ")";
            else
                return  l + " " + opString[iiiMinus] + " " + r;
        }
    }
    public class IIIMult extends IIIBinOp {
        protected final int type = iiiMult;
        public IIIMult(IExpr _l, IExpr _r) { super(_l, _r); }
        public IIIMult clone() throws CloneNotSupportedException {
            return (IIIMult) super.clone();
        }
        public int getPrio() { return prios[iiiMult]; }
        public int eval() { return (isNeg?-(left.eval() * right.eval()):(left.eval() * right.eval())); }
        public int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) * right.eval(_ss)):(left.eval(_ss) * right.eval(_ss)));
        }
        public int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) * right.eval(_ss,_st)):(left.eval(_ss,_st) * right.eval(_ss,_st)));
        }
        public int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) * right.eval(_cs)):(left.eval(_cs) * right.eval(_cs)));
        }
        public int eval(Object _qs){
            return (isNeg?-(left.eval() * right.eval()):(left.eval() * right.eval()));
        }
        public String toString() {
            String l = (left.getPrio() > prios[iiiMult] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMult] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiMult] + " " + r + ")";
            else
                return  l + " " + opString[iiiMult] + " " + r;
        }
    }
    public class IIIDiv extends IIIBinOp {
        protected final int type = iiiDiv;
        public IIIDiv(IExpr _l, IExpr _r) { super(_l, _r); }
        public IIIDiv clone() throws CloneNotSupportedException {
            return (IIIDiv) super.clone();
        }
        public int getPrio() { return prios[iiiDiv]; }
        public int eval() { return (isNeg?-(left.eval() / right.eval()):(left.eval() / right.eval())); }
        public int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) / right.eval(_ss)):(left.eval(_ss) / right.eval(_ss)));
        }
        public int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) / right.eval(_ss,_st)):(left.eval(_ss,_st) / right.eval(_ss,_st)));
        }
        public int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) / right.eval(_cs)):(left.eval(_cs) / right.eval(_cs)));
        }
        public int eval(Object _qs){
            return (isNeg?-(left.eval() / right.eval()):(left.eval() / right.eval()));
        }
        public String toString() {
            String l = (left.getPrio() > prios[iiiDiv] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiDiv] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiDiv] + " " + r + ")";
            else
                return  l + " " + opString[iiiDiv] + " " + r;
        }
    }
    public class IIIMod extends IIIBinOp {
        protected final int type = iiiMod;
        public IIIMod(IExpr _l, IExpr _r) { super(_l, _r); }
        public IIIMod clone() throws CloneNotSupportedException {
            return (IIIMod) super.clone();
        }
        public int getPrio() { return prios[iiiMod]; }
        public int eval() { return (isNeg?-(left.eval() % right.eval()):(left.eval() % right.eval())); }
        public int eval(SpecState _ss) {
            return (isNeg?-(left.eval(_ss) % right.eval(_ss)):(left.eval(_ss) % right.eval(_ss)));
        }
        public int eval(SpecState _ss, State _st) {
            return (isNeg?-(left.eval(_ss,_st) % right.eval(_ss,_st)):(left.eval(_ss,_st) % right.eval(_ss,_st)));
        }
        public int eval(CompState _cs) {
            return (isNeg?-(left.eval(_cs) % right.eval(_cs)):(left.eval(_cs) % right.eval(_cs)));
        }
        public int eval(Object _qs){
            return (isNeg?-(left.eval() % right.eval()):(left.eval() % right.eval()));
        }
        public String toString() {
            String l = (left.getPrio() > prios[iiiMod] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMod] ? "(" +  right.toString() + ")" : right.toString());
            if (isNeg)
                return opString[iNeg] + "(" + l + " " + opString[iiiMod] + " " + r + ")";
            else
                return  l + " " + opString[iiiMod] + " " + r;
        }
    }
    public class BBBAnd extends BBBBinOp {
        protected final int type = bbbAnd;
        public BBBAnd(BExpr _l, BExpr _r) { super(_l, _r); }
        public BBBAnd clone() throws CloneNotSupportedException {
            return (BBBAnd) super.clone();
        }
        public int getPrio() { return prios[bbbAnd]; }
        public int eval() {
            boolean b = left.eval()!=0 && right.eval()!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = left.eval(_ss)!=0 && right.eval(_ss)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st)!=0 && right.eval(_ss,_st)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = left.eval(_cs)!=0 && right.eval(_cs)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = left.eval(_qs)!=0 && right.eval(_qs)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = (left.getPrio() > prios[bbbAnd] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[bbbAnd] ? "(" +  right.toString() + ")" : right.toString());
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[bbbAnd] + " " + r + ")";
            else
                return  l + " " + opString[bbbAnd] + " " + r;
        }
    }
    public class BBBOr extends BBBBinOp {
        protected final int type = bbbOr;
        public BBBOr(BExpr _l, BExpr _r){ super(_l, _r); }
        public BBBOr clone() throws CloneNotSupportedException {
            return (BBBOr) super.clone();
        }
        public int getPrio() { return prios[bbbOr]; }
        public int eval() {
            boolean b = left.eval()!=0 || right.eval()!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = left.eval(_ss)!=0 || right.eval(_ss)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st)!=0 || right.eval(_ss,_st)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = left.eval(_cs)!=0 || right.eval(_cs)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = left.eval(_qs)!=0 || right.eval(_qs)!=0;
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = (left.getPrio() > prios[bbbOr] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[bbbOr] ? "(" +  right.toString() + ")" : right.toString());
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[bbbOr] + " " + r + ")";
            else
                return  l + " " + opString[bbbOr] + " " + r;
        }
    }
    public class BIIEq extends BIIBinOp {
        protected final int type = biiEq;
        public BIIEq(IExpr _l, IExpr _r){ super(_l, _r); }
        public BIIEq clone() throws CloneNotSupportedException {
            return (BIIEq) super.clone();
        }
        public int getPrio() { return prios[biiEq]; }
        public int eval() {
            boolean b = left.eval() == right.eval();
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = left.eval(_ss) == right.eval(_ss);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) == right.eval(_ss,_st);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = left.eval(_cs) == right.eval(_cs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = left.eval(_qs) == right.eval(_qs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiEq] + " " + r + ")";
            else
                return  l + " " + opString[biiEq] + " " + r;
        }
    }
    public class BBBEq extends BBBBinOp {
        protected final int type = bbbEq;
        public BBBEq(BExpr _l, BExpr _r){ super(_l, _r); }
        public BBBEq clone() throws CloneNotSupportedException {
            return (BBBEq) super.clone();
        }
        public int getPrio() { return prios[bbbEq]; }
        public int eval() {
            boolean b = left.eval() == right.eval();
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = left.eval(_ss) == right.eval(_ss);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) == right.eval(_ss,_st);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = left.eval(_cs) == right.eval(_cs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = left.eval(_qs) == right.eval(_qs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[bbbEq] + " " + r + ")";
            else
                return  l + " " + opString[bbbEq] + " " + r;
        }
    }
    public class BIIDif extends BIIBinOp {
        protected final int type = biiDif;
        public BIIDif(IExpr _l, IExpr _r){ super(_l, _r); }
        public BIIDif clone() throws CloneNotSupportedException {
            return (BIIDif) super.clone();
        }
        public int getPrio() { return prios[biiDif]; }
        public int eval() {
            boolean b = left.eval() != right.eval();
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = left.eval(_ss) != right.eval(_ss);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) != right.eval(_ss,_st);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = left.eval(_cs) != right.eval(_cs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = left.eval(_qs) != right.eval(_qs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiDif] + " " + r + ")";
            else
                return  l + " " + opString[biiDif] + " " + r;
        }
    }
    public class BBBDif extends BBBBinOp {
        protected final int type = bbbDif;
        public BBBDif(BExpr _l, BExpr _r){ super(_l, _r); }
        public BBBDif clone() throws CloneNotSupportedException {
            return (BBBDif) super.clone();
        }
        public int getPrio() { return prios[bbbDif]; }
        public int eval() {
            boolean b = left.eval() != right.eval();
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = left.eval(_ss) != right.eval(_ss);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = left.eval(_ss,_st) != right.eval(_ss,_st);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = left.eval(_cs) != right.eval(_cs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = left.eval(_qs) != right.eval(_qs);
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            return left.toString() + " " + opString[bbbDif] + " " + right.toString();
        }
    }
    public class BIILt extends BIIBinOp {
        protected final int type = biiLt;
        public BIILt(IExpr _l, IExpr _r) { super(_l, _r); }
        public BIILt clone() throws CloneNotSupportedException {
            return (BIILt) super.clone();
        }
        public int getPrio() { return prios[biiLt]; }
        public int eval() {
            boolean b = (isNot?left.eval() >= right.eval():left.eval() < right.eval());
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = (isNot?left.eval(_ss) >= right.eval(_ss):left.eval(_ss) < right.eval(_ss));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = (isNot?left.eval(_ss,_st) >= right.eval(_ss,_st):left.eval(_ss,_st) < right.eval(_ss,_st));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = (isNot?left.eval(_cs) >= right.eval(_cs):left.eval(_cs) < right.eval(_cs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = (isNot?left.eval(_qs) >= right.eval(_qs):left.eval(_qs) < right.eval(_qs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiLt] + " " + r + ")";
            else
                return  l + " " + opString[biiLt] + " " + r;
        }
    }
    public class BIIGt extends BIIBinOp {
        protected final int type = biiGt;
        public BIIGt(IExpr _l, IExpr _r) { super(_l, _r); }
        public BIIGt clone() throws CloneNotSupportedException {
            return (BIIGt) super.clone();
        }
        public int getPrio() { return prios[biiGt]; }
        public int eval() {
            boolean b = (isNot?left.eval() <= right.eval():left.eval() > right.eval());
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = (isNot?left.eval(_ss) <= right.eval(_ss):left.eval(_ss) > right.eval(_ss));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = (isNot?left.eval(_ss,_st) <= right.eval(_ss,_st):left.eval(_ss,_st) > right.eval(_ss,_st));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = (isNot?left.eval(_cs) <= right.eval(_cs):left.eval(_cs) > right.eval(_cs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = (isNot?left.eval(_qs) <= right.eval(_qs):left.eval(_qs) > right.eval(_qs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiGt] + " " + r + ")";
            else
                return  l + " " + opString[biiGt] + " " + r;
        }
    }
    public class BIILeq extends BIIBinOp {
        protected final int type = biiLeq;
        public BIILeq(IExpr _l, IExpr _r){ super(_l, _r); }
        public BIILeq clone() throws CloneNotSupportedException {
            return (BIILeq) super.clone();
        }
        public int getPrio() { return prios[biiLeq]; }
        public int eval() {
            boolean b = (isNot?left.eval() > right.eval():left.eval() <= right.eval());
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = (isNot?left.eval(_ss) > right.eval(_ss):left.eval(_ss) <= right.eval(_ss));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = (isNot?left.eval(_ss,_st) > right.eval(_ss,_st):left.eval(_ss,_st) <= right.eval(_ss,_st));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = (isNot?left.eval(_cs) > right.eval(_cs):left.eval(_cs) <= right.eval(_cs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = (isNot?left.eval(_qs) > right.eval(_qs):left.eval(_qs) <= right.eval(_qs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiLeq] + " " + r + ")";
            else
                return  l + " " + opString[biiLeq] + " " + r;
        }
    }
    public class BIIGeq extends BIIBinOp {
        protected final int type = biiGeq;
        public BIIGeq(IExpr _l, IExpr _r) { super(_l, _r); }
        public BIIGeq clone() throws CloneNotSupportedException {
            return (BIIGeq) super.clone();
        }
        public int getPrio() { return prios[biiGeq]; }
        public int eval() {
            boolean b = (isNot?left.eval() < right.eval():left.eval() >= right.eval());
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss) {
            boolean b = (isNot?left.eval(_ss) < right.eval(_ss):left.eval(_ss) >= right.eval(_ss));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            boolean b = (isNot?left.eval(_ss,_st) < right.eval(_ss,_st):left.eval(_ss,_st) >= right.eval(_ss,_st));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(CompState _cs) {
            boolean b = (isNot?left.eval(_cs) < right.eval(_cs):left.eval(_cs) >= right.eval(_cs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public int eval(Object _qs) {
            boolean b = (isNot?left.eval(_qs) < right.eval(_qs):left.eval(_qs) >= right.eval(_qs));
            return (isNot?(b?0:1):(b?1:0));
        }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiGeq] + " " + r + ")";
            else
                return  l + " " + opString[biiGeq] + " " + r;
        }
    }
    public class BIExpr extends BExpr {
        protected final int type = biExpr;
        IExpr iExpr;
        public BIExpr(IExpr _e) { iExpr=_e; }
        public BIExpr clone() throws CloneNotSupportedException {
            return (BIExpr) super.clone();
        }
        public int getPrio() { return 0; }
        public int eval() {
            return (isNot?(iExpr.eval()!=0?0:1):(iExpr.eval()!=0?1:0));
        }
        public int eval(SpecState _ss) {
            return (isNot?(iExpr.eval(_ss)!=0?0:1):(iExpr.eval(_ss)!=0?1:0));
        }
        public int eval(SpecState _ss, State _st) {
            return (isNot?(iExpr.eval(_ss,_st)!=0?0:1):(iExpr.eval(_ss,_st)!=0?1:0));
        }
        public int eval(CompState _cs) {
            return (isNot?(iExpr.eval(_cs)!=0?0:1):(iExpr.eval(_cs)!=0?1:0));
        }
        public int eval(Object _qs) {
            return (isNot?(iExpr.eval(_qs)!=0?0:1):(iExpr.eval(_qs)!=0?1:0));
        }
        public String toString() {
            String s = iExpr.toString();
            if (isNot)
                return opString[bNot] + "(" + s + ")";
            else
                return  s;
        };
        public boolean hasStates() { return false; };
        public void linkStates() { iExpr.linkStates(); };
        public void linkComps(Spec _spec) { iExpr.linkComps(_spec); };
    }
}
