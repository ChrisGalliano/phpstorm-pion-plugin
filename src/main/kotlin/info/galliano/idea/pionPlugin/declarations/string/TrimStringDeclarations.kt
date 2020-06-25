package info.galliano.idea.pionPlugin.declarations.string

fun String.trimQuote() = replace("^\"|\"$|\'|\'$".toRegex(), "")
