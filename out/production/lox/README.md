# Linlox 🚧
Lox interpreter implementation written in Kotlin

## Syntax BNF

```ebnf

program       -> declarations* EOF ;

Declarations

declarations  -> varDecl | statement ;
varDecl       -> "var" IDENTIFIER ( "=" expression )? ";" ;

Statements 

statement     -> exprStmt | printStmt | block ;
block         -> "{" declarations* "}" ;
exprStmt      -> expression ";" ;
printStmt     -> "print" expression ";" ;

Expressions

expression  -> assignment
assignment  -> IDENTIFIER "=" assignment | comma
comma       -> condition ( "," condition )*
condition   -> equality ( "?" expression ":" condition )?
equality    -> comparison ( ( "!=" | "==" ) comparison )*
comparison  -> term ( ( "<" | "<=" | ">" | ">=") term )* ;
term        -> factor ( ( "-" | "+" ) factor )* ;
factor      -> unary ( ( "/" | "*" ) unary )* ;
unary       -> ( "!" | "-" ) unary | primary
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER 
```

## What's new
This implementation includes several enhancements beyond standard Lox specification

- [Comma operator](https://en.wikipedia.org/wiki/Comma_operator): Evaluates expressions sequentially frrom left to right,
discards the early results and returns the final value
```kotlin
var x =  (1, 3, 4); // x  is 4
```

- Ternary operator
```kotlin
var result = isTrue ? "yes" : "no";
```

- Supports C-style block comments 
```kotlin
/* This is a 
   multiline block comment
 */
```
