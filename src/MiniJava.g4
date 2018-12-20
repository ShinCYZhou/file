grammar MiniJava;

// parser rules
// reference: http://www.cambridge.org/us/features/052182060X/grammar.html
prog    : mainClass (classDeclaration )* EOF;

mainClass: 'class' Identifier '{' PUBLIC STATIC VOID MAIN '(' STRING '[' ']' Identifier ')' '{' statement '}' '}';
classDeclaration  : 'class' Identifier (EXTENDS Identifier)? '{' (varDec)* (methodDec)* '}';
varDec   : type Identifier ';';
methodDec: PUBLIC type Identifier '(' (type Identifier (',' type Identifier)*)? ')' '{' (varDec)* (statement)* RETURN expression ';' '}';
type    : INT '[' ']'       #IntArrayType
        | BOOLEAN           #BooleanType
        | INT               #IntType
        | Identifier                #ClassType
        ;
statement: '{' (statement)* '}'                         #BraceStatement
         | IF '(' expression ')' statement ELSE statement     #IfStatement
         | WHILE '(' expression ')' statement                 #WhileStatement
         | 'System.out.println' '(' expression ')' ';'        #PrintStatement
         | Identifier '=' expression ';'                              #AssignStatement
         | Identifier '[' expression ']' '=' expression ';'                 #AssignArrayStatement
         ;
expression: expression '&&' expression                                #AndExpr
          | expression '<' expression                                 #LessExpr
          | expression ('+' | '-') expression                         #PlusMinusExpr
          | expression '*' expression                                 #TimesExpr
          | expression '[' expression ']'                             #IndexExpr
          | expression '.' LENGTH                               #LengthExpr
          | expression '.' Identifier '(' (expression (',' expression)*)? ')'       #CallExpr
          | <INTEGER_LITERAL>                   #IntExpr
          | TRUE                      #TrueExpr
          | FALSE                     #FalseExpr
          | Identifier                        #ClassExpr
          | THIS                      #ThisExpr
          | NEW INT '[' expression ']'      #NewIntArrayExpr
          | NEW Identifier '(' ')'            #NewClassInstanceExpr
          | '!' expression                  #NotExpr
          | '(' expression ')'              #ParenthesisExpr
          ;

// types
BOOLEAN : 'boolean';
CLASS   : 'class';
ELSE    : 'else';
EXTENDS : 'extends';
FALSE   : 'false';
IF      : 'if';
INT     : 'int';
LENGTH  : 'length';
MAIN    : 'main';
NEW     : 'new';
PUBLIC  : 'public';
RETURN  : 'return';
STATIC  : 'static';
STRING  : 'String';
THIS    : 'this';
TRUE    : 'true';
VOID    : 'void';
WHILE   : 'while';

// symbols
L_PAREN    : '(';
R_PAREN    : ')';
L_SQURE    : '[';
R_SQURE    : ']';
L_BRACE    : '{';
R_BRACE    : '}';
COMMA      : ',';
DOT        : '.';
SEMICOLON  : ';';

// Operators
ASSIGN  : '=';
GT      : '>';
LT      : '<';
GE      : '>=';
LE      : '<=';
PLUS    : '+';
MINUS   : '-';
TIMES   : '*';
BANG    : '!';
AND     : '&&';
OR      : '||';

// Identifier
Identifier      : [a-zA-Z_] ([a-zA-Z_] | [0-9])*;
INTEGER_LITERAL : ('0' | [1-9] [0-9]*);

// whitespaces
WS      : [ \t\r\n]+ -> skip ;

// Notes
NOTE_PARA: '/*' .*? '*/' -> skip;
NOTE_LINE: '//' ~[\r\n]* -> skip;