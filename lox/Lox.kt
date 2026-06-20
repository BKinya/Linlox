package lox

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.jvm.Throws
import kotlin.system.exitProcess

var hadError = false
var hadRuntimeError = false
val interpreter = Interpreter()

@Throws(IOException::class)
fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: linlox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

@Throws(IOException::class)
private fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))

    if (hadError) exitProcess(65)
    if (hadRuntimeError) exitProcess(70)
}

@Throws(IOException::class)
private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        val line = reader.readLine() ?: break
        run(line)
        hadError = false
    }
}

private fun run(source: String) {
    val scanner = Scanner(source)
    val tokens: List<Token> = scanner.scanTokens()
    val parser = Parser(tokens)
    val expressions = parser.parse()

    if (hadError) return

    interpreter.interpret(expressions)
}

fun error(line: Int, message: String) {
    report(line, "", "Error: $message")
}

private fun report(line: Int, where: String, message: String, allowSilentErrors: Boolean = false) {
    if (!allowSilentErrors) {
        System.err.println("[line $line] Error $where: $message")
    }
    hadError = true
}


fun error(token: Token, message: String, allowSilentErrors: Boolean) {
    if (token.type == TokenType.EOF) {
        report(token.line, "at end", message)
    } else {
        report(token.line, "at '${token.lexeme}'", message, allowSilentErrors)
    }
}

fun runtimeError(error: RuntimeError) {
    System.err.println("${error.message}\n[line ${error.token.line}]")
    hadRuntimeError = true
}