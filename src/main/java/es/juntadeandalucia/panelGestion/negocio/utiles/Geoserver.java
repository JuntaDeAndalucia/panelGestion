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

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import es.juntadeandalucia.panelGestion.negocio.vo.GeoserverVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

public class Geoserver {
   
   private static final Logger log = Logger.getLogger(Geoserver.class);
   
   private GeoserverVO geoserverAUX;
   private GeoServerRESTManager gsManager;
   
   public Geoserver(GeoserverVO geoserverVO) {
	   geoserverAUX = geoserverVO;
      try {
         URL geoserverUrl = URI.create(geoserverAUX.getGeoserverUrl()).toURL();
         gsManager = new GeoServerRESTManager(geoserverUrl, geoserverAUX.getGeoserverUser(), geoserverAUX.getGeoserverPassword());
      }
      catch (MalformedURLException e) {
         log.error("Error al intentar acceder a la API REST de geoserver: " + e.getLocalizedMessage());
      }
   }
   
   public List<String> getWorkspaces() {
      return gsManager.getReader().getWorkspaceNames();
   }
   
   public List<String> getDatastoresFromWorkspace(String workspaceName) {
      List<String> datastoresNames = new LinkedList<String>();
      if (!StringUtils.isEmpty(workspaceName)) {
         // bug in GeoServerRESTReader to list datasources??
         String restDatastoresUrl = geoserverAUX.getGeoserverUrl().concat("/rest/workspaces/").concat(workspaceName)
            .concat("/datastores.json");
         String datastoresResponse;
         try {
            datastoresResponse = HTTPUtils.get(restDatastoresUrl, geoserverAUX.getGeoserverUser(),
            		geoserverAUX.getGeoserverPassword());
            JSONObject jDatastoreResponse = new JSONObject(datastoresResponse);
            JSONObject jDatastores = jDatastoreResponse.getJSONObject("dataStores");
            JSONArray jDatastoresArray = jDatastores.getJSONArray("dataStore");
            for (int i = 0; i < jDatastoresArray.length(); i++) {
               JSONObject jDatastore = jDatastoresArray.getJSONObject(i);
               String datastoreName = jDatastore.getString("name");
               datastoresNames.add(datastoreName);
            }
         }
         catch (MalformedURLException e) {
            log.error("Error al conectar con la API REST de Geoserver: " + e.getLocalizedMessage());
         }
         catch (JSONException e) {
            log.error("Error al procesar la respuesta JSON de Geoserver: "
               + e.getLocalizedMessage());
         }
      }

      return datastoresNames;
   }
   
   /**
    * This method creates a new workspace with the specified
    * name
    *
    * @param workspaceName the workspace name that will be created
    * 
    * @return true if the workspace was created successfully
    */
   public boolean createWorkspace(String workspaceName) {
      boolean createdWorkspace = false;
      
      createdWorkspace = gsManager.getPublisher().createWorkspace(workspaceName);
      
      return createdWorkspace;
   }
   
   /**
    * This method creates a new DataStore in the established workspace 
    * with the specified name and using the data from the entity table
    *
    * @param workspace the workspace name where the data store will be created
    * @param datastoreName the name of the new data store
    * @param schema schema to use to create the data store
    * 
    * @return true if the data store was created successfully
    * 
    * @throws MalformedURLException thrown if the data base URL is not valid
    */
   public boolean createDataStore(String workspace, String datastoreName,
         Schema schema) throws MalformedURLException {
      boolean createdDatastore = false;
      
      if (StringUtils.isEmpty(workspace) ||
            StringUtils.isEmpty(datastoreName) || (schema == null)) {
         throw new IllegalArgumentException(
            "No se han especificado todos los parámetros necesarios");
      }
      // gets datastore params
      DataBase database = schema.getDataBase();
      String connectionUrlString = database.getConnectionUrl();
      connectionUrlString = connectionUrlString.substring(5);
      
      //TODO: pillar el nombre de la url de conexion http......./nombd y ponerlo en el campo databaseName
      
      URI connectionUri = URI.create(connectionUrlString);
      String host = connectionUri.getHost();
      int port = connectionUri.getPort();
      //String databaseName = database.getAlias();
      String databaseName = getNameByUrlConecction(connectionUrlString);	  
      String schemaName = schema.getName();
      String user = schema.getUser();
      String password = schema.getPassword();
      boolean validateConnections = true;
      boolean enabled = true;
      
      // creates store
      GSPostGISDatastoreEncoder store = new GSPostGISDatastoreEncoder(datastoreName);
      store.setHost(host);
      store.setPort(port);
      store.setDatabase(databaseName);
      store.setSchema(schemaName);
      store.setUser(user);
      store.setPassword(password);
      store.setEnabled(enabled);
      store.setValidateConnections(validateConnections);
      
      // uses the API REST
      createdDatastore = gsManager.getStoreManager().create(workspace, store);
      
      return createdDatastore;
   }
   
