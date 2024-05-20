package com.example.pricecompare.utils

object LevenshteinDistance {
    fun compute(str1: String, str2: String): Int {
        val lenStr1 = str1.length
        val lenStr2 = str2.length
        val dp = Array(lenStr1 + 1) { IntArray(lenStr2 + 1) }

        for (i in 0..lenStr1) {
            for (j in 0..lenStr2) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1,  // Удаление
                        dp[i][j - 1] + 1,  // Вставка
                        dp[i - 1][j - 1] + if (str1[i - 1] == str2[j - 1]) 0 else 1  // Замена
                    )
                }
            }
        }
        return dp[lenStr1][lenStr2]
    }
}