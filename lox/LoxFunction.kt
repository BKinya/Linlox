package lox

class LoxFunction(private val declaration: Function): LoxCallable{
    override fun arity(): Int = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(interpreter.globals)

        for (i in declaration.params.indices){
            environment.define(declaration.params[i].lexeme, arguments[i])
        }

        interpreter.executeBlock(declaration.body, environment)
        return null
    }

    override fun toString(): String = "<fn ${declaration.name.lexeme}>"

}