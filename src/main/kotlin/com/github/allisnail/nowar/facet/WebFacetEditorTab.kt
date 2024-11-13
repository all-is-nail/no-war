package com.github.allisnail.nowar.facet

import com.intellij.facet.ui.FacetEditorTab
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class WebFacetEditorTab(private val configuration: WebFacetConfiguration) : FacetEditorTab() {
    private val webXmlPathField = JBTextField(configuration.webXmlPath)
    private val webRootPathField = JBTextField(configuration.webRootPath)
    private val mainPanel: JPanel

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("web.xml Path:"), webXmlPathField)
            .addLabeledComponent(JBLabel("Web Root Path:"), webRootPathField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun getDisplayName(): String = "Web Application Settings"

    override fun createComponent(): JComponent = mainPanel

    override fun isModified(): Boolean {
        return webXmlPathField.text != configuration.webXmlPath ||
                webRootPathField.text != configuration.webRootPath
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        configuration.webXmlPath = webXmlPathField.text
        configuration.webRootPath = webRootPathField.text
    }

    override fun reset() {
        webXmlPathField.text = configuration.webXmlPath
        webRootPathField.text = configuration.webRootPath
    }
} 