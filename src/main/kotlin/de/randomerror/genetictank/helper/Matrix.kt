package de.randomerror.genetictank.helper

import java.util.*

/**
 * Created by henri on 01.11.16.
 */
class Matrix(val x: Int, val y: Int, init: (i: Int, j: Int) -> Double) {
    val data: Array<Array<Double>> = Array(x) { i -> Array(y) { j -> init(i, j) } }
    
    operator fun times(other: Matrix): Matrix {
        require(x == other.y)

        return Matrix(other.x, y) { i, j ->
            (0 until x).sumByDouble { data[it, j] * other.data[i, it] }
        }
    }

    operator fun plus(other: Matrix): Matrix {
        require(x == other.x && y == other.y)

        return Matrix(x, y) { i, j ->
            data[i, j] + other.data[i, j]
        }
    }

    companion object {
        fun random(x: Int, y: Int, range: ClosedRange<Double> = -1.0..1.0): Matrix {
            return Matrix(x, y) { i, j ->
                Math.random() * (range.endInclusive - range.start) + range.start
            }
        }
    }

    override fun toString(): String {
        return "transpose " + Arrays.deepToString(data).replace('[', '(').replace(']', ')')
    }

}