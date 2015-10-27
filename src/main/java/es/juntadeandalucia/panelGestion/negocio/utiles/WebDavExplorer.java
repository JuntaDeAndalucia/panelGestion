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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

import es.juntadeandalucia.panelGestion.exception.PanelException;
import es.juntadeandalucia.panelGestion.persistencia.entidades.FileType;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;
import es.juntadeandalucia.panelGestion.persistencia.utiles.Repository;

public abstract class WebDavExplorer {

   public static List<Source> getFilesFrom(Repository repository) throws IOException, DavException, PanelException {
      List<Source> repositoryFiles = new LinkedList<Source>();

      IOException ioe = null;
      DavException dee = null;
      
      String user = repository.getUser();
      String pass = repository.getPassword();
      String url = repository.getUrl();
      
      UrlValidator urlValidator = new UrlValidator();
      if (!urlValidator.isValid(url)) {
         throw new PanelException("La url del repositorio no es válida: " + url);
      }
      
      HttpClient client = new HttpClient();
      
      if (!StringUtils.isEmpty(user) || !StringUtils.isEmpty(pass)) {
         Credentials creds = new UsernamePasswordCredentials(user, pass);
         client.getState().setCredentials(AuthScope.ANY, creds);
      }
      
      DavMethod lsMethod = null;
      try {
         
         lsMethod = new PropFindMethod(url,
            DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
         client.executeMethod(lsMethod);
         MultiStatus multiStatus = lsMethod.getResponseBodyAsMultiStatus();
         for (MultiStatusResponse msResponse : multiStatus.getResponses()) {
            // gets properties from the remote resource
            String sourceUrl = null, sourceName = null, sourceCType = null;
            Integer sourceSize = -1;
            FileType sourceType = null;
            
            DavPropertySet properties = msResponse.getProperties(200);

            // url
            sourceUrl = msResponse.getHref();
            // name
            DavProperty<?> propName = properties.get(DavPropertyName.DISPLAYNAME);
            if (propName != null) {
               sourceName = (String) propName.getValue();
            }
            if (StringUtils.isEmpty(sourceName)) {
               sourceName = Utils.getFileNameFromPath(sourceUrl);
            }
            // size
            DavProperty<?> propLength = properties.get(DavPropertyName.GETCONTENTLENGTH);
            if (propLength != null) {
               String sizeStr = (String) propLength.getValue();
               try {
                  sourceSize = Integer.parseInt(sizeStr);
               }
               catch (NumberFormatException e) {
                  // none
               }
            }
            // content-type
            DavProperty<?> propCType = properties.get(DavPropertyName.GETCONTENTTYPE);
            if (propCType != null) {
               sourceCType = (String) propCType.getValue();
            }
            // type
            sourceType = Utils.getTypeFromContentType(sourceCType);
            if (sourceType == FileType.UNKNOW) {
               sourceType = Utils.getTypeFromName(sourceName);
            }
            // creates the source and adds it to the list
            if (sourceType != null) {
               boolean validShape = ((sourceType == FileType.SHAPEFILE)
                  && (Pattern.matches("^.+\\.shp$", sourceName.toLowerCase())));
               boolean validCSV = (sourceType == FileType.CSV);
               boolean validFile = (validShape || validCSV || Utils.isCompressedFile(sourceCType));
               if (validFile) {
                  Source source = new Source();
                  source.setRemote(true);
                  source.setName(sourceName);
                  source.setSize(sourceSize);
                  source.setContentType(sourceCType);
                  source.setType(sourceType);
                  // repository
                  source.setAlias(repository.getAlias());
                  source.setUrl(repository.getUrl());
                  source.setUser(repository.getUser());
                  source.setPassword(repository.getPassword());
                  repositoryFiles.add(source);
               }
            }
         }
      }
      catch (IOException e) {
         ioe = e;
      }
      catch (DavException e) {
         dee = e;
      }
      finally {
         if (lsMethod != null) { 
            lsMethod.releaseConnection();
         }
      }
      
      if (ioe != null) {
         throw ioe;
      }
      
      if (dee != null) {
         throw dee;
      }
      
      return repositoryFiles;
   }
}
