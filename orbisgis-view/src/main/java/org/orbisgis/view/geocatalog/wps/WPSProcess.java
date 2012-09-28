package org.orbisgis.view.geocatalog.wps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
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
                String[] templ = IOUtils.toString(inStr).split("@");
                File treq = File.createTempFile("gdms-t-req", ".xml");
                FileOutputStream rstr = new FileOutputStream(treq);
                IOUtils.write(templ[0], rstr);
                IOUtils.write(id, rstr);
                IOUtils.write(templ[1], rstr);
                for (int i = 0; i < in.size(); i++) {
                        File f = File.createTempFile("wps-in", ".json");
                        sm.exportTo(in.get(i), f);
                        String json = FileUtils.readFileToString(f);


                        IOUtils.write("<wps:Input><ows:Identifier>", rstr);
                        IOUtils.write(inputs.get(i), rstr);
                        IOUtils.write("</ows:Identifier><wps:Data><wps:ComplexData>", rstr);
                        IOUtils.write(json, rstr);
                        IOUtils.write("</wps:ComplexData></wps:Data></wps:Input>", rstr);
                        IOUtils.write("\n", rstr);

                }
               IOUtils.write(templ[2], rstr);

                for (int i = 0; i < outputs.size(); i++) {
                        IOUtils.write("<wps:Output><ows:Identifier>", rstr);
                        IOUtils.write(outputs.get(i), rstr);
                        IOUtils.write("</ows:Identifier></wps:Output>\n", rstr);
                }

                IOUtils.write(templ[3], rstr);
                rstr.flush();
                rstr.close();
                
                HttpURLConnection c = (HttpURLConnection) new URL(host).openConnection();

                c.setRequestProperty("Content-Type", "text/xml");
                c.setDoOutput(true);
                c.setRequestMethod("POST");
                FileUtils.copyFile(treq, c.getOutputStream());
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
                                                        Element inp = (Element) p;
                                                        String paramId = getTagValue("ows:Identifier", inp);
                                                        String url = inp.getElementsByTagName("wps:Reference").item(0).getAttributes().getNamedItem("href").getTextContent();
                                                        File ft = File.createTempFile("gdms-down-", ".json");
                                                        FileUtils.copyURLToFile(new URI(host).resolve(url).toURL(), ft);
                                                        jsonDatas.put(paramId, ft);
                                                }

                                        }
                                }
                        }

                } catch (IOException ex) {
                } catch (ParserConfigurationException ex) {
                } catch (SAXException ex) {
                } catch (URISyntaxException ex) {
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
