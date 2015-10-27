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
import java.util.LinkedList;
import java.util.List;

import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

public class GeosearchTableVO implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -2101009266967380987L;
   
   private List<GeosearchFieldVO> fields;
   private Table table;
   private String keywords;
   
   public GeosearchTableVO() {
      fields = new LinkedList<GeosearchFieldVO>();
   }

   /**
    * @return the fields
    */
   public List<GeosearchFieldVO> getFields() {
      return fields;
   }

   /**
    * @param fields the fields to set
    */
   public void setFields(List<GeosearchFieldVO> fields) {
      this.fields = fields;
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
    * @return the keywords
    */
   public String getKeywords() {
      return keywords;
   }

   /**
    * @param keywords the keywords to set
    */
   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }
}
