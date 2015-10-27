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

import it.geosolutions.geoserver.rest.HTTPUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import es.juntadeandalucia.panelGestion.exception.GeosearchException;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchCopyFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchInstanceVO;

public abstract class Geosearch {
   
   private static final Logger log = Logger.getLogger(Geosearch.class);
   
   private static final String GET_CORES_URL = PanelSettings.geosearchMaster.getUrl()
      .concat("/admin/cores?action=STATUS&wt=json");
   private static final String CREATE_CORE_URL_TERM = "/admin/cores?action=CREATE&loadOnStartup=true&transient=false";   
   private static final String GET_FILE_TERM = "/admin/file?file=";
   private static final String GET_SCHEMA_FIELDTYPES_TERM = "/schema/fieldtypes?wt=json&indent=off";
   private static final String GET_SCHEMA_FIELDS_TERM = "/schema/fields?wt=json&indent=off";
   private static final String GET_SCHEMA_COPYFIELDS_TERM = "/schema/copyfields?wt=json&indent=off";
   private static final int GEOSEARCH_RESPONSE_OK_CODE = 0;
   
   public static List<String> getCores() throws GeosearchException {
      List<String> coreList = new ArrayList<String>();
      
      try {
         String getCoresJSONResponse = HTTPUtils.get(GET_CORES_URL, PanelSettings.geosearchMaster.getUser(), PanelSettings.geosearchMaster.getPassword());
         
         JSONObject jCores = new JSONObject(getCoresJSONResponse);
         JSONObject jStatus = jCores.getJSONObject("status");
         
         Iterator<String> cores = jStatus.keys();
         
         // List of the cores
         while(cores.hasNext()) {
            String core = cores.next();
            coreList.add(core);
         }
      }
      catch(Exception e) {
         throw new GeosearchException("Respuesta vacía o no válida");
      }
      
      return coreList;
   }

   public static Document getConfigFile(String core) throws GeosearchException {
      return getFile(core, PanelSettings.geosearchMaster.getFiles().getConfig().getFileName());
   }

   public static Document getSchemaFile(String core) throws GeosearchException {
      return getFile(core, PanelSettings.geosearchMaster.getFiles().getSchema().getFileName());
   }
   
   public static Document getDataImportFile(String core) throws GeosearchException {
      return getFile(core, PanelSettings.geosearchMaster.getFiles().getDataImport().getFileName());
   }
   
   public static List<String> getFieldTypes(String core) throws GeosearchException {
      List<String> fieldTypes = new LinkedList<String>();

      try {
         String getFieldTypesUrl = PanelSettings.geosearchMaster.getUrl().concat("/").concat(core)
            .concat(GET_SCHEMA_FIELDTYPES_TERM);
         String getFieldTypesJSONResponse = HTTPUtils.get(getFieldTypesUrl, PanelSettings.geosearchMaster.getUser(),
            PanelSettings.geosearchMaster.getPassword());

         JSONObject jResponse = new JSONObject(getFieldTypesJSONResponse);
         JSONArray jFieldTypes = jResponse.getJSONArray("fieldTypes");
         for (int i = 0; i < jFieldTypes.length(); i++) {
            JSONObject jFieldType = jFieldTypes.getJSONObject(i);
            String fieldType = jFieldType.getString("name");
            fieldTypes.add(fieldType);
         }
      }
      catch (Exception e) {
         throw new GeosearchException("Respuesta vacía o no válida");
      }

      return fieldTypes;
   }
   
