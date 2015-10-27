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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.w3c.dom.Document;

import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.ServiceService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.utiles.JARAFolder;
import es.juntadeandalucia.panelGestion.negocio.utiles.JARALayerWriter;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.negocio.vo.JARALayerVO;
import es.juntadeandalucia.panelGestion.negocio.vo.RAActionVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Service;
import es.juntadeandalucia.panelGestion.persistencia.entidades.ServiceType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("racontroller")
public class RAController implements Serializable {
   
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = 2944654358790194218L;
   
   private static final Logger log = Logger.getLogger(RAController.class);
   
   /**
    * Workspace controller
    */
   @In(scope = ScopeType.CONVERSATION)
   private WorkspaceController wscontroller;
   
   /**
    * Service which manages services entities
    */
   @In
   private ServiceService serviceService;
   
   private String name;
   private String title;
   private String line2;
   private String line3;
   private String line4;
   private String imageUrl;
   private String attribution;
   private boolean newAttribution;
   private List<String> fields;
   
   private List<Integer> availableActionsIndexes;
   private List<RAActionVO> selectedActions;
   private String jaraUrl;
   private String fileXml;
   
   private boolean publishedService;
   private boolean showOverrideDialog;
   
   public RAController() {
      fields = new LinkedList<String>();
      availableActionsIndexes = new LinkedList<Integer>();
      availableActionsIndexes.add(0);
      selectedActions = new LinkedList<RAActionVO>();
      selectedActions.add(new RAActionVO());
   }
   
   /**
    * This method is triggered when the user
    * selects a table
    */
   public void onSelectTable() {
      Table table = wscontroller.getTable();
      selectTable(table);
   }
   
   /**
    * This method is triggered when the user selects
    * a table
    * 
    * @param table the table selected by the user
    */
   public void selectTable(Table table) {
      if (table != null) {
         name = null;
         title = null;
         line2 = null;
         line3 = null;
         line4 = null;
         imageUrl = null;
         attribution = null;
         publishedService = false;
         jaraUrl = null;
         showOverrideDialog = false;
         fileXml = null;
         
         fields.clear();
         getFieldsFromTable(table);
      }
   }

