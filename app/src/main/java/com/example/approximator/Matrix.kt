package com.example.approximator

internal class Matrix : Cloneable, java.io.Serializable {

    private var array: Array<DoubleArray>? = null

    var rowDimension: Int = 0
        private set
    var columnDimension: Int = 0
        private set

    val arrayCopy: Array<DoubleArray>
        get() {
            val C = Array(rowDimension) { DoubleArray(columnDimension) }
            for (i in 0 until rowDimension) {
                if (columnDimension >= 0) System.arraycopy(array!![i], 0, C[i], 0, columnDimension)
            }
            return C
        }

    constructor(A: Array<DoubleArray>) {
        rowDimension = A.size
        columnDimension = A[0].size
        for (i in 0 until rowDimension) {
            if (A[i].size != columnDimension) {
                throw IllegalArgumentException("All rows must have the same length.")
            }
        }
        this.array = A
    }

    constructor(A: Array<DoubleArray>, m: Int, n: Int) {
        this.array = A
        this.rowDimension = m
        this.columnDimension = n
    }


    private constructor(m: Int, n: Int) {
        this.rowDimension = m
        this.columnDimension = n
        array = Array(m) { DoubleArray(n) }
    }

    constructor(vals: DoubleArray, m: Int) {
        this.rowDimension = m
        columnDimension = if (m != 0) vals.size / m else 0
        if (m * columnDimension != vals.size) {
            throw IllegalArgumentException("Array length must be a multiple of m.")
        }
        array = Array(m) { DoubleArray(columnDimension) }
        for (i in 0 until m) {
            for (j in 0 until columnDimension) {
                array!![i][j] = vals[i + j * m]
            }
        }
    }

    operator fun get(i: Int): Double {
        return array!![i][0]
    }

    fun getMatrix(i1: Int, j1: Int): Matrix {
        val X = Matrix(i1 + 1, j1 + 1)
        val B = X.array
        try {
            for (i in 0..i1) {
                if (j1 + 1 >= 0) System.arraycopy(array!![i], 0, B!![i], 0, j1 + 1)
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw ArrayIndexOutOfBoundsException("Submatrix indices")
        }

        return X
    }

}
