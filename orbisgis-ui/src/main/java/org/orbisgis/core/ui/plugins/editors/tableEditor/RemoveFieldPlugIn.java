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
package org.orbisgis.core.ui.plugins.editors.tableEditor;

import javax.swing.JOptionPane;

import org.orbisgis.core.sif.UIFactory;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class RemoveFieldPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor
				.getElement();
		try {
			DataSource dataSource = element.getDataSource();
			int option = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
					Names.POPUP_TABLE_DELETEFIELD_OPTION
							+ dataSource.getFieldName(getSelectedColumn())
							+ "?", Names.POPUP_TABLE_REMOVEFIELD_OPTION,
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				dataSource.removeField(getSelectedColumn());
			}
		} catch (DriverException e) {
			ErrorMessages.error(ErrorMessages.CannotDeleteField, e);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_REMOVEFIELD_PATH1 },
				Names.POPUP_TABLE_REMOVEFIELD_GROUP, false,
				OrbisGISIcon.TABLE_REMOVEFIELD, wbContext);
	}

	public boolean isEnabled() {
		IEditor editor = null;
		if ((editor = getPlugInContext().getTableEditor()) != null) {
			final TableEditableElement element = (TableEditableElement) editor
					.getElement();
				return (getSelectedColumn() != -1) && element.isEditable();
		}
		return false;
	}
}
