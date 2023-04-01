package solve.utils

import org.controlsfx.control.RangeSlider
import kotlin.math.abs

val RangeSlider.valuesDifference: Double
    get() = abs(highValue - lowValue)
