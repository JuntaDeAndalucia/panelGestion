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
package es.juntadeandalucia.panelGestion.negocio.utiles;

import java.net.URI;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.juntadeandalucia.panelGestion.negocio.vo.JARALayerVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RAActionVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;

public abstract class JARALayerWriter {
   
   public static Document writeDocument(JARALayerVO jaraLayer) throws ParserConfigurationException {
      
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      
      // LAYER CONFIGURATION ELEMENT
      Document doc = docBuilder.newDocument();
      Element layerConfiguration = doc.createElement("layer-configuration");
      doc.appendChild(layerConfiguration);
      
      // LAYER NAME
      Element layerNameEl = doc.createElement("layer-name");
      layerNameEl.appendChild(doc.createTextNode(jaraLayer.getName()));
      layerConfiguration.appendChild(layerNameEl);
      
      // DEVELOPER
      Element developerId = doc.createElement("developer-id");
      developerId.appendChild(doc.createTextNode("JdA"));
      layerConfiguration.appendChild(developerId);
      
      Element developerKey = doc.createElement("developer-key");
      developerKey.appendChild(doc.createTextNode("JdA"));
      layerConfiguration.appendChild(developerKey);
      
      // CONNECTOR
      Element connector = createConnectorElement(doc);
      layerConfiguration.appendChild(connector);
      
      // SOURCE
      Element source = createSourceElement(doc, jaraLayer.getTable().getSchema());
      layerConfiguration.appendChild(source);
      
      // TABLE NAME
      Element tableNameEl = doc.createElement("table_name");
      tableNameEl.appendChild(doc.createTextNode(jaraLayer.getTable().getName()));
      layerConfiguration.appendChild(tableNameEl);
      
      // EPSG
      Element epsg = doc.createElement("EPSG_consultas");
      epsg.appendChild(doc.createTextNode(
         Utils.getSRID(jaraLayer.getTable().getEpsg()).toString()));
      layerConfiguration.appendChild(epsg);
      
      // IMAGE URL
      String imageUrl = jaraLayer.getImageUrl();
      if (imageUrl == null) {
         imageUrl = "";
      }
      Element imageUrlEl = doc.createElement("col_imageURL");
      imageUrlEl.appendChild(doc.createTextNode(imageUrl));
      layerConfiguration.appendChild(imageUrlEl);
      
      // ATTRIBUTION
      String attribution = jaraLayer.getAttribution();
      if (attribution == null) {
         attribution = "";
      }
      Element attributionEl = doc.createElement("col_atribucion");
      attributionEl.appendChild(doc.createTextNode(attribution));
      layerConfiguration.appendChild(attributionEl);
      
      // TITLE
      String title = jaraLayer.getTitle();
      if (title == null) {
         title = "";
      }
      Element titleEl = doc.createElement("col_title");
      titleEl.appendChild(doc.createTextNode(title));
      layerConfiguration.appendChild(titleEl);
      
      // LINE 2
      String line2 = jaraLayer.getLine2();
      if (line2 == null) {
         line2 = "";
      }
      Element line2El = doc.createElement("col_line2");
      line2El.appendChild(doc.createTextNode(line2));
      layerConfiguration.appendChild(line2El);
      
      // LINE 3
      String line3 = jaraLayer.getLine3();
      if (line3 == null) {
         line3 = "";
      }
      Element line3El = doc.createElement("col_line3");
      line3El.appendChild(doc.createTextNode(line3));
      layerConfiguration.appendChild(line3El);
      
      // LINE 4
      String line4 = jaraLayer.getLine4();
      if (line4 == null) {
         line4 = "";
      }
      Element line4El = doc.createElement("col_line4");
      line4El.appendChild(doc.createTextNode(line4));
      layerConfiguration.appendChild(line4El);
      
      // TYPE
      /* puts always the value '1' because the layer 
       * is always Generic 2D.
       * 
       * From documentation:
       * The type of layer, determining how the client
       * should display the POIs: 1 = generic; 2= 2d and 3d objects in 3d space.
       * via  https://www.layar.com/documentation/browser/publishing-site/register-layer/
       */
      Element type = doc.createElement("col_type");
      type.appendChild(doc.createTextNode("1"));
      layerConfiguration.appendChild(type);
      
      // INDEX
      Element index = doc.createElement("col_donotindex");
      index.appendChild(doc.createTextNode(""));
      layerConfiguration.appendChild(index);
      
      // SHOW SMALL BIW
      Element showSmallBiw = doc.createElement("col_showsmallbiw");
      showSmallBiw.appendChild(doc.createTextNode("'showsmallbiw'"));
      layerConfiguration.appendChild(showSmallBiw);
      
      // SHOW BIW ON CLICK
      Element showBiwOnClick = doc.createElement("col_showbiwonclick");
      showBiwOnClick.appendChild(doc.createTextNode("'showbiwonclick'"));
      layerConfiguration.appendChild(showBiwOnClick);
      
      // DIMENSION
      Element dimension = doc.createElement("col_dimension");
      dimension.appendChild(doc.createTextNode("1"));
      layerConfiguration.appendChild(dimension);
      
      // ACTIONS
      Element actions = createActionsElement(doc, jaraLayer.getActions());
      layerConfiguration.appendChild(actions);
      
      return doc;
   }

