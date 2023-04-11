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