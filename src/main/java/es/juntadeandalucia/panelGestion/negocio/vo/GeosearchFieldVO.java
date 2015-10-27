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

import org.apache.commons.lang.StringUtils;

//http://wiki.apache.org/solr/SchemaXml#Common_field_options
public class GeosearchFieldVO implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 1002883099925733713L;

   private String nameOnTable;
   private String name;
   private String type;
   private boolean indexed;
   private boolean stored;
   private boolean multivaluated;
   private boolean copyToText;
   private String boost;
   private boolean inSchema;
   private String error;
   private boolean valid;
   
   public GeosearchFieldVO() {
      valid = true;
   }
   
   public boolean isDefined() {
      boolean defined = false;
      
      defined = (indexed || stored
         || multivaluated || !StringUtils.isEmpty(type)
         || !StringUtils.isEmpty(name));
      
      return defined;
   }

   /**
    * @return the nameOnTable
    */
   public String getNameOnTable() {
      return nameOnTable;
   }

   /**
    * @param nameOnTable the nameOnTable to set
    */
   public void setNameOnTable(String nameOnTable) {
      this.nameOnTable = nameOnTable;
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
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * @return the indexed
    */
   public boolean isIndexed() {
      return indexed;
   }

   /**
    * @param indexed the indexed to set
    */
   public void setIndexed(boolean indexed) {
      this.indexed = indexed;
   }

   /**
    * @return the stored
    */
   public boolean isStored() {
      return stored;
   }

   /**
    * @param stored the stored to set
    */
   public void setStored(boolean stored) {
      this.stored = stored;
   }

   /**
    * @return the multivaluated
    */
   public boolean isMultivaluated() {
      return multivaluated;
   }

   /**
    * @param multivaluated the multivaluated to set
    */
   public void setMultivaluated(boolean multivaluated) {
      this.multivaluated = multivaluated;
   }

   /**
    * @return the copyToText
    */
   public boolean isCopyToText() {
      return copyToText;
   }

   /**
    * @param copyToText the copyToText to set
    */
   public void setCopyToText(boolean copyToText) {
      this.copyToText = copyToText;
   }

   /**
    * @return the boost
    */
   public String getBoost() {
      return boost;
   }

   /**
    * @param boost the boost to set
    */
   public void setBoost(String boost) {
      this.boost = boost;
   }

   /**
    * @return the inSchema
    */
   public boolean isInSchema() {
      return inSchema;
   }

   /**
    * @param inSchema the inSchema to set
    */
   public void setInSchema(boolean inSchema) {
      this.inSchema = inSchema;
   }

   /**
    * @return the error
    */
   public String getError() {
      return error;
   }

   /**
    * @param error the error to set
    */
   public void setError(String error) {
      this.error = error;
   }

   /**
    * @return the valid
    */
   public boolean isValid() {
      return valid;
   }

   /**
    * @param valid the valid to set
    */
   public void setValid(boolean valid) {
      this.valid = valid;
   }
}
