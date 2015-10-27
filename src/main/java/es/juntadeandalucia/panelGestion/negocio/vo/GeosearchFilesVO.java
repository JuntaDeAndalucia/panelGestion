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

/**
 * TODO
 *
 * @author GUADALTEL S.A
 */
public class GeosearchFilesVO {

   private GeosearchDataImportFileVO dataImport;
   private GeosearchConfigFileVO config;
   private GeosearchSchemaFileVO schema;
   
   public GeosearchFilesVO() {}

   /**
    * @return the dataImport
    */
   public GeosearchDataImportFileVO getDataImport() {
      return dataImport;
   }

   /**
    * @param dataImport the dataImport to set
    */
   public void setDataImport(GeosearchDataImportFileVO dataImport) {
      this.dataImport = dataImport;
   }

   /**
    * @return the config
    */
   public GeosearchConfigFileVO getConfig() {
      return config;
   }

   /**
    * @param config the config to set
    */
   public void setConfig(GeosearchConfigFileVO config) {
      this.config = config;
   }

   /**
    * @return the schema
    */
   public GeosearchSchemaFileVO getSchema() {
      return schema;
   }

   /**
    * @param schema the schema to set
    */
   public void setSchema(GeosearchSchemaFileVO schema) {
      this.schema = schema;
   }
}
