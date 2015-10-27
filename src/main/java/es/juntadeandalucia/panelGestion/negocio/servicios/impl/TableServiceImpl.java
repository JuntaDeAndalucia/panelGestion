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

import es.juntadeandalucia.panelGestion.negocio.servicios.TableService;
import es.juntadeandalucia.panelGestion.negocio.servicios.TaskService;
import es.juntadeandalucia.panelGestion.negocio.utiles.JDBCConnector;
import es.juntadeandalucia.panelGestion.persistencia.dao.TableDAO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Schema;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Task;

@AutoCreate
@Name("tableService")
@Scope(ScopeType.EVENT)
public class TableServiceImpl implements TableService, Serializable {

   @In
   private TaskService taskService;
   
   @In
   private TableDAO tableDao;
   
   /**
    * Generated version UID
    */
   private static final long serialVersionUID = 8264720999426930997L;

   @Override
   public Table getTable(long tableId) {
      Table table;
      
      table = tableDao.findById(tableId);
      loadTasks(table);
      
      return table;
   }

   @Override
   public List<Table> getTablesFromSchema(Schema schema) {
      List<Table> tables = new LinkedList<Table>();      
         try {
        	 updateTableBySchema(schema);
        	 tables = tableDao.findBySchema(schema.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
         for (Table table : tables) {
            loadTasks(table);
         }
      
      
      return tables;
   }

   private void loadTasks(Table table) {
      List<Task> tasks = taskService.getTasksFromTable(table);
      table.setTasks(tasks);
   }

   @Override
   public boolean create(Table table) throws Exception {
      // creation date
      table.setCreationDate(new Date());
      
      return tableDao.create(table);
   }

   @Override
   public void update(Table table) throws Exception {
      tableDao.update(table);
   }

   @Override
   public void delete(Table table) throws Exception {
      // removes related tasks
      List<Task> relatedTasks = table.getTasks();
      for (Task task : relatedTasks) {
         taskService.delete(task);
      }
      
      if (table.getTableXservices().size() > 0) {
         throw new IllegalArgumentException(
            "No puede eliminarse la tabla ya que existen servicios que depende de ésta");
      }
      tableDao.delete(table);
   }


	@Override
	public void updateTableBySchema(Schema schema) throws Exception {
		
		List<Table> oldTables = tableDao.findBySchema(schema.getId());
		List<Table> tables = new LinkedList<Table>();
	    JDBCConnector jdbcConector = new JDBCConnector(schema);
	    try {
			tables = jdbcConector.tablesByShema(schema);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    for (Table tn: tables) {
	    	boolean isPersist = false;
			for (Table to: oldTables) { 
				if (to.getName().equals(tn.getName())) {
					isPersist = true;
					break;
				}
			}
			if (!isPersist) {
				create(tn);
			}
	    }
		
	}
}