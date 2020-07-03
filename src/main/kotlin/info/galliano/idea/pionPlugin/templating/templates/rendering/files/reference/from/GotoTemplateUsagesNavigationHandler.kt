package info.galliano.idea.pionPlugin.templating.templates.rendering.files.reference.from

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.navigation.NavigationUtil
import com.intellij.navigation.GotoRelatedItem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.ui.awt.RelativePoint
import info.galliano.idea.pionPlugin.templating.rendering.methods.PionRenderingMethods
import info.galliano.idea.pionPlugin.templating.templates.files.stub.index.TemplateFileStubsIndex
import info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls.MatchTemplateCallParametersPsiElementProcessor
import java.awt.event.MouseEvent

class GotoTemplateUsagesNavigationHandler(private val templateId: String) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent?, el: PsiElement?) {
        if (e != null && el != null) {
            var relatesItems = arrayOf<GotoRelatedItem>()
            TemplateFileStubsIndex().getContainingFiles(el.project, templateId).forEach { virtualFile: VirtualFile ->
                val psiFile = PsiManager.getInstance(el.project).findFile(virtualFile)
                if (psiFile != null) {
                    for (targetMethodCallDefinition in PionRenderingMethods.methods) {
                        val templateCallsParameters = MatchTemplateCallParametersPsiElementProcessor.match(
                            psiFile,
                            virtualFile,
                            targetMethodCallDefinition
                        )
                        for (result in templateCallsParameters) {
                            val resultElementTemplateId = result.getTemplateId()
                            if (resultElementTemplateId == templateId) {
                                relatesItems = relatesItems.plus(
                                    GotoTemplateUsageRelatedItem(
                                        result.templatePathElement,
                                        psiFile
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (relatesItems.size == 1) {
                relatesItems[0].navigate()
            } else if (relatesItems.size > 1) {
                NavigationUtil
                    .getRelatedItemsPopup(relatesItems.toMutableList(), "Goto template usages")
                    .show(RelativePoint(e))
            }
        }
    }
}