package info.galliano.idea.pionPlugin.templating.templates.ids.constraints

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

class TemplateId(val id: String) {
    constructor(virtualFile: VirtualFile, project: Project) : this(
        VfsUtil.getRelativeLocation(
            virtualFile,
            project.baseDir
        )!!
    )

    constructor(psiFile: PsiFile, project: Project) : this(psiFile.virtualFile, project)

    constructor(psiFile: PsiFile) : this(psiFile.virtualFile, psiFile.project)
}