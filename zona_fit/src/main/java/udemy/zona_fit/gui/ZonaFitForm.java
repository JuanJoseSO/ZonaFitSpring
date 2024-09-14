package udemy.zona_fit.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udemy.zona_fit.modelo.Cliente;
import udemy.zona_fit.servicio.ClienteServicio;
import udemy.zona_fit.servicio.IClienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


//Tenemos que agregar esta anotación para que pueda acceder a Spring
@Component
public class ZonaFitForm extends JFrame {
    private JPanel panelPrincipal;
    private JTable clientesTabla;
    private JTextField tfNombre;
    private JTextField tfApellido;
    private JTextField tfMembresia;
    private JButton btGuardar;
    private JButton btEliminar;
    private JButton btLimpiar;
    IClienteServicio clienteServicio;
    private DefaultTableModel tablaModeloClientes;
    private Integer idCliente;

    /* Injección de dependencias, lo hacemos en el constructor para tener acceso a la base de datos desde el momento
       en el que iniciamos nuestra aplicación, es decir, que ya aparezcan clientes en la tabla desde un principio. */
    @Autowired
    public ZonaFitForm(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
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
    }

    private void initForm() {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    //Metodo que se ejecuta ANTES que el constructor de la clase.
    private void createUIComponents() {
        this.tablaModeloClientes = new DefaultTableModel(0, 4);
        String[] heads = {"ID", "Nombre", "Apellido", "Membresía"};
        this.tablaModeloClientes.setColumnIdentifiers(heads);
        this.clientesTabla = new JTable(tablaModeloClientes);

        //Cargmos los clientes iniciales
        listarClientes();

    }

    private void listarClientes() {
        this.tablaModeloClientes.setRowCount(0);
        var clientes = this.clienteServicio.listarClientes();
        clientes.forEach(cliente -> {
            //Arreglo de tipo objeto por que addRow requiere un array de objetos por parámetro.
            Object[] infoCliente = {cliente.getIdCliente(), cliente.getNombre(), cliente.getApellido(), cliente.getMembresia()};
            this.tablaModeloClientes.addRow(infoCliente);
        });
    }


    private void guardarCliente() {
        if (tfNombre.getText().isEmpty()) {
            mostrarMensaje("Proporciona un nombre.");
            /* Esté metodo hace que el ratón se posicione sobre el elemento de la interfaz de forma
            automática al cerrar el JOptionPane */
            tfNombre.requestFocusInWindow();
            //Usamós el return para salir del metodo guardarCliente directamente
            return;
        }
        if (tfMembresia.getText().isEmpty()) {
            mostrarMensaje("Proporciona una membresía.");
            tfMembresia.requestFocusInWindow();
            return;
        }
        //Recuperamos los valores de los TextFields y creamos y guardamos el objeto cliente
        var nombre = tfNombre.getText();
        var apellido = tfApellido.getText();
        try {
            var membresia = Integer.parseInt(tfMembresia.getText());

            /* Esté id solo se inicializa en el metodo cargarClienteSeleccionado, si no, su valor es null
               Si su valor fuera null,crearía un nuevo cliente, si no, actualizaría el existente.
                *Esto lo gestiona el metodo save de ClienteServicio.guardarCliente()
            */

            var cliente = new Cliente(this.idCliente, nombre, apellido, membresia);
            this.clienteServicio.guardarCliente(cliente);
            if (this.idCliente == null) mostrarMensaje("Se agregó el cliente");
            else mostrarMensaje("Se modificó el cliente.");

            limpiarFormulario();
            //Volvemos a cargar los clientes
            listarClientes();
        } catch (NumberFormatException e) {
            mostrarMensaje("La membresía debe ser un número entero.");
        }
    }

    //Metodo para que los textField se vacien.
    private void limpiarFormulario() {
        tfMembresia.setText("");
        tfNombre.setText("");
        tfApellido.setText("");
        /* Limpiamos el id del cliente seleccionado para que no se guarde el del cliente anterior en la
           variable Privata Integer idCliente.
           Aunque no veamos esta en el formulario, este se queda asignado en el programa.
        */
        idCliente = null;
        //Limpiamos también el registro seleccionado de la tabla
        this.clientesTabla.clearSelection();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarClienteSeleccionado() {
        //Nos devuelve lo que tengamos en esa fila de la tabla en forma de array
        var fila = clientesTabla.getSelectedRow();
        if (fila != -1) {   // Retorna -1 si no se seleccionó ningun registro.
            var id = clientesTabla.getModel().getValueAt(fila, 0).toString();
            this.idCliente = Integer.parseInt(id);
            var nombre = clientesTabla.getModel().getValueAt(fila, 1).toString();
            this.tfNombre.setText(nombre);
            var apellido = clientesTabla.getModel().getValueAt(fila, 2).toString();
            this.tfApellido.setText(apellido);
            var membresia = clientesTabla.getModel().getValueAt(fila, 3).toString();
            this.tfMembresia.setText(membresia);
        }
    }
}

