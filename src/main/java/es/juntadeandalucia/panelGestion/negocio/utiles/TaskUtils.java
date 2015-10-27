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

import java.util.concurrent.Callable;

import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TaskService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.vo.TaskVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;

public abstract class TaskUtils {

   public static TaskVO getTaskVOFromRunnable(Runnable r) {
      TaskVO taskvo = null;
      if (r instanceof FutureTaskCallableValue) {
         FutureTaskCallableValue<?> futureTask = (FutureTaskCallableValue<?>) r;
         Callable<?> callableValue = futureTask.getCallableValue();
         if (callableValue instanceof TaskVO) {
            taskvo = (TaskVO) callableValue;
         }
      }
      return taskvo;
   }

   public static void removeNewTable(Task task, TaskService taskService,
         TableService tableService) throws Exception {
      if (task == null) {
         throw new IllegalArgumentException("La tarea no puede ser nula");
      }
      else if (task.isUpdate()) {
         throw new IllegalArgumentException(
            "No puede eliminarse una tabla creada con anterioridad");
      }
      else {
         Table table = task.getTable();

         if (table == null) {
            throw new IllegalArgumentException("La tarea no tiene asociada ninguna tabla");
         }
         else {
            // create the connector for dataBase
            JDBCConnector connector = new JDBCConnector(table);
            RemoteDataBaseService remoteDataBaseService = new RemoteDataBaseServiceImpl(connector);
            // removes the table
            remoteDataBaseService.deleteTable(table);
            
            // updates the data model
            taskService.delete(task);
            tableService.delete(table);
         }
      }
   }

   public static void removeBackup(Task task) throws Exception {
      if (task == null) {
         throw new IllegalArgumentException("La tarea no puede ser nula");
      }
      else if (!task.isUpdate()) {
         throw new IllegalArgumentException(
            "La tarea seleccionada no ha realizado ninguna copia de seguirdad");
      }
      else {
         Table table = task.getTable();

         if (table == null) {
            throw new IllegalArgumentException("La tarea no tiene asociada ninguna tabla");
         }
         else {
            // create the connector for dataBase
            JDBCConnector connector = new JDBCConnector(table);
            RemoteDataBaseService remoteDataBaseService = new RemoteDataBaseServiceImpl(connector);
            // removes the table backup
            remoteDataBaseService.removeBackup(table);
         }
      }
   }

   public static void restoreBackup(Task task) throws Exception {
      if (task == null) {
         throw new IllegalArgumentException("La tarea no puede ser nula");
      }
      else if (!task.isUpdate()) {
         throw new IllegalArgumentException(
            "La tarea seleccionada no ha realizado ninguna copia de seguirdad");
      }
      else {
         Table table = task.getTable();

         if (table == null) {
            throw new IllegalArgumentException("La tarea no tiene asociada ninguna tabla");
         }
         else {
            // create the connector for dataBase
            JDBCConnector connector = new JDBCConnector(table);
            RemoteDataBaseService remoteDataBaseService = new RemoteDataBaseServiceImpl(connector);
            // restores the backup
            remoteDataBaseService.restoreBackup(table);
         }
      }
   }
}
