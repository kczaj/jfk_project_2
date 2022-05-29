grammar Czajmal;

prog: block;

block: (statement? NEWLINE)*;

statement: declaration
        | call_function
        | assignment
        | ifblock
        | loopblock
        ;
//LOOP
loopblock: LOOP ID condition BEGIN blockfor ENDFOR;

blockfor: block;

// CONDITIONS
ifblock: IF condition BEGIN blockif ENDIF ELSE blockelse ENDELSE;

blockif: block;

blockelse: block;

condition: ID if_operation comparable_value;

if_operation: EQUALS #eq
        | NOTEQUALS #neq
        | LESS #ls
        | GREATER #gr
        ;

// VARIABLES AND OPERATIONS

type: INT_TYPE | REAL_TYPE | CHAR_TYPE;

array_declare: '{' INT '}';

array_values: value ',' array_values
        | value;

array: '[' array_values ']';

declaration: type ID
            | type array_declare ID;

assignment: declaration '=' operation #declarationAssignment
            | ID '=' operation  #idAssignment
            | ARRAY_ID '=' expr0    #arrayIdAssignment
            | ARRAY_ID '=' STRING   #stringIdAssignment
            ;

operation: expr0    #expression
          | array   #arrayOp
          | STRING  #string
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

value: ID | INT | REAL | STRING | ARRAY_ID;

comparable_value: INT | REAL;

//TERMINALS

IF: 'if';
ENDIF: 'endif';
ELSE: 'else';
ENDELSE: 'endelse';
BEGIN: 'begin';
EQUALS: '==';
NOTEQUALS: '!=';
GREATER: '>';
LESS: '<';
LOOP: 'loop';
ENDFOR: 'endfor';

STRING : '"' ( ~('\\'|'"') )* '"';

READ : 'read';

PRINT : 'print';

INT_TYPE : 'int';

REAL_TYPE : 'real';

CHAR_TYPE : 'char';

ID : ('a'..'z'|'A'..'Z')+;

ARRAY_ID: ('a'..'z'|'A'..'Z')+'[''0'..'9'+']';

INT : '0'..'9'+;

REAL : '0'..'9'+'.''0'..'9'+;

NEWLINE: '\r'? '\n';

//NAME: [a-zA-Z0-9_]+;

WS:   (' '|'\t')+ { skip(); }
    ;


