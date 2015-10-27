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
package es.juntadeandalucia.panelGestion.persistencia.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class which manages the DataBase entities
 *
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "DATABASES")
public class DataBase implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -6701683570928260215L;

   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_DATABASE", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * Database alias
    */
   @Column(name = "T_ALIAS", length = 30)
   private String alias;
   
   /**
    * URL connection to database
    */
   @Column(name = "T_DATABASE_URL", length = 150)
   private String connectionUrl;
   
   /**
    * When the database was created
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_CREATION_DATE", length = 7)
   private Date creationDate;
   
   /**
    * Associated schemas
    */
   @OneToMany(mappedBy = "dataBase")
   private List<Schema> schemas;
   
   /**
    * Main constructor
    */
   public DataBase() { 
      schemas = new LinkedList<Schema>();
   }

   /**
    * @return the id
    */
   public long getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(long id) {
      this.id = id;
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
    * @return the connectionUrl
    */
   public String getConnectionUrl() {
      return connectionUrl;
   }

   /**
    * @param connectionUrl the connectionUrl to set
    */
   public void setConnectionUrl(String connectionUrl) {
      this.connectionUrl = connectionUrl;
   }

   /**
    * @return the creationDate
    */
   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * @param creationDate the creationDate to set
    */
   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   /**
    * @return the schemas
    */
   public List<Schema> getSchemas() {
      return schemas;
   }

   /**
    * @param schemas the schemas to set
    */
   public void setSchemas(List<Schema> schemas) {
      this.schemas = schemas;
   }
   
   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      
      if (o instanceof DataBase) {
         DataBase dataBase = (DataBase) o;
         equals = (getId() == dataBase.getId());
      }
      
      return equals;
   }
   
   @Override
   public int hashCode() {
      return 29 * (int)getId();
   }
}
