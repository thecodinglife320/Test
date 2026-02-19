package com.example.lib.exception

import java.util.Scanner;

fun demo(input: String) {
    val number = input.toInt() // an exception is possible here!
    println(number + 1)
}

fun main() {
    try {
        demo("6")
    } catch (e: Exception) {
        e.stackTrace.forEach {
            Thread.currentThread().stackTrace[0]
            println(it.methodName)
        }
    }
}