package org.orbisgis.view.sqlconsole.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.wps.ProcessConfigurationPanel;
import org.orbisgis.view.geocatalog.wps.WPSClient;
import org.orbisgis.view.geocatalog.wps.WPSConnectionPanel;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author alexis
 */
public class PostDataDialog extends JPanel implements UIPanel {

    private Logger LOGGER = Logger.getLogger(PostDataDialog.class);
    private I18n I18N = I18nFactory.getI18n(PostDataDialog.class);
    private String script;
    private JComboBox cmbURLServer;
    private WPSClient client;
    private ProcessConfigurationPanel processConfigurationPanel;
    private static final String WPSServerFile = "wpsServerList.txt";
    private ArrayList<String> serverswps;

    public PostDataDialog(ProcessConfigurationPanel processConfigurationPanel, SQLConsolePanel scp) {
        super();
        this.processConfigurationPanel = processConfigurationPanel;
        script = scp.getText();
        initialize();
    }

    public String getScript() {
        return script;
    }

    public WPSClient getClient() {
        return client;
    }

    private void initialize() {
        GridLayout gl = new GridLayout(2, 2);
        this.setLayout(gl);
        JPanel pnlURL = new JPanel(new BorderLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Connection panel
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        pnlURL.setBorder(BorderFactory.createTitledBorder(I18N.tr("WPS server URL")));
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
     * When the user click on the connect button a background job is started to
     * display some informations about the WMS server.
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

    @Override
    public URL getIconURL() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Post data to wps server";
    }

    @Override
    public String validateInput() {
        if (client == null) {
            return "Can't connect to the server";
        }
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
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
}
