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
 * {@link OriginParser IBSolverAPI}</p>
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */
// IBSExpression<Spec,Comp,State,SpecState,CompState>
public class OriginParser<
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

    public OriginParser(IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _c,
                        IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _e){
        attC = _c;
        exprC = _e;
        badIdents = new HashSet<String>();
    }
    public OriginParser(){
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

    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Spec _spec, String _s) {
        badIdents.clear();
        syntaxError = false;
        attC.initBuild(_spec);
        int index = parseIntRec(_spec,_s);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Spec _spec, String _s) {
        badIdents.clear();
        syntaxError = false;
        attC.initBuild(_spec);
        int index = parseBoolRec(_spec,_s);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Comp _comp, String _s) {
        badIdents.clear();
        syntaxError = false;
        attC.initBuild(_comp);
        int index = parseIntRec(_comp,_s);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Comp _comp, String _s) {
        badIdents.clear();
        syntaxError = false;
        attC.initBuild(_comp);
        int index = parseBoolRec(_comp,_s);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(String _s) {
        badIdents.clear();
        syntaxError = false;
        attC.initBuild();
        int index = parseIntRec(_s);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.IExpr res = exprC.getIExpr(index);
            exprC.freeInt(index);
            return res;
        }
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
        return null;
    }
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(String _s) {
        badIdents.clear();
        syntaxError = false;
        attC.initBuild();
        int index = parseBoolRec(_s);
        if (index >= 0) {
            IBSExpressionClass<Spec, Comp, State, SpecState, CompState>.BExpr res = exprC.getBExpr(index);
            exprC.freeBool(index);
            return res;
        }
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
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
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
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
        if (index == -2) TraceManager.addDev("IBSParser: Int Expression Memory Full");
        return null;
    }
    private class TestedExpr {
        public boolean test;
        public String s;
        TestedExpr(String _s,boolean _b){ s = _s; test = _b; }
    }
    public int parseIntRec(Spec _spec, String _s) {
        if (_s.matches("^.+[<>=:;\\$&\\|].*$")) {
            syntaxError = true;
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

            returnVal = checkNegated(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.matches("-?\\d+"))
                return exprC.make_iConst(Integer.parseInt(expression));
            else {
                IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_spec, expression);
                    switch (att.getType()) {
                        case IBSAttributeClass.NullAttr:
                        case IBSAttributeClass.BoolConst: return -1;
                        case IBSAttributeClass.IntConst:
                            return exprC.make_iConst(att.getConstant());
                        default:
                            return exprC.make_iVar(att.getAttribute());
                    }
            }
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;

        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();

        int left  = parseIntRec(_spec,leftExpression);
        int right = parseIntRec(_spec,rightExpression);
        if (left<0) {
            if (right < 0) return max(left, right);
            exprC.freeInt(right);
            return left;
        }
        switch(expression.charAt(index)) {
            case '+':
                return exprC.make_iiiPlus(left, right);
            case '-':
                return exprC.make_iiiMinus(left, right);
            case '*':
                return exprC.make_iiiMult(left, right);
            case '/':
                return exprC.make_iiiDiv(left, right);
            default: // should not happend
                syntaxError = true;
                return -1;
        }
    }
    public int parseIntRec(Comp _comp, String _s) {
        if (_s.matches("^.+[<>=:;\\$&\\|].*$")) {
            syntaxError = true;
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

            returnVal = checkNegated(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.matches("-?\\d+"))
                return exprC.make_iConst(Integer.parseInt(expression));
            else {
                IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_comp, expression);
                switch (att.getType()) {
                    case IBSAttributeClass.NullAttr:
                    case IBSAttributeClass.BoolConst: return -1;
                    case IBSAttributeClass.IntConst:
                        return exprC.make_iConst(att.getConstant());
                    default:
                        return exprC.make_iVar(att.getAttribute());
                }
            }
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;

        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();

        int left  = parseIntRec(_comp,leftExpression);
        int right = parseIntRec(_comp,rightExpression);
        if (left<0) {
            if (right < 0) return max(left, right);
            exprC.freeInt(right);
            return left;
        }
        switch(expression.charAt(index)) {
            case '+':
                return exprC.make_iiiPlus(left, right);
            case '-':
                return exprC.make_iiiMinus(left, right);
            case '*':
                return exprC.make_iiiMult(left, right);
            case '/':
                return exprC.make_iiiDiv(left, right);
            default: // should not happend
                syntaxError = true;
                return -1;
        }
    }
    public int parseIntRec(String _s) {
        if (_s.matches("^.+[<>=:;\\$&\\|].*$")) {
            syntaxError = true;
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

            returnVal = checkNegated(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.matches("-?\\d+"))
                return exprC.make_iConst(Integer.parseInt(expression));
            else
                return -1;
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;

        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();

        int left  = parseIntRec(leftExpression);
        int right = parseIntRec(rightExpression);
        if (left<0) {
            if (right < 0) return max(left, right);
            exprC.freeInt(right);
            return left;
        }
        switch(expression.charAt(index)) {
            case '+':
                return exprC.make_iiiPlus(left, right);
            case '-':
                return exprC.make_iiiMinus(left, right);
            case '*':
                return exprC.make_iiiMult(left, right);
            case '/':
                return exprC.make_iiiDiv(left, right);
            default: // should not happend
                syntaxError = true;
                return -1;
        }
    }
    public int parseBoolRec(Spec _spec, String _s) {
        TestedExpr returnVal;
        String expression = removeUselessBrackets(_s);
        boolean isNegated = false;

        returnVal = checkNegated(expression);
        if(returnVal==null) return -1;
        expression = returnVal.s;
        isNegated = (isNegated != returnVal.test);

        if (!expression.matches("^.+[\\+\\-\\*/].*$")) {

            returnVal = checkNegated(expression);
            if(returnVal==null) return -1;
            expression = returnVal.s;
            isNegated = (isNegated != returnVal.test);

            if (expression.matches("-?\\d+"))
                return exprC.make_iConst(Integer.parseInt(expression));
            else {
                IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute att =
                        attC.getTypedAttribute(_spec, expression);
                switch (att.getType()) {
                    case IBSAttributeClass.NullAttr:
                    case IBSAttributeClass.BoolConst: return -1;
                    case IBSAttributeClass.IntConst:
                        return exprC.make_iConst(att.getConstant());
                    default:
                        return exprC.make_iVar(att.getAttribute());
                }
            }
        }
        int index = getOperatorIndex(expression);
        if (index == -1) return -1;

        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();

        int left  = parseIntRec(_spec,leftExpression);
        int right = parseIntRec(_spec,rightExpression);
        if (left<0) {
            if (right < 0) return max(left, right);
            exprC.freeInt(right);
            return left;
        }
        switch(expression.charAt(index)) {
            case '+':
                return exprC.make_iiiPlus(left, right);
            case '-':
                return exprC.make_iiiMinus(left, right);
            case '*':
                return exprC.make_iiiMult(left, right);
            case '/':
                return exprC.make_iiiDiv(left, right);
            default: // should not happend
                syntaxError = true;
                return -1;
        }
    }
    public boolean buildExpressionRec(Comp _comp) {
            boolean returnVal;

            removeUselessBrackets();
            returnVal = checkNot();
            returnVal &= checkNegated();

            if (!returnVal) return false;

            if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
                // leaf
                isLeaf = true;
                checkNegatedNoBrackets();
                if (expression.equals("true")) {
                    intValue = 1;
                    isImmediateValue = IMMEDIATE_BOOL;
                    return true;
                } else if (expression.equals("false")) {
                    intValue = 0;
                    isImmediateValue = IMMEDIATE_BOOL;
                    return true;
                } else if (expression.matches("-?\\d+")) {
                    intValue = Integer.parseInt(expression);
                    isImmediateValue = IMMEDIATE_INT;
                    return true;
                } else {
                    IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute att =
                            attC.getTypedAttribute(_comp, expression);
                    switch (att.getType()) {
                        case IBSAttributeClass.NullAttr: {
                            return false;
                        }
                        case IBSAttributeClass.BoolConst: {
                            intValue = att.getConstant();
                            isImmediateValue = IMMEDIATE_BOOL;
                            return true;
                        }
                        case IBSAttributeClass.IntConst: {
                            intValue = att.getConstant();
                            isImmediateValue = IMMEDIATE_INT;
                            return true;
                        }
                        default: {
                            leaf = att.getAttribute();
                            return true;
                        }
                    }
                }
            }
            isLeaf = false;

            int index = getOperatorIndex();

            if (index == -1) {
                return false;
            }

            operator = expression.charAt(index);

            //split and recur
            String leftExpression = expression.substring(0, index).trim();
            String rightExpression = expression.substring(index + 1, expression.length()).trim();
            left = new Expr(leftExpression);
            right = new Expr(rightExpression);
            returnVal = left.buildExpressionRec(_comp);
            returnVal &= right.buildExpressionRec(_comp);

            return returnVal;
        }


        public boolean buildExpressionRec() {
            boolean returnVal;

            removeUselessBrackets();
            returnVal = checkNot();
            returnVal &= checkNegated();

            if (!returnVal) return false;

            if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
                // leaf
                isLeaf = true;
                checkNegatedNoBrackets();
                if (expression.equals("true")) {
                    intValue = 1;
                    isImmediateValue = IMMEDIATE_BOOL;
                    return true;
                } else if (expression.equals("false")) {
                    intValue = 0;
                    isImmediateValue = IMMEDIATE_BOOL;
                    return true;
                } else if (expression.matches("-?\\d+")) {
                    intValue = Integer.parseInt(expression);
                    isImmediateValue = IMMEDIATE_INT;
                    return true;
                } else {
                    return false;
                }
            }

            isLeaf = false;


            int index = getOperatorIndex();

            if (index == -1) {
                return false;
            }

            operator = expression.charAt(index);

            //split and recur
            String leftExpression = expression.substring(0, index).trim();
            String rightExpression = expression.substring(index + 1, expression.length()).trim();

            left = new Expr(leftExpression);
            right = new Expr(rightExpression);

            returnVal = left.buildExpressionRec();
            returnVal &= right.buildExpressionRec();

            return returnVal;
        }

        private void replaceOperators() {
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
                    int closingIndex = getClosingBracket(4);

                    if (closingIndex == -1) {
                        syntaxError=true;
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
                    int closingIndex = getClosingBracket(2);

                    if (closingIndex == -1) {
                        syntaxError=true;
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
                int closingIndex = getClosingBracket(2);

                if (closingIndex == -1) {
                    syntaxError = true;
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


        public int getResult() {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    return 0;
                }
            } else {
                res = getChildrenResult(left.getResult(), right.getResult());
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }

        public int getResult(SpecState _ss) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_ss);
                }
            } else {
                res = getChildrenResult(left.getResult(_ss), right.getResult(_ss));
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }

        public int getResult(SpecState _ss, State _st) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_ss, _st);
                }
            } else {
                res = getChildrenResult(left.getResult(_ss, _st), right.getResult(_ss, _st));
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }

        public int getResult(CompState _cs) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_cs);
                }
            } else {
                res = getChildrenResult(left.getResult(_cs), right.getResult(_cs));
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }

        public int getResult(Object _qs) {
            int res;
            if (isLeaf) {
                if (isImmediateValue != IMMEDIATE_NO) {
                    res = intValue;
                } else {
                    res = leaf.getValue(_qs);
                }
            } else {
                res = getChildrenResult(left.getResult(_qs), right.getResult(_qs));
            }

            if (isNot) {
                res = (res == 0) ? 1 : 0;
            } else if (isNegated) {
                res = -res;
            }
            return res;
        }

        private int getChildrenResult(int leftV, int rightV) {
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

        public boolean hasState() {
            boolean hasState;
            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
                    return leaf.isState();
                } else {
                    return false;
                }
            } else {
                hasState = left.hasState();
                hasState |= right.hasState();
                return hasState;
            }
        }

        //!! replaces setBlockIndex(AvatarSpecification spec)
        public void linkComp(Spec spec) {
            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
                    leaf.linkComp(spec);
                }
            } else {
                left.linkComp(spec);
                right.linkComp(spec);
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

        private boolean checkIntegrity() {
            int optype, optypel, optyper;
            boolean returnVal;

            if (isLeaf) {
                if (isNot) {
                    return getReturnType() == IMMEDIATE_BOOL;
                } else if (isNegated) {
                    return getReturnType() == IMMEDIATE_INT;
                } else {
                    return true;
                }
            }

            optype = getType();
            optypel = left.getReturnType();
            optyper = right.getReturnType();

            switch (optype) {
                case IMMEDIATE_NO:
                    returnVal = false; //Error
                    break;
                case IMMEDIATE_INT:
                    returnVal = (optypel == IMMEDIATE_INT && optyper == IMMEDIATE_INT) ? true : false;
                    break;
                case IMMEDIATE_BOOL:
                    returnVal = (optypel == IMMEDIATE_BOOL && optyper == IMMEDIATE_BOOL) ? true : false;
                    break;
                case 3:
                    returnVal = (optypel == optyper) ? true : false;
                    break;
                default:
                    returnVal = false;
            }

            if (returnVal == false) {
                return false;
            }

            returnVal = left.checkIntegrity();
            returnVal &= right.checkIntegrity();

            return returnVal;
        }

        private String removeUselessBrackets(String _s) {
            String expression = _s;
            //TraceManager.addDev("Removing first / final brackets");
            while (expression.startsWith("(") && expression.endsWith(")")) {
                if (getClosingBracket(1) == expression.length() - 1) {
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
                        int index1 = getClosingBracket(i + 1);
                        int index2 = getClosingBracket(i + 2);
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

        private int getClosingBracket(int startChar) {
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
        public final int getAttributeType(int type) {
            switch (type) {
                case IBSAttributeClass.BoolConst:
                case IBSAttributeClass.BoolAttr: return OriginParser.IMMEDIATE_BOOL;
                case IBSAttributeClass.IntConst:
                case IBSAttributeClass.IntAttr: return OriginParser.IMMEDIATE_INT;
                default: return OriginParser.IMMEDIATE_NO;
            }
        }

        private int getType() {
            int optype;

            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
                    return getAttributeType(leaf.getType());
                } else {
                    return isImmediateValue;
                }
            }

            switch (operator) {
                case '=':
                case '$':
                    optype = 3; //BOTH sides must have the same type
                    break;
                case '<':
                case '>':
                case ':':
                case ';':
                case '-':
                case '+':
                case '/':
                case '*':
                    optype = IMMEDIATE_INT;
                    break;
                case '|':
                case '&':
                    optype = IMMEDIATE_BOOL;
                    break;
                default:
                    optype = IMMEDIATE_NO; //ERROR
                    break;
            }

            return optype;
        }

        public int getReturnType() {
            int optype;

            if (isLeaf) {
                if (isImmediateValue == IMMEDIATE_NO) {
                    return getAttributeType(leaf.getType());
                } else {
                    return isImmediateValue;
                }
            }

            switch (operator) {
                case '-':
                case '+':
                case '/':
                case '*':
                    optype = IMMEDIATE_INT;
                    break;
                case '|':
                case '&':
                case '=':
                case '$':
                case '<':
                case '>':
                case ':':
                case ';':
                    optype = IMMEDIATE_BOOL;
                    break;
                default:
                    optype = IMMEDIATE_NO; //ERROR
                    break;
            }

            return optype;
        }

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
