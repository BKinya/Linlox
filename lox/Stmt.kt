package lox

sealed interface Stmt

data class Expression(val expression: Expr) : Stmt
data class Print(val expression: Expr) : Stmt
data class Var(val name: Token, val initializer: Expr?) : Stmt
data class Block(val statements: List<Stmt>) : Stmt

/**
 * AST node to be used only by REPL
 */
data class InlineResult(val expr: Expr) : Stmt