package info.galliano.idea.pionPlugin.templating.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import info.galliano.idea.pionPlugin.templating.templates.ids.constraints.TemplateId

class CreateTemplateFileQuickFix(private val filePath: String) : LocalQuickFix {
    override fun getName(): String {
        return "Create template file"
    }

    override fun getFamilyName(): String {
        return name
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val psiElement = descriptor.psiElement ?: return
        ApplicationManager.getApplication().runWriteAction {
            val containingFile = psiElement.containingFile
            if (containingFile != null) {
                val dir = containingFile.virtualFile.parent
                if (dir != null) {
                    val initialBaseDir = PsiManager.getInstance(project).findDirectory(dir)
                    if (null != initialBaseDir) {
                        val file = PsiFileFactory.getInstance(project).createFileFromText(
                            filePath,
                            PhpFileType.INSTANCE,
                            """<? declare(strict_types = 1); ?>

                                """.trimMargin(),
                            System.currentTimeMillis(),
                            true
                        )
                        if (initialBaseDir.findFile(file.name) == null) {
                            initialBaseDir.add(file)
                        }
                        val realFile = initialBaseDir.findFile(file.name)
                        if (realFile != null) {
                            psiElement.replace(
                                PhpPsiElementFactory.createPhpPsiFromText(
                                    project,
                                    StringLiteralExpression::class.java,
                                    """'${TemplateId(realFile, project).id}'"""
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}