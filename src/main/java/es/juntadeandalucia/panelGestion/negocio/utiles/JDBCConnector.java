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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.opengis.feature.simple.SimpleFeature;

import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RowVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
/**
 * This class is the base of data base connectors to create
 * dynamically a JDBC to an specific connection
 *
 * @author GUADALTEL S.A
 */
public final class JDBCConnector {
   
   private static Logger log = Logger.getLogger(JDBCConnector.class);
   
   /**
    * Driver class name for PostgreSQL data base
    */
   private static final String postgresqlDriverClass = "org.postgresql.Driver";
   
   /**
    * Driver class name for Oracle data base
    */
   private static final String oracleDriverClass = "oracle.jdbc.driver.OracleDriver";
   
   /**
    * Driver class name for MySQL data base
    */
   private static final String mysqlDriverClass = "com.mysql.jdbc.Driver";
   
   /**
    * Supported data base types
    */
   public static enum DataBaseType {
      /**
       * Type Oracle
       */
      ORACLE,
      
      /**
       * Type PostgreSQL
       */
      POSTGRESQL,
      
      /**
       * Type MySQL
       */
      MYSQL
   };
   
   /**
    * Execution type to use
    */
   private static enum ExecutionType {
      /**
       * Select query
       */
      SELECT,
      
      /**
       * Query to insert or update
       */
      INSERT_OR_UPDATE,
      
      /**
       * Query to execute some procedure or function
       */
      EXECUTE,
      
      /**
       * Query to count the rows
       */
      COUNT,
      
      /**
       * Query to check if exists
       */
      EXISTS
   };
   
   private static Map<Long, DataSource> poolDataSources;
   
   private Long schemaId;
   
   public JDBCConnector(Table table) throws Exception {
      this(table.getSchema());
   }
   
   public JDBCConnector(Schema schema) throws Exception {
      this(schema, DataBaseType.POSTGRESQL);
   }
   
