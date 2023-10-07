package templates

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.activities.common.MIN_API
import templates.list.CellListTemplateGenerator
import templates.normal.NormalTemplateGenerator
import templates.tools.*
import java.io.File

object Templates {

    //包名
    private val packageParameter = defPackageNameParameter

    //名称
    private val nameParameter = stringParameter {
        name = "Presenter Name"
        default = "UIKit"
        help = "Only need to enter a name, not an Activity, Fragment, etc."
        constraints = listOf(Constraint.NONEMPTY, Constraint.CLASS)
    }

    //改变数据请求模式。
    private val setupChangeParameter = booleanParameter {
        name = "Change Setup Mode Of Proxy"
        default = false
        help = "Select and change the default data request timing."
    }

    //数据请求模式选择。
    private val setupParameter = enumParameter<SetupMode> {
        name = "Select Setup Mode"
        help = "Choose when to get the data."
        default = SetupMode.Default
        visible = { setupChangeParameter.value }
    }

    //创建页面请求参数。
    private val idCreatorParameter = booleanParameter {
        name = "Generate Parameter"
        default = false
        help = "Whether to create parameters for page requests for data."
    }

    //请求参数基本类型。
    private val idBaseParameter = enumParameter<BaseType> {
        name = "Select Type Of Parameter"
        help = "Choose type for requests for data."
        default = BaseType.Any
        visible = { !idCreatorParameter.value }
    }

    //请求参数的自定义类型。
    private val idParameter = stringParameter {
        name = "Parameter Name"
        default = "${nameParameter.value}Req"
        suggest = {
            "${nameParameter.value}Req"
        }
        visible = { idCreatorParameter.value }
    }

    //创建页面数据。
    private val respCreatorParameter = booleanParameter {
        name = "Generate Response"
        default = false
        help = "Whether to create Bean for page response for data."
    }

    //响应数据基本类型。
    private val respBaseParameter = enumParameter<BaseType> {
        name = "Select Type Of Response"
        help = "Choose type for data for response."
        default = BaseType.Any
        visible = { !respCreatorParameter.value }
    }

    //响应数据的自定义类型。
    private val respParameter = stringParameter {
        name = "Response Name"
        default = "${nameParameter.value}Resp"
        suggest = {
            "${nameParameter.value}Resp"
        }
        visible = { respCreatorParameter.value }
    }

    /**
     * 响应数据是否为列表。
     */
    private val isRespListParameter = booleanParameter {
        name = "Response Is List"
        default = false
        help = "After this is checked, Response is a list."
    }

    //描述。
    private val descParameter = stringParameter {
        name = "Description"
        default = "${nameParameter.value}页面"
        suggest = { "${nameParameter.value}页面" }
    }

    fun normal(): Template {
        return template {
            name = "UIKit Common (Presenter + Delegate)"
            description = "Create a new CommonFragmentPresenter&Delegate."
            minApi = MIN_API
            category = Category.Fragment
            formFactor = FormFactor.Mobile
            screens = listOf(WizardUiContext.ActivityGallery, WizardUiContext.MenuEntry, WizardUiContext.NewProject, WizardUiContext.NewModule)

            //创建布局。
            val layoutParameter = booleanParameter {
                name = "Generate Layout File"
                default = true
                help = "After this is checked, a layout file is generated for the ViewDelegate."
            }

            //布局文件名。
            val layoutNameParameter = stringParameter {
                name = "Layout File Name"
                default = "fragment_${humpToUnderline(nameParameter.value)}"
                suggest = {
                    "fragment_${humpToUnderline(nameParameter.value)}"
                }
                visible = {
                    layoutParameter.value
                }
                constraints = listOf(Constraint.NONEMPTY, Constraint.LAYOUT)
            }

            widgets(
                TextFieldWidget(nameParameter),
                CheckBoxWidget(layoutParameter),
                TextFieldWidget(layoutNameParameter),
                CheckBoxWidget(setupChangeParameter),
                EnumWidget(setupParameter),
                PackageNameWidget(packageParameter),
                CheckBoxWidget(idCreatorParameter),
                EnumWidget(idBaseParameter),
                TextFieldWidget(idParameter),
                CheckBoxWidget(respCreatorParameter),
                EnumWidget(respBaseParameter),
                TextFieldWidget(respParameter),
                CheckBoxWidget(isRespListParameter),
                TextFieldWidget(descParameter),
                LanguageWidget()
            )

            recipe = {
                val template = it as ModuleTemplateData
                val packageName = template.projectTemplateData.applicationPackage ?: template.packageName
                val domainDir = template.rootDir.absolutePath.let { dir ->
                    File("${dir.substring(0, dir.lastIndexOf('/') + 1)}domain/src/main/java/${packageToPath(packageName)}/domain")
                }
                println("package:${packageName}")
                println("rootDir:${template.rootDir}")
                println("domainDir:${domainDir.absolutePath}")
                println("project:${template.projectTemplateData}")
                println("src:${template.srcDir.absolutePath}")
                println("res:${template.resDir.absolutePath}")


                val setupMode = setupParameter.value

                NormalTemplateGenerator(
                    this,
                    template,
                    packageParameter.value,
                    nameParameter.value,
                    descParameter.value,
                    if (idCreatorParameter.value) idParameter.value else idBaseParameter.value.name,
                    if (respCreatorParameter.value) respParameter.value else respBaseParameter.value.name,
                    isRespListParameter.value,
                    if (setupChangeParameter.value && setupMode != SetupMode.Default) setupMode.value else null,
                    if (layoutParameter.value) layoutNameParameter.value else null
                )
            }
        }
    }

