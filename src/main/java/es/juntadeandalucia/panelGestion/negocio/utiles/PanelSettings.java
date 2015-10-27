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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchConfigFileVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchDataImportFileVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFilesVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchInstanceVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchSchemaFileVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeoserverVO;
import es.juntadeandalucia.panelGestion.persistencia.utiles.Repository;

public class PanelSettings {
   
   private final static String CONFIGURATION_FILE_PATH = "panel-settings.xml";
   
   private final static Logger log = Logger.getLogger(PanelSettings.class);
   
   public static List<Repository> predefinedRepositories;
   public static Map<String, Integer> dataBaseTypes;
   public static List<String> projections;
   public static List<GeoserverVO> geoservers;
   public static Map<String, String> poolProperties;
   public static String nordirGeodirUrl;
   public static Integer taskProgressInterval;
   public static Integer taskLinesToSave;
   public static String mapeaUrl;
   public static String jaraUrl;
   public static String jaraConfigPath;
   public static GeosearchInstanceVO geosearchMaster;
   public static List<GeosearchInstanceVO> geosearchSlaves;
   public static Set<String> csvContentTypes;
   public static Set<String> shapeContentTypes;
   public static Set<String> compressedContentTypes;
   public static List<String> tiposBaseDatos;

   static {
      Document xmlConfiguration = readXMLConfigurationFile();
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      
      try {
         readPredefinedRepositories(xmlConfiguration);
         readDataBaseTypes(xmlConfiguration);
         readProjections(xmlConfiguration);
         readNordirGeodirUrl(xmlConfiguration, xpath);
         readPoolProperties(xmlConfiguration);
         readTaskConfiguration(xmlConfiguration);
         readGeoserverConfiguration(xmlConfiguration, xpath);
         readTiposBaseDatosConfiguration(xmlConfiguration, xpath);
         readMapeaConfiguration(xmlConfiguration);
         readJARAConfiguration(xmlConfiguration, xpath);
         readGeosearchConfiguration(xmlConfiguration, xpath);
         readContentTypesConfiguration(xmlConfiguration, xpath);
         // TODO more?
      }
      catch (XPathExpressionException e) {
         log.error("Error al leer el archivo de configuracion 'panel-settings.xml': "
            + e.getLocalizedMessage());
      }
   }

   private static void readPredefinedRepositories(Document xmlConfiguration) {
      predefinedRepositories = new LinkedList<Repository>();

      Element doc = xmlConfiguration.getDocumentElement();
      NodeList repositories = doc.getElementsByTagName("repository");

      // iterate over the repositories
      for (int i = 0; i < repositories.getLength(); i++) {
         // get the repository node
         Node repositoryNode = repositories.item(i);

         if (repositoryNode instanceof Element) {
            Element repositoryElem = (Element) repositoryNode;

            // get alias and url from the repository node
            String repositoryAlias = getStringNodeValue("alias", repositoryElem);
            String repositoryUrl = getStringNodeValue("url", repositoryElem);
            String repositoryUser = getStringNodeValue("user", repositoryElem);
            String repositoryPass = getStringNodeValue("password", repositoryElem);

            // create a repository
            Repository repository = new Repository();
            repository.setAlias(repositoryAlias);
            repository.setUrl(repositoryUrl);
            repository.setUser(repositoryUser);
            repository.setPassword(repositoryPass);

            predefinedRepositories.add(repository);
         }
      }
   }

   private static void readDataBaseTypes(Document xmlConfiguration) {
      dataBaseTypes = new HashMap<String, Integer>();

      Element doc = xmlConfiguration.getDocumentElement();
      NodeList nodesDataBaseTypes = doc.getElementsByTagName("dataBaseType");

      // iterate over the data base types
      for (int i = 0; i < nodesDataBaseTypes.getLength(); i++) {
         // get the data base type node
         Node dataBaseType = nodesDataBaseTypes.item(i);

         if (dataBaseType instanceof Element) {
            Element dataBaseTypeElem = (Element) dataBaseType;

            String dataBaseTypeLabel = getStringNodeValue("label", dataBaseTypeElem);
            String sqlTypeStr = getStringNodeValue("sqlType", dataBaseTypeElem);
            Integer sqlType = null;
            if (!StringUtils.isEmpty(sqlTypeStr)) {
               Field typeField;
               try {
                  typeField = Types.class.getDeclaredField(sqlTypeStr);
                  if (typeField != null) {
                     sqlType = typeField.getInt(null);
                  }
               }
               catch (SecurityException e) {
                  log.error("Error al intentar obtener los tipos de base de datos del fichero de configuración: "
                     + e.getLocalizedMessage());
               }
               catch (NoSuchFieldException e) {
                  log.error("Error al intentar obtener los tipos de base de datos del fichero de configuración: "
                     + e.getLocalizedMessage());
               }
               catch (IllegalArgumentException e) {
                  log.error("Error al intentar obtener los tipos de base de datos del fichero de configuración: "
                     + e.getLocalizedMessage());
               }
               catch (IllegalAccessException e) {
                  log.error("Error al intentar obtener los tipos de base de datos del fichero de configuración: "
                     + e.getLocalizedMessage());
               }
            }
            dataBaseTypes.put(dataBaseTypeLabel, sqlType);
         }
      }
   }

