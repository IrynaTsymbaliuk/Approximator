package com.example.approximator

import kotlin.math.abs
import kotlin.math.sqrt

internal class QRDecomposition(A: Matrix) : java.io.Serializable {

    private val QR: Array<DoubleArray> = A.arrayCopy

    private val m: Int = A.rowDimension
    private val n: Int = A.columnDimension

    private val Rdiag: DoubleArray

    val isFullRank: Boolean
        get() {
            for (j in 0 until n) {
                if (Rdiag[j] == 0.0)
                    return false
            }
            return true
        }

    init {
        Rdiag = DoubleArray(n)

        for (k in 0 until n) {
            var nrm = 0.0
            for (i in k until m) {
                nrm = hypot(nrm, QR[i][k])
            }

            if (nrm != 0.0) {
                if (QR[k][k] < 0) {
                    nrm = -nrm
                }
                for (i in k until m) {
                    QR[i][k] /= nrm
                }
                QR[k][k] += 1.0

                for (j in k + 1 until n) {
                    var s = 0.0
                    for (i in k until m) {
                        s += QR[i][k] * QR[i][j]
                    }
                    s = -s / QR[k][k]
                    for (i in k until m) {
                        QR[i][j] += s * QR[i][k]
                    }
                }
            }
            Rdiag[k] = -nrm
        }
    }

    fun solve(B: Matrix): Matrix {
        if (B.rowDimension != m) {
            throw IllegalArgumentException("Matrix row dimensions must agree.")
        }
        if (!this.isFullRank) {
            throw RuntimeException("Matrix is rank deficient.")
        }

        val nx = B.columnDimension
        val X = B.arrayCopy

        for (k in 0 until n) {
            for (j in 0 until nx) {
                var s = 0.0
                for (i in k until m) {
                    s += QR[i][k] * X[i][j]
                }
                s = -s / QR[k][k]
                for (i in k until m) {
                    X[i][j] += s * QR[i][k]
                }
            }
        }

        for (k in n - 1 downTo 0) {
            for (j in 0 until nx) {
                X[k][j] /= Rdiag[k]
            }
            for (i in 0 until k) {
                for (j in 0 until nx) {
                    X[i][j] -= X[k][j] * QR[i][k]
                }
            }
        }
        return Matrix(X, n, nx).getMatrix(n - 1, nx - 1)
    }

    private fun hypot(a: Double, b: Double): Double {
        var r: Double
        if (abs(a) > abs(b)) {
            r = b / a
            r = abs(a) * sqrt(1 + r * r)
        } else if (b != 0.0) {
            r = a / b
            r = abs(b) * sqrt(1 + r * r)
        } else {
            r = 0.0
        }
        return r
    }

}