package info.galliano.idea.pionPlugin.templating.templates.rendering.files.reference.from

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.ConstantFunction
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.psi.PhpFileImpl
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId

class GotoTemplateUsagesMarkerProvider : LineMarkerProvider {

    override fun collectSlowLineMarkers(
        elements: MutableList<PsiElement>,
        result: MutableCollection<LineMarkerInfo<PsiElement>>
    ) {
        elements.forEach { psiElement ->
            val file = psiElement.containingFile
            if (IsCorrectTemplateIdConstraint().valid(file.virtualFile.name) && file.language == PhpLanguage.INSTANCE) {
                if (psiElement is PhpFileImpl) {
                    result.add(
                        LineMarkerInfo(
                            psiElement,
                            psiElement.getTextRange(),
                            PhpIcons.IMPLEMENTS,
                            ConstantFunction("Goto template usages"),
                            GotoTemplateUsagesNavigationHandler(TemplateId(file).id),
                            GutterIconRenderer.Alignment.RIGHT
                        )
                    )
                }
            }
        }
    }

    override fun getLineMarkerInfo(p0: PsiElement): LineMarkerInfo<PsiElement>? {
        return null
    }
}