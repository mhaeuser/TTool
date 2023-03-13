package myutil.intboolsovler2;

public class IBSExpr <
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > extends IBSExpression{
    private final int[] prios =        { 2 , 2 , 1 , 1 , 1 , 1  ,  2 ,  3 ,  3 ,  3 ,  3 , 3 , 3 ,  3 ,  3 };
    private final String[] opString = {"+","-","*","/","%","&&","||","==","==","!=","!=","<",">","<=",">=","-","!"};
    private final int iiiPlus = 0;
    private final int iiiMinus = 1;
    private final int iiiMult = 2;
    private final int iiiDiv = 3;
    private final int iiiMod = 4;
    private final int bbbAnd = 5;
    private final int bbbOr = 6;
    private final int biiEq = 7;
    private final int bbbEq = 8;
    private final int biiDif = 9;
    private final int bbbDif = 10;
    private final int biiLt = 11;
    private final int biiGt = 12;
    private final int biiLeq = 13;
    private final int biiGeq = 14;
    private final int iNeg = 13;
    private final int bNot = 14;
    private final int biExpr = 1000; // no associated symbol

    public IExpr make_iiiPlus(IExpr left, IExpr right) { return null; }
    public IExpr make_iiiMinus(IExpr left, IExpr right) { return null; }
    public IExpr make_iiiMult(IExpr left, IExpr right) { return null; }
    public IExpr make_iiiDiv(IExpr left, IExpr right) { return null; }
    public IExpr make_iiiMod(IExpr left, IExpr right) { return null; }
    public BExpr make_bbbAnd(BExpr left, BExpr right) { return null; }
    public BExpr make_bbbOr(BExpr left, BExpr right) { return null; }
    public BExpr make_biiEq(IExpr left, IExpr right) { return null; }
    public BExpr make_bbbEq(BExpr left, BExpr right) { return null; }
    public BExpr make_biiDif(IExpr left, IExpr right) { return null; }
    public BExpr make_bbbDif(BExpr left, BExpr right) { return null; }
    public BExpr make_biiLt(IExpr left, IExpr right) { return null; }
    public BExpr make_biiGt(IExpr left, IExpr right) { return null; }
    public BExpr make_biiLeq(IExpr left, IExpr right) { return null; }
    public BExpr make_biiGeq(IExpr left, IExpr right) { return null; }
    public IExpr make_iNeg(IExpr expr) { return null; }
    public BExpr make_bNot(BExpr expr) { return null; }
    public BExpr make_biExpr(IExpr expr) { return null; }

    public abstract class Expr{
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
    }
    public abstract class BExpr extends Expr{
        public boolean isNot;
    }
    public abstract class IBinOp extends IExpr{
        public Expr left;
        public Expr right;
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
    }
    public abstract class BIIBinOp extends BBinOp{
        public IExpr left;
        public IExpr right;
        public BIIBinOp(IExpr _l, IExpr _r){
            left  = _l;
            right = _r;
        }
    }
    public abstract class BBBBinOp extends BBinOp{
        public BExpr left;
        public BExpr right;
        public BBBBinOp(BExpr _l, BExpr _r){
            left  = _l;
            right = _r;
        }
    }
    public abstract class IConst extends IExpr {
        public final boolean isNeg = false;
        public int constant;
        public IConst(int _i){ constant = _i; }
        public int eval(){ return constant; }
        public int eval(SpecState _ss) { return constant; }
        public int eval(SpecState _ss, State _st) { return constant; }
        public int eval(CompState _cs) { return constant; }
        public int eval(Object _qs) { return constant; }
        public String toString() { return ""+constant; }
        public boolean hasStates() { return false; }
        public void linkStates() {}
        public void linkComps(Spec _spec) {}
    }
    public class BConst extends BExpr {
        public final boolean isNot = false;
        public int constant;
        public BConst(int _i){ constant = _i; }
        public int getPrio(){ return 0; }
        public int eval(){ return constant; }
        public int eval(SpecState _ss) { return constant; }
        public int eval(SpecState _ss, State _st) { return constant; }
        public int eval(CompState _cs) { return constant; }
        public int eval(Object _qs) { return constant; }
        public String toString() { return ""+constant; }
        public boolean hasStates() { return false; }
        public void linkStates() {}
        public void linkComps(Spec _spec) {}
    }
    public class IVar extends IExpr {
        public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        public IVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v)
        throws Error {
            if (_v.getType() == IBSAttributeClass.BoolAttr)
                var = _v;
            else throw new Error("IVar: Bad Attribute Type");
        }
        public int getPrio(){ return 0; }
        public int eval(){ return 0; }
        public int eval(SpecState _ss) { return var.getValue(_ss); }
        public int eval(SpecState _ss, State _st) { return var.getValue(_ss, _st); }
        public int eval(CompState _cs) { return var.getValue(_cs); }
        public int eval(Object _qs) { return var.getValue(_qs); }
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
        public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute var;
        public BVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v)
                throws Error {
            if (_v.getType() == IBSAttributeClass.BoolAttr)
                var = _v;
            else throw new Error("IVar: Bad Attribute Type");
        }
        public int getPrio(){ return 0; }
        public int eval(){ return 0; }
        public int eval(SpecState _ss) { return var.getValue(_ss); }
        public int eval(SpecState _ss, State _st) { return var.getValue(_ss, _st); }
        public int eval(CompState _cs) { return var.getValue(_cs); }
        public int eval(Object _qs) { return var.getValue(_qs); }
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
        public IIIPlus(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[iiiPlus]; }
        public int eval() { return left.eval() + right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) + right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) + right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) + right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) + right.eval(_qs); }
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
        public IIIMinus(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[iiiMinus]; }
        public int eval() { return left.eval() - right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) - right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) - right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) - right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) - right.eval(_qs); }
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
        public IIIMult(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[iiiMult]; }
        public int eval() { return left.eval() * right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) * right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) * right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) * right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) * right.eval(_qs); }
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
        public IIIDiv(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[iiiDiv]; }
        public int eval() { return left.eval() / right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) / right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) / right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) / right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) / right.eval(_qs); }
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
        public IIIMod(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[iiiMod]; }
        public int eval() { return left.eval() % right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) % right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) % right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) % right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) % right.eval(_qs); }
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
        public BBBAnd(BExpr _l, BExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[bbbAnd]; }
        public int eval() { return (left.eval()!=0 && right.eval()!=0?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss)!=0 && right.eval(_ss)!=0?1:0); }
        public int eval(SpecState _ss, State _st) { return (left.eval(_ss,_st)!=0 && right.eval(_ss,_st)!=0?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs)!=0 && right.eval(_cs)!=0?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs)!=0 && right.eval(_qs)!=0?1:0); }
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
        public BBBOr(BExpr _l, BExpr _r){ super(_l, _r); }
        public int getPrio() { return prios[bbbOr]; }
        public int eval() { return (left.eval()!=0 || right.eval()!=0?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss)!=0 || right.eval(_ss)!=0?1:0); }
        public int eval(SpecState _ss, State _st) { return (left.eval(_ss,_st)!=0 || right.eval(_ss,_st)!=0?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs)!=0 || right.eval(_cs)!=0?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs)!=0 || right.eval(_qs)!=0?1:0); }
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
        public BIIEq(IExpr _l, IExpr _r){ super(_l, _r); }
        public int getPrio() { return prios[biiEq]; }
        public int eval() { return (left.eval() == right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) == right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) == right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) == right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) == right.eval(_qs)?1:0); }
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
        public BBBEq(BExpr _l, BExpr _r){ super(_l, _r); }
        public int getPrio() { return prios[bbbEq]; }
        public int eval() { return (left.eval() == right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) == right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) == right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) == right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) == right.eval(_qs)?1:0); }
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
        public BIIDif(IExpr _l, IExpr _r){ super(_l, _r); }
        public int getPrio() { return prios[biiDif]; }
        public int eval() { return (left.eval() != right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) != right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) != right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) != right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) != right.eval(_qs)?1:0); }
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
        public BBBDif(BExpr _l, BExpr _r){ super(_l, _r); }
        public int getPrio() { return prios[bbbDif]; }
        public int eval() { return (left.eval() != right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) != right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) != right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) != right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) != right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[bbbDif] + " " + right.toString();
        }
    }
    public class BIILt extends BIIBinOp {
        public BIILt(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[biiLt]; }
        public int eval() { return (left.eval() < right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) < right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) < right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) < right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) < right.eval(_qs)?1:0); }
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
        public BIIGt(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[biiGt]; }
        public int eval() { return (left.eval() > right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) > right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) > right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) > right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) > right.eval(_qs)?1:0); }
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
        public BIILeq(IExpr _l, IExpr _r){ super(_l, _r); }
        public int getPrio() { return prios[biiLeq]; }
        public int eval() { return (left.eval() <= right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) <= right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) <= right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) <= right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) <= right.eval(_qs)?1:0); }
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
        public BIIGeq(IExpr _l, IExpr _r) { super(_l, _r); }
        public int getPrio() { return prios[biiGeq]; }
        public int eval() { return (left.eval() >= right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) >= right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) >= right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) >= right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) >= right.eval(_qs)?1:0); }
        public String toString() {
            String l = left.toString();
            String r = right.toString();
            if (isNot)
                return opString[bNot] + "(" + l + " " + opString[biiGeq] + " " + r + ")";
            else
                return  l + " " + opString[biiGeq] + " " + r;
        }
    }
    public class biExpr extends BExpr {
        IExpr iExpr;
        public biExpr(IExpr _e) { iExpr=_e; }
        public int getPrio() { return 0; }
        public int eval() { return (iExpr.eval()!=0?1:0); }
        public int eval(SpecState _ss) { return (iExpr.eval(_ss)!=0?1:0); }
        public int eval(SpecState _ss, State _st) { return (iExpr.eval(_ss,_st)!=0?1:0); }
        public int eval(CompState _cs) { return (iExpr.eval(_cs)!=0?1:0); }
        public int eval(Object _qs) { return (iExpr.eval(_qs)!=0?1:0); }
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
