package info.galliano.idea.pionPlugin.templating.variables.type.provider

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import info.galliano.idea.pionPlugin.templating.templates.files.stub.index.TemplateFileStubsIndex
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId
import info.galliano.idea.pionPlugin.templating.variables.pased.PassedTemplateVariablePsiImplFinder

class VariableTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return 2.toChar()
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (psiElement is VariableImpl) {
            val file = psiElement.containingFile.originalFile
            val virtualFile = file.virtualFile
            if (virtualFile != null && IsCorrectTemplateIdConstraint().valid(virtualFile.name)) {
                val project = file.project
                val targetVariableName = psiElement.getText()
                val items = TemplateFileStubsIndex.instance.getTemplatesStubs(
                    project,
                    TemplateId(virtualFile, project).id
                )
                for (template in items) {
                    val variablesMap = template.getVariablesPositions()
                    for (variableName in variablesMap.keys) {
                        if (targetVariableName == "$$variableName") {
                            val value = PassedTemplateVariablePsiImplFinder(
                                project,
                                template,
                                variableName
                            ).get()
                            if (value != null) {
                                return PhpType().add(value)
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement> {
        return emptySet()
    }

    override fun complete(p0: String?, p1: Project?): PhpType? {
        return null
    }
}