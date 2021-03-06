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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.background.JobQueue;
import org.orbisgis.core.commandline.CommandLine;
import org.orbisgis.core.commandline.OptionSpec;
import org.orbisgis.core.commandline.ParseException;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.errorManager.DefaultErrorManager;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.translation.OrbisGISCoreI18N;
import org.orbisgis.core.ui.errors.CacheMessages;
import org.orbisgis.core.ui.errors.FilteringErrorListener;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.plugins.orbisgisFrame.configuration.EPConfigHelper;
import org.orbisgis.core.ui.plugins.orbisgisFrame.configuration.WorkspaceConfiguration;
import org.orbisgis.core.ui.preferences.translation.OrbisGISI18N;
import org.orbisgis.core.ui.workspace.DefaultSwingWorkspace;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.OrbisGISWorkspace;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.utils.I18N;

public class Main {

        private static Logger logger = Logger.getLogger(Main.class);
        private static CacheMessages cacheMessages = null;
        private static CommandLine commandLine;
        public final static String I18N_FILE = "i18n";
        public final static String CLEAN = "clean";
        public final static String WORKSPACE = "w";
        public static String I18N_SETLOCALE = "";
        public static int MIN_JAVA_VERSION = 6;
        public static String PLUGIN_DIRECTORY = "lib/ext";

        public static void main(String[] args) throws Exception {
                Splash splash = new Splash();

                if (!IsVersion()) {
                        JOptionPane.showMessageDialog(null, I18N.getString("orbisgis.main.version.error"));
                        splash.setVisible(false);
                        splash.dispose();
                } else {
                        initApplication(splash, args);

                }

        }

        private static void initApplication(Splash splash, String[] args)
                throws Exception {
                splash.setVisible(true);
                Splash.updateText("OrbisGIS services initialization.");
                initServices();
                Splash.updateText("OrbisGIS services initialization ready.");
                parseCommandLine(args);
                initI18n(splash);
                init(splash, args);
                splash.setVisible(false);
                splash.dispose();
        }

        private static void initI18n(Splash splash) {
                if (commandLine.hasOption(I18N_FILE)) {
                        I18N_SETLOCALE = commandLine.getOption(I18N_FILE).getArg(0);
                }
                // Init I18n
                I18N.addI18n(I18N_SETLOCALE, "orbisgis", OrbisGISI18N.class);
                I18N.addI18n(I18N_SETLOCALE, "orbisgis-core", OrbisGISCoreI18N.class);

        }

        private static boolean IsVersion() {
                String version = System.getProperty("java.version");
                String[] v = version.split("\\.");

                return  (v.length > 1 && "1".equals(v[0]) && Integer.parseInt(v[1]) >= MIN_JAVA_VERSION);
        }

        private static void init(Splash splash, String[] args) throws Exception {
                try {
                        initProperties();
                        splash.updateVersion();
                        Splash.updateText(I18N.getString("orbisgis.main.loading"));
                        Workspace wrsk = Services.getService(Workspace.class);

                        if (commandLine.hasOption(WORKSPACE)) {
                                wrsk.setWorkspaceFolder(commandLine.getOption(WORKSPACE).getArg(0));
                        }
                        if (commandLine.hasOption("pluginDir")) {
                                PLUGIN_DIRECTORY = commandLine.getOption("pluginDir").getArg(0);
                        }
                        wrsk.init(commandLine.hasOption(CLEAN));

                        // Install OrbisGIS core services
                        new OrbisGISWorkspace();
                        OrbisgisUIServices.installConfigurationService();
                        // Initialize configuration
                        EPConfigHelper.loadAndApplyConfigurations();

                        BasicConfiguration bc = Services.getService(BasicConfiguration.class);
                        String sTimer = bc.getProperty(WorkspaceConfiguration.getTimerProperty());
                        int iTimer = WorkspaceConfiguration.convert(sTimer);
                        if (iTimer > 0) {
                                WorkspaceConfiguration.startPeriodicSaving(iTimer);
                        }

                        initLogger();
                        logger.info("main.logger.start");
                        cacheMessages = new CacheMessages();
                        new FilteringErrorListener();
                        cacheMessages.printCacheMessages();
                } catch (Exception e) {
                        splash.setVisible(false);
                        splash.dispose();
                        Services.getErrorManager().error("Cannot init the application", e);
                }
        }

        /**
         * Install OrbisGIS core services
         */
        private static void initServices() {
                Services.registerService(
                        BackgroundManager.class,
                        "Execute tasks in background processes, "
                        + "showing progress bars. Gives access to the job queue",
                        new JobQueue());

                Services.registerService(ErrorManager.class,
                        "Notification of errors to the system",
                        new DefaultErrorManager());
                // Choose a workspace to start OrbisGIS
                DefaultWorkspace defaultWorkspace = new DefaultSwingWorkspace();
                Services.registerService(Workspace.class,
                        "Change workspace, save files in the workspace, etc.",
                        defaultWorkspace);
                ApplicationInfo applicationInfo = new OrbisGISApplicationInfo();
                Services.registerService(ApplicationInfo.class,
                        "Gets information about the application: "
                        + "name, version, etc.", applicationInfo);
                // Install OrbisGIS core services
                OrbisgisUIServices.installServices();

        }

        private static void initProperties() {
                PropertyConfigurator.configure(Main.class.getResource("log4j.properties"));
        }

        private static void initLogger() {
                PatternLayout l = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
                RollingFileAppender fa;
                try {
                        fa = new RollingFileAppender(l, Services.getService(
                                ApplicationInfo.class).getLogFile());
                        fa.setMaxFileSize("256KB");
                        Logger.getRootLogger().addAppender(fa);
                } catch (IOException e) {
                        Services.getErrorManager().error("Init logger failed!", e);
                }
        }

        private static void parseCommandLine(String[] args) throws ParseException {
                commandLine = new CommandLine('-');
                commandLine.addOptionSpec(new OptionSpec(I18N_FILE, 1));
                commandLine.addOptionSpec(new OptionSpec(WORKSPACE, 1));
                commandLine.addOptionSpec(new OptionSpec(CLEAN, 0));
                commandLine.addOptionSpec(new OptionSpec("pluginDir", 1));
                try {
                        commandLine.parse(args);
                } catch (ParseException e) {
                        ErrorMessages.error(ErrorMessages.CommandLineError, e);
                        throw e;
                }
        }
}
