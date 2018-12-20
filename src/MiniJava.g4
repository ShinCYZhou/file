grammar MiniJava;

//rules 
//reference: http://www.cambridge.org/us/features/052182060X/grammar.html

prog    : mainClass (classDeclaration)* EOF;

mainClass: 'class' Identifier '{' PUBLIC STATIC VOID MAIN '(' STRING '[' ']' Identifier ')' '{' statement '}' '}';
classDeclaration : 'class' Identifier (EXTENDS Identifier)? '{' (varDeclaration)* (methodDeclaration)* '}';
varDeclaration   : type Identifier ';';
methodDeclaration: PUBLIC type Identifier '(' (type Identifier (',' type Identifier)*)? ')' '{' (varDeclaration)* (statement)* RETURN expression ';' '}';
type    : INT '[' ']'       #TypeIntArray
        | BOOLEAN           #TypeBool
        | INT               #TypeInt
        | Identifier        #TypeClass
        ;
statement: '{' (statement)* '}'                               #BraceStatement
         | IF '(' expression ')' statement ELSE statement     #IfElseStatement
         | WHILE '(' expression ')' statement                 #WhileStatement
         | 'System.out.println' '(' expression ')' ';'        #PrintStatement
         | Identifier '=' expression ';'                      #AssignStatement
         | Identifier '[' expression ']' '=' expression ';'   #ArrayAssignStatement
         ;
expression    : expression '&&' expression                          #AndExpression
        | expression '<' expression                                 #LessThanExpression
        | expression ('+' | '-') expression                         #PlusMinusExpression
        | expression '*' expression                                 #MultiplyExpression
        | expression '[' expression ']'                             #ArrayExpression
        | expression '.' LENGTH                                     #LengthExpression
        | expression '.' Identifier '(' (expression (',' expression)*)? ')'       #CallExpression
        | INTEGER_LITERAL                 #IntExpression
        | TRUE                            #TrueExpression
        | FALSE                           #FalseExpression
        | Identifier                      #ClassExpression
        | THIS                            #ThisExpression
        | NEW INT '[' expression ']'      #NewIntArrayExpression
        | NEW Identifier '(' ')'          #NewClassExpression
        | '!' expression                  #NotExpression
        | '(' expression ')'              #ParenthesisExpression
        ;

// types
STATIC  : 'static';
STRING  : 'String';
INT     : 'int';
BOOLEAN : 'boolean';
VOID    : 'void';
CLASS   : 'class';
EXTENDS : 'extends';
MAIN    : 'main';
NEW     : 'new';
PUBLIC  : 'public';
IF      : 'if';
ELSE    : 'else';
WHILE   : 'while';
RETURN  : 'return';
TRUE    : 'true';
FALSE   : 'false';
LENGTH  : 'length';
THIS    : 'this';


// symbols
COMMA      : ',';
DOT        : '.';
SEMICOLON  : ';';
L_PAREN    : '(';
R_PAREN    : ')';
L_SQURE    : '[';
R_SQURE    : ']';
L_BRACE    : '{';
R_BRACE    : '}';


// Operators
ASSIGN      : '=';
GT          : '>';
LT          : '<';
GE          : '>=';
LE          : '<=';
PLUS        : '+';
MINUS       : '-';
MULTIPLY    : '*';
NOT         : '!';
OR          : '||';
AND         : '&&';


// Identifier
Identifier      : [a-zA-Z_] ([a-zA-Z_] | [0-9])*;
INTEGER_LITERAL : ('0' | [1-9] ([0-9])*);

// whitespaces
WS      : [ \t\r\n]+ -> skip ;

// Notes
NOTE_PARA: '/*' .*? '*/' -> skip;
NOTE_LINE: '//' ~[\r\n]* -> skip;
