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
package es.juntadeandalucia.panelGestion.negocio.utiles;

import java.io.File;
import java.io.Serializable;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public abstract class JARAFolder implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 3635354240412697310L;
   
   private static final String EXTENSION_FILE = ".xml";
   
   public static void putNewFile(String name, Document xmlJaraLayer, boolean override) throws TransformerException {
      String jaraFileName = addExtension(name);
      
      File jaraFile = new File(PanelSettings.jaraConfigPath, jaraFileName);
      
      if (jaraFile.exists() && !override) {
         throw new IllegalArgumentException("El fichero " + name + " ya existe");
      }
      else if (jaraFile.exists() && override) {
         jaraFile.delete();
      }
      
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(xmlJaraLayer);
      StreamResult result = new StreamResult(jaraFile);
      
      transformer.transform(source, result);
   }

   /**
    * TODO
    *
    * @param fileName
    * @return
    */
   public static File getFile(String fileName) {
      File file = null;
      
      String jaraFileName = addExtension(fileName);
      file = new File(PanelSettings.jaraConfigPath, jaraFileName);
      
      return file;
   }

   /**
    * TODO
    *
    * @param fileName
    * @return
    */
   public static String addExtension(String fileName) {
      return fileName.concat(EXTENSION_FILE);
   }
}
