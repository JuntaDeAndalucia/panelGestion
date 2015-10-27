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
package es.juntadeandalucia.panelGestion.presentacion.controlador.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TaskService;
import es.juntadeandalucia.panelGestion.negocio.utiles.TaskUtils;
import es.juntadeandalucia.panelGestion.negocio.utiles.TasksQueue;
import es.juntadeandalucia.panelGestion.negocio.vo.TaskProgressVO;
import es.juntadeandalucia.panelGestion.negocio.vo.TaskVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;

/**
 * Seam controller that manages the tasks queue
 *
 * @author GUADALTEL S.A
 */
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("tasksQueueController")
public class TasksQueueController implements Serializable {
   
   /**
    * Private logger
    */
   private static Logger log = Logger.getLogger(TasksQueueController.class);
   
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 702379556555680157L;

   private Task selectedTask;
   
   private List<TaskProgressVO> runningTasks;
   
   private List<TaskProgressVO> pausedTasks;
   
   private List<TaskProgressVO> failedTasks;
   
   /**
    * Service which manages Table entities
    */
   @In
   private TableService tableService;
   
   @In
   private TaskService taskService;
   
   @RequestParameter
   private String taskTicket;
   
   public TasksQueueController() { }
   
   public void viewTask(Table table) {
      List<Runnable> runningTasks = TasksQueue.getInstance().getAllRunningTasks();
      for (Runnable r : runningTasks) {
         TaskVO taskvo = TaskUtils.getTaskVOFromRunnable(r);
         if (taskvo != null) {
            Table taskTable = taskvo.getTaskEntity().getTable();
            if (table.getId() == taskTable.getId()) {
               selectedTask = taskvo.getTaskEntity();
               break;
            }
         }
      }
   }
   
   public void showTaskDetails(TaskProgressVO taskProgress) {
      taskTicket = taskProgress.getTicket();
      loadTasks();
   }

   public void loadTasks() {
      // running tasks
      runningTasks = new LinkedList<TaskProgressVO>();
      List<Runnable> runningTasksList = TasksQueue.getInstance().getRunningTasks();
      for (Runnable runnable : runningTasksList) {
         TaskVO task = TaskUtils.getTaskVOFromRunnable(runnable);
         if (task.getTaskEntity().getTicket().equals(taskTicket)) {
            selectedTask = task.getTaskEntity();
         }
         TaskProgressVO taskProgress = new TaskProgressVO(task);
         runningTasks.add(taskProgress);
      }
      
      // paused tasks
      pausedTasks = new LinkedList<TaskProgressVO>();
      List<Runnable> pausedTasksList = TasksQueue.getInstance().getPausedTasks();
      for (Runnable runnable : pausedTasksList) {
         TaskVO task = TaskUtils.getTaskVOFromRunnable(runnable);
         if (task.getTaskEntity().getTicket().equals(taskTicket)) {
            selectedTask = task.getTaskEntity();
         }
         TaskProgressVO taskProgress = new TaskProgressVO(task);
         pausedTasks.add(taskProgress);
      }
      
      // failed tasks
      failedTasks = new LinkedList<TaskProgressVO>();
      List<Task> failedTasksList = taskService.getFailedTasks();
      for (Task failedTask : failedTasksList) {
         if (failedTask.getTicket().equals(taskTicket)) {
            selectedTask = failedTask;
         }
         TaskProgressVO taskProgress = new TaskProgressVO(failedTask);
         failedTasks.add(taskProgress);
      }
   }
   
   public void removeNewTable() {
      try {
         TaskUtils.removeNewTable(selectedTask, taskService, tableService);
      }
      catch (Exception e) {
         String errorMsg = "Se ha producido un error al intentar eliminar la tabla: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
      
      // unselects task
      selectedTask = null;
   }
   
   public void taskChecked() {
      try {
         // removes the table backup done
         if (selectedTask.isUpdate()) {
            TaskUtils.removeBackup(selectedTask);
         }
         // updates data model
         taskService.delete(selectedTask);
         
         // refresh tasks lists
         loadTasks();
      }
      catch (Exception e) {
         String errorMsg = "Se ha producido un error al intentar eliminar la tarea: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      }
      
      // unselects task
      selectedTask = null;
   }

   public void restoreBackup() {
      try {
         // restore the table backup copy
         if (selectedTask.isUpdate()) {
            TaskUtils.restoreBackup(selectedTask);
         }
         // updates data model
         taskService.delete(selectedTask);
         
         // refresh tasks lists
         loadTasks();
      }
      catch (Exception e) {
         String errorMsg = "Se ha producido un error al intentar restaurar la copia de la tabla: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMsg);
         log.error(errorMsg);
      };
      
      // unselects task
      selectedTask = null;
   }

   /**
    * @return the selectedTask
    */
   public Task getSelectedTask() {
      return selectedTask;
   }

   /**
    * @param selectedTask the selectedTask to set
    */
   public void setSelectedTask(Task selectedTask) {
      this.selectedTask = selectedTask;
   }

   /**
    * @return the runningTasks
    */
   public List<TaskProgressVO> getRunningTasks() {
      return runningTasks;
   }

   /**
    * @param runningTasks the runningTasks to set
    */
   public void setRunningTasks(List<TaskProgressVO> runningTasks) {
      this.runningTasks = runningTasks;
   }

   /**
    * @return the pausedTasks
    */
   public List<TaskProgressVO> getPausedTasks() {
      return pausedTasks;
   }

   /**
    * @param pausedTasks the pausedTasks to set
    */
   public void setPausedTasks(List<TaskProgressVO> pausedTasks) {
      this.pausedTasks = pausedTasks;
   }

   /**
    * @return the taskTicket
    */
   public String getTaskTicket() {
      return taskTicket;
   }

   /**
    * @param taskTicket the taskTicket to set
    */
   public void setTaskTicket(String taskTicket) {
      this.taskTicket = taskTicket;
   }

   /**
    * @return the failedTasks
    */
   public List<TaskProgressVO> getFailedTasks() {
      return failedTasks;
   }

   /**
    * @param failedTasks the failedTasks to set
    */
   public void setFailedTasks(List<TaskProgressVO> failedTasks) {
      this.failedTasks = failedTasks;
   }
}
