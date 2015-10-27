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

public class JARALayerVO implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 623666860601879746L;

   private String name;
   private String title;
   private String line2;
   private String line3;
   private String line4;
   private String imageUrl;
   private String attribution;
   private List<RAActionVO> actions;
   private Table table;
   
   public JARALayerVO() {
      actions = new LinkedList<RAActionVO>();
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return the line2
    */
   public String getLine2() {
      return line2;
   }

   /**
    * @param line2 the line2 to set
    */
   public void setLine2(String line2) {
      this.line2 = line2;
   }

   /**
    * @return the line3
    */
   public String getLine3() {
      return line3;
   }

   /**
    * @param line3 the line3 to set
    */
   public void setLine3(String line3) {
      this.line3 = line3;
   }

   /**
    * @return the line4
    */
   public String getLine4() {
      return line4;
   }

   /**
    * @param line4 the line4 to set
    */
   public void setLine4(String line4) {
      this.line4 = line4;
   }

   /**
    * @return the imageUrl
    */
   public String getImageUrl() {
      return imageUrl;
   }

   /**
    * @param imageUrl the imageUrl to set
    */
   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   /**
    * @return the attribution
    */
   public String getAttribution() {
      return attribution;
   }

   /**
    * @param attribution the attribution to set
    */
   public void setAttribution(String attribution) {
      this.attribution = attribution;
   }

   /**
    * @return the actions
    */
   public List<RAActionVO> getActions() {
      return actions;
   }

   /**
    * @param actions the actions to set
    */
   public void setActions(List<RAActionVO> actions) {
      this.actions = actions;
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
}
