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
package es.juntadeandalucia.panelGestion.persistencia.dao.impl;

import java.io.Serializable;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.opengis.feature.simple.SimpleFeature;

import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RowVO;
import es.juntadeandalucia.panelGestion.persistencia.dao.RemoteDataBaseDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.utiles.TableUtils;

public class RemoteDataBaseDAOImpl implements RemoteDataBaseDAO, Serializable {

   /**
    * Generated version UID
    */
   private static final long serialVersionUID = -862257167470841424L;
   
   private JDBCConnector connector;
   
   private final static String geomFieldName = "the_geom";
   
   public RemoteDataBaseDAOImpl(JDBCConnector connector) {
      this.connector = connector;
   }

   @Override
   public boolean createSchema(String schemaName, String user) throws Exception {
      boolean created = false;
      
      /* schema name can not be parameterized so
       * applies a regular expression to avoid SQL Injection */
      if (Utils.isValidName(schemaName)) {
         String sql = "CREATE SCHEMA " + schemaName + " AUTHORIZATION \"" + user + "\"";
         connector.executeUpdate(sql);
         created = true;
      }
      else {
         throw new IllegalArgumentException("Nombre del esquena no válido");
      }
      return created;
   }
   
   @Override
   public boolean deleteSchema(String schemaName) throws Exception {
      String sql = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE;";
      connector.executeUpdate(sql);
      
      return true;
   }
   
   @Override
   public boolean updateSchemaName(String newSchemaName, String oldSchemaName) throws Exception {
      boolean updated = false;
      
      /* schema name can not be parameterized so
       * applies a regular expression to avoid SQL Injection */
      if (StringUtils.isEmpty(newSchemaName)) {
         throw new IllegalArgumentException("Nombre del esquena vacío");
      }
      else if (!Utils.isValidName(newSchemaName)) {
         throw new IllegalArgumentException("Nombre del esquena no válido");
      }
      else {
         String sql = "ALTER SCHEMA " + oldSchemaName + " RENAME TO \"" + newSchemaName + "\"";
         connector.executeUpdate(sql);
         updated = true;
         
         // removes the schema from the connection pools
         connector.removeFromConnectionPools();
      }
      
      return updated;
   }

   @Override
   public boolean updateSchemaUser(String schemaName, String schemaUser) throws Exception {
      String sql = "ALTER SCHEMA " + schemaName + " OWNER TO \"" + schemaUser + "\"";
      connector.executeUpdate(sql);

      // removes the schema from the connection pools
      connector.removeFromConnectionPools();
      
      return true;
   }
   
