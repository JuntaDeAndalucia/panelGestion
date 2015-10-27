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
package es.juntadeandalucia.panelGestion.negocio.vo;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import es.juntadeandalucia.panelGestion.exception.PanelException;
import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TaskService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.FileProcessor;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.csv.CSVFileProcessor;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.shape.ShapeFileProcessor;
import es.juntadeandalucia.panelGestion.persistencia.entidades.FileType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Status;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;
import es.juntadeandalucia.panelGestion.persistencia.entidades.TaskState;
import es.juntadeandalucia.panelGestion.persistencia.utiles.TableUtils;

/**
 * Associated bean to manage the task operations
 *
 * @author GUADALTEL S.A
 */
public class TaskVO implements Callable<TaskVO>, Serializable {

   private static Logger log = Logger.getLogger(TaskVO.class);
   
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -3735877896567374764L;
   
   /**
    * read columns from the uploaded file
    */
   private List<ColumnVO> columns;
   
   private List<ColumnVO> tableColumns;
   
   private List<ColumnVO> fileColumns;
   
   private RemoteDataBaseService remoteDataBaseService;
   
   private TaskService taskService;
   
   private FileProcessor fileProcessor;
   
   private File localSourceData;
   
   private Task taskEntity;
   
   private String shapeSrs;
   
   private boolean firstLineHeader;
   
   /**
    * Main constructor
    */
   public TaskVO() {
      super();
      taskEntity = new Task();
      taskEntity.setSource(new Source());
   }

   public void init() throws Exception {
      createFileProcessor();
      columns = fileProcessor.readColumns();
      // if it updates the table then filter columns
      if (taskEntity.isUpdate()) {
         tableColumns = remoteDataBaseService.getAllColumnsExceptPKeyGeom(taskEntity.getTable());
      }
   }
   
   private void createFileProcessor() throws Exception {
      // close current file processor
      if (fileProcessor != null) {
         try {
            fileProcessor.end();
         }
         catch(IOException e) {
            // none
         }
      }
      
      // create the connector for dataBase
      JDBCConnector connector = new JDBCConnector(taskEntity.getTable().getSchema());
      remoteDataBaseService = new RemoteDataBaseServiceImpl(connector);
      
      Source source = taskEntity.getSource();
      if (source.getType() == FileType.CSV) {
         fileProcessor = new CSVFileProcessor(source, localSourceData);
      }
      else if (source.getType() == FileType.SHAPEFILE) {
         String ticket = getTaskTicket();
         fileProcessor = new ShapeFileProcessor(shapeSrs, source, ticket, localSourceData);
         
         /*
          * if the user is updating a table then it checks
          * if the SRS specified on the table is equals to
          * the SRS specified for the shape file.
          * If the user is creating a new table then set the SRS
          * on the table entity
          */
         CoordinateReferenceSystem prjCRS = ((ShapeFileProcessor)fileProcessor).getCRS();
         if (getTaskEntity().isUpdate()) {
            // user is updating the table
            String epsg = getTaskEntity().getTable().getEpsg();
            Integer tableSRID = Utils.getSRID(epsg);
            Integer prjSRID = Utils.getSRID(prjCRS);
            if (tableSRID.intValue() != prjSRID.intValue()) {
               throw new IllegalArgumentException("La proyección '"
                  .concat(Utils.getEPSG(prjSRID))
                  .concat("' del shapefile no coincide con la proyección '")
                  .concat(epsg).concat("' de la tabla '")
                  .concat(getTaskEntity().getTable().getName()).concat("'"));
            }
         }
         else {
            String epsg = Utils.getEPSG(prjCRS);
            getTaskEntity().getTable().setEpsg(epsg);
         }
      }
   }

