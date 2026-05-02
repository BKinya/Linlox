package linlox

import com.sun.tools.example.debug.expr.Token.newToken
import lox.Lox
import javax.security.auth.callback.CallbackHandler

class Scanner(private val source: String) {
    private val tokens: MutableList<Token> = mutableListOf()

    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the begining of the next lexeme
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c: Char = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ':' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            else -> {
                error(line, "Unexpected character")
            }
        }
    }

    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char = source[current++]

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }
}