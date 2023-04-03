package myutil.intboolsolver;
import java_cup.runtime.*;
import java.util.HashSet;

%%

%class IBSStdLexer< Spec extends IBSParamSpec, Comp extends IBSParamComp, State extends IBSParamState, SpecState extends IBSParamSpecState, CompState extends IBSParamCompState >
%unicode
%cup

%{
private IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState> attrC;
private IBSExpressionClass<Spec,Comp,State,SpecState,CompState> exprC;
private final HashSet<String> badIdents = new HashSet<String>();

public IBSStdLexer(){}
public void setAttributeClass( IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState> _attrC) { attrC = _attrC; }
public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass() { return attrC; }
public void setExpressionClass( IBSStdExpressionClass<Spec,Comp,State,SpecState,CompState> _exprC) { exprC = _exprC; }
public IBSStdExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass() { return exprC; }
public HashSet<String> getBadIdents() { return badIdents; }
public void clearBadIdents() { badIdents.clear(); }

private abstract class AttrHandler {
    public abstract IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s);
}
private class ClosedAttrHandler extends AttrHandler {
    ClosedAttrHandler(){};
    public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s){
        badIdents.add(_s); throw new Exception ("Ident in closed expression: " +  _s);
    }
}
private class SpecAttrHandler extends AttrHandler {
    private Spec spec;
    SpecAttrHandler(Spec _spec){ spec = _spec; }
    public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s){
        return attrC.getTypedAttribute(spec,_s);
    }
}
private class CompAttrHandler extends AttrHandler {
    private Comp comp;
    CompAttrHandler(Comp _comp){ comp = _comp; }
    public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s){
        return attrC.getTypedAttribute(comp,_s);
    }
}
private AttrHandler attrHandler;
public void init(String _s) { attrHandler = new ClosedAttrHandler(); yyclose(); yyreset(new java.io.StringReader(_s)); }
public void init(Spec _spec, String _s) { attrHandler = new SpecAttrHandler(_spec); yyclose(); yyreset(new java.io.StringReader(_s)); }
public void init(Comp _comp, String _s) { attrHandler = new CompAttrHandler(_comp); yyclose(); yyreset(new java.io.StringReader(_s)); }
%}

Space = [\ \n\r\t\f]
Natural = 0 | [1-9][0-9]*
Identifier = [a-zA-Z_][a-zA-Z0-9_\.]*

%state INTBOOL

%%
<YYINITIAL> "boolean"  { yybegin(INTBOOL); return new Symbol(sym.PARSE_BOOL); }
<YYINITIAL> "integer"  { yybegin(INTBOOL); return new Symbol(sym.PARSE_INT); }
<YYINITIAL> {Space}    {}

<INTBOOL> {
 {Space}        {}
 "true"         { return new Symbol(sym.BOOL, exprC.make_bConst(1)); }
 "false"        { return new Symbol(sym.BOOL, exprC.make_bConst(0)); }
 {Natural}      { try { return new Symbol(sym.INT, exprC.make_iConst(Integer.parseInt(yytext())));}
                  catch (NumberFormatException nfe) { throw new Exception ("Lexer : Integer Format : " + yytext()); }
                }
 "+"            { return new Symbol(sym.PLUS); }
 "-"            { return new Symbol(sym.MINUS); }
 "*"            { return new Symbol(sym.MULT); }
 "/"            { return new Symbol(sym.DIV); }
 "%"            { return new Symbol(sym.MOD); }
 "&&"           { return new Symbol(sym.AND); }
 "and"          { return new Symbol(sym.AND); }
 "||"           { return new Symbol(sym.OR); }
 "or"           { return new Symbol(sym.OR); }
 "!"            { return new Symbol(sym.NOT); }
 "not"          { return new Symbol(sym.NOT); }
 "=="           { return new Symbol(sym.EQ); }
 "!="           { return new Symbol(sym.DIF); }
 "<"            { return new Symbol(sym.LT); }
 ">"            { return new Symbol(sym.GT); }
 "<="           { return new Symbol(sym.LEQ); }
 ">="           { return new Symbol(sym.GEQ); }
 "("            { return new Symbol(sym.LPAR); }
 ")"            { return new Symbol(sym.RPAR); }
 {Identifier}   { IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute attr =
                      attrHandler.getTypedAttribute(yytext());
                  switch(attr.getType()) {
                      case NullAttr : badIdents.add(yytext()); throw new Exception ("Bad Ident : " +  yytext());
                      case BoolConst : return new Symbol(sym.BOOL, exprC.make_bConst(attr.getConstant()));
                      case IntConst : return new Symbol(sym.INT, exprC.make_iConst(attr.getConstant()));
                      case BoolAttr : return new Symbol(sym.BOOL, exprC.make_bVar(attr.getAttribute()));
                      case IntAttr : return new Symbol(sym.INT, exprC.make_iVar(attr.getAttribute()));
                      default : throw new Error ("Lexer, BUG : bad attribute type");
                  }
                }
}