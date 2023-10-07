package templates.normal

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import templates.generator.BeanClassTemplateGenerator
import templates.generator.CommonLayoutTemplateGenerator
import templates.generator.TemplateGenerator
import templates.generator.TemplateGenerators
import templates.tools.BaseType
import templates.tools.time

class NormalTemplateGenerator(
    /**
     * 执行器。
     */
    override val executor: RecipeExecutor,
    /**
     * 模板数据
     */
    override val templateData: ModuleTemplateData,
    /**
     * 包路径。
     */
    override val packagePath: String,
    /**
     * 文件名。
     */
    override val pageName: String,
    /**
     * 描述。
     */
    override val desc: String,
    /**
     * 获取数据的入参类型。
     */
    private val req: String,
    /**
     * 获取到的数据的类型。
     */
    private val resp: String,
    /**
     * 获取到的数据是否是列表。
     */
    private val respIsList: Boolean,
    /**
     * 数据请求时机。
     */
    private val setupMode: String?,
    /**
     * 布局名称。
     */
    private val layout: String?
) : TemplateGenerators {

    val respType = if (respIsList) "List<${resp}>" else resp

    init {
        onGenerate()
    }

    private fun onGenerate() {
        //创建Fragment。
        PresenterGenerator().generate(executor, srcDir, pageName, open = true)
        //创建Delegate。
        DelegateGenerator().generate(executor, srcDir, pageName)
        //创建Delegate的布局文件。
        if (!layout.isNullOrBlank()) {
            CommonLayoutTemplateGenerator().generate(executor, resDir, layout)
        }
        //创建请求参数类型。
        if (!BaseType.isBaseType(req)) {
            BeanClassTemplateGenerator("${packageName}.domain.argument", req).generate(executor, domainSrcDir, pageName, "argument/{}")
        }
        //创建返回值类型。
        if (!BaseType.isBaseType(resp)) {
            BeanClassTemplateGenerator("${packageName}.domain.model", resp).generate(executor, domainSrcDir, pageName, "model/{}")
        }
    }

    private inner class PresenterGenerator : TemplateGenerator {
        override fun getFileName(pageName: String): String {
            return "${pageName}Fragment.kt"
        }

        override fun getTemplateCode(): String {
            val defId = when (req) {
                "Any" -> "defaultAny"
                "Boolean" -> "false"
                "String" -> ""
                "Int" -> "0"
                "Long" -> "0L"
                else -> "${req}()"
            }
            return """
                package $packagePath
                ${if (BaseType.isBaseType(req)) "" else "import ${packageName}.domain.argument.${req}"}
                ${if (BaseType.isBaseType(resp)) "" else "import ${packageName}.domain.model.${resp}"}
                import com.kelin.uikit.presenter.CommonFragmentPresenter
                import io.reactivex.Observable
                
                
                /**
                 * **描述:** $desc。
                 *
                 * **创建人:** kelin
                 *
                 * **创建时间:** $time
                 *
                 * **版本:** v 1.0.0
                 */
                class ${pageName}Fragment : CommonFragmentPresenter<${pageName}Delegate, ${pageName}Delegate.Callback, ${req}, ${respType}, ${respType}>() {
                
                    override val initialRequestId: $req = $defId
                    ${if (setupMode.isNullOrBlank() || setupMode == "SETUP_DEFAULT") "" else "\noverride val setupMode: Int = $setupMode\n"}
                    override fun getApiObservable(id: $req): Observable<${respType}> {
                        TODO("Not yet implemented")
                    }
                    ${if (respIsList) "" else "\n${checkEmptyMethod(respType)}\n"}
                    override val viewCallback: ${pageName}Delegate.Callback by lazy { ${pageName}DelegateCallback() }
                
                    private inner class ${pageName}DelegateCallback : CommonDelegateCallbackImpl(), ${pageName}Delegate.Callback
                }
            """.trimIndent()
        }

        private fun checkEmptyMethod(respType: String): String {
            return """
                override fun checkDataIsEmpty(data: ${respType}): Boolean {
                    TODO("Not yet implemented")
                }
            """.trimIndent()
        }
    }

    private inner class DelegateGenerator : TemplateGenerator {
        override fun getFileName(pageName: String): String {
            return "${pageName}Delegate.kt"
        }

        override fun getTemplateCode(): String {
            return """
                package $packagePath

                import com.paidian.eride.R
                import com.kelin.uikit.delegate.CommonViewDelegate
                ${if (BaseType.isBaseType(resp)) "" else "import ${packageName}.domain.model.${resp}"}


                /**
                 * **描述:** $desc。
                 *
                 * **创建人:** kelin
                 *
                 * **创建时间:** $time
                 *
                 * **版本:** v 1.0.0
                 */
                class ${pageName}Delegate : CommonViewDelegate<${pageName}Delegate.Callback, $respType>() {

                    override val rootLayoutId = ${if (layout.isNullOrBlank()) "TODO(\"Not yet implemented\")" else "R.layout.${layout}"}
                    
                    override fun setInitialData(data: $respType) {
                        TODO("Not yet implemented")
                    }
                
                    interface Callback : CommonViewDelegateCallback
                }
            """.trimIndent()
        }
    }
}