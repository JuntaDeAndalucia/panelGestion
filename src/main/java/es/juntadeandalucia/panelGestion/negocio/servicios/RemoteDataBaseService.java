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
package es.juntadeandalucia.panelGestion.negocio.servicios;

import java.util.Collection;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RowVO;
import es.juntadeandalucia.panelGestion.negocio.vo.SchemaVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

/**
 * Interface to manage remote data bases
 *
 * @author GUADALTEL S.A
 */
public interface RemoteDataBaseService {

   boolean createSchema(Schema schema) throws Exception;
   
   boolean updateSchema(SchemaVO schemaVO) throws Exception;
   
   boolean createTable(Table table, Collection<ColumnVO> columns) throws Exception;
   
   boolean insertCSVLine(String schemaTableName, String[] line, List<ColumnVO> columns, Integer srid) throws Throwable;
   
   boolean insertShapeFeature(String schemaTableName, SimpleFeature feature, List<ColumnVO> columns, Integer srid) throws Throwable;

   boolean cleanTable(Table table) throws Exception;

   List<ColumnVO> getAllColumnsExceptPKeyGeom(Table table) throws Exception;

   boolean existsTable(String schemaName, String tableName) throws Exception;

   boolean existsSchema(String schemaName) throws Exception;

   void backup(Table table) throws Exception;

   void removeBackup(Table table) throws Exception;

   void deleteTable(Table table) throws Exception;

   void restoreBackup(Table table) throws Exception;

   int countRows(Table table) throws Exception;

   String getPrimaryKey(Table table) throws Exception;

   List<RowVO> getRows(Table table, int numRows) throws Exception;

   List<ColumnVO> getAllColumns(Table table) throws Exception;

   boolean deleteSchema(Schema schema) throws Exception;

   String getGeomtryType(Table table) throws Exception;
}
