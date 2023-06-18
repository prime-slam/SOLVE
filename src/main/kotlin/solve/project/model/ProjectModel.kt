package solve.project.model

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper

class ProjectModel {
    private val _projectProperty = ReadOnlyObjectWrapper<Project>()
    val projectProperty: ReadOnlyObjectProperty<Project> = _projectProperty.readOnlyProperty
    val project: Project
        get() = projectProperty.value

    fun changeProject(newProject: Project) {
        _projectProperty.value = newProject
    }
}
