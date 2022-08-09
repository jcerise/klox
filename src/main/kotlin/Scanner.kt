package com.jcerise.klox

class Scanner(val source: String) {
    private var tokens: MutableList<Token> = mutableListOf()

    private var start: Int = 0
    private var current: Int = 0
    private var line: Int = 1

    private val keywords = hashMapOf<String, TokenType>(
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "or" to TokenType.OR,
        "and" to TokenType.AND,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "class" to TokenType.CLASS,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "fun" to TokenType.FUN,
        "return" to TokenType.RETURN,
        "var" to TokenType.VAR,
        "print" to TokenType.PRINT,
        "for" to TokenType.FOR,
        "while" to TokenType.WHILE,
        "nil" to TokenType.NIL,
    )

    fun scanTokens(): MutableList<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' ->
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            ' ' -> {}
            '\r' -> {}
            '\t' -> {}
            '\n' -> line++
            '"' -> string()
            else ->
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    Klox().error(line, "unexpected character.")
                }
        }
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text: String = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            // Unterminated string case
            Klox().error(line, "unterminated string.")
        }

        advance()
        val value: String = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') ||
                (c in 'A'..'Z') ||
                (c == '_')
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun number() {
        // Consume the integral part of the number
        while( isDigit(peek())) {
            advance()
        }

        // Look for a fractional part/mantissa
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the '.'
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text: String = source.substring(start, current)
        val type: TokenType = try { keywords[text]!! } catch (e: Exception) { TokenType.IDENTIFIER }

        addToken(type)
    }
}