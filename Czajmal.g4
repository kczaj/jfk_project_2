grammar Czajmal;

prog: (statement? NEWLINE)*;

statement: declaration
        | call_function
        | assignment
        ;

// VARIABLES AND OPERATIONS

type: INT_TYPE | REAL_TYPE;

array_declare: '[' INT ']';

array_values: value ',' array_values
        | value;

array: '[' array_values ']';

declaration: type ID
            | type array_declare ID;

assignment: declaration '=' operation
            | ID '=' operation
            | ARRAY_ID '=' operation;

operation: expr0      #expressionAssignment
          | array         #arrayAssignment
;

expr0:  expr1            #single0
      | expr0 '+' expr0       #add
;

expr1:  expr2            #single1
      | expr1 '-' expr1  #del
;

expr2:  expr3            #single2
      | expr2 '*' expr2    #mult
;

expr3:  expr4            #single3
      | expr3 '/' expr3       #div
;

expr4:   INT            #int
       | REAL            #real
       | ID              #id
       | ARRAY_ID        #array_id
       | '(' expr0 ')'        #par
;

//FUNCTIONS

call_function: function_name '(' arguments ')';

function_name: defined_functions
/* tu będzie do rozszerzenia kiedy bedziemy robić funkcje definiowane przez uzytkownikow*/ ;

defined_functions: READ | PRINT;

arguments: value ',' arguments
        | value
        | /* epsilon */;

value: ID | INT | REAL | ARRAY_ID;

//TERMINALS

READ : 'read';

PRINT : 'print';

INT_TYPE : 'int';

REAL_TYPE : 'real';

ID : ('a'..'z'|'A'..'Z')+;

ARRAY_ID: ('a'..'z'|'A'..'Z')+'[''0'..'9'+']';

INT : '0'..'9'+;

REAL : '0'..'9'+'.''0'..'9'+;

NEWLINE: '\r'? '\n';

//NAME: [a-zA-Z0-9_]+;

WS:   (' '|'\t')+ { skip(); }
    ;


