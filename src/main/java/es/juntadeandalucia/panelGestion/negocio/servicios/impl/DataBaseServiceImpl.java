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

import es.juntadeandalucia.panelGestion.negocio.servicios.DataBaseService;
import es.juntadeandalucia.panelGestion.negocio.servicios.SchemaService;
import es.juntadeandalucia.panelGestion.persistencia.dao.DataBaseDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;

@AutoCreate
@Name("dataBaseService")
@Scope(ScopeType.EVENT)
public class DataBaseServiceImpl implements DataBaseService, Serializable {
   
   /**
    * Generated version UID
    */
   private static final long serialVersionUID = 6645730063885963166L;
   
   @In
   private DataBaseDAO dataBaseDao;
   
   @In
   private SchemaService schemaService;
   
   public DataBaseServiceImpl() { }
   
   @Override
   public List<DataBase> getDataBases() {
      List<DataBase> dataBases = new LinkedList<DataBase>();
      
      dataBases = dataBaseDao.findAll();
      
      return dataBases;
   }

   @Override
   public DataBase getDataBase(long dataBaseId) {
      DataBase dataBase;
      
      dataBase = dataBaseDao.findById(dataBaseId);
      
      return dataBase;
   }

   @Override
   public void save(DataBase dataBase) throws Exception {
      if (dataBase.getId() == 0) {
         dataBaseDao.create(dataBase);
      }
      else {
         dataBaseDao.update(dataBase);
      }
   }

   @Override
   public void delete(DataBase dataBase) throws Exception {
      List<Schema> schemas = dataBase.getSchemas();
      if (schemas != null) {
         for (Schema schema : dataBase.getSchemas()) {
            schemaService.delete(schema);
         }
      }
      dataBaseDao.delete(dataBase);
   }
}
