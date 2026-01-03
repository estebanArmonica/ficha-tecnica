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
@Table(name = "tipo_sangre")
public class TipoSangre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tipo_sangre")
    @SequenceGenerator(
        name = "seq_tipo_sangre",
        sequenceName = "seq_tipo_sangre",
        allocationSize = 1,
        initialValue = 1
    )
    @Column(name = "id_tipo_sangre")
    private Long id;

    @NotBlank(message = "El nombre del tipo de sangre es obligatorio")
    @Size(min = 1, max = 5, message = "El tipo de sangre debe tener entre 1 y 5 caracteres")
    @Column(name = "nom_tipo_sangre", length = 5, nullable = false)
    private String nombreTipoSangre;
}
