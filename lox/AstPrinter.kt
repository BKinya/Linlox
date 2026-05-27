package lox

fun main(args: Array<String>) {
    val expression = Binary(
        left = Unary(
            Token(
                TokenType.MINUS, "-", null, 1
            ), Literal(123)
        ),
        operator = Token(TokenType.STAR, "*", null, 1),
        right = Grouping(
            Literal(45.67)
        )
    )

    println(expression.evaluate())
}
fun Expr.evaluate(): String = when (this) {
    is Binary -> evaluateBinary()
    is Grouping -> evaluateGrouping()
    is Literal -> evaluateLiteral()
    is Unary -> evaluateUnary()
    is Ternary -> evaluateTernary()
}

fun Ternary.evaluateTernary(): String {
    return parenthesize("?", condition, thenBranch, elseBranch)
}

fun Binary.evaluateBinary(): String {
    return parenthesize(operator.lexeme, left, right)
}

fun Unary.evaluateUnary(): String {
    return parenthesize(operator.lexeme, right)
}

fun Grouping.evaluateGrouping(): String {
    return parenthesize("group", expression)
}

fun Literal.evaluateLiteral(): String {
    return value?.toString() ?: "nil"
}

private fun parenthesize(name: String, vararg exprs: Expr): String {
    val builder = StringBuilder()
    builder.append("(").append(name)
    for (expr in exprs) {
        builder.append(" ")
        builder.append(expr.evaluate())
    }
    builder.append(")")
    return builder.toString()
}