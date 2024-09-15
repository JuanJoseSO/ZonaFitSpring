package udemy.zona_fit.controlador;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udemy.zona_fit.modelo.Cliente;
import udemy.zona_fit.servicio.IClienteServicio;

import java.util.List;

@Component
@Data // Lombok genera automáticamente los getters y setters para todos los campos.
@ViewScoped // Este bean vive mientras se esté visualizando la vista, ideal para formularios.
public class IndexControlador {
    // Inyecta el IClienteServicio.
    @Autowired
    IClienteServicio clienteServicio;
    // Lista que contendrá todos los clientes.
    private List<Cliente> clientes;
    // Cliente que será seleccionado para edición o eliminación.
    private Cliente clienteSeleccionado;
    // Logger para escribir información en el log, consola.
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    // Metodo anotado con @PostConstruct que se ejecuta una vez al inicializar el bean.
    @PostConstruct
    public void init() {
        cargarDatosClientes();
    }

    //Son metodos públicos ya que tenemos que acceder a ellos desde la vista
    public void cargarDatosClientes() {
        this.clientes = clienteServicio.listarClientes();
        this.clientes.forEach(cliente -> logger.info(cliente.toString()));
    }

    public void agregarCliente() {
        this.clienteSeleccionado = new Cliente();
    }

    public void guardarCliente() {
        logger.info("Cliente a guardar: " + this.clienteSeleccionado);
        if (this.clienteSeleccionado.getIdCliente() == null) {
            this.clienteServicio.guardarCliente(this.clienteSeleccionado);
            this.clientes.add(this.clienteSeleccionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente agregado."));
        } else {
            // Si ya tiene ID, significa que es una actualización de un cliente existente.
            this.clienteServicio.guardarCliente(clienteSeleccionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente actualizado."));

        }
        // Cierra la ventana modal de edición del cliente.
        PrimeFaces.current().executeScript("PF('ventanaModalCliente').hide()");
        // Actualiza la tabla de clientes y el área de mensajes usando AJAX.
        PrimeFaces.current().ajax().update("forma-clientes:mensajes", "forma-clientes:clientes-tabla");
        //Reseteamos cliente seleccionado
        this.clienteSeleccionado = null;
    }

    public void eliminarCliente() {
        logger.info("Cliente a eliminar: " + this.clienteSeleccionado);
        this.clienteServicio.eliminarCliente(clienteSeleccionado);
        this.clientes.remove(this.clienteSeleccionado);
        // Resetea el cliente seleccionado para que no se guarden registros antiguos
        this.clienteSeleccionado = null;

        // Muestra un mensaje en pantalla de que el cliente fue eliminado.
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente eliminado."));
        // Actualiza la tabla de clientes y el área de mensajes usando AJAX.
        PrimeFaces.current().ajax().update("forma-clientes:mensajes", "forma-clientes:clientes-tabla");
    }

    /* Aquí es importante explicar por que en unos botones usamos:
            -hide() en el frontend: Es útil para cerrar la ventana modal sin necesidad de procesar datos en
            el servidor, como ocurre con el botón "Cancelar".

            -executeScript() en el backend: Se usa para cerrar la ventana después de que una operación
            (guardar, eliminar) haya sido procesada exitosamente en el servidor. Esto asegura que la ventana se
            cierre automáticamente una vez que la lógica del servidor ha terminado.*/
}
