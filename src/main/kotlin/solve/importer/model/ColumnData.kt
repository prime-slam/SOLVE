package solve.importer.model

data class ColumnData(val name: String = "", val isLeaf: Boolean = false, val error: MutableList<String> = mutableListOf())