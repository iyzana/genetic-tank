package de.randomerror.genetictank.helper

import java.util.*

/**
 * Created by henri on 01.11.16.
 */
data class Matrix(val x: Int, val y: Int, val data: Array<Array<Double>> = Array(x) { Array(y) {0.0} }) {


    operator fun times(other: Matrix): Matrix {
        require(x == other.y)

        val m = Matrix(other.x, y)

        (0 until other.x).forEach { i ->
            (0 until y).forEach { j ->
                m.data[i][j] = (0 until x).sumByDouble { data[it][j]*other.data[i][it] }
            }
        }

        return m
    }

    operator fun plus(other: Matrix): Matrix {
        require(x == other.x && y == other.y)

        val m = Matrix(x, y)
        data.forEachIndexed { i, columns ->
            columns.forEachIndexed { j, d ->
                m.data[i][j] = d+other.data[i][j]
            }
        }
        return m
    }

    companion object {
        fun random(x: Int, y: Int, range: ClosedRange<Double> = -1.0..1.0): Matrix {
            val m = Matrix(x, y)
            (0 until x).forEach { i ->
                (0 until y).forEach { j ->
                    m.data[i][j] = Math.random()*(range.endInclusive-range.start)+range.start
                }
            }
            return m
        }
    }

    override fun toString(): String {
        return "transpose " + Arrays.deepToString(data).replace('[', '(').replace(']', ')')
    }

}