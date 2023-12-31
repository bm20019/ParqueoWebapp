/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIOutput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.model.SelectItem;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.Espacio;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.Reserva;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.TipoReserva;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control.AreaBean;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.Area;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.EspacioCaracteristica;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.TipoEspacio;

import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control.EspacioBean;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control.ReservaBean;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control.TipoEspacioBean;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control.TipoReservaBean;

/**
 *
 * @author home
 */
@Named
@ViewScoped
public class FrmReserva extends AbstractFrm<Reserva> implements Serializable {

    @Inject
    ReservaBean rbean;
    @Inject
    TipoReservaBean trbean;
    @Inject
    AreaBean aBean;
    @Inject
    EspacioBean eBean;
    @Inject
    TipoEspacioBean teBean;
    @Inject
    FrmReservaHistorial frmReservaHistorial;
    @Inject
    FacesContext Fc;
    @Inject
    FrmEspacio frmEspacio;
    List<TipoReserva> listaTipoReserva;

    Area areaE;

    TreeNode raiz;
    TreeNode nodoSeleccionado;
    List<Espacio> espaciosDisponibles;
    List<TipoEspacio> caractaristicasDisponibles;
    List<TipoEspacio> caracteristicasSeleccionadas;
    List<SelectItem> caracteristicasDisponiblesAsItems;

    @Override
    public void instanciarRegistro() {
        this.registro = new Reserva();
        listaTipoReserva = trbean.FindRange(0, 10000000);
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        this.raiz = new DefaultTreeNode("Areas", null);
        List<Area> lista = aBean.findByIdPadre(null, 0, 100000000);
        if (lista != null && !lista.isEmpty()) {

            for (Area next : lista) {
                if (next.getIdAreaPadre() == null) {
                    this.generarArbol(raiz, next);
                }

            }
        }
    }

    public void generarArbol(TreeNode padre, Area actual) {
        DefaultTreeNode nuevoPadre = new DefaultTreeNode(actual, padre);

        List<Area> hijos = this.aBean.findByIdPadre(actual.getIdArea(), 0, 1000000000);
        for (Area hijo : hijos) {
            generarArbol(nuevoPadre, hijo);
        }
    }

    public void seleccionarNodoListener(NodeSelectEvent nse) {
        Area area = (Area) nse.getTreeNode().getData();
        if (this.areaE != null && this.areaE.getIdArea() != null && this.frmEspacio != null) {
            this.frmEspacio.setIdArea(areaE.getIdArea());
        }
        espaciosDisponibles = eBean.findByIdArea(area.getIdArea(), 0, 10000);
        caractaristicasDisponibles = teBean.FindRange(0, 100000);

        rellenarEspaciosDisponibles();
        List<SelectItem> items = new ArrayList<>();

        for (TipoEspacio caracteristica : caractaristicasDisponibles) {
            for (EspacioCaracteristica esC : caracteristica.getEspacioCaracteristicaList()) {
                items.add(new SelectItem(caracteristica, caracteristica.getNombre() + ": " + esC.getValor()));
            }
        }

        setCaracteristicasDisponiblesAsItems(items);
        //rellenarEspaciosDisponibles();
    }

    private void rellenarEspaciosDisponibles() {

        if (espaciosDisponibles != null) {
            Date desde = this.registro.getDesde();
            Date hasta = this.registro.getHasta();
            List<Espacio> espacios = espaciosDisponibles;
            List<Espacio> reservado = new ArrayList<>();
            for (Espacio esp : espacios) {
                List<Reserva> rslist = esp.getReservaList();
                for (Reserva rs : rslist) {
                    if (rs.getDesde().getTime() >= desde.getTime() && rs.getHasta().getTime() <= hasta.getTime()) {
                        System.out.println("La fecha ya esta reservada para: " + esp.getNombre());
                        if (!reservado.contains(esp)) {
                            reservado.add(esp);
                        }
                    }
                }
            }

            for (Espacio es : reservado) {
                if (espaciosDisponibles.contains(es)) {
                    espaciosDisponibles.remove(es);
                }
            }

        }
    }

    @Override
    public AbstractDataAccess<Reserva> getDataAccess() {
        return rbean;
    }

    public List<TipoReserva> getListaTipoReserva() {
        return listaTipoReserva;
    }

    public void setListaTipoReserva(List<TipoReserva> listaTipoReserva) {
        this.listaTipoReserva = listaTipoReserva;
    }

    public TreeNode getRaiz() {
        return raiz;
    }

