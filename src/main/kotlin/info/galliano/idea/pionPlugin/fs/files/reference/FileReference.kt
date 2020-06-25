package info.galliano.idea.pionPlugin.fs.files.reference

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.*
import com.intellij.util.IncorrectOperationException
import info.galliano.idea.pionPlugin.fs.files.FileInterface
import info.galliano.idea.pionPlugin.fs.files.list.FilesListInterface
import info.galliano.idea.pionPlugin.fs.files.lookup.FileLookupElement

open class FileReference(
    element: PsiElement,
    private val file: FileInterface,
    private val fileList: FilesListInterface
) : PsiReferenceBase<PsiElement>(element), PsiReference {

    override fun resolve(): PsiElement? {
        var result: PsiElement? = null
        val file = VfsUtilCore.findRelativeFile(file.path(), element.project.baseDir)
        if (file != null) {
            result = PsiManager.getInstance(element.project).findFile(file)
        }
        return result
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val newPath = (
                this.file.path().substring(0, this.file.path().lastIndexOf("/"))
                        + "/"
                        + newElementName
                )
        return super.handleElementRename(
            file.withPath(newPath).name()
        )
    }

    @Throws(IncorrectOperationException::class)
    override fun bindToElement(element: PsiElement): PsiElement? {
        if (element is PsiFile) {
            val virtualFile = element.virtualFile
            if (virtualFile != null) {
                val path = VfsUtil.getRelativeLocation(virtualFile, element.getProject().baseDir)
                if (path is String) {
                    val file = this.file.withPath(path)
                    if (file.name() == this.file.name() && file.path() == this.file.path()) {
                        return getElement()
                    }
                }
            }
        }
        return null
    }

    override fun getVariants(): Array<Any> {
        return fileList.items().map {
            FileLookupElement(
                file.withPath(it)
            )
        }.toTypedArray()
    }
}