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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class which manages the Schema entities
 *
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "SCHEMAS")
public class Schema implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 8739526029112860250L;
   
   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_SCHEMA", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * Schema name
    */
   @Column(name = "T_SCHEMA", length = 50)
   private String name;
   
   /**
    * Database user
    */
   @Column(name = "T_USER", length = 30)
   private String user;
   
   /**
    * DataBase user password
    */
   @Column(name = "T_PASSWORD", length = 30)
   private String password;
   
   /**
    * When the schema was created
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_CREATION_DATE", length = 7)
   private Date creationDate;
   
   /**
    * Associated database
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "X_DATABASE")
   private DataBase dataBase;
   
   /**
    * Associated tables
    */
   @OneToMany(mappedBy = "schema")
   private List<Table> tables;
   
   /**
    * Main constructor
    */
   public Schema() { }
   
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
    * @return the dataBase
    */
   public DataBase getDataBase() {
      return dataBase;
   }

   /**
    * @param dataBase the dataBase to set
    */
   public void setDataBase(DataBase dataBase) {
      this.dataBase = dataBase;
   }

   /**
    * @return the tables
    */
   public List<Table> getTables() {
      return tables;
   }

   /**
    * @param tables the tables to set
    */
   public void setTables(List<Table> tables) {
      this.tables = tables;
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
   
   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      
      if (o instanceof Schema) {
         Schema schema = (Schema) o;
         equals = (getId() == schema.getId());
      }
      
      return equals;
   }
   
   @Override
   public int hashCode() {
      return 29 * (int)getId();
   }
}
