grammar Expression;

// Entry point
expr: logicalExpr EOF;

// Logical Expressions (lowest precedence)
logicalExpr
    : logicalExpr 'and' logicalExpr        # AndExpression
    | logicalExpr 'or' logicalExpr        # OrExpression
    | '!' logicalExpr                    # NotExpression
    | comparisonExpr                     # ToComparison
    | membershipExpr                     # ToMembership
    ;

// Membership Expression (e.g., x in [1, 2, 3])
membershipExpr
    : atom 'in' atom                      # InExpression
    | comparisonExpr                      # ToComparisonExpr
    ;

// Comparison Expressions (next precedence level)
comparisonExpr
    : arithmeticExpr (comparisonOp arithmeticExpr)?  # Comparison
    ;

// Comparison Operators
comparisonOp
    : '==' | '!=' | '<' | '>' | '<=' | '>='
    ;

// Arithmetic Expressions (higher precedence than comparison)
arithmeticExpr
    : arithmeticExpr ('+' | '-') arithmeticExpr     # AddSub
    | arithmeticExpr ('*' | '/' | '%') arithmeticExpr # MulDivMod
    | '-' arithmeticExpr                            # UnaryMinus
    | '(' logicalExpr ')'                           # ParensExpr
    | functionCall                                  # ToFunctionCall
    | atom                                          # ToAtom
    | booleanLiteral          # Boolean
    ;

// Function Calls
functionCall
    : IDENTIFIER '(' exprList? ')'                  # CustomFunctionCall
    ;

// List of arguments for function calls
exprList
    : arithmeticExpr (',' arithmeticExpr)*
    ;

// Atoms (literals and identifiers)
atom
    : NUMBER              # NumberAtom
    | STRING              # StringAtom
    | IDENTIFIER          # Variable
    | '(' exprList? ')'   # ListLiteral
    ;

// Tokens
NUMBER      : [0-9]+ ('.' [0-9]+)? ;
STRING      : '"' .*? '"' ;
IDENTIFIER  : [a-zA-Z_][a-zA-Z_0-9]* ;
booleanLiteral     : 'true' | 'false';
WS          : [ \t\r\n]+ -> skip ;
