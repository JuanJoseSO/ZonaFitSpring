package udemy.zona_fit;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import udemy.zona_fit.gui.ZonaFitForm;

import javax.swing.*;

//@SpringBootApplication
public class ZonaFitSwing {
    public static void main(String[] args) {
        //Iniciamos el modo oscuro, esto debe hacerse antes de cargar cualquier componente de la interfaz gráfica.
        FlatDarculaLaf.setup();
        /* Creamos el contexto de la aplicación Spring y desactivamos el modo web. Especificamos "headless(false)"
            porque necesitamos mostrar interfaces gráficas (GUIs).
        */
        ConfigurableApplicationContext contexSpring = new SpringApplicationBuilder(ZonaFitSwing.class)
                .headless(false) // Necesario para GUI
                .web(WebApplicationType.NONE) // No es una aplicación web, es una aplicación de escritorio
                .run(args); // Ejecutamos la aplicación con los argumentos pasados

        /* Aquí, usamos "invokeLater" de SwingUtilities para asegurar que la GUI se cree en el hilo de eventos
            de Swing. Esto es importante en las aplicaciones basadas en Swing para evitar problemas de concurrencia.  */
        SwingUtilities.invokeLater(() -> {
            /*Con Spring y el metodo getBean(), Spring resuelve automáticamente las dependencias de los objetos.
            Cuando llamamos a getBean(ZonaFitForm.class), Spring:
                    *Primero busca una instancia de ZonaFitForm.
                    *Luego revisa que ZonaFitForm necesita una implementación de IClienteServicio.
                    *Después crea o busca una instancia de IClienteServicio en el contenedor.
                    *Finalmente, inyecta esta instancia en el constructor de ZonaFitForm.*/
            ZonaFitForm zonaFitForm = contexSpring.getBean(ZonaFitForm.class);
            zonaFitForm.setVisible(true);
        });
    }
}
