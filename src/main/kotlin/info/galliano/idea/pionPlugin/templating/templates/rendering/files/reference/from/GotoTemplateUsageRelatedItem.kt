package info.galliano.idea.pionPlugin.templating.templates.rendering.files.reference.from

import com.intellij.navigation.GotoRelatedItem
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class GotoTemplateUsageRelatedItem(element: PsiElement, private val psiFile: PsiFile) : GotoRelatedItem(element) {
    override fun getCustomName(): String? {
        var name = super.getCustomName()
        val document = PsiDocumentManager.getInstance(psiFile.project).getDocument(psiFile)
        if (document != null) {
            name = psiFile.name
                .plus(":")
                .plus(document.getLineNumber(element!!.textRange.startOffset) + 1)
        }
        return name
    }
}