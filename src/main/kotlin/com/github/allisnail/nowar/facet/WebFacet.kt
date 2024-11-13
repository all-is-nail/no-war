package com.github.allisnail.nowar.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetConfiguration
import com.intellij.facet.FacetType
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

class WebFacet(facetType: FacetType<WebFacet, WebFacetConfiguration>,
               module: Module,
               name: String,
               configuration: WebFacetConfiguration,
               underlyingFacet: Facet<*>?) 
    : Facet<WebFacetConfiguration>(facetType, module, name, configuration, underlyingFacet) {

    companion object {
        const val FACET_TYPE_ID = "web"
        const val FACET_NAME = "Web"
    }
}

class WebFacetConfiguration : FacetConfiguration {
    var webXmlPath: String = "src/main/webapp/WEB-INF/web.xml"
    var webRootPath: String = "src/main/webapp"

    override fun createEditorTabs(
        editorContext: FacetEditorContext?,
        validatorsManager: FacetValidatorsManager?
    ): Array<FacetEditorTab> {
        return arrayOf(WebFacetEditorTab(this))
    }

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        webXmlPath = element.getAttributeValue("web-xml-path") ?: webXmlPath
        webRootPath = element.getAttributeValue("web-root-path") ?: webRootPath
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        element.setAttribute("web-xml-path", webXmlPath)
        element.setAttribute("web-root-path", webRootPath)
    }
} 