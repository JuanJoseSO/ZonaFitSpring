package udemy.zona_fit.modelo;

import jakarta.persistence.*;
import lombok.*;

//Tendremos que ir usando las diferentes anotaciones.
@Entity
@Data    //Genera los Getter/Setter de los atributos de forma automatica
@NoArgsConstructor  //Agrega el constructor por defecto
@AllArgsConstructor //Agrega el constructor con todos los parámetros, no tenemos la libertad de elegir solo x parámetros.
@ToString
@EqualsAndHashCode
public class Cliente {
    /* Usamos el envoltorio Integer para usar objetos y no tipos primitivos,ya que el envoltorio no tiene un valor
       null por defecto
       ID y Auto incremental*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente")
    private Integer idCliente;

    private String nombre;
    private String apellido;
    private Integer membresia;
}