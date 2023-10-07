package templates

import com.android.tools.idea.wizard.template.*

class PluginTemplatesProvider : WizardTemplateProvider() {
    override fun getTemplates(): List<Template> {
        return listOf(
            Templates.list(),
            Templates.normal()
        )
    }
}

