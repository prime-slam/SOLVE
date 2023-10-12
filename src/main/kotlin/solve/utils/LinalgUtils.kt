package solve.utils

import org.joml.Matrix4f

operator fun Matrix4f.times(otherMatrix: Matrix4f): Matrix4f = this.mul(otherMatrix)
