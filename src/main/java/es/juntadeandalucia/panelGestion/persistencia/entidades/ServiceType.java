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
package es.juntadeandalucia.panelGestion.persistencia.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class which manages the Services types entities
 *
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "SERVICES_TYPES")
public class ServiceType implements Serializable {

   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -1376811120234115846L;

   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_SERVICE_TYPE", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * Service type acronym
    */
   @Column(name = "T_ACRONYM", length = 50)
   private String acronym;
   
   /**
    * Service type description
    */
   @Column(name = "T_DESCRIPTION", length = 250)
   private String description;
   
   /**
    * Creation date of the service type
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_CREATION_DATE", length = 7)
   private Date creationDate;
   
   /**
    * Associated services
    */
   @OneToMany(mappedBy = "type")
   private List<Service> services;
   
   /**
    * Main constructor
    */
   public ServiceType() { ; }

   /**
    * @return the id
    */
   public long getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(long id) {
      this.id = id;
   }

   /**
    * @return the acronym
    */
   public String getAcronym() {
      return acronym;
   }

   /**
    * @param acronym the acronym to set
    */
   public void setAcronym(String acronym) {
      this.acronym = acronym;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return the creationDate
    */
   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * @param creationDate the creationDate to set
    */
   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   /**
    * @return the services
    */
   public List<Service> getServices() {
      return services;
   }

   /**
    * @param services the services to set
    */
   public void setServices(List<Service> services) {
      this.services = services;
   }
   
   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      if ((o != null) && (o instanceof ServiceType)) {
         ServiceType type = (ServiceType) o;
         equals = (type.getId() == getId());
      }
      return equals;
   }
   
   @Override
   public int hashCode() {
      return (int) (29 * getId());
   }
}
