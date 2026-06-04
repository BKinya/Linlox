package lox

class Parser(private val tokens: List<Token>) {

    private class ParseError : RuntimeException()

    private var current = 0

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(declaration())
        }
        return statements
    }

    private fun declaration(): Stmt {
        try {
            if (match(TokenType.VAR)) return varDeclaration()
            return statement()
        } catch (error: ParseError) {
            synchronize()
            return Expression(Literal(null))
        }
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect a variable name")

        var initializer: Expr? = null

        if (match(TokenType.EQUAL)){
            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration")
        return Var(name, initializer)

    }

    private fun statement(): Stmt{
        if( match(TokenType.PRINT)) return printStatement()

        return expressionStatement()
    }

    private fun printStatement(): Stmt{
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value")
        return Print(value)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value")
        return Expression(expr)
    }

    private fun expression(): Expr {
        /**
         * Detect the "missing left operand" error first
         */
        if (match(
                TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL,
                TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS, TokenType.LESS_EQUAL,
                TokenType.PLUS, TokenType.STAR, TokenType.SLASH
            )
        ) {
            val operator = previous()
            error(operator, "Expected left operand for binary operator")

            // parse and discard the right operand
            unary()

            // return dummy to keep the parser synchronized
            return Literal(null)

        }

        return comma()
    }

    private fun comma(): Expr {
        var expr = condition()
        while (match(TokenType.COMMA)) {
            val operator = previous()
            val right = condition()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun condition(): Expr {
        var expr = equality()
        while (match(TokenType.QUESTION)) {
            val thenBranch = expression()
            consume(TokenType.COLON, "Expected ':' after the condition")
            val elseBranch = condition()
            expr = Ternary(expr, thenBranch, elseBranch)
        }
        return expr
    }


    private fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) return Literal(false)
        if (match(TokenType.TRUE)) return Literal(true)
        if (match(TokenType.NIL)) return Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING)) return Literal(previous().literal)
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Grouping(expr)
        }

        if (match(TokenType.IDENTIFIER)){
            return Variable(previous())
        }

        throw error(peek(), "Expect expression.")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw error(peek(), message)

    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
    private fun peek(): Token = tokens[current]
    private fun previous(): Token = tokens[current - 1]

    private fun error(token: Token, message: String): ParseError {
        lox.error(token, message)

        return ParseError()
    }

    fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.CLASS,
                TokenType.FUN,
                TokenType.VAR,
                TokenType.FOR,
                TokenType.IF,
                TokenType.WHILE,
                TokenType.PRINT,
                TokenType.RETURN,
                    -> return

                else -> { /* Continue to advance*/ }
            }

            advance()
        }
    }

}