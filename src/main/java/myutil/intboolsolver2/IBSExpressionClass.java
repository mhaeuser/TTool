package myutil.intboolsolver2;

public class IBSExpressionClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    public void freeInt(int _toFree) {}
    public void freeBool(int _toFree) {}
    public IExpr getIExpr(int _expr) { return null; }
    public BExpr getBExpr(int _expr) { return null; }
    public int make_iiiPlus(int _left, int _right) { return -1; }
    public int make_iiiMinus(int _left, int _right) { return -1; }
    public int make_iiiMult(int _left, int _right) { return -1; }
    public int make_iiiDiv(int _left, int _right) { return -1; }
    public int make_iiiMod(int _left, int _right) { return -1; }
    public int make_bbbAnd(int _left, int _right) { return -1; }
    public int make_bbbOr(int _left, int _right) { return -1; }
    public int make_biiEq(int _left, int _right) { return -1; }
    public int make_bbbEq(int _left, int _right) { return -1; }
    public int make_biiDif(int _left, int _right) { return -1; }
    public int make_bbbDif(int _left, int _right) { return -1; }
    public int make_biiLt(int _left, int _right) { return -1; }
    public int make_biiGt(int _left, int _right) { return -1; }
    public int make_biiLeq(int _left, int _right) { return -1; }
    public int make_biiGeq(int _left, int _right) { return -1; }
    public int make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    public int make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    public int make_biVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return -1; }
    public int make_iConst(int _i) { return -1; }
    public int make_bConst(boolean _b) { return -1; }
    public int make_iNeg(int _expr) throws CloneNotSupportedException  { return -1; }
    public int make_bNot(int _expr)  throws CloneNotSupportedException { return -1; }
    public int make_biExpr(int _expr) { return -1; }

    public abstract class Expr implements Cloneable {
        public Expr clone() throws CloneNotSupportedException {
            return (Expr) super.clone();
        }
        public abstract String toString();
        public abstract boolean hasStates();
        public abstract void linkStates();
        public abstract void linkComps(Spec _spec);
    }
    public abstract class IExpr extends Expr {
        public IExpr clone() throws CloneNotSupportedException {
            return (IExpr) super.clone();
        }
        public abstract int eval();
        public abstract int eval(SpecState _ss);
        public abstract int eval(SpecState _ss, State _st);
        public abstract int eval(CompState _cs);
        public abstract int eval(Object _qs);
    }
    public abstract class BExpr extends Expr {
        public BExpr clone() throws CloneNotSupportedException {
            return (BExpr) super.clone();
        }
        public abstract boolean eval();
        public abstract boolean eval(SpecState _ss);
        public abstract boolean eval(SpecState _ss, State _st);
        public abstract boolean eval(CompState _cs);
        public abstract boolean eval(Object _qs);
    }
}
