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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import es.juntadeandalucia.panelGestion.negocio.servicios.SchemaService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.negocio.vo.SchemaVO;
import es.juntadeandalucia.panelGestion.persistencia.dao.SchemaDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.DataBase;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

@AutoCreate
@Name("schemaService")
@Scope(ScopeType.EVENT)
public class SchemaServiceImpl implements SchemaService, Serializable {

   @In
   private SchemaDAO schemaDao;
   
   @In
   private TableService tableService;
   
   /**
    * Generated version UID
    */
   private static final long serialVersionUID = 3328449338765119398L;

   @Override
   public List<Schema> getSchemasFromDB(DataBase dataBase) {
      if (dataBase == null) {
         throw new IllegalArgumentException("La base de datos no puede ser nula");
      }
      
      List<Schema> schemas = new LinkedList<Schema>();
      
      long dataBaseId = dataBase.getId();
      schemas = schemaDao.findByDataBase(dataBaseId);
      
      return schemas;
   }

   @Override
   public Schema getSchema(long schemaId) {
      Schema schema;
      
      schema = schemaDao.findById(schemaId);
      
      return schema;
   }
   
   @Override
   public boolean create(Schema schema) throws Exception {
      // creationDate
      schema.setCreationDate(new Date());
      
      return schemaDao.create(schema);
   }

   @Override
   public List<SchemaVO> getAllSchemas() {
      List<SchemaVO> schemas = new LinkedList<SchemaVO>();
      
      List<Schema> schemaEntities = schemaDao.findAll();
      for (Schema schemaEntity : schemaEntities) {
         SchemaVO schema = new SchemaVO();
         schema.setSchemaEntity(schemaEntity);
         schemas.add(schema);
      }
      
      return schemas;
   }

   @Override
   public void save(SchemaVO schema) throws Exception {
      Schema schemaEntity = schema.getSchemaEntity();
      if (schemaEntity.getId() == 0) {
         schemaDao.create(schemaEntity);
      }
      else {
         schemaDao.update(schemaEntity);
      }
   }

   @Override
   public void delete(Schema schema) throws Exception {
      // deletes its tables
      List<Table> tables = schema.getTables();
      if (tables != null) {
         for (Table table : schema.getTables()) {
            tableService.delete(table);
         }
      }
      schemaDao.delete(schema);
   }
}
