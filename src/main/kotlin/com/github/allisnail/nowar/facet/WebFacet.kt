package com.github.allisnail.nowar.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetConfiguration
import com.intellij.facet.FacetType
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.module.Module
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

