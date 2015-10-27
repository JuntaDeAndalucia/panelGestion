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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Clase de respaldo para pantalla de formulario
 * @author Junta de Andalucía
 *
 */
@Name("formulario")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class Formulario implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/* Controles del formulario */
	private String texto="";
	private String combo="";
	private List<String> seleccionMultiple = new ArrayList<String>();
	private String radio="";
	private String textArea="";
	private Boolean check;
	private Date fechaDesde;
	private Date fechaHasta;
	
	/* Listas para almacenar los elementos de los controles que poseen m�ltiples valores */
	private List<SelectItem> elementosCombo;
	private List<SelectItem> elementosSeleccionMultiple;
	private List<SelectItem> elementosRadio;
	
	/**
	 * Constructor.
	 * Crea e inicializa las listas para los controles de selecci�n m�ltiple.
	 */
	public Formulario() {
		elementosCombo = new ArrayList<SelectItem>();
		elementosCombo.add(new SelectItem("Álava"));
		elementosCombo.add(new SelectItem("Albacete"));
		elementosCombo.add(new SelectItem("Alicante"));
		elementosCombo.add(new SelectItem("Almería"));
		elementosCombo.add(new SelectItem("Asturias"));
		elementosCombo.add(new SelectItem("Ávila"));
		elementosCombo.add(new SelectItem("Badajoz"));
		elementosCombo.add(new SelectItem("Barcelona"));
		elementosCombo.add(new SelectItem("Burgos"));
		elementosCombo.add(new SelectItem("Cáceres"));
		elementosCombo.add(new SelectItem("Cádiz"));
		elementosCombo.add(new SelectItem("Cantabria"));
		elementosCombo.add(new SelectItem("Castellón"));
		elementosCombo.add(new SelectItem("Ciudad Real"));
		elementosCombo.add(new SelectItem("Córdoba"));
		elementosCombo.add(new SelectItem("Coruña (La)"));
		elementosCombo.add(new SelectItem("Cuenca"));
		elementosCombo.add(new SelectItem("Gerona"));
		elementosCombo.add(new SelectItem("Granada"));
		elementosCombo.add(new SelectItem("Guadalajara"));
		elementosCombo.add(new SelectItem("Guipúzcoa"));
		elementosCombo.add(new SelectItem("Huelva"));
		elementosCombo.add(new SelectItem("Huesca"));
		elementosCombo.add(new SelectItem("Islas Baleares"));
		elementosCombo.add(new SelectItem("Jaén"));
		elementosCombo.add(new SelectItem("León"));
		elementosCombo.add(new SelectItem("Lérida"));
		elementosCombo.add(new SelectItem("Lugo"));
		elementosCombo.add(new SelectItem("Madrid"));
		elementosCombo.add(new SelectItem("Málaga"));
		elementosCombo.add(new SelectItem("Murcia"));
		elementosCombo.add(new SelectItem("Navarra"));
		elementosCombo.add(new SelectItem("Orense"));
		elementosCombo.add(new SelectItem("Palencia"));
		elementosCombo.add(new SelectItem("Las Palmas"));
		elementosCombo.add(new SelectItem("Pontevedra"));
		elementosCombo.add(new SelectItem("La Rioja"));
		elementosCombo.add(new SelectItem("Salamanca"));
		elementosCombo.add(new SelectItem("Segovia"));
		elementosCombo.add(new SelectItem("Sevilla"));
		elementosCombo.add(new SelectItem("Soria"));
		elementosCombo.add(new SelectItem("Tarragona"));
		elementosCombo.add(new SelectItem("Santa Cruz de Tenerife"));
		elementosCombo.add(new SelectItem("Teruel"));
		elementosCombo.add(new SelectItem("Toledo"));
		elementosCombo.add(new SelectItem("Valencia"));
		elementosCombo.add(new SelectItem("Valladolid"));
		elementosCombo.add(new SelectItem("Vizcaya"));
		elementosCombo.add(new SelectItem("Zamora"));
		elementosCombo.add(new SelectItem("Zaragoza"));
		
		elementosSeleccionMultiple = new ArrayList<SelectItem>();
		elementosSeleccionMultiple.add(new SelectItem("Opción01"));
		elementosSeleccionMultiple.add(new SelectItem("Opción02"));
		elementosSeleccionMultiple.add(new SelectItem("Opción03"));
		elementosSeleccionMultiple.add(new SelectItem("Opción04"));
		elementosSeleccionMultiple.add(new SelectItem("Opción05"));
		elementosSeleccionMultiple.add(new SelectItem("Opción06"));
		elementosSeleccionMultiple.add(new SelectItem("Opción07"));
		elementosSeleccionMultiple.add(new SelectItem("Opción08"));

		elementosRadio = new ArrayList<SelectItem>();
		elementosRadio.add(new SelectItem("Opción A"));
		elementosRadio.add(new SelectItem("Opción B"));
		elementosRadio.add(new SelectItem("Opción C"));
		elementosRadio.add(new SelectItem("Opción D"));
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getCombo() {
		return combo;
	}

	public void setCombo(String combo) {
		this.combo = combo;
	}

	public List<String> getSeleccionMultiple() {
		return seleccionMultiple;
	}

	public void setSeleccionMultiple(List<String> seleccionMultiple) {
		this.seleccionMultiple = seleccionMultiple;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public String getTextArea() {
		return textArea;
	}

	public void setTextArea(String textArea) {
		this.textArea = textArea;
	}

	public Boolean getCheck() {
		return check;
	}

	public void setCheck(Boolean check) {
		this.check = check;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public List<SelectItem> getElementosCombo() {
		return elementosCombo;
	}

	public void setElementosCombo(List<SelectItem> elementosCombo) {
		this.elementosCombo = elementosCombo;
	}

	public List<SelectItem> getElementosSeleccionMultiple() {
		return elementosSeleccionMultiple;
	}

	public void setElementosSeleccionMultiple(List<SelectItem> elementosSeleccionMultiple) {
		this.elementosSeleccionMultiple = elementosSeleccionMultiple;
	}

	public List<SelectItem> getElementosRadio() {
		return elementosRadio;
	}

	public void setElementosRadio(List<SelectItem> elementosRadio) {
		this.elementosRadio = elementosRadio;
	}

}
