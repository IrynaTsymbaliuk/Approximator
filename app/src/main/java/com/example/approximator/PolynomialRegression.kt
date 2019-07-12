package com.example.approximator

import kotlin.math.abs

class PolynomialRegression(x: DoubleArray, y: DoubleArray, degree: Int) {

    private var degree: Int = 0
    private val beta: Matrix

    init {
        this.degree = degree
        val n = x.size
        var qr: QRDecomposition
        var matrixX: Matrix

        while (true) {
            val vandermonde = Array(n) { DoubleArray(this.degree + 1) }
            for (i in 0 until n) {
                for (j in 0..this.degree) {
                    vandermonde[i][j] = Math.pow(x[i], j.toDouble())
                }
            }
            matrixX = Matrix(vandermonde)

            qr = QRDecomposition(matrixX)
            if (qr.isFullRank) break

            this.degree--
        }

        val matrixY = Matrix(y, n)

        beta = qr.solve(matrixY)

    }

    private fun beta(j: Int): Double {
        return if (abs(beta[j]) < 1E-4) 0.0 else beta[j]
    }

    fun predict(x: Double): Double {

        var y = 0.0
        for (j in degree downTo 0)
            y = beta(j) + x * y
        return y
    }

}
