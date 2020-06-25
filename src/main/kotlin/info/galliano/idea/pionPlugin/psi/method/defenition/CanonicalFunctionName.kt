package info.galliano.idea.pionPlugin.psi.method.defenition

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.*

object CanonicalFunctionName {
    fun resolve(caller: PsiElement): String? {
        val ref = caller.reference
        return when {
            ref is MethodReference -> {
                getMethodFqn((ref as MethodReference?)!!)
            }
            ref is FunctionReference -> {
                getFuncName(caller)
            }
            caller is NewExpression -> {
                getClassConstructName(caller)
            }
            else -> null
        }
    }

    private fun getFuncName(caller: PsiElement): String? {
        val psiReference = caller.reference
        if (psiReference != null) {
            val resolvedReference = psiReference.resolve()
            if (resolvedReference is com.jetbrains.php.lang.psi.elements.Function) {
                return resolvedReference.name
            }
        }
        return null
    }

    private fun getMethodFqn(psiReference: MethodReference): String? {
        val resolvedReference = psiReference.resolve() as? Method ?: return null
        var classFqn = ""
        if (psiReference.referenceType.isStatic) {
            val classRef = psiReference.classReference
            if (classRef is ClassReference) {
                val fqnFromClassRef = classRef.fqn
                if (fqnFromClassRef != null) {
                    classFqn = fqnFromClassRef
                }
            } else if (classRef is Variable) {
                val stringResolved = classRef.declaredType.toStringResolved()
                if (stringResolved is String) {
                    val regex = Regex("#M#C(.+)\\.instance\\|\\?")
                    val result = regex.matchEntire(stringResolved)
                    if (result != null) {
                        classFqn = result.groupValues[1]
                    }
                }
            }
        }
        if (classFqn === "") {
            val methodClass = resolvedReference.containingClass
            if (methodClass != null) {
                classFqn = methodClass.fqn
            }
        }
        if (classFqn === "") {
            return null
        }
        return classFqn + "::" + resolvedReference.name
    }

    private fun getClassConstructName(caller: PsiElement): String? {

        val children = caller.children

        if (children.isEmpty())
            return null

        val psiReference = children[0].reference as? ClassReference ?: return null
        val resolved = psiReference.resolve()
        val resolvedReference = resolved as? Method ?: return null

        if (resolvedReference.name != "__construct") {
            return null
        }

        val methodClass = resolvedReference.containingClass ?: return null

        val className = methodClass.fqn
        return "$className::__construct"
    }
}