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
import java.util.Date;
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

import es.juntadeandalucia.panelGestion.negocio.servicios.DataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.RemoteDataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.SchemaService;
import es.juntadeandalucia.panelGestion.negocio.servicios.impl.RemoteDataBaseServiceImpl;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.DataBaseVO;
import es.juntadeandalucia.panelGestion.negocio.vo.SchemaVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("configurationCtrl")
public class ConfigurationController implements Serializable {
   
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -2666924509130933091L;

   private static Logger log = Logger.getLogger(ConfigurationController.class);
   
   @In
   private DataBaseService dataBaseService;
   
   @In
   private SchemaService schemaService;
   
   
   
   private DataBaseVO dataBaseVo;
   private DataBase dataBase;
   private SchemaVO schema;
   private DataBase dataBaseToDelete;
   private SchemaVO schemaToDelete;
   private List<DataBase> dataBases;
   private List<SchemaVO> schemas;
   private List<String> databaseTypes;
      
   public ConfigurationController() {
      this.dataBases = new LinkedList<DataBase>();
      this.schemas = new LinkedList<SchemaVO>();
   }
   
   public void newDataBase() {
      this.setDataBase(new DataBase());
      newDataBaseVo();
      databaseTypes =  PanelSettings.tiposBaseDatos;
   }
   
   public void newDataBaseVo() {
	   this.setDataBaseVo(new DataBaseVO());
   }
   public void newSchema() {
      this.setSchema(new SchemaVO());
   }
   
   public void closeDialog() {
      this.setDataBase(null);
      this.setDataBaseToDelete(null);
      this.setSchema(null);
      this.setSchemaToDelete(null);
   }
   

   
   public void saveDataBase() {
	      
	      try {
	    	  makeDataBaseToDataBaseVo(getDataBaseVo());
	         // checks if it has a valid alias
	    	  
	         String dataBaseAlias = getDataBase().getAlias();
	         if (StringUtils.isEmpty(dataBaseAlias)) {
	            throw new IllegalArgumentException("Alias vacío");
	         }
	         else if (!Utils.isValidName(dataBaseAlias)) {
	            throw new IllegalArgumentException("Alias inválido: " + dataBaseAlias);
	         } if (getDataBase().getConnectionUrl().equals("error")) {
	        	 throw new IllegalArgumentException("Error al construir la cadena de conexión");	        	 
	         }
	         
	         // saves the data base
	         this.dataBaseService.save(getDataBase());
	         
	         String infoMessage = "La base de datos '" + getDataBase().getAlias()
	            + "' ha sido guardada correctamente";
	         StatusMessages.instance().add(Severity.INFO, infoMessage);
	         log.info(infoMessage);
	      }
	      catch (Exception e) {
	         String errorMessage = "Se ha producido un error al intentar guardar la base de datos '"
	            + getDataBase().getAlias() + "'. " + e.getLocalizedMessage();
	         StatusMessages.instance().add(Severity.ERROR, errorMessage);
	         log.error(errorMessage);
	      }
	      
	      this.closeDialog();
	   }
   
