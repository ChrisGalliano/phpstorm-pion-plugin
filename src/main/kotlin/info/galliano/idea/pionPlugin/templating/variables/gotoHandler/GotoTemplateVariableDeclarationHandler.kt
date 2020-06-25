package info.galliano.idea.pionPlugin.templating.variables.gotoHandler

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import info.galliano.idea.pionPlugin.templating.templates.files.stub.index.TemplateFileStubsIndex
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.IsCorrectTemplateIdConstraint
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId
import java.util.*

class GotoTemplateVariableDeclarationHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(psiElement: PsiElement?, i: Int, editor: Editor): Array<PsiElement?>? {
        if (psiElement == null || !PlatformPatterns.psiElement(PhpTokenTypes.VARIABLE).accepts(psiElement)) {
            return null
        }

        val file = psiElement.containingFile.originalFile
        val project = file.project
        val virtualFile = file.virtualFile
        if (virtualFile == null || !IsCorrectTemplateIdConstraint().valid(virtualFile.name)) {
            return null
        }
        val templates = TemplateFileStubsIndex.instance.getTemplatesStubs(project, TemplateId(virtualFile, project).id)
        val elements = ArrayList<PsiElement>()
        val fileElements = HashMap<String, MutableSet<Int>>()
        for (template in templates) {
            val variablesMap = template.getVariablesPositions()
            for (variableName in variablesMap.keys) {
                if (psiElement.text == "$$variableName") {
                    var positions = fileElements[template.getFilePath()]
                    if (positions == null) {
                        positions = HashSet()
                    }
                    positions.addAll(variablesMap.getValue(variableName))
                    fileElements[template.getFilePath()] = positions
                }
            }
        }
        for (filePath in fileElements.keys) {
            val targetFile = VfsUtil.findRelativeFile("/$filePath", project.baseDir) ?: continue
            val psiFile = PsiManager.getInstance(psiElement.project).findFile(targetFile) ?: continue
            for (position in fileElements[filePath]!!) {
                val el = psiFile.findElementAt(position)
                if (el == null || el.parent == null) {
                    continue
                }
                elements.add(el.parent)
            }
        }
        return elements.toTypedArray()
    }

    override fun getActionText(dataContext: DataContext): String? {
        return null
    }
}