package myutil.intboolsovler2;

public class IBSExpression <
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    public IExpr make_iiiPlus(IExpr _left, IExpr _right) { return null; }
    public IExpr make_iiiMinus(IExpr _left, IExpr _right) { return null; }
    public IExpr make_iiiMult(IExpr _left, IExpr _right) { return null; }
    public IExpr make_iiiDiv(IExpr _left, IExpr _right) { return null; }
    public IExpr make_iiiMod(IExpr _left, IExpr _right) { return null; }
    public BExpr make_bbbAnd(BExpr _left, BExpr _right) { return null; }
    public BExpr make_bbbOr(BExpr _left, BExpr _right) { return null; }
    public BExpr make_biiEq(IExpr _left, IExpr _right) { return null; }
    public BExpr make_bbbEq(BExpr _left, BExpr _right) { return null; }
    public BExpr make_biiDif(IExpr _left, IExpr _right) { return null; }
    public BExpr make_bbbDif(BExpr _left, BExpr _right) { return null; }
    public BExpr make_biiLt(IExpr _left, IExpr _right) { return null; }
    public BExpr make_biiGt(IExpr _left, IExpr _right) { return null; }
    public BExpr make_biiLeq(IExpr _left, IExpr _right) { return null; }
    public BExpr make_biiGeq(IExpr _left, IExpr _right) { return null; }
    public IExpr make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return null; }
    public BExpr make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) { return null; }
    public IExpr make_iConst(int _i) { return null; }
    public BExpr make_bConst(int _i) { return null; }
    public IExpr make_iNeg(IExpr _expr) { return null; }
    public BExpr make_bNot(BExpr _expr) { return null; }
    public BExpr make_biExpr(IExpr _expr) { return null; }

    public abstract class Expr {
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
    public abstract class IExpr extends Expr {}
    public abstract class BExpr extends Expr {}
}
