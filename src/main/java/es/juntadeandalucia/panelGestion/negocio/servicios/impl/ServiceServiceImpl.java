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
package es.juntadeandalucia.panelGestion.negocio.servicios.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import es.juntadeandalucia.panelGestion.negocio.servicios.ServiceService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.persistencia.dao.ServiceDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Service;
import es.juntadeandalucia.panelGestion.persistencia.entidades.ServiceType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

@AutoCreate
@Name("serviceService")
@Scope(ScopeType.EVENT)
public class ServiceServiceImpl implements ServiceService, Serializable {
   
   @In
   private TableService tableService;
   
   @In
   private ServiceDAO serviceDao;
   
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 1695743280565995176L;

   @Override
   public ServiceType getServiceType(String acronym) {
      return serviceDao.findType(acronym);
   }

   @Override
   public boolean create(Service service, Table table) throws Exception {
      // adds the tableXservice to the service
      serviceDao.create(service);
      
      // creates the tableXservice
      serviceDao.createRelation(service, table);
      
      // adds the tableXservice to the table
      tableService.update(table);
      
      return true;
   }

   @Override
   public List<Service> getWMSServices(Table table) {
      ServiceType type = getServiceType("WMS");
      return serviceDao.findBy(type, table);
   }

   @Override
   public List<Service> getWFSServices(Table table) {
      ServiceType type = getServiceType("WFS");
      return serviceDao.findBy(type, table);
   }

   @Override
   public List<Service> getOGCServices(Table table) {
      List<Service> ogcServices = new LinkedList<Service>();
      List<Service> wmsServices = getWMSServices(table);
      List<Service> wfsServices = getWFSServices(table);
      
      ogcServices.addAll(wmsServices);
      ogcServices.addAll(wfsServices);
      
      return ogcServices;
   }

   @Override
   public Service getRAServiceByName(String name) {
      Service raService = null;
      
      ServiceType raType = getServiceType("RA");
      List<Service> raServices = serviceDao.findBy(raType, name);
      if (!raServices.isEmpty()) {
         raService = raServices.get(0);
      }
      
      return raService;
   }

   @Override
   public List<Service> getRAServices(Table table) {
      ServiceType type = getServiceType("RA");
      return serviceDao.findBy(type, table);
   }

   @Override
   public List<Service> getGeosearchServices(Table table) {
      ServiceType type = getServiceType("geobusquedas");
      return serviceDao.findBy(type, table);
   }

   @Override
   public List<Service> getDownloadsServices(Table table) {
      ServiceType type = getServiceType("downloads");
      return serviceDao.findBy(type, table);
   }

   @Override
   public void delete(Service service) throws Exception {
      serviceDao.delete(service);
   }

}
