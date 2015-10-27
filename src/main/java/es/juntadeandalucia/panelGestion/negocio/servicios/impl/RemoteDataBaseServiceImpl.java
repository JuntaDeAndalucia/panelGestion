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
package es.juntadeandalucia.panelGestion.negocio.servicios.impl;

import java.util.Collection;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RowVO;
import es.juntadeandalucia.panelGestion.negocio.vo.SchemaVO;
import es.juntadeandalucia.panelGestion.persistencia.dao.RemoteDataBaseDAO;
import es.juntadeandalucia.panelGestion.persistencia.dao.impl.RemoteDataBaseDAOImpl;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.utiles.TableUtils;

public class RemoteDataBaseServiceImpl implements RemoteDataBaseService {
   public RemoteDataBaseDAO remoteDataBaseDao;
   
   public RemoteDataBaseServiceImpl(JDBCConnector connector) {
      this.remoteDataBaseDao = new RemoteDataBaseDAOImpl(connector);
   }
   
   @Override
   public boolean createSchema(Schema schema) throws Exception {
      boolean created = false;
      String schemaName = schema.getName();
      if (!remoteDataBaseDao.existsSchema(schemaName)) {
         String user = schema.getUser();
         created = remoteDataBaseDao.createSchema(schemaName, user);
      }
      return created;
   }
   
   @Override
   public boolean deleteSchema(Schema schema) throws Exception {
      boolean deleted = false;
      String schemaName = schema.getName();
      if (remoteDataBaseDao.existsSchema(schemaName)) {
         deleted = remoteDataBaseDao.deleteSchema(schemaName);
      }
      return deleted;
   }
   
   @Override
   public boolean updateSchema(SchemaVO schemaVO) throws Exception {
      boolean modified = false;

      String schemaName = schemaVO.getSchemaEntity().getName();
      String oldSchemaName = schemaVO.getOldName();
      String schemaUser = schemaVO.getSchemaEntity().getUser();
      String oldSchemaUser = schemaVO.getOldUser();
      
      // updates schema name
      if (!schemaName.equalsIgnoreCase(oldSchemaName)) {
         modified = remoteDataBaseDao.updateSchemaName(schemaName, oldSchemaName);
      }
      // updates schema user
      if (!schemaUser.equalsIgnoreCase(oldSchemaUser)) {
         modified = remoteDataBaseDao.updateSchemaUser(schemaName, schemaUser);
      }

      return modified;
   }

   @Override
   public boolean createTable(Table table, Collection<ColumnVO> columns) throws Exception {
      boolean tableCreated = false;
      boolean sequenceCreated = remoteDataBaseDao.createSequence(table);
      if (sequenceCreated) {
         tableCreated = remoteDataBaseDao.createTable(table, columns);
         if (!tableCreated) {
            remoteDataBaseDao.removeSequence(table);
         }
      }
      return tableCreated;
   }

   @Override
   public boolean insertCSVLine(String schemaTableName, String[] line, List<ColumnVO> columns, Integer srid) throws Throwable {
      int numRowsAffected = remoteDataBaseDao.insertCSVLine(schemaTableName, line, columns, srid);
      return (numRowsAffected > 0);
   }

   @Override
   public boolean cleanTable(Table table) throws Exception {
      boolean cleanedTable = remoteDataBaseDao.clean(table);
      return cleanedTable;
   }

   @Override
   public List<ColumnVO> getAllColumnsExceptPKeyGeom(Table table) throws Exception {
      return remoteDataBaseDao.getAllColumnsExceptPKeyGeom(table);
   }

   @Override
   public boolean existsTable(String schemaName, String tableName) throws Exception {
      return remoteDataBaseDao.existsTable(schemaName, tableName);
   }

   @Override
   public boolean existsSchema(String schemaName) throws Exception {
      return remoteDataBaseDao.existsSchema(schemaName);
   }

   @Override
   public boolean insertShapeFeature(String schemaTableName, SimpleFeature feature,
      List<ColumnVO> columns, Integer srid) throws Throwable {
      int numRowsAffected = remoteDataBaseDao.insertShapeFeature(schemaTableName, feature, columns, srid);
      return (numRowsAffected > 0);
   }

   @Override
   public void backup(Table table) throws Exception {
      remoteDataBaseDao.backup(table);
   }
   
   @Override
   public void removeBackup(Table table) throws Exception {
      String tableSchemaBackupName = TableUtils.getBackupSchemaTable(table);
      remoteDataBaseDao.deleteTable(tableSchemaBackupName);
   }

   @Override
   public void deleteTable(Table table) throws Exception {
      String schemaTable = TableUtils.getSchemaTable(table);
      // removes sequence and table
      remoteDataBaseDao.deleteTable(schemaTable);
      remoteDataBaseDao.removeSequence(table);
   }

   @Override
   public void restoreBackup(Table table) throws Exception {
      remoteDataBaseDao.restoreBackup(table);
   }

   @Override
   public int countRows(Table table) throws Exception {
      String schemaTable = TableUtils.getSchemaTable(table);
      return remoteDataBaseDao.countRows(schemaTable);
   }

   @Override
   public String getPrimaryKey(Table table) throws Exception {
      return remoteDataBaseDao.getPrimaryKey(table);
   }

   @Override
   public List<RowVO> getRows(Table table, int numRows) throws Exception {
      return remoteDataBaseDao.getRows(table, numRows);
   }

   @Override
   public List<ColumnVO> getAllColumns(Table table) throws Exception {
      return remoteDataBaseDao.getAllColumns(table);
   }

   @Override
   public String getGeomtryType(Table table) throws Exception {
      return remoteDataBaseDao.getGeomtryType(table.getSchema().getName(), table.getName(), table.getGeomField());
   }
}
