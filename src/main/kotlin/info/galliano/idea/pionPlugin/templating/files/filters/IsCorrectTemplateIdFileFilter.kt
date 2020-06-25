package info.galliano.idea.pionPlugin.templating.files.filters

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import info.galliano.idea.pionPlugin.fs.files.filters.FileFilterInterface
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint

class IsCorrectTemplateIdFileFilter : FileFilterInterface {
    override fun valid(file: VirtualFile, project: Project): Boolean {
        return IsCorrectTemplateIdConstraint().valid(file.name)
    }
}