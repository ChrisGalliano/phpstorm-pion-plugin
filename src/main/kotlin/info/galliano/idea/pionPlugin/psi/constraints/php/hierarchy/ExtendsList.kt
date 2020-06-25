package info.galliano.idea.pionPlugin.psi.constraints.php.hierarchy

import com.jetbrains.php.lang.psi.elements.PhpClass
import info.galliano.idea.pionPlugin.psi.index.utils.php.PhpIndexUtil

class ExtendsList(private val phpClass: PhpClass) : Sequence<String> {
    @Suppress("RemoveExplicitTypeArguments")
    override fun iterator() = iterator<String> {
        val references = phpClass.extendsList.referenceElements.plus(phpClass.implementsList.referenceElements)
        for (reference in references) {
            val fqn = reference.fqn
            if (fqn != null) {
                yield(fqn)
                val extendClass = PhpIndexUtil.getClassByFqn(fqn, phpClass.project)
                if (extendClass != null) {
                    yieldAll(ExtendsList(extendClass))
                }
            }
        }
    }
}