   /**
    * Constructor
    * 
    * @param dataBase DataBaseVO object this JDBCConnector will
    * connect to
    * @param type the base data type
    * @throws Exception thrown by the jndi
    */
   public JDBCConnector(Schema schema, DataBaseType type) throws Exception {
      if (poolDataSources == null) {
         Class.forName("org.postgresql.Driver");
         poolDataSources = new HashMap<Long, DataSource>();
      }
      // gets JDBC pool for the connection
      schemaId = schema.getId();
      synchronized (poolDataSources) {
         DataSource dataSource = poolDataSources.get(schemaId);
         /* if there is not any configured JDBC pool 
          * or the schema is not created we create a new one
          */
         if ((schemaId == 0) || (dataSource == null)) {
            // gets connection properties
            String connectionUrl = schema.getDataBase().getConnectionUrl();
            String user = schema.getUser();
            String password = schema.getPassword();
            String driverClass;
            if (type == DataBaseType.POSTGRESQL) {
               driverClass = postgresqlDriverClass;
            }
            else if (type == DataBaseType.ORACLE) {
               driverClass = oracleDriverClass;
            }
            else if (type == DataBaseType.MYSQL) {
               driverClass = mysqlDriverClass;
            }
            else {
               throw new IllegalArgumentException("tipo de base de datos no soportada " + type);
            }
            
            /************** POOL PROPERTIES **************/
            PoolProperties poolProperties = new PoolProperties();
            // conection properties
            poolProperties.setUrl(connectionUrl);
            poolProperties.setDriverClassName(driverClass);
            poolProperties.setUsername(user);
            poolProperties.setPassword(password);
            // validation interval
            String validationInterval = PanelSettings.poolProperties.get("validationInterval");
            if (!StringUtils.isEmpty(validationInterval)) {
               poolProperties.setValidationInterval(Integer.parseInt(validationInterval));
            }
            // time between eviction runs milliseconds
            String timeBetweenEvictionRunsMillis = PanelSettings.poolProperties.get("timeBetweenEvictionRunsMillis");
            if (!StringUtils.isEmpty(timeBetweenEvictionRunsMillis)) {
               poolProperties.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));
            }
            // max active
            String maxActive = PanelSettings.poolProperties.get("maxActive");
            if (!StringUtils.isEmpty(maxActive)) {
               poolProperties.setMaxActive(Integer.parseInt(maxActive));
            }
            // initial size
            String initialSize = PanelSettings.poolProperties.get("initialSize");
            if (!StringUtils.isEmpty(initialSize)) {
               poolProperties.setInitialSize(Integer.parseInt(initialSize));
            }
            // max wait
            String maxWait = PanelSettings.poolProperties.get("maxWait");
            if (!StringUtils.isEmpty(maxWait)) {
               poolProperties.setMaxWait(Integer.parseInt(maxWait));
            }
            // removed abandoned timeout
            String removeAbandonedTimeout = PanelSettings.poolProperties.get("removeAbandonedTimeout");
            if (!StringUtils.isEmpty(removeAbandonedTimeout)) {
               poolProperties.setRemoveAbandonedTimeout(Integer.parseInt(removeAbandonedTimeout));
            }
            // min evictable idle time milliseconds
            String minEvictableIdleTimeMillis = PanelSettings.poolProperties
               .get("minEvictableIdleTimeMillis");
            if (!StringUtils.isEmpty(minEvictableIdleTimeMillis)) {
               poolProperties.setMinEvictableIdleTimeMillis(Integer
                  .parseInt(minEvictableIdleTimeMillis));
            }
            // min idle
            String minIdle = PanelSettings.poolProperties.get("minIdle");
            if (!StringUtils.isEmpty(minIdle)) {
               poolProperties.setMinIdle(Integer.parseInt(minIdle));
            }
            // remove abandoned
            String removeAbandoned = PanelSettings.poolProperties.get("removeAbandoned");
            if (!StringUtils.isEmpty(removeAbandoned)) {
               poolProperties.setRemoveAbandoned(Boolean.parseBoolean(removeAbandoned));
            }
            // JDBC interceptors
            String jdbcInterceptors = PanelSettings.poolProperties.get("jdbcInterceptors");
            if (!StringUtils.isEmpty(jdbcInterceptors)) {
               poolProperties.setJdbcInterceptors(jdbcInterceptors);
            }
            
            // sets the connection properties of the data source
            dataSource = new DataSource();
            dataSource.setPoolProperties(poolProperties);
            dataSource.setUrl(connectionUrl);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(driverClass);
            
            // adds the pool to the map
            poolDataSources.put(schemaId, dataSource);
         }
      }
   }
   
   /**
    * This method removes the connection pools for
    * the specified schema id because that schema
    * changed and it needs to define a new pool
    */
   public void removeFromConnectionPools() {
      if (schemaId != null) {
         poolDataSources.remove(schemaId);
      }
   }

   /**
    * This method executes a JDBC "executeQuery" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * 
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public ResultSet executeQuery(String sql) throws Exception {
      return executeQuery(sql, null);
   }
   
   /**
    * This method executes a JDBC "execute" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * @param params the parameters for the query
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public ResultSet executeQuery(String sql, String[] params) throws Exception {
      return (ResultSet) executeBase(sql, params, ExecutionType.SELECT);
   }
   
   /**
    * This method executes a JDBC "executeUpdate" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * 
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public int executeUpdate(String sql) throws Exception {
      return executeUpdate(sql, null);
   }
   
   /**
    * This method executes a JDBC "SQL COUNT" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * 
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public int executeCount(String sql) throws Exception {
      return executeCount(sql, null);
   }
   
   /**
    * This method executes a JDBC "EXISTS" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * 
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public boolean executeExists(String sql) throws Exception {
      return executeExists(sql, null);
   }
   
   /**
    * This method executes a JDBC "executeUpdate" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * @param params the parameters for the query
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public int executeUpdate(String sql, String[] params) throws Exception {
      return (Integer) executeBase(sql, params, ExecutionType.INSERT_OR_UPDATE);
   }
   
   /**
    * This method executes a JDBC "execute" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * 
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public Boolean execute(String sql) throws Exception {
      return execute(sql, null);
   }
   
   /**
    * This method executes a JDBC "execute" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * @param params the parameters for the query
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public Boolean execute(String sql, String[] params) throws Exception {
      return (Boolean) executeBase(sql, params, ExecutionType.EXECUTE);
   }
   
   /**
    * This method executes a JDBC "execute" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * @param params the parameters for the query
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public int executeCount(String sql, String[] params) throws Exception {
      return (Integer) executeBase(sql, params, ExecutionType.COUNT);
   }
   
   /**
    * This method executes a JDBC "EXISTS" operation on
    * the configured data base
    *
    * @param sql the SQL sentence to execute
    * @param params the parameters for the query
    * @return result of the SQL query
    * @throws Exception if some error occurred
    */
   public boolean executeExists(String sql, String[] params) throws Exception {
      return (Boolean) executeBase(sql, params, ExecutionType.EXISTS);
   }
   
   /**
    * This method is the execution base. It depends on the execution type
    *
    * @param sql the SQL sentence to execute
    * @param params the parameters for the query
    * @param executionType type of the execution
    * 
    * @return the result of the query execution
    * @throws Exception if some error occurred
    */
   private Object executeBase(String sql, String[] params, ExecutionType executionType)
      throws Exception {
      Exception error = null;
      
      Object result = null;
      
      Connection connection = null;
      PreparedStatement preparedStmnt = null;
      
      try {
         DataSource dataSource = poolDataSources.get(schemaId);
         connection = dataSource.getConnection();
         connection.setAutoCommit(false);
         
         preparedStmnt = connection.prepareStatement(sql);
         if (params != null) {
            for (int i = 0; i < params.length; i++) {
               String param = params[i];
               preparedStmnt.setString(i + 1, param);
            }
         }
         result = executePreparedStatement(preparedStmnt, executionType);
         
         connection.commit();
      }
      catch (SQLException e) {
         error = e;
      }
      finally {
         if (preparedStmnt != null) {
            try {
               preparedStmnt.close();
            }
            catch (SQLException se2) {
               log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
            }
         }
         if (connection != null) {
            try {
               if (error != null) {
                  connection.rollback();
               }
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
            }
            try {
               connection.close();
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
            }
         }
      }
      
      if (error != null) {
         throw error;
      }
      return result;
   }

   /**
    * This method executes the prepared statement depending on
    * the execution type
    *
    * @param preparedStmnt PreparedStatement object for the query
    * @param executionType type of the execution
    * @return result of the execution
    * @throws SQLException exception thrown by the JDBC driver
    */
   private Object executePreparedStatement(PreparedStatement preparedStmnt,
      ExecutionType executionType) throws SQLException {
      Object result = null;
      if (executionType == ExecutionType.SELECT) {
         result = preparedStmnt.executeQuery();
      }
      else if (executionType == ExecutionType.INSERT_OR_UPDATE) {
         result = preparedStmnt.executeUpdate();
      }
      else if (executionType == ExecutionType.EXECUTE) {
         preparedStmnt.execute();
         result = true;
      }
      else if (executionType == ExecutionType.COUNT) {
         ResultSet rs = preparedStmnt.executeQuery();
         while (rs.next()) {
            result = rs.getInt(1);
         }
      }
      else if (executionType == ExecutionType.EXISTS) {
         ResultSet rs = preparedStmnt.executeQuery();
         result = rs.next();
      }
      return result;
   }
   
   /**
    * This method executes a low level insert with the data values
    * and data types (java.sql.Types) specified in a Map.
    * This is a way to improve the performance of data insertion.
    *
    * @param sql escaped SQL to avoid SQL-I
    * @param line the line to inser
    * @param columns of the table
    * @return number of affected rows
    * @throws Exception exception thrown
    */
   public int executeLineInsertLowLevel(String sql, String[] line, List<ColumnVO> columns)
      throws Exception {
      Exception error = null;

      int numRowsAffected = 0;

      if (columns.size() > 0) {
         Connection connection = null;
         PreparedStatement preparedStmnt = null;

         try {
            DataSource dataSource = poolDataSources.get(schemaId);
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            preparedStmnt = connection.prepareStatement(sql);

            String coordinateX = null;
            String coordinateY = null;
            int paramPosition = 1;
            for (ColumnVO column : columns) {
               Integer filePosition = column.getFilePosition();
               
               String dataValue;
               Integer dataType = column.getSqlType();
               
               if (column.isCoordinateX()) {
                  dataValue = line[filePosition];
                  coordinateX = dataValue;
                  preparedStmnt.setObject(paramPosition, dataValue, dataType);
               }
               else if (column.isCoordinateY()) {
                  dataValue = line[filePosition];
                  coordinateY = dataValue;
                  preparedStmnt.setObject(paramPosition, dataValue, dataType);
               }
               else if (column.isFromCoordinates()) {
                  int coordXIndex = column.getFileCoordinateXPosition();
                  int coordYIndex = column.getFileCoordinateYPosition();
                  coordinateX = line[coordXIndex];
                  coordinateY = line[coordYIndex];
                  continue;
               }
               else if (dataType == Types.OTHER) { // it is a geometry
                  // ((org.postgresql.PGConnection)connection).addDataType(column.getName(),
                  // column.getTypeClass());
                  dataValue = line[filePosition];
                  preparedStmnt.setObject(paramPosition, dataValue);
               }
               else {
                  dataValue = line[filePosition];
                  if (StringUtils.isEmpty(dataValue)) {
                     preparedStmnt.setNull(paramPosition, dataType);
                  }
                  else {
                     preparedStmnt.setObject(paramPosition, dataValue, dataType);
                  }
               }
               paramPosition++;
            }
            if ((coordinateX != null) && (coordinateY != null)) {
               String pointWKT = Utils.getPointWKTFromCoordinates(coordinateX, coordinateY);
               preparedStmnt.setObject(paramPosition, pointWKT);
            }
            numRowsAffected = preparedStmnt.executeUpdate();
            
            connection.commit();
         }
         catch (SQLException e) {
            error = e;
         }
         finally {
            if (preparedStmnt != null) {
               try {
                  preparedStmnt.close();
               }
               catch (SQLException se2) {
                  log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
               }
            }
            if (connection != null) {
               try {
                  if (error != null) {
                     connection.rollback();
                  }
               }
               catch (SQLException se) {
                  log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
               }
               try {
                  connection.close();
               }
               catch (SQLException se) {
                  log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
               }
            }
         }
         if (error != null) {
            throw error;
         }
      }
      return numRowsAffected;
   }
   
   public int executeFeatureInsertLowLevel(String sql, SimpleFeature feature,
         List<ColumnVO> columns) throws Exception {
      Exception error = null;

      int numRowsAffected = 0;

      if (columns.size() > 0) {
         Connection connection = null;
         PreparedStatement preparedStmnt = null;

         try {
            DataSource dataSource = poolDataSources.get(schemaId);
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            preparedStmnt = connection.prepareStatement(sql);

            int paramPosition = 1;
            for (ColumnVO column : columns) {
               String dataValue = null;
               Object attribute = feature.getAttribute(column.getFilePosition());
               if (attribute != null) {
                  dataValue = attribute.toString();
               }
               Integer dataType = column.getSqlType();
               if (dataType == Types.OTHER) { // it is a geometry
                  // ((org.postgresql.PGConnection)connection).addDataType(column.getName(),
                  // column.getTypeClass());
                  preparedStmnt.setObject(paramPosition, dataValue);
               }
               else {
                  if (StringUtils.isEmpty(dataValue)) {
                     preparedStmnt.setNull(paramPosition, dataType);
                  }
                  else {
                     preparedStmnt.setObject(paramPosition, dataValue, dataType);
                  }
               }
               paramPosition++;
            }

            numRowsAffected = preparedStmnt.executeUpdate();
            
            connection.commit();
         }
         catch (SQLException e) {
            error = e;
         }
         finally {
            if (preparedStmnt != null) {
               try {
                  preparedStmnt.close();
               }
               catch (SQLException se2) {
                  log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
               }
            }
            if (connection != null) {
               try {
                  if (error != null) {
                     connection.rollback();
                  }
               }
               catch (SQLException se) {
                  log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
               }
               try {
                  connection.close();
               }
               catch (SQLException se) {
                  log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
               }
            }
         }
         if (error != null) {
            throw error;
         }
      }
      return numRowsAffected;
   }

   public List<ColumnVO> getColumnsMetaDataExceptGeom(String sql, String geometryName) throws Exception {
      Exception error = null;

      List<ColumnVO> tableColumns = new LinkedList<ColumnVO>();

      Connection connection = null;
      PreparedStatement preparedStmnt = null;

      try {
         DataSource dataSource = poolDataSources.get(schemaId);
         connection = dataSource.getConnection();
         connection.setAutoCommit(false);
         
         preparedStmnt = connection.prepareStatement(sql);
         ResultSet rs = preparedStmnt.executeQuery();
         ResultSetMetaData rsmd = rs.getMetaData();
         String geometryFieldName = geometryName;
         if (StringUtils.isEmpty(geometryFieldName)) {
            geometryFieldName = "the_geom";
         }
         for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i);
            if (!columnName.equals(geometryFieldName)){
            String columnType = rsmd.getColumnTypeName(i);
                int columnSqlType = rsmd.getColumnType(i);
                int columnLength = rsmd.getColumnDisplaySize(i);
                int columnPrecision = rsmd.getPrecision(i);

                ColumnVO column = new ColumnVO();
                column.setNameOnTable(columnName);
                column.setType(columnType);
                column.setSqlType(columnSqlType);
                column.setLength(columnLength);
                column.setPrecision(columnPrecision);
                column.setInTable(true);
                
                tableColumns.add(column);
            }
            
         }
      }
      catch (SQLException e) {
         error = e;
      }
      finally {
         if (preparedStmnt != null) {
            try {
               preparedStmnt.close();
            }
            catch (SQLException se2) {
               log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
            }
         }
         if (connection != null) {
            try {
               if (error != null) {
                  connection.rollback();
               }
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
            }
            try {
               connection.close();
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
            }
         }
      }
      if (error != null) {
         throw error;
      }
      return tableColumns;
   }
   
   public List<ColumnVO> getColumnsMetaData(String sql) throws Exception {
      Exception error = null;

      List<ColumnVO> tableColumns = new LinkedList<ColumnVO>();

      Connection connection = null;
      PreparedStatement preparedStmnt = null;

      try {
         DataSource dataSource = poolDataSources.get(schemaId);
         connection = dataSource.getConnection();
         connection.setAutoCommit(false);

         preparedStmnt = connection.prepareStatement(sql);
         ResultSet rs = preparedStmnt.executeQuery();
         ResultSetMetaData rsmd = rs.getMetaData();
         for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i);
            String columnType = rsmd.getColumnTypeName(i);
            int columnSqlType = rsmd.getColumnType(i);
            int columnLength = rsmd.getColumnDisplaySize(i);
            int columnPrecision = rsmd.getPrecision(i);

            ColumnVO column = new ColumnVO();
            column.setNameOnTable(columnName);
            column.setType(columnType);
            column.setSqlType(columnSqlType);
            column.setLength(columnLength);
            column.setPrecision(columnPrecision);
            column.setInTable(true);

            tableColumns.add(column);
         }
      }
      catch (SQLException e) {
         error = e;
      }
      finally {
         if (preparedStmnt != null) {
            try {
               preparedStmnt.close();
            }
            catch (SQLException se2) {
               log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
            }
         }
         if (connection != null) {
            try {
               if (error != null) {
                  connection.rollback();
               }
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al manejar la conexión: ".concat(se
                  .getLocalizedMessage()));
            }
            try {
               connection.close();
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se
                  .getLocalizedMessage()));
            }
         }
      }
      if (error != null) {
         throw error;
      }
      return tableColumns;
   }
   
   public String getPrimaryKey(String tableName, String schemaName) throws Exception {
      Exception error = null;

      String primaryKey = null;
      
      Connection connection = null;

      try {
         DataSource dataSource = poolDataSources.get(schemaId);
         connection = dataSource.getConnection();
         connection.setAutoCommit(false);
         
         // get primary keys
         DatabaseMetaData dataBaseMetaData = connection.getMetaData();
         ResultSet rs = dataBaseMetaData.getPrimaryKeys(null, schemaName, tableName);
         if (rs.next()) {
            primaryKey = rs.getString("column_name");
         }
      }
      catch (SQLException e) {
         error = e;
      }
      finally {
         if (connection != null) {
            try {
               if (error != null) {
                  connection.rollback();
               }
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
            }
            try {
               connection.close();
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
            }
         }
      }
      if (error != null) {
         throw error;
      }
      return primaryKey;
   }

   public List<RowVO> getRows(String sql, List<ColumnVO> columns) throws Exception {
      List<RowVO> rows = new LinkedList<RowVO>();
      
      Exception error = null;
      
      Connection connection = null;
      PreparedStatement preparedStmnt = null;
      
      try {
         DataSource dataSource = poolDataSources.get(schemaId);
         connection = dataSource.getConnection();
         connection.setAutoCommit(false);
         
         preparedStmnt = connection.prepareStatement(sql);
         ResultSet result = preparedStmnt.executeQuery();
         while (result.next()) {
            RowVO row = new RowVO();
            for (ColumnVO column : columns) {
               String columnName = column.getNameOnTable();
               String columnValue = null;
               Object columnValueObj = result.getObject(columnName);
               if (columnValueObj != null) {
                  columnValue = columnValueObj.toString();
                  
               }
               
               /* if the column is a geometry type then set the
                * geometry column name of the row */
               if (column.getSqlType() == Types.OTHER) {
                  row.setGeomFieldName(columnName);
               }
               row.addField(columnName, columnValue);   
            }
            rows.add(row);
         }
      }
      catch (SQLException e) {
         error = e;
      }
      finally {
         if (preparedStmnt != null) {
            try {
               preparedStmnt.close();
            }
            catch (SQLException se2) {
               log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
            }
         }
         if (connection != null) {
            try {
               if (error != null) {
                  connection.rollback();
               }
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
            }
            try {
               connection.close();
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
            }
         }
      }
      
      if (error != null) {
         throw error;
      }
      
      return rows;
   }
   /**
    * Method for all tables of a database schema 
    * @param schema
    * @return list of tables
    * @return list tables
    * @throws Exception 
    */   
   public List<Table> tablesByShema(Schema schema) throws Exception {
	    Exception error = null;

	    List<Table> tablesBySchema = new LinkedList<Table>();

	    Connection connection = null;
	    PreparedStatement preparedStmnt = null;
	    try {
	       DataSource dataSource = poolDataSources.get(schemaId);
	       connection = dataSource.getConnection();
	       connection.setAutoCommit(false);
	       //Se actualiza la vista geometry_columns para hacer la consulta con datos actualizados
	      /* try {
	       	   PreparedStatement preparedStmntActualizeGC = null;
		       preparedStmntActualizeGC = connection.prepareStatement("SELECT Populate_Geometry_Columns(true)");
		       preparedStmntActualizeGC.execute();
	       } catch (Exception oops) {
	    	   log.info("No se a podido actualizar la tabla geometry_columns");
	       }*/
	       
	       //Se seleccionan todas las tablas de la base de datos distinguiendo de las vistas y las tablas propias del sistema.
	       preparedStmnt = connection.prepareStatement("SELECT * FROM geometry_columns WHERE f_table_name IN (SELECT table_name"+
	    		   " FROM information_schema.columns"+
	    		   " WHERE udt_name = 'geometry'  AND table_name IN (SELECT table_name FROM information_schema.tables )) AND f_geometry_column <> 'extent';");//AND srid > 0
	      
	       ResultSet rs = preparedStmnt.executeQuery();
	       while (rs.next()) {
            	  Table table = new Table();
	              table.setName(rs.getString("f_table_name"));
	              table.setCreationDate(new Date());
	              table.setGeomField(rs.getString("f_geometry_column"));
	              table.setEpsg("EPSG:".concat(rs.getString("srid")));
	              table.setModificationDate(null);
	              table.setTableXservices(null);
	              table.setTasks(null);
	              table.setSchema(schema);
	              tablesBySchema.add(table);
	        }
	    }
	    catch (SQLException e) {
	       error = e;
	    }
	    finally {
	       if (preparedStmnt != null) {
	          try {
	             preparedStmnt.close();
	          }
	          catch (SQLException se2) {
	             log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
	          }
	       }
	       if (connection != null) {
	          try {
	             if (error != null) {
	                connection.rollback();
	             }
	          }
	          catch (SQLException se) {
	             log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
	          }
	          try {
	             connection.close();
	          }
	          catch (SQLException se) {
	             log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
	          }
	       }
	    }
	    if (error != null) {
	       throw error;
	    }
	    return tablesBySchema;
	 }

   /**
    * TODO
    *
    * @param sql
    * @return
    */
   public String getGeometryType(String sql) throws Exception {
      Exception error = null;

      String geometryType = null;
      
      Connection connection = null;
      PreparedStatement preparedStmnt = null;
      
      try {
         DataSource dataSource = poolDataSources.get(schemaId);
         connection = dataSource.getConnection();
         connection.setAutoCommit(false);
         
         preparedStmnt = connection.prepareStatement(sql);
         ResultSet result = preparedStmnt.executeQuery();
         while (result.next()) {
            geometryType = result.getString("type");
         }
      }
      catch (SQLException e) {
         error = e;
      }
      finally {
         if (connection != null) {
            try {
               if (error != null) {
                  connection.rollback();
               }
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
            }
            try {
               connection.close();
            }
            catch (SQLException se) {
               log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
            }
         }
      }
      if (error != null) {
         throw error;
      }
      return geometryType;
   }
   
