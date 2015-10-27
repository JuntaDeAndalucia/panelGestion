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
public class GeosearchDataImportFileVO {

   private String name;
   private String javaClass;
   private String fileName;
   private String type;
   private String driver;
   private String transformer;
   
   public GeosearchDataImportFileVO() {}

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
    * @return the javaClass
    */
   public String getJavaClass() {
      return javaClass;
   }

   /**
    * @param javaClass the javaClass to set
    */
   public void setJavaClass(String javaClass) {
      this.javaClass = javaClass;
   }

   /**
    * @return the fileName
    */
   public String getFileName() {
      return fileName;
   }

   /**
    * @param fileName the fileName to set
    */
   public void setFileName(String fileName) {
      this.fileName = fileName;
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
    * @return the driver
    */
   public String getDriver() {
      return driver;
   }

   /**
    * @param driver the driver to set
    */
   public void setDriver(String driver) {
      this.driver = driver;
   }

   /**
    * @return the transformer
    */
   public String getTransformer() {
      return transformer;
   }

   /**
    * @param transformer the transformer to set
    */
   public void setTransformer(String transformer) {
      this.transformer = transformer;
   }
}
