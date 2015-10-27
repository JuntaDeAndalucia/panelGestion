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
package es.juntadeandalucia.panelGestion.negocio.utiles.file.csv;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.AbstractFileProcessor;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.FileReader;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.LocalFileReader;
import es.juntadeandalucia.panelGestion.negocio.utiles.file.RemoteFileReader;
import es.juntadeandalucia.panelGestion.negocio.vo.ColumnVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Source;

/**
 * This class manages and processes CSV files
 * uploaded by the user or available in a public repository.
 * 
 * @author GUADALTEL S.A
 */
public final class CSVFileProcessor extends AbstractFileProcessor {
   
   private CSVReader csvReader;
   
   private List<ColumnVO> columns;
   
   private String[] columnsLine;
   
   /**
    * Main constructor
    * @throws IOException thrown to try to get the reader
    */
   public CSVFileProcessor(Source source, File localSourceData) throws IOException {
      char divider = source.getDivider().charAt(0);
      
      FileReader fileReader;
      if (source.isRemote()) {
         fileReader = new RemoteFileReader(source);
      }
      else {
         fileReader = new LocalFileReader(localSourceData);
         numEntries = fileReader.countLines();
      }
      bytesLength = fileReader.getBytesLength();
      csvReader = new CSVReader(fileReader.getInputStreamReader(), divider, true);
   }
   
   @Override
   public List<ColumnVO> readColumns() throws IOException {
      columns = new LinkedList<ColumnVO>();
      
      columnsLine = csvReader.readNext();
      for (int i = 0; i < columnsLine.length; i++) {
         String strColumn = columnsLine[i];
         
         ColumnVO column = new ColumnVO();
         column.setFilePosition(i);
         column.setText(strColumn);
         column.setNameOnTable(Utils.removeSpecialChars(strColumn));
         
         columns.add(column);
      }
      
      return columns;
   }

   @Override
   public String[] nextCSVEntry() throws IOException {
      String[] line = csvReader.readNext();
      
      numCurrentEntry++;
      readBytes = csvReader.getReadBytes();
      
      return line;
   }
   
   @Override
   public void end() throws IOException {
      if (columns != null) {
         columns.clear();
         columns = null;
      }
      if (csvReader != null) {
         csvReader.close();
         csvReader = null;
      }
   }

   /**
    * @return the columnsLine
    */
   public String[] getColumnsLine() {
      return columnsLine;
   }
}
