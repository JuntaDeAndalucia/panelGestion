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

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.RemoteFileReader;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;

public class RemoteShapeReader implements ShapeReader {
   
   private FeatureIterator<SimpleFeature> iterator;
   
   private long shapefileSize;
   
   private int numFeatures;
   
   private SimpleFeatureType featureType;
   
   private CoordinateReferenceSystem epsg;
   
   public RemoteShapeReader(String userEPSG, Source source) throws IOException, IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException {
      String sourceName = source.getName();
      String repositoryUrlStr = source.getUrl();
      String sourceUrlStr = repositoryUrlStr + sourceName;
      String repositoryUser = source.getUser();
      String repositoryPassword = source.getPassword();

      // gets size
      URL sourceUrl = URI.create(sourceUrlStr).toURL();
      GetMethod getMethod = RemoteFileReader.httpGet(sourceUrlStr, repositoryUser, repositoryPassword);
      shapefileSize = getMethod.getResponseContentLength();
      
      // reads the features
      Map<String, Serializable> map = new HashMap<String, Serializable>();
      map.put(ShapefileDataStoreFactory.URLP.key, sourceUrl);
      map.put(ShapefileDataStoreFactory.USER.key, repositoryUser);
      map.put(ShapefileDataStoreFactory.PASSWORD.key, repositoryPassword);
      map.put(ShapefileDataStoreFactory.COMPRESSED.key, Utils.isCompressedFile(sourceUrl, repositoryUser, repositoryPassword));
      DataStore dataStore = DataStoreFinder.getDataStore(map);
      SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
      numFeatures = featureSource.getCount(Query.ALL);
      featureType = featureSource.getSchema();
      SimpleFeatureCollection features = featureSource.getFeatures();

      // gets the EPSG
      epsg = Utils.getCRS(new ShpFiles(sourceUrl, repositoryUser, repositoryPassword), userEPSG);
      
      // gets iterator
      iterator = features.features();
   }
   
   @Override
   public int getNumFeatures() {
      return numFeatures;
   }

   @Override
   public long getShapefileSize() {
      return shapefileSize;
   }

   @Override
   public SimpleFeatureType getFeatureType() {
      return featureType;
   }

   @Override
   public SimpleFeature getNext() {
      SimpleFeature nextFeature = null;
      if (iterator.hasNext()) {
         nextFeature = iterator.next();
      }
      return nextFeature;
   }

   @Override
   public void close() throws IOException {
      iterator.close();
   }

   @Override
   public CoordinateReferenceSystem getEpsg() {
      return epsg;
   }
}
