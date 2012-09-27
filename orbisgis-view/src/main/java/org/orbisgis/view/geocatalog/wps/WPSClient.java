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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Antoine Gourlay
 */
public class WPSClient {

        private String host;
        private List<WPSProcess> processes = new ArrayList<WPSProcess>();

        public WPSClient(String host) {
                this.host = host;

                init();
        }

        public String getHost() {
                return host;
        }

        WPSProcess getProcess(int index) {
                return processes.get(index);
        }

        int getProcessCount() {
                return processes.size();
        }

        int getIndex(Object child) {
                return processes.indexOf(child);
        }
        
        WPSProcess getProcess(String id) {
                for (int i = 0; i < processes.size(); i++) {
                        if (processes.get(i).getId().equals(id)) {
                                return processes.get(i);
                        }
                }
                return null;
        }
        
        private void init() {

                String url = host + "?SERVICE=wps&AcceptVersion=1.0.0&REQUEST=GetCapabilities";
                try {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(url);
                        doc.getDocumentElement().normalize();

                        NodeList els = doc.getFirstChild().getChildNodes();
                        for (int i = 0; i < els.getLength(); i++) {
                                Node n = els.item(i);
                                if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("wps:ProcessOfferings")) {
                                        NodeList pr = n.getChildNodes();
                                        for (int j = 0; j < pr.getLength() - 1; j+=2) {
                                                Node p = els.item(i);
                                                if (p.getNodeType() == Node.ELEMENT_NODE) {
                                                        Element e = (Element) n;
                                                        String id = getTagValue("ows:Identifier", e);
                                                        String title = getTagValue("ows:Title", e);
                                                        String abstractText = getTagValue("ows:Abstract", e);
                                                        processes.add(new WPSProcess(id, title, abstractText));
                                                }
                                        }
                                }
                        }

                } catch (IOException ex) {
                } catch (ParserConfigurationException ex) {
                } catch (SAXException ex) {
                }

        }

        private static String getTagValue(String sTag, Element eElement) {
                NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

                Node nValue = (Node) nlList.item(0);

                return nValue.getNodeValue();
        }
}
