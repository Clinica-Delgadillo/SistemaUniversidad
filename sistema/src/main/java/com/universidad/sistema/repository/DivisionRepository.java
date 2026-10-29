package com.universidad.sistema.repository;

import com.universidad.sistema.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Capa de acceso a datos para la entidad Division.
 * Expone las operaciones estándar de JpaRepository (findAll, findById,
 * save, deleteById, existsById) sin consultas SQL propias.
 */
public interface DivisionRepository extends JpaRepository<Division, Long> {
}
