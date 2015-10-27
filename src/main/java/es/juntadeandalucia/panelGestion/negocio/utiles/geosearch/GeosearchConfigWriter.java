/**
 * Empresa desarrolladora: GUADALTEL S.A.
 *
 * Autor: Junta de Andalucía
 *
 * Derechos de explotación propiedad de la Junta de Andalucía.
 *
 * Este programa es software libre: usted tiene derecho a redistribuirlo y/o modificarlo bajo los términos de la
 *
 * Licencia EUPL European Public License publicada por el organismo IDABC de la Comisión Europea, en su versión 1.0.
 * o posteriores.
 *
 * Este programa se distribuye de buena fe, pero SIN NINGUNA GARANTÍA, incluso sin las presuntas garantías implícitas
 * de USABILIDAD o ADECUACIÓN A PROPÓSITO CONCRETO. Para mas información consulte la Licencia EUPL European Public
 * License.
 *
 * Usted recibe una copia de la Licencia EUPL European Public License junto con este programa, si por algún motivo no
 * le es posible visualizarla, puede consultarla en la siguiente URL: http://ec.europa.eu/idabc/servlets/Doc?id=31099
 *
 * You should have received a copy of the EUPL European Public License along with this program. If not, see
 * http://ec.europa.eu/idabc/servlets/Doc?id=31096
 *
 * Vous devez avoir reçu une copie de la EUPL European Public License avec ce programme. Si non, voir
 * http://ec.europa.eu/idabc/servlets/Doc?id=30194
 *
 * Sie sollten eine Kopie der EUPL European Public License zusammen mit diesem Programm. Wenn nicht, finden Sie da
 * http://ec.europa.eu/idabc/servlets/Doc?id=29919
 */
/**
 * 
 */
package es.juntadeandalucia.panelGestion.negocio.utiles.geosearch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.juntadeandalucia.panelGestion.exception.GeosearchException;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchTableVO;
import es.juntadeandalucia.panelGestion.negocio.vo.XMLFileVO;

public class GeosearchConfigWriter extends GeosearchFileWriter {
   
   public GeosearchConfigWriter(List<GeosearchTableVO> tables, String core) throws GeosearchException, ParserConfigurationException {
      super(tables, core);
   }

   public XMLFileVO write() throws XPathExpressionException {
      addBoosts();
      if (!existsDataImport()) {
         addDataImportHandler();
      }
      
      XMLFileVO config = new XMLFileVO();
      config.setName(PanelSettings.geosearchMaster.getFiles().getConfig().getFileName());
      config.setXml(configXML);
      
      return config;
   }
   
   private void addBoosts() throws XPathExpressionException {
      // gets its qf node from the search requestHandler
      Node qf = getQFNode();
      String boosts = qf.getTextContent();
      
      // gets the new boosts from the new configured tables
      String newBoosts = getBoosts();
      
      boosts = boosts.concat("\n").concat(newBoosts).concat("\n");
      qf.setTextContent(boosts);
   }
   
   public String getBoosts() {
      StringBuilder boosts = new StringBuilder();
      
      // control set to avoid duplicated fields
      Set<String> addedFields = new HashSet<String>();
      for (GeosearchTableVO table : tables) {
         List<GeosearchFieldVO> fields = table.getFields();
         for (GeosearchFieldVO field : fields) {
            if (field.isIndexed() && field.isCopyToText()) {
               String fieldName = Utils.getFieldName(field);
               if (!addedFields.contains(fieldName)) {
                  String boost = field.getBoost();
                  boosts.append("\n ").append(fieldName).append("^").append(boost);
                  addedFields.add(fieldName);
               }
            }
         }
         boosts.append("\n");
      }
      
      return boosts.toString();
   }
   
   private void addDataImportHandler() throws XPathExpressionException {
      Node root = getConfigRootNode();
      Document rootDoc = root.getOwnerDocument();

      // creates requestHandler
      Element requestHandler = rootDoc.createElement("requestHandler");
      // attribute name
      requestHandler.setAttribute("name", PanelSettings.geosearchMaster.getFiles().getDataImport().getName());
      // attribute class
      requestHandler.setAttribute("class", PanelSettings.geosearchMaster.getFiles().getDataImport().getJavaClass());
      Node lastRequestHandler = getLastRequestHandler();
      if (lastRequestHandler != null) {
         root.insertBefore(requestHandler, lastRequestHandler);
      }
      else {
         root.appendChild(requestHandler);
      }
      
      // inserts comment
      insertDateComment(root, requestHandler);

      // defaults
      Element lst = rootDoc.createElement("lst");
      // name attribute
      lst.setAttribute("name", "defaults");
      requestHandler.appendChild(lst);

      // str child
      Element str = rootDoc.createElement("str");
      // name attribute
      str.setAttribute("name", "config");
      str.setTextContent(PanelSettings.geosearchMaster.getFiles().getDataImport().getFileName());
      lst.appendChild(str);
   }
   
   private boolean existsDataImport() {
      boolean exists = false;
      
      try {
         Node dataImportNode = (Node) xpath.compile("/config/requestHandler[@name='".concat(PanelSettings.geosearchMaster.getFiles().getDataImport().getName()).concat("']"))
            .evaluate(configXML, XPathConstants.NODE);
         exists = (dataImportNode != null);
      }
      catch (XPathExpressionException e) {
         exists = false;
      }
      
      return exists;
   }
   
   private Node getConfigRootNode() throws XPathExpressionException {
      Node rootNode = null;
      
      rootNode = (Node) xpath.compile("/config").evaluate(configXML, XPathConstants.NODE);
      
      return rootNode;
   }
   
   private Node getLastRequestHandler() throws XPathExpressionException {
      Node lastRequestHandler = null;
      
      lastRequestHandler = (Node) xpath.compile("(/config/requestHandler[@class='org.apache.solr.handler.dataimport.DataImportHandler'])[last()]").evaluate(configXML, XPathConstants.NODE);
      
      return lastRequestHandler;
   }
   
   private Node getQFNode() throws XPathExpressionException {
      Node qf = null;
      
      qf = (Node) xpath.compile("/config/requestHandler[@name='".concat(PanelSettings.geosearchMaster.getFiles().getConfig().getSearchHandler()).concat("'][1]/lst/str[@name='qf'][1]"))
         .evaluate(configXML, XPathConstants.NODE);
      
      return qf;
   }
}
