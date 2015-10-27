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
package es.juntadeandalucia.panelGestion.persistencia.entidades;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Parent;
import org.hibernate.annotations.Type;

import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;

/**
 * Class which manages sources
 *
 * @author GUADALTEL S.A
 */
@Embeddable
public class Source implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -879131984932791702L;

   /**
    * Nunber of lines of the source file
    */
   @Column(name = "N_SOURCE_NUM_LINES", nullable = false)
   private int numLines;
   
   /**
    * Name of the source
    */
   @Column(name = "T_SOURCE_NAME", length = 150)
   private String name;
   
   /**
    * Size of the source
    */
   @Column(name = "N_SOURCE_SIZE")
   private Integer size;

   /**
    * Content-type of the source
    */
   @Column(name = "T_SOURCE_CONTENT_TYPE", length = 150)
   private String contentType;
   
   /**
    * Status
    */
   @Enumerated(EnumType.STRING)
   private FileType type;
   
   /**
    * Divider used in the source
    */
   @Column(name = "T_DIVIDER", length = 5)
   private String divider;
   
   /**
    * If the source is remote
    */
   @Column(name = "L_REMOTE", nullable = false)
   @Type(type = "org.hibernate.type.NumericBooleanType")
   private boolean remote;
   
   /**
    * Url of the repository
    */
   @Column(name = "T_REPOSITORY_URL", length = 250)
   private String url;
   
   /**
    * Alias given to the repository
    */
   @Column(name = "T_REPOSITORY_ALIAS", length = 250)
   private String alias;
   
   /**
    * User of the repository
    */
   @Column(name = "T_REPOSITORY_USER", length = 150)
   private String user;
   
   /**
    * Password of the repository
    */
   @Column(name = "T_REPOSITORY_PASSWORD", length = 150)
   private String password;
   
   /**
    * Task
    */
   @Parent
   private Task task;
   
   /**
    * Main constructor
    */
   public Source() { }

   /**
    * @return the numLines
    */
   public int getNumLines() {
      return numLines;
   }

   /**
    * @param numLines the numLines to set
    */
   public void setNumLines(int numLines) {
      this.numLines = numLines;
   }

   /**
    * @return the type
    */
   public FileType getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(FileType type) {
      this.type = type;
   }

   /**
    * @return the divider
    */
   public String getDivider() {
      return divider;
   }

   /**
    * @param divider the divider to set
    */
   public void setDivider(String divider) {
      this.divider = divider;
   }

   /**
    * @return the remote
    */
   public boolean isRemote() {
      return remote;
   }

   /**
    * @param remote the remote to set
    */
   public void setRemote(boolean remote) {
      this.remote = remote;
   }

   /**
    * @return the task
    */
   public Task getTask() {
      return task;
   }

   /**
    * @param task the task to set
    */
   public void setTask(Task task) {
      this.task = task;
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
    * @return the size
    */
   public Integer getSize() {
      return size;
   }

   /**
    * @param size the size to set
    */
   public void setSize(Integer size) {
      this.size = size;
   }

   /**
    * @return the contentType
    */
   public String getContentType() {
      return contentType;
   }

   /**
    * @param contentType the contentType to set
    */
   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

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

   public String getFormattedSize() {
      return Utils.formatSize(size);
   }
}