   public void preareFileColumns() throws PanelException {
      fileColumns = new LinkedList<ColumnVO>();
      if (taskEntity.isUpdate()) {
         for (ColumnVO column : tableColumns) {
            if ((column.getFilePosition() >= 0) || column.isFromCoordinates()) {
               fileColumns.add(column);
            }
         }
      }
      else {
         for (ColumnVO column : columns) {
            if (column.isInTable()) {
               // checks valid column name
               String columnNameOnTable = column.getNameOnTable();
               if (StringUtils.isEmpty(columnNameOnTable)) {
                  columnNameOnTable = column.getText();
               }
               if (StringUtils.isEmpty(columnNameOnTable)) {
                  throw new PanelException("nombre de columna vacío");
               }
               else if (!Utils.isValidName(columnNameOnTable)) {
                  throw new PanelException("nombre de columna inválido: " + columnNameOnTable);
               }

               String dbType = column.getType();
               Integer sqlType = PanelSettings.dataBaseTypes.get(dbType);
               if (sqlType == Types.OTHER) { // geometry type
                  if (Pattern.compile(".*\\s*(X|lat)\\s*.*", Pattern.CASE_INSENSITIVE)
                     .matcher(dbType).matches()) {
                     column.setCoordinateX(true);
                  }
                  else if (!Pattern.matches("geometry", dbType.toLowerCase())
                     && Pattern.compile(".*\\s*(Y|lon)\\s*.*", Pattern.CASE_INSENSITIVE)
                        .matcher(dbType).matches()) {
                     column.setCoordinateY(true);
                  }
                  else {
                     getTaskEntity().getTable().setGeomField(column.getNameOnTable());
                  }
               }
               column.setSqlType(sqlType);
               fileColumns.add(column);
            }
         }
      }
   }
   
   @Override
   public TaskVO call() throws Exception {
      try {
         // initialize state
         changeStatusTo(Status.RUNNING);
         
         // it does a backup of the current table
         backupTable();
         
         // remove the old rows from the table
         cleanTable();
         
         // process the file
         processFile();
         
         // removes the backup done
         try {
            removeBackupTable();
         }
         catch (Exception e) {
            log.error("Error al intentar eliminar el backup de la tabla realizado: "
               + e.getLocalizedMessage());
         }
      }
      catch (Throwable t) {
         // manages the error
         manageError(t);
      }
      finally {
         // closes the file manager
         closeFileManager();
         
         try {
            // saves the state
            saveTask();
         }
         catch (Exception e) {
            log.error("Error al intentar guardar el estado de la tarea: "
               + e.getLocalizedMessage());
         }
      }
      return this;
   }
   
   private void removeBackupTable() throws Exception {
      if (getTaskEntity().isUpdate()) {
         Table table = getTaskEntity().getTable();
         remoteDataBaseService.removeBackup(table);
      }
   }

   /**
    * This method does a backup of the current table
    * if the task will update a selected table by the user
    *
    * @throws Exception if there was any error doing the backup
    */
   private void backupTable() throws Exception {
      if (getTaskEntity().isUpdate()) {
         Table table = getTaskEntity().getTable();
         remoteDataBaseService.backup(table);
      }
   }

   private void closeFileManager() {
      if (fileProcessor != null) {
         try {
            fileProcessor.end();
         }
         catch (IOException e) {
            log.warn("Error al intentar cerrar el procesador de archivo"
               + e.getLocalizedMessage());
         }
      }
   }

   private void manageError(Throwable t) {
      taskEntity.getState().setStatus(Status.ERROR);
      taskEntity.getState().setDescription(
         "Error en la línea ".concat(String.valueOf(fileProcessor.getNumCurrentEntry()))
            .concat(": ").concat(t.getLocalizedMessage()));
      
      // gets ticket
      String taskTicket = null;
      if (getTaskEntity() != null) {
         taskTicket = getTaskEntity().getTicket();
      }
      
      try {
         saveTask();
      }
      catch (Exception e) {
         log.error("Error al intentar guardar el estado de la tarea '"
            .concat(taskTicket).concat("': ")+ e.getLocalizedMessage());
      }
      log.error("Error en la ejecución de la tarea '"
         .concat(taskTicket).concat("': ").concat(t.getLocalizedMessage()));
   }

   private void processFile() throws Throwable {
      if (taskEntity.getSource().getType() == FileType.CSV) {
         processCSVFile();
      }
      else if (taskEntity.getSource().getType() == FileType.SHAPEFILE) {
         processShapeFile();
      }
   }

   private void processShapeFile() throws Throwable {
      Table table = taskEntity.getTable();
      Integer srid = Utils.getSRID(table.getEpsg());
      
      String schemaTableName = TableUtils.getSchemaTable(table);
      
      BigDecimal totalFeatures = BigDecimal.valueOf(fileProcessor.getNumEntries());
      int saveLine = 0;
      while (taskEntity.getState().getStatus() != Status.FINISHED) {
         insertShapeFeature(schemaTableName, fileColumns, srid);
         updateShapeProgress(totalFeatures);
         // save the task state every 'LINES_TO_SAVE' lines processed
         if (saveLine == PanelSettings.taskLinesToSave) {
            saveTask();
            saveLine = 0;
         }
         saveLine++;
      }
   }

