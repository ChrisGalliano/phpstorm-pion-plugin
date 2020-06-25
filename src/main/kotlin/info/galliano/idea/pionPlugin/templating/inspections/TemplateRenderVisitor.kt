package info.galliano.idea.pionPlugin.templating.inspections

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import info.galliano.idea.pionPlugin.templating.rendering.methods.PionRenderingMethods
import info.galliano.idea.pionPlugin.psi.method.defenition.MethodCallDefinition

class TemplateRenderVisitor(val callback: (call: MethodCallDefinition) -> Unit) : PhpElementVisitor() {
    override fun visitPhpClassReference(classReference: ClassReference) {
        if (classReference.context is NewExpression) {
            val parameters = (classReference.context as NewExpression).parameters
            visitRenderCall(parameters)
        }
    }

    override fun visitPhpMethodReference(reference: MethodReference) {
        super.visitPhpMethodReference(reference)
        val parameters = reference.parameters
        visitRenderCall(parameters)
    }

    private fun visitRenderCall(parameters: Array<PsiElement>) {
        if (parameters.isNotEmpty()) {
            val parameter = parameters[0]
            if (parameter is StringLiteralExpression) {
                val methodCall = MethodCallDefinition.createFromArgument(parameter)
                if (methodCall != null && methodCall.isCallTo(PionRenderingMethods.methods, parameter.getProject())) {
                    callback(methodCall)
                }
            }
        }
    }
}