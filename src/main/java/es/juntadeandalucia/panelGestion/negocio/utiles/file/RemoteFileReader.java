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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;

/**
 * This class manages remote files
 *
 * @author GUADALTEL S.A
 */
public class RemoteFileReader implements FileReader {

   /**
    * The input stream reader of the remote source
    */
   private InputStreamReader inputStreamReader;
   
   private long numBytes;
   
   /**
    * Main constructor
    * @param source Source to connect
    * @throws IOException thrown by the reader constructor
    */
   public RemoteFileReader(Source source) throws IOException {
   // gets repository properties
      String sourceName = source.getName();
      String url = source.getUrl();
      String sourceUrl = url + sourceName;
      String user = source.getUser();
      String password = source.getPassword();
      
      HttpMethod getMethod = httpGet(sourceUrl, user, password);
      InputStream is = getMethod.getResponseBodyAsStream();
      
      // gets the reader
      inputStreamReader = new InputStreamReader(is);
      
      numBytes = ((GetMethod)getMethod).getResponseContentLength();
   }
   
   @Override
   public int countLines() {
      /* it always returns -1 in order to avoid large time reading
       * the source to get the num lines */
      return -1;
   }
   
   @Override
   public long getBytesLength() {
      return numBytes;
   }

   @Override
   public InputStreamReader getInputStreamReader() {
      return inputStreamReader;
   }
   
   public static GetMethod httpGet(String url, String user, String password) throws HttpException, IOException {
   // http client
      HttpClient client = new HttpClient();

      // credentials
      if (!StringUtils.isEmpty(user) || !StringUtils.isEmpty(password)) {
         Credentials creds = new UsernamePasswordCredentials(user, password);
         client.getState().setCredentials(AuthScope.ANY, creds);
      }

      // executes get method
      HttpMethod getMethod = new GetMethod(url);
      client.executeMethod(getMethod);
      
      return (GetMethod) getMethod;
   }
   
   public static InputStream getInputStream(URL url, String user, String password) throws HttpException, IOException {
      InputStream inputStream;
      if (StringUtils.isEmpty(user) && StringUtils.isEmpty(password)) {
         inputStream = url.openConnection().getInputStream();
      }
      else {
         HttpMethod getMethod = httpGet(url.toExternalForm(), user, password);
         inputStream = getMethod.getResponseBodyAsStream();
      }
      return inputStream;
   }
}
