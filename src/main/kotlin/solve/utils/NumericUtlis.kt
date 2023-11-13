package solve.utils

import kotlin.math.ceil

fun Double.ceilToInt() = ceil(this).toInt()

fun Float.ceilToInt() = ceil(this).toInt()

fun sign(value: Int) = kotlin.math.sign(value.toFloat()).toInt()
