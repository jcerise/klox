package com.jcerise.klox

class Token(var type: TokenType, var lexeme: String, var literal: Any?, var line: Int) {

    public override fun toString(): String {
        return "$type $lexeme $literal"
    }
}