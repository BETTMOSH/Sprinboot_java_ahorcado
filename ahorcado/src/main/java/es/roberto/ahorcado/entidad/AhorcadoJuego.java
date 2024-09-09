package es.roberto.ahorcado.entidad;


import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class AhorcadoJuego {
    private Long id;
    private String palabraOculta;
    private String estado;
    private String letrasFalladas;
    // no se aceptan numeros ni caracteres especiales
    @Pattern(regexp = "[a-zA-Z]", message = "Solo se aceptan letras")
    private String nuevaLetra;
    private int intentos;


    public AhorcadoJuego(long id, String palabraOculta) {
        this.id = id;
        this.palabraOculta = palabraOculta;
        this.letrasFalladas = " ";
        this.estado = creaEstado();
        this.intentos = 6;
    }

    // metodo para comprobar si la letra introducida esta en la palabra oculta.
    private String creaEstado(){
        String estado = " ";
        for (int i = 0; i < palabraOculta.length(); i++) {
            estado = estado + "_";
        }
        return estado;
    }

}

