package tools

import java.io.IOException
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

@Throws(IOException::class)
fun main(args: Array<String>) {
    if(args.size != 1){
        System.err.println("Usage: generate_ast <output file>")
        exitProcess(64)
    }
    val outputDir = args[0]

    defineAst(outputDir, "Expr", listOf(
        "Binary: val left: Expr, val operator: Token, val right: Expr",
        "Grouping: val expression: Expr",
        "Unary: val operator: Token, val right: Expr",
        "Literal: val value: Any?",
    ))
}

@Throws(IOException::class)
private fun  defineAst(outputDir: String, baseName: String, types: List<String>,) {
    val path = "$outputDir/$baseName.kt"
    PrintWriter(path, StandardCharsets.UTF_8.name()).use { writer ->
        with(writer) {
            println("package lox")
            println()
            println("import java.util.List")
            println()
            println("sealed interface $baseName")
            println()
        }
        for (type in types) {
            val className = type.substringBefore(':')
            val fields = type.substringAfter(':')
            defineType(writer, baseName, className, fields )
        }
    }
}

private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldsList: String) {
    with(writer) {
        println("data class $className( $fieldsList ) : $baseName")
    }
}