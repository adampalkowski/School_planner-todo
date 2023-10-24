package com.palrasp.myapplication.utils

import kotlin.random.Random

fun generateRandomId(): Long {
    val random = Random.Default
    // Define the range for your random ID. Adjust the values as needed.
    val minIdValue = 1L
    val maxIdValue = Long.MAX_VALUE
    return random.nextLong(minIdValue, maxIdValue)
}
