package lox


class Interpreter {

    private class BreakException : RuntimeException()

    val globals = Environment()
    private var environment = globals

    init {
        globals.define("clock", object : LoxCallable {
            override fun arity(): Int = 0

            override fun call(
                interpreter: Interpreter,
                arguments: List<Any?>
            ): Any {
                return (System.currentTimeMillis() / 1000).toDouble()
            }

            override fun toString(): String = "<native fn>"

        })
    }

    fun interpret(stmts: List<Stmt>) {
        return try {
            stmts.forEach { stmt -> execute(stmt) }
        } catch (error: RuntimeError) {
            runtimeError(error)
        }
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Expression -> executeExprStatement(stmt)
            is Print -> executePrintStatement(stmt)
            is Var -> executerVarStatement(stmt)
            is Block -> executeBlock(stmt.statements, Environment(environment))
            is If -> executeIf(stmt)
            is While -> executeWhile(stmt)
            is InlineResult -> executeInlineResult(stmt)
            is Break -> executeBreakStatement()
            is Function -> executeFunctionStmt(stmt)
            is ReturnS -> executeReturnStmt(stmt)
        }
    }

    private fun executeReturnStmt(stmt: ReturnS) {
       var value: Any? = null
        if (stmt.value != null) value = evaluate(stmt.value)

        throw Return(value)
    }

    private fun executeFunctionStmt(stmt: Function) {
        val function = LoxFunction(stmt, environment)
        environment.define(stmt.name.lexeme, function)
    }

    private fun executeBreakStatement() {
        throw BreakException()
    }

    private fun executeWhile(stmt: While) {

        try {
            while (isTruthy(evaluate(stmt.condition))) {
                execute(stmt.body)
            }
        } catch (ex: BreakException) {
            // Do nothing
            // Catching the exception breaks us out of the while loop completely
        }
    }

    private fun executeIf(stmt: If) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
    }

    private fun executeInlineResult(stmt: InlineResult) {
        val value = evaluate(stmt.expr)
        println(stringify(value))
    }

    fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            statements.forEach(::execute)
        } finally {
            this.environment = previous
        }

    }

    private fun executeExprStatement(stmt: Expression) {
        evaluate(stmt.expression)
    }

    private fun executePrintStatement(stmt: Print) {
        val value = evaluate(stmt.expression)
        println(stringify(value))
    }

    private fun executerVarStatement(stmt: Var) {
        val value: Any = stmt.initializer?.let { evaluate(it) } ?: Uninitialized
        environment.define(stmt.name.lexeme, value)
    }

    private fun evaluate(expr: Expr): Any? = when (expr) {
        is Binary -> evaluateBinary(expr)
        is Grouping -> evaluateGrouping(expr)
        is Literal -> evaluateLiteral(expr)
        is Ternary -> evaluateTernary(expr)
        is Unary -> evaluateUnary(expr)
        is Variable -> evaluateVariable(expr)
        is Assignment -> evaluateAssignment(expr)
        is Logical -> evaluateLogical(expr)
        is Call -> evaluateCall(expr)
    }

    private fun evaluateAssignment(expr: Assignment): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    private fun evaluateLogical(expr: Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    private fun evaluateTernary(expr: Ternary): Any? {
        val condition = evaluate(expr.condition)
        return if (isTruthy(condition)) {
            evaluate(expr.thenBranch)
        } else {
            evaluate(expr.elseBranch)
        }
    }

    private fun evaluateLiteral(expr: Literal): Any? = expr.value

    private fun evaluateGrouping(expr: Grouping): Any? {
        return evaluate(expr.expression)
    }

    private fun evaluateUnary(expr: Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }

            else -> null
        }
    }

    private fun evaluateVariable(expr: Variable): Any? {
        return environment.get(expr.name)
    }

    private fun checkNumberOperand(operator: Token, right: Any?) {
        if (right is Double) return
        throw RuntimeError(operator, "Operand must be a number")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be number")
    }

    private fun checkDivisorIsNonZero(operator: Token, right: Any?) {
        if (right is Double && right != 0.0) return
        throw RuntimeError(operator, "Arithmetic exception / by zero")
    }

    private fun evaluateBinary(expr: Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) > (right as Double)
            }

            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) >= (right as Double)
            }

            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }

            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }

            TokenType.PLUS -> when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                left is String -> left + right.toString()
                right is String -> left.toString() + right
                else -> throw RuntimeError(expr.operator, "Operands must be numbers or two strings")
            }

            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                checkDivisorIsNonZero(expr.operator, right)
                (left as Double) / (right as Double)
            }

            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }

            TokenType.COMMA -> {
                // Evaluate left side to handle any side effects
                evaluate(expr.left)

                // Return the value of the right side
                evaluate(expr.right)
            }

            else -> null
        }
    }

    private fun evaluateCall(expr: Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments = mutableListOf<Any?>()
        expr.arguments.forEach {
            arguments.add(
                evaluate(it)
            )
        }

        if (callee !is LoxCallable) {
            throw RuntimeError(expr.paren, "Can only call functions and classes")
        }

        val function: LoxCallable = callee
        if (arguments.size != callee.arity()) {
            throw RuntimeError(
                expr.paren,
                "Expected ${callee.arity()} arguments but got ${arguments.size}"
            )
        }
        return function.call(this, arguments)

    }

    /**
     * false and null values are falsey
     * Everything else is truthy
     */
    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun isEqual(left: Any?, right: Any?): Boolean {
        if (left == null && right == null) return true
        if (left == null) return false

        return left == right
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"

        if (obj is Double) {
            val text = obj.toString()
            if (text.endsWith(".0")) {
                return text.substring(0, text.length - 2)
            }
            return text
        }
        return obj.toString()
    }
}


