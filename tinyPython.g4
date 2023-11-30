grammar tinyPython;

@header {
	package generated;
}

options {
    visitor = false;
}

program:
    file_input
    ;

//single_input: NEWLINE | simple_stmt | compound_stmt NEWLINE;
file_input:
	defs
    (NEWLINE | stmt)* EOF
    ;

defs:
	(NEWLINE| def_stmt)*
	;

stmt:
      simple_stmt
    | compound_stmt
    ;

simple_stmt:
    small_stmt NEWLINE
    ;

small_stmt:
      assignment_stmt
    | flow_stmt
    | print_stmt
    | return_stmt
    ;

assignment_stmt:
    NAME '=' expr
    ;

flow_stmt:
      break_stmt
    | continue_stmt
    ;

break_stmt:
    'break'
    ;

continue_stmt:
    'continue'
    ;

compound_stmt:
      if_stmt
    | while_stmt
   // | def_stmt
    ;

if_stmt:
    'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?
    ;

while_stmt:
    'while' test ':' suite
    ;

def_stmt:
    'def' NAME OPEN_PAREN args CLOSE_PAREN ':' suite
    ;

suite:
      simple_stmt
    | NEWLINE  stmt+
    ;

args:

    | NAME (',' NAME)*
    ;

return_stmt:
    'return' expr?
    ;

test:
    expr comp_op expr
    ;

print_stmt:
    'print' print_arg
    ;

print_arg:
      STRING
    | expr
    ;

comp_op:
      '<'
    | '>'
    | '=='
    | '>='
    | '<='
    | '!='
    ;

expr:
      NAME opt_paren
    | NUMBER
    | '(' expr ')'
    | expr (( '+' | '-' ) expr)+
 ;

opt_paren:

    | '(' ')'
    | '(' expr (',' expr)* ')'
    ;
/*
 * lexer rules
 */

STRING:
    STRING_LITERAL
    ;

NUMBER:
    INTEGER
    ;

INTEGER:
    DECIMAL_INTEGER
    ;

NEWLINE:
    ( '\r'? '\n' | '\r' | '\f' ) SPACES?
    ;

NAME:
    ID_START ID_CONTINUE*
    ;

STRING_LITERAL:
    '"' .*? '"'
    ;

DECIMAL_INTEGER:
      NON_ZERO_DIGIT DIGIT*
    | '0'+
    ;

OPEN_PAREN:     '(';
CLOSE_PAREN:    ')';
OPEN_BRACK:     '[';
CLOSE_BRACK:    ']';
OPEN_BRACE:     '{';
CLOSE_BRACE:    '}';

SKIP_:
    ( SPACES | COMMENT | LINE_JOINING ) -> skip
    ;

UNKNOWN_CHAR:
    .
    ;


/*
 * fragments
 */

fragment NON_ZERO_DIGIT:
    [1-9]
    ;

fragment DIGIT:
    [0-9]
    ;

fragment SPACES:
    [ \t]+
    ;

fragment COMMENT:
    '#' ~[\r\n\f]*
    ;

fragment LINE_JOINING:
    '\\' SPACES? ( '\r'? '\n' | '\r' | '\f' )
    ;

fragment ID_START:
    '_'
    | [A-Z]
    | [a-z]
    ;

fragment ID_CONTINUE:
    ID_START
    | [0-9]
    ;