   private static Element createActionsElement(Document doc, List<RAActionVO> actions) {
      // actions
      Element actionsElement = doc.createElement("actions");
      
      for(RAActionVO action : actions) {
         // uri
         String field = action.getField();
         if (field != null) {
            // action
            Element actionElement = doc.createElement("action");
            
            Element uri = doc.createElement("uri");
            uri.appendChild(doc.createTextNode(action.getField()));
            actionElement.appendChild(uri);
         
            // label
            String label = action.getLabel();
            Element labelEl = doc.createElement("label");
            labelEl.appendChild(doc.createTextNode(label));
            actionElement.appendChild(labelEl);
         
            actionsElement.appendChild(actionElement);
         }
      }
      return actionsElement;
   }

   private static Element createSourceElement(Document doc, Schema schema) {
      String schemaName = schema.getName();
      if (schemaName == null) {
         schemaName = "";
      }
      String user = schema.getUser();
      if (user == null) {
         user = "";
      }
      String passwordStr = schema.getPassword();
      if (passwordStr == null) {
         passwordStr = "";
      }
      String connectionUrlString = schema.getDataBase().getConnectionUrl();
      connectionUrlString = connectionUrlString.substring(5);
      URI connectionUri = URI.create(connectionUrlString);
      String host = connectionUri.getHost();
      int port = connectionUri.getPort();
      String dataBase = schema.getDataBase().getAlias();
      
      Element source = doc.createElement("source");
      
      // dsn
      String dsnString = "pgsql:host=".concat(host).concat(";dbname=").concat(dataBase)
         .concat(";port=").concat(String.valueOf(port));
      Element dsn = doc.createElement("dsn");
      dsn.appendChild(doc.createTextNode(dsnString));
      source.appendChild(dsn);
      
      // TODO schema?
      Element schemaElement = doc.createElement("schema");
      schemaElement.appendChild(doc.createTextNode(schemaName));
      source.appendChild(schemaElement);
      
      // username
      Element username = doc.createElement("username");
      username.appendChild(doc.createTextNode(user));
      source.appendChild(username);
      
      // password
      Element password = doc.createElement("password");
      password.appendChild(doc.createTextNode(passwordStr));
      source.appendChild(password);
      
      return source;
   }

   private static Element createConnectorElement(Document doc) {
      Element connector = doc.createElement("connector");
      
      // name
      Element name = doc.createElement("name");
      name.appendChild(doc.createTextNode("JaraPostGisConnector"));
      connector.appendChild(name);
      
      // file
      Element file = doc.createElement("file");
      file.appendChild(doc.createTextNode("JaraPostGisConnector.class.php"));
      connector.appendChild(file);
      
      return connector;
   }
}
