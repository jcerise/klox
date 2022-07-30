package com.jcerise.klox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Klox {
    fun runFile(path: String) {
        val bytes: ByteArray = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))
    }

    fun runPrompt() {
        val input: InputStreamReader = InputStreamReader(System.`in`)
        val reader: BufferedReader = BufferedReader(input)

        while (true) {
            println("> ")
            val line: String = reader.readLine()
            if (line == null) {
                break
            }

            run(line)

        }
    }

    fun run(source: String) {
        val scanner: Scanner = Scanner(source)
        val tokens: List<Token> = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }
}

fun main(args: Array<String>) {
    val klox: Klox = Klox()

    if (args.size > 1) {
        println("Usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        klox.runFile()
    } else {
        klox.runPrompt()
    }
}