   private void getFieldsFromTable(Table table) {
      fields.clear();
      try {
         RemoteDataBaseService remoteService = new RemoteDataBaseServiceImpl(new JDBCConnector(table.getSchema()));
         List<ColumnVO> columns = remoteService.getAllColumnsExceptPKeyGeom(table);
         for (ColumnVO column : columns) {
            fields.add(column.getNameOnTable());
         }
      }
      catch (Exception e) {
         String errorMessage = 
            "No se ha podido obtener las columnas de la tabla seleccionada: " 
               + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }
   
   public void newAvailableAction() {
      selectedActions.add(new RAActionVO());
      availableActionsIndexes.add(availableActionsIndexes.size());
   }
   
   public void publishService(boolean override) {
      try {
         Table table = wscontroller.getTable();
         
         // packs the layer info
         JARALayerVO jaraLayer = new JARALayerVO();
         jaraLayer.setName(name);
         jaraLayer.setTitle(title);
         jaraLayer.setLine2(line2);
         jaraLayer.setLine3(line3);
         jaraLayer.setLine4(line4);
         jaraLayer.setImageUrl(imageUrl);
         jaraLayer.setAttribution(attribution);
         jaraLayer.setActions(selectedActions);
         jaraLayer.setTable(table);

         // gets the XML content of the layer
         Document xmlJaraLayer = JARALayerWriter.writeDocument(jaraLayer);
         
         // puts the XML into the JARA configuration folder
         JARAFolder.putNewFile(name, xmlJaraLayer, override);
         
         // gets verification cases
         jaraUrl = PanelSettings.jaraUrl.concat(name);
         fileXml = Utils.documentToString(xmlJaraLayer);
         
         // updates the data model creating the RA service
         Service raService = serviceService.getRAServiceByName(name);
         if (raService == null) {
            ServiceType raType = serviceService.getServiceType("RA");
            raService = new Service();
            raService.setName(name);
            raService.setServiceUrl(jaraUrl);
            raService.setType(raType);
            serviceService.create(raService, table);
         }
         
         publishedService = true;
         showOverrideDialog = false;
         
         String successMessage = "El servicio se ha publicado correctamente. No olvide ejecutar "
            + "el paso 3. Puede verificarlo en:";
         StatusMessages.instance().add(Severity.INFO, successMessage);
         log.info(successMessage);
      }
      catch(IllegalArgumentException e) {
         showOverrideDialog = true;
      }
      catch(Exception e) {
         String errorMessage = "No se ha podido añadir el fichero '"
            + name + "': " + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
   }
   
   public void cancelOverritingFile() {
      showOverrideDialog = false;
      publishedService = false;
   }
   
   /**
    * TODO
    *
    * @param service
    * @return
    * @throws FileNotFoundException 
    */
   public InputStream getConfigurationFile(Service service) throws FileNotFoundException {
      InputStream fileIs = null;
      
      String fileName = service.getName();
      File file = JARAFolder.getFile(fileName);
      fileIs = new FileInputStream(file);
      
      return fileIs;
   }

   /**
    * TODO
    *
    * @param service
    * @return
    */
   public String getFileNameWithExtension(Service service) {
      String fileNameWithExtension = null;
      
      String fileName = service.getName();
      fileNameWithExtension = JARAFolder.addExtension(fileName);
      
      return fileNameWithExtension;
   }
   
   /**
    * resets the form
    */
   public void reset() {
      name = null;
      title = null;
      line2 = null;
      line3 = null;
      line4 = null;
      imageUrl = null;
      attribution = null;
      newAttribution = false;
      jaraUrl = null;
      fileXml = null;
      
      publishedService = false;
      showOverrideDialog = false;
      
      fields.clear();
      availableActionsIndexes.clear();
      availableActionsIndexes.add(0);
      selectedActions.clear();
      selectedActions.add(new RAActionVO());
      
      onSelectTable();
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
    * @return the line2
    */
   public String getLine2() {
      return line2;
   }

   /**
    * @param line2 the line2 to set
    */
   public void setLine2(String line2) {
      this.line2 = line2;
   }

   /**
    * @return the line3
    */
   public String getLine3() {
      return line3;
   }

   /**
    * @param line3 the line3 to set
    */
   public void setLine3(String line3) {
      this.line3 = line3;
   }

   /**
    * @return the line4
    */
   public String getLine4() {
      return line4;
   }

   /**
    * @param line4 the line4 to set
    */
   public void setLine4(String line4) {
      this.line4 = line4;
   }

   /**
    * @return the imageUrl
    */
   public String getImageUrl() {
      return imageUrl;
   }

   /**
    * @param imageUrl the imageUrl to set
    */
   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   /**
    * @return the attribution
    */
   public String getAttribution() {
      return attribution;
   }

   /**
    * @param attribution the attribution to set
    */
   public void setAttribution(String attribution) {
      this.attribution = attribution;
   }

   /**
    * @return the fields
    */
   public List<String> getFields() {
      return fields;
   }

   /**
    * @param fields the fields to set
    */
   public void setFields(List<String> fields) {
      this.fields = fields;
   }

   /**
    * @return the newAttribution
    */
   public boolean isNewAttribution() {
      return newAttribution;
   }

   /**
    * @param newAttribution the newAttribution to set
    */
   public void setNewAttribution(boolean newAttribution) {
      this.newAttribution = newAttribution;
   }

   /**
    * @return the selectedActions
    */
   public List<RAActionVO> getSelectedActions() {
      return selectedActions;
   }

   /**
    * @param selectedActions the selectedActions to set
    */
   public void setSelectedActions(List<RAActionVO> selectedActions) {
      this.selectedActions = selectedActions;
   }

   /**
    * @return the availableActionsIndexes
    */
   public List<Integer> getAvailableActionsIndexes() {
      return availableActionsIndexes;
   }

   /**
    * @param availableActionsIndexes the availableActionsIndexes to set
    */
   public void setAvailableActionsIndexes(List<Integer> availableActionsIndexes) {
      this.availableActionsIndexes = availableActionsIndexes;
   }

   /**
    * @return the publishedService
    */
   public boolean isPublishedService() {
      return publishedService;
   }

   /**
    * @param publishedService the publishedService to set
    */
   public void setPublishedService(boolean publishedService) {
      this.publishedService = publishedService;
   }

   /**
    * @return the jaraUrl
    */
   public String getJaraUrl() {
      return jaraUrl;
   }

   /**
    * @param jaraUrl the jaraUrl to set
    */
   public void setJaraUrl(String jaraUrl) {
      this.jaraUrl = jaraUrl;
   }

   /**
    * @return the showOverrideDialog
    */
   public boolean isShowOverrideDialog() {
      return showOverrideDialog;
   }

   /**
    * @param showOverrideDialog the showOverrideDialog to set
    */
   public void setShowOverrideDialog(boolean showOverrideDialog) {
      this.showOverrideDialog = showOverrideDialog;
   }

   /**
    * @return the fileXml
    */
   public String getFileXml() {
      return fileXml;
   }

   /**
    * @param fileXml the fileXml to set
    */
   public void setFileXml(String fileXml) {
      this.fileXml = fileXml;
   }
}
