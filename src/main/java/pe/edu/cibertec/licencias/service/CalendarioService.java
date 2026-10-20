package pe.edu.cibertec.licencias.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.licencias.entity.Feriado;
import pe.edu.cibertec.licencias.repository.IFeriado;


@Service
@RequiredArgsConstructor
public class CalendarioService
{
    private final IFeriado feriados;

    //FECHA EN QUE VENCE UN PLAZO DE DÍAS HÁBILES (LUNES A VIERNES, SIN FERIADOS). 
    public LocalDate sumarDiasHabiles(LocalDate desde, int dias)
    {
        // UNA SOLA CONSULTA: LOS FERIADOS DE UNA VENTANA AMPLIA
        LocalDate tope = desde.plusDays(dias * 3L + 10);
        Set<LocalDate> sinLaborar = feriados.findByFechaBetween(desde, tope).stream()
                .map(Feriado::getFecha)
                .collect(Collectors.toSet());

        LocalDate fecha = desde;
        int contados = 0;
        while (contados < dias)
        {
            fecha = fecha.plusDays(1);              // EL DÍA DEL ENVÍO NO CUENTA
            if (esHabil(fecha, sinLaborar))
            {
                contados++;
            }
        }
        return fecha;
    }

    private boolean esHabil(LocalDate fecha, Set<LocalDate> feriados)
    {
        DayOfWeek d = fecha.getDayOfWeek();
        return d != DayOfWeek.SATURDAY && d != DayOfWeek.SUNDAY && !feriados.contains(fecha);
    }
}