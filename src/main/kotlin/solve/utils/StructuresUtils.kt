package solve.utils

fun <K, V> Map<K, V>.getKeys(value: V) = keys.filter { this[it] == value }
