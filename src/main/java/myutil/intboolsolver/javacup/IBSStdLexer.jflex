import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
/*
%eofval{
  return sym.EOF;
%eofval}
*/
%{

%}

Space = [\ \n\r\t\f]
Natural = 0 | [1-9][0-9]*
Ident = [A-Za-z_][A-Za-z_0-9]*

%state INTBOOL

%%
<YYINITIAL> "parse_bool" { yybegin(INTBOOL); return new Symbol(sym.PARSE_BOOL); }
<YYINITIAL> "parse_int"  { yybegin(INTBOOL); return new Symbol(sym.PARSE_INT); }

<INTBOOL> {
 {Space}        {}
 "true"         { return new Symbol(sym.TRUE); }
 "false"        { return new Symbol(sym.FALSE); }
 {Natural}      { try {return new Symbol(sym.INT, Integer.parseInt(yytext()));}
                  catch (NumberFormatException nfe) {
                  System.out.println ("NumberFormatException: " + nfe.getMessage());
                  return new Symbol(sym.INT, Integer.valueOf(0));
                  }
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
 {Ident}        { return new Symbol(sym.BIDENT, new String(yytext())); }
 "$"{Ident}     { return new Symbol(sym.IIDENT, new String(yytext())); }
}