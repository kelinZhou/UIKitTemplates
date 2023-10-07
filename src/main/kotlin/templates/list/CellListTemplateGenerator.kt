package templates.list

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import templates.generator.BeanClassTemplateGenerator
import templates.generator.CommonLayoutTemplateGenerator
import templates.generator.TemplateGenerator
import templates.generator.TemplateGenerators
import templates.tools.BaseType
import templates.tools.time

class CellListTemplateGenerator(
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
     * 分页功能是否可用。
     */
    private val isEnablePage: Boolean,
    /**
     * 数据请求时机。
     */
    private val setupMode: String?,
    /**
     * 布局名称。
     */
    private val layout: String?,
    /**
     * 单元条目名称。
     */
    private val cellName: String?,
    /**
     * 单元条目是否可以被点击。
     */
    private val cellClickable: Boolean,
    /**
     * 单元条目布局名称。
     */
    private val cellLayout: String?
) : TemplateGenerators {

    val cellPath = if (cellName.isNullOrBlank()) "com.kelin.uikit.listcell.Cell" else "${packagePath}.cell.${cellName}"
    val cell = if (cellName.isNullOrBlank()) "Cell" else cellName

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
        //创建Cell条目
        if (!cellName.isNullOrBlank()) {
            CellGenerator().generate(executor, templateData.srcDir, pageName, "cell/{}")
        }
        //创建Cell条目的布局文件。
        if (!cellLayout.isNullOrBlank()) {
            CommonLayoutTemplateGenerator().generate(executor, resDir, cellLayout)
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

    inner class PresenterGenerator : TemplateGenerator {
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
            val respType = if (respIsList) "${if (isEnablePage) "Mutable" else ""}List<${resp}>" else resp
            return """
                package $packagePath
                ${if (BaseType.isBaseType(req)) "" else "import ${packageName}.domain.argument.${req}"}
                ${if (BaseType.isBaseType(resp)) "" else "import ${packageName}.domain.model.${resp}"}
                import com.kelin.uikit.presenter.ItemListFragmentPresenter
                import $cellPath
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
                class ${pageName}Fragment : ItemListFragmentPresenter<${pageName}Delegate, ${pageName}Delegate.Callback, ${req}, $cell, ${respType}>() {
        
                    override val initialRequestId: $req = $defId            
                    ${if (setupMode.isNullOrBlank() || setupMode == "SETUP_DEFAULT") "" else "\noverride val setupMode: Int = $setupMode"}
                    ${if (isEnablePage) "" else "\noverride val isEnablePage: Boolean = false\n"}
                    override fun getApiObservable(id: ${req}, page: Int, size: Int): Observable<${respType}> {
                        TODO("Not yet implemented")
                    }
                    
                    override fun transformUIData(page: Int, cells: MutableList<$cell>, data: ${respType}) {
                        ${if (respIsList && !cellName.isNullOrBlank()) "data.forEach { cells.add($cell(it)) }" else "TODO(\"Not yet implemented\")"} 
                    }
                    ${if (respIsList) "" else "\n${checkEmptyMethod(respType)}"}${if (isEnablePage) "\n${addMoreMethod(respType)}${if (respIsList) "" else "\n${checkNoMoreDataMethod(respType)}"}" else ""}${"\n"}
                    override val viewCallback: ${pageName}Delegate.Callback by lazy { ${pageName}DelegateCallback() }
        
                    private inner class ${pageName}DelegateCallback : ItemListDelegateCallbackImpl(), ${pageName}Delegate.Callback
                }
            """.trimIndent()
        }

        private fun addMoreMethod(respType: String): String {
            return """
                override fun addMoreData(initialData: ${respType}, data: ${respType}): $respType {
                    TODO("Not yet implemented")
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

        private fun checkNoMoreDataMethod(respType: String): String {
            return """
                override fun checkIfGotAllData(data: ${respType}): Boolean {
                    TODO("Not yet implemented")
                }
            """.trimIndent()
        }
    }

    inner class DelegateGenerator : TemplateGenerator {
        override fun getFileName(pageName: String): String {
            return "${pageName}Delegate.kt"
        }

        override fun getTemplateCode(): String {
            return """
                package $packagePath
        
                import com.kelin.uikit.delegate.ItemListDelegate
                import $cellPath
                ${if (layout.isNullOrBlank()) "" else "import ${packageName}.R\n"}
                /**
                 * **描述:** $desc。
                 *
                 * **创建人:** kelin
                 *
                 * **创建时间:** $time
                 *
                 * **版本:** v 1.0.0
                 */
                class ${pageName}Delegate : ItemListDelegate<${pageName}Delegate.Callback, $cell>() {
                    ${
                if (layout.isNullOrBlank()) "" else """
                        
                        override val rootLayoutId: Int
                            get() = R.layout.${layout}
                            
                    """.trimIndent()
            }
                    interface Callback : ItemListDelegateCallback<$cell>
                }
            """.trimIndent()
        }
    }

    inner class CellGenerator : TemplateGenerator {
        override fun getFileName(pageName: String): String {
            return "${cell}.kt"
        }

        override fun getTemplateCode(): String {
            return """
                package ${packagePath}.cell
                ${if (cellClickable) "\nimport android.content.Context" else ""}
                import com.kelin.uikit.listcell.SimpleCell
                import android.view.View
                import ${packageName}.domain.model.${resp}
                import ${packageName}.R
                ${if (cellClickable) "import com.kelin.uikit.tools.ToastUtil\n" else ""}
                /**
                 * **描述:** 封装 ${pageName}Fragment 的ListCell。
                 *
                 * **创建人:** kelin
                 *
                 * **创建时间:** $time
                 *
                 * **版本:** v 1.0.0
                 */
                class ${cell}(val item: ${resp}) : SimpleCell() {
        
                    override val itemLayoutRes = ${if (cellLayout.isNullOrBlank()) "TODO(\"Not yet implemented\")" else "R.layout.${cellLayout}"}
        
                    override fun onBindData(iv: View) {
                        TODO("Not yet implemented")
                    }${if (cellClickable) "\n\noverride val itemClickable: Boolean = true\n\noverride fun onItemClick(iv: View, context: Context, position: Int) {\nToastUtil.showShortToast(\"${desc}的${cell}被点击!\")\n}" else ""}
                }
            """.trimIndent()
        }

    }
}