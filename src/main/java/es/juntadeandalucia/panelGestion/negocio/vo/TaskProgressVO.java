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
package es.juntadeandalucia.panelGestion.negocio.vo;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import es.juntadeandalucia.panelGestion.persistencia.entidades.Status;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("taskProgress")
public class TaskProgressVO implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -5554008772696038473L;
   
   private String ticket;
   private Status status;
   private double progress;
   
   public TaskProgressVO() { }
   
   public TaskProgressVO(TaskVO task) {
      this(task.getTaskEntity());
   }

   public TaskProgressVO(Task task) {
      this.ticket = task.getTicket();
      this.status = task.getState().getStatus();
      this.progress = task.getState().getProgress();
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
}
