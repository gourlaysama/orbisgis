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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.gvsig.remoteClient.wms.WMSClient;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * 
 * @author Antoine Gourlay
 * @author Erwan Bocher
 */
public class WPSConnectionPanel extends JPanel implements UIPanel {

        private static final I18n I18N = I18nFactory.getI18n(WPSConnectionPanel.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private static final String WPSServerFile = "wpsServerList.txt";
        private JComboBox cmbURLServer;
        private JLabel lblVersion;
        private JLabel lblTitle;
        private JTextArea txtDescription;
        private WPSClient client;
        private ArrayList<String> serverswps;
        private ProcessConfigurationPanel processConfigurationPanel;

        /**
         * The WMSConnectionPanel is the first panel used to specify the URL of the WMS server.
         * It shows some informations on the server.
         * @param configPanel 
         */
        public WPSConnectionPanel(ProcessConfigurationPanel processConfigurationPanel) {
                this.processConfigurationPanel = processConfigurationPanel;
                
                GridBagLayout gl = new GridBagLayout();
                this.setLayout(gl);
                GridBagConstraints c = new GridBagConstraints();

                // Connection panel
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 1;
                c.weighty = 0;
                JPanel pnlURL = new JPanel(new BorderLayout());
                pnlURL.setBorder(BorderFactory.createTitledBorder(I18N.tr("WPS server URL")));

                serverswps = loadWPSServers();
                cmbURLServer = new JComboBox(serverswps.toArray(new String[serverswps.size()]));
                cmbURLServer.setEditable(true);
                cmbURLServer.setMaximumSize(new Dimension(100, 20));

                pnlURL.add(cmbURLServer, BorderLayout.NORTH);
                JToolBar wmsBtnManager = new JToolBar();
                wmsBtnManager.setFloatable(false);
                wmsBtnManager.setOpaque(false);

                JButton btnConnect = new JButton(OrbisGISIcon.getIcon("server_connect"));
                btnConnect.setToolTipText(I18N.tr("Connect to the server."));
                btnConnect.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                connect();
                        }
                });
                btnConnect.setBorderPainted(false);

                JButton btnDelete = new JButton(OrbisGISIcon.getIcon("remove"));
                btnDelete.setToolTipText(I18N.tr("Delete the server connection."));
                btnDelete.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                String item = cmbURLServer.getSelectedItem().toString();
                                if (serverswps.contains(item)) {
                                        serverswps.remove(item);
                                        saveWPSServerFile();
                                }
                                cmbURLServer.removeItem(item);
                        }
                });
                btnDelete.setBorderPainted(false);

                JButton btnUpdate = new JButton(OrbisGISIcon.getIcon("arrow_refresh"));
                btnUpdate.setToolTipText(I18N.tr("Reload the server connection."));
                btnUpdate.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                                try {
                                        ArrayList<String> updateServersList = readWPSServerFile(WPSConnectionPanel.class.getResourceAsStream(WPSServerFile));

                                        for (String updatewms : updateServersList) {
                                                if (!serverswps.contains(updatewms)) {
                                                        serverswps.add(updatewms);
                                                        cmbURLServer.addItem(updatewms);
                                                }
                                        }
                                        saveWPSServerFile();
                                } catch (IOException e1) {
                                        LOGGER.error(I18N.tr("Cannot update and save the URL list"), e1);
                                }

                        }
                });
                btnUpdate.setBorderPainted(false);

                wmsBtnManager.add(btnConnect);
                wmsBtnManager.add(btnDelete);
                wmsBtnManager.add(btnUpdate);
                pnlURL.add(wmsBtnManager, BorderLayout.SOUTH);
                this.add(pnlURL, c);

                // Info panel
                c.fill = GridBagConstraints.BOTH;
                c.gridy = 1;
                c.weightx = 1;
                c.weighty = 1;
                JPanel pnlInfo = new JPanel();
                pnlInfo.setLayout(new BorderLayout());
                pnlInfo.setBorder(BorderFactory.createTitledBorder(I18N.tr("Information")));
                JPanel pnlNorth = new JPanel();
                pnlNorth.setLayout(new CRFlowLayout());
                lblVersion = new JLabel(I18N.tr("Version :"));
                lblTitle = new JLabel(I18N.tr("Nom :"));
                pnlNorth.add(lblVersion);
                pnlNorth.add(new CarriageReturn());
                pnlNorth.add(lblTitle);
                pnlInfo.add(pnlNorth, BorderLayout.NORTH);
                txtDescription = new JTextArea("\n\n\n\n\n\n");
                txtDescription.setEditable(false);
                txtDescription.setLineWrap(true);
                JScrollPane comp = new JScrollPane(txtDescription);
                pnlInfo.add(comp, BorderLayout.CENTER);
                this.add(pnlInfo, c);
        }

        /**
         * Load a list of servers stored in file in the current workspace if the
         * file doesn't exist a list of default URL is loaded.
         *
         * @return
         */
        private ArrayList<String> loadWPSServers() {
                CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                File file = new File(ws.getWorkspaceFolder() + File.separator + WPSServerFile);
                try {
                        if (file.exists()) {
                                return readWPSServerFile(new FileInputStream(file));
                        } else {
                                //return readWPSServerFile(WPSConnectionPanel.class.getResourceAsStream(WPSServerFile));
                                return new ArrayList<String>();
                        }
                } catch (IOException e) {
                         LOGGER.error(I18N.tr("Cannot load the list of WMS url"), e);
                }

                return null;
        }

        /**
         * Read the wms servers file list to populate the combobox
         *
         * @param layoutStream
         * @return
         * @throws IOException
         */
        private ArrayList<String> readWPSServerFile(InputStream layoutStream)
                throws IOException {
                ArrayList<String> serversList = new ArrayList<String>();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(layoutStream));

                String str;
                while ((str = bufferedReader.readLine()) != null) {
                        if (str.length() > 0) {
                                if (!serversList.contains(str)) {
                                        serversList.add(str);
                                }
                        }
                }
                bufferedReader.close();
                layoutStream.close();

                return serversList;
        }

        /**
         * A method to save the list of WMS url in a file located in the current
         * OrbisGIS workspace.
         */
        public void saveWPSServerFile() {
                try {
                        CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                        File file = new File(ws.getWorkspaceFolder() + File.separator + WPSServerFile);
                        PrintWriter pw = new PrintWriter(file);
                        for (String server : serverswps) {
                                pw.println(server);
                        }
                        pw.close();
                } catch (FileNotFoundException e) {
                        LOGGER.error(I18N.tr("Cannot save the list of WPS url"));
                }
        }

        /**
         * When the user click on the connect button a background job is started
         * to display some informations about the WMS server.
         */
        private void connect() {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new BackgroundJob() {

                        @Override
                        public void run(ProgressMonitor pm) {
                                String url = cmbURLServer.getSelectedItem().toString().trim();
                                try {
                                        getWPSClient(url);
                                        processConfigurationPanel.initialize();
                                } catch (IOException ex) {
                                        LOGGER.error(
                                                I18N.tr("orbisgis.errorMessages.wms.CannotGetCapabilities"
                                                + " " + url), ex);
                                }
                                processConfigurationPanel.initialize();
                        }

                        @Override
                        public String getTaskName() {
                                return I18N.tr("Connecting to the server...");
                        }
                });
        }

        /**
         * Replace an empty string by a null.
         * @param property
         * @return 
         */
        private String changeNullForEmptyString(String property) {
                if (property == null) {
                        return "";
                } else {
                        return property;
                }
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getTitle() {
                return I18N.tr("WPS server connection");
        }

        @Override
        public String validateInput() {
                if (client == null) {
                        return I18N.tr("Please connect to the WPS server");
                }

                return null;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        /**
         * Return the current WMSClient.
         * @return 
         */
        public WPSClient getWPSClient() {
                return client;
        }

        /**
         * Return a WMSClient corresponding to a URL.
         *
         * @param host
         * @return
         * @throws ConnectException
         * @throws IOException
         */
        public WPSClient getWPSClient(String host) throws IOException {
                if (client == null) {
                        client = new WPSClient(host);
                        processConfigurationPanel.setClient(client);
                        return client;
                }
                return client;
        }
}
