package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.helper.Matrix
import java.io.Serializable
import java.util.*

/**
 * Created by henri on 24.10.16.
 */

class Brain(val layerCounts: List<Int>) : Serializable {

    private data class Layer(val bias: Matrix, val weights: Matrix) : Serializable

    private var theNetwork = (0 until layerCounts.size - 1).map { i ->
        Layer(Matrix.random(1, layerCounts[i + 1]), Matrix.random(layerCounts[i], layerCounts[i + 1]))
    }

    val allAxons: List<Matrix>
        get() = theNetwork.flatMap { listOf(it.bias, it.weights) }

    fun thinkAbout(idea: Matrix): Matrix {
        return theNetwork.fold(idea) { result, layer ->
            val (bias, weight) = layer
            return sigmoid(weight * result + bias)
        }
    }

    fun getThinkData(idea: Matrix): List<Matrix> {
        val data = mutableListOf<Matrix>(idea)
        theNetwork.fold(idea) { result, layer ->
            val (bias, weight) = layer
            val layerResult = sigmoid(weight * result + bias)
            data.add(layerResult)
            return@fold layerResult
        }
        return data
    }

    private fun sigmoid(vector: Matrix): Matrix {
        require(vector.w == 1)

        (0 until vector.h).forEach { y -> vector[y] = 1.0 / (1.0 + Math.exp(-vector[y])) }

        return vector
    }

    fun copy(): Brain {
        val brain = Brain(layerCounts)
        brain.theNetwork = theNetwork.map { layer ->
            Layer(layer.bias.copy(), layer.weights.copy())
        }
        return brain
    }

    override fun toString(): String {
        return """
            |brain ${hashCode()}
            |layers ${Arrays.toString(layerCounts.toIntArray())}
            |${theNetwork.joinToString(separator = "\n|") { it.bias.toString() + "\n|" + it.weights.toString() }}
            """.trimMargin()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Brain) return false

        if (theNetwork != other.theNetwork) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(theNetwork.toTypedArray()).hashCode()
    }
}