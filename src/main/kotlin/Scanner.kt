package com.jcerise.klox

/**
 * Scans lines of klox code and generates usable tokens based on language keywords and character types
 *
 * @param source the source to tokenize. Can be a single line, or multiple lines separated by newlines
 * @property tokens the list of tokens created by scanning the source
 * @property start the starting position for scanning the source
 * @property current the current position of the scanner in the source
 * @property line the current line in the source (if source contains multiple lines)
 * @property keywords the list of reserved keywords klox respects
 */
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

    /**
     * Scans the source and determines usable tokens
     * @return a MutableList of Tokens found while scanning
     */
    fun scanTokens(): MutableList<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    /**
     * Scans the source at the current position for characters or keywords that can be tokenized. Adds a Token if found,
     * or, ignores characters that cannot be tokenized (comments, etc)
     */
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
                    // Handle a line comment, terminated with a newline
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else if (match('*')) {
                    // Handle block comments - /* ... */ with any number of newlines included in the block
                    commentBlock()
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

    /**
     * Advance the current pointer, consuming the character in the process. If nothing is done with the return character
     * the character will be ignored
     * @return the consumed character
     */
    private fun advance(): Char {
        return source[current++]
    }

    /**
     * Look ahead at the next character in the source, one position beyond current
     * @return the next character beyond current
     */
    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    /**
     * Look ahead two characters in the source, two positions beyond current
     * @return the character two positions beyond current
     */
    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    /**
     * Create a Token with a null literal value
     * @param type the type of the Token to create
     */
    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    /**
     * Create a Token with the specified type and literal value
     * @param type the type of the Token to create
     * @param literal the literal value for the Token, can be null
     */
    private fun addToken(type: TokenType, literal: Any?) {
        val text: String = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    /**
     * Check if the position within the source is at the end of the source or not
     * @return true if current is at end of source, false otherwise
     */
    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    /**
     * Check if the current character in source matches an expected character
     * @param expected the char to match current against
     * @return true if expected matches the character at current, false otherwise
     */
    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    /**
     * Handle a string within source, and create a token for the entire string. A string is delimted by double quotes
     * at the beginning, and double quotes at the end. The Token will be the contents between these delimeters
     */
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

    /**
     * Checks if the current character in source is a digit or not
     * @param c the current character in source
     * @return true if the current character is a digit, false otherwise
     */
    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    /**
     * Checks if the current character in source is an alpha character or not
     * @param c the current character in source
     * @return true if the current character is an alpha character, false otherwise
     */
    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') ||
                (c in 'A'..'Z') ||
                (c == '_')
    }

    /**
     * Checks if the current character in source is an alphanumeric character or not
     * @param c the current character in source
     * @return true if the current character is an alphanumeric character, false otherwise
     */
    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    /**
     * Handle a number character (integral or floating point) within source. Will create a Number Token regardless or
     * fractional component. Literal for Number is a Kotlin Double
     * TODO: Create separate floating point representation
     */
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

    /**
     * Handle found identifiers in source. An identifier is a keyword from the keywords map
     */
    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text: String = source.substring(start, current)
        val type: TokenType = try { keywords[text]!! } catch (e: Exception) { TokenType.IDENTIFIER }

        addToken(type)
    }

    /**
     * Handle a block comment in source. Block comments are delimted by \/* and \*/. Everything inside a block comment,
     * including the delimeters is ignored.
     */
    private fun commentBlock() {
        while (!(peek() == '*' && peekNext() == '/')) {
            if (peek() == '\n') line++
            advance()
        }

        advance()
        advance()
    }
}