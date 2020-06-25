package info.galliano.idea.pionPlugin.templating.templates.files.stub.index

import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.ParameterList
import info.galliano.idea.pionPlugin.declarations.string.trimQuote
import info.galliano.idea.pionPlugin.templating.rendering.methods.PionRenderingMethods
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint
import info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls.MatchTemplateCallParametersPsiElementProcessor

class TemplateCallsParser(private val psiFile: PsiFile) : Sequence<Pair<String, ParameterList>> {
    override fun iterator() = iterator {
        for (targetMethodCallDefinition in PionRenderingMethods.methods) {
            for (result in MatchTemplateCallParametersPsiElementProcessor.match(psiFile, targetMethodCallDefinition)) {
                val templateId = result.templateIdElement.text.trimQuote()
                if (IsCorrectTemplateIdConstraint().valid(templateId)) {
                    yield(Pair(templateId, result.parameterList))
                }
            }
        }
    }
}