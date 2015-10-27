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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import es.juntadeandalucia.panelGestion.negocio.servicios.ServiceService;
import es.juntadeandalucia.panelGestion.negocio.utiles.Geoserver;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeoserverVO;
import es.juntadeandalucia.panelGestion.negocio.vo.ServiceVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Service;
import es.juntadeandalucia.panelGestion.persistencia.entidades.ServiceType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

/**
 * Seam controller that manages the new OGC services
 *
 * @author GUADALTEL S.A
 */
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("ogccontroller")
public class OGCController implements Serializable {
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -9107081680911285127L;
   
   /**
    * Log for this class
    */
   private Logger log = Logger.getLogger(OGCController.class);
   
   /**
    * Workspace controller
    */
   @In(scope = ScopeType.CONVERSATION)
   private WorkspaceController wscontroller;
   
   /**
    * Mapea controller
    */
   @In
   private MapeaController mapeacontroller;
   
   /**
    * Service which manages services entities
    */
   @In
   private ServiceService serviceService;
   
   /**
    * Available workspaces in the specified Geoserver
    */
   private List<String> workspaces;
   
   /**
    * Name of the new workspace
    */
   private String workspace;
   
   /**
    * Available data stores of the selected workspace
    */
   private List<String> datastores;
   
   /**
    * Name of the new data store
    */
   private String datastore;
   
   /**
    * Layer title
    */
   private String title;
   
   /**
    * Layer name
    */
   private String name;
   
   /**
    * Flag that indicates if the user specified a
    * new workspace
    */
   private boolean newWorkspace;
   
   /***
    * Flag that indicates if the suer specified a
    * new datastore
    */
   private boolean newDatastore;
   
   /**
    * Created services list for the
    * selected table
    */
   private List<ServiceVO> services;
   
   @In("#{msg['ogc.services.wms']}")
   private String serviceWMSMessage;
   
   @In("#{msg['ogc.services.wfs']}")
   private String serviceWFSMessage;
   
   @In("#{msg['ogc.services.mapea']}")
   private String mapeaMessage;
   
   @In("#{msg['ogc.services.preview']}")
   private String previewMessage;
   
   private Geoserver selectedGeoserver;
   
   private List<Geoserver> geoservers;
   
   /**
    * Main constructor
    */
   public OGCController() {
	   geoservers = new LinkedList<Geoserver>();
	   for (GeoserverVO gVO: PanelSettings.geoservers) {
		   geoservers.add(new Geoserver(gVO));
	   }
      workspaces = new LinkedList<String>();
      datastores = new LinkedList<String>();
      services = new LinkedList<ServiceVO>();
   }
   
   /**
    * This method is triggered when the user
    * selects a table
    */
   public void onSelectTable() {
	  if (wscontroller != null) {
		  Table table = wscontroller.getTable();
	      selectTable(table);
	  }
      
   }

   /**
    * This method is called when selecting a geoserver in view for
    * created a service OGC
    */
   public void onSelectedGeoserverVO() {
	   if (selectedGeoserver == null) {
		   workspaces.clear();
	   } else {
		   workspaces = selectedGeoserver.getWorkspaces();
	   }
   }
   /**
    * Action that switches between a
    * new data store
    */
   public void changeDatastore() {
      newDatastore = !newDatastore;
      datastore = null;
   }
   
   /**
    * Action that switches between a
    * new workspaces
    */
   public void changeWorkspace() {
      newWorkspace = !newWorkspace;
      workspace = null;
      datastore = null;
      datastores.clear();
   }
   
