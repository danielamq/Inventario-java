package dharmaInventario.dharmaInventario.infrastructure;

import dharmaInventario.dharmaInventario.domain.Repository.UsuarioRepository;
import dharmaInventario.dharmaInventario.domain.Utils.JwtUtil;
import dharmaInventario.dharmaInventario.domain.model.DTO.LoginRequest;
import dharmaInventario.dharmaInventario.domain.model.DTO.LoginResponse;
import dharmaInventario.dharmaInventario.domain.model.Entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var usuario = usuarioRepository.findByUsuario(request.getUsuario())
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Credenciales inválidas", null));
        }

        String token = jwtUtil.generarToken(usuario.getUsuario());
        return ResponseEntity.ok(new LoginResponse("Login exitoso", token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody UsuarioEntity nuevoUsuario) {
        if (usuarioRepository.findByUsuario(nuevoUsuario.getUsuario()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LoginResponse("El usuario ya existe", null));
        }

        nuevoUsuario.setContrasena(passwordEncoder.encode(nuevoUsuario.getContrasena()));
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse("Usuario registrado correctamente", null));
    }
}
