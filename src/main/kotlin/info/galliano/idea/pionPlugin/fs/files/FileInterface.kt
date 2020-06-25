package info.galliano.idea.pionPlugin.fs.files

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import info.galliano.idea.pionPlugin.fs.path.PathInterface

interface FileInterface : PathInterface,
    PresentableNameInterface {
    fun withPath(path: String): FileInterface
    fun withName(name: String): FileInterface
    fun withFile(file: VirtualFile, project: Project): FileInterface
}