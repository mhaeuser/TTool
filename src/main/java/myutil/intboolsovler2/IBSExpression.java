package myutil.intboolsovler2;

public class IBSExpression <
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    public boolean make_iiiPlus(int _left, int _right, int _tgt) { return false; }
    public boolean make_iiiMinus(int _left, int _right, int _tgt) { return false; }
    public boolean make_iiiMult(int _left, int _right, int _tgt) { return false; }
    public boolean make_iiiDiv(int _left, int _right, int _tgt) { return false; }
    public boolean make_iiiMod(int _left, int _right, int _tgt) { return false; }
    public boolean make_bbbAnd(int _left, int _right, int _tgt) { return false; }
    public boolean make_bbbOr(int _left, int _right, int _tgt) { return false; }
    public boolean make_biiEq(int _left, int _right, int _tgt) { return false; }
    public boolean make_bbbEq(int _left, int _right, int _tgt) { return false; }
    public boolean make_biiDif(int _left, int _right, int _tgt) { return false; }
    public boolean make_bbbDif(int _left, int _right, int _tgt) { return false; }
    public boolean make_biiLt(int _left, int _right, int _tgt) { return false; }
    public boolean make_biiGt(int _left, int _right, int _tgt) { return false; }
    public boolean make_biiLeq(int _left, int _right, int _tgt) { return false; }
    public boolean make_biiGeq(int _left, int _right, int _tgt) { return false; }
    public boolean make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v, int _tgt) { return false; }
    public boolean make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v, int _tgt) { return false; }
    public boolean make_iConst(int _i, int _tgt) { return false; }
    public boolean make_bConst(boolean _b, int _tgt) { return false; }
    public boolean make_iNeg(int _expr, int _tgt) throws CloneNotSupportedException  { return false; }
    public boolean make_bNot(int _expr, int _tgt)  throws CloneNotSupportedException { return false; }
    public boolean make_biExpr(int _expr, int _tgt) { return false; }
    public IExpr getIExpr(int _expr) { return null; }
    public BExpr getBExpr(int _expr) { return null; }

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
