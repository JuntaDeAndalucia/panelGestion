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
package es.juntadeandalucia.panelGestion.persistencia.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import es.juntadeandalucia.panelGestion.persistencia.dao.TaskDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Status;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;

@AutoCreate
@Name("taskDao")
public class TaskDAOImpl extends BaseDAO implements TaskDAO, Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -8229666999595957888L;
   
   @In(create = true, required = true, value = "persistenceContextPanelGestion")
   private EntityManager entityManager;
   
   @Override
   public boolean update(Task task) throws Exception {
      Exception exception = null;
      
      boolean updatedTask = false;
      
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         session.saveOrUpdate(task);
         tx.commit();
         updatedTask = true;
      }
      catch(Exception e) {
         tx.rollback();
         exception = e;
      }
      if (exception != null) {
         throw exception;
      }
      
      return updatedTask;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public List<Task> getTasksByStatus(Status status) {
      List<Task> tasks;
      
      Criteria criteria = getSession().createCriteria(Task.class, "task")
         .add(Restrictions.eq("state.status", status));
      tasks = criteria.list();
      
      return tasks;
   }
   
   @Override
   public void delete(Task task) throws Exception {
      Exception exception = null;
      
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         session.delete(task);
         tx.commit();
      }
      catch(Exception e) {
         tx.rollback();
         exception = e;
      }
      if (exception != null) {
         throw exception;
      }
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public List<Task> getTasksByTable(long tableId) {
      List<Task> tasks;
      
      Criteria criteria = getSession().createCriteria(Task.class, "task")
         .add(Restrictions.eq("table.id", tableId));
      tasks = criteria.list();
      
      return tasks;
   }

   /**
    * @return the entityManager
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager the entityManager to set
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}
