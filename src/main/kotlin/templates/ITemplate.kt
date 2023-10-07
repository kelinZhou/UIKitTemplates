package templates


interface ITemplate {
    /**
     * 包路径-当前模板类的包。
     */
    val packagePath: String

    /**
     * 页面名称。
     */
    val pageName: String

    /**
     * 描述。
     */
    val desc: String
}