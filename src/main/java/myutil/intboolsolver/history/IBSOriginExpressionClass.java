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

/**
 * Class OriginExpr.
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */

public class IBSOriginExpressionClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState>
        extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState> {
    private ArrayList<IExpr> iExpressions = new ArrayList<IExpr>(16);
    private ArrayList<Boolean> iBusy = new ArrayList<Boolean>(16);
    private ArrayList<BExpr> bExpressions = new ArrayList<BExpr>(16);
    private ArrayList<Boolean> bBusy = new ArrayList<Boolean>(16);
    public IBSOriginExpressionClass(){}
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
            throw new Error("IBSOriginExpressionClass.getIExpr: out of bounds");
        else
            return iExpressions.get(_expr);
    }
    public BExpr getBExpr(int _expr) {
        if (_expr >= bExpressions.size() || _expr < 0)
            throw new Error("IBSOriginExpressionClass.getBExpr: out of bounds");
        else
            return bExpressions.get(_expr);
    }

    public int make_iiiPlus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_iiiPlus called on undefined subexpression");
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '+';
        e.isLeaf = false;
        iExpressions.set(tgt,e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiMinus(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_iiiMinus called on undefined subexpression");
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '-';
        e.isLeaf = false;
        iExpressions.set(tgt,e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiMult(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_iiiMult called on undefined subexpression");
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '*';
        e.isLeaf = false;
        iExpressions.set(tgt,e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiDiv(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_iiiDiv called on undefined subexpression");
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '/';
        e.isLeaf = false;
        iExpressions.set(tgt,e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iiiMod(int _left, int _right) {
        int tgt = findIfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_iiiMod called on undefined subexpression");
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '%';
        e.isLeaf = false;
        iExpressions.set(tgt,e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbAnd(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_bbbAnd called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bbb;
        e.bleft = bExpressions.get(_left);
        e.bright = bExpressions.get(_right);
        e.operator = '&';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbOr(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_bbbOr called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bbb;
        e.bleft = bExpressions.get(_left);
        e.bright = bExpressions.get(_right);
        e.operator = '|';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiEq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                    iExpressions.get(_left) == null || iExpressions.get(_right) == null)
                throw new Error("IBSOriginExpressionClass.make_biiEq called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bii;
        e.ileft = iExpressions.get(_left);
        e.iright = iExpressions.get(_right);
        e.operator = '=';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bbbEq(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_bbbEq called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bbb;
        e.bleft = bExpressions.get(_left);
        e.bright = bExpressions.get(_right);
        e.operator = '=';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biiDif(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_biiDif called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bii;
        e.ileft = iExpressions.get(_left);
        e.iright = iExpressions.get(_right);
        e.operator = '$';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }

    public int make_bbbDif(int _left, int _right) {
        int tgt = findBfree();
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || 0 > _left || 0 > _right ||
                bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_bbbDif called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bbb;
        e.bleft = bExpressions.get(_left);
        e.bright = bExpressions.get(_right);
        e.operator = '$';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }

    public int make_biiLt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_biiLt called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bii;
        e.ileft = iExpressions.get(_left);
        e.iright = iExpressions.get(_right);
        e.operator = '<';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }

    public int make_biiGt(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_biiGt called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bii;
        e.ileft = iExpressions.get(_left);
        e.iright = iExpressions.get(_right);
        e.operator = '>';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }

    public int make_biiLeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_biiLeq called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bii;
        e.ileft = iExpressions.get(_left);
        e.iright = iExpressions.get(_right);
        e.operator = ';';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }

    public int make_biiGeq(int _left, int _right) {
        int tgt = findBfree();
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || 0 > _left || 0 > _right ||
                iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            throw new Error("IBSOriginExpressionClass.make_biiGeq called on undefined subexpression");
        BExpr e = new BExpr();
        e.type = BExpr.bii;
        e.ileft = iExpressions.get(_left);
        e.iright = iExpressions.get(_right);
        e.operator = ':';
        bExpressions.set(tgt,e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findIfree();
        if (_v == null)
            throw new Error("IBSOriginExpressionClass.make_iVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        IExpr e = new IExpr();
        e.leaf = _v;
        iExpressions.set(tgt, e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tgt = findBfree();
        if (_v == null)
            throw new Error("IBSOriginExpressionClass.make_bVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.BoolAttr)
            return -1;
        BExpr e = new BExpr();
        e.type = BExpr.bVar;
        e.leaf = _v;
        bExpressions.set(tgt, e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v) {
        int tmp = findIfree();
        if (_v == null)
            throw new Error("IBSOriginExpressionClass.make_biVar called on null attribute");
        if (_v.getType() != IBSAttributeClass.IntAttr)
            return -1;
        IExpr e = new IExpr();
        e.leaf = _v;
        iExpressions.set(tmp, e);
        iBusy.set(tmp,Boolean.TRUE);
        int tgt = make_biExpr(tmp);
        freeInt(tmp);
        return tgt;
    }

    public int make_iConst(int _i) {
        int tgt = findIfree();
        IExpr e = new IExpr();
        e.isImmediateValue = true; //0: No; 1: Int; 2: Boolean;
        e.intValue = _i;
        iExpressions.set(tgt, e);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }

    public int make_bConst(boolean _b) {
        int tgt = findBfree();
        BExpr e = new BExpr();
        e.type = BExpr.bCst; //0: No; 1: Int; 2: Boolean;
        e.boolValue = _b;
        bExpressions.set(tgt, e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_iNeg(int _expr){
        int tgt = findIfree();
        if (iExpressions.size()<=_expr || _expr < 0 ||iExpressions.get(_expr) == null)
            throw new Error("IBSOriginExpressionClass.make_iNeg called on undefined subexpression");
        IExpr e = iExpressions.get(_expr);
        IExpr res = new IExpr();
        res.isNegated = (e.isImmediateValue?e.isNegated:!e.isNegated);
        res.left = e.left;
        res.right= e.right;
        res.operator = e.operator;
        res.isLeaf = e.isLeaf;
        res.isImmediateValue = e.isImmediateValue;
        res.intValue = (e.isImmediateValue? -e.intValue: e.intValue);
        res.leaf = e.leaf;
        iExpressions.set(tgt, res);
        iBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_bNot(int _expr) {
        int tgt = findBfree();
        if (bExpressions.size()<=_expr || _expr < 0 || bExpressions.get(_expr) == null)
            throw new Error("IBSOriginExpressionClass.make_bNot called on undefined subexpression");
        BExpr e = bExpressions.get(_expr);
        BExpr res = new BExpr();
        res.type = e.type;
        res.bleft = e.bleft;
        res.bright= e.bright;
        res.ileft = e.ileft;
        res.iright= e.iright;
        res.operator= e.operator;
        res.isNot = (e.type==BExpr.bCst?e.isNot:!e.isNot);
        res.boolValue = (e.type==BExpr.bCst?!e.boolValue:e.boolValue);
        bExpressions.set(tgt, res);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public int make_biExpr(int _expr) {
        int tgt = findBfree();
        if (iExpressions.size()<=_expr || _expr < 0 || iExpressions.get(_expr) == null)
            throw new Error("IBSOriginExpressionClass.make_biExpr called on undefined subexpression");
        IExpr expr = iExpressions.get(_expr);
        BExpr e = new BExpr();
        e.type = BExpr.bi;
        e.ileft = expr;
        bExpressions.set(tgt, e);
        bBusy.set(tgt,Boolean.TRUE);
        return tgt;
    }
    public class IExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr {
        public IExpr left, right;
        public char operator;
        public boolean isLeaf = true;
        public boolean isNegated = false;
        public boolean isImmediateValue = false; //0: No; 1: Int; 2: Boolean;
        public int intValue = 0;
        public IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute leaf;
        public IExpr() {
        }
        public int eval() {
            int res;
            if (isLeaf) {
                if (isImmediateValue) {
                    res = intValue;
                } else {
                    throw new Error("eval() on Open Leave");
                }
            } else {
                res = iChildrenResult(left.eval(), right.eval(), operator);
            }
            if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(SpecState _ss) {
            int res;
            if (isLeaf) {
                if (isImmediateValue) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_ss);
                }
            } else {
                res = iChildrenResult(left.eval(_ss), right.eval(_ss),operator);
            }

            if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(SpecState _ss, State _st) {
            int res;
            if (isLeaf) {
                if (isImmediateValue) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_ss, _st);
                }
            } else {
                res = iChildrenResult(left.eval(_ss, _st),
                        right.eval(_ss, _st), operator);
            }

            if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(CompState _cs) {
            int res;
            if (isLeaf) {
                if (isImmediateValue) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_cs); // BUG JAVA 11 ? System.out.println(leaf.getClass() + "  " + res);
                }
            } else {
                res = iChildrenResult(left.eval(_cs), right.eval(_cs),operator);
            }

            if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(Object _qs) {
            int res;
            if (isLeaf) {
                if (isImmediateValue) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_qs);
                }
            } else {
                res = iChildrenResult(left.eval(_qs), right.eval(_qs),operator);
            }

            if (isNegated) {
                res = -res;
            }
            return res;
        }
        public String toString() {
            String retS;
            if (isLeaf) {
                if (!isImmediateValue) {
                    retS = leaf.toString();
                } else  {
                    retS = String.valueOf(intValue);
                }
                if (isNegated) {
                    retS = "-(" + retS + ")";
                }
            } else {
                String leftString = left.toString();
                String rightString = right.toString();
                String opString = "" + operator;
                retS = "(" + leftString + " " + opString + " " + rightString + ")";
                if (isNegated) {
                    retS = "-" + retS;
                }
            }
            return retS;
        }
        public boolean hasStates() {
            boolean hasStates;
            if (isLeaf) {
                if (!isImmediateValue) {
                    return leaf.isState();
                } else {
                    return false;
                }
            } else {
                hasStates = left.hasStates();
                hasStates |= right.hasStates();
                return hasStates;
            }
        }
        public void linkStates() {
            if (isLeaf) {
                if (!isImmediateValue) {
                    leaf.linkState();
                }
            } else {
                left.linkStates();
                right.linkStates();
            }
        }
        public void linkComps(Spec _spec) {
            if (isLeaf) {
                if (!isImmediateValue) {
                    leaf.linkComp(_spec);
                }
            } else {
                left.linkComps(_spec);
                right.linkComps(_spec);
            }
        }
    }
    public class BExpr extends IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr {
        public static final int bCst = 0;
        public static final int bVar = 1;
        public static final int bii = 2;
        public static final int bbb = 3;
        public static final int bi = 5;
        public int type;

        public BExpr bleft, bright;
        public IExpr ileft, iright;

        public char operator;
        public boolean isNot = false;
        public boolean boolValue = false;
        public IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute leaf;
        public BExpr() {
        }
        public boolean eval() {
            boolean res;
            switch (type) {
                case bCst:
                    res = boolValue;
                    break;
                case bVar:
                    throw new Error("eval() called on Open Leave"); //res = leaf.getvalue();
                case bii:
                    res = bChildrenResult(ileft.eval(), iright.eval(), operator);
                    break;
                case bbb:
                    res = bChildrenResult(bleft.eval(), bright.eval(), operator);
                    break;
                case bi:
                    res = ileft.eval() != 0;
                    break;
                default: throw new Error("Invalid Boolean Expression Type");
            }
            if (isNot) {
                res = !res;
            }
            return res;
        }
        public boolean eval(SpecState _ss) {
            boolean res;
            switch (type) {
                case bCst:
                    res = boolValue;
                    break;
                case bVar:
                    res = leaf.getValue(_ss)!=0;
                    break;
                case bii:
                    res = bChildrenResult(ileft.eval(_ss), iright.eval(_ss), operator);
                    break;
                case bbb:
                    res = bChildrenResult(bleft.eval(_ss), bright.eval(_ss), operator);
                    break;
                case bi:
                    res = ileft.eval(_ss) != 0;
                    break;
                default: throw new Error("Invalid Boolean Expression Type");
            }
            if (isNot) {
                res = !res;
            }
            return res;
        }
        public boolean eval(SpecState _ss, State _st) {
            boolean res;
            switch (type) {
                case bCst:
                    res = boolValue;
                    break;
                case bVar:
                    res = leaf.getValue(_ss,_st)!=0;
                    break;
                case bii:
                    res = bChildrenResult(ileft.eval(_ss,_st), iright.eval(_ss,_st), operator);
                    break;
                case bbb:
                    res = bChildrenResult(bleft.eval(_ss,_st), bright.eval(_ss,_st), operator);
                    break;
                case bi:
                    res = ileft.eval(_ss,_st) != 0;
                    break;
                default: throw new Error("Invalid Boolean Expression Type");
            }
            if (isNot) {
                res = !res;
            }
            return res;
        }
        public boolean eval(CompState _cs) {
            boolean res;
            switch (type) {
                case bCst:
                    res = boolValue;
                    break;
                case bVar:
                    res = leaf.getValue(_cs)!=0;
                    break;
                case bii:
                    res = bChildrenResult(ileft.eval(_cs), iright.eval(_cs), operator);
                    break;
                case bbb:
                    res = bChildrenResult(bleft.eval(_cs), bright.eval(_cs), operator);
                    break;
                case bi:
                    res = ileft.eval(_cs) != 0;
                    break;
                default: throw new Error("Invalid Boolean Expression Type");
            }
            if (isNot) {
                res = !res;
            }
            return res;
        }
        public boolean eval(Object _qs) {
            boolean res;
            switch (type) {
                case bCst:
                    res = boolValue;
                    break;
                case bVar:
                    res = leaf.getValue(_qs)!=0;
                    break;
                case bii:
                    res = bChildrenResult(ileft.eval(_qs), iright.eval(_qs), operator);
                    break;
                case bbb:
                    res = bChildrenResult(bleft.eval(_qs), bright.eval(_qs), operator);
                    break;
                case bi:
                    res = ileft.eval(_qs) != 0;
                    break;
                default: throw new Error("Invalid Boolean Expression Type");
            }
            if (isNot) {
                res = !res;
            }
            return res;
        }
        public String toString() {
            String retS;
            String leftString;
            String rightString;
            String opString;
            switch (type) {
                case bCst:
                    if (boolValue)
                        retS = "true";
                    else
                        retS = "false";
                    if (isNot) retS = "not(" + retS + ")";
                break;
                case bVar:
                    retS = leaf.toString();
                    if (isNot) retS = "not(" + retS + ")";
                    break;
                case bii:
                    leftString = ileft.toString();
                    rightString = iright.toString();
                    switch (operator) {
                        case '=':
                            opString = "==";
                            break;
                        case '$':
                            opString = "!=";
                            break;
                        case ':':
                            opString = ">=";
                            break;
                        case ';':
                            opString = "<=";
                            break;
                       default:
                            opString = "" + operator;
                            break;
                    }
                    retS = "(" + leftString + " " + opString + " " + rightString + ")";
                    if (isNot)  retS = "not" + retS;
                    break;
                case bbb:
                    leftString = bleft.toString();
                    rightString = bright.toString();
                    switch (operator) {
                        case '=':
                            opString = "==";
                            break;
                        case '$':
                            opString = "!=";
                            break;
                        case '|':
                            opString = "||";
                            break;
                        case '&':
                            opString = "&&";
                            break;
                        default:
                            opString = "" + operator;
                            break;
                    }
                    retS = "(" + leftString + " " + opString + " " + rightString + ")";
                    if (isNot) retS = "not" + retS;
                    break;
                case bi:
                    retS = "(" + ileft.toString() + ")";
                    if (isNot) retS = "not" + retS;
                    break;
                default: throw new Error("Invalid Boolean Expression Type");
            }
                    return retS;
        }
        public boolean hasStates() {
            boolean hasStates;
            switch (type) {
                case bCst: return false;
                case bVar: return leaf.isState();
                case bii: return ileft.hasStates() || iright.hasStates();
                case bbb: return bleft.hasStates() || bright.hasStates();
                case bi: return ileft.hasStates();
                default: throw new Error("Invalid Boolean Expression Type");
            }
        }
        public void linkStates() {
            switch (type) {
                case bVar: leaf.linkState(); break;
                case bii: ileft.linkStates(); iright.linkStates(); break;
                case bbb: bleft.linkStates(); bright.linkStates(); break;
                case bi: ileft.linkStates();  break;
                default: ;
            }
        }
        public void linkComps(Spec _spec) {
            switch (type) {
                case bVar: leaf.linkComp(_spec); break;
                case bii: ileft.linkComps(_spec); iright.linkComps(_spec); break;
                case bbb: bleft.linkComps(_spec); bright.linkComps(_spec); break;
                case bi: ileft.linkComps(_spec);  break;
                default: ;
            }
        }
    }
    private static int iChildrenResult(int leftV, int rightV, char operator) {
        int result;
        switch (operator) {
            case '-':
                result = leftV - rightV;
                break;
            case '+':
                result = leftV + rightV;
                break;
            case '/':
                result = leftV / rightV;
                break;
            case '*':
                result = leftV * rightV;
                break;
            default:
                throw new Error ("Invalid Boolean Operator");
        }
        //System.out.println(leftV + " " + operator + " " + rightV + " = " + result);
        return result;
    }
    private static boolean bChildrenResult(int leftV, int rightV, char operator) {
        boolean result;
        switch (operator) {
            case '=':
                result = (leftV == rightV);
                break;
            case '$':
                result = (leftV != rightV);
                break;
            case '<':
                result = (leftV < rightV);
                break;
            case '>':
                result = (leftV > rightV);
                break;
            case ':':
                result = (leftV >= rightV);
                break;
            case ';':
                result = (leftV <= rightV);
                break;
            default:
                throw new Error ("Invalid Integer Comparator");
        }
        //System.out.println(leftV + " " + operator + " " + rightV + " = " + result);
        return result;
    }
    private static boolean bChildrenResult(boolean leftV, boolean rightV, char operator) {
        boolean result;

        switch (operator) {
            case '=':
                result = (leftV == rightV);
                break;
            case '$':
                result = (leftV != rightV);
                break;
            case '|':
                result = leftV || rightV;
                break;
            case '&':
                result = leftV && rightV ;
                break;
            default:
                throw new Error ("Invalid Boolean Operator");
        }
        return result;
    }
}

