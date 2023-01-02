package solve.importer.model

class FileInTree(
    val name: ColumnData,
    val isLeaf: Boolean = false,
    val error: ColumnData = ColumnData()
)
