package templates.generator

class BeanClassTemplateGenerator(private val packagePath: String, private val name: String) : TemplateGenerator {
    override fun getFileName(pageName: String): String {
        return "${name}.kt"
    }

    override fun getTemplateCode(): String {
        return """
            package $packagePath

            class $name
        """.trimIndent()
    }
}