   private static void readProjections(Document xmlConfiguration) {
      projections = new LinkedList<String>();

      Element doc = xmlConfiguration.getDocumentElement();
      NodeList nodesProjection = doc.getElementsByTagName("projection");

      // iterate over the data base types
      for (int i = 0; i < nodesProjection.getLength(); i++) {
         // get the data base type node
         Node projectionNode = nodesProjection.item(i);

         if (projectionNode instanceof Element) {
            Element projectionElem = (Element) projectionNode;
            String projection = projectionElem.getTextContent();
            projections.add(projection);
         }
      }
   }

   private static void readNordirGeodirUrl(Document xmlConfiguration, XPath xpath) throws XPathExpressionException {
      nordirGeodirUrl = null;
      
      nordirGeodirUrl = (String) xpath.compile("/configuration/general/nordir-geodir-url/@value")
         .evaluate(xmlConfiguration, XPathConstants.STRING);
   }

   private static void readTaskConfiguration(Document xmlConfiguration) {
      taskProgressInterval = null;
      taskLinesToSave = null;

      Element doc = xmlConfiguration.getDocumentElement();

      // task progress interval
      NodeList taskProgressIntervalNodes = doc.getElementsByTagName("progressInterval");
      Node taskProgressIntervalNode = taskProgressIntervalNodes.item(0);
      if (taskProgressIntervalNode instanceof Element) {
         Element taskProgressIntervalElement = (Element) taskProgressIntervalNode;
         String taskProgressIntervalValue = taskProgressIntervalElement.getAttribute("value");
         if (!StringUtils.isEmpty(taskProgressIntervalValue)) {
            taskProgressInterval = Integer.parseInt(taskProgressIntervalValue);
         }
      }

      // task lines to save
      NodeList taskLinesToSaveNodes = doc.getElementsByTagName("linesToSave");
      Node taskLinesToSaveNode = taskLinesToSaveNodes.item(0);
      if (taskLinesToSaveNode instanceof Element) {
         Element taskLinesToSaveElement = (Element) taskLinesToSaveNode;
         String linesToSaveValue = taskLinesToSaveElement.getAttribute("value");
         if (!StringUtils.isEmpty(linesToSaveValue)) {
            taskLinesToSave = Integer.parseInt(linesToSaveValue);
         }
      }
   }

   private static void readPoolProperties(Document xmlConfiguration) {
      poolProperties = new HashMap<String, String>();

      Element doc = xmlConfiguration.getDocumentElement();
      NodeList jdbcPoolNodes = doc.getElementsByTagName("jdbc-pool");
      Node jdbcPoolNode = jdbcPoolNodes.item(0);

      if (jdbcPoolNode != null) {
         NodeList poolPropertiesNodes = jdbcPoolNode.getChildNodes();

         // iterate over the properties
         for (int i = 0; i < poolPropertiesNodes.getLength(); i++) {
            // get the pool property node
            Node poolPropertiesNode = poolPropertiesNodes.item(i);

            if (poolPropertiesNode instanceof Element) {
               Element poolPropertiesElement = (Element) poolPropertiesNode;

               String name = poolPropertiesElement.getLocalName();
               String value = poolPropertiesElement.getTextContent();

               if (!StringUtils.isEmpty(value)) {
                  poolProperties.put(name, value);
               }
            }
         }
      }
   }

