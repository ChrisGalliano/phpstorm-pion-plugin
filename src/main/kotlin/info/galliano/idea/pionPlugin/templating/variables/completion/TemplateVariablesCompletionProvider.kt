package info.galliano.idea.pionPlugin.templating.variables.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.ProjectAndLibrariesScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import gnu.trove.THashMap
import info.galliano.idea.pionPlugin.templating.variables.pased.PassedTemplateVariablePsiImplFinder
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint
import info.galliano.idea.pionPlugin.templating.templates.files.stub.index.TemplateFileStubsIndex
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId

class TemplateVariablesCompletionProvider : CompletionProvider<CompletionParameters>() {
    public override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        val psiElement = parameters.position.originalElement
        val variableImpl = psiElement.parent as? VariableImpl ?: return
        if (variableImpl.useScope is ProjectAndLibrariesScope) {
            val file = psiElement.containingFile ?: return
            val virtualFile = file.originalFile.virtualFile
            if (!IsCorrectTemplateIdConstraint().valid(virtualFile.name)) {
                return
            }

            val variableNames: MutableMap<String, Boolean> = THashMap()

            PsiTreeUtil.processElements(file) { filePsiElement: PsiElement? ->
                if (filePsiElement !is Variable) {
                    return@processElements true
                }
                if (filePsiElement.useScope != variableImpl.useScope) {
                    return@processElements true
                }
                variableNames[filePsiElement.name] = true
                true
            }

            val project = file.project
            val templates = TemplateFileStubsIndex.instance.getTemplatesStubs(project, TemplateId(virtualFile, project).id)
            for (template in templates) {
                for (variableName in template.getVariablesPositions().keys) {
                    if (variableNames.containsKey(variableName)) {
                        continue
                    }

                    var type = ""
                    val value = PassedTemplateVariablePsiImplFinder(
                        project,
                        template,
                        variableName
                    ).get()
                    if (value is PhpTypedElement) {
                        val phpType = value.type
                        val signatureType = phpType.toStringResolved()
                        if (!signatureType.contains(".")) {
                            type = signatureType
                        }
                    }

                    resultSet.addElement(
                        LookupElementBuilder.create("$$variableName").withLookupString(variableName)
                            .withTypeText(type)
                            .withPresentableText(variableName)
                            .withBoldness(true)
                            .withIcon(PhpIcons.VARIABLE)
                    )
                }
            }
        }
    }
}