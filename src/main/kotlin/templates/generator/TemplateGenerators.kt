package templates.generator

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import templates.ITemplate
import templates.tools.packageToPath
import java.io.File

/**
 * **描述:** 代码模板生成器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023-09-27 17:16:22
 *
 * **版本:** v 1.0.0
 */
interface TemplateGenerators : ITemplate {
    /**
     * 执行器。
     */
    val executor: RecipeExecutor

    /**
     * 模板数据。
     */
    val templateData: ModuleTemplateData

    val srcDir: File
        get() = templateData.srcDir

    val resDir: File
        get() = templateData.resDir

    val domainSrcDir: File
        get() = templateData.rootDir.absolutePath.let { dir ->
            File("${dir.substring(0, dir.lastIndexOf('/') + 1)}domain/src/main/java/${packageToPath(packageName)}/domain")
        }

    /**
     * 包名。
     */
    val packageName: String
        get() = templateData.projectTemplateData.applicationPackage ?: templateData.packageName
}