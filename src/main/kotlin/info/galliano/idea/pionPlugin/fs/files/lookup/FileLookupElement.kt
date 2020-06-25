package info.galliano.idea.pionPlugin.fs.files.lookup

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import info.galliano.idea.pionPlugin.fs.files.PresentableNameInterface

class FileLookupElement(private val presentableName: PresentableNameInterface) : LookupElement() {
    override fun getLookupString(): String {
        return presentableName.name()
    }

    override fun getAllLookupStrings(): Set<String> {
        val name = presentableName.name()
        return setOf(
            name,
            name.toLowerCase(),
            name.toUpperCase()
        )
    }

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = lookupString
        presentation.isTypeGrayed = true
    }
}