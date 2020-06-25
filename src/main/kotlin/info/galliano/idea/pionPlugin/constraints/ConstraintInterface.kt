package info.galliano.idea.pionPlugin.constraints

interface ConstraintInterface<T> {
    fun valid(input: T): Boolean
}