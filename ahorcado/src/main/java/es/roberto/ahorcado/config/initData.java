package es.roberto.ahorcado.config;

import es.roberto.ahorcado.controlador.AhorcadoController;
import es.roberto.ahorcado.storage.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Configuration
@RequiredArgsConstructor
public class initData {

    private final StorageService storageService;

    private static final List<String> PALABRAS = Arrays.asList("casa", "perro", "gato", "pelota", "sol", "lápiz", "mesa", "silla", "ordenador", "teléfono", "árbol", "montaña", "río", "playa");

    @PostConstruct
    public void init() {
        List<String> palabras = generateRandomWords(10);
        storageService.init(palabras);
    }

    public List<String> generateRandomWords(int numeroPalabras) {
        List<String> palabras = new ArrayList<>(PALABRAS); // Crear una copia para evitar modificar la lista original

        if (numeroPalabras >= palabras.size()) {
            return new ArrayList<>(palabras); // Devuelve todas las palabras si se solicitan más de las existentes
        }

        Collections.shuffle(palabras); // Barajar las palabras

        return palabras.subList(0, numeroPalabras); // Devolver 'numberOfWords' palabras barajadas
    }

}


