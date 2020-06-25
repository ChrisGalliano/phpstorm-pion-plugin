package info.galliano.idea.pionPlugin.psi.method.defenition

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import info.galliano.idea.pionPlugin.psi.constraints.php.hierarchy.IsSubclassOfConstraint
import info.galliano.idea.pionPlugin.psi.index.utils.php.PhpIndexUtil

class MethodCallDefinition(val classFqn: String, private val method: String, val argumentIndex: Int = 0) {
    private var parametersList: ParameterList? = null

    fun getMethodName(): String? {
        return method
    }

    fun isCallTo(methods: Array<MethodCallDefinition>, project: Project): Boolean {
        for (possibleMethodCall in methods) {
            if (possibleMethodCall.isSame(this, project)) {
                return true
            }
        }
        return false
    }

    private fun isSame(callToDefinition: MethodCallDefinition, project: Project): Boolean {
        val validIndex = callToDefinition.argumentIndex == this.argumentIndex
        if (!validIndex) {
            return false
        }
        val validMethod = callToDefinition.getMethodName() == getMethodName()
        if (!validMethod) {
            return false
        }
        if (this.classFqn == callToDefinition.classFqn) {
            return true
        }
        val definitionClass: PhpClass = PhpIndexUtil.getClassByFqn(callToDefinition.classFqn, project)
            ?: return false
        return IsSubclassOfConstraint(setOf(this.classFqn)).valid(definitionClass)
    }

    fun getTargetArgument(): PsiElement? {
        val parameters = this.parametersList?.parameters ?: return null
        return parameters[this.argumentIndex]
    }

    fun getParametersList(): ParameterList? {
        return parametersList
    }

    companion object {
        fun createFromArgument(argument: PsiElement): MethodCallDefinition? {
            val parameterList = argument.parent as? ParameterList ?: return null
            val params = parameterList.parameters
            var parameterIndex = -1
            for (i in params.indices) {
                if (params[i] == argument) {
                    parameterIndex = i
                    break
                }
            }

            if (parameterIndex == -1) {
                return null
            }

            val definition: MethodCallDefinition = createDefinition(parameterList.parent, parameterIndex)
                ?: return null
            definition.parametersList = parameterList
            return definition
        }

        private fun createDefinition(functionCall: PsiElement, argumentIndex: Int): MethodCallDefinition? {
            var result: MethodCallDefinition? = null
            val functionFqn = CanonicalFunctionName.resolve(functionCall)
            if (functionFqn != null) {
                val items = functionFqn.split("::").toTypedArray()
                if (1 == items.size) {
                    result = MethodCallDefinition("", items[0], argumentIndex)
                } else if (2 == items.size) {
                    result = MethodCallDefinition(items[0], items[1], argumentIndex)
                }
            }
            return result
        }
    }
}