package info.galliano.idea.pionPlugin.psi.constraints.php.hierarchy

import com.jetbrains.php.lang.psi.elements.PhpClass
import info.galliano.idea.pionPlugin.constraints.ConstraintInterface

class IsSubclassOfConstraint(private val classesFQNs: Set<String>) : ConstraintInterface<PhpClass> {
    override fun valid(input: PhpClass): Boolean {
        return this.classesFQNs.contains(input.fqn) ||
                ExtendsList(input).any { this.classesFQNs.contains(it) }
    }
}