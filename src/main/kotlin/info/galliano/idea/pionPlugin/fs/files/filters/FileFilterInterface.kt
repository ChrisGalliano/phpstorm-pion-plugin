package info.galliano.idea.pionPlugin.fs.files.filters

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

interface FileFilterInterface {
    fun valid(file: VirtualFile, project: Project): Boolean
}