   private static void readTiposBaseDatosConfiguration(Document xmlConfiguration, XPath xpath) throws XPathExpressionException {

	   	tiposBaseDatos = new LinkedList<String>();

	      Element doc = xmlConfiguration.getDocumentElement();
	      NodeList databasetype_aux = doc.getElementsByTagName("databaseType");

	      // iterate over the repositories
	      for (int i = 0; i < databasetype_aux.getLength(); i++) {
	    	 
	         Node databaseTypeNode = databasetype_aux.item(i);

	         if (databaseTypeNode instanceof Element) {
	            Element databaseTypeElem = (Element) databaseTypeNode;

	            // get alias and url from the repository node
	            NodeList databaseTypeChildren = databaseTypeElem.getChildNodes();
	            for (int a = 0; a < databaseTypeChildren.getLength(); a++) {
	            	
	            	Node childrenAux = databaseTypeChildren.item(a);
	            	if (childrenAux instanceof Element) {
	            		Element childrenAuxElement = (Element) childrenAux; 
	            		if (childrenAuxElement.getNodeName().equals("name")) {
	            			tiposBaseDatos.add(childrenAuxElement.getAttributeNode("value").getValue());
	            		}
	            	}
	            }
	         }
	         
	      }
	  
      
   }

   private static void readGeoserverConfiguration(Document xmlConfiguration, XPath xpath)
      throws XPathExpressionException {

      geoservers = new LinkedList<GeoserverVO>();

      Element doc = xmlConfiguration.getDocumentElement();
      NodeList geoserver_aux = doc.getElementsByTagName("geoserver");

      // iterate over the repositories
      for (int i = 0; i < geoserver_aux.getLength(); i++) {

         GeoserverVO geoserverAux = new GeoserverVO();

         Node geoserverNode = geoserver_aux.item(i);

         if (geoserverNode instanceof Element) {
            Element geoserverElem = (Element) geoserverNode;

            // get alias and url from the repository node
            NodeList geoserverChildren = geoserverElem.getChildNodes();
            for (int a = 0; a < geoserverChildren.getLength(); a++) {

               Node childrenAux = geoserverChildren.item(a);
               if (childrenAux instanceof Element) {
                  Element childrenAuxElement = (Element) childrenAux;
                  if (childrenAuxElement.getNodeName().equals("url")) {
                     geoserverAux.setGeoserverUrl(Utils.removeLastSlash(childrenAuxElement.getAttributeNode("value")
                        .getValue()));
                  }
                  if (childrenAuxElement.getNodeName().equals("user")) {
                     geoserverAux.setGeoserverUser(childrenAuxElement.getAttributeNode("value")
                        .getValue());
                  }
                  if (childrenAuxElement.getNodeName().equals("password")) {
                     geoserverAux.setGeoserverPassword(childrenAuxElement.getAttributeNode("value")
                        .getValue());
                  }
                  if (childrenAuxElement.getNodeName().equals("version")) {
                     NodeList childrenListVersionAux = childrenAuxElement.getChildNodes();
                     for (int b = 0; b < childrenListVersionAux.getLength(); b++) {
                        Node childrenVersionAux = childrenListVersionAux.item(b);
                        if (childrenVersionAux instanceof Element) {
                           Element childrenElementVersionAux = (Element) childrenVersionAux;
                           if (childrenElementVersionAux.getNodeName().equals("wms")) {
                              geoserverAux.setGeoserverWMSVersion(childrenElementVersionAux
                                 .getAttributeNode("value").getValue());
                           }
                           if (childrenElementVersionAux.getNodeName().equals("wfs")) {
                              geoserverAux.setGeoserverWFSVersion(childrenElementVersionAux
                                 .getAttributeNode("value").getValue());
                           }
                        }
                     }

                  }

               }

            }
            geoservers.add(geoserverAux);
         }

      }

   }
   
   
   private static void readMapeaConfiguration(Document xmlConfiguration) {
      mapeaUrl = null;

      StringBuilder mapeaUrlConfigBuilder = new StringBuilder();
      
      Element doc = xmlConfiguration.getDocumentElement();

      // mapea node
      NodeList mapeaNodes = doc.getElementsByTagName("mapea");
      Node mapeaNode = mapeaNodes.item(0);
      NodeList mapeaChildNodes = mapeaNode.getChildNodes();
      
      // child nodes
      for (int i = 0; i < mapeaChildNodes.getLength(); i++) {
         Node mapeaChildNode = mapeaChildNodes.item(i);
         if (mapeaChildNode.getNodeType() == Node.ELEMENT_NODE) {
            String tagName = mapeaChildNode.getNodeName();
            if (!StringUtils.isEmpty(tagName)) {
               if ("url".equalsIgnoreCase(tagName)) {
                  mapeaUrl = ((Element) mapeaChildNode).getAttribute("value");
                  mapeaUrl = Utils.removeLastSlash(mapeaUrl);
               }
               else {
                  String tagValue = ((Element) mapeaChildNode).getAttribute("value");
                  mapeaUrlConfigBuilder.append(tagName).append("=").append(tagValue);
                  mapeaUrlConfigBuilder.append("&");
               }
            }
         }
      }
      
      // removes last &
      mapeaUrlConfigBuilder.deleteCharAt(mapeaUrlConfigBuilder.length() - 1);
      
      mapeaUrl += mapeaUrlConfigBuilder.toString();
   }
   
