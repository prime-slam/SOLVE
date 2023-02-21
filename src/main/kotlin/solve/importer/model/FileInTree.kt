package solve.importer.model

class FileInTree(
    val file: FileInfo
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileInTree
        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}