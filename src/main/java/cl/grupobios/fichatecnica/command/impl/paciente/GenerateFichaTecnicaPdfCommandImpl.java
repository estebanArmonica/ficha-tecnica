package cl.grupobios.fichatecnica.command.impl.paciente;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.utils.ReporteUtils;

public class GenerateFichaTecnicaPdfCommandImpl implements Command<byte[]>{

    private final Long idPaciente;
    private final ReporteUtils reporteUtils;

    public GenerateFichaTecnicaPdfCommandImpl(Long idPaciente, ReporteUtils reporteUtils) {
        this.idPaciente = idPaciente;
        this.reporteUtils = reporteUtils;
    }

    @Override
    public byte[] execute() {
        if (idPaciente == null || idPaciente <= 0) {
            throw new IllegalArgumentException("ID de paciente inválido: " + idPaciente);
        }

        // Usamos directamente tu ReporteUtils existente
        return reporteUtils.generarFichaTecnicaPaciente(idPaciente);
    }
    
}