   @Override
   public boolean createTable(Table table, Collection<ColumnVO> columns) throws Exception {
      boolean tableCreated = false;
      if (columns.size() > 0) {
         String schemaName = table.getSchema().getName();
         String geometricField = table.getGeomField();
         String geometryType = null;
         String epsg = table.getEpsg();
         String user = table.getSchema().getUser();

         String sequenceName = TableUtils.getSequenceName(table);
         String idColumnName = TableUtils.getIdColumnName(table);
         String pkeyConstraint = TableUtils.getPKeyConstraint(table);

         String schemaTable = TableUtils.getSchemaTable(table);
         String schemaSequence = schemaName + "." + sequenceName;

         StringBuilder sqlBuilder = new StringBuilder();

         // create table
         sqlBuilder.append("CREATE TABLE ").append(schemaTable).append("(");

         // id with sequence
         sqlBuilder.append(idColumnName).append(" integer NOT NULL DEFAULT nextval('")
            .append(schemaSequence).append("'::regclass)");

         // columns
         boolean hasX = false;
         boolean hasY = false;
         for (ColumnVO column : columns) {
            if (column.isInTable()) {
               /*
                * gets the column name and replace the white characters for "_"
                */
               String columnName = column.getNameOnTable();
               if (StringUtils.isEmpty(columnName)) {
                  columnName = column.getText();
               }
               columnName = columnName.replaceAll("\\s", "_");

               String type = column.getType();
               Integer length = column.getLength();
               Integer precision = column.getPrecision();
               Integer sqlType = column.getSqlType();
               if (column.isCoordinateX()) {
                  hasX = true;
                  type = "numeric";
               }
               else if (column.isCoordinateY()) {
                  hasY = true;
                  type = "numeric";
               }
               else if (sqlType == Types.OTHER) { // geometry type
                  geometryType = column.getGeometryType().toUpperCase();
               }
               // column name
               sqlBuilder.append(", ").append(columnName);

               // column type
               sqlBuilder.append(" ").append(type);

               // column length & precision
               if ((length != null) && (precision != null)) {
                  sqlBuilder.append("(").append(length).append(",").append(precision).append(")");
               }
               else if (length != null) {
                  sqlBuilder.append("(").append(length).append(")");
               }
               else if (precision != null) {
                  sqlBuilder.append("(").append(precision).append(")");
               }
            }
         }
         if (hasX && hasY) {
            geometryType = "POINT";
            geometricField = geomFieldName;
            table.setGeomField(geometricField);
            sqlBuilder.append(", ").append(geometricField);
            sqlBuilder.append(" ").append("geometry");
         }
         else if (hasX || hasY) {
            throw new IllegalArgumentException("No ha especificado la coordenada "
               .concat(hasX?"Y":"X"));
         }

         // primary key constraint
         sqlBuilder.append(", CONSTRAINT ").append(pkeyConstraint).append(" PRIMARY KEY (")
            .append(idColumnName).append(")");

         // constraint enforce srid
         if (!StringUtils.isEmpty(geometricField)) {
            // geometryc type constraint
            sqlBuilder.append(", CONSTRAINT enforce_geotype_the_geom CHECK (geometrytype(")
               .append(geometricField).append(") = '").append(geometryType).append("'::text OR ")
               .append(geometricField).append(" IS NULL)");
            // geometry srid constraint
            Integer srid = Utils.getSRID(epsg);
            sqlBuilder.append(", CONSTRAINT enforce_srid_the_geom CHECK (st_srid(")
               .append(geometricField).append(") = ").append(srid).append(")");
         }
         sqlBuilder.append(");");

         // owner user
         sqlBuilder.append("ALTER TABLE ").append(schemaTable).append(" OWNER TO ").append(user)
            .append(";");

         // grants
         sqlBuilder.append("GRANT ALL ON TABLE ").append(schemaTable).append(" TO ").append(user)
            .append(";");
         sqlBuilder.append("GRANT SELECT ON TABLE ").append(schemaTable).append(" TO ")
            .append(user).append(";");

         String sql = sqlBuilder.toString();

         connector.executeUpdate(sql);
         tableCreated = true;
      }
      return tableCreated;
   }

   @Override
   public boolean existsSchema(String schemaName) throws Exception {
      boolean exists = false;
      
      String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?;";
      String[] params = { schemaName } ;
      int num = connector.executeCount(sql, params);
      exists = (num > 0);
      
      return exists;
   }

   @Override
   public boolean createSequence(Table table) throws Exception {
      String schemaName = table.getSchema().getName();
      String squenceName = TableUtils.getSequenceName(table);
      String user = table.getSchema().getUser();
      
      // drop and create the sequence
      String schemaSequence = schemaName + "." + squenceName;
      String sql = "DROP SEQUENCE IF EXISTS " + schemaSequence + "; CREATE SEQUENCE " + schemaSequence 
         + " INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 7 CACHE 1;";
      sql += "ALTER TABLE " + schemaSequence + " OWNER TO " + user + ";";
      
      return connector.execute(sql);
   }
   
