package info.galliano.idea.pionPlugin.templating.templates.files.stub.index

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorStringDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import gnu.trove.THashMap
import info.galliano.idea.pionPlugin.declarations.string.trimQuote
import info.galliano.idea.pionPlugin.templating.templates.files.stub.TemplateFileStub
import java.util.*

class TemplateFileStubsIndex : FileBasedIndexExtension<String, TemplateFileStub>() {
    private val key = ID.create<String, TemplateFileStub>("pionSupport.templating.TemplateFileStubsIndex")
    private val myKeyDescriptor = EnumeratorStringDescriptor()
    override fun getName() = key
    override fun getKeyDescriptor() = myKeyDescriptor
    override fun getValueExternalizer() = ObjectStreamDataExternalizer<TemplateFileStub>()
    override fun getVersion() = 4
    override fun dependsOnFileContent() = true

    companion object {
        val instance = TemplateFileStubsIndex()
    }

    override fun getIndexer(): DataIndexer<String, TemplateFileStub, FileContent> {
        return DataIndexer {
            val map = THashMap<String, TemplateFileStub>()
            val psiFile = it.psiFile as? PhpFile ?: return@DataIndexer map
            val relativePath = VfsUtil.getRelativeLocation(it.file, psiFile.project.baseDir)
            if (relativePath == null || relativePath.isEmpty()) {
                return@DataIndexer map
            }

            for ((templateId, parameterList) in TemplateCallsParser(psiFile)) {
                val templateFileStub = map[templateId] ?: TemplateFileStub(templateId, relativePath)
                val templateParameter = getMethodParameterPsiElementAt(parameterList)
                if (templateParameter is ArrayCreationExpression) {
                    val arrayHashElements = templateParameter.hashElements
                    for (arrayHashElement in arrayHashElements) {
                        val arrayKey = arrayHashElement.key as? StringLiteralExpression ?: continue
                        val variableName = arrayKey.text.trimQuote()
                        templateFileStub.addVariablePosition(variableName, arrayKey.textOffset)
                    }
                }
                map[templateId] = templateFileStub
            }
            map
        }
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return FileBasedIndex.InputFilter {
            it.fileType === PhpFileType.INSTANCE && it.name.isNotEmpty()
        }
    }

    fun getTemplatesStubs(project: Project, templateId: String): List<TemplateFileStub> {
        return FileBasedIndex.getInstance().getValues(name, templateId, GlobalSearchScope.allScope(project))
    }

    fun getContainingFiles(project: Project, templateId: String): Collection<VirtualFile> {
        val virtualFiles = ArrayList<VirtualFile>()
        FileBasedIndex.getInstance().getFilesWithKey(name, HashSet(listOf(templateId)), { virtualFile ->
            virtualFiles.add(virtualFile)
            true
        }, GlobalSearchScope.allScope(project))
        return virtualFiles
    }

    private fun getMethodParameterPsiElementAt(parameterList: ParameterList): PsiElement? {
        val parameters = parameterList.parameters
        return if (parameters.size < 2) null else parameters[1]
    }
}