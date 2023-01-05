package solve.importer.model

data class FileInfo(val name: String = "", val isLeaf: Boolean = false, val error: MutableList<String> = mutableListOf())