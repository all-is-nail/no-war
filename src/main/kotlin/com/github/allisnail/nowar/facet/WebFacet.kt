package com.github.allisnail.nowar.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.openapi.module.Module

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

