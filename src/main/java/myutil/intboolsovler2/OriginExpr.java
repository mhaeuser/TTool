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

package myutil.intboolsovler2;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class OriginExpr.
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */

public class OriginExpr<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState>
        extends IBSExpression<Spec,Comp,State,SpecState,CompState> {
    private ArrayList<IExpr> iExpressions = new ArrayList<IExpr>();
    private ArrayList<BExpr> bExpressions = new ArrayList<BExpr>();

    public boolean make_iiiPlus(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '+';
        e.isLeaf = false;
        iExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_iiiMinus(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '-';
        e.isLeaf = false;
        iExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_iiiMult(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '*';
        e.isLeaf = false;
        iExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_iiiDiv(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '/';
        e.isLeaf = false;
        iExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_iiiMod(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        IExpr e = new IExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '%';
        e.isLeaf = false;
        iExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_bbbAnd(int _left, int _right, int _tgt) {
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = bExpressions.get(_left);
        e.right = bExpressions.get(_right);
        e.operator = '&';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_bbbOr(int _left, int _right, int _tgt) {
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = bExpressions.get(_left);
        e.right = bExpressions.get(_right);
        e.operator = '|';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_biiEq(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '=';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_bbbEq(int _left, int _right, int _tgt) {
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = bExpressions.get(_left);
        e.right = bExpressions.get(_right);
        e.operator = '=';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }
    public boolean make_biiDif(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '$';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }

    public boolean make_bbbDif(int _left, int _right, int _tgt) {
        if (bExpressions.size()<= _left || bExpressions.size() <= _right || bExpressions.get(_left) == null || bExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = bExpressions.get(_left);
        e.right = bExpressions.get(_right);
        e.operator = '$';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }

    public boolean make_biiLt(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '<';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }

    public boolean make_biiGt(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = '>';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }

    public boolean make_biiLeq(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = ';';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }

    public boolean make_biiGeq(int _left, int _right, int _tgt) {
        if (iExpressions.size()<= _left || iExpressions.size() <= _right || iExpressions.get(_left) == null || iExpressions.get(_right) == null)
            return false;
        BExpr e = new BExpr();
        e.left = iExpressions.get(_left);
        e.right = iExpressions.get(_right);
        e.operator = ':';
        e.isLeaf = false;
        bExpressions.set(_tgt,e);
        return true;
    }

    public boolean make_iVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v, int _tgt) {
        if (_v == null || _v.getType() != IBSAttributeClass.IntAttr)
            return false;
        IExpr e = new IExpr();
        e.leaf = _v;
        iExpressions.set(_tgt, e);
        return true;
    }

    public boolean make_bVar(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _v, int _tgt) {
        if (_v == null || _v.getType() != IBSAttributeClass.BoolAttr)
            return false;
        BExpr e = new BExpr();
        e.leaf = _v;
        bExpressions.set(_tgt, e);
        return true;
    }

    public boolean make_iConst(int _i, int _tgt) {
        IExpr e = new IExpr();
        e.isImmediateValue = IMMEDIATE_INT; //0: No; 1: Int; 2: Boolean;
        e.intValue = _i;
        iExpressions.set(_tgt, e);
        return true;
    }

    public boolean make_bConst(int _i, int _tgt) {
        BExpr e = new BExpr();
        e.isImmediateValue = IMMEDIATE_BOOL; //0: No; 1: Int; 2: Boolean;
        e.intValue = _i;
        bExpressions.set(_tgt, e);
        return true;
    }

    public boolean make_iNeg(int _expr, int _tgt) throws CloneNotSupportedException {
        if (iExpressions.size()<=_expr || iExpressions.get(_expr) == null)
            return false;
        IExpr e = iExpressions.get(_expr).clone();
        e.isNegated = !iExpressions.get(_expr).isNegated;
        iExpressions.set(_tgt, e);
        return true;
    }

    public boolean make_bNot(int _expr, int _tgt) throws CloneNotSupportedException {
        if (bExpressions.size()<=_expr || bExpressions.get(_expr) == null)
            return false;
        BExpr e = bExpressions.get(_expr).clone();
        e.isNot = !iExpressions.get(_expr).isNot;
        bExpressions.set(_tgt, e);
        return true;
    }

    public boolean make_biExpr(int _expr, int _tgt) {
        if (iExpressions.size()<=_expr || iExpressions.get(_expr) == null)
            return false;
        IExpr expr = iExpressions.get(_expr);
        BExpr e = new BExpr();
        e.left = expr.left;
        e.right = expr.right;
        if (expr.isLeaf) {
            e.isLeaf = true;
            e.leaf = expr.leaf;
        } else {
            e.operator = expr.operator;
        }
        if (expr.isImmediateValue == IBSAttributeClass.IntConst) {
            e.isImmediateValue = IBSAttributeClass.BoolConst;
            e.intValue = expr.intValue;
        }
        bExpressions.set(_tgt, e);
        return true;
    }
    public class IExpr extends IBSExpression<Spec,Comp,State,SpecState,CompState>.IExpr {
        public IExpr left, right;
        public char operator;
        public boolean isLeaf = true;
        public boolean isNegated = false;
        public boolean isImmediateValue = false; //0: No; 1: Int; 2: Boolean;
        public int intValue = 0;
        public IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute leaf;
        public IExpr() {
        }
        public IExpr clone() throws CloneNotSupportedException {
            return (IExpr) super.clone();
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
                res = getChildrenResult(left.eval(), right.eval(), operator);
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
                res = getChildrenResult(left.eval(_ss), right.eval(_ss),operator);
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
                res = getChildrenResult(left.eval(_ss, _st),
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
                    res = leaf.getValue(_cs);
                }
            } else {
                res = getChildrenResult(left.eval(_cs), right.eval(_cs),operator);
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
                res = getChildrenResult(left.eval(_qs), right.eval(_qs),operator);
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
    public class BExpr extends IBSExpression<Spec,Comp,State,SpecState,CompState>.BExpr {
        public static final int bCst = 0;
        public static final int bVar = 1;
        public static final int bii = 2;
        public static final int bbb = 3;
        public static final int bi = 4;
        public int type;

        public BExpr bleft, bright;
        public IExpr ileft, iright;

        public char operator;
        public boolean isNot = false;
        public boolean boolValue = false;
        public IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute leaf;
        public BExpr() {
        }
        public BExpr clone() throws CloneNotSupportedException {
            return (BExpr) super.clone();
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
            switch (type) {
                case bCst:
                    if (boolValue)
                        retS = "false";
                    else
                        retS = "true";
                    if (isNot) retS = "not(" + retS + ")";
                break;
                case bVar:
                    retS = leaf.toString();
                    if (isNot) retS = "not(" + retS + ")";
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
            if (isLeaf) {
            } else {
                String leftString = left.toString();
                String rightString = right.toString();
                String opString;
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
                if (isNegated) {
                    retS = "-" + retS;
                }
                if (isNot) {
                    retS = "not" + retS;
                }
            }
            return retS;
        }
        public boolean hasStates() {
            boolean hasStates;
            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
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
                if (isImmediateValue == IMMEDIATE_NO) {
                    leaf.linkState();
                }
            } else {
                left.linkStates();
                right.linkStates();
            }
        }
        public void linkComps(Spec _spec) {
            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
                    leaf.linkComp(_spec);
                }
            } else {
                left.linkComps(_spec);
                right.linkComps(_spec);
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

