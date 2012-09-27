/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.wps;

import org.orbisgis.view.geocatalog.sourceWizards.wms.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.gvsig.remoteClient.wms.WMSClient;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.components.button.JButtonTextField;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import org.gdms.source.SourceManager;

/**
 *
 * @author Erwan Bocher
 */
public class RunPanel extends JPanel implements UIPanel {

        private static final I18n I18N = I18nFactory.getI18n(RunPanel.class);
        private JPanel inputPanel;
        private JPanel outputPanel;
        private WPSClient client;
        private List<JComboBox> inCbs = new ArrayList<JComboBox>();
        private List<JTextField> outTx = new ArrayList<JTextField>();

        public void setClient(WPSClient client) {
                this.client = client;
        }

        /**
         * The SRSPanel lists all available SRS.
         *
         */
        public RunPanel() {
                initialize();
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        public List<String> getInputs() {
                ArrayList<String> s = new ArrayList<String>();
                for (int i = 0; i < inCbs.size(); i++) {
                        s.add(inCbs.get(i).getSelectedItem().toString());
                }
                
                return s;
        }

        public List<String> getOutputs() {
                ArrayList<String> s = new ArrayList<String>();
                for (int i = 0; i < outTx.size(); i++) {
                        s.add(outTx.get(i).getText().trim());
                }
                
                return s;
        }
        
        @Override
        public String getTitle() {
                return I18N.tr("Select a SRS");
        }

        /**
         * Create the SRSPanel with a populated list of supported SRS.
         */
        private void initialize() {
                this.setLayout(new GridLayout(2, 1));
                
                inputPanel = new JPanel();                
                outputPanel = new JPanel();
                
                JPanel j1 = new JPanel();
                j1.add(inputPanel);
                JPanel j2 = new JPanel();
                j2.add(outputPanel);
                this.add(j1);
                this.add(j2);
        }

        @Override
        public String validateInput() {
                for (int i = 0; i < inCbs.size(); i++) {
                        JComboBox c = inCbs.get(i);
                        if (c.getSelectedItem() == null || c.getSelectedItem().toString().trim().isEmpty()) {
                                return "Please populate all required fields!";
                        }
                }
                SourceManager sm = Services.getService(DataManager.class).getSourceManager();
                
                for (int i = 0; i < outTx.size(); i++) {
                        String name = outTx.get(i).getText();
                        if (sm.exists(name)) {
                                return "Please specify a non-existing table name!";
                        }
                }
                
                return null;
        }
        
        public void populatePanels(String id) {
                WPSProcess p = client.getProcess(id);
                SourceManager sm = Services.getService(DataManager.class).getSourceManager();
                if (p != null) {
                        p.describeProcess(client.getHost());
                        List<String> in = p.getInputs();
                        GridLayout g = new GridLayout(in.size(), 2);
                        inputPanel.setLayout(g);
                        
                        for (int i = 0; i < in.size(); i++) {
                                String name = in.get(i);
                                JLabel label = new JLabel(name + ": ");
                                JComboBox cb = new JComboBox();
                                String[] sNames = sm.getSourceNames();
                                for (int j = 0; j < sNames.length; j++) {
                                        if (!sm.getSource(sNames[j]).isSystemTableSource()) {
                                                cb.addItem(sNames[j]);
                                        }
                                }
                                inputPanel.add(label);
                                inputPanel.add(cb);
                                inCbs.add(cb);
                        }
                        List<String> out = p.getOutputs();
                        GridLayout g2 = new GridLayout(out.size(), 2);
                        outputPanel.setLayout(g2);
                        
                        for (int i = 0; i < out.size(); i++) {
                                String name = out.get(i);
                                JLabel label = new JLabel(name + ": ");
                                JTextField t = new JTextField(name);
                                inputPanel.add(label);
                                inputPanel.add(t);
                                outTx.add(t);
                        }
                }
        }

}
