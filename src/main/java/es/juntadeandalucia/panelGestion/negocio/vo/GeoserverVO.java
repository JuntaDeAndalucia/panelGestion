package es.juntadeandalucia.panelGestion.negocio.vo;

import java.io.Serializable;

public class GeoserverVO implements Serializable{
	 /**
    * Generated version UID
    */
   private static final long serialVersionUID = 1583130288038753065L;

   private String geoserverUrl;
   private String geoserverUser;
   private String geoserverPassword;
   private String geoserverWMSVersion;
   private String geoserverWFSVersion;
   
   public GeoserverVO(){
	   
   }
	   
   public static long getSerialversionuid() {
		return serialVersionUID;
	}
	   
	public String getGeoserverUrl() {
		return geoserverUrl;
	}
	public void setGeoserverUrl(String geoserverUrl) {
		this.geoserverUrl = geoserverUrl;
	}
	public String getGeoserverUser() {
		return geoserverUser;
	}
	public void setGeoserverUser(String geoserverUser) {
		this.geoserverUser = geoserverUser;
	}
	public String getGeoserverPassword() {
		return geoserverPassword;
	}
	public void setGeoserverPassword(String geoserverPassword) {
		this.geoserverPassword = geoserverPassword;
	}
	public String getGeoserverWMSVersion() {
		return geoserverWMSVersion;
	}
	public void setGeoserverWMSVersion(String geoserverWMSVersion) {
		this.geoserverWMSVersion = geoserverWMSVersion;
	}
	public String getGeoserverWFSVersion() {
		return geoserverWFSVersion;
	}
	public void setGeoserverWFSVersion(String geoserverWFSVersion) {
		this.geoserverWFSVersion = geoserverWFSVersion;
	}
	
	   
	}