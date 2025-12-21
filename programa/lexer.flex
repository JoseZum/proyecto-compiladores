import java_cup.runtime.*;

%%

%class Lexer
%cup
%unicode
%line
%column

%%

<YYINITIAL> {

/* Comentarios */
"|" [^\r\n]* { /* ignore */ }
"є" [^э]* "э" { /* ignore */ }

/* Bloque de codigo */
"¡" { return new Symbol(sym.BLOCK_OPEN, yyline+1, yycolumn+1, yytext()); }
"!" { return new Symbol(sym.BLOCK_CLOSE, yyline+1, yycolumn+1, yytext()); }

/* Palabras clave */
"navidad" { return new Symbol(sym.NAVIDAD, yyline+1, yycolumn+1, yytext()); }
"gift" { return new Symbol(sym.GIFT, yyline+1, yycolumn+1, yytext()); }
"world" { return new Symbol(sym.WORLD, yyline+1, yycolumn+1, yytext()); }
"int" { return new Symbol(sym.INT, yyline+1, yycolumn+1, yytext()); }
"float" { return new Symbol(sym.FLOAT, yyline+1, yycolumn+1, yytext()); }
"boolean" { return new Symbol(sym.BOOLEAN, yyline+1, yycolumn+1, yytext()); }
"char" { return new Symbol(sym.CHAR, yyline+1, yycolumn+1, yytext()); }
"string" { return new Symbol(sym.STRING, yyline+1, yycolumn+1, yytext()); }
"local" { return new Symbol(sym.LOCAL, yyline+1, yycolumn+1, yytext()); }
"coal"    { return new Symbol(sym.COAL_TYPE, yyline+1, yycolumn+1, yytext()); }

/* Estructuras de Control */
"decide" { return new Symbol(sym.DECIDE, yyline+1, yycolumn+1, yytext()); }
"of" { return new Symbol(sym.OF, yyline+1, yycolumn+1, yytext()); }
"end" { return new Symbol(sym.END, yyline+1, yycolumn+1, yytext()); }
"else" { return new Symbol(sym.ELSE, yyline+1, yycolumn+1, yytext()); }
"loop" { return new Symbol(sym.LOOP, yyline+1, yycolumn+1, yytext()); }
"exit" { return new Symbol(sym.EXIT, yyline+1, yycolumn+1, yytext()); }
"when" { return new Symbol(sym.WHEN, yyline+1, yycolumn+1, yytext()); }
"for" { return new Symbol(sym.FOR, yyline+1, yycolumn+1, yytext()); }
"break" { return new Symbol(sym.BREAK, yyline+1, yycolumn+1, yytext()); }
"return" { return new Symbol(sym.RETURN, yyline+1, yycolumn+1, yytext()); }

/* Simbolos especiales */
"->" { return new Symbol(sym.ARROW, yyline+1, yycolumn+1, yytext()); }
"endl" { return new Symbol(sym.ENDL, yyline+1, yycolumn+1, yytext()); }
"=" { return new Symbol(sym.ASSIGN, yyline+1, yycolumn+1, yytext()); }
"¿" { return new Symbol(sym.PARENTHESIS_OPEN, yyline+1, yycolumn+1, yytext()); }
"?" { return new Symbol(sym.PARENTHESIS_CLOSE, yyline+1, yycolumn+1, yytext()); }
"[" { return new Symbol(sym.ARRAY_OPEN, yyline+1, yycolumn+1, yytext()); }
"]" { return new Symbol(sym.ARRAY_CLOSE, yyline+1, yycolumn+1, yytext()); }
"," { return new Symbol(sym.COMMA, yyline+1, yycolumn+1, yytext()); }

/* Operadores */
"+" { return new Symbol(sym.PLUS, yyline+1, yycolumn+1, yytext()); }
"-" { return new Symbol(sym.MINUS, yyline+1, yycolumn+1, yytext()); }
"*" { return new Symbol(sym.TIMES, yyline+1, yycolumn+1, yytext()); }
"//" { return new Symbol(sym.DIV_INT, yyline+1, yycolumn+1, yytext()); }
"/" { return new Symbol(sym.DIV, yyline+1, yycolumn+1, yytext()); }
"%" { return new Symbol(sym.MODULE, yyline+1, yycolumn+1, yytext()); }
"^" { return new Symbol(sym.POWER, yyline+1, yycolumn+1, yytext()); }

/* Relacionales */
"<=" { return new Symbol(sym.LESS_OR_EQ_THAN, yyline+1, yycolumn+1, yytext()); }
"<" { return new Symbol(sym.LESS_THAN, yyline+1, yycolumn+1, yytext()); }
">=" { return new Symbol(sym.MORE_OR_EQ_THAN, yyline+1, yycolumn+1, yytext()); }
">" { return new Symbol(sym.MORE_THAN, yyline+1, yycolumn+1, yytext()); }
"==" { return new Symbol(sym.EQUALS, yyline+1, yycolumn+1, yytext()); }
"!=" { return new Symbol(sym.DIFFERENT, yyline+1, yycolumn+1, yytext()); }

/* Expresiones Aritmeticas */

/* Logicos */
"@" { return new Symbol(sym.AND, yyline+1, yycolumn+1, yytext()); }
"~" { return new Symbol(sym.OR, yyline+1, yycolumn+1, yytext()); }
"Σ" { return new Symbol(sym.NOT, yyline+1, yycolumn+1, yytext()); }

/* Unarios */
"--" { return new Symbol(sym.DECREMENT, yyline+1, yycolumn+1, yytext()); }
"++" { return new Symbol(sym.INCREMENT, yyline+1, yycolumn+1, yytext()); }

/* I/O */
"show" { return new Symbol(sym.SHOW, yyline+1, yycolumn+1, yytext()); }
"get" { return new Symbol(sym.GET, yyline+1, yycolumn+1, yytext()); }


/* Literales */
0|[1-9][0-9]* { return new Symbol(sym.INT_LIT, yyline+1, yycolumn+1, yytext()); }
(0|[1-9][0-9]*) "." [0-9]+ { return new Symbol(sym.FLOAT_LIT, yyline+1, yycolumn+1, yytext()); }
"true" | "false" { return new Symbol(sym.BOOLEAN_LIT, yyline+1, yycolumn+1, yytext()); }
\' [^\' ] \' { return new Symbol(sym.CHAR_LIT, yyline+1, yycolumn+1, yytext()); }
\" [^\"]* \" { return new Symbol(sym.STRING_LIT, yyline+1, yycolumn+1, yytext()); }

/* Identificadores */
[a-zA-Z_][a-zA-Z0-9_]* { return new Symbol(sym.ID, yyline+1, yycolumn+1, yytext()); }

/* Espacio en blanco */
[ \t\r\n]+ { /* Ignore whitespace */ }

/* Errores */
. { System.err.println("Caracter no reconocido en línea " + (yyline+1) + ": " + yytext()); }


}

