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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.ServiceService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TaskService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.utiles.Geoserver;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.utiles.TaskUtils;
import es.juntadeandalucia.panelGestion.negocio.utiles.TasksQueue;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.MetadataVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RowVO;
import es.juntadeandalucia.panelGestion.negocio.vo.TaskVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Service;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.TableXService;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("gicontroller")
public class GIController implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 4988884680412441537L;

   private static Logger log = Logger.getLogger(GIController.class);

   private final int NUM_ROWS = 20;
   

   @RequestParameter
   private Boolean fromExternal;
   
   /**
    * Mapea controller
    */
   @In
   private MapeaController mapeacontroller;

   /**
    * Workspace controller
    */
   @In
   private WorkspaceController wscontroller;
   
   /**
    * Realidad Aumentada controller
    */
   @In
   private RAController racontroller;

   @In
   private ServiceService serviceService;

   @In
   private TableService tableService;
   
   /**
    * Service which manages Task entities
    */
   @In
   private TaskService taskService;
   
   @In("#{msg['ogc.services.mapea']}")
   private String mapeaMessage;
   
   @In("#{msg['ig.services.ogc.remainder']}")
   private String ogcServicesRemainder;
   
   @In("#{msg['ig.services.ra.remainder']}")
   private String raServiceRemainder;
   
   @In("#{msg['ig.services.geosearch.remainder']}")
   private String geosearchServiceRemainder;
   
   @In(value="#{facesContext.externalContext}")
   private ExternalContext externalCtx;
   
   @In(value="#{facesContext}")
   private FacesContext facesContext;

   private List<Service> wmsServices;
   private List<Service> wfsServices;
   private List<Service> raServices;
   private List<Service> geosearchServices;
   private List<Service> downloadsServices;
   private List<Service> publishedServices;
   private List<MetadataVO> metadata;
   private List<ColumnVO> columns;
   private List<RowVO> rows;
   private Service serviceToRemove;
   private Integer numRows;
   private String geometryWKT;
   private String associatedTaskTicket;
   

   // private WKTWriter writer;

   public GIController() {
      this.wmsServices = new LinkedList<Service>();
      this.wfsServices = new LinkedList<Service>();
      this.raServices = new LinkedList<Service>();
      this.geosearchServices = new LinkedList<Service>();
      this.downloadsServices = new LinkedList<Service>();
      this.publishedServices = new LinkedList<Service>();
      this.metadata = new LinkedList<MetadataVO>();
      this.columns = new LinkedList<ColumnVO>();
      this.rows = new LinkedList<RowVO>();
      // this.writer = new WKTWriter();
   }

   /**
    * This method is triggered when the user selects a table
    */
   public void onSelectTable() {
      if ((fromExternal != null) && fromExternal.booleanValue()) {
         fromExternal = false;
         Table table = wscontroller.getTable();
         if (table != null) {
            selectTable(table);
         }
      }
   }

   /**
    * This method is triggered when the user selects a table
    * 
    * @param table
    *           the table selected by the user
    */
   public void selectTable(Table table) {
      // removes previous values
      cleanLists();

      this.numRows = null;

      this.geometryWKT = null;

      if (table != null) {
         // checks associated tasks
         associatedTaskTicket = getTaskTicketFrom(table);

         // services
         loadServices(table);

         // metadata
         loadMetadata(table);

         // extract info
         extractInfo(table);
      }

   }

   public void removeService(Service service) {
      String errorMessage = null;

      if (service != null) {
         try {
            String serviceType = service.getType().getAcronym();
            
            // gets the associated service
            Service associatedService = null;
            if ("WMS".equals(serviceType)) {
               associatedService = getAssociatedWFSService(service);
            }
            else if ("WFS".equals(serviceType)) {
               associatedService = getAssociatedWMSService(service);
            }

            // removes the associated service
            if (associatedService != null) {
               this.serviceService.delete(associatedService);
            }
            
            // remove the service
            this.serviceService.delete(service);
            
            // unselect the service to remove
            this.serviceToRemove = null;

            // load again the available services (refreshes the list)
            reloadServices(wscontroller.getTable());

            // success message
            String infoMessage = "Se ha eliminado el servicio ".concat(service.getName()).concat(
               " correctamente. ");
            
            // remainder message
            infoMessage = infoMessage.concat(getRemainderMessage(serviceType));
            
            StatusMessages.instance().add(Severity.INFO, infoMessage);
            log.info(infoMessage);
         }
         catch (Exception e) {
            errorMessage = "No se ha podido eliminar el servicio seleccionado: "
               + e.getLocalizedMessage();
         }
      }
      else {
         errorMessage = "No se ha seleccionado un servicio a eliminar";
      }

      if (errorMessage != null) {
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }

   /**
    * TODO
    *
    * @return
    */
   private String getRemainderMessage(String serviceType) {
      String remainderMessage = "";
      
      if ("WMS".equals(serviceType) || "WFS".equals(serviceType)) {
         remainderMessage = getOgcServicesRemainder();
      }
      else if ("RA".equals(serviceType)) {
         remainderMessage = getRaServiceRemainder();
      }
      else if ("GEOBUSQUEDAS".equals(serviceType)) {
         remainderMessage = getGeosearchServiceRemainder();
      }
      
      return remainderMessage;
   }

   /**
    * TODO
    *
    * @param service
    * @return
    */
   private Service getAssociatedWMSService(Service service) {
      Service associatedWmsService = null;
      
      String serviceUrl = service.getServiceUrl();
      String geoserverUrl = Utils.getGeoserverUrl(serviceUrl);
      String workspace = Utils.getWorkspaceFromServiceUrl(serviceUrl);
      
      String geoserverWorkspaceUrl = geoserverUrl.concat("/").concat(workspace);
      for (Service wmsService : wmsServices) {
         if (wmsService.getServiceUrl().startsWith(geoserverWorkspaceUrl)) {
            associatedWmsService = wmsService;
            break;
         }
      }
      
      return associatedWmsService;
   }

   /**
    * TODO
    *
    * @param service
    * @return
    */
   private Service getAssociatedWFSService(Service service) {
      Service associatedWfsService = null;
      
      String serviceUrl = service.getServiceUrl();
      String geoserverUrl = Utils.getGeoserverUrl(serviceUrl);
      String workspace = Utils.getWorkspaceFromServiceUrl(serviceUrl);
      
      String geoserverWorkspaceUrl = geoserverUrl.concat("/").concat(workspace);
      for (Service wfsService : wfsServices) {
         if (wfsService.getServiceUrl().startsWith(geoserverWorkspaceUrl)) {
            associatedWfsService = wfsService;
            break;
         }
      }
      
      return associatedWfsService;
   }

   public void deleteTable() {
      if (associatedTaskTicket == null) {
         String errorMessage = null;
         Table table = wscontroller.getTable();
         if (table != null) {
            if (table.getTableXservices().size() > 0) {
               errorMessage = "No puede eliminarse la tabla ya que existen servicios que depende de ésta";
            }
            else {
               // deletes remotely the table
               try {
                  JDBCConnector connector = new JDBCConnector(table);
                  RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
                  rdbs.deleteTable(table);
               }
               catch (Exception e) {
                  errorMessage = "No se ha podido eliminar físicamente la tabla seleccionada: "
                     + e.getLocalizedMessage();
               }

               // deletes the table
               try {
                  tableService.delete(table);
               }
               catch (Exception e) {
                  errorMessage = "No se ha podido eliminar la tabla seleccionada: "
                     + e.getLocalizedMessage();
               }

               // reset form
               cleanLists();
               serviceToRemove = null;

               // reload the schema
               wscontroller.loadTables();
            }
         }
         else {
            errorMessage = "Por favor, seleccione una tabla para poder borrarla";
         }

         if (errorMessage != null) {
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
         else {
            String infoMessage = "Se ha eliminado la tabla de satisfactoriamente";
            StatusMessages.instance().add(Severity.INFO, infoMessage);
            log.info(infoMessage);
         }
      }
   }

   public void emptyTable() {
      if (associatedTaskTicket == null) {
         String errorMessage = null;
         Table table = wscontroller.getTable();
         if (table != null) {
            associatedTaskTicket = getTaskTicketFrom(table);
            try {
               JDBCConnector connector = new JDBCConnector(table);
               RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
               if (rdbs.cleanTable(table)) {
                  // reload metadata and extract info
                  reloadMetadata(table);
                  reloadExtractInfo(table);

                  // success message
                  String infoMessage = "Se ha vaciado la tabla de forma satisfactoria";
                  StatusMessages.instance().add(Severity.INFO, infoMessage);
                  log.info(infoMessage);
               }
               else {
                  errorMessage = "No se ha podido vaciar la tabla seleccionada";
               }
            }
            catch (Exception e) {
               errorMessage = "No se ha podido vaciar la tabla seleccionada: "
                  + e.getLocalizedMessage();
            }
         }
         else {
            errorMessage = "Por favor, seleccione una tabla para borrar su contenido";
         }

         if (errorMessage != null) {
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
      }
   }
   
   private String getTaskTicketFrom(Table table) {
      String associatedTaskTicket = null;
      
      // checks running tasks
      List<Runnable> runningTasks = TasksQueue.getInstance().getAllRunningTasks();
      for (Runnable r : runningTasks) {
         TaskVO task = TaskUtils.getTaskVOFromRunnable(r);
         if (task != null) {
            Table taskTable = task.getTaskEntity().getTable();
            if (table.getId() == taskTable.getId()) {
               associatedTaskTicket = task.getTaskEntity().getTicket();
               break;
            }
         }
      }
      // checks failed tasks
      List<Task> failedTasks = taskService.getFailedTasks();
      for (Task task : failedTasks) {
         Table taskTable = task.getTable();
         if (table.getId() == taskTable.getId()) {
            associatedTaskTicket = task.getTicket();
            break;
         }
      }
      
      return associatedTaskTicket;
   }

   private void reloadExtractInfo(Table table) {
      this.rows.clear();
      extractInfo(table);
   }

   private void reloadMetadata(Table table) {
      this.columns.clear();
      this.metadata.clear();

      loadMetadata(table);
   }

   private void reloadServices(Table table) {
      this.wmsServices.clear();
      this.wfsServices.clear();
      this.raServices.clear();
      this.geosearchServices.clear();
      this.downloadsServices.clear();
      this.publishedServices.clear();

      loadServices(table);
   }

   private void extractInfo(Table table) {
      JDBCConnector connector;
      try {
         connector = new JDBCConnector(table);
         RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
         this.rows = rdbs.getRows(table, NUM_ROWS);
      }
      catch (Exception e) {
         String errorMessage = "No se ha podido obtener información de la tabla: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }

   private void loadMetadata(Table table) {
      JDBCConnector connector;
      try {
         connector = new JDBCConnector(table);
         RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
         this.columns = rdbs.getAllColumns(table);
         this.numRows = rdbs.countRows(table);
         String pk = rdbs.getPrimaryKey(table);
         String epsg = table.getEpsg();

         // num columns
         MetadataVO metadataNumColumns = new MetadataVO();
         metadataNumColumns.setDescription("Número de campos");
         metadataNumColumns.setValue(String.valueOf(this.columns.size()));
         this.metadata.add(metadataNumColumns);

         // columns
         for (int i = 0; i < this.columns.size(); i++) {
            ColumnVO column = this.columns.get(i);
            String fieldName = column.getNameOnTable();
            String fieldType = column.getType();

            StringBuilder field = new StringBuilder("CAMPO ");
            field.append(i + 1).append(": ").append(fieldName);

            MetadataVO metadataField = new MetadataVO();
            metadataField.setDescription(field.toString());
            metadataField.setValue(fieldType);

            this.metadata.add(metadataField);
         }
         // num rows
         MetadataVO metadataNumRows = new MetadataVO();
         metadataNumRows.setDescription("Número de registros");
         metadataNumRows.setValue(String.valueOf(numRows));
         this.metadata.add(metadataNumRows);

         // primary key
         MetadataVO metadataPK = new MetadataVO();
         metadataPK.setDescription("Primary Key");
         metadataPK.setValue(pk);
         this.metadata.add(metadataPK);

         // EPSG
         MetadataVO metadataEPSG = new MetadataVO();
         metadataEPSG.setDescription("EPSG");
         metadataEPSG.setValue(epsg);
         this.metadata.add(metadataEPSG);
      }
      catch (Exception e) {
         String errorMessage = "No se ha podido obtener información de la tabla: "
            + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }

   private void loadServices(Table table) {
      this.wmsServices = this.serviceService.getWMSServices(table);
      this.wfsServices = this.serviceService.getWFSServices(table);
      this.raServices = this.serviceService.getRAServices(table);
      this.geosearchServices = this.serviceService.getGeosearchServices(table);
      this.downloadsServices = this.serviceService.getDownloadsServices(table);
      this.publishedServices.addAll(this.wmsServices);
      this.publishedServices.addAll(this.wfsServices);
      this.publishedServices.addAll(this.raServices);
      this.publishedServices.addAll(this.geosearchServices);
      this.publishedServices.addAll(this.downloadsServices);
   }

   private void cleanLists() {
      this.wmsServices.clear();
      this.wfsServices.clear();
      this.raServices.clear();
      this.geosearchServices.clear();
      this.downloadsServices.clear();
      this.publishedServices.clear();
      this.metadata.clear();
      this.columns.clear();
      this.rows.clear();
   }
   
   public String getMapeaUrl(Service service) {
      String mapeaUrl = null;
      
      String serviceUrl = service.getServiceUrl();
      String serviceName = service.getName();
      String serviceType = service.getType().getAcronym();
      
      if ("WMS".equals(serviceType)) {
         mapeaUrl = mapeacontroller.getUrlForWMSLayer(serviceName, serviceUrl, true);
      }
      else if ("WFS".equals(serviceType)) {
         String workspace = Utils.getWorkspaceFromServiceUrl(serviceUrl);
         String geometryType = getGeometryType(service);
         Geoserver geoserver = Utils.getGeoserverFromWFSCapabilitiesUrl(serviceUrl);
         if (!StringUtils.isEmpty(workspace) && !StringUtils.isEmpty(geometryType)
            && (geoserver!= null)) {
            mapeaUrl = mapeacontroller.getUrlForWFSLayer(serviceName, workspace, geometryType, geoserver);
         }
      }
      else if ("GEOBUSQUEDAS".equals(serviceType)) {
         mapeaUrl = mapeacontroller.getUrlForGeosearch(serviceUrl);
      }
     
      return mapeaUrl;
   }
   
   /**
    * TODO
    *
    * @param service
    * @return
    */
   private String getGeometryType(Service service) {
      String geometryType = null;

      if (!service.getTableXservices().isEmpty()) {
         TableXService tableXService = service.getTableXservices().get(0);
         Table table = tableXService.getTable();
         if (table != null) {
            try {
               JDBCConnector connector = new JDBCConnector(table);
               RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
               geometryType = rdbs.getGeomtryType(table);
            }
            catch (Exception e) {
               String errorMessage = "No se ha podido obtener el tipo de geometría del servicio: "
                  + e.getLocalizedMessage();
               StatusMessages.instance().add(Severity.ERROR, errorMessage);
               log.error(errorMessage);
            }
         }
      }
      
      return geometryType;
   }

   public boolean hasMapea(Service service) {
      // has a Mapea URL if the type is WMS, WFS or geosearch service
      String acronym = service.getType().getAcronym();
      
      return ("WMS".equalsIgnoreCase(acronym)
         || "WFS".equalsIgnoreCase(acronym) || "GEOBUSQUEDAS".equalsIgnoreCase(acronym));
   }

   public void getRAFile(Service service) {
      String errorMessage = null;

      ServletOutputStream os = null;
      
      try {
         // configures the response
         String fileNameWithExtension = racontroller.getFileNameWithExtension(service);
         HttpServletResponse response = (HttpServletResponse) externalCtx.getResponse();
         response.setContentType("text/xml");
         response.addHeader("Content-disposition", "attachment; filename=\""
            .concat(fileNameWithExtension).concat("\""));
         
         // gets the configuration file
         InputStream is = racontroller.getConfigurationFile(service);

         os = response.getOutputStream();
         
         IOUtils.copy(is, os);

         os.flush();
         os.close();
         
         is.close();
         
         facesContext.responseComplete();
      }
      catch (IOException e) {
         errorMessage = "Error al comprimir los archivos de configuración: "
            + e.getLocalizedMessage();
      }
      catch (Exception e) {
         errorMessage = "Error en la descarga de la configuración: " + e.getLocalizedMessage();
      }
      finally {
         try {
            if (os != null) {
               os.flush();
               os.close();
            }
         }
         catch (IOException e) {
            errorMessage = "Error al comprimir los archivos de configuración: "
               + e.getLocalizedMessage();
         }
      }

      if (errorMessage != null) {
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }
   
   public String completeURLWMSorWFS(Service entityService) {
      if (entityService.getType().getAcronym().equals("WMS")) {
         return Geoserver.getWMSGetCapabilitiesUrl(entityService.getServiceUrl());
      }
      else {
         if (entityService.getType().getAcronym().equals("WFS")) {
            return Geoserver.getWFSGetCapabilitiesUrl(entityService.getServiceUrl());
         }
         else {
            return entityService.getServiceUrl();
         }
      }

   }

   public void readGeometry(RowVO row) {
      geometryWKT = row.get(row.getGeomFieldName());
   }

   public boolean hasPublishedWMS() {
      return (wmsServices.size() > 0);
   }

   public boolean hasPublishedWFS() {
      return (wfsServices.size() > 0);
   }

   public boolean hasPublishedRA() {
      return (raServices.size() > 0);
   }

   public boolean hasPublishedGeosearch() {
      return (geosearchServices.size() > 0);
   }

   public boolean hasPublishedDownloads() {
      return (downloadsServices.size() > 0);
   }

   /**
    * @return the wmsServices
    */
   public List<Service> getWmsServices() {
      return wmsServices;
   }

   /**
    * @param wmsServices
    *           the wmsServices to set
    */
   public void setWmsServices(List<Service> wmsServices) {
      this.wmsServices = wmsServices;
   }

   /**
    * @return the wfsServices
    */
   public List<Service> getWfsServices() {
      return wfsServices;
   }

   /**
    * @param wfsServices
    *           the wfsServices to set
    */
   public void setWfsServices(List<Service> wfsServices) {
      this.wfsServices = wfsServices;
   }

   /**
    * @return the raServices
    */
   public List<Service> getRaServices() {
      return raServices;
   }

   /**
    * @param raServices
    *           the raServices to set
    */
   public void setRaServices(List<Service> raServices) {
      this.raServices = raServices;
   }

   /**
    * @return the geosearchServices
    */
   public List<Service> getGeosearchServices() {
      return geosearchServices;
   }

   /**
    * @param geosearchServices
    *           the geosearchServices to set
    */
   public void setGeosearchServices(List<Service> geosearchServices) {
      this.geosearchServices = geosearchServices;
   }

   /**
    * @return the downloadsServices
    */
   public List<Service> getDownloadsServices() {
      return downloadsServices;
   }

   /**
    * @param downloadsServices
    *           the downloadsServices to set
    */
   public void setDownloadsServices(List<Service> downloadsServices) {
      this.downloadsServices = downloadsServices;
   }

   /**
    * @return the publishedServices
    */
   public List<Service> getPublishedServices() {
      return publishedServices;
   }

   /**
    * @param publishedServices
    *           the publishedServices to set
    */
   public void setPublishedServices(List<Service> publishedServices) {
      this.publishedServices = publishedServices;
   }

   /**
    * @return the metadata
    */
   public List<MetadataVO> getMetadata() {
      return metadata;
   }

   /**
    * @param metadata
    *           the metadata to set
    */
   public void setMetadata(List<MetadataVO> metadata) {
      this.metadata = metadata;
   }

   /**
    * @return the columns
    */
   public List<ColumnVO> getColumns() {
      return columns;
   }

   /**
    * @param columns
    *           the columns to set
    */
   public void setColumns(List<ColumnVO> columns) {
      this.columns = columns;
   }

   /**
    * @return the rows
    */
   public List<RowVO> getRows() {
      return rows;
   }

   /**
    * @param rows
    *           the rows to set
    */
   public void setRows(List<RowVO> rows) {
      this.rows = rows;
   }

   /**
    * @return the serviceToRemove
    */
   public Service getServiceToRemove() {
      return serviceToRemove;
   }

   /**
    * @param serviceToRemove
    *           the serviceToRemove to set
    */
   public void setServiceToRemove(Service serviceToRemove) {
      this.serviceToRemove = serviceToRemove;
   }

   /**
    * @return the numRows
    */
   public Integer getNumRows() {
      return numRows;
   }

   /**
    * @param numRows
    *           the numRows to set
    */
   public void setNumRows(Integer numRows) {
      this.numRows = numRows;
   }

   /**
    * @return the geometryWKT
    */
   public String getGeometryWKT() {
      return geometryWKT;
   }

   /**
    * @param geometryWKT
    *           the geometryWKT to set
    */
   public void setGeometryWKT(String geometryWKT) {
      this.geometryWKT = geometryWKT;
   }

   /**
    * @return the associatedTaskTicket
    */
   public String getAssociatedTaskTicket() {
      return associatedTaskTicket;
   }

   /**
    * @param associatedTaskTicket the associatedTaskTicket to set
    */
   public void setAssociatedTaskTicket(String associatedTaskTicket) {
      this.associatedTaskTicket = associatedTaskTicket;
   }

   public MapeaController getMapeacontroller() {
      return mapeacontroller;
   }

   public void setMapeacontroller(MapeaController mapeacontroller) {
      this.mapeacontroller = mapeacontroller;
   }

   public String getMapeaMessage() {
      return mapeaMessage;
   }

   public void setMapeaMessage(String mapeaMessage) {
      this.mapeaMessage = mapeaMessage;
   }

   /**
    * @return the ogcServicesRemainder
    */
   public String getOgcServicesRemainder() {
      return ogcServicesRemainder;
   }

   /**
    * @param ogcServicesRemainder the ogcServicesRemainder to set
    */
   public void setOgcServicesRemainder(String ogcServicesRemainder) {
      this.ogcServicesRemainder = ogcServicesRemainder;
   }

   /**
    * @return the raServiceRemainder
    */
   public String getRaServiceRemainder() {
      return raServiceRemainder;
   }

   /**
    * @param raServiceRemainder the raServiceRemainder to set
    */
   public void setRaServiceRemainder(String raServiceRemainder) {
      this.raServiceRemainder = raServiceRemainder;
   }

   /**
    * @return the geosearchServiceRemainder
    */
   public String getGeosearchServiceRemainder() {
      return geosearchServiceRemainder;
   }

   /**
    * @param geosearchServiceRemainder the geosearchServiceRemainder to set
    */
   public void setGeosearchServiceRemainder(String geosearchServiceRemainder) {
      this.geosearchServiceRemainder = geosearchServiceRemainder;
   }

   /**
    * @return the wscontroller
    */
   public WorkspaceController getWscontroller() {
      return wscontroller;
   }

   /**
    * @param wscontroller the wscontroller to set
    */
   public void setWscontroller(WorkspaceController wscontroller) {
      this.wscontroller = wscontroller;
   }

   /**
    * @return the racontroller
    */
   public RAController getRacontroller() {
      return racontroller;
   }

   /**
    * @param racontroller the racontroller to set
    */
   public void setRacontroller(RAController racontroller) {
      this.racontroller = racontroller;
   }

   /**
    * @return the fromExternal
    */
   public boolean isFromExternal() {
      return fromExternal;
   }

   /**
    * @param fromExternal the fromExternal to set
    */
   public void setFromExternal(boolean fromExternal) {
      this.fromExternal = fromExternal;
   }
}
