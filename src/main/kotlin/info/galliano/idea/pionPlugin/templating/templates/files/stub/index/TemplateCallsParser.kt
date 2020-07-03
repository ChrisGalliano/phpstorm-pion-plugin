package info.galliano.idea.pionPlugin.templating.templates.files.stub.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.ParameterList
import info.galliano.idea.pionPlugin.templating.rendering.methods.PionRenderingMethods
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint
import info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls.MatchTemplateCallParametersPsiElementProcessor

class TemplateCallsParser(private val psiFile: PsiFile, private val virtualFile: VirtualFile) :
    Sequence<Pair<String, ParameterList>> {
    override fun iterator() = iterator {
        for (targetMethodCallDefinition in PionRenderingMethods.methods) {
            for (result in MatchTemplateCallParametersPsiElementProcessor.match(
                psiFile,
                virtualFile,
                targetMethodCallDefinition
            )) {
                val templateId = result.getTemplateId()
                if (IsCorrectTemplateIdConstraint().valid(templateId)) {
                    yield(Pair(templateId, result.parameterList))
                }
            }
        }
    }
}