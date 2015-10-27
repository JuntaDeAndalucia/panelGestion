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
package es.juntadeandalucia.panelGestion.presentacion.controlador.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import es.juntadeandalucia.panelGestion.exception.GeosearchException;
import es.juntadeandalucia.panelGestion.exception.PanelException;
import es.juntadeandalucia.panelGestion.negocio.servicios.ServiceService;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.utiles.geosearch.Geosearch;
import es.juntadeandalucia.panelGestion.negocio.utiles.geosearch.GeosearchConfigWriter;
import es.juntadeandalucia.panelGestion.negocio.utiles.geosearch.GeosearchDataImportWriter;
import es.juntadeandalucia.panelGestion.negocio.utiles.geosearch.GeosearchSchemaWriter;
import es.juntadeandalucia.panelGestion.negocio.utiles.geosearch.GeosearchUtils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchTableVO;
import es.juntadeandalucia.panelGestion.negocio.vo.XMLFileVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Service;
import es.juntadeandalucia.panelGestion.persistencia.entidades.ServiceType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("geosearchController")
public class GeosearchController implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -3097437847618823807L;
   
   private static Logger log = Logger.getLogger(GeosearchController.class);
   
   private static final String CONTENT_TYPE = "application/zip";
   
   private static final String FILE_NAME = "configuration.zip";
   
   @In
   private ServiceService serviceService;
   
   /**
    * Workspace controller
    */
   @In
   private WorkspaceController wscontroller;
   
   @In(value="#{facesContext.externalContext}")
   private ExternalContext externalCtx;
   
   @In(value="#{facesContext}")
   private FacesContext facesContext;
   
   private String newCoreName;
   private List<GeosearchTableVO> tables;
   private int tableIndex;
   private String core;
   private List<String> cores;
   private boolean newTable;
   private List<GeosearchFieldVO> schemaFields;
   private List<String> geosearchTypes;
   private boolean newCore;
   
   public GeosearchController() {
      cores = new LinkedList<String>();
      
      try {
         cores = Geosearch.getCores();
      }
      catch (GeosearchException e) {
         log.error("Error en la consulta de cores de Geobúsquedas: "
            + e.getLocalizedMessage());
      }
      tables = new LinkedList<GeosearchTableVO>();
      tables.add(new GeosearchTableVO());
   }
   
   public void onSelectTable() {
      Table table = wscontroller.getTable();
      selectTable(table);
   }
   
   public void onSelectCore() {
      try {
         if (!StringUtils.isEmpty(core)) {
            geosearchTypes = Geosearch.getFieldTypes(core);
            schemaFields = Geosearch.getFields(core);
         }
      }
      catch (GeosearchException e) {
         log.error("Error en la consulta de los campos de Geobúsquedas: "
            + e.getLocalizedMessage());
      }
   }
   
   public void selectTable(Table table) {
      if (table != null) {
         // checks if the table was already configured
         Integer condiguredTableIndex = null;
         for (int i = 0; i < tables.size(); i++) {
            GeosearchTableVO configuredTable = tables.get(i);
            if (table.equals(configuredTable.getTable())) {
               condiguredTableIndex = i;
               break;
            }
         }

         int lastIndex = tables.size() - 1;
         if (condiguredTableIndex != null) {
            // remove the new created table
            if (tables.get(lastIndex).getTable() == null) {
               tables.remove(lastIndex);
            }
            tableIndex = condiguredTableIndex;
            newTable = false;
         }
         else {
            try {
               tableIndex = lastIndex;
               newTable = false;
               
               // updates table index pointer and set the last table with the selected one
               GeosearchTableVO geosearchTable = GeosearchUtils.toGeosearchTable(table);
               
               // if it has not a new created table then creates one
               if ((tables.get(lastIndex).getTable() != null) 
                     && !StringUtils.isEmpty(core)) {
                  tables.add(geosearchTable);
                  tableIndex++;
               }
               else {
                  tables.set(tableIndex, geosearchTable);
               }
            }
            catch (Exception e) {
               String errorMessage = "No se ha podido obtener la configuración de la tabla: "
                  + e.getLocalizedMessage();
               StatusMessages.instance().add(Severity.ERROR, errorMessage);
               log.error(errorMessage);
            }
         }
      }
   }
  
   public void addTable() {
      // add a new empty table
      tables.add(new GeosearchTableVO());
      tableIndex = tables.size() - 1;
      wscontroller.reset();
      newTable = true;
   }
   
   public void removeTable(int i) {
      tables.remove(i);
      tableIndex--;
   }
   
   /**
    * This method 
    * TODO
    *
    * Para realizar la configuración se obtienen los archivos de configuración
    * se realizan las modificaciones necesarias y se le facilita al usuario
    * dichos archivos para que finalice él el procedimiento de configuración.
    * Las últimas versiones de Solr admiten modificaciones del Schema a través
    * de un API REST pero consideramos lioso realizar por un lado la configuración
    * del schema mediante API y transparente al usuario y por otro lado darle los
    * archivos al usuario para que los sustituya en Geosearch.
    * 
    * @see https://wiki.apache.org/solr/SchemaRESTAPI
    */
   public void downloadConfig() {
      String errorMessage = null;

      ServletOutputStream os = null;
      
      try {
         // checks
         // checks if specified a table
         if (tables.isEmpty()) {
            throw new Exception("No se ha especificado ninguna tabla");
         }
         // checks if specified a field
         boolean specifiedField = false;
      tables_loop:
         for (GeosearchTableVO table : tables) {
            List<GeosearchFieldVO> fields = table.getFields();
            for (GeosearchFieldVO field : fields) {
               if (field.isDefined()) {
                  specifiedField = true;
                  break tables_loop;
               }
            }
         }
         if (!specifiedField) {
            throw new Exception("No se ha configurado ningún campo de las tablas seleccionadas");
         }
         // checks duplicated fields each table
         for (GeosearchTableVO table : tables) {
            if (tableHasDuplicatedFields(table)) {
               throw new Exception("Existen campos duplicados en la tabla '".concat(
                  table.getTable().getName()).concat("'. Revise su configuración."));
            }
         }

         // ovverides the duplicated field values
         overrideDuplicatedFields();
         
         checkFieldErrors();

         // gets the zip file with configuration
         byte[] configurationData = generateConfigurationZipData();

         // configures the response
         HttpServletResponse response = (HttpServletResponse) externalCtx.getResponse();
         response.setContentType(CONTENT_TYPE);
         response.addHeader("Content-disposition", "attachment; filename=\"".concat(FILE_NAME)
            .concat("\""));

         os = response.getOutputStream();
         os.write(configurationData);
         os.flush();
         os.close();
         facesContext.responseComplete();
      }
      catch (GeosearchException e) {
         errorMessage = "Error en la generación de los archivos de configuración: "
            + e.getLocalizedMessage();
      }
      catch (ParserConfigurationException e) {
         errorMessage = "Error en la generación de los archivos de configuración: "
            + e.getLocalizedMessage();
      }
      catch (XPathExpressionException e) {
         errorMessage = "Error en la generación de los archivos de configuración: "
            + e.getLocalizedMessage();
      }
      catch (TransformerException e) {
         errorMessage = "Error al comprimir los archivos de configuración: "
            + e.getLocalizedMessage();
      }
      catch (IOException e) {
         errorMessage = "Error al comprimir los archivos de configuración: "
            + e.getLocalizedMessage();
      }
      catch (Exception e) {
         errorMessage = "Error en la descarga de la configuración: " + e.getLocalizedMessage();
      }
      finally {
         try {
            if (os != null) {
            os.flush();
            os.close();
            }
         }
         catch (IOException e) {
            errorMessage = "Error al comprimir los archivos de configuración: "
               + e.getLocalizedMessage();
         }
      }

      if (errorMessage != null) {
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
      else {
         // saves the new service for each table
         try {
            ServiceType geosearchType = serviceService.getServiceType("geobusquedas");
            for (GeosearchTableVO geosearchTable : tables) {
               Table table = geosearchTable.getTable();
               Service geosearchService = new Service();
               geosearchService.setName(table.getName());
               geosearchService.setServiceUrl(PanelSettings.geosearchMaster.getUrl().concat("/").concat(core));
               geosearchService.setType(geosearchType);
               serviceService.create(geosearchService, table);
            }
         }
         catch (Exception e) {
            errorMessage = "";
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
      }
   }
   
   private void checkFieldErrors() throws PanelException {
      for (GeosearchTableVO table : tables) {
         List<GeosearchFieldVO> fields = table.getFields();
         for (GeosearchFieldVO field : fields) {
            if (field.isDefined()) {
               String fieldName = Utils.getFieldName(field);
               
               // checks field name
               if (StringUtils.isEmpty(fieldName)) {
                  String errorMessage = "No ha especificado nombre para el campo";
                  throw new PanelException(errorMessage);
               }
               else if (!Utils.isValidName(fieldName)) {
                  String errorMessage = "Nombre del campo inválido: ".concat(fieldName);
                  throw new PanelException(errorMessage);
               }
               
               // checks type
               if (StringUtils.isEmpty(field.getType())) {
                  String errorMessage = "No ha especificado tipo para el campo '" + fieldName + "'";
                  throw new PanelException(errorMessage);
               }
               
               // checks boost
               String fieldBoost = field.getBoost();
               if (!StringUtils.isEmpty(fieldBoost) && !Utils.isValidDouble(fieldBoost)) {
                  String errorMessage = "El peso del campo '" + fieldName + "' es inválido";
                  throw new PanelException(errorMessage);
               }
            }
         }
      }
   }

   /**
    * Overrides the duplicated fields. These are the fields which
    * have equal names or nameOnTable attributes
    * @throws PanelException if some error occurs
    */
   private void overrideDuplicatedFields() {
      GeosearchTableVO currentTable = tables.get(this.tableIndex);
      List<GeosearchFieldVO> fields = currentTable.getFields();
      for (GeosearchFieldVO field : fields) {
         if (field.isDefined()) {
            // override each field
            overrideDuplicatedField(field);
         }
      }
   }

   /**
    * Generates the bytes of the compressed generated files: solrconfig.xml, schema.xml
    * and dataImport.xml for the selected configuration
    *
    * @return the bytes of the compressed generated files
    * 
    * @throws GeosearchException thrown if some error occurred while
    *  trying to connect with Geosearch
    * @throws ParserConfigurationException thrown by the configuration parser
    * @throws XPathExpressionException thrown while managing the configuration files
    * @throws TransformerException thrown while managing the configuration files
    * @throws IOException thrown while compressing the files
    */
   private byte[] generateConfigurationZipData() throws GeosearchException,
      ParserConfigurationException, XPathExpressionException, TransformerException, IOException {
      byte[] zipFile = null;

      // generates the configuration files
      // solrconfig.xml
      GeosearchConfigWriter configWriter = new GeosearchConfigWriter(tables, core);
      XMLFileVO configXML = configWriter.write();
      // schema.xml
      GeosearchSchemaWriter schemaWriter = new GeosearchSchemaWriter(tables, core);
      XMLFileVO schemaXML = schemaWriter.write();
      // dataimport.xml
      GeosearchDataImportWriter dataImportWriter = new GeosearchDataImportWriter(tables, core);
      XMLFileVO dataImportXML = dataImportWriter.write();

      XMLFileVO[] xmlFiles = { configXML, dataImportXML, schemaXML };
      zipFile = Utils.compressXMLFiles(xmlFiles);

      return zipFile;
   }
   
   public void createNewCore() {
      String errorMessage = null;

      try {
         if (StringUtils.isEmpty(newCoreName)) {
            errorMessage = "Debe especificar un nombre para el nuevo core";
         }
         else if (!Utils.isValidName(newCoreName)) {
            errorMessage = "El nombre para el nuevo core es inválido: " + newCoreName;
         }
         else if (Geosearch.existsCore(newCoreName)) {
            errorMessage = "El core '".concat(newCoreName).concat("' ya existe en Geobúsquedas");
         }
         else {
            boolean created = Geosearch.createCore(newCoreName);
            if (created) {
               // reloads the core list
               cores = Geosearch.getCores();
               // selects the new core
               core = newCoreName;
               // reset newCore flag
               newCore = false;
            
               // triggers the onSelectCore event
               onSelectCore();
               
               // informs to the user
               String successMessage = "El core '".concat(core).concat("' ha sido creado satisfactoriamente");
               StatusMessages.instance().add(Severity.INFO, successMessage);
               log.info(successMessage);
            }
            else {
               errorMessage = "El core no ha podido crearse. Compruebe la configuración de Geobúsquedas";
            }
         }
      }
      catch (IOException e) {
         errorMessage = "Error en la creación de la carpeta del core: "
            + e.getLocalizedMessage();
      }
      catch (GeosearchException e) {
         errorMessage = "Error en la creación del core: "
            + e.getLocalizedMessage();
      }

      if (errorMessage != null) {
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }

   public void reset() {
      newCoreName = null;
      tables.clear();
      tables.add(new GeosearchTableVO());
      tableIndex = 0;
      core = null;
      newTable = false;
      newCore = false;

      if (schemaFields != null) {
         schemaFields.clear();
      }

      if (geosearchTypes != null) {
         geosearchTypes.clear();
      }

      try {
         cores.clear();
         cores = Geosearch.getCores();
      }
      catch (GeosearchException e) {
         log.error("Error en la consulta de cores de Geobúsquedas: "
            + e.getLocalizedMessage());
      }
//      wscontroller.reset();
   }

   /**
    * Checks if the field exists in the schema.xml file of the
    * selected core or there are some fields using the same name
    *
    * @param field Field to check
    */
   public void overrideDuplicatedField(GeosearchFieldVO field) {
      field.setError(null);
      field.setInSchema(false);

      if (!field.isDefined()) {
         return;
      }

      // gets the column name
      String fieldName = Utils.getFieldName(field);

      /*
       * gets the selected fields with same name which is not in the current table and the fields in
       * the schema.xml
       */
      GeosearchTableVO tableOwn = tables.get(tableIndex);

      // checks duplicated fields on the own table
      boolean duplicatedOnTable = tableHasDuplicatedFields(tableOwn);
      if (duplicatedOnTable) {
         String errorMessage = "La tabla '".concat(tableOwn.getTable().getName())
            .concat("' posee campos duplicados");
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
      else {
         GeosearchFieldVO selectedColumn = getSelectedColumn(fieldName, tableOwn.getTable());
         GeosearchFieldVO schemaField = getSchemaField(fieldName);

         // if there is a field in the schema.xml with the same name
         if (schemaField != null) {
            field.setIndexed(schemaField.isIndexed());
            field.setStored(schemaField.isStored());
            field.setType(schemaField.getType());
            field.setCopyToText(schemaField.isCopyToText());
            field.setMultivaluated(schemaField.isMultivaluated());
            field.setBoost(schemaField.getBoost());
            field.setInSchema(true);

            String errorMessage = "El campo '".concat(fieldName).concat(
               "' ya existe en el schema.xml. Se usarán los valores definidos en él");
            field.setError(errorMessage);
         }
         // otherwise checks if exists a field
         else if (selectedColumn != null) {
            selectedColumn.setIndexed(field.isIndexed());
            selectedColumn.setStored(field.isStored());
            selectedColumn.setType(field.getType());
            selectedColumn.setCopyToText(field.isCopyToText());
            selectedColumn.setMultivaluated(field.isMultivaluated());
            selectedColumn.setBoost(field.getBoost());
            field.setError(selectedColumn.getError());
         }
         else {
            // reset errors and vars
            field.setError(null);
            field.setInSchema(false);
         }
         
         // checks field name
         if (StringUtils.isEmpty(fieldName)) {
            String errorMessage = "No ha especificado nombre para el campo";
            field.setError(errorMessage);
         }
         else if (!Utils.isValidName(fieldName)) {
            String errorMessage = "Nombre del campo inválido: ".concat(fieldName);
            field.setError(errorMessage);
         }
         
         // checks type
         if (StringUtils.isEmpty(field.getType())) {
            String errorMessage = "No ha especificado tipo para el campo '" + fieldName + "'";
            field.setError(errorMessage);
         }
         
         // checks boost
         String fieldBoost = field.getBoost();
         if (!StringUtils.isEmpty(fieldBoost) && !Utils.isValidDouble(fieldBoost)) {
            String errorMessage = "El peso del campo '" + fieldName + "' es inválido";
            field.setError(errorMessage);
         }
      }
   }
   
   private boolean tableHasDuplicatedFields(GeosearchTableVO table) {
      boolean duplicatedOnTable = false;
      Set<String> fieldNames = new HashSet<String>();
      for (GeosearchFieldVO field : table.getFields()) {
         String fieldName = Utils.getFieldName(field);
         if (fieldNames.contains(fieldName)) {
            duplicatedOnTable = true;
            break;
         }
         fieldNames.add(fieldName);
      }
      return duplicatedOnTable;
   }

   private GeosearchFieldVO getSelectedColumn(String fieldName, Table tableOwn) {
      GeosearchFieldVO schemaField = null;

      table_loop: for (GeosearchTableVO table : tables) {
         if (table.getTable().equals(tableOwn)) {
            continue table_loop;
         }
         List<GeosearchFieldVO> fields = table.getFields();
         for (GeosearchFieldVO field : fields) {
            String currentFieldName = Utils.getFieldName(field);
            if (currentFieldName.equals(fieldName)) {
               schemaField = field;
               String errorMessage = "Ya existe un campo con el nombre '".concat(fieldName)
                  .concat("' en la tabla '").concat(table.getTable().getName())
                  .concat("'. Se sobreescribirán las propiedades que se definan para este campo");
               schemaField.setError(errorMessage);
               break table_loop;
            }
         }
      }

      return schemaField;
   }

   private GeosearchFieldVO getSchemaField(String fieldName) {
      GeosearchFieldVO schemaField = null;
      
      for (GeosearchFieldVO field : schemaFields) {
         if (field.getName().equals(fieldName)) {
            schemaField = field;
            break;
         }
      }
         
      return schemaField;
   }

   /**
    * @return the url
    */
   public String getUrl() {
      return PanelSettings.geosearchMaster.getUrl();
   }

   /**
    * @return the tables
    */
   public List<GeosearchTableVO> getTables() {
      return tables;
   }

   /**
    * @param tables the tables to set
    */
   public void setTables(List<GeosearchTableVO> tables) {
      this.tables = tables;
   }

   /**
    * @return the geosearchTypes
    */
   public List<String> getGeosearchTypes() {
      return geosearchTypes;
   }

   /**
    * @param geosearchTypes the geosearchTypes to set
    */
   public void setGeosearchTypes(List<String> geosearchTypes) {
      this.geosearchTypes = geosearchTypes;
   }

   /**
    * @return the tableIndex
    */
   public int getTableIndex() {
      return tableIndex;
   }

   /**
    * @param tableIndex the tableIndex to set
    */
   public void setTableIndex(int tableIndex) {
      overrideDuplicatedFields();
      
      this.tableIndex = tableIndex;
      Table selectedTable = tables.get(tableIndex).getTable();
      this.wscontroller.setDataBase(selectedTable.getSchema().getDataBase());
      this.wscontroller.loadSchemas();
      this.wscontroller.setSchema(selectedTable.getSchema());
      this.wscontroller.loadTables();
      this.wscontroller.setTable(selectedTable);
   }

   /**
    * @return the core
    */
   public String getCore() {
      return core;
   }

   /**
    * @param core the core to set
    */
   public void setCore(String core) {
      this.core = core;
   }

   /**
    * @return the cores
    */
   public List<String> getCores() {
      return cores;
   }

   /**
    * @param cores the cores to set
    */
   public void setCores(List<String> cores) {
      this.cores = cores;
   }

   /**
    * @return the newTable
    */
   public boolean isNewTable() {
      return newTable;
   }

   /**
    * @param newTable the newTable to set
    */
   public void setNewTable(boolean newTable) {
      this.newTable = newTable;
   }

   /**
    * @return the schemaFields
    */
   public List<GeosearchFieldVO> getSchemaFields() {
      return schemaFields;
   }

   /**
    * @param schemaFields the schemaFields to set
    */
   public void setSchemaFields(List<GeosearchFieldVO> schemaFields) {
      this.schemaFields = schemaFields;
   }

   /**
    * @return the newCoreName
    */
   public String getNewCoreName() {
      return newCoreName;
   }

   /**
    * @param newCoreName the newCoreName to set
    */
   public void setNewCoreName(String newCoreName) {
      this.newCoreName = newCoreName;
   }

   /**
    * @return the newCore
    */
   public boolean isNewCore() {
      return newCore;
   }

   /**
    * @param newCore the newCore to set
    */
   public void setNewCore(boolean newCore) {
      this.newCore = newCore;
   }
}