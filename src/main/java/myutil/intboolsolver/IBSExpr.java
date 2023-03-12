package myutil.intboolsolver;

public class IBSExpr <
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState,
        ATT extends IBSAttribute<Spec,Comp,State,SpecState,CompState>
        > {
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
    public abstract class BBinOp extends IExpr{
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
    }
    public abstract class BIIBinOp extends BBinOp{
        public IExpr left;
        public IExpr right;
    }
    public abstract class BBBBinOp extends BBinOp{
        public BExpr left;
        public BExpr right;
    }
    public abstract class IConst extends IExpr {
        public int constant;
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
        public int constant;
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
        public ATT var;
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
        public ATT var;
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
        public boolean hasStates() { return false; }
        public void linkStates() {var.linkState();}
        public void linkComps(Spec _spec) {var.linkComp(_spec);}
    }
    public class IIIPlus extends IIIBinOp {
        public int getPrio() { return prios[iiiPlus]; }
        public int eval() { return left.eval() + right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) + right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) + right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) + right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) + right.eval(_qs); }
        public String toString() {
            String l = (left.getPrio() > prios[iiiPlus] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiPlus] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[iiiPlus] + " " + r;
        }
    }
    public class IIIMinus extends IIIBinOp {
        public int getPrio() { return prios[iiiMinus]; }
        public int eval() { return left.eval() - right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) - right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) - right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) - right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) - right.eval(_qs); }
        public String toString() {
            String l = (left.getPrio() > prios[iiiMinus] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMinus] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[iiiMinus] + " " + r;
        }
    }
    public class IIIMult extends IIIBinOp {
        public int getPrio() { return prios[iiiMult]; }
        public int eval() { return left.eval() * right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) * right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) * right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) * right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) * right.eval(_qs); }
        public String toString() {
            String l = (left.getPrio() > prios[iiiMult] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMult] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[iiiMult] + " " + r;
        }
    }
    public class IIIDiv extends IIIBinOp {
        public int getPrio() { return prios[iiiDiv]; }
        public int eval() { return left.eval() / right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) / right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) / right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) / right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) / right.eval(_qs); }
        public String toString() {
            String l = (left.getPrio() > prios[iiiDiv] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiDiv] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[iiiDiv] + " " + r;
        }
    }
    public class IIIMod extends IIIBinOp {
        public int getPrio() { return prios[iiiMod]; }
        public int eval() { return left.eval() % right.eval(); }
        public int eval(SpecState _ss) { return left.eval(_ss) % right.eval(_ss); }
        public int eval(SpecState _ss, State _st) { return left.eval(_ss,_st) % right.eval(_ss,_st); }
        public int eval(CompState _cs) { return left.eval(_cs) % right.eval(_cs); }
        public int eval(Object _qs) { return left.eval(_qs) % right.eval(_qs); }
        public String toString() {
            String l = (left.getPrio() > prios[iiiMod] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[iiiMod] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[iiiMod] + " " + r;
        }
    }
    public class BBBAnd extends BBBBinOp {
        public int getPrio() { return prios[bbbAnd]; }
        public int eval() { return (left.eval()!=0 && right.eval()!=0?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss)!=0 && right.eval(_ss)!=0?1:0); }
        public int eval(SpecState _ss, State _st) { return (left.eval(_ss,_st)!=0 && right.eval(_ss,_st)!=0?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs)!=0 && right.eval(_cs)!=0?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs)!=0 && right.eval(_qs)!=0?1:0); }
        public String toString() {
            String l = (left.getPrio() > prios[bbbAnd] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[bbbAnd] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[bbbAnd] + " " + r;
        }
    }
    public class BBBOr extends BBBBinOp {
        public int getPrio() { return prios[bbbOr]; }
        public int eval() { return (left.eval()!=0 || right.eval()!=0?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss)!=0 || right.eval(_ss)!=0?1:0); }
        public int eval(SpecState _ss, State _st) { return (left.eval(_ss,_st)!=0 || right.eval(_ss,_st)!=0?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs)!=0 || right.eval(_cs)!=0?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs)!=0 || right.eval(_qs)!=0?1:0); }
        public String toString() {
            String l = (left.getPrio() > prios[bbbOr] ? "(" +  left.toString() + ")" : left.toString());
            String r = (right.getPrio() > prios[bbbOr] ? "(" +  right.toString() + ")" : right.toString());
            return l + " " + opString[bbbOr] + " " + r;
        }
    }
    public class BIIEq extends BIIBinOp {
        public int getPrio() { return prios[biiEq]; }
        public int eval() { return (left.eval() == right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) == right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) == right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) == right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) == right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[biiEq] + " " + right.toString();
        }
    }
    public class BBBEq extends BBBBinOp {
        public int getPrio() { return prios[bbbEq]; }
        public int eval() { return (left.eval() == right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) == right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) == right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) == right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) == right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[bbbEq] + " " + right.toString();
        }
    }
    public class BIIDif extends BIIBinOp {
        public int getPrio() { return prios[biiDif]; }
        public int eval() { return (left.eval() != right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) != right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) != right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) != right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) != right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[biiDif] + " " + right.toString();
        }
    }
    public class BBBDif extends BBBBinOp {
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
        public int getPrio() { return prios[biiLt]; }
        public int eval() { return (left.eval() < right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) < right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) < right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) < right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) < right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[biiLt] + " " + right.toString();
        }
    }
    public class BIIGt extends BIIBinOp {
        public int getPrio() { return prios[biiGt]; }
        public int eval() { return (left.eval() > right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) > right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) > right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) > right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) > right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[biiGt] + " " + right.toString();
        }
    }
    public class BIILeq extends BIIBinOp {
        public int getPrio() { return prios[biiLeq]; }
        public int eval() { return (left.eval() <= right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) <= right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) <= right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) <= right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) <= right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[biiLeq] + " " + right.toString();
        }
    }
    public class BIIGeq extends BIIBinOp {
        public int getPrio() { return prios[biiGeq]; }
        public int eval() { return (left.eval() >= right.eval()?1:0); }
        public int eval(SpecState _ss) { return (left.eval(_ss) >= right.eval(_ss)?1:0); }
        public int eval(SpecState _ss, State _st) {return (left.eval(_ss,_st) >= right.eval(_ss,_st)?1:0); }
        public int eval(CompState _cs) { return (left.eval(_cs) >= right.eval(_cs)?1:0); }
        public int eval(Object _qs) { return (left.eval(_qs) >= right.eval(_qs)?1:0); }
        public String toString() {
            return left.toString() + " " + opString[biiGeq] + " " + right.toString();
        }
    }
}
