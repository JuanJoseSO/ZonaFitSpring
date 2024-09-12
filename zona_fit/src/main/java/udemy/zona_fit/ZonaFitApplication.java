package udemy.zona_fit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import udemy.zona_fit.modelo.Cliente;
import udemy.zona_fit.servicio.IClienteServicio;

import java.util.Scanner;

@SpringBootApplication
public class ZonaFitApplication implements CommandLineRunner {
    /* Injectamos la dependencia de IClienteServicio, basicamente permitimos a esta clase acceder a la clase
    servicio que nos permite acceder a las clases Repositorio, que nos permiten acceder a las clases de Entity y a
     su vez a la base de datos. */
    @Autowired
    private IClienteServicio clienteServicio;

    private static final Logger logger = LoggerFactory.getLogger(ZonaFitApplication.class);

    public static void main(String[] args) {
        logger.info("Iniciando la aplicación");
        // Levanta la fabrica de spring
        SpringApplication.run(ZonaFitApplication.class, args);
        logger.info("Aplicación finalizada.");
    }

    @Override
    public void run(String... args) throws Exception {
        zonaFitApp();
    }

    private void zonaFitApp() {
        logger.info("*** Aplicación Zona Fit (GYM) ***");
        var salir = false;
        var sc = new Scanner(System.in);

        while (!salir) {
            try {
                //Tomamos la opción del menú
                var opcion = mostrarMenu(sc);
                //Si la opcion es -1, no ejecutamos ninguna opción
                if (opcion != -1) salir = ejercutarOpciones(sc, opcion);
                else logger.info("Opción no reconocida");

            } catch (Exception e) {
                logger.info("Error al ejecutar opciones {}", e.getMessage());
            }
            //Salto de línea cada vez que se llama al menú
            logger.info("");
        }
    }

    private boolean ejercutarOpciones(Scanner sc, int opcion) {
        switch (opcion) {
            case 1 -> listarClientes();
            case 2 -> buscarClientePorId(sc);
            case 3 -> insertarCliente(sc);
            case 4 -> modificarCliente(sc);
            case 5 -> eliminarCliente(sc);
            case 6 -> {
                logger.info("Cerrando aplicación.");
                return true;
            }
            default -> logger.info("Opción no reconocida.");
        }
        return false;
    }

    private void modificarCliente(Scanner sc) {
        logger.info("--- Modificar Cliente ---");
        logger.info("Introduzca el ID del cliente: ");
        try {
            var idCliente = Integer.parseInt(sc.nextLine());
            var cliente = clienteServicio.buscarClientePorId(idCliente);
            if (cliente != null) {
                logger.info("Introduzca el nombre del usuario:");
                var nombre = sc.nextLine().trim();
                //Verifica si el nombre está vacío
                if (nombre.isEmpty()) {
                    logger.info("El nombre no puede estar vacío, introduzca uno válido.");
                    return;
                }
                logger.info("Introduzca el apellido del usuario:");
                //Verifica si el apellido está vacío
                var apellido = sc.nextLine().trim();
                if (apellido.isEmpty()) {
                    logger.info("El apellido no puede estar vacío, introduzca uno válido.");
                    return;
                }
                logger.info("Introduzca la membresía del usuario.");
                try {
                    //Intenta convertir la entrada en un número entero
                    var membresia = Integer.parseInt(sc.nextLine());
                    cliente.setNombre(nombre);
                    cliente.setApellido(apellido);
                    cliente.setMembresia(membresia);

                    clienteServicio.guardarCliente(cliente);
                    logger.info("Se ha agregado el cliente correctamente: {}", cliente);

                } catch (NumberFormatException e) {
                    logger.info("La membresía debe ser un número entero.{}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.info("El ID debe ser un número entero.");
        }
    }

    private void insertarCliente(Scanner sc) {
        logger.info("Introduzca el nombre del usuario:");
        var nombre = sc.nextLine().trim();
        //Verifica si el nombre está vacío
        if (nombre.isEmpty()) {
            logger.info("El nombre no puede estar vacío, introduzca uno válido.");
            return;
        }
        logger.info("Introduzca el apellido del usuario:");
        //Verifica si el apellido está vacío
        var apellido = sc.nextLine().trim();
        if (apellido.isEmpty()) {
            logger.info("El apellido no puede estar vacío, introduzca uno válido.");
            return;
        }
        logger.info("Introduzca la membresía del usuario.");
        try {
            //Intenta convertir la entrada en un número entero
            var membresia = Integer.parseInt(sc.nextLine());
            //Crea un nuevo objeto Cliente con los datos proporcionados (nombre, apellido, membresía)
            var cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setMembresia(membresia);
            /*Intenta insertar el cliente usando el objeto ClienteDao y gestionamos las opciones con el
            booleano que devuelve la función */
            if (cliente != null) {
                clienteServicio.guardarCliente(cliente);
                logger.info("Se ha agregado el cliente correctamente: {}", cliente);
            } else logger.info("Error. No se ha agregado el cliente: {}", cliente);

        } catch (NumberFormatException e) {
            logger.info("La membresía debe ser un número entero.{}", e.getMessage());
        }
    }

    private void buscarClientePorId(Scanner sc) {
        logger.info("Introduce el ID del cliente a buscar:");
        try {
            /* ¿Por qué primero validamos el número? Es más eficiente en memoria crear la variable int
                y no un objeto Cliente si no hay necesidad para ello
             */
            //Obtenemos y validamos que el ID sea un número válido
            int idCliente = Integer.parseInt(sc.nextLine());

            // Validamos si el ID es positivo, si no, salimos de la función.
            if (idCliente <= 0) {
                logger.info("El ID debe ser un número positivo.");
                return;
            }
            //Buscamos el cliente en la base de datos usando el objeto Cliente.
            //Esté metodo retorna un objeto cliente
            var cliente = clienteServicio.buscarClientePorId(idCliente);
            //Si se ha encontrado, mostramos el cliente, que YA se ha asignado dentro de el metodo al objeto.
            if (cliente != null) {
                logger.info("Cliente encontrado:\n{}", cliente);
            } else logger.info("No se encontró un cliente con ese ID.");
        } catch (NumberFormatException e) {
            logger.info("El ID tiene que ser un valor numérico.");
        }
    }


    private void eliminarCliente(Scanner sc) {
        logger.info("--- Eliminar Cliente ---");
        logger.info("Introduzca el ID:");
        try {
            var idCliente = Integer.parseInt(sc.nextLine());
            var cliente = new Cliente();
            cliente.setIdCliente(idCliente);
            clienteServicio.eliminarCliente(cliente);
            logger.info("Se ha eliminado el cliente con ID: {} ", idCliente);
        } catch (NumberFormatException e) {
            logger.info("El ID debe ser un número entero: {}", e.getMessage());
        }
    }

    private void listarClientes() {
        logger.info("--- Listado de Clientes ---");
        var clientes = clienteServicio.listarClientes();
        clientes.forEach(cliente -> logger.info(cliente.toString()));
    }

    public static int mostrarMenu(Scanner sc) {
        logger.info("""             
                1. Listar Clientes
                2. Buscar Cliente
                3. Agregar Cliente
                4. Modificar Cliente
                5. Eliminar Cliente
                6. Salir
                Elije una opcion:""");

        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            logger.info("No ha introducido una opción válida.");
        }
        return -1;
    }
}