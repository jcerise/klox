package com.jcerise.klox

abstract class Token(type: TokenType, lexeme: String, literal: Any, line: Int) {
    abstract val type: TokenType
    abstract val lexeme: String
    abstract val literal: Any
    abstract val line: Int

    public override fun toString(): String {
        return "$type $lexeme $literal"
    }
}