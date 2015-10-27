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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
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

public class LocalShapeReader implements ShapeReader {
   
   private File shapefilesFolder;
   
   private URI shapefilePath;
   
   private FeatureIterator<SimpleFeature> iterator;
   
   private long shapefileSize;
   
   private int numFeatures;
   
   private SimpleFeatureType featureType;
   
   private CoordinateReferenceSystem epsg;
   
   public LocalShapeReader(String userEPSG, String taskTicket, File shapefileData) throws IOException, NoSuchAuthorityCodeException, IllegalArgumentException, FactoryException {
      shapefileSize = shapefileData.length();
      
      // the folder where the shapefiles will locate in
      shapefilesFolder = new File(FileUtils.getTempDirectory(), taskTicket);

      // creates the shapefiles into the folder
      unzipShapefiles(shapefileData);
      
      // reads the features
      URL shapeFileUrl = shapefilePath.toURL();
      Map<String, Serializable> map = new HashMap<String, Serializable>();
      map.put(ShapefileDataStoreFactory.URLP.key, shapeFileUrl);
      map.put(ShapefileDataStoreFactory.COMPRESSED.key, true);
      DataStore dataStore = DataStoreFinder.getDataStore(map);
      SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
      featureType = featureSource.getSchema();
      numFeatures = featureSource.getCount(Query.ALL);
      SimpleFeatureCollection features = featureSource.getFeatures();
      
      // gets the EPSG
      epsg = Utils.getCRS(new ShpFiles(shapeFileUrl), userEPSG);
      
      // gets iterator
      iterator = features.features();
   }
   
   private void unzipShapefiles(File shapefileCompressedData) throws IOException {
      IOException error = null;
      if (!shapefilesFolder.exists()) {
         shapefilesFolder.mkdirs();
      }
      InputStream bis = new FileInputStream(shapefileCompressedData);
      ZipInputStream zis = new ZipInputStream(bis);
      ZipEntry ze;
      try {
         while ((ze = zis.getNextEntry()) != null) {
            if (!ze.isDirectory()) {
               String fileName = ze.getName();
               File unzippedFile = new File(shapefilesFolder, fileName);
               byte[] buffer = new byte[1024];
               FileOutputStream fos = new FileOutputStream(unzippedFile);
               int len;
               while ((len = zis.read(buffer)) > 0) {
                  fos.write(buffer, 0, len);
               }
               fos.close();
               if (fileName.toLowerCase().endsWith(".shp")) {
                  shapefilePath = unzippedFile.toURI();
               }
            }
         }
      }
      catch (IOException e) {
         error = e;
      }
      finally {
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         try {
            zis.close();
         }
         catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      if (error != null) {
         throw error;
      }
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
      FileUtils.deleteDirectory(shapefilesFolder);
   }

   /* (non-Javadoc)
    * @see es.juntadeandalucia.panelGestion.negocio.utiles.file.shape.ShapeReader#getEpsg()
    */
   @Override
   public CoordinateReferenceSystem getEpsg() {
      return epsg;
   }
}
