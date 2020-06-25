package info.galliano.idea.pionPlugin.templating.files

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import info.galliano.idea.pionPlugin.fs.files.FileInterface
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId

class TemplateFile(private val id: String) : FileInterface {
    override fun withFile(file: VirtualFile, project: Project): FileInterface {
        return TemplateFile(TemplateId(file, project).id)
    }

    override fun withName(name: String): FileInterface {
        return TemplateFile(name)
    }

    override fun withPath(path: String): FileInterface {
        return TemplateFile(path)
    }

    override fun path(): String {
        return this.id
    }

    override fun name(): String {
        return id
    }
}