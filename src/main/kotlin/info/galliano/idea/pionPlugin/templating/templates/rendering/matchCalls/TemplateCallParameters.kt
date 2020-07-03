package info.galliano.idea.pionPlugin.templating.templates.rendering.matchCalls

import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.impl.ConcatenationExpressionImpl
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId

class TemplateCallParameters(
    val templatePathElement: ConcatenationExpressionImpl,
    val parameterList: ParameterList,
    private val virtualFile: VirtualFile
) {
    fun getTemplateId(): String {
        return TemplateId(templatePathElement, virtualFile).id
    }
}