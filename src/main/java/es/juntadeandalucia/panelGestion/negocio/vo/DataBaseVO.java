package es.juntadeandalucia.panelGestion.negocio.vo;

import java.io.Serializable;
import java.util.Date;

public class DataBaseVO implements Serializable{
	 /**
    * Generated version UID
    */
   private static final long serialVersionUID = 1583130288038753065L;
/*
 * jdbc:postgresql://hefesto.guadaltel.es:5432/bdcallejero
 * 
 */
   private long id;
   private String alias;
   private String host;
   private String puerto;
   private String baseDatos;
   private String tipoBaseDatos;
   private Date dateCreation;
   
   public DataBaseVO(){
	   this.alias = "";
	   this.host = "";
	   this.puerto = "";
	   this.baseDatos = "";
	   this.tipoBaseDatos = "";
   }
	   
   public static long getSerialversionuid() {
		return serialVersionUID;
	}
   
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getPuerto() {
		return puerto;
	}
	
	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	
	public String getBaseDatos() {
		return baseDatos;
	}
	
	public void setBaseDatos(String baseDatos) {
		this.baseDatos = baseDatos;
	}
	
	public String getTipoBaseDatos() {
		return tipoBaseDatos;
	}
	
	public void setTipoBaseDatos(String tipoBaseDatos) {
		this.tipoBaseDatos = tipoBaseDatos;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	@Override
	public String toString() {
		return "DataBaseVO [alias=" + alias + ", host=" + host + ", puerto="
				+ puerto + ", baseDatos=" + baseDatos + ", tipoBaseDatos="
				+ tipoBaseDatos + "]";
	}
	   
	
	
	
	
	   
	}