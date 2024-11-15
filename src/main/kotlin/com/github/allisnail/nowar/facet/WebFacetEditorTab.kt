package com.github.allisnail.nowar.facet

import com.github.allisnail.nowar.facet.model.WebModuleDeploymentDescriptor
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.table.AbstractTableModel

class WebFacetEditorTab(private val configuration: WebFacetConfiguration) : FacetEditorTab() {
    private val mainPanel: JPanel = JPanel(BorderLayout())
    private val descriptorsModel = DescriptorsTableModel()
    private val descriptorsTable = JBTable(descriptorsModel)
    private val resourcesModel = ResourcesTableModel()
    private val resourcesTable = JBTable(resourcesModel)
    private val sourceRootsListModel = DefaultListModel<String>()
    private val sourceRootsList = JBList<String>(sourceRootsListModel)

    init {
        val formBuilder = FormBuilder.createFormBuilder()
        
        // Deployment Descriptors section
        formBuilder.addComponent(TitledSeparator("Deployment Descriptors"))
        formBuilder.addComponent(JBScrollPane(descriptorsTable))
        formBuilder.addComponent(createButtonPanel(descriptorsTable))

        // Web Resource Directories section
        formBuilder.addComponent(TitledSeparator("Web Resource Directories"))
        formBuilder.addComponent(JBScrollPane(resourcesTable))
        formBuilder.addComponent(createButtonPanel(resourcesTable))

        // Source Roots section
        formBuilder.addComponent(TitledSeparator("Source Roots"))
        val sourceRootsDecorator = ToolbarDecorator.createDecorator(sourceRootsList)
            .setAddAction { addSourceRoot() }
            .setEditAction { editSourceRoot() }
            .setRemoveAction { removeSourceRoot() }
            .createPanel()
        formBuilder.addComponent(sourceRootsDecorator)

        mainPanel.add(formBuilder.panel, BorderLayout.CENTER)
        
        reset() // Load initial data
    }

    private fun addSourceRoot() {
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
        val file = FileChooser.chooseFile(descriptor, null, null)
        if (file != null) {
            configuration.sourceRoots.add(file.path)
            updateSourceRootsList()
        }
    }

    private fun editSourceRoot() {
        val selectedIndex = sourceRootsList.selectedIndex
        if (selectedIndex < 0) return

        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
        val file = FileChooser.chooseFile(descriptor, null, null)
        if (file != null) {
            configuration.sourceRoots[selectedIndex] = file.path
            updateSourceRootsList()
        }
    }

    private fun removeSourceRoot() {
        val selectedIndex = sourceRootsList.selectedIndex
        if (selectedIndex < 0) return

        configuration.sourceRoots.removeAt(selectedIndex)
        updateSourceRootsList()
    }

    private fun updateSourceRootsList() {
        sourceRootsListModel.clear()
        configuration.sourceRoots.forEach { sourceRootsListModel.addElement(it) }
    }

    private fun createButtonPanel(table: JBTable): JPanel {
        val panel = JPanel()
        val addButton = JButton("Add")
        val editButton = JButton("Edit")
        val removeButton = JButton("Remove")

        addButton.addActionListener { onAdd(table) }
        editButton.addActionListener { onEdit(table) }
        removeButton.addActionListener { onRemove(table) }

        panel.add(addButton)
        panel.add(editButton)
        panel.add(removeButton)
        return panel
    }

    private fun onAdd(table: JBTable) {
        when (table.model) {
            is DescriptorsTableModel -> {
                val descriptor = WebModuleDeploymentDescriptor()
                if (editDescriptor(descriptor)) {
                    configuration.deploymentDescriptors.add(descriptor)
                    descriptorsModel.fireTableDataChanged()
                }
            }
            is ResourcesTableModel -> {
                val result = JOptionPane.showInputDialog(
                    mainPanel,
                    "Enter relative path:",
                    "Add Web Resource Directory",
                    JOptionPane.QUESTION_MESSAGE
                )
                if (result != null) {
                    configuration.webResourceDirectories[result] = ""
                    resourcesModel.fireTableDataChanged()
                }
            }
        }
    }

