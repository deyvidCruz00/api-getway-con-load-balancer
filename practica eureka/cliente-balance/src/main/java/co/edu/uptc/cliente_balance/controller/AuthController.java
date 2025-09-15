package co.edu.uptc.cliente_balance.controller;

import co.edu.uptc.cliente_balance.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Modelo simple para el request de login
    public static class LoginRequest {
        private String username;
        private String password;

        // Constructores
        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Getters y Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Modelo simple para la respuesta del token
    public static class TokenResponse {
        private String token;
        private String type = "Bearer";

        public TokenResponse(String token) {
            this.token = token;
        }

        // Getters y Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Para este ejemplo, vamos a usar credenciales simples hardcodeadas
            // En producción, deberías validar contra una base de datos
            String validUsername = "admin";
            String validPassword = "password"; // En producción esto estaría hasheado

            if (validUsername.equals(loginRequest.getUsername()) && 
                validPassword.equals(loginRequest.getPassword())) {
                
                // Generar token JWT
                String token = jwtUtil.generateToken(loginRequest.getUsername());
                
                return ResponseEntity.ok(new TokenResponse(token));
            } else {
                return ResponseEntity.status(401).body("Credenciales inválidas");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    return ResponseEntity.ok("Token válido para usuario: " + username);
                } else {
                    return ResponseEntity.status(401).body("Token inválido");
                }
            } else {
                return ResponseEntity.status(400).body("Header Authorization inválido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al validar token: " + e.getMessage());
        }
    }
}