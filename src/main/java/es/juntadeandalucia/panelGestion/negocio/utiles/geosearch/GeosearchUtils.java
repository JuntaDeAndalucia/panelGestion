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

import java.util.LinkedList;
import java.util.List;

import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchTableVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

public class GeosearchUtils {

   public static GeosearchTableVO toGeosearchTable(Table table) throws Exception {
      GeosearchTableVO geosearchTableVO = new GeosearchTableVO();
      
      List<GeosearchFieldVO> columns = getGeosearchColumns(table);
      
      geosearchTableVO.setFields(columns);
      geosearchTableVO.setTable(table);
      
      return geosearchTableVO;
   }

   public static List<GeosearchFieldVO> getGeosearchColumns(Table table) throws Exception {
      List<GeosearchFieldVO> columns = new LinkedList<GeosearchFieldVO>();
      
      JDBCConnector connector = new JDBCConnector(table);
      RemoteDataBaseService remoteDataBaseService = new RemoteDataBaseServiceImpl(connector);
      List<ColumnVO> tableColumns = remoteDataBaseService.getAllColumnsExceptPKeyGeom(table);
      columns = tableColumnsToGeosearchColumns(tableColumns);
      
      return columns;
   }

   public static List<GeosearchFieldVO> tableColumnsToGeosearchColumns(List<ColumnVO> tableColumns) {
      List<GeosearchFieldVO> geosearchColumns = new LinkedList<GeosearchFieldVO>();
      
      for (ColumnVO tableColumn : tableColumns) {
         GeosearchFieldVO geosearchColumn = tableColumnToGeosearchColumn(tableColumn);
         geosearchColumns.add(geosearchColumn);
      }
      return geosearchColumns;
   }

   public static GeosearchFieldVO tableColumnToGeosearchColumn(ColumnVO tableColumn) {
      GeosearchFieldVO geosearchColumn = new GeosearchFieldVO();
      
      String nameOnTable = tableColumn.getNameOnTable();
      geosearchColumn.setNameOnTable(nameOnTable);
      
      return geosearchColumn;
   }

}