   @Override
   public void removeSequence(Table table) throws Exception {
      String schemaName = table.getSchema().getName();
      String squenceName = TableUtils.getSequenceName(table);
      
      // drop and create the sequence
      String schemaSequence = schemaName + "." + squenceName;
      String sql = "DROP SEQUENCE IF EXISTS " + schemaSequence + ";";
      
      connector.execute(sql);
   }

   @Override
   public boolean clean(Table table) throws Exception {
      boolean cleanedTable = false;
      String schemaTable = TableUtils.getSchemaTable(table);
      
      String sql = "DELETE FROM " + schemaTable;
      
      cleanedTable = connector.execute(sql);
      
      return cleanedTable;
   }

   @Override
   public int insertCSVLine(String schemaTableName, String[] line, List<ColumnVO> columns, Integer srid) throws Throwable {
      String sqlInsert = getSQLInsertFromColumns(schemaTableName, columns, srid);
      return connector.executeLineInsertLowLevel(sqlInsert, line, columns);
   }
   
   @Override
   public int insertShapeFeature(String schemaTableName, SimpleFeature feature, List<ColumnVO> columns, Integer srid) throws Throwable {
      String sqlInsert = getSQLInsertFromColumns(schemaTableName, columns, srid);
      return connector.executeFeatureInsertLowLevel(sqlInsert, feature, columns);
   }
   
   private String getSQLInsertFromColumns(String schemaTableName, List<ColumnVO> columns, Integer srid) {
      StringBuilder sqlBuilder = new StringBuilder();
      StringBuilder columnNames = new StringBuilder();
      StringBuilder columnValues = new StringBuilder();

      // escapes column names and column values
      boolean hasX = false, hasY = false;
      columnNames.append("(");
      columnValues.append("(");
      for (int i = 0; i < columns.size(); i++) {
         ColumnVO column = columns.get(i);
         String columnName = column.getNameOnTable();
         if (StringUtils.isEmpty(columnName)) {
            columnName = column.getText();
         }
         if (StringUtils.isEmpty(columnName)) {
            throw new IllegalArgumentException("Nombre de la columna vacío");
         }
         else if (!Utils.isValidName(columnName)) {
            throw new IllegalArgumentException("Nombre de la columna no válido: " + columnName);
         }
         else {
            if (column.isCoordinateX()) {
               columnValues.append("?");
               columnNames.append(columnName);
               hasX = true;
            }
            else if (column.isCoordinateY()) {
               hasY = true;
               columnValues.append("?");
               columnNames.append(columnName);
            }
            else if ((column.getSqlType() == Types.OTHER) || column.isFromCoordinates()) {
               // geometry type
               columnNames.append(columnName);
               columnValues.append("ST_GeometryFromText(?, ").append(srid).append(")");
            }
            else {
               columnValues.append("?");
               columnNames.append(columnName);
            }
            
            if (i < (columns.size() - 1)) {
               columnNames.append(", ");
               columnValues.append(", ");
            }
         }
      }
      if (hasX && hasY) {
         columnNames.append(", ");
         columnValues.append(", ");
         columnValues.append("ST_GeometryFromText(?, ").append(srid).append(")");
         columnNames.append(geomFieldName);
      }
      columnNames.append(")");
      columnValues.append(")");

      sqlBuilder.append("INSERT INTO ").append(schemaTableName).append(" ").append(columnNames)
         .append(" VALUES ").append(columnValues).append(";");
      
      return sqlBuilder.toString();
   }

   @Override
   public List<ColumnVO> getAllColumnsExceptPKeyGeom(Table table) throws Exception {
      
      // gets all columns except geometry column 
      List<ColumnVO> columns = getAllColumnsExceptGeom(table);
      
      // removes primary key from columns
      String primaryKey = getPrimaryKey(table);
      Iterator<ColumnVO> itColumn = columns.iterator();
      while (itColumn.hasNext()) {
         ColumnVO column = itColumn.next();
         if (column.getNameOnTable().equals(primaryKey)) {
            itColumn.remove();
         }
      }
      return columns;
   }
   
