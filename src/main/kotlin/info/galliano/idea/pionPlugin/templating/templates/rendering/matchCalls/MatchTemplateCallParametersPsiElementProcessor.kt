package info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.impl.ConcatenationExpressionImpl
import info.galliano.idea.pionPlugin.psi.method.defenition.MethodCallDefinition

class MatchTemplateCallParametersPsiElementProcessor(
    private val parameterIndex: Int = 0,
    private val targetMethodCallDefinition: MethodCallDefinition,
    private val targets: MutableCollection<TemplateCallParameters>,
    private val virtualFile: VirtualFile
) : PsiElementProcessor<PsiElement> {
    companion object {
        fun match(psiFile: PsiFile, virtualFile: VirtualFile, target: MethodCallDefinition): Collection<TemplateCallParameters> {
            val targets = mutableListOf<TemplateCallParameters>()
            PsiTreeUtil.processElements(
                psiFile,
                MatchTemplateCallParametersPsiElementProcessor(
                    target.argumentIndex,
                    target,
                    targets,
                    virtualFile
                )
            )
            return targets
        }
    }

    override fun execute(psiElement: PsiElement): Boolean {
        if (
            psiElement !is ConcatenationExpressionImpl
            || !IsTemplatePathPsiElementConstraint().valid(psiElement)
        ) {
            return true
        }
        var parent: PsiElement? = psiElement.parent as? ParameterList ?: return true
        val parameterList = parent as ParameterList
        if (parameterList.parameters[parameterIndex] != psiElement) {
            return true
        }
        parent = parent.getParent()
        if (parent == null) {
            return true
        }
        val isConstructor: Boolean = targetMethodCallDefinition.getMethodName().equals("__construct")
        if (isConstructor) {
            if (parent is NewExpression) {
                val classReference = parent.classReference
                val fqn = classReference?.fqn
                if (fqn != null && fqn == targetMethodCallDefinition.classFqn) {
                    targets.add(
                        TemplateCallParameters(
                            psiElement,
                            parameterList,
                            virtualFile
                        )
                    )
                }
            }
        } else if (parent is MethodReference) {
            val methodName = parent.name!!
            if (targetMethodCallDefinition.getMethodName().equals(methodName)) {
                targets.add(
                    TemplateCallParameters(
                        psiElement,
                        parameterList,
                        virtualFile
                    )
                )
            }
        }
        return true
    }
}