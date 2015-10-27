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
package es.juntadeandalucia.panelGestion.persistencia.utiles;

import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

public class TableUtils {

   public static String getIdColumnName(Table table) {
      String idColumnName;
      if (table != null) {
         String tableName = table.getName();
         idColumnName = "x_" + tableName.toLowerCase();
      }
      else {
         idColumnName = null;
      }
      return idColumnName;
   }
   
   public static String getSequenceName(Table table) {
      String sequenceName;
      if (table != null) {
         String tableName = table.getName();
         String idColumnName = getIdColumnName(table);
         sequenceName = tableName.toLowerCase();
         sequenceName += "_";
         sequenceName += idColumnName;
         sequenceName += "_seq";
      }
      else {
         sequenceName = null;
      }
      return sequenceName;
   }
   
   public static String getBackupSchemaTable(Table table) {
      String backupName = table.getSchema().getName().concat(".");
      backupName = backupName.concat("\"")
         .concat(getBackupTable(table)).concat("\"");
      return backupName;
   }
   
   public static String getBackupTable(Table table) {
      return table.getName().concat("_backup");
   }
   
   public static String getPKeyConstraint(Table table) {
      String pkeyConstraint;
      if (table != null) {
         String tableName = table.getName();
         pkeyConstraint = tableName.toLowerCase() + "_pkey";
      }
      else {
         pkeyConstraint = null;
      }
      return pkeyConstraint;
   }
   
   public static String getSchemaTable(Table table) {
      String schemaTableName = table.getSchema().getName().concat(".");
      schemaTableName = schemaTableName.concat("\"")
         .concat(table.getName()).concat("\"");
      return schemaTableName;
   }
   
   public static String getSchemaTableDataImport(Table table) {
      String schemaTableName = table.getSchema().getName().concat(".");
      schemaTableName = schemaTableName.concat(table.getName());
      return schemaTableName;
   }
}
