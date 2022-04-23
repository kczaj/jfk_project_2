grammar Czajmal;

prog: (statement? NEWLINE)*;

statement: declaration
        | call_function
        | assignment
        ;

declaration: type ID ;

call_function: function_name '(' arguments ')';

type: 'int' | 'real';

assignment: declaration '=' operation
            | ID '=' operation;

operation: expr0 ;//expression;

//expression: expr0
//            | /*epsilon*/;

expr0:  expr1            #single0
      | expr1 '+' expr0        #add
      | expr1 '-' expr0        #del
;

expr1:  expr2            #single1
      | expr2 '*' expr0    #mult
      | expr2 '/' expr0    #dif
;

expr2:   INT            #int
       | REAL            #real
       | ID              #id
       | '(' expr0 ')'        #par
;

function_name: defined_functions
/* tu będzie do rozszerzenia kiedy bedziemy robić funkcje definiowane przez uzytkownikow*/ ;

defined_functions: READ | PRINT;

arguments: value ',' arguments
        | value
        | /* epsilon */;

value: ID | INT | REAL;

READ : 'read';

PRINT : 'print';

ID : ('a'..'z'|'A'..'Z')+;

INT : '0'..'9'+;

REAL : '0'..'9'+'.''0'..'9'+;

NEWLINE: '\r'? '\n';

//NAME: [a-zA-Z0-9_]+;

WS:   (' '|'\t')+ { skip(); }
    ;

