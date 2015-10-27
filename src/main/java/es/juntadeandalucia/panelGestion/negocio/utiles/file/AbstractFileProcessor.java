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
package es.juntadeandalucia.panelGestion.negocio.utiles.file;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;

public abstract class AbstractFileProcessor implements FileProcessor, Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -6805059197554835939L;
   
   protected int numEntries;
   protected int numCurrentEntry;
   protected long readBytes;
   protected long bytesLength;
   
   @Override
   public abstract List<ColumnVO> readColumns() throws IOException;

   @Override
   public String[] nextCSVEntry() throws IOException, IllegalAccessException {
      throw new IllegalAccessException("No puede ejecutarse el método CSV para una clase que lee de un Shapefile");
   }
   
   @Override
   public SimpleFeature nextShapeEntry() throws IOException, IllegalAccessException {
      throw new IllegalAccessException("No puede ejecutarse el método SHAPE para una clase que lee de un CSV");
   }

   @Override
   public abstract void end() throws IOException;

   @Override
   public final int getNumCurrentEntry() {
      return numCurrentEntry;
   }

   @Override
   public final int getNumEntries() {
      return numEntries;
   }

   @Override
   public final long getBytesLength() {
      return bytesLength;
   }

   @Override
   public final long getReadBytes() {
      return readBytes;
   }

}
