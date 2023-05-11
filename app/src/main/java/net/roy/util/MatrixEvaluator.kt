package net.roy.util

import android.animation.FloatArrayEvaluator
import android.animation.TypeEvaluator
import android.graphics.Matrix

/**
 * This class is used to interpolate between two matrices and animate the transition.
 **/
class MatrixEvaluator(reuseMatrix: Matrix) : TypeEvaluator<Matrix> {
    private val startData = FloatArray(9)
    private val endData = FloatArray(9)
    private val workMatrix = reuseMatrix
    private val floatArrayEvaluator = FloatArrayEvaluator(FloatArray(9))

    override fun evaluate(
        fraction: Float,
        startValue: Matrix,
        endValue: Matrix
    ): Matrix {
        startValue.getValues(startData)
        endValue.getValues(endData)

        workMatrix.setValues(
            floatArrayEvaluator.evaluate(fraction, startData, endData),
        )

        return workMatrix
    }
}
