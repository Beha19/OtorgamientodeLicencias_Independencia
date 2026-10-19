package pe.edu.cibertec.licencias.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.Feriado;

public interface IFeriado extends JpaRepository<Feriado, LocalDate>
{
    List<Feriado> findByFechaBetween(LocalDate desde, LocalDate hasta);
}