   private static void readJARAConfiguration(Document xmlConfiguration, XPath xpath) throws XPathExpressionException {
      // jara url
      jaraUrl = (String) xpath.compile("/configuration/general/jara/url/@value")
         .evaluate(xmlConfiguration, XPathConstants.STRING);
      jaraUrl = Utils.removeLastSlash(jaraUrl);
      
      // jara config directory
      jaraConfigPath = (String) xpath.compile("/configuration/general/jara/config-dir/@value")
         .evaluate(xmlConfiguration, XPathConstants.STRING);
   }
   
   private static void readGeosearchConfiguration(Document xmlConfiguration, XPath xpath) throws XPathExpressionException {
      /**************************
       * MASTER
       **************************/
      Node masterNode = (Node) xpath.compile("/configuration/general/geosearch/master")
         .evaluate(xmlConfiguration, XPathConstants.NODE);
      geosearchMaster = readGeosearchNode(masterNode);
      
      if (geosearchMaster != null) {
         geosearchMaster.setMaster(true);
         // sets files
         GeosearchFilesVO geosearchFilesVO = readGeosearchMasterFiles(xmlConfiguration, xpath);
         geosearchMaster.setFiles(geosearchFilesVO);
      }
      
      /*************************
       * SLAVES
       *************************/
      geosearchSlaves = new LinkedList<GeosearchInstanceVO>();

      NodeList slavesNodes = (NodeList) xpath.compile("/configuration/general/geosearch/slaves/slave")
         .evaluate(xmlConfiguration, XPathConstants.NODESET);
      for (int i = 0; i < slavesNodes.getLength(); i++) {
         Node slaveNode = slavesNodes.item(i);
         GeosearchInstanceVO slave = readGeosearchNode(slaveNode);
         if (slave != null) {
            slave.setMaster(false);
            geosearchSlaves.add(slave);
         }
      }
   }
   
   /**
    * TODO
    *
    * @param slaveNode
    * @return
    */
   private static GeosearchInstanceVO readGeosearchNode(Node geosearchNode) {
      GeosearchInstanceVO instanceVO = null;
      if (geosearchNode.getNodeType() == Node.ELEMENT_NODE) {
         Element geosearchlem = ((Element) geosearchNode);
         String url = geosearchlem.getAttribute("url");
         url = Utils.removeLastSlash(url);
         String user = geosearchlem.getAttribute("user");
         String password = geosearchlem.getAttribute("password");
         String srs = geosearchlem.getAttribute("srs");
         
         instanceVO = new GeosearchInstanceVO();
         instanceVO.setUrl(url);
         instanceVO.setUser(user);
         instanceVO.setPassword(password);
         instanceVO.setSRS(srs);
      }
      return instanceVO;
   }

