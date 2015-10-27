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
package es.juntadeandalucia.panelGestion.presentacion.controlador.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import es.juntadeandalucia.panelGestion.exception.PanelException;
import es.juntadeandalucia.panelGestion.negocio.servicios.DataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.SchemaService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TaskService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.SourceNameComparator;
import es.juntadeandalucia.panelGestion.negocio.utiles.TaskUtils;
import es.juntadeandalucia.panelGestion.negocio.utiles.TasksQueue;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.utiles.WebDavExplorer;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.TaskVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.FileType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Status;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;
import es.juntadeandalucia.panelGestion.persistencia.utiles.Repository;

/**
 * Seam controller that manages the new geographic information
 * uploads and the geographic information management
 *
 * @author GUADALTEL S.A
 */
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("newgicontroller")
public class NewGIController implements Serializable {
   
   /**
    * Private logger
    */
   private static Logger log = Logger.getLogger(NewGIController.class);
   
   /**
    * Generated version UID
    */
   private static final long serialVersionUID = -6842369023227810965L;
   
   /**
    * Size limit for local files (MB)
    */
   private int fileLimit = 10;
   
   /**
    * Refresh interval for task progress in milliseconds
    */
   private final Integer updatingInterval = PanelSettings.taskProgressInterval;
   
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
    * Service which manages Task entities
    */
   @In
   private TaskService taskService;
   
   /**
    * Task for the geographic information configuration
    */
   private TaskVO taskvo;
   
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
    * Name of the new schema to create
    */
   private String schemaName;
   
   /**
    * List of tables of the selected schema
    */
   private List<Table> tables;
   
   /**
    * Selected table
    */
   private Table table;
   
   /**
    * Name of the new table to create
    */
   private String tableName;
   
   /**
    * Url of the repository
    */
   private String repositoryUrl;
   
   /**
    * User name who will connect to the repository
    */
   private String repositoryUser;
   
   /**
    * User password used to connect to the repository
    */
   private String repositoryPassword;
   
   /**
    * flag which indicates if the user
    * specified a new repository
    */
   private boolean newRepository;
   
   /**
    * flag which indicates if the user
    * specified a new schema
    */
   private boolean newSchema;
   
   /**
    * Flag which indicates if the selected table
    * or the table name is valid
    */
   private boolean validTable;
   
   /**
    * Repositories configured in the settings file
    */
   private final List<Repository> repositories = PanelSettings.predefinedRepositories;
   
   /**
    * Repository where the user connected to
    */
   private Repository repository;
   
   /**
    * Available files from the selected repository
    */
   private List<Source> repositoryFiles;
   
   /**
    * Configured column types in the settings file
    */
   private final Map<String, Integer> dbTypes = PanelSettings.dataBaseTypes;
   
   /**
    * Configured projections in the settings file
    */
   private final List<String> projections = PanelSettings.projections;
   
   /**
    * the NorDirGeoDir application url
    */
   private final String nordirGeodirUrl = PanelSettings.nordirGeodirUrl;
   
   /**
    * Ticket of the task which is updating the
    * selected table if it exists
    */
   private String runningTaskTableTicket;
   
   /**
    * Flag that indicates the user is on
    * reading head file phase
    */
   private boolean readFilePhase;
   
   /**
    * Flag that indicates the user is on
    * processing file phase
    */
   private boolean processFilePhase;
   
   /**
    * Flag that indicates the user is on
    * uploading file phase
    */
   private boolean uploadingFilePhase;
   
   /**
    * Text of an error related with uploading
    * local file
    */
   private String localFileError;
   
   /**
    * Column of the new coordinate of a
    * CSV file
    */
   private ColumnVO coordinateColumn;
   
   /**
    * Main constructor
    */
   public NewGIController() {
      schemas = new LinkedList<Schema>();
      tables = new LinkedList<Table>();
      taskvo = new TaskVO();
   }
   
