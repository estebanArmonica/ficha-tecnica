package cl.grupobios.fichatecnica.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "genero")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_genero")
    @SequenceGenerator(
        name = "seq_genero",
        sequenceName = "seq_genero",
        allocationSize = 1,
        initialValue = 1
    )
    @Column(name = "id_genero")
    private Long id;

    @NotBlank(message = "El nombre del género es obligatorio")
    @Size(min = 1, max = 20, message = "El nombre del género debe tener entre 1 y 20 caracteres")
    @Column(name = "nom_genero", length = 20, nullable = false)
    private String nombreGenero;


    @NotBlank(message = "La sigla es obligatoria")
    @Size(min = 1, max = 1, message = "La sigla debe tener exactamente 1 carácter")
    @Column(name = "sigla", length = 1, nullable = false)
    private String sigla;
}
