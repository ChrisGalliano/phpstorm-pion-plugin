package info.galliano.idea.pionPlugin.templating.templates.rendering.files.path.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import info.galliano.idea.pionPlugin.declarations.string.trimQuote
import info.galliano.idea.pionPlugin.psi.method.defenition.MethodCallDefinition
import info.galliano.idea.pionPlugin.templating.inspections.CreateTemplateFileQuickFix
import info.galliano.idea.pionPlugin.templating.inspections.TemplateRenderVisitor

class InvalidTemplateFilePathInspection : PhpInspection() {
    override fun getShortName() = "InvalidTemplateFilePathInspection"

    override fun buildVisitor(problemsHolder: ProblemsHolder, b: Boolean): PsiElementVisitor {
        return TemplateRenderVisitor {
            visitRenderCall(it, problemsHolder)
        }
    }

    private fun visitRenderCall(methodCall: MethodCallDefinition, problemsHolder: ProblemsHolder) {
        val parametersList = methodCall.getParametersList() ?: return
        val targetElement = parametersList.parameters[0]
        val path = targetElement.text.trimQuote()
        val file = VfsUtilCore.findRelativeFile(path, targetElement.project.baseDir)
        if (file == null) {
            var qf: CreateTemplateFileQuickFix? = null
            if (!path.contains("/")) {
                qf = CreateTemplateFileQuickFix(path)
            }
            problemsHolder.registerProblem(
                targetElement,
                String.format("Invalid view file path: %s", path),
                ProblemHighlightType.ERROR,
                qf
            )
        }
    }
}