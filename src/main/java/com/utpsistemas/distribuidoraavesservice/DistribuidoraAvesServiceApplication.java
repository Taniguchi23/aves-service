package com.utpsistemas.distribuidoraavesservice;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Rol;
import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.repository.RolRepository;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class DistribuidoraAvesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistribuidoraAvesServiceApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner initData(UsuarioRepository usuarioRepo,
//                                      RolRepository rolRepo,
//                                      PasswordEncoder passwordEncoder) {
//        return args -> {
//
//            // 1. Crear roles si no existen
//            Rol rolDistribuidor= rolRepo.findByNombre("Distribuidor").orElseGet(() ->
//                    rolRepo.save(new Rol(null, "Distribuidor")));
//
//            Rol rolVendedor = rolRepo.findByNombre("Vendedor").orElseGet(() ->
//                    rolRepo.save(new Rol(null,"Vendedor")));
//
//            Rol rolCobrador = rolRepo.findByNombre("Cobrador").orElseGet(() ->
//                    rolRepo.save(new Rol(null,"Cobrador")));
//
//            // 2. Crear usuario admin
//            if (usuarioRepo.findByEmailAndEstado("distribuidor@correo.com",'A').isEmpty()) {
//                Usuario admin = new Usuario();
//                admin.setEmail("distribuidor@correo.com");
//                admin.setPassword(passwordEncoder.encode("xyz123"));
//                admin.setEstado('A');
//                admin.setNombres("Distribuidor");
//                admin.setRoles(List.of(rolDistribuidor, rolVendedor, rolCobrador)); // tiene ambos roles
//                usuarioRepo.save(admin);
//            }
//
//            // 3. Crear usuario normal
//            if (usuarioRepo.findByEmailAndEstado("vendedor@correo.com",'A').isEmpty()) {
//                Usuario user = new Usuario();
//                user.setEmail("vendedor@correo.com");
//                user.setPassword(passwordEncoder.encode("xyz123"));
//                user.setEstado('A');
//                user.setNombres("Vendedor");
//                user.setRoles(List.of(rolVendedor)); // solo ROLE_USER
//                usuarioRepo.save(user);
//            }
//
//            if (usuarioRepo.findByEmailAndEstado("cobrador@correo.com",'A').isEmpty()) {
//                Usuario user = new Usuario();
//                user.setEmail("cobrador@correo.com");
//                user.setPassword(passwordEncoder.encode("xyz123"));
//                user.setEstado('A');
//                user.setNombres("Cobrador");
//                user.setRoles(List.of(rolCobrador)); // solo ROLE_USER
//                usuarioRepo.save(user);
//            }
//        };
//    }
}
