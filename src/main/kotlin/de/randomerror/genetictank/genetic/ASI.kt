package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.helper.Matrix
import java.util.*

/**
 * Created by henri on 24.10.16.
 */

class ASI(val layers: List<Int>) {

    val theNetwork: List<Pair<Matrix, Matrix>>

    init {
        theNetwork = (0 until layers.size-1).map { i->
            Matrix.random(1, layers[i]) to Matrix.random(layers[i], layers[i+1])
        }
    }

    fun calc(input: Matrix): Matrix {
        return theNetwork.fold(input) { result, pair ->
            val (bias, weight) = pair
            return sigmoid(weight * result + bias)
        }
    }

    private fun sigmoid(vector: Matrix): Matrix {
        require(vector.x == 1)

        val result = Matrix(1, vector.y)

        vector.data[0].forEachIndexed { i, d ->
            result.data[0][i] = 1.0/(1.0+Math.exp(-d))
        }

        return result
    }
}