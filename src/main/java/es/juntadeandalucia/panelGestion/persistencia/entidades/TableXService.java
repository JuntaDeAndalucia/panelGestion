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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class which manages the relation between Table entities
 * and Services entities. This class is necessary because the
 * JPA technology does not allow join tables with additional data
 * 
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "TABLES_X_SERVICES")
public class TableXService implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -4421096912406728087L;
   
   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_TABLEXSERVI", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * Associated schema
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "TABLES_X_TABLE")
   private Table table;
   
   /**
    * Associated schema
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "SERVICES_X_SERVICE")
   private Service service;
   
   /**
    * Creation date
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_CREATION_DATE", length = 7)
   private Date creationDate;
   
   /**
    * Main constructor
    */
   public TableXService() { ; }
   
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
    * @return the table
    */
   public Table getTable() {
      return table;
   }

   /**
    * @param table the table to set
    */
   public void setTable(Table table) {
      this.table = table;
   }

   /**
    * @return the service
    */
   public Service getService() {
      return service;
   }

   /**
    * @param service the service to set
    */
   public void setService(Service service) {
      this.service = service;
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

   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      if ((o != null) && (o instanceof TableXService)) {
         TableXService tableXservice = (TableXService) o;
         equals = (id == tableXservice.getId());
      }
      return equals;
   }

   @Override
   public int hashCode() {
      return (int)(29 * id);
   }
}
