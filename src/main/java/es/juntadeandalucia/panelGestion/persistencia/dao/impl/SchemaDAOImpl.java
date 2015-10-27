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

import es.juntadeandalucia.panelGestion.persistencia.dao.SchemaDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;

@AutoCreate
@Name("schemaDao")
public class SchemaDAOImpl extends BaseDAO implements SchemaDAO, Serializable {

   /**
    * Generated version UID
    */
   private static final long serialVersionUID = -8459699207420238393L;

   @In(create = true, required = true, value = "persistenceContextPanelGestion")
   private EntityManager entityManager;
   
   
   public SchemaDAOImpl() { }
   
   @SuppressWarnings("unchecked")
   @Override
   public List<Schema> findByDataBase(long dataBaseId) {
      Criteria criteria = getSession().createCriteria(Schema.class);
      
      criteria.setFetchMode("dataBase", FetchMode.JOIN);
      criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      criteria.add(Restrictions.eq("dataBase.id", dataBaseId));
      
      return criteria.list();
   }
   
   @Override
   public Schema findById(long id) {
      Schema schema = (Schema) getSession().get(Schema.class, id);
      return schema;
   }
   
   @Override
   public boolean create(Schema schema) throws Exception {
      schema.setCreationDate(new Date());
      EntityTransaction tx = null;
      try {
         tx = entityManager.getTransaction();
         tx.begin();
         entityManager.persist(schema);
         tx.commit();
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
   public void update(Schema schema) throws Exception {
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         session.saveOrUpdate(schema);
         tx.commit();
      }
      catch(Exception e) {
         tx.rollback();
         throw e;
      }
   }
   
   @Override
   public void delete(Schema schema) throws Exception {
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         session.delete(schema);
         tx.commit();
      }
      catch(Exception e) {
         tx.rollback();
         throw e;
      }
   }

   /**
    * @return the entityManager
    */
   @Override
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager the entityManager to set
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Schema> findAll() {
      Criteria criteria = getSession().createCriteria(Schema.class);
      return criteria.list();
   }
}
