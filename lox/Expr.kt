package lox

sealed interface Expr

data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr
data class Grouping(val expression: Expr) : Expr
data class Unary(val operator: Token, val right: Expr) : Expr
data class Literal(val value: Any?) : Expr


fun Expr.evaluate(): String = when (this) {
    is Binary -> evaluateBinary()
    is Grouping -> evaluateGrouping()
    is Literal -> evaluateLiteral()
    is Unary -> evaluateUnary()
}

