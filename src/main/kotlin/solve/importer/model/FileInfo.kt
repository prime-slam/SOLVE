package solve.importer.model

data class FileInfo(val name: String = "", val isLeaf: Boolean = false, val errors: MutableList<String> = mutableListOf()) {
    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileInfo
        if (name != other.name) return false
        if (isLeaf != other.isLeaf) return false
        if (errors != other.errors) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + isLeaf.hashCode()
        result = 31 * result + errors.hashCode()
        return result
    }
}