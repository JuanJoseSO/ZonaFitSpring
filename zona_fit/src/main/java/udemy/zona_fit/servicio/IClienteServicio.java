package udemy.zona_fit.servicio;

import udemy.zona_fit.modelo.Cliente;
import java.util.List;

public interface IClienteServicio {
    public List<Cliente> listarClientes();
    public Cliente buscarClientePorId(Integer cliente);
    /* Este metodo sirve tanto para guardar como actualizar:
        -Si el valor del id es nulo, se guardará el objeto.
        -Si no es nulo, se actualizará el cliente existente. */
    public void guardarCliente(Cliente cliente);
    public void eliminarCliente(Cliente cliente);
}
