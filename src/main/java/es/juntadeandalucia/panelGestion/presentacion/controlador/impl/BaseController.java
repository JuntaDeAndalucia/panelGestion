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
/*
Empresa desarrolladora: Guadaltel

Autor: Junta de Andalucía


Derechos de explotación propiedad de la Junta de Andalucía.

Este programa es software libre: usted tiene derecho a redistribuirlo y/o modificarlo bajo los términos de la Licencia EUPL European Public License publicada por el organismo IDABC de la Comisión Europea, en su versión 1.0. o posteriores.

Este programa se distribuye de buena fe, pero SIN NINGUNA GARANTÍA, incluso sin las presuntas garantías implícitas de USABILIDAD o ADECUACIÓN A PROPÓSITO CONCRETO. Para mas información consulte la Licencia EUPL European Public License.

Usted recibe una copia de la Licencia EUPL European Public License junto con este programa, si por algún motivo no le es posible visualizarla, puede consultarla en la siguiente URL: http://ec.europa.eu/idabc/servlets/Doc?id=31099

You should have received a copy of the EUPL European Public License along with this program. If not, see http://ec.europa.eu/idabc/servlets/Doc?id=31096

Vous devez avoir reçu une copie de la EUPL European Public License avec ce programme. Si non, voir http://ec.europa.eu/idabc/servlets/Doc?id=31205

Sie sollten eine Kopie der EUPL European Public License zusammen mit diesem Programm. Wenn nicht, finden Sie da http://ec.europa.eu/idabc/servlets/Doc?id=29919
*/
package es.juntadeandalucia.panelGestion.presentacion.controlador.impl;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

/**
 * 
 * @author Guadaltel
 */
public abstract class BaseController {
	
	@In(required = false)
	@Out(required = false)
	private Boolean inEditMode;
	private static String miga;

	@In(required = false)
	@Out(required = false)
	private String vistaOrigen;
	
	@In
	protected FacesMessages facesMessages;
	
	@Logger
	protected Log log;

	public BaseController() {
	}

	public Boolean getInEditMode() {
		return inEditMode;
	}

	public void setInEditMode(Boolean inEditMode) {
		this.inEditMode = inEditMode;
	}

	public String getMiga() {
		return miga;
	}

	public void setMiga(String miga) {
		BaseController.miga = miga;
	}

	public String getVistaOrigen() {
		return vistaOrigen;
	}

	public void setVistaOrigen(String vistaOrigen) {
		this.vistaOrigen = vistaOrigen;
	}

	/**
	 * añade un FacesMessage con SEVERITY_ERROR
	 * 
	 * @param string
	 */
	public void addMensaseError(String error) {
		addMensaje(Severity.ERROR, error);
	}

	/**
	 * añade un FacesMessage con SEVERITY_INFO
	 * 
	 * @param string
	 */
	public void addMensaseInfo(String error) {
		addMensaje(Severity.INFO, error);
	}

	/**
	 * añade un FacesMessage con SEVERITY_WARN
	 * 
	 * @param string
	 */
	public void addMensaseWarning(String error) {
		addMensaje(Severity.WARN, error);
	}

	/**
	 * Añade un error con una severidad indicada
	 * 
	 * @param SEVERITY_ERROR nivel del mensaje
	 * @param error cadena con el mensaje
	 */
	private void addMensaje(Severity severidad, String mensaje) {
		facesMessages.add(severidad, mensaje);
	}

}
