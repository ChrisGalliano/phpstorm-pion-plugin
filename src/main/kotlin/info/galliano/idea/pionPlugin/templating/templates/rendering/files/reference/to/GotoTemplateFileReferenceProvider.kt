package info.galliano.idea.pionPlugin.templating.templates.rendering.files.reference.to

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import info.galliano.idea.pionPlugin.declarations.string.trimQuote
import info.galliano.idea.pionPlugin.fs.files.list.FilteredFilesList
import info.galliano.idea.pionPlugin.fs.files.reference.FileReference
import info.galliano.idea.pionPlugin.psi.method.defenition.MethodCallDefinition
import info.galliano.idea.pionPlugin.templating.files.TemplateFile
import info.galliano.idea.pionPlugin.templating.files.filters.IsCorrectTemplateIdFileFilter
import info.galliano.idea.pionPlugin.templating.rendering.methods.PionRenderingMethods

class GotoTemplateFileReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(psiElement: PsiElement, p1: ProcessingContext): Array<PsiReference> {
        var result = PsiReference.EMPTY_ARRAY
        val methodCall = MethodCallDefinition.createFromArgument(psiElement)
        if (methodCall != null && methodCall.isCallTo(PionRenderingMethods.methods, psiElement.project)) {
            val targetArgument = methodCall.getTargetArgument()
            if (targetArgument is StringLiteralExpression) {
                result = arrayOf<PsiReference>(
                    FileReference(
                        targetArgument,
                        TemplateFile("").withName(targetArgument.text.trimQuote()),
                        FilteredFilesList(
                            IsCorrectTemplateIdFileFilter(),
                            psiElement.project
                        )
                    )
                )
            }
        }
        return result
    }
}