package templates.tools

/**
 * 基本数据类型。
 */
enum class BaseType {
    Any, Boolean, String, Int, Long;

    companion object {
        fun isBaseType(type: kotlin.String): kotlin.Boolean {
            return values().any { it.name == type }
        }
    }
}