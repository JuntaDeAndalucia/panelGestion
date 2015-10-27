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

import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;

public class SchemaVO implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -8683226949412991976L;
   
   private Schema schemaEntity;
   private String alias;
   private String oldName;
   private String oldUser;
   private boolean create;
   
   public SchemaVO() {
      this.schemaEntity = new Schema();
   }

   /**
    * @return the schemaEntity
    */
   public Schema getSchemaEntity() {
      return schemaEntity;
   }

   /**
    * @param schemaEntity the schemaEntity to set
    */
   public void setSchemaEntity(Schema schemaEntity) {
      this.schemaEntity = schemaEntity;
      
      DataBase schemaDb = schemaEntity.getDataBase();
      if (schemaDb != null) {
         String alias = schemaDb.getAlias().concat(":")
            .concat(schemaEntity.getName());
         setAlias(alias);
      }
      
      if (oldName == null) {
         oldName = schemaEntity.getName();
      }
      
      if (oldUser == null) {
         oldUser = schemaEntity.getUser();
      }
   }

   /**
    * @return the alias
    */
   public String getAlias() {
      return alias;
   }

   /**
    * @param alias the alias to set
    */
   public void setAlias(String alias) {
      this.alias = alias;
   }

   /**
    * @return the oldName
    */
   public String getOldName() {
      return oldName;
   }

   /**
    * @return the oldUser
    */
   public String getOldUser() {
      return oldUser;
   }

   /**
    * @return the create
    */
   public boolean isCreate() {
      return create;
   }

   /**
    * @param create the create to set
    */
   public void setCreate(boolean create) {
      this.create = create;
   }
}
