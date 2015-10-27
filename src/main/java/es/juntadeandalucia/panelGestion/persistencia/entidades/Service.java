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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Class which manages the Service entities
 *
 * @author GUADALTEL S.A
 */
@Entity
@javax.persistence.Table(name = "SERVICES")
public class Service implements Serializable {
   /**
    * Generated serial version UID
    */
   private static final long serialVersionUID = -202637300496670205L;

   /**
    * Entity identifier
    */
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "X_SERVICE", unique = true,
      nullable = false, precision = 5, scale = 0)
   private long id;
   
   /**
    * Service url
    */
   @Column(name = "T_SERVICE_URL", length = 250)
   private String serviceUrl;
   
   /**
    * Service name
    */
   @Column(name = "T_NAME", length = 50)
   private String name;
   
   /**
    * Creation date of the service
    */
   @Temporal(TemporalType.DATE)
   @Column(name = "F_CREATION_DATE", length = 7)
   private Date creationDate;
   
   /**
    * Type of the service
    */
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "SETY_X_SERVICE_TYPE")
   private ServiceType type;
   
   /**
    * Associated services
    */
   @OnDelete(action = OnDeleteAction.CASCADE)
   @OneToMany(mappedBy = "service", cascade = CascadeType.REMOVE)
   private List<TableXService> tableXservices;
   
   /**
    * Service parents dependences
    */
   @ManyToMany
   @JoinTable(
      name = "SERVICES_DEPENDENCES",
      joinColumns = {@JoinColumn(name = "SERVICES_X_SERVICE_PARENT",
         referencedColumnName="X_SERVICE")},
      inverseJoinColumns = {@JoinColumn(name = "SERVICES_X_SERVICE_CHILD",
         referencedColumnName="X_SERVICE")})
   private List<Service> parents;
   
   /**
    * Service children dependences
    */
   @ManyToMany(mappedBy = "parents", cascade = CascadeType.REMOVE)
   private List<Service> children;
   
   /**
    * Main constructor
    */
   public Service() { 
      tableXservices = new LinkedList<TableXService>();
   }

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
    * @return the serviceUrl
    */
   public String getServiceUrl() {
      return serviceUrl;
   }

   /**
    * @param serviceUrl the serviceUrl to set
    */
   public void setServiceUrl(String serviceUrl) {
      this.serviceUrl = serviceUrl;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
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
    * @return the type
    */
   public ServiceType getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(ServiceType type) {
      this.type = type;
   }

   /**
    * @return the parents
    */
   public List<Service> getParents() {
      return parents;
   }

   /**
    * @param parents the parents to set
    */
   public void setParents(List<Service> parents) {
      this.parents = parents;
   }

   /**
    * @return the children
    */
   public List<Service> getChildren() {
      return children;
   }

   /**
    * @param children the children to set
    */
   public void setChildren(List<Service> children) {
      this.children = children;
   }
   
   /**
    * @return the tableXservices
    */
   public List<TableXService> getTableXservices() {
      return tableXservices;
   }

   /**
    * @param tableXservices the tableXservices to set
    */
   public void setTableXservices(List<TableXService> tableXservices) {
      this.tableXservices = tableXservices;
   }

   @Override
   public boolean equals(Object o) {
      boolean equals = false;
      
      if ((o != null) && (o instanceof Service)) {
         Service service = (Service) o;
         equals = (getId() == service.getId());
      }
      
      return equals;
   }
   
   @Override
   public int hashCode() {
      return (int)(getId() * 29);
   }
}
