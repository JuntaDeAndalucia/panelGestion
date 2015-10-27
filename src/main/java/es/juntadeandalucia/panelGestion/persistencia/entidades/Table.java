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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
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
 * Class which manages the Table entities
 *
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "TABLES")
public class Table implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 6537281684276829018L;

   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_TABLE", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * Table name
    */
   @Column(name = "T_TABLE_NAME", length = 50)
   private String name;
   
   /**
    * Table projection
    */
   @Column(name = "T_EPSG", length = 10)
   private String epsg;
   
   /**
    * Name of the geometric field
    */
   @Column(name = "T_GEOM_FIELD", length = 50)
   private String geomField;
   
   /**
    * When the table was created
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_CREATION_DATE", length = 7)
   private Date creationDate;
   
   /**
    * Last time when the table was modified
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_MODIFICATION_DATE", length = 7)
   private Date modificationDate;
   
   /**
    * Associated schema
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "X_SCHEMA")
   private Schema schema;
   
   /**
    * Associated tables
    */
   @OneToMany(mappedBy = "table", cascade = CascadeType.REMOVE)
   private List<TableXService> tableXservices;
   
   /**
    * Associated tasks
    */
   @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
   private List<Task> tasks;
   
   /**
    * Main constructor
    */
   public Table() {
      tableXservices = new LinkedList<TableXService>();
      tasks = new LinkedList<Task>();
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
    * @return the epsg
    */
   public String getEpsg() {
      return epsg;
   }

   /**
    * @param epsg the epsg to set
    */
   public void setEpsg(String epsg) {
      this.epsg = epsg;
   }

   /**
    * @return the geomField
    */
   public String getGeomField() {
      return geomField;
   }

   /**
    * @param geomField the geomField to set
    */
   public void setGeomField(String geomField) {
      this.geomField = geomField;
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
    * @return the modificationDate
    */
   public Date getModificationDate() {
      return modificationDate;
   }

   /**
    * @param modificationDate the modificationDate to set
    */
   public void setModificationDate(Date modificationDate) {
      this.modificationDate = modificationDate;
   }

   /**
    * @return the schema
    */
   public Schema getSchema() {
      return schema;
   }

   /**
    * @param schema the schema to set
    */
   public void setSchema(Schema schema) {
      this.schema = schema;
   }

   /**
    * @return the tasks
    */
   public List<Task> getTasks() {
      return tasks;
   }

   /**
    * @param tasks the tasks to set
    */
   public void setTasks(List<Task> tasks) {
      this.tasks = tasks;
   }
   
   /**
    * @return the tableXservices
    */
   public List<TableXService> getTableXservices() {
      return tableXservices;
   }

   /**
    * @param tableXservices the tableXservices to set
    */
   public void setTableXservices(List<TableXService> tableXservices) {
      this.tableXservices = tableXservices;
   }

   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      
      if (o instanceof Table) {
         Table table = (Table) o;
         equals = (getId() == table.getId());
      }
      
      return equals;
   }
   
   @Override
   public int hashCode() {
      return 29 * (int)getId();
   }
}