//   public List<Table> tablesByShema(Schema schema) throws Exception {
//	    Exception error = null;
//	    
//	    List<Table> tablesBySchema = new LinkedList<Table>();
//
//	    Connection connection = null;
//	    PreparedStatement preparedStmnt = null;
//	    PreparedStatement preparedStmntAux = null;
//	    try {
//	       DataSource dataSource = poolDataSources.get(schemaId);
//	       connection = dataSource.getConnection();
//	       connection.setAutoCommit(false);
//	       //Se seleccionan todas las tablas de la base de datos distinguiendo de las vistas y las tablas propias del sistema.
//	       preparedStmnt = connection.prepareStatement("SELECT distinct *"+
//	    	" FROM information_schema.columns"+
//			" WHERE udt_name = 'geometry';");
//	      
//	       ResultSet rs = preparedStmnt.executeQuery();
//	       String name_table = "";
//	       String column_geometry = "";
//	       while (rs.next()) {//Se recorren todas las tablas con geometria
//	    	   name_table = rs.getString("table_name");
//	    	   column_geometry = rs.getString("column_name");
//	    	   preparedStmntAux = connection.prepareStatement("SELECT distinct" + 
//	    	   " st_srid("+column_geometry+"), GeometryType("+column_geometry+") FROM \""+name_table+"\" LIMIT 1;");
//	    	   ResultSet rsAux = preparedStmntAux.executeQuery();
//	    	   while (rsAux.next()) {//Se hace consulta para cada tabla para obtener el tipo de geometria y el srid
//	    		   Table table = new Table();
//	              table.setName(name_table);
//	              table.setCreationDate(new Date());
//	              table.setGeomField(rsAux.getString("geometrytype"));
//	              table.setEpsg("EPSG:".concat(String.valueOf(rsAux.getString("st_srid"))));
//	              table.setModificationDate(null);
//	              table.setTableXservices(null);
//	              table.setTasks(null);
//	              table.setSchema(schema);
//	              tablesBySchema.add(table);
//	              break;
//	    	   }
//	        }
//	    }
//	    catch (SQLException e) {
//	       error = e;
//	    }
//	    finally {
//	       if (preparedStmnt != null) {
//	          try {
//	             preparedStmnt.close();
//	          }
//	          catch (SQLException se2) {
//	             log.warn("No se pudo cerrar el statment: ".concat(se2.getLocalizedMessage()));
//	          }
//	       }
//	       if (connection != null) {
//	          try {
//	             if (error != null) {
//	                connection.rollback();
//	             }
//	          }
//	          catch (SQLException se) {
//	             log.warn("Se produjo un error al manejar la conexión: ".concat(se.getLocalizedMessage()));
//	          }
//	          try {
//	             connection.close();
//	          }
//	          catch (SQLException se) {
//	             log.warn("Se produjo un error al intentar cerrar la conexión: ".concat(se.getLocalizedMessage()));
//	          }
//	       }
//	    }
//	    if (error != null) {
//	       throw error;
//	    }
//	    return tablesBySchema;
//	 }
//   
//   
//   
   
   
}
