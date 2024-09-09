package es.roberto.ahorcado.controlador;
import es.roberto.ahorcado.entidad.AhorcadoJuego;
import es.roberto.ahorcado.servicios.AhorcadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/")
public class AhorcadoController {

    public final AhorcadoService servicio;


    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("listaPartidas", servicio.findAll());
        return "index";
    }
    // metodo para obtener las palabras de la clase servicio (generaRandom)
   @GetMapping("/palabras")
    public String obtenerPalabras(Model model) {
    model.addAttribute("listaPalabras", servicio.palabrasRa);
        return "redirect:/partida/jugar/";
    }
    // este metodo inicializa la partida,
    @GetMapping("/partida/jugar/{id}")
    public String comienzaPartida(@PathVariable("id") int id, Model model) {
        AhorcadoJuego aJ = servicio.obtenerPartida(id);
        String claseImagen = servicio.asignaImg(aJ.getIntentos());
        model.addAttribute("claseImagen", claseImagen);

        model.addAttribute("partida", aJ);
        return "partida";
    }

    @GetMapping("/partida/borrar/{id}")
    public String borrarPartida(@PathVariable("id") int id) {
        servicio.delete(id);
        return "redirect:/index";
    }
    // metodo para obtener una partida aleatoria o iniciar una nueva del listado de palabras.
    @GetMapping("/partida/jugar/random")
    public String partidaRandom() {
        int idPartida = servicio.getRandom();
        return "redirect:/partida/jugar/" + idPartida;
    }

   /* @PostMapping("/partida/nuevaLetra")
    public String jugada(@ModelAttribute("partida") AhorcadoJuego partida, Model model) {
        //si el caracter introducido es una letra, procedemos a realizar la partida
        Pattern regex = Pattern.compile("[a-zA-ZÀ-ÿ\u00f1\u00d1]");
        Matcher mat = regex.matcher(partida.getNuevaLetra());
        //si la letra es correcta, se añade a la lista de letras usadas y se comprueba si está en la palabra
        if (mat.matches()) {
            partida = servicio.manejarPartida(partida);
            model.addAttribute("noLetra", "");
        } else {
            model.addAttribute("noLetra", "Solo se aceptan letras");
            partida.setNuevaLetra(""); //una vez ingresada la letra limpia el campo de la nueva letra
            partida.setIntentos(partida.getIntentos() + 1);
        }
        //model.addAttribute("partida", partida);
        //si se acaban los intentos o se acierta la palabra, se redirige a la vista final
        if (partida.getIntentos() == 0 || Objects.equals(partida.getEstado(), partida.getPalabraOculta()))
            return "findejuego";
        //se asigna la imagen correspondiente a los intentos restantes
        String claseImagen = servicio.asignaImg(partida.getIntentos());
        model.addAttribute("claseImagen", claseImagen);
        return "partida";
    }*/
    //se ha intentado corregir el error que al ingresar un numero desaparecia la imagen y
    // se ha conseguido poniendo un "PATTERN" en el atributo "nuevaLetra" de la entidad AhorcadoJuego.

@PostMapping("/partida/nuevaLetra")
    public String añadePalabra(@ModelAttribute("partida") AhorcadoJuego partida, Model model) {
        String nuevaLetra = partida.getNuevaLetra();
        if (nuevaLetra.matches("[a-zA-ZÀ-ÿ\u00f1\u00d1]")) {
            partida = servicio.manejarPartida(partida);
            model.addAttribute("noLetra", "");
        } else {
            model.addAttribute("noLetra", "Solo se aceptan letras");
            partida.setNuevaLetra("");
        }
        if (partida.getIntentos() == 0 || Objects.equals(partida.getEstado(), partida.getPalabraOculta())) {
            return "findejuego";
        }
        String claseImagen = servicio.asignaImg(partida.getIntentos());
        model.addAttribute("claseImagen", claseImagen);
        return "partida";
    }

    // metodo para añadir una nueva partida
    @PostMapping("/addPartida")
    public String añadePalabra(@RequestParam String palabra, Model model) {
        //si el carcter introducido es una letra, procedemos a realizar la partida
        Pattern regex = Pattern.compile("[a-zA-ZÀ-ÿ\u00f1\u00d1]*");
        Matcher mat = regex.matcher(palabra);
        //si la letra es correcta, se añade a la lista de letras usadas y se comprueba si está en la palabra
        if (mat.matches() && !palabra.isEmpty()) {
            servicio.add(new AhorcadoJuego(servicio.last(), palabra));
            model.addAttribute("mensaje", "");
        } else {
            model.addAttribute("mensaje", "La palabra solo puede contener letras");
        }
        model.addAttribute("listaPartidas", servicio.findAll());
        return "index";
    }


}


