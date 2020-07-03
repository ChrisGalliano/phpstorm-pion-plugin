package info.galliano.idea.pionPlugin.templating.templates.ids.constraints

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.ConcatenationExpressionImpl
import info.galliano.idea.pionPlugin.declarations.string.trimQuote

class TemplateId {
    val id: String

    constructor(id: String) {
        this.id = id
    }

    constructor(virtualFile: VirtualFile, project: Project) : this(
        VfsUtil.getRelativeLocation(
            virtualFile,
            project.baseDir
        )!!
    )

    constructor(psiFile: PsiFile) : this(psiFile.virtualFile, psiFile.project)

    constructor(
        templatePathElement: ConcatenationExpressionImpl,
        virtualFile: VirtualFile
    ) {
        val rightOperand = templatePathElement.rightOperand
        var id = ""
        if (rightOperand is StringLiteralExpression) {
            id = TemplateId(virtualFile, templatePathElement.containingFile.project).id
                .replace(virtualFile.name, "")
                .trimEnd('/')
                .plus(rightOperand.text.trimQuote())
        }
        this.id = id
    }

    constructor(templatePathElement: ConcatenationExpressionImpl) : this(
        templatePathElement,
        templatePathElement.containingFile.virtualFile
    )
}