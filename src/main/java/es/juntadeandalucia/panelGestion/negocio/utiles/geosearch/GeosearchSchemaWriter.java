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
package es.juntadeandalucia.panelGestion.negocio.utiles.geosearch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.juntadeandalucia.panelGestion.exception.GeosearchException;
import es.juntadeandalucia.panelGestion.negocio.utiles.PanelSettings;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchTableVO;
import es.juntadeandalucia.panelGestion.negocio.vo.XMLFileVO;

public class GeosearchSchemaWriter extends GeosearchFileWriter {
   
   public GeosearchSchemaWriter(List<GeosearchTableVO> tables, String core) throws GeosearchException, ParserConfigurationException {
      super(tables, core);
   }

   public XMLFileVO write() throws XPathExpressionException {
      addFields();
      addCopyFields();
      
      XMLFileVO schema = new XMLFileVO();
      schema.setName(PanelSettings.geosearchMaster.getFiles().getSchema().getFileName());
      schema.setXml(schemaXML);
      
      return schema;
   }
   
   private void addFields() throws XPathExpressionException {
      Node fields = getFieldsNode();
      Document fieldsDocument = fields.getOwnerDocument();

      // set control to avoid duplicated
      Set<String> addedFields = new HashSet<String>();
      
      boolean firstTable = true;
      for (GeosearchTableVO table : tables) {
         // gets table columns
         List<GeosearchFieldVO> tableFields = table.getFields();
         String tableName = table.getTable().getName();

         boolean firstField = true;
         for (GeosearchFieldVO field : tableFields) {
            String fieldName = Utils.getFieldName(field);
            if (field.isDefined() && !field.isInSchema()
               && !addedFields.contains(fieldName)) {
               String typeField = field.getType();
               String indexed = String.valueOf(field.isIndexed());
               String stored = String.valueOf(field.isStored());
               String multiValued = String.valueOf(field.isMultivaluated());

               // field tag
               Element fieldElement = fieldsDocument.createElement("field");
               // attribute 'name1'
               fieldElement.setAttribute("name", fieldName);
               // attribute 'type'
               fieldElement.setAttribute("type", typeField);
               // attribute 'indexed'
               fieldElement.setAttribute("indexed", indexed);
               // attribute 'stored'
               fieldElement.setAttribute("stored", stored);
               // attribute 'multivalued'
               fieldElement.setAttribute("multiValued", multiValued);

               fields.appendChild(fieldElement);

               if (firstTable) {
                  // inserts comment
                  insertDateComment(fields, fieldElement);
                  firstTable = false;
               }
               if (firstField) {
                  // insert table comment
                  insertTableNameComment(fields, tableName, fieldElement);
                  firstField = false;
               }
               
               addedFields.add(fieldName);
            }
         }
      }
   }
   
   private void addCopyFields() throws XPathExpressionException {
      Node schema = getSchemaRootNode();
      Node lastCopyField = getLastCopyField();
      
      Document schemaDocument = schema.getOwnerDocument();

      // set control to avoid duplicated
      Set<String> addedFields = new HashSet<String>();

      boolean firstTable = true;
      for (GeosearchTableVO table : tables) {
         // gets table columns
         List<GeosearchFieldVO> fields = table.getFields();
         String tableName = table.getTable().getName();
         
         boolean firstField = true;
         for (GeosearchFieldVO field : fields) {
            String fieldName = Utils.getFieldName(field);
            if (field.isCopyToText() && !field.isInSchema()
               && !addedFields.contains(fieldName)) {
               // copyField tag
               Element copyField = schemaDocument.createElement("copyField");
               // attribute 'source'
               copyField.setAttribute("source", fieldName);
               // attribute 'dest'
               copyField.setAttribute("dest", PanelSettings.geosearchMaster.getFiles().getSchema().getDefaultSearchField());
               
               if (lastCopyField != null) {
                  schema.insertBefore(copyField, lastCopyField);
               }
               else {
                  schema.appendChild(copyField);
               }
               
               if (firstTable) {
                  // inserts comment
                  insertDateComment(schema, copyField);
                  firstTable = false;
               }
               if (firstField) {
                  // insert table comment
                  insertTableNameComment(schema, tableName, copyField);
                  firstField = false;
               }
               addedFields.add(fieldName);
            }
         }
      }
   }
   
   private Node getFieldsNode() throws XPathExpressionException {
      Node fields = null;
      
      fields = (Node) xpath.compile("/schema/fields").evaluate(schemaXML, XPathConstants.NODE);
      
      return fields;
   }
   
   private Node getSchemaRootNode() throws XPathExpressionException {
      Node rootNode = null;
      
      rootNode = (Node) xpath.compile("/schema").evaluate(schemaXML, XPathConstants.NODE);
      
      return rootNode;
   }
   
   private Node getLastCopyField() throws XPathExpressionException {
      Node lastCopyField = null;
      
      lastCopyField = (Node) xpath.compile("(/schema/copyField)[last()]").evaluate(schemaXML, XPathConstants.NODE);
      
      return lastCopyField;
   }
}
