package pe.edu.cibertec.licencias.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pe.edu.cibertec.licencias.entity.Expediente;


// lezama
@Getter
@AllArgsConstructor
public class BandejaFila
{
    private Expediente expediente;
    private Long diasRestantes;          
    private String semaforo;             

    public static BandejaFila de(Expediente e)
    {
        if (e.getFechaLimite() == null)
        {
            return new BandejaFila(e, null, "s-verde");
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), e.getFechaLimite());
        String color = dias <= 3 ? "s-rojo" : (dias <= 7 ? "s-amarillo" : "s-verde");
        return new BandejaFila(e, dias, color);
    }
}