   public static List<GeosearchFieldVO> getFields(String core) throws GeosearchException {
      List<GeosearchFieldVO> fields = new LinkedList<GeosearchFieldVO>();
      
      try {
         String getFieldsUrl = PanelSettings.geosearchMaster.getUrl().concat("/").concat(core)
            .concat(GET_SCHEMA_FIELDS_TERM);
         String getFieldsJSONResponse = HTTPUtils.get(getFieldsUrl, PanelSettings.geosearchMaster.getUser(),
            PanelSettings.geosearchMaster.getPassword());

         JSONObject jResponse = new JSONObject(getFieldsJSONResponse);
         JSONArray jFields = jResponse.getJSONArray("fields");
         for (int i = 0; i < jFields.length(); i++) {
            JSONObject jField = jFields.getJSONObject(i);
            String name = jField.getString("name");
            String type = jField.getString("type");
            // indexed
            boolean indexed = false;
            if (jField.has("indexed")) {
               indexed = jField.getBoolean("indexed");
            }
            // stored
            boolean stored = false;
            if (jField.has("stored")) {
               stored = jField.getBoolean("stored");
            }
            // multivalued
            boolean multivalued = false;
            if (jField.has("multiValued")) {
               multivalued = jField.getBoolean("multiValued");
            }
            
            GeosearchFieldVO field = new GeosearchFieldVO();
            field.setName(name);
            field.setType(type);
            field.setIndexed(indexed);
            field.setStored(stored);
            field.setMultivaluated(multivalued);
            
            fields.add(field);
         }
         setCopyFields(fields, core);
//         setBoosts(fields, core);
      }
      catch (Exception e) {
         throw new GeosearchException("Respuesta vacía o no válida");
      }
      
      return fields;
   }
   
   public static List<GeosearchCopyFieldVO> getCopyFields(String core) throws GeosearchException {
      List<GeosearchCopyFieldVO> copyFields = new LinkedList<GeosearchCopyFieldVO>();
      
      try {
         String getCopyFieldsUrl = PanelSettings.geosearchMaster.getUrl().concat("/").concat(core)
            .concat(GET_SCHEMA_COPYFIELDS_TERM);
         String getCopyFieldsJSONResponse = HTTPUtils.get(getCopyFieldsUrl, PanelSettings.geosearchMaster.getUser(),
            PanelSettings.geosearchMaster.getPassword());

         JSONObject jResponse = new JSONObject(getCopyFieldsJSONResponse);
         JSONArray jCopyFields = jResponse.getJSONArray("copyFields");
         for (int i = 0; i < jCopyFields.length(); i++) {
            JSONObject jCopyField = jCopyFields.getJSONObject(i);
            String source = jCopyField.getString("source");
            String dest = jCopyField.getString("dest");
            
            GeosearchCopyFieldVO copyField = new GeosearchCopyFieldVO();
            copyField.setSource(source);
            copyField.setDest(dest);
            
            copyFields.add(copyField);
         }
      }
      catch (Exception e) {
         throw new GeosearchException("Respuesta vacía o no válida");
      }
      
      return copyFields;
   }
   
   private static void setCopyFields(List<GeosearchFieldVO> fields, String core) throws GeosearchException {
      List<GeosearchCopyFieldVO> copyFields = getCopyFields(core);
      for (GeosearchFieldVO field : fields) {
         String fieldName = Utils.getFieldName(field);
         for (GeosearchCopyFieldVO copyField : copyFields) {
            if (copyField.getSource().equals(fieldName)) {
               field.setCopyToText(true);
               break;
            }
         }
      }
   }

   private static Document getFile(String core, String fileName) throws GeosearchException {
      Document file = null;
      
      String getFileUrl = PanelSettings.geosearchMaster.getUrl().concat("/").concat(core).concat(GET_FILE_TERM).concat(fileName);
      
      try {
         String getFileXML = HTTPUtils.get(getFileUrl, PanelSettings.geosearchMaster.getUser(), PanelSettings.geosearchMaster.getPassword());
         if (!StringUtils.isEmpty(getFileXML)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            file = db.parse(new ByteArrayInputStream(getFileXML.getBytes("UTF-8")));
         }
      }
      catch(Exception e) {
         throw new GeosearchException("Respuesta vacía o inválida");
      }
      
      return file;
   }

