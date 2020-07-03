package info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls

import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.ConcatenationExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.ConstantReferenceImpl
import info.galliano.idea.pionPlugin.constraints.ConstraintInterface
import info.galliano.idea.pionPlugin.declarations.string.trimQuote
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint

class IsTemplatePathPsiElementConstraint : ConstraintInterface<ConcatenationExpressionImpl> {
    override fun valid(input: ConcatenationExpressionImpl): Boolean {
        val leftOperand = input.leftOperand
        val rightOperand = input.rightOperand
        return leftOperand is ConstantReferenceImpl
                && rightOperand is StringLiteralExpression
                && leftOperand.text == "__DIR__"
                && IsCorrectTemplateIdConstraint().valid(rightOperand.text.trimQuote())
    }
}