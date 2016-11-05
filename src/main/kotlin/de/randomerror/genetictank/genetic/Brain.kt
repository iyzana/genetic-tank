package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.helper.Matrix

/**
 * Created by henri on 24.10.16.
 */

class Brain(val layerCounts: List<Int>) {

    private data class Layer(val bias: Matrix, val weights: Matrix)

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

    private fun sigmoid(vector: Matrix): Matrix {
        require(vector.x == 1)

        return Matrix(1, vector.y) { _i, j ->
            1.0 / (1.0 + Math.exp(-vector[j]))
        }
    }

    fun copy(): Brain {
        val brain = Brain(layerCounts)
        brain.theNetwork = theNetwork.map { layer ->
            Layer(layer.bias.copy(), layer.weights.copy())
        }
        return brain
    }
}