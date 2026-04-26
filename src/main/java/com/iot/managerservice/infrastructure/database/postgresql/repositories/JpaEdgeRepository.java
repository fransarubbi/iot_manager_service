package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.EdgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link EdgeEntity}.
 * <p>
 * Maneja el acceso a datos para la tabla {@code edges}. Al extender {@link JpaRepository},
 * hereda de forma transparente todos los métodos básicos (CRUD, paginación y ordenamiento)
 * para guardar, eliminar, buscar por ID y listar todos los registros, sin necesidad
 * de escribir sentencias SQL nativas ni configuraciones complejas de Hibernate.
 * </p>
 */
@Repository
public interface JpaEdgeRepository extends JpaRepository<EdgeEntity, String> {  }