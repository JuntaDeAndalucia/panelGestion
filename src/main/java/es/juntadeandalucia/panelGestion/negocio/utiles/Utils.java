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
package es.juntadeandalucia.panelGestion.negocio.utiles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.crs.ReprojectFeatureResults;
import org.geotools.data.shapefile.ShpFileType;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.Hints;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;

import es.juntadeandalucia.panelGestion.negocio.utiles.file.LocalFileReader;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.RemoteFileReader;
import es.juntadeandalucia.panelGestion.negocio.vo.GeosearchFieldVO;
import es.juntadeandalucia.panelGestion.negocio.vo.GeoserverVO;
import es.juntadeandalucia.panelGestion.negocio.vo.XMLFileVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.FileType;

public final class Utils {

   static {
      /*
       * setup the referencing tolerance to make it more tolerant to tiny differences between
       * projections (increases the chance of matching a random prj file content to an actual EPSG
       * code
       */
      Hints.putSystemDefault(Hints.COMPARISON_TOLERANCE, 1e-9);
   }

   public static String removeSpecialChars(String strColumn) {
      String strNoSpecialChars = strColumn;
      if (strNoSpecialChars != null) {
         strNoSpecialChars = strNoSpecialChars.toLowerCase();
         // removes white spaces
         strNoSpecialChars = strNoSpecialChars.replaceAll("\\s", "_");

         // removes accents
         strNoSpecialChars = Normalizer.normalize(strNoSpecialChars, Normalizer.Form.NFD);
         strNoSpecialChars = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            .matcher(strNoSpecialChars).replaceAll("");
      }
      return strNoSpecialChars;
   }

   public static FileType getTypeFromContentType(String contentType) {
      FileType type = FileType.UNKNOW;

      if (!StringUtils.isEmpty(contentType)) {
         String contentTypeLw = contentType.toLowerCase();

         if (PanelSettings.csvContentTypes.contains(contentTypeLw)
            && (PanelSettings.shapeContentTypes.contains(contentTypeLw) || PanelSettings.compressedContentTypes
               .contains(contentTypeLw))) {
            type = FileType.UNKNOW;
         }
         else if (PanelSettings.csvContentTypes.contains(contentTypeLw)) {
            type = FileType.CSV;
         }
         else if (PanelSettings.shapeContentTypes.contains(contentTypeLw)
            || PanelSettings.compressedContentTypes.contains(contentTypeLw)) {
            type = FileType.SHAPEFILE;
         }
      }
      return type;
   }

   public static FileType getTypeFromName(String fileName) {
      FileType type = null;

      if (!StringUtils.isEmpty(fileName)) {
         Pattern csvPattern = Pattern.compile("^.*\\.(csv|txt)$", Pattern.CASE_INSENSITIVE);
         Pattern shapePattern = Pattern.compile("^.*\\.(shp|shape|zip|rar|tar|gz|xz)$",
            Pattern.CASE_INSENSITIVE);
         if (csvPattern.matcher(fileName).matches()) {
            type = FileType.CSV;
         }
         else if (shapePattern.matcher(fileName).matches()) {
            type = FileType.SHAPEFILE;
         }
      }
      return type;
   }

   public static boolean isCompressedFile(String contentType) {
      String contentTypeLw = contentType.toLowerCase();
      return PanelSettings.compressedContentTypes.contains(contentTypeLw);
   }

   public static String getFileNameFromPath(String sourceUrl) {
      String fileName = null;

      if (sourceUrl != null) {
         int pathSlashIdx = sourceUrl.lastIndexOf("/");
         if (pathSlashIdx >= 0) {
            fileName = sourceUrl.substring(pathSlashIdx + 1);
         }
         else {
            fileName = sourceUrl;
         }
      }
      return fileName;
   }