   /**
    * TODO
    *
    * @return
    * @throws XPathExpressionException 
    */
   private static GeosearchFilesVO readGeosearchMasterFiles(Document xmlConfiguration, XPath xpath) throws XPathExpressionException {
      GeosearchFilesVO geosearchFilesVO = new GeosearchFilesVO();
      
      // DATAIMPORT
      String dataImportName = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/dataImport/@name").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String dataImportJavaClass = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/dataImport/@class").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String dataImportFileName = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/dataImport/@fileName").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String dataImportType = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/dataImport/@type").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String dataImportDriver = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/dataImport/@driver").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String dataImportTransformer = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/dataImport/@transformer").evaluate(
            xmlConfiguration, XPathConstants.STRING);
      
      GeosearchDataImportFileVO dataImportFileVO = new GeosearchDataImportFileVO();
      dataImportFileVO.setName(dataImportName);
      dataImportFileVO.setJavaClass(dataImportJavaClass);
      dataImportFileVO.setFileName(dataImportFileName);
      dataImportFileVO.setType(dataImportType);
      dataImportFileVO.setDriver(dataImportDriver);
      dataImportFileVO.setTransformer(dataImportTransformer);
      
      // CONFIG  
      String configFileName = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/config/@fileName").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String searchHandler = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/config/@searchHandler").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      GeosearchConfigFileVO configFileVO = new GeosearchConfigFileVO();
      configFileVO.setFileName(configFileName);
      configFileVO.setSearchHandler(searchHandler);
      
      // SCHEMA
      String schemaFileName = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/schema/@fileName").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      String defaultSearchField = (String) xpath.compile(
         "/configuration/general/geosearch/master/files/schema/@defaultSearchField").evaluate(xmlConfiguration,
            XPathConstants.STRING);
      GeosearchSchemaFileVO schemaFileVO = new GeosearchSchemaFileVO();
      schemaFileVO.setFileName(schemaFileName);
      schemaFileVO.setDefaultSearchField(defaultSearchField);
      
      geosearchFilesVO.setDataImport(dataImportFileVO);
      geosearchFilesVO.setConfig(configFileVO);
      geosearchFilesVO.setSchema(schemaFileVO);
      
      return geosearchFilesVO;
   }

   private static void readContentTypesConfiguration(Document xmlConfiguration, XPath xpath) throws XPathExpressionException {
      csvContentTypes = new HashSet<String>();
      shapeContentTypes = new HashSet<String>();
      compressedContentTypes = new HashSet<String>();
      
      // CSV
      NodeList csvContentTypeNodes = (NodeList) xpath.compile("/configuration/files[@type='csv']/content-type")
         .evaluate(xmlConfiguration, XPathConstants.NODESET);
      for (int i = 0; i < csvContentTypeNodes.getLength(); i++) {
         Node csvContentTypeNode = csvContentTypeNodes.item(i);
         String csvContentType = csvContentTypeNode.getTextContent().toLowerCase();
         csvContentTypes.add(csvContentType);
      }
      
      // SHAPE
      NodeList shapeContentTypeNodes = (NodeList) xpath.compile("/configuration/files[@type='shape']/content-type")
         .evaluate(xmlConfiguration, XPathConstants.NODESET);
      for (int i = 0; i < shapeContentTypeNodes.getLength(); i++) {
         Node shapeContentTypeNode = shapeContentTypeNodes.item(i);
         String shapeContentType = shapeContentTypeNode.getTextContent().toLowerCase();
         shapeContentTypes.add(shapeContentType);
      }
      
      // COMPRESSED
      NodeList compressedContentTypeNodes = (NodeList) xpath.compile("/configuration/files[@type='compressed']/content-type")
         .evaluate(xmlConfiguration, XPathConstants.NODESET);
      for (int i = 0; i < compressedContentTypeNodes.getLength(); i++) {
         Node compressedContentTypeNode = compressedContentTypeNodes.item(i);
         String compressedContentType = compressedContentTypeNode.getTextContent().toLowerCase();
         compressedContentTypes.add(compressedContentType);
      }
   }
   
   private static Document readXMLConfigurationFile() {
      Document xmlFile;

      InputStream xmlFileStream = PanelSettings.class.getClassLoader().getResourceAsStream(
         CONFIGURATION_FILE_PATH);

      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         xmlFile = db.parse(xmlFileStream);
      }
      catch (IOException e) {
         log.error("Error al leer el archivo de configuracion. Detalle: " + e.getLocalizedMessage());
         e.printStackTrace();
         xmlFile = null;
      }
      catch (ParserConfigurationException e) {
         log.error("Error al parsear el archivo de configuracion. Detalle: "
            + e.getLocalizedMessage());
         e.printStackTrace();
         xmlFile = null;
      }
      catch (SAXException e) {
         log.error("Error al parsear el archivo de configuracion. Detalle: "
            + e.getLocalizedMessage());
         e.printStackTrace();
         xmlFile = null;
      }

      return xmlFile;
   }

   private static String getStringNodeValue(String nodeName, Element node) {
      String value = null;

      NodeList nodeChildren = node.getElementsByTagName(nodeName);
      for (int i = 0; i < nodeChildren.getLength(); i++) {
         Node child = nodeChildren.item(i);
         if (child instanceof Element) {
            Element childElem = (Element) child;
            value = childElem.getTextContent();
            break;
         }
      }

      return value;
   }
   
   
   

}