   /**
    * This function manages the local file upload
    *
    * @param event upload event sent by the richfaces
    * uploadfile control
    */
   public void processLocalFile(UploadEvent event) {
      localFileError = null;
      
      // gets its type
      UploadItem uploadFileItem = event.getUploadItem();
      
      String fileName = uploadFileItem.getFileName();
      String contentType = uploadFileItem.getContentType();
      boolean compressed = Utils.isCompressedFile(contentType);
      FileType type = Utils.getTypeFromContentType(contentType);
      if (type == FileType.UNKNOW) {
         type = Utils.getTypeFromName(fileName);
      }
      // no type defined or Shapefile uncompressed
      if ((type == null) || ((type == FileType.SHAPEFILE) && !compressed)){
         localFileError = "Tipo de archivo inválido: " + contentType;
         localFileError = localFileError.concat(". Sólo se permiten archivos CSV o comprimidos");
         log.error(localFileError);
      }
      else {
         // assigns the source to the task
         Source source = new Source();
         source.setName(fileName);
         source.setRemote(false);
         source.setContentType(contentType);
         source.setType(type);
         source.setTask(taskvo.getTaskEntity());
         
         taskvo.getTaskEntity().setSource(source);
         taskvo.setLocalSourceData(uploadFileItem.getFile());
         
         // NEXT PHASE --> READ FILE
         readFilePhase = true;
      }
   }
   
   /**
    * This method selects the file from the listed
    * files of the repository
    *
    * @param file the selected file from the repository
    */
   public void selectFile(Source file) {
      // set the source
      taskvo.getTaskEntity().setSource(file);
      
      // remove and add
      repositoryFiles.clear();
      repositoryFiles.add(file);
      
      // NEXT PHASE --> READ FILE
      readFilePhase = true;
   }
   
   /**
    * This method initializes the task which reads the file
    * and counts the number of lines
    */
   public void initTask() {
      checkValidTable();
      
      if (validTable) {
         try {
            // if it is a new schema then creates a new one
            if (isNewSchema()) {
               schema = new Schema();
               schema.setDataBase(dataBase);
               schema.setName(schemaName);
            }
            
            // if it is a new table then creates a new one
            if (!taskvo.getTaskEntity().isUpdate()) {
               table = new Table();
               table.setSchema(schema);
               table.setName(tableName);
            }
            
            // assigns the table to the task entity
            taskvo.getTaskEntity().setTable(table);
            // initializes the task
            taskvo.init();

            // NEXT PHASE --> PROCESS FILE
            processFilePhase = true;
            uploadingFilePhase = false;
         }
         catch (IOException e) {
            String errorMsg = e.getLocalizedMessage();
            StatusMessages.instance().add(Severity.ERROR, errorMsg);
            log.error(errorMsg);
         }
         catch (Exception e) {
            String errorMsg = "Error al iniciar la tarea: " + e.getLocalizedMessage();
            StatusMessages.instance().add(Severity.ERROR, errorMsg);
            log.error(errorMsg);
         }
      }
   }
   
   /**
    * This method starts the task which
    * will process the file
    */
   public void runTask() {
      // generates the ticket
      taskvo.getTaskTicket();

      // checks if the table is valid
      checkValidTable();
      if (validTable) {
         // prepares file columns
         try {
            taskvo.preareFileColumns();

            // no columns
            if (taskvo.getFileColumns().isEmpty()) {
               StatusMessages.instance().add(Severity.ERROR,
                  "Debe seleccionar al menos una columna");
            }
            else {
               // prepare location of the file
               prepareLocation();

               // assings the task service to update tasks
               taskvo.setTaskService(taskService);

               // appends the task to the thread queue
               TasksQueue.getInstance().appendTask(taskvo);

               // NEXT STATE
               uploadingFilePhase = true;
            }

         }
         catch (PanelException e) {
            String errorMsg = "Error al ejecutar la tarea: " + e.getLocalizedMessage();
            StatusMessages.instance().add(Severity.ERROR, errorMsg);
            log.error(errorMsg);
         }
         catch (Exception e) {
            String errorMsg = "Error al ejecutar la tarea: " + e.getLocalizedMessage();
            StatusMessages.instance().add(Severity.ERROR, errorMsg);
            log.error(errorMsg);
         }
      }
   }
   