   @Override
   public List<ColumnVO> getAllColumnsExceptGeom(Table table) throws Exception {
      // gets columns
      String schemaTable = TableUtils.getSchemaTable(table);
      String sql = "SELECT * FROM " + schemaTable + " WHERE 0 = 1";
      
      return connector.getColumnsMetaDataExceptGeom(sql, table.getGeomField());
   }
   
   @Override
   public List<ColumnVO> getAllColumns(Table table) throws Exception {
      // gets columns
      String schemaTable = TableUtils.getSchemaTable(table);
      String sql = "SELECT * FROM " + schemaTable + " WHERE 0 = 1";
      
      return connector.getColumnsMetaData(sql);
   }
   
   @Override
   public String getPrimaryKey(Table table) throws Exception {
      String schemaName = table.getSchema().getName();
      String tableName = table.getName();
      
      return connector.getPrimaryKey(tableName, schemaName);
   }
   
   @Override
   public void backup(Table table) throws Exception {
      String schemaTable = TableUtils.getSchemaTable(table);
      String backupName = TableUtils.getBackupSchemaTable(table);
      String user = table.getSchema().getUser();
      
      // DROP PREVIOUS BACKUP IF EXISTS
      if (existsTable(table.getSchema().getName(), TableUtils.getBackupTable(table))) {
         deleteTable(backupName);
      }
      // CREATES THE BACKUP TABLE
      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("CREATE TABLE ").append(backupName)
            .append(" AS SELECT * FROM ").append(schemaTable);
      sqlBuilder.append(";ALTER TABLE ").append(backupName)
         .append(" OWNER TO ").append(user).append(";");

      // grants
      sqlBuilder.append("GRANT ALL ON TABLE ").append(backupName)
         .append(" TO ").append(user).append(";");
      sqlBuilder.append("GRANT SELECT ON TABLE ").append(backupName)
         .append(" TO ").append(user).append(";");
      
      connector.execute(sqlBuilder.toString());
   }
   
   @Override
   public void deleteTable(String schemaTable) throws Exception {
      connector.execute("DROP TABLE ".concat(schemaTable).concat(" CASCADE;"));
   }

   @Override
   public boolean existsTable(String schemaName, String tableName) throws Exception {
      String[] params = {schemaName, tableName};
      return connector.executeExists("SELECT * FROM information_schema.tables  WHERE table_schema = ? AND table_name = ?;", params);
   }

   @Override
   public void restoreBackup(Table table) throws Exception {
      String schemaTable = TableUtils.getSchemaTable(table);
      String backupName = TableUtils.getBackupSchemaTable(table);
      
      // clean the table
      clean(table);
      
      // inserts data from backup
      String sql = "INSERT INTO ".concat(schemaTable).concat(" SELECT * FROM ").concat(backupName).concat(";");
      connector.execute(sql);
      
      // removes the backup
      deleteTable(backupName);
   }

   @Override
   public int countRows(String schemaTable) throws Exception {
      int numRows = -1;

      String sql = "SELECT COUNT(*) FROM ".concat(schemaTable).concat(";");
      numRows = connector.executeCount(sql);
      
      return numRows;
   }

   @Override
   public List<RowVO> getRows(Table table, int numRows) throws Exception {
      
      List<ColumnVO> columns = getAllColumns(table);
      String sql = "SELECT * FROM ".concat(TableUtils.getSchemaTable(table))
         .concat(" LIMIT ").concat(String.valueOf(numRows)).concat(";");
      
      return connector.getRows(sql, columns);
   }

   @Override
   public String getGeomtryType(String schema, String table, String geometryField) throws Exception {
      String sql = "SELECT * FROM geometry_columns WHERE f_table_schema = '"
         .concat(schema).concat("' AND f_table_name = '").concat(table).concat("' AND f_geometry_column = '").concat(geometryField).concat("';");
      return connector.getGeometryType(sql);
   }
}