   /**
    * Action that publishes the new layer with the specified data
    * by the user
    */
   public void publishServices() {
      try {
         // checks if the layer was already created on the selected workspace
         Table table = wscontroller.getTable();
         String wmsUrl = selectedGeoserver.getWMSUrl(workspace);
         List<Service> wmsServices = serviceService.getWMSServices(table);
         for (Service wmsService : wmsServices) {
            if (wmsService.getServiceUrl().equals(wmsUrl)) {
               throw new IllegalArgumentException("la tabla '" + table.getName()
                  + "' ya ha sido publicada sobre el workspace '" + workspace + "'");
            }
         }
         
         // creates the new layer
         boolean createdLayer = selectedGeoserver
            .createLayer(name, title, workspace, datastore, table);
         if (!createdLayer) {
            throw new Exception("No se ha podido crear la capa '"
               + name + "' por causas desconocidas");
         }
         
         // creates a new WMS service
         ServiceType wmsServiceType = serviceService.getServiceType("WMS");
         Service wmsService = new Service();
         wmsService.setName(name);
         wmsService.setServiceUrl(wmsUrl);
         wmsService.setType(wmsServiceType);
         serviceService.create(wmsService, table);
         
         // creates a new WFS service
         ServiceType wfsServiceType = serviceService.getServiceType("WFS");
         String wfsUrl = selectedGeoserver.getWFSUrl(workspace);
         Service wfsService = new Service();
         wfsService.setName(name);
         wfsService.setServiceUrl(wfsUrl);
         wfsService.setType(wfsServiceType);
         serviceService.create(wfsService, table);

         // creates the services to the list
         createServicesList(wmsService, wfsService);
         
         StatusMessages.instance().add(Severity.INFO,
            "Los servicios se han publicado correctamente");
      }
      catch (Exception e) {
         String errorMessage = e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }
   
   /**
    * This method creates the workspace into the
    * Geoserver instance through its API REST
    */
   public void saveWorkspace() {
      if (newWorkspace) {
         if (StringUtils.isEmpty(workspace)) {
            String errorMessage = "No se ha especificado ningún espacio de trabajo";
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
         else if (!Utils.isValidName(workspace)) {
            String errorMessage = "Nombre del espacio de trabajo inválido: " + workspace;
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
         else {
            // checks if the workspace exists
            boolean existsWorkspace = false;
            for (String publishedWorkspace : workspaces) {
               if (publishedWorkspace.equals(workspace)) {
                  existsWorkspace = true;
                  break;
               }
            }
            if (existsWorkspace) {
               String errorMessage = "El workspace '" + workspace + "' ya existe";
               StatusMessages.instance().add(Severity.ERROR, errorMessage);
               log.error(errorMessage);
            }
            else {
               boolean createdWorkspace = selectedGeoserver.createWorkspace(workspace);
               if (createdWorkspace) {
                  // reloads the workspaces
                  workspaces = selectedGeoserver.getWorkspaces();
                  newWorkspace = false;
                  datastore = null;
                  datastores.clear();
                  newDatastore = true;
                  String successMessage = "Se ha creado correctamente el workspace '" + workspace
                     + "'";
                  StatusMessages.instance().add(Severity.INFO, successMessage);
                  log.info(successMessage);
               }
               else {
                  String errorMessage = "No se ha podido crear el nuevo workspace '" + workspace
                     + "' por causas desconocidas";
                  StatusMessages.instance().add(Severity.ERROR, errorMessage);
                  log.error(errorMessage);
               }
            }
         }
      }
   }
   
   /**
    * This method creates a new datastore into the
    * Geoserver instance through its API REST
    */
   public void saveDatastore() {
      if (newDatastore) {
         if (StringUtils.isEmpty(datastore)) {
            String errorMessage = "No se ha especificado ningún datastore";
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
         else if (!Utils.isValidName(datastore)) {
            String errorMessage = "Nombre del almacén de datos inválido: " + datastore;
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
         else {
            try {
               Schema schema = wscontroller.getSchema();
               boolean createdDatastore = selectedGeoserver.createDataStore(workspace,
                  datastore, schema);
               if (createdDatastore) {
                  // reloads the data sotres from the selected workspace
                  datastores.clear();
                  datastores = selectedGeoserver.getDatastoresFromWorkspace(workspace);
                  newDatastore = false;
                  String successMessage = "Se ha creado correctamente el datastore '" + datastore
                     + "'";
                  StatusMessages.instance().add(Severity.INFO, successMessage);
                  log.info(successMessage);
               }
               else {
                  String errorMessage = "No se ha podido crear el nuevo almacén de datos '"
                     + datastore + "' por causas desconocidas";
                  StatusMessages.instance().add(Severity.ERROR, errorMessage);
                  log.error(errorMessage);
               }
            }
            catch (MalformedURLException e) {
               String errorMessage = "No se ha podido crear el nuevo almacén de datos '"
                  + datastore + "'. URL de la base de datos no válida";
               StatusMessages.instance().add(Severity.ERROR, errorMessage);
               log.error(errorMessage);
            }
         }
      }
   }

   /**
    * Loads the data store available in
    * the specified workspace
    */
   public void loadDatastores() {
      datastores.clear();
      datastore = null;
      datastores = selectedGeoserver.getDatastoresFromWorkspace(workspace);
      newDatastore = datastores.isEmpty();
   }
   
   /**
    * This method returns the Geoserver administrator
    * URL for the layer and specified workspace
    *
    * @return the geoserver administrator url
    */
   public String getGeoserverAdminUrl() {
      String geoserverUrl = selectedGeoserver.getGeoserverAUX().getGeoserverUrl().concat("/web");
      if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(workspace)) {
         geoserverUrl = geoserverUrl.concat("?wicket:bookmarkablePage=:org.geoserver." +
            "web.data.resource.ResourceConfigurationPage&name=").concat(name)
            .concat("&wsName=").concat(workspace);
      }
      return geoserverUrl;
   }
   
   /**
    * This method is triggered when the user selects
    * a table
    * 
    * @param table the table selected by the user
    */
   public void selectTable(Table table) {
      if (table != null) {
    	 if (selectedGeoserver != null) {
    		 workspaces = selectedGeoserver.getWorkspaces();
    	 }
         workspace = null;
         datastore = null;
         datastores.clear();
         title = null;
         services.clear();
         name = table.getName();
      }
   }
   
   private void createServicesList(Service wmsService, Service wfsService) {
      services.clear();
      if ((wmsService != null) && (wfsService != null)) {
         // WMS service
         String wmsGetCapabilities = selectedGeoserver
            .getWMSGetCapabilitiesUrl(wmsService.getServiceUrl());
         ServiceVO wmsServiceVO = new ServiceVO();
         wmsServiceVO.setName(wmsService.getName());
         wmsServiceVO.setUrl(serviceWMSMessage);
         wmsServiceVO.setTestServiceUrl(wmsGetCapabilities);

         // WFS service
         String wfsGetCapabilities = selectedGeoserver
            .getWFSGetCapabilitiesUrl(wfsService.getServiceUrl());
         ServiceVO wfsServiceVO = new ServiceVO();
         wfsServiceVO.setName(wfsService.getName());
         wfsServiceVO.setUrl(serviceWFSMessage);
         wfsServiceVO.setTestServiceUrl(wfsGetCapabilities);

         // Mapea service
         String mapeaUrl = mapeacontroller.getUrlForWMSLayer(wmsService.getName(),
            wfsService.getServiceUrl(), true);
         ServiceVO mapeaServiceVO = new ServiceVO();
         mapeaServiceVO.setName(wmsService.getName());
         mapeaServiceVO.setUrl(mapeaMessage);
         mapeaServiceVO.setTestServiceUrl(mapeaUrl);
         
         // Preview service
         ServiceVO previewServiceVO = new ServiceVO();
         previewServiceVO.setName(wmsService.getName());
         previewServiceVO.setUrl(previewMessage);
         previewServiceVO.setTestServiceUrl(selectedGeoserver.getServicePreview(workspace, name));

         // adds the services
         services.add(wmsServiceVO);
         services.add(wfsServiceVO);
         services.add(mapeaServiceVO);
         services.add(previewServiceVO);
      }
   }
   
   /**
    * resets the form
    */
   public void reset() {
      workspaces.clear();
      workspaces = selectedGeoserver.getWorkspaces();
      workspace = null;
      datastores.clear();
      datastore = null;
      title = null;
//      name = null;
      newWorkspace = false;
      newDatastore = false;
      services.clear();
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
    * @param workspaces the workspaces to set
    */
   public void setWorkspaces(List<String> workspaces) {
      this.workspaces = workspaces;
   }
   
   /**
    * @return the workspaces
    */
   public List<String> getWorkspaces() {
      return workspaces;
   }

   /**
    * @return the workspace
    */
   public String getWorkspace() {
      return workspace;
   }

   /**
    * @param workspace the workspace to set
    */
   public void setWorkspace(String workspace) {
      this.workspace = workspace;
   }

   /**
    * @param datastores the datastores to set
    */
   public void setDatastores(List<String> datastores) {
      this.datastores = datastores;
   }
   
   /**
    * @return the datastores
    */
   public List<String> getDatastores() {
      return datastores;
   }

   /**
    * @return the datastore
    */
   public String getDatastore() {
      return datastore;
   }

   /**
    * @param datastore the datastore to set
    */
   public void setDatastore(String datastore) {
      this.datastore = datastore;
   }

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the newWorkspace
    */
   public boolean isNewWorkspace() {
      return newWorkspace;
   }

   /**
    * @param newWorkspace the newWorkspace to set
    */
   public void setNewWorkspace(boolean newWorkspace) {
      this.newWorkspace = newWorkspace;
   }

   /**
    * @return the newDatastore
    */
   public boolean isNewDatastore() {
      return newDatastore;
   }

   /**
    * @param newDatastore the newDatastore to set
    */
   public void setNewDatastore(boolean newDatastore) {
      this.newDatastore = newDatastore;
   }

   /**
    * @return the mapeacontroller
    */
   public MapeaController getMapeacontroller() {
      return mapeacontroller;
   }

   /**
    * @param mapeacontroller the mapeacontroller to set
    */
   public void setMapeacontroller(MapeaController mapeacontroller) {
      this.mapeacontroller = mapeacontroller;
   }

   /**
    * @return the services
    */
   public List<ServiceVO> getServices() {
      return services;
   }

   /**
    * @param services the services to set
    */
   public void setServices(List<ServiceVO> services) {
      this.services = services;
   }

   /**
    * 
    * @return geoserverVO selected in view
    */
	public Geoserver getSelectedGeoserver() {
		return selectedGeoserver;
	}
	
	public void setSelectedGeoserver(Geoserver selectedGeoserver) {
		this.selectedGeoserver = selectedGeoserver;
	}

	public List<GeoserverVO> getGeoserverVO(){
		return PanelSettings.geoservers;
	}

	public List<Geoserver> getGeoservers() {
		return geoservers;
	}

	public void setGeoservers(List<Geoserver> geoservers) {
		this.geoservers = geoservers;
	}

	

   
}
