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
package es.juntadeandalucia.panelGestion.security;

import java.io.IOException;
import java.util.ResourceBundle;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

/**
 * This class implements the security to check if an user
 * is administrator and if he can access to the panel administration 
 *
 * @author GUADALTEL S.A
 */
@Name("authenticator")
public class Authenticator {
   /**
    * Credentials specified by the user
    */
   @In private Credentials credentials;
   
   /**
    * The security properties
    */
   @In private ResourceBundle resourceBundle;
   
   /**
    * The message to show when an error occurred
    * while the user is trying to log in
    */
   private String failedLoginMessage;
   
   /**
    * Main constructor
    */
   public Authenticator() {}
   
   /**
    * This method authenticates an user checking the user name
    * and the password specified by him against the configured
    * user and password in the properties file
    *
    * @return true if the user log in successfully false in other case
    * @throws IOException if it can not access to the security file
    */
   public boolean authenticate() throws IOException {
      
      // gets the configured user and pass
      String adminUsername = resourceBundle.getString("user");
      String adminPassword = resourceBundle.getString("password");
      
      // gets the specified user name and pass by the user
      String username = credentials.getUsername();
      String password = credentials.getPassword();
      
      boolean success = (adminUsername.equals(username) && adminPassword.equals(password));
      if (!success) {
         failedLoginMessage = "Usuario y/o contraseña inválidos";
      }
      else {
         failedLoginMessage = null;
      }
      return success;
   }

   /**
    * @return the credentials
    */
   public Credentials getCredentials() {
      return credentials;
   }

   /**
    * @param credentials the credentials to set
    */
   public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
   }

   /**
    * @return the resourceBundle
    */
   public ResourceBundle getResourceBundle() {
      return resourceBundle;
   }

   /**
    * @param resourceBundle the resourceBundle to set
    */
   public void setResourceBundle(ResourceBundle resourceBundle) {
      this.resourceBundle = resourceBundle;
   }

   /**
    * @return the failedLoginMessage
    */
   public String getFailedLoginMessage() {
      return failedLoginMessage;
   }

   /**
    * @param failedLoginMessage the failedLoginMessage to set
    */
   public void setFailedLoginMessage(String failedLoginMessage) {
      this.failedLoginMessage = failedLoginMessage;
   }
}
