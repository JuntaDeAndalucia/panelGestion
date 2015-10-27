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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.juntadeandalucia.panelGestion.exception.GeosearchException;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchTableVO;

public class GeosearchFileWriter {

   protected List<GeosearchTableVO> tables;
   protected Document configXML;
   protected Document schemaXML;
   protected Document dataImportXML;
   protected XPath xpath;
   
   protected GeosearchFileWriter(List<GeosearchTableVO> tables, String core) throws GeosearchException, ParserConfigurationException {
      this.tables = tables;
      
      this.dataImportXML = Geosearch.getDataImportFile(core);
      // if it has not defined a dataImport then creates one with <dataConfig> tag
      if (this.dataImportXML == null) {
         DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
         this.dataImportXML = docBuilder.newDocument();
         // dataConfig element
         Element dataConfig = this.dataImportXML.createElement("dataConfig");
         this.dataImportXML.appendChild(dataConfig);
      }
      this.configXML = Geosearch.getConfigFile(core);
      this.schemaXML = Geosearch.getSchemaFile(core);
      
      this.xpath = XPathFactory.newInstance().newXPath();
      
   }
   
   protected void insertDateComment(Node parent, Element toElement) {
      Timestamp timeStamp = new Timestamp(new Date().getTime());
      String commentText = "ADDED at ".concat(timeStamp.toString())
         .concat(" FROM PANELSIGC");
      insertComment(parent, toElement, commentText);
   }
   
   protected void insertTableNameComment(Node parent, String tableName, Element toElement) {
      String commentText = "From table '".concat(tableName).concat("'");
      insertComment(parent, toElement, commentText);
   }
   
   protected void insertSchemaNameComment(Node parent, String schemaName, Element toElement) {
      String commentText = "From schema '".concat(schemaName).concat("'");
      insertComment(parent, toElement, commentText);
   }
   
   protected void insertComment(Node parent, Element toElement, String commentText) {
      Document doc = parent.getOwnerDocument();
      Comment comment = doc.createComment(commentText);
      parent.insertBefore(comment, toElement);
   }
}
