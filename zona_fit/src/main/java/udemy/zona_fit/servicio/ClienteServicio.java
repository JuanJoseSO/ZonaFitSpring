package udemy.zona_fit.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import udemy.zona_fit.modelo.Cliente;
import udemy.zona_fit.repositorio.ClienteRepositorio;

import java.util.List;

@Service
public class ClienteServicio implements IClienteServicio {
    //Lo primero que vamos a hacer es agregar una injección de dependencia, el repositorio de datos.
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    public List<Cliente> listarClientes() {
        //Usamos uno de los métodos de la interfaz JpaRepository de la que hereda la clase ClienteRepositorio
        return clienteRepositorio.findAll();
    }

    @Override
    public Cliente buscarClientePorId(Integer idCliente) {
        // Usamos otro mtodo de la interfaz, que nos devolverá un cliente con el id requerido o nulo,
        return clienteRepositorio.findById(idCliente).orElse(null);
    }

    @Override
    public void guardarCliente(Cliente cliente) {
        //Si fuera nulo, se hace un insert, si no fuera nulo, es decir, si existiera, se hace un update
        clienteRepositorio.save(cliente);
    }

    @Override
    public void eliminarCliente(Cliente cliente) {
clienteRepositorio.delete(cliente);
    }
}
