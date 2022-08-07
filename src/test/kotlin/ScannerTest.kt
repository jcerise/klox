import com.jcerise.klox.Scanner
import com.jcerise.klox.Token
import com.jcerise.klox.TokenType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ScannerTest {
    @Test
    fun testScanTokensError() {
        val kloxLine: String = "^"
        val testScanner: Scanner = Scanner(kloxLine)

        val tokens: MutableList<Token> = testScanner.scanTokens()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.EOF, tokens[0].type)
    }

    @Test
    fun testScanTokensSingleChar() {
        val kloxLine: String = "(-){+},.;*"
        val testScanner: Scanner = Scanner(kloxLine)

        val tokens: MutableList<Token> = testScanner.scanTokens()
        assertEquals(11, tokens.size)

        assertEquals(TokenType.LEFT_PAREN, tokens[0].type)
        assertEquals(TokenType.MINUS, tokens[1].type)
        assertEquals(TokenType.RIGHT_PAREN, tokens[2].type)
        assertEquals(TokenType.LEFT_BRACE, tokens[3].type)
        assertEquals(TokenType.PLUS, tokens[4].type)
        assertEquals(TokenType.RIGHT_BRACE, tokens[5].type)
        assertEquals(TokenType.COMMA, tokens[6].type)
        assertEquals(TokenType.DOT, tokens[7].type)
        assertEquals(TokenType.SEMICOLON, tokens[8].type)
        assertEquals(TokenType.STAR, tokens[9].type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun testScanTokensMultiChar() {
        val testCases = HashMap<TokenType, String>()
        testCases[TokenType.BANG] = "!"
        testCases[TokenType.BANG_EQUAL] = "!="
        testCases[TokenType.EQUAL] = "="
        testCases[TokenType.EQUAL_EQUAL] = "=="
        testCases[TokenType.LESS] = "<"
        testCases[TokenType.LESS_EQUAL] = "<="
        testCases[TokenType.GREATER] = ">"
        testCases[TokenType.GREATER_EQUAL] = ">="


        for (testCase in testCases.entries.iterator()) {
            val scanner = Scanner(testCase.value)
            val tokens: MutableList<Token> = scanner.scanTokens()

            assertEquals(2, tokens.size)
            assertEquals(testCase.key, tokens[0].type)
        }
    }

    @Test
    fun testScanTokensStrings() {
        val kloxLine: String = "\"test\" \"test2\" \"test3\""
        val testScanner: Scanner = Scanner(kloxLine)

        val tokens: MutableList<Token> = testScanner.scanTokens()
        assertEquals(4, tokens.size)
        assertEquals(TokenType.STRING, tokens[0].type)
        assertEquals("test", tokens[0].literal)
        assertEquals(TokenType.STRING, tokens[1].type)
        assertEquals("test2", tokens[1].literal)
        assertEquals(TokenType.STRING, tokens[2].type)
        assertEquals("test3", tokens[2].literal)
    }

    @Test
    fun testScanTokensNumbers() {
        val kloxLine: String = "1234 1.234 12.34"
        val testScanner: Scanner = Scanner(kloxLine)

        val tokens: MutableList<Token> = testScanner.scanTokens()
        assertEquals(4, tokens.size)
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals(1234.0, tokens[0].literal)
        assertEquals(TokenType.NUMBER, tokens[1].type)
        assertEquals(1.234, tokens[1].literal)
        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals(12.34, tokens[2].literal)
    }
}