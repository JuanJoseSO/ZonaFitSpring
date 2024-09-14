package udemy.zona_fit.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udemy.zona_fit.modelo.Cliente;
import udemy.zona_fit.servicio.ClienteServicio;
import udemy.zona_fit.servicio.IClienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


//Tenemos que agregar esta anotación para que Spring pueda gestionar esta clase como un bean.
@Component
public class ZonaFitForm extends JFrame {
    private JPanel panelPrincipal;
    private JTable clientesTabla;     // Tabla para mostrar los datos de los clientes.
    private JTextField tfNombre;
    private JTextField tfApellido;
    private JTextField tfMembresia;
    private JButton btGuardar;
    private JButton btEliminar;
    private JButton btLimpiar;
    IClienteServicio clienteServicio;
    private DefaultTableModel tablaModeloClientes; // Modelo de la tabla que contiene los datos de los clientes.
    private Integer idCliente;            // Variable para almacenar el ID del cliente seleccionado (si existe).

    /* Constructor con inyección de dependencias. Aquí se inyecta el servicio de cliente, lo hacemos en el constructor
     para tener acceso a la base de datos desde el momento en el que iniciamos nuestra aplicación, es decir,
     que ya aparezcan clientes en la tabla desde un principio. */
    @Autowired
    public ZonaFitForm(IClienteServicio clienteServicio) {
        // Se asigna el servicio inyectado a la variable local.
        this.clienteServicio = clienteServicio;
        // Inicialización del formulario.
        initForm();
        btGuardar.addActionListener(e -> guardarCliente());
        //MouseListener para capturar cuando hacemos clic en un elemento de la tabla.
        clientesTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cargarClienteSeleccionado();
            }
        });
        btEliminar.addActionListener(e -> eliminarCliente());
        btLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void initForm() {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    //Metodo que se ejecuta antes del constructor para crear los componentes de la interfaz.
    private void createUIComponents() {
        /* Inicializa el modelo de la tabla con 0 filas y 4 columnas. Sobrescribe el metodo `isCellEditable` para
            que las celdas no sean editables. */
        this.tablaModeloClientes = new DefaultTableModel(0, 4) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   //Impide que las celdas se puedan editar directamente desde la tabla.
            }
        };
        //Heads de las columnas
        String[] heads = {"ID", "Nombre", "Apellido", "Membresía"};
        this.tablaModeloClientes.setColumnIdentifiers(heads);
        //Asigna el modelo de datos a la tabla.
        this.clientesTabla = new JTable(tablaModeloClientes);
        //Configura la tabla para que solo se pueda seleccionar una fila a la vez.
        this.clientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Cargamos los clientes iniciales
        listarClientes();
    }

    private void listarClientes() {
        // Limpia las filas existentes de la tabla antes de añadir nuevas.
        this.tablaModeloClientes.setRowCount(0);
        // Obtiene la lista de clientes del servicio de cliente.
        var clientes = this.clienteServicio.listarClientes();
        clientes.forEach(cliente -> {
            //Arreglo de tipo objeto por que addRow requiere un array de objetos por parámetro.
            Object[] infoCliente = {cliente.getIdCliente(), cliente.getNombre(), cliente.getApellido(), cliente.getMembresia()};
            // Añade la fila al modelo de la tabla.
            this.tablaModeloClientes.addRow(infoCliente);
        });
    }

    private void guardarCliente() {
        if (tfNombre.getText().isEmpty()) {
            mostrarMensaje("Proporciona un nombre.", "Error!");
            /* Esté metodo hace que el ratón se posicione sobre el elemento de la interfaz de forma
            automática al cerrar el JOptionPane */
            tfNombre.requestFocusInWindow();
            //Usamós el return para salir del metodo guardarCliente directamente
            return;
        }
        if (tfMembresia.getText().isEmpty()) {
            mostrarMensaje("Proporciona una membresía.", "Error!");
            tfMembresia.requestFocusInWindow();
            return;
        }
        //Recuperamos los valores de los TextFields y creamos y guardamos el objeto cliente
        var nombre = tfNombre.getText();
        var apellido = tfApellido.getText();
        try {
            var membresia = Integer.parseInt(tfMembresia.getText());

            /* Esté id solo se inicializa en el metodo cargarClienteSeleccionado, si no, su valor es null
               Si su valor fuera null, crearía un nuevo cliente, si no, actualizaría el existente.
                *Esto lo gestiona el metodo save de ClienteServicio.guardarCliente()
            */

            var cliente = new Cliente(this.idCliente, nombre, apellido, membresia);
            this.clienteServicio.guardarCliente(cliente);
            //Muestra un mensaje dependiendo si se creó o actualizó el cliente.
            if (this.idCliente == null) mostrarMensaje("Se agregó el cliente", "Información!");
            else mostrarMensaje("Se modificó el cliente.", "Error!");

            limpiarFormulario();
            //Volvemos a cargar los clientes
            listarClientes();
        } catch (NumberFormatException e) {
            mostrarMensaje("La membresía debe ser un número entero.", "Error!");
        }
    }

    //Metodo para que los textField se vacíen.
    private void limpiarFormulario() {
        tfMembresia.setText("");
        tfNombre.setText("");
        tfApellido.setText("");

        /* Limpiamos el id del cliente seleccionado para que no se guarde el del cliente anterior en la
           variable Private Integer idCliente.
           Aunque no veamos esta en el formulario, este se queda asignado en el programa.  */
        idCliente = null;
        //Limpiamos también el registro seleccionado de la tabla
        this.clientesTabla.clearSelection();
    }

    private void mostrarMensaje(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarClienteSeleccionado() {
        //Nos devuelve lo que tengamos en esa fila de la tabla en forma de array
        var fila = clientesTabla.getSelectedRow();
        if (fila != -1) {   // Retorna -1 si no se seleccionó ningún registro.
            // Obtiene el ID del cliente de la fila seleccionada y lo guarda en la variable idCliente.
            var id = clientesTabla.getModel().getValueAt(fila, 0).toString();
            this.idCliente = Integer.parseInt(id);

            // Carga el nombre, apellido y membresía del cliente en los campos de texto.
            var nombre = clientesTabla.getModel().getValueAt(fila, 1).toString();
            this.tfNombre.setText(nombre);
            var apellido = clientesTabla.getModel().getValueAt(fila, 2).toString();
            this.tfApellido.setText(apellido);
            var membresia = clientesTabla.getModel().getValueAt(fila, 3).toString();
            this.tfMembresia.setText(membresia);
        }
    }

    private void eliminarCliente() {
        //Verifica si hay una fila seleccionada en la tabla y que el id no sea nulo.
        if (clientesTabla.getSelectedRow() != -1 && idCliente != null) {
            var cliente = new Cliente();
            cliente.setIdCliente(idCliente);
            clienteServicio.eliminarCliente(cliente);
            mostrarMensaje("Cliente con ID" + this.idCliente + "eliminado.", "Información");
            listarClientes();
        } else mostrarMensaje("Debe seleccionar un registro de la tabla.", "Error!");
    }
}