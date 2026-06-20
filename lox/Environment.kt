package lox

class Environment(val enclosing: Environment? = null) {

    private val values = mutableMapOf<String, Any?>()

    fun get(name: Token): Any?{
        if (values.containsKey(name.lexeme)){
            val value = values[name.lexeme]
            if (value is Uninitialized){
                throw RuntimeError(name, "Uninitialized variable '${name.lexeme}'.")
            }
            return value
        }

        if (enclosing != null) return enclosing.get(name)

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    fun assign(name: Token, value: Any?){
        if (values.containsKey(name.lexeme)){
            values[name.lexeme] = value
            return
        }

        if (enclosing != null){
            enclosing.assign(name, value)
            return
        }

        throw RuntimeError(name,"Undefined variable '${name.lexeme}'.")
    }

    fun define(name: String, value: Any?){
        values[name] = value
    }
}

// Marker for the uninitialized variable
object Uninitialized