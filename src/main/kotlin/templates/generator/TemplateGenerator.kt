package templates.generator

import com.android.tools.idea.wizard.template.RecipeExecutor
import java.io.File

/**
 * **描述:** 模板生成器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023-09-27 17:16:22
 *
 * **版本:** v 1.0.0
 */
interface TemplateGenerator {
    fun getFileName(pageName: String): String

    /**
     * 获取模板代码。
     */
    fun getTemplateCode(): String

    /**
     * 创建模板。
     */
    fun generate(executor: RecipeExecutor, dir: File, pageName: String, relative: String = "{}", open: Boolean = false) {
        println("relative=${relative}")
        val relative1 = relative.replace("{}", getFileName(pageName))
        println("fileName=${getFileName(pageName)}")
        println("relative1=${relative1}")
        val target = dir.resolve(relative1)
        println("targetPath:${getFileName(pageName)}=${target.absolutePath}")
        executor.save(getTemplateCode(), target)
        if (open) {
            executor.open(target)
        }
    }
}