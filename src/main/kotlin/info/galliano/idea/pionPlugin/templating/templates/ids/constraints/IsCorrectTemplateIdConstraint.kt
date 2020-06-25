package info.galliano.idea.pionPlugin.templating.templates.ids.constraints

import info.galliano.idea.pionPlugin.constraints.ConstraintInterface

class IsCorrectTemplateIdConstraint : ConstraintInterface<String> {
    override fun valid(input: String): Boolean {
        return input.matches(".+View\\.html".toRegex())
    }
}