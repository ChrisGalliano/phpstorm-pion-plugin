package info.galliano.idea.pionPlugin.psi.index.utils.php

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

object PhpIndexUtil {
    fun getClassByFqn(fqn: String, project: Project): PhpClass? {
        var result: PhpClass? = null
        if (fqn.isNotEmpty()) {
            val classes = PhpIndex.getInstance(project).getClassesByFQN(fqn)
                .plus(PhpIndex.getInstance(project).getInterfacesByFQN(fqn))
            if (classes.isNotEmpty()) {
                result = classes.iterator().next()
            }
        }
        return result
    }
}