    private fun onEdit(table: JBTable) {
        val row = table.selectedRow
        if (row < 0) return

        when (table.model) {
            is DescriptorsTableModel -> {
                val descriptor = configuration.deploymentDescriptors[row]
                if (editDescriptor(descriptor)) {
                    descriptorsModel.fireTableDataChanged()
                }
            }
            is ResourcesTableModel -> {
                val key = configuration.webResourceDirectories.keys.toList()[row]
                val result = JOptionPane.showInputDialog(
                    mainPanel,
                    "Edit relative path:",
                    "Edit Web Resource Directory",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    key
                )
                if (result != null) {
                    val value = configuration.webResourceDirectories[key] ?: ""
                    configuration.webResourceDirectories.remove(key)
                    configuration.webResourceDirectories[result.toString()] = value
                    resourcesModel.fireTableDataChanged()
                }
            }
        }
    }

    private fun onRemove(table: JBTable) {
        val row = table.selectedRow
        if (row < 0) return

        when (table.model) {
            is DescriptorsTableModel -> {
                configuration.deploymentDescriptors.removeAt(row)
                descriptorsModel.fireTableDataChanged()
            }
            is ResourcesTableModel -> {
                val key = configuration.webResourceDirectories.keys.toList()[row]
                configuration.webResourceDirectories.remove(key)
                resourcesModel.fireTableDataChanged()
            }
        }
    }

    private fun editDescriptor(descriptor: WebModuleDeploymentDescriptor): Boolean {
        val panel = JPanel()
        val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.layout = layout

        val typeField = JTextField(descriptor.type)
        val pathField = TextFieldWithBrowseButton()
        pathField.text = descriptor.path
        pathField.addBrowseFolderListener(
            "Select Deployment Descriptor",
            null,
            null,
            FileChooserDescriptor(true, false, false, false, false, false)
        )

        panel.add(JBLabel("Type:"))
        panel.add(typeField)
        panel.add(JBLabel("Path:"))
        panel.add(pathField)

        val result = JOptionPane.showConfirmDialog(
            mainPanel,
            panel,
            "Edit Deployment Descriptor",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        )

        if (result == JOptionPane.OK_OPTION) {
            descriptor.type = typeField.text
            descriptor.path = pathField.text
            return true
        }
        return false
    }

    override fun getDisplayName(): String = "Web"
    override fun createComponent(): JComponent = mainPanel

    override fun isModified(): Boolean {
        return true // For simplicity, always allow applying changes
    }

    override fun apply() {
        // Changes are applied immediately in the UI
    }

    override fun reset() {
        descriptorsModel.fireTableDataChanged()
        resourcesModel.fireTableDataChanged()
        updateSourceRootsList()
    }

    private inner class DescriptorsTableModel : AbstractTableModel() {
        private val columns = arrayOf("Type", "Path")

        override fun getRowCount(): Int = configuration.deploymentDescriptors.size
        override fun getColumnCount(): Int = 2
        override fun getColumnName(column: Int): String = columns[column]

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            val descriptor = configuration.deploymentDescriptors[rowIndex]
            return when (columnIndex) {
                0 -> descriptor.type
                1 -> descriptor.path
                else -> ""
            }
        }
    }

    private inner class ResourcesTableModel : AbstractTableModel() {
        private val columns = arrayOf("Relative Path", "Directory")

        override fun getRowCount(): Int = configuration.webResourceDirectories.size
        override fun getColumnCount(): Int = 2
        override fun getColumnName(column: Int): String = columns[column]

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            val entry = configuration.webResourceDirectories.entries.toList()[rowIndex]
            return when (columnIndex) {
                0 -> entry.key
                1 -> entry.value
                else -> ""
            }
        }
    }
} 