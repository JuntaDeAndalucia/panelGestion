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
package es.juntadeandalucia.panelGestion.negocio.utiles.file.shape;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.AbstractFileProcessor;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;

public final class ShapeFileProcessor extends AbstractFileProcessor implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -2003117920952286268L;

   private List<ColumnVO> columns;
   
   private ShapeReader shapeReader;
   
   
   public ShapeFileProcessor(String epsg, Source source, String taskTicket, File localSourceData) throws IOException, IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException {
      if (source.isRemote()) {
         shapeReader = new RemoteShapeReader(epsg, source);
      }
      else {
         shapeReader = new LocalShapeReader(epsg, taskTicket, localSourceData);
      }
      numEntries = shapeReader.getNumFeatures();
      bytesLength = shapeReader.getShapefileSize();
   }
   
   @Override
   public List<ColumnVO> readColumns() throws IOException {
      columns = new LinkedList<ColumnVO>();
      
      SimpleFeatureType featureType = shapeReader.getFeatureType();
      for (int i = 0; i < featureType.getAttributeCount(); i++) {
         ColumnVO column = new ColumnVO();
         AttributeType at = featureType.getType(i);
         String strColumn;
         Class<?> typeClass = at.getBinding();
         if (Geometry.class.isAssignableFrom(typeClass)
            || com.vividsolutions.jts.geom.Geometry.class.isAssignableFrom(typeClass)) {
            strColumn = "the_geom";
            column.setType("geometry");
            column.setGeometryType(at.getName().getLocalPart());
            column.setInTable(true);
         }
         else {
            strColumn = at.getName().getLocalPart();
         }
         column.setFilePosition(i);
         column.setText(strColumn);
         column.setNameOnTable(Utils.removeSpecialChars(strColumn));
         column.setTypeClass(typeClass);
         columns.add(column);
      }
      
      return columns;
   }

   @Override
   public SimpleFeature nextShapeEntry() throws IOException {
      SimpleFeature feature = shapeReader.getNext();
      numCurrentEntry++;
      return feature;
   }
   
   @Override
   public void end() throws IOException {
      if (columns != null) {
         columns.clear();
         columns = null;
      }
      if (shapeReader != null) {
         shapeReader.close();
         shapeReader = null;
      }
   }

   public CoordinateReferenceSystem getCRS() {
      return shapeReader.getEpsg();
   }
}