    public void setRaiz(TreeNode raiz) {
        this.raiz = raiz;
    }

    public TreeNode getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void setNodoSeleccionado(TreeNode nodoSeleccionado) {
        this.nodoSeleccionado = nodoSeleccionado;
    }

    public List<Espacio> getEspaciosDisponibles() {
  
        return espaciosDisponibles;
    }

    public void setEspaciosDisponibles(List<Espacio> espaciosDisponibles) {
        this.espaciosDisponibles = espaciosDisponibles;
    }

    public List<TipoEspacio> getCaractaristicasDisponibles() {
        return caractaristicasDisponibles;
    }

    public void setCaractaristicasDisponibles(List<TipoEspacio> caractaristicasDisponibles) {
        this.caractaristicasDisponibles = caractaristicasDisponibles;
    }

    public List<TipoEspacio> getCaracteristicasSeleccionadas() {
        return caracteristicasSeleccionadas;
    }

    public void setCaracteristicasSeleccionadas(List<TipoEspacio> caracteristicasSeleccionadas) {
        this.caracteristicasSeleccionadas = caracteristicasSeleccionadas;
    }

    public List<SelectItem> getCaracteristicasDisponiblesAsItems() {
        return caracteristicasDisponiblesAsItems;
    }

    public void setCaracteristicasDisponiblesAsItems(List<SelectItem> caracteristicasDisponiblesAsItems) {
        this.caracteristicasDisponiblesAsItems = caracteristicasDisponiblesAsItems;
    }

    public String generarPathArea(long idEspacio) {

        Espacio espacio = eBean.findById(idEspacio);

        if (espacio != null) {
            Area areaPadre = espacio.getIdArea().getIdAreaPadre();
            Area area = espacio.getIdArea();
            if (areaPadre != null) {
                return "Espacio: " + espacio.getNombre() + " Areas:" + areaPadre.getNombre() + "/" + area.getNombre();
            } else if (area != null) {
                return "Espacio: " + espacio.getNombre() + " Areas: " + area.getNombre();
            }
        }
        return "";
    }

    public void cambiarFechaDesde(AjaxBehaviorEvent event) {
        this.registro.setDesde((Date) ((UIOutput) event.getSource()).getValue());
        System.out.println(registro.getDesde());
    }

    private boolean espacioDesbloqueado;

    public boolean isEspacioDesbloqueado() {
        return espacioDesbloqueado;
    }

    public void setEspacioDesbloqueado(boolean espacioDesbloqueado) {
        this.espacioDesbloqueado = espacioDesbloqueado;
    }

    public void desbloquearEspacio() {
        espacioDesbloqueado = true;
    }

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            FacesMessage message = new FacesMessage("Error", "La fecha ingresada es nula");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        Date startDateValue = this.registro.getDesde();
        if (startDateValue == null) {
            FacesMessage message = new FacesMessage("Error: ", "La fecha de inicio es nula");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        Date startDate = (Date) startDateValue;
        Date endDate = (Date) value;
        if (endDate.before(startDate)) {
            FacesMessage message = new FacesMessage("Error: ","La fecha final no puede ser anterior a la fecha Inicial.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        if (startDate.before(new Date())) {
            FacesMessage message = new FacesMessage("Error: ","La fecha inicial no debe ser menor a la del sistema");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        if (startDate.equals(endDate)) {
            FacesMessage message = new FacesMessage("Error: ","Fechas iguales.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

    }
// Agregar el siguiente atributo y método
    private boolean fechaValida;

    public boolean isFechaValida() {
        return fechaValida;
    }

    @Override
    public FacesContext getFacesContext() {
        return Fc;
    }

    @Override
    public String getIdPorObjeto(Reserva object) {
        if (object != null && object.getIdReserva() != null) {
            return object.getIdReserva().toString();
        }
        return null;
    }

    @Override
    public Reserva getObjetoPorId(String id) {
        if (id != null && this.modelo != null && this.modelo.getWrappedData() != null) {
            return this.modelo.getWrappedData().stream().filter(r -> r.getIdReserva().toString().equals(id)).collect(Collectors.toList()).get(0);
        }
        return null;
    }

    public void refinarBusquedaNodo() {
        
        
    }

    public FrmReservaHistorial getFrmReservaHistorial() {
        return frmReservaHistorial;
    }

    public void setFrmReservaHistorial(FrmReservaHistorial frmReservaHistorial) {
        this.frmReservaHistorial = frmReservaHistorial;
    }
    
    
}
