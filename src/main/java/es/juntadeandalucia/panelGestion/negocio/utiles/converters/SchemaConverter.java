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
package es.juntadeandalucia.panelGestion.negocio.utiles.converters;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import es.juntadeandalucia.panelGestion.negocio.servicios.SchemaService;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;

/**
 * This class converts between Schema and
 * selected item values from its id
 *
 * @author GUADALTEL S.A
 */
@Name(value = "schemaConverter")
@Converter
@BypassInterceptors
public class SchemaConverter implements javax.faces.convert.Converter, Serializable {

   private SchemaService schemaService;
   
   /**
    * Generated version UID
    */
   private static final long serialVersionUID = 2339471772967696884L;

   public SchemaConverter() { }
   
   @Override
   public Object getAsObject(FacesContext context, UIComponent component, String value) {
      Schema schema = null;
      if (value != null) {
         long schemaId = Long.valueOf(value);
         schema = schemaService.getSchema(schemaId);
      }
      return schema;
   }

   @Override
   public String getAsString(FacesContext context, UIComponent component, Object value) {
      String valueStr = "";
      if ((value != null) && (value instanceof Schema)) {
         Schema schema = (Schema) value;
         valueStr = String.valueOf(schema.getId());
      }
      return valueStr;
   }

   /**
    * @return the schemaService
    */
   public SchemaService getSchemaService() {
      return schemaService;
   }

   /**
    * @param schemaService the schemaService to set
    */
   public void setSchemaService(SchemaService schemaService) {
      this.schemaService = schemaService;
   }
}
