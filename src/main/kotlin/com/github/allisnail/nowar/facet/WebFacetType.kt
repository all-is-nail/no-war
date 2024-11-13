package com.github.allisnail.nowar.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.facet.FacetTypeId
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import javax.swing.Icon

class WebFacetType : FacetType<WebFacet, WebFacetConfiguration>(ID, STRING_ID, "Web") {

    companion object {
        val ID = FacetTypeId<WebFacet>(WebFacet.FACET_TYPE_ID)
        const val STRING_ID = "web"
        
        val INSTANCE: WebFacetType
            get() = FacetType.findInstance(WebFacetType::class.java)
    }

    override fun createFacet(
        module: Module,
        name: String,
        configuration: WebFacetConfiguration,
        underlyingFacet: Facet<*>?
    ): WebFacet {
        return WebFacet(this, module, name, configuration, underlyingFacet)
    }

    override fun createDefaultConfiguration(): WebFacetConfiguration {
        return WebFacetConfiguration()
    }

    override fun isSuitableModuleType(moduleType: ModuleType<*>?): Boolean {
        return moduleType?.id == "JAVA_MODULE"
    }
} 