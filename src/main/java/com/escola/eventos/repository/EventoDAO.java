package com.escola.eventos.repository;

import com.escola.eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoDAO extends JpaRepository<Evento, Long> {
    // JpaRepository já fornece: findAll, findById, save, deleteById, etc.
}
