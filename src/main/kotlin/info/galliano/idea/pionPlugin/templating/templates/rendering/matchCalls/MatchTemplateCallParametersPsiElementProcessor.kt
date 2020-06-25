package info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import info.galliano.idea.pionPlugin.psi.method.defenition.MethodCallDefinition

class MatchTemplateCallParametersPsiElementProcessor(
    private val parameterIndex: Int = 0,
    private val targetMethodCallDefinition: MethodCallDefinition,
    private val targets: MutableCollection<TemplateCallParameters>
) : PsiElementProcessor<PsiElement> {
    companion object {
        fun match(psiFile: PsiFile, target: MethodCallDefinition): Collection<TemplateCallParameters> {
            val targets = mutableListOf<TemplateCallParameters>()
            PsiTreeUtil.processElements(
                psiFile,
                MatchTemplateCallParametersPsiElementProcessor(
                    target.argumentIndex,
                    target,
                    targets
                )
            )
            return targets
        }
    }

    override fun execute(psiElement: PsiElement): Boolean {
        if (psiElement !is StringLiteralExpression) {
            return true
        }
        var parent: PsiElement? = psiElement.getParent() as? ParameterList ?: return true
        val parameterList =
            parent as ParameterList
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
                val classReference =
                    parent.classReference
                val fqn = classReference?.fqn
                if (fqn != null && fqn == targetMethodCallDefinition.classFqn) {
                    targets.add(
                        TemplateCallParameters(
                            psiElement,
                            parameterList
                        )
                    )
                }
            }
            /*  @TODO fix this ... */
//            if (parent is MethodReference
//                && parent.name == "__construct"
//            ) {
//                val resolved = parent.resolve()
//                if (resolved is MethodImpl) {
//                    val containingClass =
//                        resolved.containingClass
//                    if (containingClass != null) {
//                        val isTargetMethodCall: Boolean = targetMethodCallDefinition.isSame(
//                            MethodCallDefinition(
//                                containingClass.fqn,
//                                resolved.name
//                            ),
//                            resolved.getProject()
//                        )
//                        if (isTargetMethodCall) {
//                            targets.add(MatchedMethod(psiElement, parameterList))
//                        }
//                    }
//                }
//            }
        } else if (parent is MethodReference) {
            val methodName = parent.name!!
            if (targetMethodCallDefinition.getMethodName().equals(methodName)) {
                targets.add(
                    TemplateCallParameters(
                        psiElement,
                        parameterList
                    )
                )
            }
        }
        return true
    }
}