   /**
    * This method creates a new layer with the specified name and
    * title in the workspace using the data store.
    *
    * @param name name of the layer
    * @param title title of the layer
    * @param workspace workspace where the layer will be added to
    * @param datastore data store where the layer will be read from
    * @param table the table that contains the information
    * 
    * @return true if the layer was created successfully
    */
   public boolean createLayer(String name, String title,
         String workspace, String datastore, Table table) {
      boolean createdLayer = false;
      // gets data
      String srs = table.getEpsg();
      boolean enabled = true;
      boolean queryable = true;
      
      // creates the feature type
      GSFeatureTypeEncoder featureType = new GSFeatureTypeEncoder();
      featureType.setName(name);
      featureType.setTitle(title);
      featureType.setSRS(srs);
      featureType.setEnabled(enabled);
      
      // creates the layer encoder
      GSLayerEncoder layerEncoder = new GSLayerEncoder();
      layerEncoder.setQueryable(queryable);
      layerEncoder.setEnabled(enabled);
      createdLayer = gsManager.getPublisher()
         .publishDBLayer(workspace, datastore, featureType, layerEncoder);
      
      return createdLayer;
   }
   
   public String getWMSUrl(String workspace) {
      String wmsUrl = geoserverAUX.getGeoserverUrl().concat("/");
      if (!StringUtils.isEmpty(workspace)) {
         wmsUrl = wmsUrl.concat(workspace).concat("/");
      }
      wmsUrl = wmsUrl.concat("wms?");
      
      return wmsUrl;
   }
   
   public String getWFSUrl(String workspace) {
      String wfsUrl = geoserverAUX.getGeoserverUrl().concat("/");
      if (!StringUtils.isEmpty(workspace)) {
         wfsUrl = wfsUrl.concat(workspace).concat("/");
      }
      wfsUrl = wfsUrl.concat("wfs?");
      
      return wfsUrl;
   }

   public static String getWMSGetCapabilitiesUrl(String wmsUrl) {
	  GeoserverVO geoserverVO = Utils.getGeoserverVOByURL(wmsUrl);
      String wmsGetCapabilities = wmsUrl.concat("service=WMS&request=GetCapabilities&version=")
         .concat(geoserverVO.getGeoserverWMSVersion());
      
      return wmsGetCapabilities;
   }
   
   public static String getWFSGetCapabilitiesUrl(String wfsUrl) {
	   GeoserverVO geoserverVO = Utils.getGeoserverVOByURL(wfsUrl);
      String wfsGetCapabilities = wfsUrl.concat("service=WFS&request=GetCapabilities&version=")
         .concat(geoserverVO.getGeoserverWFSVersion());
      
      return wfsGetCapabilities;
   }

   public String getServicePreview(String workspace, String name) {
      return geoserverAUX.getGeoserverUrl().concat("/").concat(workspace)
         .concat("/wms/reflect?layers=").concat(name);
   }
   
   
   /**
	* Gets the name of the database connection string to assign to the bd and it will fail when
	* trying to connect.
    * 
    * @param connectionUrlString
    * @return
    */
   private String getNameByUrlConecction(String connectionUrlString) { 
	   	String[] arrayAux = connectionUrlString.split("/");
   		return  arrayAux[arrayAux.length-1];
   		
   }

	public GeoserverVO getGeoserverAUX() {
		return geoserverAUX;
	}
	
	public void setGeoserverAUX(GeoserverVO geoserverAUX) {
		this.geoserverAUX = geoserverAUX;
	}   
	
	
	
	public boolean equals(Object o) {
		boolean res = false;
		if (o != null && o instanceof Geoserver) {
			Geoserver geoserverAux = (Geoserver)o;
			res = geoserverAux.getGeoserverAUX().getGeoserverUrl().equals(this.geoserverAUX.getGeoserverUrl());
		}
		return res;
	}
   
	
	
   public int hashCode() {
      return 29 * this.geoserverAUX.getGeoserverUrl().hashCode();
   }
   

}
