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

    public PostDataDialog(String script) {
        super();
        this.script = script;
        initialize();
    }

    public String getScript() {
        return script;
    }
    
    public String getUrl() {
            return cmbURLServer.getSelectedItem().toString().trim();
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

        cmbURLServer = new JComboBox();
        cmbURLServer.setEditable(true);
        cmbURLServer.setMaximumSize(new Dimension(100, 20));

        pnlURL.add(cmbURLServer, BorderLayout.NORTH);
        this.add(pnlURL, c);

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
        return new ArrayList<String>();
    }
}
