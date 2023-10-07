package templates.tools

import com.android.tools.idea.wizard.template.Constraint
import com.android.tools.idea.wizard.template.StringParameter
import com.android.tools.idea.wizard.template.stringParameter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 获取当前时间
 */
val time: String
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())

fun packageToPath(packageName: String): String {
    return if (packageName.isBlank()) {
        ""
    } else {
        packageName.split(".").joinToString("/")
    }
}

fun humpToUnderline(param: String): String {
    return if (param.isBlank()) {
        ""
    } else {
        StringBuilder().apply {
            param.forEachIndexed { i, char ->
                if (Character.isUpperCase(char)) {
                    if (i > 0) {
                        append('_')
                    }
                    append(Character.toLowerCase(char))
                } else {
                    append(char)
                }
            }
        }.toString()
    }
}

val defPackageNameParameter: StringParameter
    get() = stringParameter {
        name = "Package name"
        visible = { !isNewModule }
        default = "com.github.uikit"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }
