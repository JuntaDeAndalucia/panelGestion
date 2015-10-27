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
package es.juntadeandalucia.panelGestion.negocio.vo;

import java.io.Serializable;

public class ColumnVO implements Serializable {
   
   /**
    * Generated version UID
    */
   private static final long serialVersionUID = 1583130288038753065L;
   
   private String text;
   private String nameOnTable;
   private String type;
   private String geometryType;
   private Integer sqlType;
   private Integer length;
   private Integer precision;
   private int filePosition;
   private boolean inTable;
   private Class<?> typeClass;
   private boolean coordinateX;
   private boolean coordinateY;
   private boolean fromCoordinates;
   private Integer fileCoordinateXPosition;
   private Integer fileCoordinateYPosition;
   
   public ColumnVO() { 
      filePosition = -1;
   }

   /**
    * @return the text
    */
   public String getText() {
      return text;
   }

   /**
    * @param text the text to set
    */
   public void setText(String text) {
      this.text = text;
   }

   /**
    * @return the nameOnTable
    */
   public String getNameOnTable() {
      return nameOnTable;
   }

   /**
    * @param nameOnTable the tableName to set
    */
   public void setNameOnTable(String nameOnTable) {
      this.nameOnTable = nameOnTable;
   }

   /**
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * @return the filePosition
    */
   public int getFilePosition() {
      return filePosition;
   }

   /**
    * @param filePosition the filePosition to set
    */
   public void setFilePosition(int filePosition) {
      this.filePosition = filePosition;
   }

   /**
    * @return the inTable
    */
   public boolean isInTable() {
      return inTable;
   }

   /**
    * @param inTable the inTable to set
    */
   public void setInTable(boolean inTable) {
      this.inTable = inTable;
   }

   /**
    * @return the length
    */
   public Integer getLength() {
      return length;
   }

   /**
    * @param length the length to set
    */
   public void setLength(Integer length) {
      this.length = length;
   }

   /**
    * @return the precision
    */
   public Integer getPrecision() {
      return precision;
   }

   /**
    * @param precision the precision to set
    */
   public void setPrecision(Integer precision) {
      this.precision = precision;
   }

   /**
    * @return the sqlType
    */
   public Integer getSqlType() {
      return sqlType;
   }

   /**
    * @param sqlType the sqlType to set
    */
   public void setSqlType(Integer sqlType) {
      this.sqlType = sqlType;
   }

   /**
    * @return the typeClass
    */
   public Class<?> getTypeClass() {
      return typeClass;
   }

   /**
    * @param typeClass the typeClass to set
    */
   public void setTypeClass(Class<?> typeClass) {
      this.typeClass = typeClass;
   }

   /**
    * @return the geometryType
    */
   public String getGeometryType() {
      return geometryType;
   }

   /**
    * @param geometryType the geometryType to set
    */
   public void setGeometryType(String geometryType) {
      this.geometryType = geometryType;
   }

   /**
    * @return the coordinateX
    */
   public boolean isCoordinateX() {
      return coordinateX;
   }

   /**
    * @param coordinateX the coordinateX to set
    */
   public void setCoordinateX(boolean coordinateX) {
      this.coordinateX = coordinateX;
   }

   /**
    * @return the coordinateY
    */
   public boolean isCoordinateY() {
      return coordinateY;
   }

   /**
    * @param coordinateY the coordinateY to set
    */
   public void setCoordinateY(boolean coordinateY) {
      this.coordinateY = coordinateY;
   }

   /**
    * @return the fromCoordinates
    */
   public boolean isFromCoordinates() {
      return fromCoordinates;
   }

   /**
    * @param fromCoordinates the fromCoordinates to set
    */
   public void setFromCoordinates(boolean fromCoordinates) {
      this.fromCoordinates = fromCoordinates;
   }

   /**
    * @return the fileCoordinateXPosition
    */
   public Integer getFileCoordinateXPosition() {
      return fileCoordinateXPosition;
   }

   /**
    * @param fileCoordinateXPosition the fileCoordinateXPosition to set
    */
   public void setFileCoordinateXPosition(Integer fileCoordinateXPosition) {
      this.fileCoordinateXPosition = fileCoordinateXPosition;
   }

   /**
    * @return the fileCoordinateYPosition
    */
   public Integer getFileCoordinateYPosition() {
      return fileCoordinateYPosition;
   }

   /**
    * @param fileCoordinateYPosition the fileCoordinateYPosition to set
    */
   public void setFileCoordinateYPosition(Integer fileCoordinateYPosition) {
      this.fileCoordinateYPosition = fileCoordinateYPosition;
   }
}
