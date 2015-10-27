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
package es.juntadeandalucia.panelGestion.negocio.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RowVO implements Serializable {
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 3663785087539796003L;
   
   private Map<String, String> fields;
   
   private String geomFieldName;
   
   public RowVO() {
      fields = new HashMap<String, String>();
   }
   
   public void addField(String columnName, String columnValue) {
      this.fields.put(columnName, columnValue);
   }
   
   public String get(String columnName) {
      return this.fields.get(columnName);
   }

   /**
    * @return the geomField
    */
   public String getGeomFieldName() {
      return geomFieldName;
   }

   /**
    * @param geomField the geomField to set
    */
   public void setGeomFieldName(String geomField) {
      this.geomFieldName = geomField;
   }
}