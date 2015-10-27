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
package es.juntadeandalucia.panelGestion.negocio.utiles.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * This class manages local files
 *
 * @author GUADALTEL S.A
 */
public class LocalFileReader implements FileReader, Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -9000386859709822869L;

   private static Logger log = Logger.getLogger(LocalFileReader.class);
   
   private File data;
   
   public LocalFileReader(File localSourceData) {
      this.data = localSourceData;
   }
   
   @Override
   public InputStreamReader getInputStreamReader() throws FileNotFoundException {
      InputStream in = new FileInputStream(data);
      
      return new InputStreamReader(in);
   }
   
   @Override
   public int countLines() throws FileNotFoundException {
      final int bufferSize = 1024;
      int lines = 0;
      
      
      InputStream is = new FileInputStream(data);
      boolean empty = true;
      try {
         byte[] buffer = new byte[bufferSize];
         int readChars = 0;
         try {
            while ((readChars = is.read(buffer)) != -1) {
               empty = false;
               for (int i = 0; i < readChars; ++i) {
                  if (buffer[i] == '\n') {
                     ++lines;
                  }
               }
            }
         }
         catch (IOException e) {
            log.error("Error al intentar contar las líneas del fichero: " + e.getLocalizedMessage());
         }
      }
      finally {
         try {
            is.close();
         }
         catch (IOException e) {
            log.warn("Error al intentar cerrar InputStream: " + e.getLocalizedMessage());
         }
      }
      if ((lines == 0) && !empty) {
         lines = 1;
      }
      return lines;
   }

   @Override
   public long getBytesLength() {
      return data.length();
   }

   public static InputStream getInputStream(URL url) throws FileNotFoundException, URISyntaxException {
      InputStream inputStream;
      
      File file = new File(url.toURI());
      inputStream = new FileInputStream(file);
      
      return inputStream;
   }
}