# Linlox 🚧
Lox interpreter implementation written in Kotlin

## Syntax BNF

```ebnf

program       -> declarations* EOF ;

Declarations

declarations  -> funDecl | varDecl | statement ;
funDecl      -> "fun" fucntion ;
function      -> IDENTIFIER "(" parameters? ")" block ;
parameters    -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl       -> "var" IDENTIFIER ( "=" expression )? ";" ;

Statements 

statement     -> exprStmt | forStmt | ifStmt | printStmt | whileStmt | block | break ;
forStmt       -> "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement ;
whileStmt     -> "while" "(" expression ")" statement ; 
ifStmt        -> "if" "(" expression ")" statement ( "else" statement )? ;
block         -> "{" declarations* "}" ;
exprStmt      -> expression ";" ;
printStmt     -> "print" expression ";" ;
break         -> "break" ";" ;

Expressions

expression  -> comma ;
comma        -> assignment ( "," assignment )* ;
assignment  -> logic_or ( "=" assignment )? ;
logic_or    -> logic_and ( "or" logic_and )* ;
logic_and   -> condition ( "and" condition )* ;
condition   -> equality ( "?" expression ":" condition )? ;
equality    -> comparison ( ( "!=" | "==" ) comparison )* ;
comparison  -> term ( ( "<" | "<=" | ">" | ">=") term )* ;
term        -> factor ( ( "-" | "+" ) factor )* ;
factor      -> unary ( ( "/" | "*" ) unary )* ;
unary       -> ( "!" | "-" ) unary | call ;
call        -> primary ( "(" arguments? ")" )*  ;
arguments   -> expression ( "," expression )* ;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```

## What's new
This implementation includes several enhancements beyond standard Lox specification

- [Comma operator](https://en.wikipedia.org/wiki/Comma_operator): Evaluates expressions sequentially frrom left to right,
discards the early results and returns the final value
```kotlin
var x =  (1, 3, 4); // x is 4
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
