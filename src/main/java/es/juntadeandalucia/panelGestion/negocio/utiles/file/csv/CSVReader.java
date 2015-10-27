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
package es.juntadeandalucia.panelGestion.negocio.utiles.file.csv;

/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;

/**
 * Adapted Class from the CSVReader class of the OpenCSV library The new features of this customized
 * class are: get number of read bytes
 * 
 * @author GUADALTEL S.A
 */
public class CSVReader implements Closeable {

   private BufferedReader br;

   private boolean hasNext = true;

   private CSVParser parser;

   private int skipLines;

   private boolean linesSkiped;

   /**
    * The new feature. Get the number of read bytes
    */
   private long readBytes;

   /**
    * The default line to start reading.
    */
   public static final int DEFAULT_SKIP_LINES = 0;
   
   /**
    * Constructs CSVReader using a comma for the separator.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    */
   public CSVReader(Reader reader) {
      this(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER,
         CSVParser.DEFAULT_ESCAPE_CHARACTER);
   }

   /**
    * Constructs CSVReader with supplied separator.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries.
    */
   public CSVReader(Reader reader, char separator) {
      this(reader, separator, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER);
   }

   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    */
   public CSVReader(Reader reader, char separator, char quotechar) {
      this(reader, separator, quotechar, CSVParser.DEFAULT_ESCAPE_CHARACTER, DEFAULT_SKIP_LINES,
         CSVParser.DEFAULT_STRICT_QUOTES);
   }

   /**
    * Constructs CSVReader with supplied separator, quote char and quote handling behavior.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param strictQuotes
    *           sets if characters outside the quotes are ignored
    */
   public CSVReader(Reader reader, char separator, char quotechar, boolean strictQuotes) {
      this(reader, separator, quotechar, CSVParser.DEFAULT_ESCAPE_CHARACTER, DEFAULT_SKIP_LINES,
         strictQuotes);
   }

   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param escape
    *           the character to use for escaping a separator or quote
    */

   public CSVReader(Reader reader, char separator, char quotechar, char escape) {
      this(reader, separator, quotechar, escape, DEFAULT_SKIP_LINES,
         CSVParser.DEFAULT_STRICT_QUOTES);
   }

   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param line
    *           the line number to skip for start reading
    */
   public CSVReader(Reader reader, char separator, char quotechar, int line) {
      this(reader, separator, quotechar, CSVParser.DEFAULT_ESCAPE_CHARACTER, line,
         CSVParser.DEFAULT_STRICT_QUOTES);
   }

   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param escape
    *           the character to use for escaping a separator or quote
    * @param line
    *           the line number to skip for start reading
    */
   public CSVReader(Reader reader, char separator, char quotechar, char escape, int line) {
      this(reader, separator, quotechar, escape, line, CSVParser.DEFAULT_STRICT_QUOTES);
   }

   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param escape
    *           the character to use for escaping a separator or quote
    * @param line
    *           the line number to skip for start reading
    * @param strictQuotes
    *           sets if characters outside the quotes are ignored
    */
   public CSVReader(Reader reader, char separator, char quotechar, char escape, int line,
      boolean strictQuotes) {
      this(reader, separator, quotechar, escape, line, strictQuotes,
         CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE);
   }

   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param escape
    *           the character to use for escaping a separator or quote
    * @param line
    *           the line number to skip for start reading
    * @param strictQuotes
    *           sets if characters outside the quotes are ignored
    * @param ignoreLeadingWhiteSpace
    *           it true, parser should ignore white space before a quote in a field
    */
   public CSVReader(Reader reader, char separator, char quotechar, char escape, int line,
      boolean strictQuotes, boolean ignoreLeadingWhiteSpace) {
      this.br = new BufferedReader(reader);
      this.parser = new CSVParser(separator, quotechar, escape, strictQuotes,
         ignoreLeadingWhiteSpace, CSVParser.DEFAULT_IGNORE_QUOTATIONS);
      this.skipLines = line;
   }
   
   /**
    * Constructs CSVReader with supplied separator and quote char.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param quotechar
    *           the character to use for quoted elements
    * @param escape
    *           the character to use for escaping a separator or quote
    * @param line
    *           the line number to skip for start reading
    * @param strictQuotes
    *           sets if characters outside the quotes are ignored
    * @param ignoreLeadingWhiteSpace
    *           if true, parser should ignore white space before a quote in a field
    * @param ignoreQuotations
    *           if true, parser should ignore the quotes in a line
    */
   public CSVReader(Reader reader, char separator, char quotechar, char escape, int line,
      boolean strictQuotes, boolean ignoreLeadingWhiteSpace, boolean ignoreQuotations) {
      this.br = new BufferedReader(reader);
      this.parser = new CSVParser(separator, quotechar, escape, strictQuotes,
         ignoreLeadingWhiteSpace, ignoreQuotations);
      this.skipLines = line;
   }

   /**
    * Constructs CSVReader with supplied separator and ignore quotes option.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    * @param separator
    *           the delimiter to use for separating entries
    * @param ignoreQuotations
    *           if true, parser should ignore the quotes in a line
    */
   public CSVReader(InputStreamReader reader, char separator, boolean ignoreQuotations) {
      this(reader, separator, CSVParser.DEFAULT_QUOTE_CHARACTER,
         CSVParser.DEFAULT_ESCAPE_CHARACTER, DEFAULT_SKIP_LINES, CSVParser.DEFAULT_STRICT_QUOTES,
         CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE, ignoreQuotations);
   }

   /**
    * Reads the entire file into a List with each element being a String[] of tokens.
    * 
    * @return a List of String[], with each String[] representing a line of the file.
    * 
    * @throws IOException
    *            if bad things happen during the read
    */
   public List<String[]> readAll() throws IOException {

      List<String[]> allElements = new ArrayList<String[]>();
      while (hasNext) {
         String[] nextLineAsTokens = readNext();
         if (nextLineAsTokens != null)
            allElements.add(nextLineAsTokens);
      }
      return allElements;

   }

   /**
    * Reads the next line from the buffer and converts to a string array.
    * 
    * @return a string array with each comma-separated element as a separate entry.
    * 
    * @throws IOException
    *            if bad things happen during the read
    */
   public String[] readNext() throws IOException {

      String[] result = null;
      do {
         String nextLine = getNextLine();
         if (!hasNext) {
            return result; // should throw if still pending?
         }
         String[] r = parser.parseLineMulti(nextLine);
         if (r.length > 0) {
            if (result == null) {
               result = r;
            }
            else {
               String[] t = new String[result.length + r.length];
               System.arraycopy(result, 0, t, 0, result.length);
               System.arraycopy(r, 0, t, result.length, r.length);
               result = t;
            }
         }
      }
      while (parser.isPending());
      return result;
   }

   /**
    * Reads the next line from the file.
    * 
    * @return the next line from the file without trailing newline
    * @throws IOException
    *            if bad things happen during the read
    */
   private String getNextLine() throws IOException {
      if (!this.linesSkiped) {
         for (int i = 0; i < skipLines; i++) {
            br.readLine();
         }
         this.linesSkiped = true;
      }
      String nextLine = br.readLine();
      if (nextLine == null) {
         hasNext = false;
      }
      else {
         readBytes += nextLine.getBytes().length;
      }
      return nextLine;
   }

   /**
    * Closes the underlying reader.
    * 
    * @throws IOException
    *            if the close fails
    */
   public void close() throws IOException {
      br.close();
   }
   
  

   /**
    * @return the readBytes
    */
   public long getReadBytes() {
      return readBytes;
   }
}
