package info.galliano.idea.pionPlugin.templating.variables.pased

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiManager
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.patterns.PhpPatterns
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import info.galliano.idea.pionPlugin.templating.templates.files.stub.TemplateFileStub

class PassedTemplateVariablePsiImplFinder(
    private val project: Project,
    private val templateFileStub: TemplateFileStub,
    private val variable: String
) {
    fun get(): PhpPsiElement? {
        val targetFile = VfsUtil.findRelativeFile("/" + templateFileStub.getFilePath(), project.baseDir) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(targetFile) ?: return null
        val positions = templateFileStub.getVariablesPositions()[variable] ?: return null

        for (position in positions.iterator()) {
            val el = psiFile.findElementAt(position)
            if (el == null || el.parent == null) {
                continue
            }
            val parent = el.parent.parent
            if (PhpPatterns.psiElement(PhpElementTypes.ARRAY_KEY).accepts(parent)) {
                val context = parent.context
                if (context is ArrayHashElement) {
                    return context.value
                }
            }
        }
        return null
    }
}