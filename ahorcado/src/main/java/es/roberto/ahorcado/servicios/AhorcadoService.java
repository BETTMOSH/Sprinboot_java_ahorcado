package es.roberto.ahorcado.servicios;
import es.roberto.ahorcado.entidad.AhorcadoJuego;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class AhorcadoService {
    private List<AhorcadoJuego> repositorio = new ArrayList<>();

    public void add (AhorcadoJuego aJ){
        repositorio.add(aJ);
    }
    public void delete (int id){
        repositorio.removeIf(partida -> partida.getId() == id);
    }

    public List<AhorcadoJuego> findAll(){
        return repositorio;
    }


    /*@PostConstruct
    public void init() {
        try {
            List<String> palabras = generateRandomWords(14);
            for (int i = 0; i < palabras.size(); i++) {
                repositorio.add(new AhorcadoJuego(i + 1, palabras.get(i)));
            }
        } catch (IOException | JSONException e) {
            // Manejo de excepciones
            e.printStackTrace(); // Imprime el rastro de la excepción en la consola
            // Aquí puedes agregar un log con la excepción para registrar el error
            // throw new RuntimeException("Error en la inicialización", e); // Esto puede lanzar una excepción, asegúrate de manejarla
        }
    }
    public List<String> generateRandomWords(int numberOfWords) throws IOException, JSONException {
        List<String> listado = new ArrayList<>();

        for (int i = 0; i < numberOfWords; i++) {
            StringBuilder result = new StringBuilder();
            String nueva;

            URL url = new URL("https://palabras-aleatorias-public-api.herokuapp.com/random");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (var reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }

            JSONObject jsonarray = new JSONObject(result.toString());
            JSONObject objeto = jsonarray.getJSONObject("body");
            nueva = objeto.getString("Word");
            listado.add(nueva.split(" ")[0]);
        }
        return listado;
    }*/

    //este metodo reemplaza al de arriba ya que daba errores en las excepciones y JSONObjects.
    //genera una lista de palabras aleatorias, en base a un array de palabras
    public List<String> palabrasRa = Arrays.asList( "pelota", "ordenador", "teléfono", "árbol",
            "montaña","playa","estropajo","escarabajo","murcielago", "plumifero");
    @PostConstruct
    public void init() {
        List<String> palabrasR = generaRandom(10);
        for (int i = 0; i < palabrasR.size(); i++) {
            repositorio.add(new AhorcadoJuego(i + 1, palabrasR.get(i)));
        }
    }
    //este metodo genera una lista de palabras aleatorias
    public List<String> generaRandom(int numPalabras) {
        List<String> palabras = new ArrayList<>(palabrasRa);
        // si el numero de palabras es igual o mayor que el tamaño de la lista, devolvemos todas las palabras
        if (numPalabras >= palabras.size()) {
            return new ArrayList<>(palabras); // Devolver todas las palabras si se solicitan más de las existentes
        }
        Collections.shuffle(palabras); // Baraja las palabras
        return palabras.subList(0, numPalabras); // Devuelve 'numPalabras'  barajadas
    }

    //obtiene el id de la última partida, para poder crear una nueva
    public int last(){
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i < repositorio.size()+1; i++) {
            numeros.add(i);
        }
        for (int i = 1; i < repositorio.size()+1; i++) {
            //si al reccorrer el array, hay una partida que no tiene el mismo id que su posición, significa que no existe ese id; lo devolvemos
            if (i != repositorio.get(i-1).getId() && !numeros.contains(i)){
                return i;
            }
        }
        //le sumamos 1 al tamaño del array, ya que si no ha entrado en el if anterior,
        // significa que el id que falta es el siguiente al último
        return repositorio.size()+1;
    }

    //obtiene número aleatorio para seleccionar una partida (en base al tamaño del array de partidas)
    // nota: este codigo no he terminado de entender como funciona, lo he sacado de (chat GPT)
    public int getRandom(){
        //int n = (int) (Math.random() * (<número_máximo + 1> - <número_mínimo>)) + <numero_mínimo>;
        int longitud = repositorio.size() ;
        return (int) (Math.random() * (longitud + 1 - 1) + 1);
    }

    //obtiene la partida en base a su id (que es el mismo que su posición en el array)
    public AhorcadoJuego obtenerPartida(int id){
        int i;
        for (i = 0; i <= repositorio.size(); i++) {
            if (repositorio.get(i).getId() == id)
                break;
        }
        return repositorio.get(i);
    }

    //comprueba si la letra es acertada o no
    public boolean compruebaLetra(String palabra, char letra){
        for (int i = 0; i < palabra.length(); i++) {
            //comparamos las letras contienen tildes
            if (Objects.equals(manejoTildes(String.valueOf(palabra.charAt(i))), manejoTildes(String.valueOf(letra))))
                return true;
        }
        return false;
    }
    //manejo de tildes, para que no afecten a la comparación de letras
    public static String manejoTildes(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }


    //comprobamos si ha acertado la letra usando otros métodos, y realizamos los ajustes pertinentes (número de intentos y nuevo estado)
    public AhorcadoJuego manejarPartida(AhorcadoJuego partida){
        char nuevaLetra = partida.getNuevaLetra().charAt(0);
        String palabra = partida.getPalabraOculta();
        //si ha adivinado la letra, actualizamos el estado. En caso contrario, la añadimos a falladas y restamos un intento (solo si no se habia intentado antes esa letra)
        if (compruebaLetra(palabra, nuevaLetra)){
            String estado = actualizarEstado(palabra, partida.getEstado(), nuevaLetra) ;
            partida.setEstado(estado);
        } else {
            String falladas = partida.getLetrasFalladas();
            //si la lista de falladas no contiene la letra introducida con o sin tilde
            if (!falladas.contains("" + nuevaLetra)) {
                if (falladas.length() == 0)
                    partida.setLetrasFalladas(falladas + nuevaLetra);
                else {
                    partida.setLetrasFalladas(falladas + "-" + nuevaLetra);
                    partida.setIntentos(partida.getIntentos() - 1);
                }
            }
        }
        partida.setNuevaLetra(""); //vaciamos el valor de la letra
        return partida;
    }
    /*public AhorcadoJuego manejarPartida(AhorcadoJuego partida) {
        char nuevaLetra = partida.getNuevaLetra().charAt(0);
        String palabra = partida.getPalabraOculta();
        if (compruebaLetra(palabra, nuevaLetra)) {
            partida.setEstado(actualizarEstado(palabra, partida.getEstado(), nuevaLetra));
        } else {
            String falladas = partida.getLetrasFalladas();
            if (!falladas.contains("" + nuevaLetra)) {
                partida.setLetrasFalladas(falladas + (falladas.isEmpty() ? "" : "-") + nuevaLetra);
                if (!falladas.isEmpty()) {
                    partida.setIntentos(partida.getIntentos() - 1);
                }
            }
        }
        partida.setNuevaLetra("");
        return partida;
    }*/


    //crea un String que forma la palabra con las letras acertadas y con guiones en los caracteres no adivinados
    public String actualizarEstado(String palabra, String estado, char nuevaLetra){
        String nuevoEstado = "";
        for (int i = 0; i < palabra.length(); i++) {
            char letraActual = palabra.charAt(i);
            //comprobamos si ambas letras son iguales sin tildes
            if (Objects.equals(manejoTildes(String.valueOf(letraActual)), manejoTildes(String.valueOf(nuevaLetra)))){
                nuevoEstado += letraActual;
            }else if(Objects.equals(manejoTildes(String.valueOf(estado.charAt(i))), manejoTildes(String.valueOf(letraActual)))) {
                nuevoEstado += letraActual;
            }else {
                nuevoEstado += '_';
            }
        }
        return nuevoEstado;
    }

    //asigna la clase de css que se dará a la imagen (representa los intentos)
    //por cada intento fallido, se le asigna una clase(imagen) distinta

    //He añadido 7 imagenes y tuve que cambiar el orden para que se mostraran correctamente.
    public String asignaImg(int intentos){
        String img = "";
        switch (intentos){
            case 6:
                img = "uno";
                break;
            case 5:
                img = "dos";
                break;
            case 4:
                img = "tres";
                break;
            case 3:
                img = "cuatro";
                break;
            case 2:
                img = "cinco";
                break;
                case 1:
                    img = "seis";
                    break;
            default:
                img = "siete";
        }
        return img;
    }

}
