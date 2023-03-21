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

package myutil.intboolsolver2;

import myutil.TraceManager;

import java.util.HashSet;

import static java.lang.Integer.max;

/**
 * Class IBSolver implements the generic solver.
 *
 * <p>For general information about the solver, see
 * {@link myutil.intboolsolver package page}.</p>
 *
 * <p>For documentation about exported API, see
 * {@link IBSOriginParser IBSolverAPI}</p>
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */
public class IBSOriginParser<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    public  IBSAttributeClass<Spec,Comp,State,SpecState,CompState> attC;
    public  IBSExpressionClass<Spec,Comp,State,SpecState,CompState> exprC;
    private HashSet<String> badIdents;
    private boolean syntaxError = false;

    public IBSOriginParser(IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _c,
                           IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _e){
        attC = _c;
        exprC = _e;
        badIdents = new HashSet<String>();
    }
    public IBSOriginParser(){
        badIdents = new HashSet<String>();
    }
    public void setAttributeClass(IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _c){
        attC = _c;
    }
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass(){ return attC; }
    public void setExpressionClass(IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _c){
        exprC = _c;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass(){ return exprC; }
    public HashSet<String> getBadIdents() {
        return badIdents;
    }
    public void clearBadIdents() {
        badIdents.clear();
    }
    public boolean syntaxError() { return syntaxError; }

    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Spec _comp, String _s) {
        badIdents.clear();
        attC.initBuild(_comp);
        int index = parseIntRec(_comp,replaceOperators(_s));
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Spec _comp, String _s) {
        badIdents.clear();
        attC.initBuild(_comp);
        int index = parseBoolRec(_comp,replaceOperators(_s));
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Comp _comp, String _s) {
        badIdents.clear();
        attC.initBuild(_comp);
        int index = parseIntRec(_comp,replaceOperators(_s));
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Comp _comp, String _s) {
        badIdents.clear();
        attC.initBuild(_comp);
        int index = parseBoolRec(_comp,replaceOperators(_s));
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(String _s) {
        badIdents.clear();
        attC.initBuild();
        int index = parseIntRec(replaceOperators(_s));
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(String _s) {
        badIdents.clear();
        attC.initBuild();
        int index = parseBoolRec(replaceOperators(_s));
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr
    makeInt(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _attr) {
        int index = exprC.make_iVar(_attr);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr
    makeBool(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _attr) {
        int index = exprC.make_bVar(_attr);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index==-1) syntaxError=true; else syntaxError=false;
        return null;
    }
    private class TestedExpr {
        public boolean test;
        public String s;
        TestedExpr(String _s,boolean _b){ s = _s; test = _b; }
    }
    public int parseIntRec(Spec _comp, String _s) {
        if (_s.matches("^.+[<>=:;\\$&\\|].*$")) {
            return -1;
        }

        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNegated = false;

        returnVal = checkNegated(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNegated = (isNegated != returnVal.test);

        if (!expression.matches("^.+[\\+\\-\\*/].*$")) {

            returnVal = checkNegatedNoBrackets(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.equals("true") || expression.equals("false"))
                return -2;
            if (expression.matches("-?\\d+")) {
                if (isNegated)
                    return exprC.make_iConst(0 - Integer.parseInt(expression));
                return exprC.make_iConst(Integer.parseInt(expression));
            }
            else {
                int res,idx;
                IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_comp, expression);
                    switch (att.getType()) {
                        case IBSAttributeClass.NullAttr:
                            badIdents.add(expression);
                            return -1;
                        case IBSAttributeClass.BoolAttr:
                        case IBSAttributeClass.BoolConst:
                            return -2;
                        case IBSAttributeClass.IntConst:
                            res = exprC.make_iConst(att.getConstant());
                            if (isNegated){
                                idx=exprC.make_iNeg(res);
                                exprC.freeInt(res);
                                return idx;
                            }
                            return res;
                        case IBSAttributeClass.IntAttr:
                            res = exprC.make_iVar(att.getAttribute());
                            if (isNegated){
                                idx=exprC.make_iNeg(res);
                                exprC.freeInt(res);
                                return idx;
                            }
                            return res;
                    }
            }
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;
        int res;

        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();

        int left  = parseIntRec(_comp,leftExpression);
        if (left < 0) return left;
        int right = parseIntRec(_comp,rightExpression);
        if (right < 0) {exprC.freeInt(left); return right;}

        switch(expression.charAt(index)) {
            case '+':
                res = exprC.make_iiiPlus(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '-':
                res = exprC.make_iiiMinus(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '*':
                res = exprC.make_iiiMult(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '/':
                res = exprC.make_iiiDiv(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            default: // should not happend
                throw new Error("IBSOriginParser, parseIntRec: unknown operator");
        }
    }
    public int parseIntRec(Comp _comp, String _s) {
        if (_s.matches("^.+[<>=:;\\$&\\|].*$")) {
            return -1;
        }

        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNegated = false;

        returnVal = checkNegated(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNegated = (isNegated != returnVal.test);

        if (!expression.matches("^.+[\\+\\-\\*/].*$")) {

            returnVal = checkNegatedNoBrackets(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.equals("true") || expression.equals("false"))
                return -2;
            if (expression.matches("-?\\d+")) {
                int res = exprC.make_iConst(Integer.parseInt(expression));
                if (isNegated) {
                    int idx = exprC.make_iNeg(res);
                    exprC.freeInt(res);
                    return idx;
                }
                return res;
            }
            else {
                int res,idx;
                IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_comp, expression);
                switch (att.getType()) {
                    case IBSAttributeClass.NullAttr:
                        badIdents.add(expression);
                        return -1;
                    case IBSAttributeClass.BoolAttr:
                    case IBSAttributeClass.BoolConst:
                        return -2;
                    case IBSAttributeClass.IntConst:
                        res = exprC.make_iConst(att.getConstant());
                        if (isNegated){
                            idx=exprC.make_iNeg(res);
                            exprC.freeInt(res);
                            return idx;
                        }
                        return res;
                    case IBSAttributeClass.IntAttr:
                        res = exprC.make_iVar(att.getAttribute());
                        if (isNegated){
                            idx=exprC.make_iNeg(res);
                            exprC.freeInt(res);
                            return idx;
                        }
                        return res;
                }
            }
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;
        int res;
        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();

        int left  = parseIntRec(_comp,leftExpression);
        if (left < 0) return left;
        int right = parseIntRec(_comp,rightExpression);
        if (right < 0) {exprC.freeInt(left); return right;}

        switch(expression.charAt(index)) {
            case '+':
                res = exprC.make_iiiPlus(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '-':
                res = exprC.make_iiiMinus(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '*':
                res = exprC.make_iiiMult(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '/':
                res = exprC.make_iiiDiv(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            default: // should not happend
                throw new Error("IBSOriginParser, parseIntRec: unknown operator");
        }
    }
    public int parseIntRec(String _s) {
        if (_s.matches("^.+[<>=:;\\$&\\|].*$")) {
            return -1;
        }

        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNegated = false;

        returnVal = checkNegated(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNegated = (isNegated != returnVal.test);

        if (expression.equals("true") || expression.equals("false"))
            return -2;
        if (!expression.matches("^.+[\\+\\-\\*/].*$")) {

            returnVal = checkNegatedNoBrackets(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.matches("-?\\d+")) {
                int res = exprC.make_iConst(Integer.parseInt(expression));
                if (isNegated) {
                    int idx = exprC.make_iNeg(res);
                    exprC.freeInt(res);
                    return idx;
                }
                return res;
            }
            else {
                badIdents.add(expression);
                return -1;
            }
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;

        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();
        int res;
        int left  = parseIntRec(leftExpression);
        if (left < 0) return left;
        int right = parseIntRec(rightExpression);
        if (right < 0) {exprC.freeInt(left); return right;}

        switch(expression.charAt(index)) {
            case '+':
                res = exprC.make_iiiPlus(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '-':
                res = exprC.make_iiiMinus(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '*':
                res = exprC.make_iiiMult(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            case '/':
                res = exprC.make_iiiDiv(left, right);
                exprC.freeInt(left);
                exprC.freeInt(right);
                return res;
            default: // should not happend
                throw new Error("IBSOriginParser, parseIntRec: unknown operator");
        }
    }
    public int parseBoolRec(Spec _spec, String _s) {
        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNot = false;

        returnVal = checkNot(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNot = (returnVal.test != isNot);
        int res;
        if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
            returnVal = checkNegatedNoBrackets(expression);
            if (returnVal == null) return -1;
            expression = returnVal.s;

            if (expression.equals("true")) {
                res = exprC.make_bConst(true);
            }
            else if (expression.equals("false")) {
                res= exprC.make_bConst(false);
            }
            else if (expression.matches("-?\\d+")) {
                res = exprC.make_bConst((Integer.parseInt(expression) != 0));
            } else {
                IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_spec, expression);
                switch (att.getType()) {
                    case IBSAttributeClass.NullAttr:
                        badIdents.add(expression);
                        return -1;
                    case IBSAttributeClass.BoolConst:
                        res= exprC.make_bConst(att.getConstant() == 0);
                        break;
                    case IBSAttributeClass.IntConst:
                        res= exprC.make_bConst(att.getConstant()!=0);
                        break;
                    case IBSAttributeClass.BoolAttr:
                        res= exprC.make_bVar(att.getAttribute());
                        break;
                    case IBSAttributeClass.IntAttr:
                        res= exprC.make_biVar(att.getAttribute());
                    default:
                        throw new Error ("IBSParser... Cannot happen !?");
                }
            }
        } else {

            int index = getOperatorIndex(expression);
            if (index == -1) return -1;

            String leftExpression = expression.substring(0, index).trim();
            String rightExpression = expression.substring(index + 1, expression.length()).trim();
            int left, right, idx;

            switch (expression.charAt(index)) {
                case '+':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiPlus(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '-':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiMinus(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '*':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiMult(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '/':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiDiv(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '&':
                    left = parseBoolRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbAnd(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '|':
                    left = parseBoolRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbOr(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '=':
                    left = parseIntRec(_spec, leftExpression);
                    if (left >= 0) {
                        right = parseIntRec(_spec, rightExpression);
                        if (right < 0) {
                            right = parseBoolRec(_spec, rightExpression);
                            if (right < 0) {
                                exprC.freeInt(left);
                                return right;
                            }
                            idx = left;
                            left = exprC.make_biExpr(idx);
                            exprC.freeInt(idx);
                            res = exprC.make_bbbEq(left, right);
                            exprC.freeBool(left);
                            exprC.freeBool(right);
                            return res;
                        }
                        res = exprC.make_biiEq(left, right);
                        exprC.freeInt(left);
                        exprC.freeInt(right);
                        return res;
                    }
                    left = parseBoolRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbEq(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '$':
                    left = parseIntRec(_spec, leftExpression);
                    if (left >= 0) {
                        right = parseIntRec(_spec, rightExpression);
                        if (right < 0) {
                            right = parseBoolRec(_spec, rightExpression);
                            if (right < 0) {
                                exprC.freeInt(left);
                                return right;
                            }
                            idx = left;
                            left = exprC.make_biExpr(idx);
                            exprC.freeInt(idx);
                            res = exprC.make_bbbDif(left, right);
                            exprC.freeBool(left);
                            exprC.freeBool(right);
                        } else {
                            res = exprC.make_biiDif(left, right);
                            exprC.freeInt(left);
                            exprC.freeInt(right);
                        }
                    } else {
                        left = parseBoolRec(_spec, leftExpression);
                        if (left < 0) return left;
                        right = parseBoolRec(_spec, rightExpression);
                        if (right < 0) {
                            exprC.freeBool(left);
                            return right;
                        }
                        res = exprC.make_bbbDif(left, right);
                        exprC.freeBool(left);
                        exprC.freeBool(right);
                    }
                    break;

                case '<':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiLt(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '>':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiGt(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case ';':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiLeq(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case ':':
                    left = parseIntRec(_spec, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_spec, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiGeq(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                default:
                    throw new Error("IBSOriginParser, parseBoolRec: unknown operator");
            }
        }
        if (isNot) {
            int idx = exprC.make_bNot(res);
            exprC.freeBool(res);
            return idx;
        }
        return res;
    }
    public int parseBoolRec(Comp _comp, String _s) {
        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNot = false;

        returnVal = checkNot(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNot = (returnVal.test != isNot);
        int res;
        if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
            returnVal = checkNegatedNoBrackets(expression);
            if (returnVal == null) return -1;
            expression = returnVal.s;

            if (expression.equals("true")) {
                res = exprC.make_bConst(true);
            }
            else if (expression.equals("false")) {
                res= exprC.make_bConst(false);
            }
            else if (expression.matches("-?\\d+")) {
                res = exprC.make_bConst((Integer.parseInt(expression) != 0));
            } else {
                IBSAttributeClass<Spec, Comp, State, SpecState, CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_comp, expression);
                switch (att.getType()) {
                    case IBSAttributeClass.NullAttr:
                        badIdents.add(expression);
                        return -1;
                    case IBSAttributeClass.BoolConst:
                        res= exprC.make_bConst(att.getConstant() == 0);
                        break;
                    case IBSAttributeClass.IntConst:
                        res= exprC.make_bConst(att.getConstant()!=0);
                        break;
                    case IBSAttributeClass.BoolAttr:
                        res= exprC.make_bVar(att.getAttribute());
                        break;
                    case IBSAttributeClass.IntAttr:
                        res= exprC.make_biVar(att.getAttribute());
                    default:
                        throw new Error ("IBSParser... Cannot happen !?");
                }
            }
        } else {

            int index = getOperatorIndex(expression);
            if (index == -1) return -1;

            String leftExpression = expression.substring(0, index).trim();
            String rightExpression = expression.substring(index + 1, expression.length()).trim();
            int left, right, idx;

            switch (expression.charAt(index)) {
                case '+':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiPlus(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '-':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiMinus(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '*':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiMult(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '/':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiDiv(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '&':
                    left = parseBoolRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbAnd(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '|':
                    left = parseBoolRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbOr(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '=':
                    left = parseIntRec(_comp, leftExpression);
                    if (left >= 0) {
                        right = parseIntRec(_comp, rightExpression);
                        if (right < 0) {
                            right = parseBoolRec(_comp, rightExpression);
                            if (right < 0) {
                                exprC.freeInt(left);
                                return right;
                            }
                            idx = left;
                            left = exprC.make_biExpr(idx);
                            exprC.freeInt(idx);
                            res = exprC.make_bbbEq(left, right);
                            exprC.freeBool(left);
                            exprC.freeBool(right);
                            return res;
                        }
                        res = exprC.make_biiEq(left, right);
                        exprC.freeInt(left);
                        exprC.freeInt(right);
                        return res;
                    }
                    left = parseBoolRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbEq(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '$':
                    left = parseIntRec(_comp, leftExpression);
                    if (left >= 0) {
                        right = parseIntRec(_comp, rightExpression);
                        if (right < 0) {
                            right = parseBoolRec(_comp, rightExpression);
                            if (right < 0) {
                                exprC.freeInt(left);
                                return right;
                            }
                            idx = left;
                            left = exprC.make_biExpr(idx);
                            exprC.freeInt(idx);
                            res = exprC.make_bbbDif(left, right);
                            exprC.freeBool(left);
                            exprC.freeBool(right);
                        } else {
                            res = exprC.make_biiDif(left, right);
                            exprC.freeInt(left);
                            exprC.freeInt(right);
                        }
                    } else {
                        left = parseBoolRec(_comp, leftExpression);
                        if (left < 0) return left;
                        right = parseBoolRec(_comp, rightExpression);
                        if (right < 0) {
                            exprC.freeBool(left);
                            return right;
                        }
                        res = exprC.make_bbbDif(left, right);
                        exprC.freeBool(left);
                        exprC.freeBool(right);
                    }
                    break;

                case '<':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiLt(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '>':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiGt(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case ';':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiLeq(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case ':':
                    left = parseIntRec(_comp, leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec(_comp, rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiGeq(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                default:
                    throw new Error("IBSOriginParser, parseBoolRec: unknown operator");
            }
        }
        if (isNot) {
            int idx = exprC.make_bNot(res);
            exprC.freeBool(res);
            return idx;
        }
        return res;
    }

    public int parseBoolRec(String _s) {
        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNot = false;

        returnVal = checkNot(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNot = (returnVal.test != isNot);
        int res;
        if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
            returnVal = checkNegatedNoBrackets(expression);
            if (returnVal == null) return -1;
            expression = returnVal.s;

            if (expression.equals("true")) {
                res = exprC.make_bConst(true);
            }
            else if (expression.equals("false")) {
                res= exprC.make_bConst(false);
            }
            else if (expression.matches("-?\\d+")) {
                res = exprC.make_bConst((Integer.parseInt(expression) != 0));
            } else {
                badIdents.add(expression);
                return -1;
            }
        } else {

            int index = getOperatorIndex(expression);
            if (index == -1) return -1;

            String leftExpression = expression.substring(0, index).trim();
            String rightExpression = expression.substring(index + 1, expression.length()).trim();
            int left, right, idx;

            switch (expression.charAt(index)) {
                case '+':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiPlus(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '-':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiMinus(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '*':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiMult(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '/':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    idx = exprC.make_iiiDiv(left, right);
                    res = exprC.make_biExpr(idx);
                    exprC.freeInt(idx);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '&':
                    left = parseBoolRec( leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec( rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbAnd(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '|':
                    left = parseBoolRec( leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec( rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbOr(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '=':
                    left = parseIntRec( leftExpression);
                    if (left >= 0) {
                        right = parseIntRec( rightExpression);
                        if (right < 0) {
                            right = parseBoolRec( rightExpression);
                            if (right < 0) {
                                exprC.freeInt(left);
                                return right;
                            }
                            idx = left;
                            left = exprC.make_biExpr(idx);
                            exprC.freeInt(idx);
                            res = exprC.make_bbbEq(left, right);
                            exprC.freeBool(left);
                            exprC.freeBool(right);
                            return res;
                        }
                        res = exprC.make_biiEq(left, right);
                        exprC.freeInt(left);
                        exprC.freeInt(right);
                        return res;
                    }
                    left = parseBoolRec( leftExpression);
                    if (left < 0) return left;
                    right = parseBoolRec( rightExpression);
                    if (right < 0) {
                        exprC.freeBool(left);
                        return right;
                    }
                    res = exprC.make_bbbEq(left, right);
                    exprC.freeBool(left);
                    exprC.freeBool(right);
                    break;

                case '$':
                    left = parseIntRec( leftExpression);
                    if (left >= 0) {
                        right = parseIntRec( rightExpression);
                        if (right < 0) {
                            right = parseBoolRec( rightExpression);
                            if (right < 0) {
                                exprC.freeInt(left);
                                return right;
                            }
                            idx = left;
                            left = exprC.make_biExpr(idx);
                            exprC.freeInt(idx);
                            res = exprC.make_bbbDif(left, right);
                            exprC.freeBool(left);
                            exprC.freeBool(right);
                        } else {
                            res = exprC.make_biiDif(left, right);
                            exprC.freeInt(left);
                            exprC.freeInt(right);
                        }
                    } else {
                        left = parseBoolRec( leftExpression);
                        if (left < 0) return left;
                        right = parseBoolRec( rightExpression);
                        if (right < 0) {
                            exprC.freeBool(left);
                            return right;
                        }
                        res = exprC.make_bbbDif(left, right);
                        exprC.freeBool(left);
                        exprC.freeBool(right);
                    }
                    break;

                case '<':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiLt(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case '>':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiGt(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case ';':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiLeq(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                case ':':
                    left = parseIntRec( leftExpression);
                    if (left < 0) return left;
                    right = parseIntRec( rightExpression);
                    if (right < 0) {
                        exprC.freeInt(left);
                        return right;
                    }
                    res = exprC.make_biiGeq(left, right);
                    exprC.freeInt(left);
                    exprC.freeInt(right);
                    break;
                default:
                    throw new Error("IBSOriginParser, parseBoolRec: unknown operator");
            }
        }
        if (isNot) {
            int idx = exprC.make_bNot(res);
            exprC.freeBool(res);
            return idx;
        }
        return res;
    }

        private String replaceOperators(String _expr) {
            String expression = _expr;
            expression = expression.replaceAll("\\|\\|", "\\|").trim();
            expression = expression.replaceAll("&&", "&").trim();
            expression = expression.replaceAll("==", "=").trim();
            expression = expression.replaceAll("!=", "\\$").trim();
            expression = expression.replaceAll(">=", ":").trim();
            expression = expression.replaceAll("<=", ";").trim();
            expression = expression.replaceAll("\\bor\\b", "\\|").trim();
            expression = expression.replaceAll("\\band\\b", "&").trim();
            //expression.replaceAll("\\btrue\\b", "t").trim();
            //expression.replaceAll("\\bfalse\\b", "f").trim();
            return expression;
        }

        private TestedExpr checkNot(String _s) {
            String expression = _s;
            boolean isNot = false;

            boolean notStart1, notStart2;

            notStart1 = expression.startsWith("not(");
            notStart2 = expression.startsWith("!(");

            while (notStart1 || notStart2) {
                if (notStart1) {
                    //not bracket must be closed in the last char
                    int closingIndex = getClosingBracket(expression,4);

                    if (closingIndex == -1) {
                        return null;
                    }
                    if (closingIndex == expression.length() - 1) {
                        //not(expression)
                        isNot = !isNot;
                        expression = expression.substring(4, expression.length() - 1).trim();
                    } else {
                        return new TestedExpr(expression,isNot);
                    }
                } else if (notStart2) {
                    int closingIndex = getClosingBracket(expression,2);

                    if (closingIndex == -1) {
                        return null;
                    }
                    if (closingIndex == expression.length() - 1) {
                        //not(expression)
                        isNot = !isNot;
                        expression = expression.substring(2, expression.length() - 1).trim();
                    } else {
                        return new TestedExpr(expression,isNot);
                    }
                }
                notStart1 = expression.startsWith("not(");
                notStart2 = expression.startsWith("!(");
            }
            return new TestedExpr(expression,isNot);
        }

    private TestedExpr checkNegated(String _s) {
        String expression = _s;
        boolean isNegated = false;
        while (expression.startsWith("-(")) {
                //not bracket must be closed in the last char
                int closingIndex = getClosingBracket(expression,2);

                if (closingIndex == -1) {
                    return null;
                }
                if (closingIndex == expression.length() - 1) {
                    //-(expression)
                    isNegated = !isNegated;
                    expression = expression.substring(2, expression.length() - 1).trim();
                } else {
                    return new TestedExpr(expression,isNegated);
                }
            }
            return new TestedExpr(expression,isNegated);
        }

    private TestedExpr checkNegatedNoBrackets(String _s) {
        String expression = _s;
        boolean isNegated = false;
        if (expression.startsWith("-")) {
            isNegated = true;
            expression = expression.substring(1, expression.length()).trim();
        }
        return new TestedExpr(expression,isNegated);
    }
    private TestedExpr checkNotNoBrackets(String _s) {
        String expression = _s;
        boolean isNot = false;
        if (expression.startsWith("!")) {
            isNot = true;
            expression = expression.substring(1, expression.length()).trim();
        }
        if (expression.startsWith("not ")) {
            isNot = true;
            expression = expression.substring(4, expression.length()).trim();
        }
        return new TestedExpr(expression,isNot);
    }

        private int getOperatorIndex(String expression) {
            int index;
            // find the last executed operator
            int i, level, priority;
            boolean subVar = true; //when a subtraction is only one one variable
            char a;
            level = 0;
            priority = 0;
            for (i = 0, index = -1; i < expression.length(); i++) {
                a = expression.charAt(i);
                switch (a) {
                    case '|':
                        if (level == 0) {
                            index = i;
                            priority = 5;
                        }
                        break;
                    case '&':
                        if (level == 0 && priority < 5) {
                            index = i;
                            priority = 4;
                        }
                        break;
                    case '=':
                        if (level == 0 && priority < 4) {
                            index = i;
                            priority = 3;
                        }
                        subVar = true;
                        break;
                    case '$':
                        if (level == 0 && priority < 4) {
                            index = i;
                            priority = 3;
                        }
                        subVar = true;
                        break;
                    case '<':
                        if (level == 0 && priority < 3) {
                            index = i;
                            priority = 2;
                        }
                        subVar = true;
                        break;
                    case '>':
                        if (level == 0 && priority < 3) {
                            index = i;
                            priority = 2;
                        }
                        subVar = true;
                        break;
                    case ':':
                        if (level == 0 && priority < 3) {
                            index = i;
                            priority = 2;
                        }
                        subVar = true;
                        break;
                    case ';':
                        if (level == 0 && priority < 3) {
                            index = i;
                            priority = 2;
                        }
                        subVar = true;
                        break;
                    case '-':
                        if (level == 0 && !subVar && priority < 2) {
                            index = i;
                            priority = 1;
                        }
                        break;
                    case '+':
                        if (level == 0 && !subVar && priority < 2) {
                            index = i;
                            priority = 1;
                        }
                        break;
                    case '/':
                        if (level == 0 && priority == 0) {
                            index = i;
                        }
                        break;
                    case '*':
                        if (level == 0 && priority == 0) {
                            index = i;
                        }
                        break;
                    case '(':
                        level++;
                        subVar = true;
                        break;
                    case ')':
                        level--;
                        subVar = false;
                        break;
                    case ' ':
                        break;
                    default:
                        subVar = false;
                        break;
                }
            }
            return index;
        }

        private String removeUselessBrackets(String _s) {
            String expression = _s;
            //TraceManager.addDev("Removing first / final brackets");
            while (expression.startsWith("(") && expression.endsWith(")")) {
                if (getClosingBracket(expression,1) == expression.length() - 1) {
                    expression = expression.substring(1, expression.length() - 1).trim();
                } else {
                    break;
                }
            }
            //TraceManager.addDev("Removing dual brackets");
            // Removing duplicate brackets
            // typically ((x)) -> (x)
            // Parse expression step by step, search for "((" schemes and look for corresponding brackets
            for (int i = 0; i < expression.length() - 2; i++) {
                String s1 = expression.substring(i, i + 1);
                if (s1.startsWith("(")) {
                    String s2 = expression.substring(i + 1, i + 2);
                    if (s2.startsWith("(")) {
                        //TraceManager.addDev("Found dual at i=" + i);
                        int index1 = getClosingBracket(expression,i + 1);
                        int index2 = getClosingBracket(expression,i + 2);
                        //TraceManager.addDev("Found dual at i=" + i + " index1=" + index1 + " index2=" + index2 + " expr=" + expression);
                        if (index1 == index2 + 1) {
                            // Two following brackets. We can remove one of them
                            //TraceManager.addDev("old expression:" + expression);
                            expression = expression.substring(0, i + 1) + expression.substring(i + 2, index2) + expression.substring(index1);
                            //TraceManager.addDev("new expression:" + expression);
                        }
                    }
                }
            }
            return expression;
        }

        private int getClosingBracket(String expression, int startChar) {
            int level = 0;
            char a;
            for (int i = startChar; i < expression.length(); i++) {
                a = expression.charAt(i);
                if (a == ')') {
                    if (level == 0) {
                        return i;
                    } else {
                        level--;
                    }
                } else if (a == '(') {
                    level++;
                }
            }
            return -1;
        }

    //!! A Deplacer ailleurs !!!!!!!!!!! methode statique
    public int indexOfVariable(String expr, String variable) {
        int index;
        String tmp = expr;
        int removed = 0;
        //System.out.println("\nHandling expr: " + expr);

        while ((index = tmp.indexOf(variable)) > -1) {
            char c1, c2;
            if (index > 0) {
                c1 = tmp.charAt(index - 1);
            } else {
                c1 = ' ';
            }

            if (index + variable.length() < tmp.length())
                c2 = tmp.charAt(index + variable.length());
            else
                c2 = ' ';

            //System.out.println("tmp=" + tmp + " c1=" + c1 + " c2=" + c2);

            if (!(Character.isLetterOrDigit(c1) || (c1 == '_'))) {
                if (!(Character.isLetterOrDigit(c2) || (c2 == '_'))) {
                    //System.out.println("Found at index=" + index + " returnedIndex=" + (index+removed));
                    return index + removed;
                }
            }
            tmp = tmp.substring(index + variable.length(), tmp.length());
            //System.out.println("tmp=" + tmp);
            removed = index + variable.length();
            if (tmp.length() == 0) {
                return -1;
            }
            // We cut until we find a non alphanumerical character
            while (Character.isLetterOrDigit(tmp.charAt(0)) || (tmp.charAt(0) == '_')) {
                tmp = tmp.substring(1, tmp.length());
                if (tmp.length() == 0) {
                    return -1;
                }
                removed++;
            }
            //System.out.println("after remove: tmp=" + tmp);

        }
        return -1;
    }

    //!! A Deplacer ailleurs !!!!!!!!!!! methode statique
    public String replaceVariable(String expr, String oldVariable, String newVariable) {
        if (oldVariable.compareTo(newVariable) == 0) {
            return expr;
        }
        int index;
        String tmp = expr;

        while ((index = indexOfVariable(tmp, oldVariable)) > -1) {
            String tmp1 = "";
            if (index > 0) {
                tmp1 = tmp.substring(0, index);
            }
            tmp1 += newVariable;
            tmp1 += tmp.substring(index + oldVariable.length(), tmp.length());
            tmp = tmp1;
        }

        return tmp;
    }

}
