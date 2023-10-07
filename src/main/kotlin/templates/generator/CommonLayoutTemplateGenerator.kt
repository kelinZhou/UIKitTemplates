package templates.generator

import com.android.tools.idea.wizard.template.RecipeExecutor
import java.io.File

class CommonLayoutTemplateGenerator : TemplateGenerator {
    override fun getFileName(pageName: String): String = ""

    override fun getTemplateCode(): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <androidx.constraintlayout.widget.ConstraintLayout 
                xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent">

            </androidx.constraintlayout.widget.ConstraintLayout>
        """.trimIndent()
    }

    override fun generate(executor: RecipeExecutor, dir: File, pageName: String, relative: String, open: Boolean) {
        val target = dir.resolve("layout/${pageName}.xml")
        executor.save(getTemplateCode(), target)
        if (open) {
            executor.open(target)
        }
    }
}