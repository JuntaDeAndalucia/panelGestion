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

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.juntadeandalucia.panelGestion.exception.GeosearchException;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchTableVO;
import es.juntadeandalucia.panelGestion.negocio.vo.XMLFileVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.utiles.TableUtils;

public class GeosearchDataImportWriter extends GeosearchFileWriter {
   
   private Node dataConfig;
   
   public GeosearchDataImportWriter(List<GeosearchTableVO> tables, String core) throws GeosearchException, ParserConfigurationException, XPathExpressionException {
      super(tables, core);
      dataConfig = getDataConfigNode();
   }

   public XMLFileVO write() throws XPathExpressionException {
      // data sources elements
      addDataSources();
      
      // entity elements
      addEntities();
      
      XMLFileVO dataImport = new XMLFileVO();
      dataImport.setName(PanelSettings.geosearchMaster.getFiles().getDataImport().getFileName());
      dataImport.setXml(dataImportXML);
      
      return dataImport;
   }

   private Node getDocumentNode() throws XPathExpressionException {
      Node document = null;
      
      document = (Node) xpath.compile("/dataConfig/document").evaluate(dataImportXML, XPathConstants.NODE);
      if (document == null) {
         document = dataConfig.getOwnerDocument().createElement("document");
         dataConfig.appendChild(document);
      }
      
      return document;
   }

   private void addDataSources() throws XPathExpressionException, DOMException {
      Set<Schema> addedSchemas = new HashSet<Schema>();
      for (GeosearchTableVO geosearchTable : tables) {
         Schema schema = geosearchTable.getTable().getSchema();
         /*
          * creates datasource if it has not been added already and
          * it does not exist on the dataImport.xml
          */
         if (!addedSchemas.contains(schema) && !existsDataSource(schema)) {
            Element dataSource = generateDataSource(schema);
            dataConfig.appendChild(dataSource);
            insertSchemaNameComment(dataConfig, schema.getName(), dataSource);
            addedSchemas.add(schema);
         }
      }
   }
   
   private boolean existsDataSource(Schema schema) throws XPathExpressionException {
      boolean exists = false;
      
      String dataSourceName = getDataSourceName(schema);
      Node dataSource = getDataSource(dataSourceName);
      exists = (dataSource != null);
      
      return exists;
   }

   private void addEntities() throws XPathExpressionException {
      Node document = getDocumentNode();
      
      for (GeosearchTableVO geosearchTable : tables) {
         Element entity = generateEntity(geosearchTable, document);
         if (entity != null) {
            /*
             * checks if the entity already exists. In that case, duplicates the
             * entity with other name and adds a comment
             */
            boolean existsEntity = existsEntity(entity);
            if (existsEntity) {
               String entityName = entity.getAttribute("name");
               int numSameEntities = getNumOfSameEntities(entity);
               entityName = entityName.concat("_duplicated_").concat(String.valueOf(numSameEntities));
               entity.setAttribute("name", entityName);
            }
            document.appendChild(entity);
            insertTableNameComment(document, geosearchTable.getTable().getName(), entity);
            if (existsEntity) {
               String comment = "IMPORTANTE: Esta entidad ya se encuentra duplicada en el fichero dataImport."
                  .concat(" Revise la configuración ya que es posible que exista información duplicada");
               insertComment(document, entity, comment);
            }
         }
      }
   }

   private int getNumOfSameEntities(Element entity) throws XPathExpressionException {
      int numSameEntities = 0;
      
      String entityName = entity.getAttribute("name");
      NodeList entities = getEntities(entityName);
      if (entities != null) {
         numSameEntities = entities.getLength();
      }
      
      return numSameEntities;
   }

   private boolean existsEntity(Element entity) throws XPathExpressionException {
      return (getNumOfSameEntities(entity) > 0);
   }

   private NodeList getEntities(String entityName) throws XPathExpressionException {
      NodeList entities = null;
      
      entities = (NodeList) xpath.compile("/dataConfig/document/entity[@name='"
         .concat(entityName).concat("']")).evaluate(dataImportXML, XPathConstants.NODESET);
      
      return entities;
   }

   private Element generateEntity(GeosearchTableVO geosearchTable, Node document) {
      Element entity = null;
      
      Table table = geosearchTable.getTable();
      String dataSourceName = getDataSourceName(table.getSchema());
      String entityName = getEntityName(table);
      String query = getEntityQuery(geosearchTable);
      String keywords = geosearchTable.getKeywords();
      List<GeosearchFieldVO> fields = geosearchTable.getFields();
      
      // entity
      entity = document.getOwnerDocument().createElement("entity");
      entity.setAttribute("dataSource", dataSourceName);
      entity.setAttribute("name", entityName);
      entity.setAttribute("query", query);
      entity.setAttribute("transformer", PanelSettings.geosearchMaster.getFiles().getDataImport().getTransformer());
      if (!StringUtils.isEmpty(keywords)) {
         entity.setAttribute("keywords", keywords);
      }
      
      // fields
      String geomField = table.getGeomField();
      boolean hasDefinedField = false;
      for (GeosearchFieldVO field : fields) {
         if (field.isDefined()) {
            Element fieldElement = generateField(field, document, geomField);
            entity.appendChild(fieldElement);
            hasDefinedField = true;
         }
      }
      // adds the entity if it has defined fields
      if (!hasDefinedField) {
         entity = null;
      }
      return entity;
   }