    fun list(): Template {
        return template {
            name = "UIKit CellList (Presenter + Delegate)"
            description = "Create a new CellListFragmentPresenter&Delegate."
            minApi = MIN_API
            category = Category.Fragment
            formFactor = FormFactor.Mobile
            screens = listOf(WizardUiContext.ActivityGallery, WizardUiContext.MenuEntry, WizardUiContext.NewProject, WizardUiContext.NewModule)

            //创建布局。
            val layoutParameter = booleanParameter {
                name = "Generate Layout File"
                default = false
                help = "After this is checked, a layout file is generated for the ViewDelegate."
            }

            //布局文件名。
            val layoutNameParameter = stringParameter {
                name = "Layout File Name"
                default = "fragment_${humpToUnderline(nameParameter.value)}"
                suggest = {
                    "fragment_${humpToUnderline(nameParameter.value)}"
                }
                visible = {
                    layoutParameter.value
                }
                constraints = listOf(Constraint.NONEMPTY, Constraint.LAYOUT)
            }

            //是否支持分页。
            val pageParameter = booleanParameter {
                name = "Page Enable"
                default = true
                help = "After this is checked, the paging function can be used."
            }

            // 是否创建Cell文件。
            val cellParameter = booleanParameter {
                name = "Generate Cell"
                default = false
                help = "After this is checked, a Cell file is generated for the list page."
            }

            //Cell的名称。
            val cellNameParameter = stringParameter {
                name = "Cell Name"
                default = "${nameParameter.value}Cell"
                suggest = {
                    "${nameParameter.value}Cell"
                }
                visible = { cellParameter.value }
            }

            //是否为Cell创建布局文件。
            val cellLayoutParameter = booleanParameter {
                name = "Generate Cell Layout File"
                default = false
                help = "After this is checked, a layout file is generated for the cell."
                visible = { cellParameter.value }
            }

            //Cell布局文件的名称。
            val cellLayoutNameParameter = stringParameter {
                name = "Cell Layout File Name"
                default = "cell_${humpToUnderline(nameParameter.value)}"
                suggest = {
                    "cell_${humpToUnderline(nameParameter.value)}"
                }
                visible = {
                    cellParameter.value && cellLayoutParameter.value
                }
            }

            //是否为Cell创建点击事件。
            val cellClickableParameter = booleanParameter {
                name = "Cell Clickable"
                default = false
                help = "After this is checked, the cell responds to the click event."
                visible = { cellParameter.value }
            }

            widgets(
                TextFieldWidget(nameParameter),
                CheckBoxWidget(layoutParameter),
                TextFieldWidget(layoutNameParameter),
                CheckBoxWidget(setupChangeParameter),
                EnumWidget(setupParameter),
                CheckBoxWidget(pageParameter),
                PackageNameWidget(packageParameter),
                CheckBoxWidget(idCreatorParameter),
                EnumWidget(idBaseParameter),
                TextFieldWidget(idParameter),
                CheckBoxWidget(respCreatorParameter),
                EnumWidget(respBaseParameter),
                TextFieldWidget(respParameter),
                CheckBoxWidget(isRespListParameter),
                CheckBoxWidget(cellParameter),
                TextFieldWidget(cellNameParameter),
                CheckBoxWidget(cellLayoutParameter),
                TextFieldWidget(cellLayoutNameParameter),
                CheckBoxWidget(cellClickableParameter),
                TextFieldWidget(descParameter),
                LanguageWidget()
            )


            recipe = {
                val template = it as ModuleTemplateData
                val packageName = template.projectTemplateData.applicationPackage ?: template.packageName
                val domainDir = template.rootDir.absolutePath.let { dir ->
                    File("${dir.substring(0, dir.lastIndexOf('/') + 1)}domain/src/main/java/${packageToPath(packageName)}/domain")
                }
                println("package:${packageName}")
                println("rootDir:${template.rootDir}")
                println("domainDir:${domainDir.absolutePath}")
                println("project:${template.projectTemplateData}")
                println("src:${template.srcDir.absolutePath}")
                println("res:${template.resDir.absolutePath}")


                val setupMode = setupParameter.value

                CellListTemplateGenerator(
                    this,
                    template,
                    packageParameter.value,
                    nameParameter.value,
                    descParameter.value,
                    if (idCreatorParameter.value) idParameter.value else idBaseParameter.value.name,
                    if (respCreatorParameter.value) respParameter.value else respBaseParameter.value.name,
                    isRespListParameter.value,
                    pageParameter.value,
                    if (setupChangeParameter.value && setupMode != SetupMode.Default) setupMode.value else null,
                    if (layoutParameter.value) layoutNameParameter.value else null,
                    if (cellParameter.value) cellNameParameter.value else null,
                    cellClickableParameter.value,
                    if (cellLayoutParameter.value) cellLayoutNameParameter.value else null
                )
            }
        }
    }
}