  /**
  * Metodo que transforma una entidad databaseVo en database para persistirla 
  * @param dataBaseVo2
  */
private void makeDataBaseToDataBaseVo(DataBaseVO dataBaseVo2) {
	DataBase db = null;
	if ( dataBaseVo2.getId() > 0) {//Base de datos existente
		db = dataBaseService.getDataBase(dataBaseVo2.getId());
	} else {//Nueva base de datos
		db = getDataBase();
		//db.setAlias(dataBaseVo2.getAlias());
		db.setCreationDate(new Date());		
	}
	db.setAlias(dataBaseVo2.getAlias());
	db.setConnectionUrl(makeURLconnection(dataBaseVo2));
	
}

public void makeDataBaseVoToDataBase (DataBase db) {
	DataBaseVO dbvo = new DataBaseVO();
	dbvo.setAlias(db.getAlias());
	dbvo.setId(db.getId());
	dbvo.setDateCreation(db.getCreationDate());
	String [] parametersConnection = descomposeURLtoParameterConnection(db.getConnectionUrl());
	dbvo.setBaseDatos(parametersConnection[3]);
	dbvo.setHost(parametersConnection[1]);
	dbvo.setPuerto(parametersConnection[2]);
	dbvo.setTipoBaseDatos(parametersConnection[0]);
	setDataBaseVo(dbvo);
	setDataBase(db);
}

private String makeURLconnection(DataBaseVO dbVo){
	String res = "";
	res = res.concat("jdbc:" + dbVo.getTipoBaseDatos() + "://" +dbVo.getHost() + ":" + dbVo.getPuerto() + "/" + dbVo.getBaseDatos());
	try {
		descomposeURLtoParameterConnection(res);
	} catch (Exception oops) {
		log.info("Error en la cadena de conexion");
		res = "error";
	}
	return res;
}

private String[] descomposeURLtoParameterConnection(String url){
	 //jdbc:postgresql://hefesto.guadaltel.es:5432/bdcallejero
	String res = "";
	String tipoBD = url.split("://")[0].replace("jdbc:", "");
	String aux2 = url.split("://")[1];
	String host = aux2.split(":")[0];
	aux2 = aux2.split(":")[1];
	String port = aux2.split("/")[0];
	String baseDatos = aux2.split("/")[1];
	res = res.concat(tipoBD).concat(";").concat(host).concat(";").concat(port).concat(";").concat(baseDatos);
	return 	res.split(";");
} 

public void saveSchema() {
      SchemaVO schemaVO = getSchema();
      try {
         Schema schema = schemaVO.getSchemaEntity();
         // checks if the schema has a valid name
         String schemaName = schema.getName();
         if (StringUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("Nombre del esquema vacío");
         }
         else if (!Utils.isValidName(schemaName)) {
            throw new IllegalArgumentException("Nombre del esquema inválido: " + schemaName);
         }
         
         // checks if the user has a valid name
         String schemaUser = schema.getUser();
         if (StringUtils.isEmpty(schemaUser)) {
            throw new IllegalArgumentException("Usuario del esquema vacío");
         }
         else if (!Utils.isValidName(schemaUser)) {
            throw new IllegalArgumentException("Nombre del usuario inválido: " + schemaUser);
         }
         
         // checks if the user is editing or creating the schema
         long schemaId = schema.getId();
         if (schemaId != 0) {
//            JDBCConnector connector = new JDBCConnector(schema);
//            RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
            
            // gets previous values
//            if (rdbs.updateSchema(schemaVO)) {
//               // saves the schema into the data model
//               this.schemaService.save(schemaVO);
//               
//               String infoMessage = "El esquema '" + schemaName + "' ha sido guardado correctamente";
//               StatusMessages.instance().add(Severity.INFO, infoMessage);
//               log.info(infoMessage);
//            }
//            
            this.schemaService.save(schemaVO);
            
            String infoMessage = "El esquema '" + schemaName + "' ha sido guardado correctamente";
            StatusMessages.instance().add(Severity.INFO, infoMessage);
            log.info(infoMessage);
 
         }
         else {
            // checks if the user wants to create the schema
            boolean schemaCreated = true;
            if (schemaVO.isCreate()) {
               JDBCConnector connector = new JDBCConnector(schema);
               RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
               
               schemaCreated = rdbs.createSchema(schema);
            }
            if (schemaCreated) {
               // saves the schema into the data model
               this.schemaService.save(schemaVO);
               
               String infoMessage = "El esquema '" + schemaName + "' ha sido guardado correctamente";
               StatusMessages.instance().add(Severity.INFO, infoMessage);
               log.info(infoMessage);
            }
            else {
               String errorMessage = "Se ha producido un error al intentar crear el esquema '"
                  + schemaName;
               StatusMessages.instance().add(Severity.ERROR, errorMessage);
               log.error(errorMessage);
            }
         }
      }
      catch (Exception e) {
         String errorMessage = "Se ha producido un error al intentar guardar el esquema '"
            + schemaVO.getSchemaEntity().getName() + "'. " + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
      
      this.closeDialog();
   }
   
   public void deleteDataBase() {
      DataBase dataBase = getDataBaseToDelete();

      if (!dataBase.getSchemas().isEmpty()) {
         String errorMessage = "Debe eliminar los esquemas asociados a la base de datos '"
            + dataBase.getAlias() + "'";
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
      else {
         try {
            this.dataBaseService.delete(getDataBaseToDelete());

            String infoMessage = "La base de datos '" + getDataBaseToDelete().getAlias()
               + "' ha sido eliminado correctamente";
            StatusMessages.instance().add(Severity.INFO, infoMessage);
            log.info(infoMessage);
         }
         catch (Exception e) {
            String errorMessage = "Se ha producido un error al intentar eliminar la base de datos '"
               + getDataBaseToDelete().getAlias() + "'. " + e.getLocalizedMessage();
            StatusMessages.instance().add(Severity.ERROR, errorMessage);
            log.error(errorMessage);
         }
      }

      this.closeDialog();
   }
   
   public void deleteSchema() {
      SchemaVO schemaVO = getSchemaToDelete();
      Schema schema = schemaVO.getSchemaEntity();
      
      try {
         // deletes the schema
         /*JDBCConnector connector = new JDBCConnector(schema);
         RemoteDataBaseService rdbs = new RemoteDataBaseServiceImpl(connector);
         rdbs.deleteSchema(schema);*/
         
         // deletes the schema into the data model
         this.schemaService.delete(schema);

         String infoMessage = "El esquema '" + schema.getName()
            + "' ha sido eliminado correctamente";
         StatusMessages.instance().add(Severity.INFO, infoMessage);
         log.info(infoMessage);
      }
      catch (Exception e) {
         String errorMessage = "Se ha producido un error al intentar eliminar el esquema '"
            + schema.getName() + "'. " + e.getLocalizedMessage();
         StatusMessages.instance().add(Severity.ERROR, errorMessage);
         log.error(errorMessage);
      }
      
      this.closeDialog();
   }

   /**
    * @return the dataBase
    */
   public DataBase getDataBase() {
	   databaseTypes =  PanelSettings.tiposBaseDatos;
      return dataBase;
      

   }

   /**
    * @param dataBase the dataBase to set
    */
   public void setDataBase(DataBase dataBase) {
      this.dataBase = dataBase;

   }

   /**
    * @return the schema
    */
   public SchemaVO getSchema() {
      return schema;
   }

   /**
    * @param schema the schema to set
    */
   public void setSchema(SchemaVO schema) {
      this.schema = schema;
   }

   /**
    * @return the dataBases
    */
   public List<DataBase> getDataBases() {
      this.dataBases = dataBaseService.getDataBases();
      return dataBases;
   }

   /**
    * @param dataBases the dataBases to set
    */
   public void setDataBases(List<DataBase> dataBases) {
      this.dataBases = dataBases;
   }

   /**
    * @return the schemas
    */
   public List<SchemaVO> getSchemas() {
      this.schemas = schemaService.getAllSchemas();
      return schemas;
   }

   /**
    * @param schemas the schemas to set
    */
   public void setSchemas(List<SchemaVO> schemas) {
      this.schemas = schemas;
   }

   /**
    * @return the dataBaseToDelete
    */
   public DataBase getDataBaseToDelete() {
      return dataBaseToDelete;
   }

   /**
    * @param dataBaseToDelete the dataBaseToDelete to set
    */
   public void setDataBaseToDelete(DataBase dataBaseToDelete) {
      this.dataBaseToDelete = dataBaseToDelete;
   }

   /**
    * @return the schemaToDelete
    */
   public SchemaVO getSchemaToDelete() {
      return schemaToDelete;
   }

   /**
    * @param schemaToDelete the schemaToDelete to set
    */
   public void setSchemaToDelete(SchemaVO schemaToDelete) {
      this.schemaToDelete = schemaToDelete;
   }

	public DataBaseVO getDataBaseVo() {
		return dataBaseVo;
	}
	
	public void setDataBaseVo(DataBaseVO dataBaseVo) {
		this.dataBaseVo = dataBaseVo;
	}

	public List<String> getDatabaseTypes() {
		return databaseTypes;
	}

	public void setDatabaseTypes(List<String> databaseTypes) {
		this.databaseTypes = databaseTypes;
	}

}
