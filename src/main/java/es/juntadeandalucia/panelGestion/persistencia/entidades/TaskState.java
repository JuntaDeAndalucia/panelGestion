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
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Parent;
import org.hibernate.validator.Max;
import org.hibernate.validator.Min;

/**
 * Class which manages Task states
 *
 * @author GUADALTEL S.A
 */
@Embeddable
public class TaskState implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 7614781045042741049L;
   
   /**
    * Read lines of the source
    */
   @Column(name = "N_READ_LINES", nullable = false)
   private int readLines;
   
   /**
    * Task progress
    */
   @Min(value = 0)
   @Max(value = 100)
   @Column(name = "PROGRESS", precision = 3, scale = 0)
   private double progress;
   
   /**
    * State description
    */
   @Column(name = "DESCRIPTION", length = 250)
   private String description;
   
   /**
    * Status
    */
   @Enumerated(EnumType.STRING)
   private Status status = Status.NEW;
   
   /**
    * Task
    */
   @Parent
   private Task task;
   
   /**
    * Main constructor
    */
   public TaskState() { }

   /**
    * @return the readLines
    */
   public int getReadLines() {
      return readLines;
   }

   /**
    * @param readLines the readLines to set
    */
   public void setReadLines(int readLines) {
      this.readLines = readLines;
   }

   /**
    * @return the progress
    */
   public double getProgress() {
      return progress;
   }

   /**
    * @param progress the progress to set
    */
   public void setProgress(double progress) {
      this.progress = progress;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return the status
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @param status the status to set
    */
   public void setStatus(Status status) {
      this.status = status;
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
}