   /**
    * This method creates the location for the
    * specified source in this task
    * @throws Exception 
    */
   private void prepareLocation() throws Exception {
      if (table != null) {
         // creates remote schema
         boolean schemaCreated = taskvo.createSchema(schema);
         if (schemaCreated) {
            // updates the application model
            schemaService.create(schema);
         }
         if (!taskvo.getTaskEntity().isUpdate()) {
            table.setSchema(schema);
            boolean tableCreated = taskvo.createTable(table);
            if (tableCreated) {
               // updates the application model
               tableService.create(table);
            }
         }
      }
   }
   
   /**
    * This method explores the selected repository
    * and lists the public files
    */
   public void browseRepository() {
      
      localFileError = null;
      
      repositoryFiles = new LinkedList<Source>();
      
      try {
         // creates the new repository and gets its files
         if (newRepository) {
            Repository repository = new Repository();
            repository.setUrl(repositoryUrl);
            repository.setUser(repositoryUser);
            repository.setPassword(repositoryPassword);
            repositoryFiles = WebDavExplorer.getFilesFrom(repository);
         }
         else {
            // simply gets its files
            repositoryFiles = WebDavExplorer.getFilesFrom(repository);
         }
         Collections.sort(repositoryFiles, new SourceNameComparator());
         
         // PREVIOUS STATE
         readFilePhase = false;
         processFilePhase = false;
         uploadingFilePhase = false;
      }
      catch (IOException e) {
         String errorMsg = "Error al conectar con el repositorio: " + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
      catch (DavException e) {
         String errorMsg = "Error al conectar con el repositorio: " + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
      catch (PanelException e) {
         String errorMsg = "Error al conectar con el repositorio: " + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
   }
   
   /**
    * Gets the schemas from the selected
    * data base
    */
   public void loadSchemas() {
      schemas = new LinkedList<Schema>();
      if (dataBase != null) {
         schemas = schemaService.getSchemasFromDB(dataBase);
         if (schemas.isEmpty()) {
            newSchema = true;
         }
      }
      // PREVIOUS STATE
      processFilePhase = false;
      uploadingFilePhase = false;
      
   }
   
   /**
    * Gets the tables from the selected
    * schema
    */
   public void loadTables() {
      tables = new LinkedList<Table>();
      if (schema != null) {
         tables = tableService.getTablesFromSchema(schema);
      }
      // PREVIOUS STATE
      processFilePhase = false;
      uploadingFilePhase = false;
   }
   
   /**
    * Prepares the new repository form
    */
   public void changeRepository() {
      this.newRepository = !this.newRepository;
      if (this.newRepository) {
         this.repositoryUrl = "http://";
      }
      // PREVIOUS STATE
      processFilePhase = false;
      uploadingFilePhase = false;
   }
   
   /**
    * Prepares the new schema form
    */
   public void changeSchema() {
      this.newSchema = !this.newSchema;
      if (this.newSchema) {
         schema = new Schema();
         schema.setDataBase(dataBase);
         
         taskvo.getTaskEntity().setUpdate(false);
         table = new Table();
         table.setSchema(schema);
      }
      // PREVIOUS STATE
      processFilePhase = false;
      uploadingFilePhase = false;
   }
   
   /**
    * Prepares the new table form
    */
   public void changeTable() {
      taskvo.getTaskEntity().setUpdate(!taskvo.getTaskEntity().isUpdate());
      if (taskvo.getTaskEntity().isUpdate()) {
         table = new Table();
         table.setSchema(schema);
         checkValidTable();
      }
      else {
         validTable = true;
      }
      
      // PREVIOUS STATE
      processFilePhase = false;
      uploadingFilePhase = false;
   }
   
   /**
    * Indicates if the selected table
    * or the table name is valid, that is, the selected
    * table is not being updated by an running task or the
    * new table name is not being used by any existing table
    */
   public void checkValidTable() {
      validTable = true;
      runningTaskTableTicket = null;
      
      // checks if the name is a valid name
      if (!taskvo.getTaskEntity().isUpdate() && StringUtils.isEmpty(tableName)) {
         validTable = false;
         String errorMsg = "Nombre de la tabla vacío";
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
      else if (!taskvo.getTaskEntity().isUpdate() && !Utils.isValidName(tableName)) {
         validTable = false;
         String errorMsg = "Nombre de la tabla inválido: " + tableName;
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
      else {
         /* if the user is updating a table gets all the running
          * and failed tasks and checks if they are updating the
          * selected table */
         if (taskvo.getTaskEntity().isUpdate() && (table != null)) {
            // checks running tasks
            List<Runnable> runningTasks = TasksQueue.getInstance().getAllRunningTasks();
            for (Runnable r : runningTasks) {
               TaskVO task = TaskUtils.getTaskVOFromRunnable(r);
               if (task != null) {
                  Table taskTable = task.getTaskEntity().getTable();
                  if (table.getId() == taskTable.getId()) {
                     validTable = false;
                     runningTaskTableTicket = task.getTaskEntity().getTicket();
                     break;
                  }
               }
            }
            // checks failed tasks
            List<Task> failedTasks = taskService.getFailedTasks();
            for (Task task : failedTasks) {
               Table taskTable = task.getTable();
               if (table.getId() == taskTable.getId()) {
                  validTable = false;
                  runningTaskTableTicket = task.getTicket();
                  break;
               }
            }
         }
         else if ((!newSchema) && (schema != null)) {
            schema.setDataBase(dataBase);
            try {
               // checks if exists a table with the selected name in the remote data base
               JDBCConnector connector = new JDBCConnector(schema);
               RemoteDataBaseService remoteDataBase = new RemoteDataBaseServiceImpl(connector);
               String schemaName = schema.getName();
               if (remoteDataBase.existsTable(schemaName, tableName)) {
                  validTable = false;
                  String errorMsg = "Ya existe una tabla con el nombre '" + tableName
                     + "' en el esquema '" + schemaName + "'";
                  StatusMessages.instance().add(Severity.ERROR, errorMsg);
                  log.error(errorMsg);
               }
            }
            catch (Exception e) {
               String errorMsg = "Error conectando con la base de datos: " + e.getLocalizedMessage();
               StatusMessages.instance().add(Severity.ERROR, errorMsg);
               log.error(errorMsg);
            }
         }
      }
   }
   
   /**
    * This method rests the local o remote
    * sources
    */
   public void resetSources() {
      if (taskvo != null) {
         taskvo.setLocalSourceData(null);
      }
      if (repositoryFiles != null) {
         repositoryFiles.clear();
      }
      
      // PREVIOUS PHASE
      readFilePhase = false;
      processFilePhase = false;
      uploadingFilePhase = false;
   }
   
   public void removeNewTable() {
      Task task = taskvo.getTaskEntity();
      try {
         TaskUtils.removeNewTable(task, taskService, tableService);
      }
      catch (Exception e) {
         String errorMsg = "Se ha producido un error al intentar eliminar la tabla: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
   }
   
   public void restoreBackup() {
      Task task = taskvo.getTaskEntity();
      try {
         // restore the table backup copy
         if (task.isUpdate()) {
            TaskUtils.restoreBackup(task);
         }
         // updates data model
         taskService.delete(task);
         taskvo.setTaskEntity(new Task());
      }
      catch (Exception e) {
         String errorMsg = "Se ha producido un error al intentar restaurar la copia de la tabla: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      };
   }
   
   public void checkCurrentTask() {
      Task task = taskvo.getTaskEntity();
      if ((task.getState().getStatus() == Status.ERROR) && uploadingFilePhase) {
         if (task.isUpdate()) {
            restoreBackup();
         }
         else {
            removeNewTable();
         }
         
         try {
            // initializes the task again
            Source source = task.getSource();
            Table table = task.getTable();
            boolean update = task.isUpdate();
            
            Task newTask = new Task();
            source.setTask(newTask);
            newTask.setSource(source);
            newTask.setTable(table);
            newTask.setUpdate(update);
            
            taskvo.setTaskEntity(newTask);
            initTask();
            
            // shows message
            String errorMsg = task.getState().getDescription();
            if (task.isUpdate()) {
               errorMsg = errorMsg.concat(". Se han restaurado los datos anteriores.");
            }
            StatusMessages.instance().add(Severity.ERROR, errorMsg);
         }
         catch (Exception e) {
            String errorMsg = "Error al iniciar la tarea: "+ e.getLocalizedMessage();
            StatusMessages.instance().add(Severity.ERROR, errorMsg);
            log.error(errorMsg);
         }
         
      }
   }
   
   /**
    * @return the schemas
    */
   public List<Schema> getSchemas() {
      return schemas;
   }
   
   /**
    * @return the tables
    */
   public List<Table> getTables() {
      return tables;
   }
   
   /**
    * resets the form
    */
   public void reset() {
      dataBase = null;
      schema = null;
      schemaName = null;
      table = null;
      tableName = null;
      repositoryUrl = null;
      repositoryUser = null;
      repositoryPassword = null;
      newRepository = false;
      newSchema = false;
      validTable = false;
      repository = null;
      runningTaskTableTicket = null;
      readFilePhase = false;
      processFilePhase = false;
      uploadingFilePhase = false;
      localFileError = null;
      schemas = new LinkedList<Schema>();
      tables = new LinkedList<Table>();
      repositoryFiles = new LinkedList<Source>();
      taskvo = new TaskVO();
   }
   
   /**
    * @return the dataBases
    */
   public List<DataBase> getDataBases() {
      return dataBaseService.getDataBases();
   }
   
   /**
    * @return the fileLimit
    */
   public int getFileLimit() {
      return fileLimit;
   }

   /**
    * @param fileLimit the fileLimit to set
    */
   public void setFileLimit(int fileLimit) {
      this.fileLimit = fileLimit;
   }

   /**
    * @return the repositoryUrl
    */
   public String getRepositoryUrl() {
      return repositoryUrl;
   }

   /**
    * @param repositoryUrl the repositoryUrl to set
    */
   public void setRepositoryUrl(String repositoryUrl) {
      this.repositoryUrl = repositoryUrl;
   }

   /**
    * @return the newRepository
    */
   public boolean isNewRepository() {
      return newRepository;
   }

   /**
    * @param newRepository the newRepository to set
    */
   public void setNewRepository(boolean newRepository) {
      this.newRepository = newRepository;
   }

   /**
    * @return the newSchema
    */
   public boolean isNewSchema() {
      return newSchema;
   }

   /**
    * @param newSchema the newSchema to set
    */
   public void setNewSchema(boolean newSchema) {
      this.newSchema = newSchema;
   }

   /**
    * @return the repositories
    */
   public List<Repository> getRepositories() {
      return repositories;
   }

   /**
    * @return the repositoryFiles
    */
   public List<Source> getRepositoryFiles() {
      return repositoryFiles;
   }

   /**
    * @param repositoryFiles the repositoryFiles to set
    */
   public void setRepositoryFiles(List<Source> repositoryFiles) {
      this.repositoryFiles = repositoryFiles;
   }

   /**
    * @return the dbTypes
    */
   public Set<String> getDbTypes() {
      return dbTypes.keySet();
   }

   /**
    * @return the projections
    */
   public List<String> getProjections() {
      return projections;
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
    * @return the tableName
    */
   public String getTableName() {
      return tableName;
   }

   /**
    * @param tableName the tableName to set
    */
   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   /**
    * @return the updatingInterval
    */
   public int getUpdatingInterval() {
      return updatingInterval;
   }

   /**
    * @return the repository
    */
   public Repository getRepository() {
      return repository;
   }

   /**
    * @param repository the repository to set
    */
   public void setRepository(Repository repository) {
      this.repository = repository;
   }

   /**
    * @return the schemaName
    */
   public String getSchemaName() {
      return schemaName;
   }

   /**
    * @param schemaName the schemaName to set
    */
   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }
   
   /**
    * @return the taskvo
    */
   public TaskVO getTaskvo() {
      return taskvo;
   }

   /**
    * @param taskvo the taskvo to set
    */
   public void setTaskvo(TaskVO taskvo) {
      this.taskvo = taskvo;
   }

   /**
    * @return the repositoryUser
    */
   public String getRepositoryUser() {
      return repositoryUser;
   }

   /**
    * @param repositoryUser the repositoryUser to set
    */
   public void setRepositoryUser(String repositoryUser) {
      this.repositoryUser = repositoryUser;
   }

   /**
    * @return the repositoryPassword
    */
   public String getRepositoryPassword() {
      return repositoryPassword;
   }

   /**
    * @param repositoryPassword the repositoryPassword to set
    */
   public void setRepositoryPassword(String repositoryPassword) {
      this.repositoryPassword = repositoryPassword;
   }

   /**
    * @return the validTable
    */
   public boolean isValidTable() {
      return validTable;
   }

   /**
    * @param validTable the validTable to set
    */
   public void setValidTable(boolean validTable) {
      this.validTable = validTable;
   }

   /**
    * @return the nordirGeodirUrl
    */
   public String getNordirGeodirUrl() {
      return nordirGeodirUrl;
   }

   /**
    * @return the runningTaskTableTicket
    */
   public String getRunningTaskTableTicket() {
      return runningTaskTableTicket;
   }

   /**
    * @param runningTaskTableTicket the runningTaskTableTicket to set
    */
   public void setRunningTaskTableTicket(String runningTaskTableTicket) {
      this.runningTaskTableTicket = runningTaskTableTicket;
   }

   /**
    * @return the readFile
    */
   public boolean isReadFilePhase() {
      return readFilePhase;
   }

   /**
    * @param readFilePhase the readFilePhase to set
    */
   public void setReadFilePhase(boolean readFilePhase) {
      this.readFilePhase = readFilePhase;
   }

   /**
    * @return the processFilePhase
    */
   public boolean isProcessFilePhase() {
      return processFilePhase;
   }

   /**
    * @param processFilePhase the processFilePhase to set
    */
   public void setProcessFilePhase(boolean processFilePhase) {
      this.processFilePhase = processFilePhase;
   }

   /**
    * @return the uploadingFilePhase
    */
   public boolean isUploadingFilePhase() {
      return uploadingFilePhase;
   }

   /**
    * @param uploadingFilePhase the uploadingFilePhase to set
    */
   public void setUploadingFilePhase(boolean uploadingFilePhase) {
      this.uploadingFilePhase = uploadingFilePhase;
   }

   /**
    * @return the localFileError
    */
   public String getLocalFileError() {
      return localFileError;
   }

   /**
    * @param localFileError the localFileError to set
    */
   public void setLocalFileError(String localFileError) {
      this.localFileError = localFileError;
   }

   /**
    * @return the coordinateColumn
    */
   public ColumnVO getCoordinateColumn() {
      return coordinateColumn;
   }

   /**
    * @param coordinateColumn the coordinateColumn to set
    */
   public void setCoordinateColumn(ColumnVO coordinateColumn) {
      this.coordinateColumn = coordinateColumn;
   }
}