   public static String formatSize(Integer size) {
      String formattedFileSize = "";
      if (size != null) {
         BigDecimal kb = BigDecimal.valueOf(1024);
         BigDecimal mb = BigDecimal.valueOf(1048576);
         BigDecimal fileSizeBD = BigDecimal.valueOf(size);
         if (size > 999999) {
            formattedFileSize = fileSizeBD.divide(mb, 2, RoundingMode.HALF_UP).toPlainString();
            formattedFileSize += " MB";
         }
         else if (size > 999) {
            formattedFileSize = fileSizeBD.divide(kb, 2, RoundingMode.HALF_UP).toPlainString();
            formattedFileSize += " KB";
         }
         else {
            formattedFileSize = fileSizeBD.toPlainString();
            formattedFileSize += " B";
         }
      }
      return formattedFileSize;
   }

   public static String limitLength(String source, int length) {
      String limitedSource = source;
      if ((limitedSource != null) && (limitedSource.length() >= length)) {
         limitedSource = limitedSource.substring(0, length).concat("...");
      }
      return limitedSource;
   }

   public static byte[] getShapeDataFromZip(byte[] localSourceData) {
      byte[] data = null;
      InputStream in = new ByteArrayInputStream(localSourceData);
      ZipInputStream zis = new ZipInputStream(in);
      ZipEntry ze;
      try {
         while ((ze = zis.getNextEntry()) != null) {
            if (!ze.isDirectory()) {
               String fileName = ze.getName().toLowerCase();
               if (fileName.endsWith(".shp")) {
                  long entrySize = ze.getSize();
                  if (entrySize != -1) {
                     data = IOUtils.toByteArray(zis, entrySize);
                     break;
                  }
               }
            }
         }
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
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
      return data;
   }

   public static boolean isCompressedRemoteFile(URL url, String user, String password) {
      boolean compressed = false;

      String urlString = url.toExternalForm();

      GetMethod get;
      try {
         get = RemoteFileReader.httpGet(urlString, user, password);
         String contentType = get.getResponseHeader("Content-Type").getValue();
         compressed = isCompressedFile(contentType);
      }
      catch (HttpException e) {
         // TODO Auto-generated catch block
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
      }

      return compressed;
   }

   public static String getShapefileNameFromCompressed(InputStream is) {
      String shapefileName = null;
      ZipInputStream zis = new ZipInputStream(is);
      ZipEntry ze;
      try {
         while (((ze = zis.getNextEntry()) != null) && (shapefileName == null)) {
            if (!ze.isDirectory()) {
               String fileName = ze.getName().toLowerCase();
               if (fileName.endsWith(".shp")) {
                  shapefileName = fileName;
               }
            }
         }
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
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
      return shapefileName;
   }

   public static InputStream getShapefileFromCompressed(InputStream is, ShpFileType type) {
      InputStream shapefile = null;
      ZipInputStream zis = new ZipInputStream(is);
      ZipEntry ze;
      try {
         while ((ze = zis.getNextEntry()) != null) {
            if (!ze.isDirectory()) {
               String fileName = ze.getName().toLowerCase();
               String baseShapefileName = type.toBase(fileName);
               if (baseShapefileName != null) {
                  shapefile = zis;
                  break;
               }
            }
         }
      }
      catch (IOException e) {
         try {
            zis.closeEntry();
         }
         catch (IOException e2) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         try {
            zis.close();
         }
         catch (IOException e2) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      return shapefile;
   }

   public static boolean isLocalFile(URL url) {
      return url.toExternalForm().toLowerCase().startsWith("file:");
   }

   public static boolean isCompressedFile(URL url, String user, String password) {
      boolean compressed = false;
      if (isLocalFile(url)) {
         // compressed = isCompressedLocalFile(url);
         compressed = false;
      }
      else {
         compressed = isCompressedRemoteFile(url, user, password);
      }
      return compressed;
   }

   public static InputStream getInputStream(URL url, String user, String password)
      throws HttpException, IOException, URISyntaxException {
      InputStream inputStream = null;
      if (isLocalFile(url)) {
         inputStream = LocalFileReader.getInputStream(url);
      }
      else {
         inputStream = RemoteFileReader.getInputStream(url, user, password);
      }
      return inputStream;
   }

   public static void reprojectFeatureCollectionUsingPrjFile(
      SimpleFeatureCollection featureCollection, ShpFiles shapefiles, String epsg)
      throws IllegalArgumentException, NoSuchAuthorityCodeException, FactoryException {
      CoordinateReferenceSystem sourceCrs = null;

      if (!StringUtils.isEmpty(epsg)) {
         sourceCrs = CRS.decode(epsg);
      }
      else {
         try {
            PrjFileReader prjReader = new PrjFileReader(shapefiles);
            sourceCrs = prjReader.getCoodinateSystem();
         }
         catch (Exception e) {
            throw new IllegalArgumentException(
               "No se ha especificado proyección de origen y no se ha encontrado ningún fichero prj");
         }
      }
      if ((sourceCrs != null) && !CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, sourceCrs)) {
         try {
            featureCollection = new ReprojectFeatureResults(featureCollection,
               DefaultGeographicCRS.WGS84);
         }
         catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   public static Integer getSRID(String epsg) {
      Integer srid = null;

      try {
         CoordinateReferenceSystem crs = CRS.decode(epsg);
         srid = getSRID(crs);
      }
      catch (FactoryException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return srid;
   }

   public static Integer getSRID(CoordinateReferenceSystem crs) {
      Integer srid = null;

      try {
         srid = CRS.lookupEpsgCode(crs, true);
      }
      catch (FactoryException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return srid;
   }

   public static String getEPSG(Integer srid) {
      String epsg = null;
      if (srid != null) {
         epsg = "EPSG:".concat(srid.toString());
      }
      return epsg;
   }

   public static String getEPSG(CoordinateReferenceSystem crs) {
      Integer srid = getSRID(crs);
      return getEPSG(srid);
   }

   public static CoordinateReferenceSystem getCRS(String epsg) {
      CoordinateReferenceSystem crs = null;

      try {
         crs = CRS.decode(epsg);
      }
      catch (NoSuchAuthorityCodeException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (FactoryException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return crs;
   }

   public static CoordinateReferenceSystem getCRS(ShpFiles shpFiles, String epsg)
      throws NoSuchAuthorityCodeException, FactoryException {
      CoordinateReferenceSystem crs = null;
      CoordinateReferenceSystem userEPSG = null;
      CoordinateReferenceSystem shpFileEPSG = null;

      // user epsg
      if (!StringUtils.isEmpty(epsg)) {
         userEPSG = getCRS(epsg);
      }

      // file PRJ epsg
      try {
         PrjFileReader prjReader = new PrjFileReader(shpFiles);
         shpFileEPSG = prjReader.getCoodinateSystem();
      }
      catch (Exception e) {
         // no PRJ file was found
         shpFileEPSG = null;
      }

      if ((userEPSG == null) && (shpFileEPSG == null)) {
         throw new IllegalArgumentException(
            "No se ha especificado proyección de origen y no se ha encontrado ningún fichero prj");
      }
      else if ((userEPSG != null) && (shpFileEPSG != null)) {
         Integer userSRID = getSRID(userEPSG);
         Integer shpFileSRID = getSRID(shpFileEPSG);
         if (userSRID != shpFileSRID) {
            throw new IllegalArgumentException("El EPSG especificado '" + userSRID
               + "' no coincide con el del fichero PRJ '" + shpFileSRID + "'");
         }
         crs = shpFileEPSG;
      }
      else if (userEPSG == null) {
         crs = shpFileEPSG;
      }
      else if (shpFileEPSG == null) {
         crs = userEPSG;
      }

      return crs;
   }

   public static String documentToString(Document document) throws TransformerException {
      StringWriter sw = new StringWriter();
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

      transformer.transform(new DOMSource(document), new StreamResult(sw));

      return sw.toString();
   }

   public static byte[] documentToByte(Document document) throws TransformerException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

      transformer.transform(new DOMSource(document), new StreamResult(bos));

      return bos.toByteArray();
   }

   public static byte[] compressXMLFiles(XMLFileVO[] xmlFiles) throws TransformerException,
      IOException {
      byte[] compressedFiles = null;

      ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
      ZipOutputStream zipOS = new ZipOutputStream(byteOS);
      for (XMLFileVO xmlFile : xmlFiles) {
         ZipEntry entry = new ZipEntry(xmlFile.getName());
         zipOS.putNextEntry(entry);
         byte[] xml = documentToByte(xmlFile.getXml());
         ByteArrayInputStream byteIS = new ByteArrayInputStream(xml);
         IOUtils.copy(byteIS, zipOS);
         byteIS.close();
         zipOS.closeEntry();
      }
      zipOS.close();

      compressedFiles = byteOS.toByteArray();

      return compressedFiles;
   }

   public static String removeLastSlash(String url) {
      String checkedUrl = url;

      if (!StringUtils.isEmpty(checkedUrl)) {
         int lastIndex = (checkedUrl.length() - 1);
         if (checkedUrl.lastIndexOf("/") == lastIndex) {
            checkedUrl = checkedUrl.substring(0, lastIndex);
         }
      }

      return checkedUrl;
   }

   public static String getFieldName(GeosearchFieldVO field) {
      String fieldName = field.getName();
      if (StringUtils.isEmpty(fieldName)) {
         fieldName = field.getNameOnTable();
      }
      return fieldName;
   }

   /**
    * Builds a WKT point from the coordinate X and the coordinate Y
    * 
    * @param coordinateX
    *           the coordinate X of the point
    * @param coordinateY
    *           the coordinate Y of the point
    * 
    * @return the WKT point from the coordinates
    */
   public static String getPointWKTFromCoordinates(String coordinateX, String coordinateY) {
      return "POINT(".concat(coordinateX).concat(" ").concat(coordinateY).concat(")");
   }
   
   public static boolean isValidName(String name) {
      boolean validName = false;
      if (!StringUtils.isEmpty(name)) {
         Pattern validNamePattern = Pattern.compile("^[a-zA-Z][\\w\\d]*$", Pattern.CASE_INSENSITIVE);
         validName = validNamePattern.matcher(name).matches();
      }
      return validName;
   }
   
   public static boolean isValidDouble(String doubleString) {
      boolean validDouble = false;
      try {
         Double.parseDouble(doubleString);
         validDouble = true;
      }
      catch (NumberFormatException nfe) { }
      
      return validDouble;
   }
   
   public static GeoserverVO getGeoserverVOByURL(String urlService) {
      GeoserverVO geoserverVO = null;
      for (GeoserverVO gSA : PanelSettings.geoservers) {
         if (urlService.startsWith(gSA.getGeoserverUrl())) {
            geoserverVO = gSA;
            break;
         }
      }
      return geoserverVO;
   }

   /**
    * TODO
    *
    * @param serviceUrl
    * @return
    */
   public static String getWorkspaceFromServiceUrl(String serviceUrl) {
      String workspace = null;
      GeoserverVO geoserver = getGeoserverVOByURL(serviceUrl);
      String geoserverUrl = geoserver.getGeoserverUrl();
      if (serviceUrl.startsWith(geoserverUrl)) {
         String geoserverSlashUrl = geoserverUrl.concat("/");
         String workspaceAndParameters = serviceUrl.replace(geoserverSlashUrl, "");
         int idxSlash = workspaceAndParameters.indexOf("/");

         workspace = workspaceAndParameters.substring(0, idxSlash);
      }
      return workspace;
  }

   /**
    * TODO
    *
    * @param serviceUrl
    * @return
    */
   public static Geoserver getGeoserverFromWFSCapabilitiesUrl(String serviceUrl) {
      Geoserver geoserver = null;
      for (GeoserverVO geoserverVO : PanelSettings.geoservers) {
         String geoserverUrl = geoserverVO.getGeoserverUrl();
         if (serviceUrl.startsWith(geoserverUrl)) {
            geoserver = new Geoserver(geoserverVO);
            break;
         }
      }
      return geoserver;
   }

   /**
    * TODO
    *
    * @param serviceUrl
    * @return
    */
   public static String getGeoserverUrl(String serviceUrl) {
      String geoserverUrl = null;
      
      GeoserverVO geoserver = getGeoserverVOByURL(serviceUrl);
      if (geoserver != null) {
         geoserverUrl = geoserver.getGeoserverUrl();
      }
      
      return geoserverUrl;
   }
}
