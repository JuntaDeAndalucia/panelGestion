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
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import es.juntadeandalucia.panelGestion.persistencia.dao.TableDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

@AutoCreate
@Name("tableDao")
public class TableDAOImpl extends BaseDAO implements TableDAO, Serializable {

   /**
    * Generated version UID
    */
   private static final long serialVersionUID = -6453555297114204591L;

   @In(create = true, required = true, value = "persistenceContextPanelGestion")
   private EntityManager entityManager;
   
   public TableDAOImpl() { }
   
   @Override
   public Table findById(long id) {
      Table table = (Table) getSession().get(Table.class, id);
      return table;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Table> findBySchema(long schemaId) {
      Criteria criteria = getSession().createCriteria(Table.class);
      
      criteria.setFetchMode("schema", FetchMode.JOIN);
      criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      criteria.add(Restrictions.eq("schema.id", schemaId));
      
      return criteria.list();
   }
   
   @Override
   public boolean create(Table table) throws Exception {
      table.setCreationDate(new Date());
      EntityTransaction tx = null;
      Session session = getSession();
      try {
         tx = entityManager.getTransaction();
         tx.begin();
         entityManager.persist(table);
         tx.commit();
         session.flush();
      }
      catch (Exception e) {
         if ((tx != null) && tx.isActive()) {
            tx.rollback();
         }
         throw e;
      }
      
      return true;
   }
   
   @Override
   public void update(Table table) throws Exception {
      Exception exception = null;
      
      table.setModificationDate(new Date());
      
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         session.saveOrUpdate(table);
         tx.commit();
      }
      catch(Exception e) {
         tx.rollback();
         exception = e;
         session.flush();
      }
      if (exception != null) {
         throw exception;
      }
   }
   
   @Override
   public void delete(Table table) throws Exception {
      Exception exception = null;
      
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         session.delete(table);
         tx.commit();
      }
      catch(Exception e) {
         tx.rollback();
         exception = e;
         session.flush();
      }
      if (exception != null) {
         throw exception;
      }
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
