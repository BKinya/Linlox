package lox

sealed interface Expr

data class Assignment(val name: Token, val value: Expr): Expr
data class Ternary(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr
data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr
data class Grouping(val expression: Expr) : Expr
data class Unary(val operator: Token, val right: Expr) : Expr
data class Literal(val value: Any?) : Expr
data class Variable(val name: Token) : Expr
data class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr


