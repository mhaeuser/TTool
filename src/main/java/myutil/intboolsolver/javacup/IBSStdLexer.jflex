package myutil.intboolsolver;
import java_cup.runtime.*;
import java.util.HashSet;

%%

%class IBSStdLexer< Spec extends IBSParamSpec, Comp extends IBSParamComp, State extends IBSParamState, SpecState extends IBSParamSpecState, CompState extends IBSParamCompState >
%unicode
%cup
%eofval{
   return new java_cup.runtime.Symbol(IBSStdParserSym.EOF);
%eofval}

%yylexthrow Exception

%{
private IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState> attrC;
private IBSExpressionClass<Spec,Comp,State,SpecState,CompState> exprC;
private final HashSet<String> badIdents = new HashSet<String>();

public IBSStdLexer(){}
public void setAttributeClass( IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState> _attrC) { attrC = _attrC; }
public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass() { return attrC; }
public void setExpressionClass( IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _exprC) { exprC = _exprC; }
public IBSExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass() { return exprC; }
public HashSet<String> getBadIdents() { return badIdents; }
public void clearBadIdents() { badIdents.clear(); }

private abstract class AttrHandler {
    public abstract IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s) throws Exception;
}
private class ClosedAttrHandler extends AttrHandler {
    ClosedAttrHandler(){};
    public IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute getTypedAttribute(String _s)
       throws Exception {
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
public void init(String _s) throws java.io.IOException { attrHandler = new ClosedAttrHandler(); yyclose(); yyreset(new java.io.StringReader(_s)); }
public void init(Spec _spec, String _s) throws java.io.IOException { attrHandler = new SpecAttrHandler(_spec); yyclose(); yyreset(new java.io.StringReader(_s)); }
public void init(Comp _comp, String _s) throws java.io.IOException { attrHandler = new CompAttrHandler(_comp); yyclose(); yyreset(new java.io.StringReader(_s)); }
%}

Space = [\ \n\r\t\f]
Natural = 0 | [1-9][0-9]*
Identifier = [a-zA-Z_][a-zA-Z0-9_\.]*

%state INTBOOL

%%
<YYINITIAL> "boolean"  { yybegin(INTBOOL); return new Symbol(IBSStdParserSym.PARSE_BOOL); }
<YYINITIAL> "integer"  { yybegin(INTBOOL); return new Symbol(IBSStdParserSym.PARSE_INT); }
<YYINITIAL> {Space}    {}

<INTBOOL> {
 {Space}        {}
 "true"         { return new Symbol(IBSStdParserSym.BOOL, Integer.valueOf(exprC.make_bConst(true))); }
 "false"        { return new Symbol(IBSStdParserSym.BOOL, Integer.valueOf(exprC.make_bConst(false))); }
 {Natural}      { try { return new Symbol(IBSStdParserSym.INT, Integer.valueOf(exprC.make_iConst(Integer.parseInt(yytext()))));}
                  catch (NumberFormatException nfe) { throw new Exception ("Lexer : Integer Format : " + yytext()); }
                }
 "+"            { return new Symbol(IBSStdParserSym.PLUS); }
 "-"            { return new Symbol(IBSStdParserSym.MINUS); }
 "*"            { return new Symbol(IBSStdParserSym.MULT); }
 "/"            { return new Symbol(IBSStdParserSym.DIV); }
 "%"            { return new Symbol(IBSStdParserSym.MOD); }
 "&&"           { return new Symbol(IBSStdParserSym.AND); }
 "and"          { return new Symbol(IBSStdParserSym.AND); }
 "||"           { return new Symbol(IBSStdParserSym.OR); }
 "or"           { return new Symbol(IBSStdParserSym.OR); }
 "!"            { return new Symbol(IBSStdParserSym.NOT); }
 "not"          { return new Symbol(IBSStdParserSym.NOT); }
 "=="           { return new Symbol(IBSStdParserSym.EQ); }
 "!="           { return new Symbol(IBSStdParserSym.DIF); }
 "<"            { return new Symbol(IBSStdParserSym.LT); }
 ">"            { return new Symbol(IBSStdParserSym.GT); }
 "<="           { return new Symbol(IBSStdParserSym.LEQ); }
 ">="           { return new Symbol(IBSStdParserSym.GEQ); }
 "("            { return new Symbol(IBSStdParserSym.LPAR); }
 ")"            { return new Symbol(IBSStdParserSym.RPAR); }
 {Identifier}   { IBSStdAttributeClass<Spec,Comp,State,SpecState,CompState>.TypedAttribute attr =
                      attrHandler.getTypedAttribute(yytext());
                  switch(attr.getType()) {
                      case IBSStdAttributeClass.NullAttr : badIdents.add(yytext()); throw new Exception ("Bad Ident : " +  yytext());
                      case IBSStdAttributeClass.BoolConst : return new Symbol(IBSStdParserSym.BOOL, Integer.valueOf(exprC.make_bConst(attr.getConstant()!=0)));
                      case IBSStdAttributeClass.IntConst : return new Symbol(IBSStdParserSym.INT, Integer.valueOf(exprC.make_iConst(attr.getConstant())));
                      case IBSStdAttributeClass.BoolAttr : return new Symbol(IBSStdParserSym.BOOL, Integer.valueOf(exprC.make_bVar(attr.getAttribute())));
                      case IBSStdAttributeClass.IntAttr : return new Symbol(IBSStdParserSym.INT, Integer.valueOf(exprC.make_iVar(attr.getAttribute())));
                      default : throw new Error ("Lexer, BUG : bad attribute type");
                  }
                }
}