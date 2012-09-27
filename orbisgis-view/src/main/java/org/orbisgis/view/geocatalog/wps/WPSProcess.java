package org.orbisgis.view.geocatalog.wps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;

/**
 *
 * @author Antoine Gourlay
 */
public class WPSProcess {

        private String id;
        private String title;
        private String abstractText;
        private List<String> inputs = new ArrayList<String>();
        private List<String> outputs = new ArrayList<String>();

        public WPSProcess(String id, String title, String abstractText) {
                this.id = id;
                this.title = title;
                this.abstractText = abstractText;
        }

        public String getAbstractText() {
                return abstractText;
        }

        public String getId() {
                return id;
        }

        public String getTitle() {
                return title;
        }

        @Override
        public String toString() {
                return id + ": " + title;
        }

        public void describeProcess(String host) {
                String url = host + "?SERVICE=wps&AcceptVersion=1.0.0&REQUEST=DescribeProcess&IDENTIFIER=" + id;
                try {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(url);
                        doc.getDocumentElement().normalize();

                        NodeList els = doc.getFirstChild().getChildNodes();
                        for (int i = 0; i < els.getLength(); i++) {
                                Node n = els.item(i);
                                if (testNode(n, "ProcessDescription")) {
                                        NodeList pr = n.getChildNodes();
                                        for (int j = 1; j < pr.getLength() - 1; j += 2) {
                                                Node p = pr.item(j);
                                                if (testNode(p, "DataInputs")) {
                                                        NodeList inputNodes = p.getChildNodes();
                                                        for (int k = 1; k < inputNodes.getLength(); k += 2) {
                                                                Element inp = (Element) inputNodes.item(k);
                                                                String paramId = getTagValue("ows:Identifier", inp);
                                                                inputs.add(paramId);
                                                        }
                                                } else if (testNode(p, "ProcessOutputs")) {
                                                        NodeList inputNodes = p.getChildNodes();
                                                        for (int k = 1; k < inputNodes.getLength(); k += 2) {
                                                                Element inp = (Element) inputNodes.item(k);
                                                                String paramId = getTagValue("ows:Identifier", inp);
                                                                outputs.add(paramId);
                                                        }
                                                }

                                        }
                                }
                        }

                } catch (IOException ex) {
                } catch (ParserConfigurationException ex) {
                } catch (SAXException ex) {
                }
        }

        private boolean testNode(Node n, String name) {
                return n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name);
        }

        private static String getTagValue(String sTag, Element eElement) {
                NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

                Node nValue = (Node) nlList.item(0);

                return nValue.getNodeValue();
        }

        public List<String> getInputs() {
                return inputs;
        }

        public List<String> getOutputs() {
                return outputs;
        }

        public void execute(String host, List<String> in, List<String> out) throws IOException, DriverException, NoSuchTableException, DataSourceCreationException {
                SourceManager sm = Services.getService(DataManager.class).getSourceManager();

                InputStream inStr = WPSProcess.class.getResourceAsStream("wpsRequestTemplate.xml");
                String templ = IOUtils.toString(inStr);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < in.size(); i++) {
                        File f = File.createTempFile("wps-in", ".json");
                        sm.exportTo(in.get(i), f);
                        String json = FileUtils.readFileToString(f);


                        sb.append("<wps:Input><ows:Identifier>").append(inputs.get(i));
                        sb.append("</ows:Identifier><wps:Data><wps:ComplexData>");
                        sb.append(json);
                        sb.append("</wps:ComplexData></wps:Data></wps:Input>").append('\n');

                }
                templ = templ.replace("@inputs", sb.toString());

                sb = new StringBuilder();
                for (int i = 0; i < outputs.size(); i++) {
                        sb.append("<wps:Output><ows:Identifier>").append(outputs.get(i));
                        sb.append("</ows:Identifier></wps:Output>\n");
                }

                templ = templ.replace("@outputs", sb.toString());
                FileUtils.write(File.createTempFile("toto-", ".xml"), templ);
                HttpURLConnection c = (HttpURLConnection)new URL(host).openConnection();

                c.setRequestProperty("Content-Type", "text/xml");
                c.setDoOutput(true);
                c.setRequestMethod("POST");
                IOUtils.write(templ, c.getOutputStream());
                c.getOutputStream().close();
                c.connect();
                System.out.println(c.getResponseMessage());
                File response = File.createTempFile("gdms-wps-res", ".xml");
                FileUtils.copyInputStreamToFile(c.getInputStream(), response);

                Map<String, File> jsonDatas = new HashMap<String, File>();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                try {
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(response);
                        doc.getDocumentElement().normalize();

                        NodeList els = doc.getFirstChild().getChildNodes();
                        for (int i = 0; i < els.getLength(); i++) {
                                Node n = els.item(i);
                                if (testNode(n, "wps:ProcessOutputs")) {
                                        NodeList pr = n.getChildNodes();
                                        for (int j = 1; j < pr.getLength() - 1; j += 2) {
                                                Node p = pr.item(j);
                                                if (testNode(p, "wps:Output")) {
                                                        NodeList inputNodes = p.getChildNodes();
                                                        for (int k = 1; k < inputNodes.getLength(); k += 2) {
                                                                Element inp = (Element) inputNodes.item(k);
                                                                String paramId = getTagValue("ows:Identifier", inp);
                                                                String jsData = getTagValue("wps:Data", inp);
                                                                File ft = File.createTempFile("gdms-wps-out", ".json");
                                                                System.out.println(jsData);
                                                                FileUtils.write(ft, jsData);
                                                                jsonDatas.put(paramId, ft);
                                                        }
                                                }

                                        }
                                }
                        }

                } catch (IOException ex) {
                } catch (ParserConfigurationException ex) {
                } catch (SAXException ex) {
                }
                
                List<File> orderedOut = new ArrayList<File>();
                for (int i = 0; i < outputs.size(); i++) {
                        orderedOut.add(jsonDatas.get(outputs.get(i)));
                }
                
                for (int i = 0; i < out.size(); i++) {
                        sm.importFrom(out.get(i), orderedOut.get(i));
                }
        }
}
