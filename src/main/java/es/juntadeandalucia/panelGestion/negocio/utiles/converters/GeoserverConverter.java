/**
 * 
 */
package es.juntadeandalucia.panelGestion.negocio.utiles.converters;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import es.juntadeandalucia.panelGestion.negocio.utiles.Geoserver;
import es.juntadeandalucia.panelGestion.negocio.utiles.Utils;
import es.juntadeandalucia.panelGestion.negocio.vo.GeoserverVO;
import es.juntadeandalucia.panelGestion.persistencia.entidades.Table;

/**
 * This class converts between Table and
 * selected item values from its id
 *
 * @author GUADALTEL S.A
 */
@Name(value = "geoserverConverter")
@Converter
@BypassInterceptors
public class GeoserverConverter implements javax.faces.convert.Converter, Serializable {

   /**
    * Generated version UID
    */
   private static final long serialVersionUID = -3655291908874218345L;
   
   @Override
   public Object getAsObject(FacesContext context, UIComponent component, String value) {
      Geoserver geoserver = null;
      if (value != null) {
         GeoserverVO geoserverVO = Utils.getGeoserverVOByURL(value);
         geoserver = new Geoserver(geoserverVO);
      }
      return geoserver;
   }

   @Override
   public String getAsString(FacesContext context, UIComponent component, Object value) {
      String geoserverUrl = "";
      if ((value != null) && (value instanceof Geoserver)) {
    	 Geoserver geoserver = (Geoserver) value;
         geoserverUrl = geoserver.getGeoserverAUX().getGeoserverUrl();
      }
      return geoserverUrl;
   }
}
