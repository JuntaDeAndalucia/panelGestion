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
package es.juntadeandalucia.panelGestion.negocio.utiles;

import java.util.LinkedList;
import java.util.List;

import es.juntadeandalucia.panelGestion.negocio.vo.TaskVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Status;

/**
 * This class implements two tasks priority queues. First it checks if a task is local or remote.
 * If the task is remote then appends it on the queue for remote tasks otherwise appends it
 * on the the queue for local tasks.
 * There is not limit for the local task parallel executions 
 * (defined by the @attribute NUM_SIMULTANEOUS_LOCAL TASKS but the remote tasks are limited by
 * the @attribute NUM_SIMULTANEOUS_REMOTE_TASKS.
 * The priority of the queues are implemented by two comparators which compares two tasks. One
 * comparator for local tasks and one comparator for remote tasks.
 * An external Cron job triggers the @method saveState to save the remote queue state into
 * the data base.
 * This class implements the Singleton pattern.
 * 
 * @author GUADALTEL S.A
 */
public final class TasksQueue {
   
   private final int NUM_SIMULTANEOUS_LOCAL_TASKS = 999;
   
   private final int NUM_SIMULTANEOUS_REMOTE_TASKS = 3;
   
   private TaskExecutor localTaskExecutor;
   private TaskExecutor remoteTaskExecutor;
   
   private static TasksQueue instance;
   
   public static TasksQueue getInstance() {
      if (instance == null) {
         instance = new TasksQueue();
      }
      return instance;
   }
   
   private TasksQueue() {
      // local task queue and executor
      localTaskExecutor = new TaskExecutor(NUM_SIMULTANEOUS_LOCAL_TASKS);
      
      // remote executor
      remoteTaskExecutor = new TaskExecutor(NUM_SIMULTANEOUS_REMOTE_TASKS);
   }

   public synchronized void appendTask(TaskVO task) throws Exception {
      /*
       * the executors manage the queues and the parallel executions
       */
      Source source = task.getTaskEntity().getSource();
      if (source != null) {
         task.changeStatusTo(Status.NEW);
         FutureTaskCallableValue<TaskVO> futureTask = new FutureTaskCallableValue<TaskVO>(task);

         if (source.isRemote()) {
            remoteTaskExecutor.execute(futureTask);
         }
         else {
            localTaskExecutor.execute(futureTask);
         }
      }
   }

   /**
    * @return the runningTasks
    */
   public List<Runnable> getAllRunningTasks() {
      List<Runnable> allRunningTasks = new LinkedList<Runnable>();
      
      allRunningTasks.addAll(remoteTaskExecutor.getRunningTasks());
      allRunningTasks.addAll(localTaskExecutor.getRunningTasks());
      
      return allRunningTasks;
   }
   
   /**
    * @return the runningTasks
    */
   public List<Runnable> getRunningTasks() {
      return remoteTaskExecutor.getRunningTasks();
   }

   /**
    * @return the pausedTasks
    */
   public List<Runnable> getPausedTasks() {
      return remoteTaskExecutor.getPausedTasks();
   }
}
