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

package myutil.intboolsolver;
import java_cup.runtime.*;
import java.util.HashSet;

%%

%class IBSLexerClassName
%implements IBSScanner<Spec, Comp, State, SpecState, CompState>

%public
%unicode
%cup
%eofval{
   return new java_cup.runtime.Symbol(IBSFlex#Symb.EOF);
%eofval}

%yylexthrow Exception

%{
private IBSAttributeClass<Spec,Comp,State,SpecState,CompState> attrC;
private IBSExpressionClass<Spec,Comp,State,SpecState,CompState> exprC;
private final HashSet<String> badIdents = new HashSet<String>();

public IBSLexerClassName(){}
public void setAttributeClass( IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _attrC) { attrC = _attrC; }
public IBSAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass() { return attrC; }
public void setExpressionClass( IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _exprC) { exprC = _exprC; }
public IBSExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass() { return exprC; }
public HashSet<String> getBadIdents() { return badIdents; }
public void clearBadIdents() { badIdents.clear(); }

private abstract class AttrHandler {
    public abstract IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s) throws Exception;
}
private class ClosedAttrHandler extends AttrHandler {
    ClosedAttrHandler(){};
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s)
       throws Exception {
        badIdents.add(_s); throw new Exception ("Ident in closed expression: " +  _s);
    }
}
private class SpecAttrHandler extends AttrHandler {
    private Spec spec;
    SpecAttrHandler(Spec _spec){ spec = _spec; }
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s){
        return attrC.getTypedAttribute(spec,_s);
    }
}
private class CompAttrHandler extends AttrHandler {
    private Comp comp;
    CompAttrHandler(Comp _comp){ comp = _comp; }
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s){
        return attrC.getTypedAttribute(comp,_s);
    }
}
private AttrHandler attrHandler;
public void init(String _s) throws java.io.IOException { attrHandler = new ClosedAttrHandler(); yyclose(); yyreset(new java.io.StringReader(_s)); }
public void init(Spec _spec, String _s) throws java.io.IOException { attrHandler = new SpecAttrHandler(_spec); yyclose(); yyreset(new java.io.StringReader(_s)); }
public void init(Comp _comp, String _s) throws java.io.IOException { attrHandler = new CompAttrHandler(_comp); yyclose(); yyreset(new java.io.StringReader(_s)); }
%}

Space = [\ \n\r\t\f]
Natural = 0 | [1-9][0-9]*
Identifier = [a-zA-Z_][a-zA-Z0-9_\.]*

%state INTBOOL

%%
<YYINITIAL> "boolean"  { yybegin(INTBOOL); return new Symbol(IBSFlex#Symb.PARSE_BOOL); }
<YYINITIAL> "guard"    { yybegin(INTBOOL); return new Symbol(IBSFlex#Symb.PARSE_GUARD); }
<YYINITIAL> "integer"  { yybegin(INTBOOL); return new Symbol(IBSFlex#Symb.PARSE_INT); }
<YYINITIAL> {Space}    {}

<INTBOOL> {
 {Space}        {}
 "true"         { return new Symbol(IBSFlex#Symb.BOOL, Integer.valueOf(exprC.make_bConst(true))); }
 "false"        { return new Symbol(IBSFlex#Symb.BOOL, Integer.valueOf(exprC.make_bConst(false))); }
 {Natural}      { try { return new Symbol(IBSFlex#Symb.INT, Integer.valueOf(exprC.make_iConst(Integer.parseInt(yytext()))));}
                  catch (NumberFormatException nfe) { throw new Exception ("Lexer : Integer Format : " + yytext()); }
                }
 "+"            { return new Symbol(IBSFlex#Symb.PLUS); }
 "-"            { return new Symbol(IBSFlex#Symb.MINUS); }
 "*"            { return new Symbol(IBSFlex#Symb.MULT); }
 "/"            { return new Symbol(IBSFlex#Symb.DIV); }
 "%"            { return new Symbol(IBSFlex#Symb.MOD); }
 "&&"           { return new Symbol(IBSFlex#Symb.AND); }
 "and"          { return new Symbol(IBSFlex#Symb.AND); }
 "||"           { return new Symbol(IBSFlex#Symb.OR); }
 "or"           { return new Symbol(IBSFlex#Symb.OR); }
 "!"            { return new Symbol(IBSFlex#Symb.NOT); }
 "not"          { return new Symbol(IBSFlex#Symb.NOT); }
 "=="           { return new Symbol(IBSFlex#Symb.EQ); }
 "!="           { return new Symbol(IBSFlex#Symb.DIF); }
 "<"            { return new Symbol(IBSFlex#Symb.LT); }
 ">"            { return new Symbol(IBSFlex#Symb.GT); }
 "<="           { return new Symbol(IBSFlex#Symb.LEQ); }
 ">="           { return new Symbol(IBSFlex#Symb.GEQ); }
 "("            { return new Symbol(IBSFlex#Symb.LPAR); }
 ")"            { return new Symbol(IBSFlex#Symb.RPAR); }
 {Identifier}   { IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute attr =
                      attrHandler.getTypedAttribute(yytext());
                  switch(attr.getType()) {
                      case IBSAttributeClass.NullAttr : badIdents.add(yytext()); throw new Exception ("Bad Ident : " +  yytext());
                      case IBSAttributeClass.BoolConst : return new Symbol(IBSFlex#Symb.BOOL, Integer.valueOf(exprC.make_bConst(attr.getConstant()!=0)));
                      case IBSAttributeClass.IntConst : return new Symbol(IBSFlex#Symb.INT, Integer.valueOf(exprC.make_iConst(attr.getConstant())));
                      case IBSAttributeClass.BoolAttr : return new Symbol(IBSFlex#Symb.BOOL, Integer.valueOf(exprC.make_bVar(attr.getAttribute())));
                      case IBSAttributeClass.IntAttr : return new Symbol(IBSFlex#Symb.INT, Integer.valueOf(exprC.make_iVar(attr.getAttribute())));
                      default : throw new Error ("Lexer, BUG : bad attribute type");
                  }
                }
}