package cl.grupobios.fichatecnica.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ReporteUtils {
    @Autowired
    private DataSource dataSource;

    /**
     * Genera la ficha técnica de un paciente en formato PDF
     * @param idPaciente ID del paciente
     * @return byte array con el PDF generado
     */
    public byte[] generarFichaTecnicaPaciente(Long idPaciente) {
        try (Connection connection = dataSource.getConnection()) {
            
            // Cargar el archivo JRXML desde resources
            Resource resource = new ClassPathResource("static/files/Ficha_Tecnica.jrxml");
            InputStream reportStream = resource.getInputStream();
            
            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // Parámetros del reporte
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PACIENTE_ID", idPaciente);
            
            // Llenar el reporte con datos de la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, 
                parameters, 
                connection
            );
            
            // Exportar a PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }
}
