package pe.edu.cibertec.licencias.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.licencias.entity.Feriado;

@Repository
public interface IFeriado extends JpaRepository<Feriado, LocalDate>
{

}