package info.galliano.idea.pionPlugin.fs.files.list

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import info.galliano.idea.pionPlugin.fs.files.filters.FileFilterInterface

class FilteredFilesList(private val filter: FileFilterInterface, private val project: Project) : FilesListInterface {
    override fun items(): List<String> {
        val result = mutableListOf<String>()
        val virtualFileVisitor = object : VirtualFileVisitor<VirtualFile>() {
            override fun visitFile(virtualFile: VirtualFile): Boolean {
                val path = VfsUtil.getRelativeLocation(virtualFile, project.baseDir)
                if (path != null && filter.valid(virtualFile, project)) {
                    result.add(path)
                }
                return super.visitFile(virtualFile)
            }
        }
        val searchDir = VfsUtilCore.findRelativeFile("", project.baseDir)
        if (searchDir != null) {
            VfsUtil.visitChildrenRecursively(searchDir, virtualFileVisitor)
        }
        return result
    }
}