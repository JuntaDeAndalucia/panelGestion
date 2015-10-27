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
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutor extends ThreadPoolExecutor {

   private static final int QUEUE_SIZE = 100;

   private List<Runnable> pausedTasks;
   private List<Runnable> runningTasks;
   private List<Runnable> finishedTasks;
   private List<Runnable> errorTasks;

   public TaskExecutor(int simultaneousTasks) {
      super(simultaneousTasks, simultaneousTasks, 0L, TimeUnit.MILLISECONDS,
         new PriorityBlockingQueue<Runnable>(QUEUE_SIZE, new TaskQueueComparator()));
      pausedTasks = new LinkedList<Runnable>();
      runningTasks = new LinkedList<Runnable>();
      finishedTasks = new LinkedList<Runnable>();
      errorTasks = new LinkedList<Runnable>();
   }

   @Override
   public void execute(Runnable task) {
      // paused <--- task
      pausedTasks.add(task);
      super.execute(task);
   }
   
   @Override
   protected void beforeExecute(Thread t, Runnable task) {
      // paused  ---> task
      // running <--- task
      pausedTasks.remove(task);
      runningTasks.add(task);
      super.beforeExecute(t, task);
   }

   @Override
   protected void afterExecute(Runnable task, Throwable t) {
      // running   ---> task
      runningTasks.remove(task);
      
//      TaskVO taskvo = Utils.getTaskVOFromRunnable(task);
//      if (taskvo != null) {
//         Status taskStatus = taskvo.getTaskEntity().getState().getStatus();
//         if (taskStatus ==  Status.FINISHED) {
//            // finished <--- task
//            finishedTasks.add(task);
//         }
//         else if (taskStatus ==  Status.ERROR) {
//            // finished <--- task
//            errorTasks.add(task);
//         }
//      }
      
      super.afterExecute(task, t);
   }

   @Override
   public boolean remove(Runnable command) {
      return super.remove(command);
   }

   @Override
   public void purge() {
      super.purge();
   }

   /**
    * @return the pausedTasks
    */
   public List<Runnable> getPausedTasks() {
      return pausedTasks;
   }

   /**
    * @return the runningTasks
    */
   public List<Runnable> getRunningTasks() {
      return runningTasks;
   }

   /**
    * @return the finishedTasks
    */
   public List<Runnable> getFinishedTasks() {
      return finishedTasks;
   }

   /**
    * @return the errorTasks
    */
   public List<Runnable> getErrorTasks() {
      return errorTasks;
   }
}
