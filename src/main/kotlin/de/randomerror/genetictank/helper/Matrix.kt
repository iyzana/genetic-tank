package de.randomerror.genetictank.helper

import java.io.Serializable
import java.util.*

/**
 * Created by henri on 01.11.16.
 */
class Matrix(val w: Int, val h: Int, init: (x: Int, y: Int) -> Double) : Serializable {
    private val data: Array<Double> = Array(w * h) { i -> init(i % w, i / w) }

    operator fun get(y: Int) = get(0, y)

    operator fun get(x: Int, y: Int) = data[x + y * w]

    operator fun set(y: Int, value: Double) {
        set(0, y, value)
    }

    operator fun set(x: Int, y: Int, value: Double) {
        data[x + y * w] = value
    }

    operator fun plus(other: Matrix): Matrix {
        require(w == other.w && h == other.h) { "$w x $h incompatible with ${other.w} x ${other.h}" }
        
        data.indices.forEach { i -> data[i] += other.data[i] }
        
        return this
//        return Matrix(w, h) { i, j ->
//            data[i, j] + other[i, j]
//        }
    }

    operator fun times(other: Matrix): Matrix {
        require(w == other.h)

        return Matrix(other.w, h) { x, y ->
            (0 until w).sumByDouble { k -> get(k, y) * other[x, k] }
        }
    }

    fun copy() = Matrix(w, h) { x, y -> get(x, y) }
    
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false

        if (w != other.w) return false
        if (h != other.h) return false
        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = w
        result = 31 * result + h
        result = 31 * result + Arrays.hashCode(data)
        return result
    }

}