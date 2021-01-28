// Usercode
package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

// JFlex specific DSL
// Options and declarations
%%

/* Include in a generated class definition. Useful for helper methods. */
%{
	StringBuffer string = new StringBuffer();
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		// Klasa simbol je dogovor(interfejs) o komunikaciji izmedju parsera i lexera.
		// Informacije o trenutnoj liniji i koloni su korisne za error reporting


		return new Symbol(type, yyline+1, yycolumn);
	}

	// ukljucivanje informacije o poziciji tokena i vrednosti.
	private Symbol new_symbol(int type, Object value) {


		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

/* JFlex configuration switches */
%cup
%line
%column

/* Meta states */
%xstate COMMENT
%state STRING

/* EOF edge case */
%eofval{
	return new_symbol(sym.EOF);
%eofval}

/* Macros */

/* Alias = regex */
/* :regex_common: */

%%

/* ==========================================

	Lexical Rules

==============================================*/

/* Meta states provide context. Global context is YYINITIAL */

<YYINITIAL> {
	/* whitespace - swallow */
	" " 	{ }
	"\b" 	{ }
	"\t" 	{ }
	"\r\n" 	{ }
	"\n" 	{ }
	"\f" 	{ }

	/* keywords */
	"program"   { return new_symbol(sym.PROG, yytext()); }
	"break"   { return new_symbol(sym.BREAK, yytext()); }
	"class"   { return new_symbol(sym.CLASS, yytext()); }
	"else"   { return new_symbol(sym.ELSE, yytext()); }
	"if"   { return new_symbol(sym.IF, yytext()); }
	"new"   { return new_symbol(sym.NEW, yytext()); }
	"const"   { return new_symbol(sym.CONST, yytext()); }
	"enum"   { return new_symbol(sym.ENUM, yytext()); }
	"print"   { return new_symbol(sym.PRINT, yytext()); }
	"read"   { return new_symbol(sym.READ, yytext()); }
	"return"   { return new_symbol(sym.RETURN, yytext()); }
	"void"   { return new_symbol(sym.VOID, yytext()); }
	"do"   { return new_symbol(sym.DO, yytext()); }
	"while" 	{ return new_symbol(sym.WHILE, yytext()); }
	"extends" 	{ return new_symbol(sym.EXTENDS, yytext()); }
	"continue" 		{ return new_symbol(sym.CONTINUE, yytext()); }

	/* operators */
	"+" 		{ return new_symbol(sym.PLUS, yytext()); }
	"-" 		{ return new_symbol(sym.MINUS, yytext()); }
	"*" 		{ return new_symbol(sym.MUL, yytext()); }
	"/" 		{ return new_symbol(sym.DIV, yytext()); }
	"%" 		{ return new_symbol(sym.MOD, yytext()); }
	"==" 		{ return new_symbol(sym.SAME, yytext()); }
	"!=" 		{ return new_symbol(sym.NOT_SAME, yytext()); }
	">" 		{ return new_symbol(sym.GREATER, yytext()); }
	">=" 		{ return new_symbol(sym.GREATER_EQUAL, yytext()); }
	"<" 		{ return new_symbol(sym.LOWER, yytext()); }
	"<=" 		{ return new_symbol(sym.LOWER_EQUAL, yytext()); }
	"&&" 		{ return new_symbol(sym.AND, yytext()); }
	"||" 		{ return new_symbol(sym.OR, yytext()); }
	"=" 		{ return new_symbol(sym.EQUAL, yytext()); }
	"++" 		{ return new_symbol(sym.INCREMENT, yytext()); }
	"--" 		{ return new_symbol(sym.DECREMENT, yytext()); }
	";" 		{ return new_symbol(sym.SEMI, yytext()); }
	"," 		{ return new_symbol(sym.COMMA, yytext()); }
	"." 		{ return new_symbol(sym.POINT, yytext()); }
	"(" 		{ return new_symbol(sym.LPAREN, yytext()); }
	")" 		{ return new_symbol(sym.RPAREN, yytext()); }
	"[" 		{ return new_symbol(sym.LBRACKET, yytext()); }
	"]" 		{ return new_symbol(sym.RBRACKET, yytext()); }
	"{" 		{ return new_symbol(sym.LBRACE, yytext()); }
	"}"			{ return new_symbol(sym.RBRACE, yytext()); }
	"?"			{ return new_symbol(sym.TERNARY_IF, yytext()); }
	":"			{ return new_symbol(sym.TERNARY_ELSE, yytext()); }

	/* constants */
    [0-9]+                        { return new_symbol(sym.NUMBER, new Integer (yytext())); }
 	'[\040-\176]'                  { return  new_symbol(sym.CHAR, new Character(yytext().charAt(1))); }
	"true" | "false"               { return new_symbol(sym.BOOLEAN, Boolean.valueOf(yytext())); }

	/* identifiers */
	([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{return new_symbol (sym.IDENT, yytext()); }
	"//" 		     { yybegin(COMMENT); }
}

<COMMENT> .      { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }
<COMMENT> "\n"   { yybegin(YYINITIAL); }

// This needs to be on the bottom, because it has the least priority.
. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1) + ", kolona " + yycolumn); }






