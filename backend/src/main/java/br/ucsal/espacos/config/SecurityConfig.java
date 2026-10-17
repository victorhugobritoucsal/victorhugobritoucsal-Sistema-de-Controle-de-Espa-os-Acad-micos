package br.ucsal.espacos.config;

import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.model.Usuario;
import br.ucsal.espacos.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/colaborador/**").hasRole("COLABORADOR")
                        .requestMatchers("/api/usuario/**").hasRole("USUARIO")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, auth) -> response.setStatus(200)));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repository) {
        return login -> {
            Usuario usuario = repository.findByLogin(login)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
            boolean docenteInativo = usuario.getDocente() != null && usuario.getDocente().getStatus() == Status.INATIVO;
            return User.withUsername(usuario.getLogin())
                    .password(usuario.getSenha())
                    .roles(usuario.getPerfis().stream().map(Enum::name).toArray(String[]::new))
                    .disabled(!usuario.isAtivo() || docenteInativo)
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
