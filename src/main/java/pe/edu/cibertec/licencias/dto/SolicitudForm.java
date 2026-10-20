package pe.edu.cibertec.licencias.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class SolicitudForm
{
    // ---- I. Modalidad del trámite ----
    private String modalidad = "INDETERMINADA";            // INDETERMINADA | TEMPORAL
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFinVigencia;                    // solo si es TEMPORAL

    // ---- II. Solicitante (persona natural o jurídica) ----
    private String tipoPersona = "NATURAL";                // NATURAL | JURIDICA
    private String ruc;                                    // solo si es JURIDICA
    private String razonSocial;                            // solo si es JURIDICA

    // ---- III. Representante (opcional) ----
    private boolean conRepresentante;
    private String repDni;
    private String repNombres;
    private String repApellidos;
    private String repTipoPoder;                           // CARTA_PODER_LEGALIZADA | VIGENCIA_PODER

    // ---- IV. Establecimiento ----
    @NotBlank(message = "Escribe el nombre comercial")
    private String nombreComercial;

    @NotNull(message = "Elige el giro")
    private Integer idGiro;

    private String actividad;
    private String autorizacionSectorial;

    @NotBlank(message = "Elige el tipo de vía")
    private String viaTipo;                                // AV | JR | CALLE | PSJE

    @NotBlank(message = "Escribe el nombre de la vía")
    private String viaNombre;

    @NotBlank(message = "Escribe el número")
    private String numero;

    private String interior;
    private String referencia;

    @NotNull(message = "Escribe el área")
    @DecimalMin(value = "0.01", message = "El área debe ser mayor a 0")
    @DecimalMax(value = "100", message = "Este trámite es solo para locales de hasta 100 m²")
    private BigDecimal areaM2;

    // La zona NO la elige el usuario: el sistema la calcula con el punto del mapa
    @NotNull(message = "Marca la ubicación del local en el mapa")
    private BigDecimal latitud;

    @NotNull(message = "Marca la ubicación del local en el mapa")
    private BigDecimal longitud;

    // ---- Declaración jurada de condiciones de seguridad ----
    private Integer aforo;
    private Integer antiguedadAnios;
    private BigDecimal areaTechadaM2;
    private Map<Integer, Boolean> respuestas = new HashMap<>();   // idCondicion -> cumple
    @AssertTrue(message = "Debes aceptar la declaración jurada")
    private boolean aceptaDj;
}