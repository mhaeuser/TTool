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

    public static final int IMMEDIATE_NO = 0;
    public static final int IMMEDIATE_INT = 1;
    public static final int IMMEDIATE_BOOL = 2;

    public IExpr make_iiiPlus(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        IExpr e = new IExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '+';
        e.isLeaf = false;
        return e;
    }

    public IExpr make_iiiMinus(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        IExpr e = new IExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '-';
        e.isLeaf = false;
        return e;
    }

    public IExpr make_iiiMult(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        IExpr e = new IExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '*';
        e.isLeaf = false;
        return e;
    }

    public IExpr make_iiiDiv(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        IExpr e = new IExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '/';
        e.isLeaf = false;
        return e;
    }

    public IExpr make_iiiMod(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        IExpr e = new IExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '%';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_bbbAnd(BExpr _left, BExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '&';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_bbbOr(BExpr _left, BExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '|';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_biiEq(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '=';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_bbbEq(BExpr _left, BExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '=';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_biiDif(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '$';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_bbbDif(BExpr _left, BExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '$';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_biiLt(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '<';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_biiGt(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = '>';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_biiLeq(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = ';';
        e.isLeaf = false;
        return e;
    }

    public BExpr make_biiGeq(IExpr _left, IExpr _right) {
        if (_left == null || _right == null) return null;
        BExpr e = new BExpr();
        e.left = _left;
        e.right = _right;
        e.operator = ':';
        e.isLeaf = false;
        return e;
    }

    public IExpr make_iVar(IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute _v) {
        if (_v == null || _v.getType() != IBSAttributeClass.IntAttr) return null;
        IExpr e = new IExpr();
        e.leaf = _v;
        return e;
    }

    public BExpr make_bVar(IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute _v) {
        if (_v == null || _v.getType() != IBSAttributeClass.BoolAttr) return null;
        BExpr e = new BExpr();
        e.leaf = _v;
        return e;
    }

    public IExpr make_iConst(int _i) {
        IExpr e = new IExpr();
        e.isImmediateValue = IMMEDIATE_INT; //0: No; 1: Int; 2: Boolean;
        e.intValue = _i;
        return e;
    }

    public BExpr make_bConst(int _i) {
        BExpr e = new BExpr();
        e.isImmediateValue = IMMEDIATE_BOOL; //0: No; 1: Int; 2: Boolean;
        e.intValue = _i;
        return e;
    }

    public IExpr make_iNeg(IExpr _expr) throws CloneNotSupportedException {
        if (_expr==null) return null;
        IExpr e = _expr.clone();
        e.isNegated = !_expr.isNegated;
        return e;
    }

    public BExpr make_bNot(BExpr _expr) throws CloneNotSupportedException {
        if (_expr==null) return null;
        BExpr e = _expr.clone();
        e.isNot = !_expr.isNot;
        return e;
    }

    public BExpr make_biExpr(IExpr _expr) {
        if (_expr==null) return null;
        BExpr e = new BExpr();
        e.left = _expr.left;
        e.right = _expr.right;
        if (_expr.isLeaf) {
            e.isLeaf = true;
            e.leaf = _expr.leaf;
        } else {
            e.operator = _expr.operator;
        }
        if (_expr.isImmediateValue == IBSAttributeClass.IntConst) {
            e.isImmediateValue = IBSAttributeClass.BoolConst;
            e.intValue = _expr.intValue;
        }
        return e;
    }

    public class Expr implements Cloneable {
        public Expr left, right;
        public char operator;
        public boolean isLeaf = true; //variable
        public boolean isNot = false;
        public boolean isNegated = false;
        public int isImmediateValue = IMMEDIATE_NO; //0: No; 1: Int; 2: Boolean;
        public int intValue = 0;
        public IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.Attribute leaf;
        public Expr() {
        }
        public Expr clone() throws CloneNotSupportedException {
            return (Expr) super.clone();
        }
        public int eval() {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    throw new Error("eval() on Open Leave");
                }
            } else {
                res = getChildrenResult(left.eval(), right.eval(), operator);
            }
            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(SpecState _ss) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_ss);
                }
            } else {
                res = getChildrenResult(left.eval(_ss), right.eval(_ss),operator);
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(SpecState _ss, State _st) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_ss, _st);
                }
            } else {
                res = getChildrenResult(left.eval(_ss, _st),
                        right.eval(_ss, _st), operator);
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(CompState _cs) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_cs);
                }
            } else {
                res = getChildrenResult(left.eval(_cs), right.eval(_cs),operator);
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }
        public int eval(Object _qs) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_qs);
                }
            } else {
                res = getChildrenResult(left.eval(_qs), right.eval(_qs),operator);
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }
        public String toString() {
            String retS;
            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
                    retS = leaf.toString();
                } else if (isImmediateValue == IMMEDIATE_INT) {
                    retS = String.valueOf(intValue);
                } else {
                    if (intValue == 0) {
                        retS = "false";
                    } else {
                        retS = "true";
                    }
                }
                if (isNegated) {
                    retS = "-(" + retS + ")";
                }
                if (isNot) {
                    retS = "not(" + retS + ")";
                }
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

    public class IExpr extends Expr {
        public IExpr() {
            super();
        }
        public IExpr clone() throws CloneNotSupportedException {
            return (IExpr) super.clone();
        }
    }

    public class BExpr extends Expr {
        public BExpr() {
            super();
        }
        public BExpr clone() throws CloneNotSupportedException {
            return (BExpr) super.clone();
        }
    }
    private static int getChildrenResult(int leftV, int rightV, char operator) {
        int result;

        switch (operator) {
            case '=':
                result = (leftV == rightV) ? 1 : 0;
                break;
            case '$':
                result = (leftV != rightV) ? 1 : 0;
                break;
            case '<':
                result = (leftV < rightV) ? 1 : 0;
                break;
            case '>':
                result = (leftV > rightV) ? 1 : 0;
                break;
            case ':':
                result = (leftV >= rightV) ? 1 : 0;
                break;
            case ';':
                result = (leftV <= rightV) ? 1 : 0;
                break;
            case '-':
                result = leftV - rightV;
                break;
            case '+':
                result = leftV + rightV;
                break;
            case '|':
                result = (leftV == 0 && rightV == 0) ? 0 : 1;
                break;
            case '/':
                result = leftV / rightV;
                break;
            case '*':
                result = leftV * rightV;
                break;
            case '&':
                result = (leftV == 0 || rightV == 0) ? 0 : 1;
                break;
            default:
                //System.out.println("Error in EquationSolver::getResult");
                result = 0;
                break;
        }
        //System.out.println(leftV + " " + operator + " " + rightV + " = " + result);
        return result;
    }
}

