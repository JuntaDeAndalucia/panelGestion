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
package es.juntadeandalucia.panelGestion.persistencia.dao;

import java.util.Collection;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RowVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

public interface RemoteDataBaseDAO {

   public boolean createSchema(String schemaName, String user) throws Exception;
   
   public boolean deleteSchema(String schemaName) throws Exception;
   
   public boolean updateSchemaName(String newSchemaName, String oldSchemaName) throws Exception;
   
   public boolean updateSchemaUser(String newSchemaName, String newSchemaUser) throws Exception;
   
   public boolean createTable(Table table, Collection<ColumnVO> columns) throws Exception;
   
   public boolean createSequence(Table table) throws Exception;

   public boolean existsSchema(String schemaName) throws Exception;

   public boolean clean(Table table) throws Exception;
   
   public void removeSequence(Table table) throws Exception;

   public List<ColumnVO> getAllColumnsExceptGeom(Table table) throws Exception;
   
   /**
    * Retrieves all columns except the primary key and
    * geometry columns
    *
    * @param table the table from getting the columns
    * @return the column list of the table
    * @throws Exception if there was an error
    */
   public List<ColumnVO> getAllColumnsExceptPKeyGeom(Table table) throws Exception;
   
   /**
    * Retrieves all columns
    *
    * @param table the table from getting the columns
    * @return the column list of the table
    * @throws Exception if there was an error
    */
   public List<ColumnVO> getAllColumns(Table table) throws Exception;
   
   public String getPrimaryKey(Table table) throws Exception;

   public boolean existsTable(String schemaName, String tableName) throws Exception;

   public int insertCSVLine(String schemaTableName, String[] line, List<ColumnVO> columns, Integer srid) throws Throwable;

   public int insertShapeFeature(String schemaTableName, SimpleFeature feature, List<ColumnVO> columns, Integer srid) throws Throwable;

   public void backup(Table table) throws Exception;

   public void deleteTable(String schemaTable) throws Exception;

   public void restoreBackup(Table table) throws Exception;

   public int countRows(String schemaTable) throws Exception;

   public List<RowVO> getRows(Table table, int numRows) throws Exception;

   public String getGeomtryType(String schema, String table, String geometryField) throws Exception;
}
