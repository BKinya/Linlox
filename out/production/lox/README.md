# linlox 🚧
lox interpreter implementation written Kotlin

## Syntax BNF

```kotlin
Expressions
expression  -> comma
comma       -> condition ("," condition) *
condition   -> equality (? expression : condition) ?
equality    -> comparison ( ( "!=" | "==" ) comparison )*
comparison  -> term ( ( "<" | "<=" | ">" | ">=") term )* ;
term        -> factor ( ( "-" | "+" ) factor )* ;
factor      -> unary ( ( "/" | "*" ) unary )* ;
unary       -> ( "!" | "-" ) unary | primary
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
```

## What's new
- Supports C-style block comments /* ... */
- 



