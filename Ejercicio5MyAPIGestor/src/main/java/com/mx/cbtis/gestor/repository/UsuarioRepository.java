package com.mx.cbtis.gestor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mx.cbtis.gestor.modelo.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario,Integer> {
	
	@Query("select u from Usuario u where u.correo_electronico = :correo")
	Optional<Usuario> findByCorreo(@Param("correo") String correo);
                
}