   private Element generateField(GeosearchFieldVO field, Node document, String geomFieldName) {
      String column = field.getNameOnTable();
      String name = Utils.getFieldName(field);
      
      // uses the geometric field name as column name
      if ((geomFieldName != null) && geomFieldName.equals(column)) {
         column = name;
      }
      
      Element fieldElement = document.getOwnerDocument().createElement("field");
      fieldElement.setAttribute("column", column);
      fieldElement.setAttribute("name", name);
      
      return fieldElement;
   }

   private Element generateDataSource(Schema schema) {
      Element dataSource = null;
      
      String dataSourceName = getDataSourceName(schema);
      String user = schema.getUser();
      String password = schema.getPassword();
      String url = schema.getDataBase().getConnectionUrl();
      
      dataSource = dataConfig.getOwnerDocument().createElement("dataSource");
      dataSource.setAttribute("name", dataSourceName);
      dataSource.setAttribute("type", PanelSettings.geosearchMaster.getFiles().getDataImport().getType());
      dataSource.setAttribute("driver", PanelSettings.geosearchMaster.getFiles().getDataImport().getDriver());
      dataSource.setAttribute("url", url);
      dataSource.setAttribute("user", user);
      dataSource.setAttribute("password", password);
      
      return dataSource;
   }

   private String getDataSourceName(Schema schema) {
      DataBase dataBase = schema.getDataBase();
      return dataBase.getAlias().concat("_").concat(schema.getName());
   }
   
   private String getEntityName(Table table) {
      String dataBaseName = table.getSchema().getDataBase().getAlias();
      String schemaName = table.getSchema().getName();
      String tableName = table.getName();
      
      return dataBaseName.concat("_").concat(schemaName).concat("_").concat(tableName);
   }
   
   private String getEntityQuery(GeosearchTableVO tableVO) {
      StringBuilder query = new StringBuilder("SELECT *");
      
      Table table = tableVO.getTable();
      String geomFieldName = getGeomFieldName(tableVO);
      String tableGeomFieldName = table.getGeomField();
      
      /* 
       * if the table has different SRS applies a ST_Transform
       * @see http://postgis.net/docs/manual-2.0/ST_Transform.html
       */
      String tableSrs = table.getEpsg();
      if (!tableSrs.equals(PanelSettings.geosearchMaster.getSRS()) ||
            !StringUtils.isEmpty(geomFieldName)) {
         // comma
         query.append(", ");
         
         /* 
          * SRS
          * if the table has different SRS applies a ST_Transform
          * @see http://postgis.net/docs/manual-2.0/ST_Transform.html
          */
         if (!tableSrs.equals(PanelSettings.geosearchMaster.getSRS())) {
            Integer srid = Utils.getSRID(tableSrs);
            query.append(", ST_Transform(").append(tableGeomFieldName)
               .append(", ").append(srid).append(")");
         }
         else {
            query.append(tableGeomFieldName);
         }
         query.append(" AS ");
         
         // ALIAS
         if (!StringUtils.isEmpty(geomFieldName)) {
            query.append(geomFieldName);
         }
         else {
            query.append(tableGeomFieldName);
         }
      }
      
      String schemaTable = TableUtils.getSchemaTableDataImport(table);
      query.append(" FROM ").append(schemaTable);
      
      return query.toString();
   }

   private String getGeomFieldName(GeosearchTableVO tableVO) {
      String geomFieldName = null;
      
      String tableGeomFieldName = tableVO.getTable().getGeomField();
      for (GeosearchFieldVO field : tableVO.getFields()) {
         String fieldNameOnTable = field.getNameOnTable();
         if (fieldNameOnTable.equals(tableGeomFieldName)) {
            geomFieldName = field.getName();
            break;
         }
      }
      
      return geomFieldName;
   }
   
   private Node getDataConfigNode() throws XPathExpressionException {
      Node dataConfig = null;
      
      dataConfig = (Node) xpath.compile("/dataConfig").evaluate(dataImportXML, XPathConstants.NODE);
      if (dataConfig == null) {
         dataConfig = this.dataImportXML.createElement("dataConfig");
         this.dataImportXML.appendChild(dataConfig);
      }
      
      return dataConfig;
   }
   
   private Node getDataSource(String dataSourceName) throws XPathExpressionException {
      Node dataSource = null;
      
      dataSource = (Node) xpath.compile("/dataConfig/dataSource[@name='"
         .concat(dataSourceName).concat("']")).evaluate(dataImportXML, XPathConstants.NODE);
      
      return dataSource;
   }
}
