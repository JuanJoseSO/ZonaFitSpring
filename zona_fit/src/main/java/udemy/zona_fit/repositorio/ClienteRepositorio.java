package udemy.zona_fit.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import udemy.zona_fit.modelo.Cliente;

//Debemos indicarle la clase con la que vamos a trabajar y su tipo de clase primaria
public interface ClienteRepositorio extends JpaRepository<Cliente,Integer> {

}
