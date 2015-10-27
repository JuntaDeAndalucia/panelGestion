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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import es.juntadeandalucia.panelGestion.negocio.servicios.DataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.SchemaService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

/**
 * Seam controller that manages the data base the schema
 * and the table which the user uses
 * 
 * @author GUADALTEL S.A
 */
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("wscontroller")
public class WorkspaceController implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 9140995525063928228L;

   /**
    * Private logger
    */
   protected static Logger log = Logger.getLogger(WorkspaceController.class);
   
   /**
    * Service which manages DataBase entities
    */
   @In
   private DataBaseService dataBaseService;
   
   /**
    * Service which manages Schema entities
    */
   @In
   private SchemaService schemaService;
   
   /**
    * Service which manages Table entities
    */
   @In
   private TableService tableService;
   
   /**
    * OGC controller
    */
   @In
   private OGCController ogccontroller;
   
   /**
    * RA controller
    */
   @In
   private RAController racontroller;
   
   /**
    * Geosearch controller
    */
   @In
   private GeosearchController geosearchController;
   
   /**
    * IG controller
    */
   @In
   private GIController gicontroller;
   
   /**
    * Available data bases
    */
   private List<DataBase> dataBases;
   
   /**
    * Selected data base
    */
   private DataBase dataBase;
   
   /**
    * Schemas of the selected data base
    */
   private List<Schema> schemas;
   
   /**
    * Selected schema
    */
   private Schema schema;
   
   /**
    * List of tables of the selected schema
    */
   private List<Table> tables;
   
   /**
    * Selected table
    */
   private Table table;
   
   /**
    * Main constructor
    */
   public WorkspaceController() {
      dataBases = new LinkedList<DataBase>();
      schemas = new LinkedList<Schema>();
      tables = new LinkedList<Table>();
   }
   
   /**
    * @return the dataBases
    */
   public List<DataBase> getDataBases() {
      dataBases = dataBaseService.getDataBases();
      return dataBases;
   }
   
   /**
    * @return the schemas
    */
   public List<Schema> loadSchemas() {
      table = null;
      tables.clear();
      schema = null;
      schemas.clear();
      if (dataBase != null) {
         schemas = schemaService.getSchemasFromDB(dataBase);
      }
      return schemas;
   }
   
   /**
    * @return the tables
    */
   public List<Table> loadTables() {
      table = null;
      tables.clear();
      if (schema != null) {
         tables = tableService.getTablesFromSchema(schema);
      }
      return tables;
   }
   
   /**
    * This method resets the selected
    * database, schema and table
    */
   public void reset() {
      table = null;
      schema = null;
      dataBase = null;
      getDataBases();
      loadSchemas();
      loadTables();
   }
   
   /**
    * This method checks if a table
    * was selected
    *
    * @return return true if a table was selected
    * false in other case
    */
   public boolean isSelectedTable() {
      return (table != null);
   }
   
   /**
    * This method is triggered when a table
    * is selected and executes onSelectTable
    * methods of the other controllers
    */
   public void onSelectedTable() {
      if (ogccontroller != null) {
         ogccontroller.onSelectTable(); // trigger
      }
      if (racontroller != null) {
         racontroller.onSelectTable(); // trigger
      }
      if (geosearchController != null) {
         geosearchController.onSelectTable(); // trigger
      }
      if (gicontroller != null) {
         Table table = getTable();
         if (table != null) {
            gicontroller.selectTable(table); // trigger
         }
      }
   }
   
   /**
    * @return the dataBaseService
    */
   public DataBaseService getDataBaseService() {
      return dataBaseService;
   }

   /**
    * @param dataBaseService the dataBaseService to set
    */
   public void setDataBaseService(DataBaseService dataBaseService) {
      this.dataBaseService = dataBaseService;
   }

   /**
    * @return the schemaService
    */
   public SchemaService getSchemaService() {
      return schemaService;
   }

   /**
    * @param schemaService the schemaService to set
    */
   public void setSchemaService(SchemaService schemaService) {
      this.schemaService = schemaService;
   }

   /**
    * @return the tableService
    */
   public TableService getTableService() {
      return tableService;
   }

   /**
    * @param tableService the tableService to set
    */
   public void setTableService(TableService tableService) {
      this.tableService = tableService;
   }

   /**
    * @return the dataBase
    */
   public DataBase getDataBase() {
      return dataBase;
   }

   /**
    * @param dataBase the dataBase to set
    */
   public void setDataBase(DataBase dataBase) {
      this.dataBase = dataBase;
   }

   /**
    * @return the schemas
    */
   public List<Schema> getSchemas() {
      return schemas;
   }

   /**
    * @param schemas the schemas to set
    */
   public void setSchemas(List<Schema> schemas) {
      this.schemas = schemas;
   }

   /**
    * @return the schema
    */
   public Schema getSchema() {
      return schema;
   }

   /**
    * @param schema the schema to set
    */
   public void setSchema(Schema schema) {
      this.schema = schema;
   }

   /**
    * @return the tables
    */
   public List<Table> getTables() {
      return tables;
   }

   /**
    * @param tables the tables to set
    */
   public void setTables(List<Table> tables) {
      this.tables = tables;
   }

   /**
    * @return the table
    */
   public Table getTable() {
      return table;
   }

   /**
    * @param table the table to set
    */
   public void setTable(Table table) {
      this.table = table;
   }

   /**
    * @param dataBases the dataBases to set
    */
   public void setDataBases(List<DataBase> dataBases) {
      this.dataBases = dataBases;
   }
}
