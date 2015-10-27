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
package es.juntadeandalucia.panelGestion.persistencia.utiles;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;

/**
 * Class which manages Repositories
 *
 * @author GUADALTEL S.A
 */
public class Repository implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -8546484875670508060L;

   /**
    * Url of the repository
    */
   private String url;
   
   /**
    * Alias given to the repository
    */
   private String alias;
   
   /**
    * User of the repository
    */
   private String user;
   
   /**
    * Password of the repository
    */
   private String password;
   
   /**
    * Source
    */
   private Source source;
   
   /**
    * Main constructor
    */
   public Repository() { }

   /**
    * @return the url
    */
   public String getUrl() {
      return url;
   }

   /**
    * @param url the url to set
    */
   public void setUrl(String url) {
      this.url = url;
      if (!StringUtils.isEmpty(this.url)) {
         int lastCharIdx = this.url.length() - 1;
         if (this.url.charAt(lastCharIdx) != '/') {
            this.url += "/";
         }
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
    * @return the user
    */
   public String getUser() {
      return user;
   }

   /**
    * @param user the user to set
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * @return the password
    */
   public String getPassword() {
      return password;
   }

   /**
    * @param password the password to set
    */
   public void setPassword(String password) {
      this.password = password;
   }

   /**
    * @return the source
    */
   public Source getSource() {
      return source;
   }

   /**
    * @param source the source to set
    */
   public void setSource(Source source) {
      this.source = source;
   }
}