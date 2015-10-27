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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

/**
 * Class which manages the Task entities
 *
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "TASKS")
public class Task implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 4651104997916838582L;
   
   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_TASK", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * The generated ticket for the task
    */
   @Column(name = "T_TICKET", length = 250, unique = true, nullable = false)
   private String ticket;

   /**
    * Flag which indicates if the table
    * will be overrode
    */
   @Column(name = "L_UPDATE", nullable = false)
   @Type(type = "org.hibernate.type.NumericBooleanType")
   private boolean update;
   
   /**
    * Source of the task
    */
   @Embedded
   private Source source;
   
   /**
    * State of the task
    */
   @Embedded
   private TaskState state;
   
   /**
    * Associated table
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "TABLES_X_TABLE")
   private Table table;
   
   /**
    * Main constructor
    */
   public Task() { }

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
    * @return the update
    */
   public boolean isUpdate() {
      return update;
   }

   /**
    * @param update the update to set
    */
   public void setUpdate(boolean update) {
      this.update = update;
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
    * @return the state
    */
   public TaskState getState() {
      return state;
   }

   /**
    * @param state the state to set
    */
   public void setState(TaskState state) {
      this.state = state;
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
   
   /**
    * @return the ticket
    */
   public String getTicket() {
      return ticket;
   }

   /**
    * @param ticket the ticket to set
    */
   public void setTicket(String ticket) {
      this.ticket = ticket;
   }
   
   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      
      if (o instanceof Task) {
         Task task = (Task) o;
         equals = (getId() == task.getId());
      }
      
      return equals;
   }
   
   @Override
   public int hashCode() {
      return 29 * (int)getId();
   }
}