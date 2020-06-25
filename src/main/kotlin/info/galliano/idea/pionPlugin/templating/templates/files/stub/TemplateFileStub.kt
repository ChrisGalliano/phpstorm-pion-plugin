package info.galliano.idea.pionPlugin.templating.templates.files.stub

import gnu.trove.THashMap
import org.apache.commons.lang.builder.HashCodeBuilder
import java.io.Serializable
import java.util.*

class TemplateFileStub(private val name: String, private var filePath: String) : Serializable {
    private val variablesPositions: MutableMap<String, MutableSet<Int>> = THashMap()

    fun getName(): String {
        return name
    }

    fun addVariablePosition(variable: String, index: Int) {
        var items = variablesPositions[variable]
        if (items == null) {
            items = HashSet()
        }
        items.add(index)
        variablesPositions[variable] = items
    }

    fun getVariablesPositions(): Map<String, MutableSet<Int>> {
        return variablesPositions
    }

    fun getFilePath(): String {
        return filePath
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(name)
            .append(filePath)
            .append(THashMap(variablesPositions))
            .toHashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is TemplateFileStub
                && other.getName() == name
                && other.getFilePath() == filePath
                && THashMap(other.variablesPositions) == THashMap(variablesPositions)
    }
}