   private void processCSVFile() throws Throwable {
      Table table = taskEntity.getTable();
      Integer srid = Utils.getSRID(table.getEpsg());
      
      String schemaTableName = TableUtils.getSchemaTable(table);
      
      BigDecimal totalSize = BigDecimal.valueOf(fileProcessor.getBytesLength());
      int saveLine = 0;
      
      /* if the first line is not a header
       * then it is stored into the table
       */
      if (!firstLineHeader) {
         insertCSVHeader(schemaTableName, fileColumns, srid);
      }
      
      while (taskEntity.getState().getStatus() != Status.FINISHED) {
         insertCSVLine(schemaTableName, fileColumns, srid);
         updateCSVProgress(totalSize);
         // save the task state every 'LINES_TO_SAVE' lines processed
         if (saveLine == PanelSettings.taskLinesToSave) {
            saveTask();
            saveLine = 0;
         }
         saveLine++;
      }
   }

   private void saveTask() throws Exception {
      if (taskEntity.getSource().isRemote()) {
         taskService.update(taskEntity);
      }
   }

   private void updateCSVProgress(BigDecimal totalSize) {
      if (taskEntity.getState().getStatus() == Status.FINISHED) {
         finishState();
      }
      else {
         // gets current data
         int readLines = fileProcessor.getNumCurrentEntry() + 1;
         long readBytes = fileProcessor.getReadBytes();

         // calculates the current process
         BigDecimal readBytesBD = BigDecimal.valueOf(readBytes);
         double progress = readBytesBD.divide(totalSize, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100)).doubleValue();

         // updates the task state
         taskEntity.getState().setReadLines(readLines);
         taskEntity.getState().setProgress(progress);

         // finishes when all bytes were read
         if (readBytesBD.longValue() == totalSize.longValue()) {
            finishState();
         }
      }
   }
   
   private void updateShapeProgress(BigDecimal numFeatures) {
      if (taskEntity.getState().getStatus() == Status.FINISHED) {
         finishState();
      }
      else {
         // gets current data
         int readLines = fileProcessor.getNumCurrentEntry();
         int currentNumFeature = fileProcessor.getNumCurrentEntry();

         // calculates the progress
         BigDecimal currentNumFeatureBD = BigDecimal.valueOf(currentNumFeature);
         double progress = currentNumFeatureBD.divide(numFeatures, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100)).doubleValue();

         // updates the task state
         taskEntity.getState().setReadLines(readLines);
         taskEntity.getState().setProgress(progress);

         // finishes when all bytes are read
         if (currentNumFeatureBD.longValue() == numFeatures.longValue()) {
            finishState();
         }
      }
   }
   
   private void insertCSVHeader(String schemaTableName, List<ColumnVO> fileColumns2, Integer srid) throws Throwable {
      String[] columnsLine = ((CSVFileProcessor) fileProcessor).getColumnsLine();
      
      if (columnsLine != null) {
         boolean insertedLine = remoteDataBaseService.insertCSVLine(schemaTableName, columnsLine, fileColumns, srid);
         if (!insertedLine) {
            throw new IllegalStateException("No se ha podido insertar la línea en la tabla destino");
         }
      }
      else {
         finishState();
      }
   }

   private void insertCSVLine(String schemaTableName, List<ColumnVO> fileColumns, Integer srid) throws Throwable {
      String[] line = fileProcessor.nextCSVEntry();
      
      if (line != null) {
         boolean insertedLine = remoteDataBaseService.insertCSVLine(schemaTableName, line, fileColumns, srid);
         if (!insertedLine) {
            throw new IllegalStateException("No se ha podido insertar la línea en la tabla destino");
         }
      }
      else {
         finishState();
      }
   }
   
   private void insertShapeFeature(String schemaTableName, List<ColumnVO> fileColumns, Integer srid) throws Throwable {
      SimpleFeature feature = fileProcessor.nextShapeEntry();
      
      if (feature != null) {
         boolean insertedFeature = remoteDataBaseService.insertShapeFeature(schemaTableName, feature, fileColumns, srid);
         if (!insertedFeature) {
            throw new IllegalStateException("No se ha podido insertar el feature en la tabla destino");
         }
      }
      else {
         finishState();
      }
   }
   
   public void changeStatusTo(Status status) throws Exception {
      if (status == Status.NEW) {
         initializeState();
      }
      else if (status == Status.RUNNING) {
         runningState();
      }
      else if (status == Status.FINISHED) {
         finishState();
      }
      saveTask();
   }

   private void finishState() {
      int numProcessedEntries = fileProcessor.getNumCurrentEntry();
      if ((taskEntity.getSource().getType() == FileType.CSV) && firstLineHeader) {
         numProcessedEntries--;
      }
      taskEntity.getState().setStatus(Status.FINISHED);
      taskEntity.getState().setDescription("La tarea ha finalizado de forma satisfactoria. Se han cargado "
         + numProcessedEntries + " líneas en la tabla '" + taskEntity.getTable().getName() + "'");
      taskEntity.getState().setProgress(100);
   }

   private void runningState() {
      taskEntity.getState().setStatus(Status.RUNNING);
      taskEntity.getState().setDescription("La información geográfica está siendo cargada en la tabla '"
         + getTaskEntity().getTable().getName() + "'");
      taskEntity.getState().setProgress(0);
   }

   private void initializeState() {
      TaskState state = new TaskState();
      state.setProgress(0);
      state.setTask(taskEntity);
      state.setStatus(Status.NEW);
      state.setDescription("A la espera de procesar el archivo.");
      taskEntity.setState(state);
   }
   
   public boolean createSchema(Schema schema) throws Exception {
      return remoteDataBaseService.createSchema(schema);
   }
   
   public boolean cleanTable() throws Exception {
      Table table = taskEntity.getTable();
      return remoteDataBaseService.cleanTable(table);
   }

   public boolean createTable(Table table) throws Exception {
      return remoteDataBaseService.createTable(table, fileColumns);
   }
   
   public boolean existsTableWithName(String schemaName, String tableName) throws Exception {
      return remoteDataBaseService.existsTable(schemaName, tableName);
   }
   
   public String getTaskTicket() {
      String ticket = getTaskEntity().getTicket();
      
      if (ticket == null) { // generates the ticket
        UUID uuid = UUID.randomUUID();
        ticket = uuid.toString();
        getTaskEntity().setTicket(ticket);
      }
      
      return ticket;
   }
   
   /**
    * This method creates a new source
    * object
    */
   public void resetSource() {
      localSourceData = null;
   }
   
   /**
    * @return the columns
    */
   public List<ColumnVO> getColumns() {
      return columns;
   }

   /**
    * @param columns the columns to set
    */
   public void setColumns(List<ColumnVO> columns) {
      this.columns = columns;
   }

   /**
    * @return the localSourceData
    */
   public File getLocalSourceData() {
      return localSourceData;
   }

   /**
    * @param localSourceData the localSourceData to set
    */
   public void setLocalSourceData(File localSourceData) {
      this.localSourceData = localSourceData;
   }

   /**
    * @return the tableColumns
    */
   public List<ColumnVO> getTableColumns() {
      return tableColumns;
   }

   /**
    * @param tableColumns the tableColumns to set
    */
   public void setTableColumns(List<ColumnVO> tableColumns) {
      this.tableColumns = tableColumns;
   }

   /**
    * @return the taskEntity
    */
   public Task getTaskEntity() {
      return taskEntity;
   }

   /**
    * @param taskEntity the taskEntity to set
    */
   public void setTaskEntity(Task taskEntity) {
      this.taskEntity = taskEntity;
   }

   /**
    * @param taskService the taskService to set
    */
   public void setTaskService(TaskService taskService) {
      this.taskService = taskService;
   }

   /**
    * @return the fileColumns
    */
   public List<ColumnVO> getFileColumns() {
      return fileColumns;
   }

   /**
    * @param fileColumns the fileColumns to set
    */
   public void setFileColumns(List<ColumnVO> fileColumns) {
      this.fileColumns = fileColumns;
   }

   /**
    * @return the shapeSrs
    */
   public String getShapeSrs() {
      return shapeSrs;
   }

   /**
    * @param shapeSrs the shapeSrs to set
    */
   public void setShapeSrs(String shapeSrs) {
      this.shapeSrs = shapeSrs;
   }

   /**
    * @return the firstLineHeader
    */
   public boolean isFirstLineHeader() {
      return firstLineHeader;
   }

   /**
    * @param firstLineHeader the firstLineHeader to set
    */
   public void setFirstLineHeader(boolean firstLineHeader) {
      this.firstLineHeader = firstLineHeader;
   }
}