   public static boolean existsCore(String newCoreName) throws GeosearchException {
      boolean exists = false;
      List<String> cores = getCores();
      for (String core : cores) {
         if (core.equalsIgnoreCase(newCoreName)) {
            exists = true;
            break;
         }
      }
      return exists;
   }

   public static boolean createCore(String newCoreName) throws IOException, GeosearchException {
      
      boolean createdCore = false;
      
      // creates a core each defined instance
      // master
      createdCore = createCoreForInstance(newCoreName, PanelSettings.geosearchMaster);
      
      // slaves
      for (GeosearchInstanceVO slave : PanelSettings.geosearchSlaves) {
         createdCore = (createdCore && createCoreForInstance(newCoreName, slave));
         if (!createdCore) {
            break;
         }
      }
      
      return createdCore;
   }

   /**
    * TODO
    *
    * @param newCoreName
    * @param geosearchMaster
    * @return
    * @throws MalformedURLException 
    * @throws GeosearchException 
    */
   private static boolean createCoreForInstance(String newCoreName, GeosearchInstanceVO instance) throws MalformedURLException, GeosearchException {
      boolean createdCore = false;
      
      // create core url
      StringBuilder createCoreUrl = new StringBuilder(instance.getUrl());
      // create core
      createCoreUrl.append(Geosearch.CREATE_CORE_URL_TERM);
      // core
      createCoreUrl.append("&name=").append(newCoreName);
      // master
      createCoreUrl.append("&master=").append(String.valueOf(instance.isMaster()));
      
      String createCoreResponse = HTTPUtils.get(createCoreUrl.toString(),
         instance.getUser(), instance.getPassword());
      
      createdCore = checkCoreCreationResponse(createCoreResponse, newCoreName);
      
      return createdCore;
   }

   private static boolean checkCoreCreationResponse(String createCoreResponse, String coreName) throws GeosearchException {
      boolean createdCore = false;
      
      String errorMessage = null;
      
      try {
         // parses the response string as XML
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document createCoreResponseXML = db
            .parse(new ByteArrayInputStream(createCoreResponse.getBytes("UTF-8")));
         
         XPath xpath = XPathFactory.newInstance().newXPath();
         
         // gets the status, saved and the core nodes
         Node statusNode = (Node) xpath.compile("/response/lst/int[@name='status']")
            .evaluate(createCoreResponseXML, XPathConstants.NODE);
         Node coreNode = (Node) xpath.compile("/response/str[@name='core']")
            .evaluate(createCoreResponseXML, XPathConstants.NODE);
         Node savedNode = (Node) xpath.compile("/response/str[@name='saved']")
            .evaluate(createCoreResponseXML, XPathConstants.NODE);
         
         // checks if the status node has the correct value
         String statusString = statusNode.getTextContent();
         createdCore = (Integer.parseInt(statusString) == GEOSEARCH_RESPONSE_OK_CODE);
         
         // checks if the core node has the same value as the created core name
         String createdCoreName = coreNode.getTextContent();
         createdCore = (createdCore && coreName.equals(createdCoreName));
         
         // checks if the saved node is present
         createdCore = (createdCore && (savedNode != null));
      }
      catch (ParserConfigurationException e) {
         errorMessage = "Error al parsear el XML de respuesta";
      }
      catch (UnsupportedEncodingException e) {
         errorMessage = "Error en la codificación del XML de respuesta";
      }
      catch (SAXException e) {
         errorMessage = "Error al parsear el XML de respuesta";
      }
      catch (IOException e) {
         errorMessage = "Error al parsear el XML de respuesta";
      }
      catch (XPathExpressionException e) {
         errorMessage = "Respuesta de Geobúsquedas vacía o inválida";
      }
      
      if (errorMessage != null) {
         throw new GeosearchException(errorMessage);
      }
      
      return createdCore;
   }
}
