package pe.edu.cibertec.licencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.licencias.entity.DocumentoAdjunto;

public interface IDocumentoAdjunto extends JpaRepository<DocumentoAdjunto, Integer>
{
    List<DocumentoAdjunto> findByExpediente_IdExpediente(Integer idExpediente);
}