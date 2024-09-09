package es.roberto.ahorcado.controlador;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Arrays;
import java.util.Optional;
import static org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME;

@Slf4j
@Controller
@RequestMapping("/")
public class homeController {
    private static final String CONTADOR_NAME_PARTIDA = "numVisitasPartida";
    private static final String CONTADOR_NAME_INDEX = "numVisitasIndex";

    private static final int COOKIE_MAX_AGE = 604800; // 7 * 24 * 60 * 60 = 604800 (7 días)

    @GetMapping({"","/bienvenida"})
    public String bienvenida(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String usuario = authentication.getName();

            // Si ya hay datos de contador de visitas en la sesión es que no es la primera vez
            // que se pasa por aquí y no incrementamos la cookie
            HttpSession session = request.getSession();
            boolean primeraVez = (session.getAttribute(CONTADOR_NAME_PARTIDA) == null);

            if (primeraVez) {

                // Comprobar si el navegador tenía cookie del usuario
                Optional<Cookie> cookieEncontrada = Arrays.stream(request.getCookies())
                        .filter(cookie -> usuario.equals(cookie.getName()))
                        .findAny();

                // si no existe la cookie el contador de visitas se pone a 1
                int contador = 1;
                if (cookieEncontrada.isEmpty()) {  // si no existe la cookie >> primera visita
                    Cookie cookie = new Cookie(usuario, "1");
                    cookie.setPath("/");
                    cookie.setDomain("localhost");
                    cookie.setMaxAge(COOKIE_MAX_AGE);
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                } else { // si existe la cookie se recupera el contador y se le suma 1
                    Cookie cookie = cookieEncontrada.get();
                    contador = Integer.parseInt(cookie.getValue()) + 1;
                    cookie.setValue(String.valueOf(contador));
                    cookie.setMaxAge(7 * 24 * 60 * 60);  // 7 días
                    response.addCookie(cookie);
                }
                // Almacenar en session el contador de visitas a la aplicación
                session.setAttribute(CONTADOR_NAME_PARTIDA, contador);
                log.info("id de session: {}", session.getId());


            }

            // Almacenar en session el contador de visitas a la pagina index
            Object contadorIndex = session.getAttribute(CONTADOR_NAME_INDEX);
            session.setAttribute(CONTADOR_NAME_INDEX, (contadorIndex == null) ? 1 : (int)contadorIndex + 1);


            log.info("juego preferido: {}", session.getAttribute(LOCALE_SESSION_ATTRIBUTE_NAME));
            log.info("atributo del Jugador {}", LOCALE_SESSION_ATTRIBUTE_NAME);

        }
        return "bienvenida";
    }

      @GetMapping("/login")
      public String login(){
     return "login";
      }

      @GetMapping("/logout")
      public String logout(){
     return "redirect:/login";
      }
}
