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

import java.util.Comparator;

import es.juntadeandalucia.panelGestion.negocio.vo.TaskVO;

/**
 * Task queue comparator. Higher priority to tasks which have 
 * smallest source size
 *
 * @author GUADALTEL S.A
 */
public class TaskQueueComparator implements Comparator<Runnable> {
   
   /**
    * Main constructor
    */
   public TaskQueueComparator() { }
   
   @Override
   @SuppressWarnings("unchecked")
   public int compare(Runnable r1, Runnable r2) {
      int comparation;
      int size1 = 0, size2 = 0;
      
      if (r1 instanceof FutureTaskCallableValue) {
         FutureTaskCallableValue<TaskVO> ft = (FutureTaskCallableValue<TaskVO>) r1;
         TaskVO taskVO = (TaskVO) ft.getCallableValue();
         size1 = taskVO.getTaskEntity().getSource().getNumLines();
      }
      
      if (r2 instanceof FutureTaskCallableValue) {
         FutureTaskCallableValue<TaskVO> ft = (FutureTaskCallableValue<TaskVO>) r2;
         TaskVO taskVO = (TaskVO) ft.getCallableValue();
         size2 = taskVO.getTaskEntity().getSource().getNumLines();
      }
      
      comparation = (size1 - size2);
      
      return comparation;
   }

}
