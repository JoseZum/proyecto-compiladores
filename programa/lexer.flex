import java_cup.runtime.*;

%%

%class Lexer
%cup
%unicode
%line
%column

%%

/* Keywords */
"world"   { return new Symbol(sym.WORLD, yyline+1, yycolumn+1, yytext()); }
"int"     { return new Symbol(sym.INT_TYPE, yyline+1, yycolumn+1, yytext()); }

/* Special symbols */
"endl"    { return new Symbol(sym.ENDL, yyline+1, yycolumn+1, yytext()); }
"="       { return new Symbol(sym.ASSIGN, yyline+1, yycolumn+1, yytext()); }

/* Literals */
[0-9]+    { return new Symbol(sym.INT_LIT, yyline+1, yycolumn+1, Integer.parseInt(yytext())); }

/* Identifiers */
[a-zA-Z_][a-zA-Z0-9_]* { return new Symbol(sym.ID, yyline+1, yycolumn+1, yytext()); }

/* Whitespace */
[ \t\r\n]+ { /* Ignore whitespace */ }

/* Error handling */
.         { System.err.println("Caracter no reconocido en línea " + (yyline+1) + ": " + yytext()); }