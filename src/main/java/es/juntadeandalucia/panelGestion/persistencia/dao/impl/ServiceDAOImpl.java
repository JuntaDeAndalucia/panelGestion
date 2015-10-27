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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import es.juntadeandalucia.panelGestion.persistencia.dao.ServiceDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Service;
import es.juntadeandalucia.panelGestion.persistencia.entidades.ServiceType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.TableXService;

@AutoCreate
@Name("serviceDao")
public class ServiceDAOImpl extends BaseDAO implements ServiceDAO, Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -5968945768474415187L;
   
   @In(create = true, required = true, value = "persistenceContextPanelGestion")
   private EntityManager entityManager;
   
   public ServiceDAOImpl() { }
   
   @Override
   public ServiceType findType(String acronym) {
      ServiceType serviceType = null;
      Criteria serviceTypeCriteria = getSession().createCriteria(ServiceType.class);
      serviceTypeCriteria.add(Restrictions.ilike("acronym", acronym));
      @SuppressWarnings("unchecked")
      List<ServiceType> serviceTypes = serviceTypeCriteria.list();
      if (!serviceTypes.isEmpty()) {
         serviceType = serviceTypes.get(0);
      }
      return serviceType;
   }

   @Override
   public void create(Service service) throws Exception {
      service.setCreationDate(new Date());
      EntityTransaction tx = null;
      try {
         tx = entityManager.getTransaction();
         tx.begin();
         entityManager.persist(service);
         tx.commit();
      }
      catch (Exception e) {
         if ((tx != null) && tx.isActive()) {
            tx.rollback();
         }
         throw e;
      }
   }

   @Override
   public void createRelation(Service service, Table table) throws Exception {
      // creates the tableXservice
      TableXService tableXservice = new TableXService();
      tableXservice.setService(service);
      tableXservice.setTable(table);
      tableXservice.setCreationDate(new Date());
      
      service.getTableXservices().add(tableXservice);
      table.getTableXservices().add(tableXservice);
      
      // creates the relation
      EntityTransaction tx = null;
      try {
         tx = entityManager.getTransaction();
         tx.begin();
         entityManager.persist(tableXservice);
         tx.commit();
      }
      catch (Exception e) {
         if ((tx != null) && tx.isActive()) {
            tx.rollback();
         }
         throw e;
      }
   }
   
   @Override
   public List<Service> findBy(ServiceType type, Table table) {
      List<Service> services = new LinkedList<Service>();
      
      Criteria tableXserviceCriteria = getSession().createCriteria(TableXService.class, "txs");
      tableXserviceCriteria = tableXserviceCriteria.createAlias("txs.service", "service");
      tableXserviceCriteria = tableXserviceCriteria.createAlias("txs.table", "table");
      tableXserviceCriteria = tableXserviceCriteria.add(Restrictions.eq("service.type", type));
      tableXserviceCriteria = tableXserviceCriteria.add(Restrictions.eq("table", table));
      @SuppressWarnings("unchecked")
      List<TableXService> tableXservices = tableXserviceCriteria.list();
      for (TableXService tableXservice : tableXservices) {
         services.add(tableXservice.getService());
      }
      return services;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public List<Service> findBy(ServiceType type, String name) {
      List<Service> services = null;
      
      Criteria serviceCriteria = getSession().createCriteria(Service.class, "service");
      serviceCriteria = serviceCriteria.add(Restrictions.eq("service.type", type));
      serviceCriteria = serviceCriteria.add(Restrictions.ilike("name", name));
      
      services = serviceCriteria.list();
      
      return services;
   }
   
   @Override
   public void delete(Service service) throws Exception {
      Exception exception = null;
      
      Session session = getSession();
      Transaction tx = null;
      
      try {
         tx = session.beginTransaction();
         for (TableXService txs : service.getTableXservices()) {
            session.delete(txs);
         }
         session.delete(service);
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
}
