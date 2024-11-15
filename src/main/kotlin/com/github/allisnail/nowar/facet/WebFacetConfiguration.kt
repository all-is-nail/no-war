package com.github.allisnail.nowar.facet

import com.github.allisnail.nowar.facet.model.WebModuleDeploymentDescriptor
import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

/**
 * Configuration class for Web Facet that stores and manages web module settings.
 * Implements FacetConfiguration to integrate with IntelliJ's facet framework.
 */
class WebFacetConfiguration : FacetConfiguration {
    // Default paths and configurations
    var webXmlPath: String = "src/main/webapp/WEB-INF/web.xml"
    var webRootPath: String = "src/main/webapp"
    var contextPath: String = "/"
    var sourceRoots: MutableList<String> = mutableListOf()
    var deploymentDescriptors: MutableList<WebModuleDeploymentDescriptor> = mutableListOf()
    var webResourceDirectories: MutableMap<String, String> = mutableMapOf("/" to "")
    
    /**
     * Creates editor tabs for the facet configuration UI
     */
    override fun createEditorTabs(
        editorContext: FacetEditorContext?,
        validatorsManager: FacetValidatorsManager?
    ): Array<FacetEditorTab> {
        return arrayOf(WebFacetEditorTab(this))
    }

    /**
     * Reads configuration from XML element
     * @param element XML element containing facet configuration
     * @throws InvalidDataException if data cannot be read properly
     */
    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        // Read basic configuration
        webXmlPath = element.getAttributeValue("web-xml-path") ?: webXmlPath
        webRootPath = element.getAttributeValue("web-root-path") ?: webRootPath
        contextPath = element.getAttributeValue("context-path") ?: contextPath
        
        // Read deployment descriptors
        element.getChild("deployment-descriptors")?.let { descriptors ->
            deploymentDescriptors.clear()
            for (descriptor in descriptors.getChildren("descriptor")) {
                deploymentDescriptors.add(WebModuleDeploymentDescriptor(
                    type = descriptor.getAttributeValue("type") ?: "",
                    path = descriptor.getAttributeValue("path") ?: ""
                ))
            }
        }

        // Read web resource directories
        element.getChild("web-resource-directories")?.let { directories ->
            webResourceDirectories.clear()
            for (dir in directories.getChildren("directory")) {
                val path = dir.getAttributeValue("path") ?: continue
                val relPath = dir.getAttributeValue("relative-path") ?: "/"
                webResourceDirectories[relPath] = path
            }
        }
        
        // Read source roots
        element.getChild("source-roots")?.let { roots ->
            sourceRoots.clear()
            for (root in roots.getChildren("root")) {
                root.getAttributeValue("path")?.let { sourceRoots.add(it) }
            }
        }
    }

    /**
     * Writes configuration to XML element
     * @param element XML element to write configuration to
     * @throws WriteExternalException if data cannot be written properly
     */
    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        // Write basic configuration
        element.setAttribute("web-xml-path", webXmlPath)
        element.setAttribute("web-root-path", webRootPath)
        element.setAttribute("context-path", contextPath)
        
        // Write deployment descriptors
        val descriptorsElement = Element("deployment-descriptors")
        for (descriptor in deploymentDescriptors) {
            val descriptorElement = Element("descriptor")
            descriptorElement.setAttribute("type", descriptor.type)
            descriptorElement.setAttribute("path", descriptor.path)
            descriptorsElement.addContent(descriptorElement)
        }
        element.addContent(descriptorsElement)

        // Write web resource directories
        val directoriesElement = Element("web-resource-directories")
        for ((relPath, path) in webResourceDirectories) {
            val dirElement = Element("directory")
            dirElement.setAttribute("path", path)
            dirElement.setAttribute("relative-path", relPath)
            directoriesElement.addContent(dirElement)
        }
        element.addContent(directoriesElement)
        
        // Write source roots
        val rootsElement = Element("source-roots")
        for (path in sourceRoots) {
            val rootElement = Element("root")
            rootElement.setAttribute("path", path)
            rootsElement.addContent(rootElement)
        }
        element.addContent(rootsElement)
    }
} 