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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.components.sif.AskValue;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.LegendPicker;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class LegendList extends JPanel {
	private JToolBar toolBar;
	private JButton jButtonMenuAdd;
	private JButton jButtonMenuDel;
	private JButton jButtonMenuDown;
	private JButton jButtonMenuRename;
	private JButton jButtonMenuUp;

	private JList list;
	private LegendsPanel legendsPanel;
	private LegendModel model;
	private LegendListRenderer legendListRenderer;

	public LegendList(final LegendsPanel legendsPanel) {
		this.legendsPanel = legendsPanel;
		toolBar = new JToolBar();
		toolBar.setFloatable(false);

		jButtonMenuUp = new JButton();
		jButtonMenuUp.setIcon(OrbisGISIcon.GO_UP);
		jButtonMenuUp.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.up")); //$NON-NLS-1$
		jButtonMenuUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuUpActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuUp);

		jButtonMenuDown = new JButton();
		jButtonMenuDown.setIcon(OrbisGISIcon.GO_DOWN);
		jButtonMenuDown.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.down")); //$NON-NLS-1$
		jButtonMenuDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuDownActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuDown);

		jButtonMenuAdd = new JButton();
		jButtonMenuAdd.setIcon(OrbisGISIcon.PICTURE_ADD);
		jButtonMenuAdd.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.add")); //$NON-NLS-1$
		jButtonMenuAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuAddActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuAdd);

		jButtonMenuDel = new JButton();
		jButtonMenuDel.setIcon(OrbisGISIcon.PICTURE_DEL);
		jButtonMenuDel.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.delete")); //$NON-NLS-1$
		jButtonMenuDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuDelActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuDel);

		jButtonMenuRename = new JButton();
		jButtonMenuRename.setIcon(OrbisGISIcon.PICTURE_EDI);
		jButtonMenuRename.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.rename")); //$NON-NLS-1$
		jButtonMenuRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuRenameActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuRename);

		list = new JList();

		legendListRenderer = new LegendListRenderer(legendsPanel);

		list.setCellRenderer(legendListRenderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				jList1ValueChanged(evt);
				refresh();
			}
		});

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				Rectangle checkBoxBounds = legendListRenderer
						.getCheckBoxBounds();

				checkBoxBounds.translate((int) e.getX(), (int) e.getY());

				if (checkBoxBounds.contains(e.getPoint())) {

					Legend[] legends = legendsPanel.getLegends();

					Legend legend = legends[list.getSelectedIndex()];

					if (!legend.isVisible()) {
						legend.setVisible(true);
					} else {
						legend.setVisible(false);
					}

					list.repaint();

				}

			}
		});

		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.PAGE_START);
		this.add(new JScrollPane(list), BorderLayout.CENTER);

		model = new LegendModel();
		list.setModel(model);
		refresh();

		if (model.getSize() > 0) {
			list.setSelectedIndex(0);
		}
	}

	/**
	 * Refresh the state of the menu buttons
	 */
	public void refresh() {
		model.refresh();

		if (list.getModel().getSize() == 0) {
			list.clearSelection();
		}
		int idx = list.getSelectedIndex();
		int maximo = list.getModel().getSize() - 1;
		int minimo = 0;

		if (idx == -1) {
			jButtonMenuUp.setEnabled(false);
			jButtonMenuDown.setEnabled(false);
			jButtonMenuDel.setEnabled(false);
			jButtonMenuRename.setEnabled(false);
		} else {
			jButtonMenuDel.setEnabled(true);
			jButtonMenuRename.setEnabled(true);
			if (idx == minimo) {
				if (idx == maximo)
					jButtonMenuDown.setEnabled(false);
				else
					jButtonMenuDown.setEnabled(true);
				jButtonMenuUp.setEnabled(false);
			} else {
				if (idx == maximo) {
					jButtonMenuUp.setEnabled(true);
					jButtonMenuDown.setEnabled(false);
				} else {
					jButtonMenuUp.setEnabled(true);
					jButtonMenuDown.setEnabled(true);
				}
			}
		}

	}

	/**
	 * rename of a selected value in the list.
	 *
	 * @param evt
	 */
	private void jButtonMenuRenameActionPerformed(ActionEvent evt) {// GEN-FIRST:
		// event_jButtonMenuRenameActionPerformed
		String legendName = (String) list.getSelectedValue();
		int idx = list.getSelectedIndex();

		AskValue ask = new AskValue(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.insertNewName"), "txt is not null", //$NON-NLS-1$ //$NON-NLS-2$
				I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.aNameMustBeSpecified"), legendName); //$NON-NLS-1$
		String newName = ""; //$NON-NLS-1$
		if (UIFactory.showDialog(ask)) {
			newName = ask.getValue();
			legendsPanel.legendRenamed(idx, newName);
		}
	}

	/**
	 * remove a selected values
	 *
	 * @param evt
	 */
	private void jButtonMenuDelActionPerformed(ActionEvent evt) {
		int idx = list.getSelectedIndex();
		int size = list.getModel().getSize();
		if (list.getSelectedIndex() >= size - 1) {
			list.setSelectedIndex(Math.min(list.getSelectedIndex() - 1, 0));
		}
		legendsPanel.legendRemoved(idx);
	}

	/**
	 * adds a new legend
	 *
	 * @param evt
	 */
	private void jButtonMenuAddActionPerformed(ActionEvent evt) {// GEN-FIRST:
		// event_jButtonMenuAddActionPerformed
		ArrayList<String> paneNames = new ArrayList<String>();
		ArrayList<ILegendPanel> ids = new ArrayList<ILegendPanel>();
		ILegendPanel[] legends = legendsPanel.getAvailableLegends();
		for (int i = 0; i < legends.length; i++) {
			ILegendPanel legendPanelUI = legends[i];
			if (legendPanelUI.acceptsGeometryType(legendsPanel
					.getGeometryType())) {
				paneNames.add(legendPanelUI.getLegend().getLegendTypeName());
				ids.add(legendPanelUI);
			}
		}
		LegendPicker legendPicker = new LegendPicker(paneNames
				.toArray(new String[paneNames.size()]), ids.toArray(new ILegendPanel[ids.size()]));

		if (UIFactory.showDialog(legendPicker)) {
			ILegendPanel panel = (ILegendPanel) legendPicker.getSelected();

			legendsPanel.legendAdded(panel);
		}
	}

	/**
	 * move down the selected legend
	 *
	 * @param evt
	 */
	private void jButtonMenuDownActionPerformed(ActionEvent evt) {
		int idx = list.getSelectedIndex();
		list.setSelectedIndex(idx + 1);
		legendsPanel.legendMovedDown(idx);
	}

	/**
	 * moves up the selected legend
	 *
	 * @param evt
	 */
	private void jButtonMenuUpActionPerformed(ActionEvent evt) {
		int idx = list.getSelectedIndex();
		list.setSelectedIndex(idx - 1);
		legendsPanel.legendMovedUp(idx);
	}

	/**
	 * selection of a new legend in the list. it will fire this event and will
	 * open the appropriate panel for these legend.
	 *
	 * @param evt
	 */
	private void jList1ValueChanged(ListSelectionEvent evt) {
		legendsPanel.legendSelected(list.getSelectedIndex());
	}

	/**
	 * Returns true
	 *
	 * @return
	 */
	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}

	private class LegendModel extends AbstractListModel implements ListModel {

		public Object getElementAt(int index) {
			String legendName = legendsPanel.getLegendsNames()[index];
			if ((legendName == null) || legendName.trim().length() == 0) {
				legendName = I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.noName"); //$NON-NLS-1$
			}
			return legendName;
		}

		public void refresh() {
			fireContentsChanged(this, 0, getSize() + 1);
		}

		public int getSize() {
			return legendsPanel.getLegendsNames().length;
		}

	}

}