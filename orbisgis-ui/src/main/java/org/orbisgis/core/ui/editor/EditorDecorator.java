/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.editor;

import java.awt.Component;

import javax.swing.Icon;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.utils.I18N;

public class EditorDecorator implements IEditor {

	private IEditor editor;
	private Icon icon;
	private String id;

	public EditorDecorator(IEditor editor, Icon icon, String id) {
		this.editor = editor;
		this.icon = icon;
		this.id = id;
	}

	public boolean acceptElement(String typeId) {
		return editor.acceptElement(typeId);
	}

	public void delete() {
		getView().delete();
	}

	public Component getComponent() {
                final ViewPlugIn view = getView();
                if (view == null) {
                        throw new org.gdms.data.ClosedDataSourceException(I18N.getString("orbisgis.org.orbisgis.ui.editorDecorator.sourceClosedOrDoesntExist")); //$NON-NLS-1$
                }
		return view.getComponent();
	}

	public void loadStatus() throws PersistenceException {
		getView().loadStatus();
	}

	public void saveStatus() throws PersistenceException {
		getView().saveStatus();
	}

	public void setElement(EditableElement object) {
		editor.setElement(object);
	}

	public String getTitle() {
		return editor.getTitle();
	}

	public Icon getIcon() {
		return icon;
	}

	public EditableElement getElement() {
		return editor.getElement();
	}

	public IEditor getEditor() {
		return editor;
	}

	public String getId() {
		return id;
	}

	public ViewPlugIn getView() {
		return editor.getView();
	}

	@Override
	public void initialize(PlugInContext wbContext)	throws Exception {
	}

}
