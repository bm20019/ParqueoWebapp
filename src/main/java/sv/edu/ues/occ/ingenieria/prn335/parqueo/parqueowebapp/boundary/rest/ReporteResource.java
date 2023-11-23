package sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.boundary.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import jakarta.annotation.Resource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.net.URL;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

// import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

@Path("reporte")
public class ReporteResource implements Serializable {
    
    @Resource(lookup = "jdbc/pgdb")
    DataSource poolDeConexiones;

    @GET
    @Path("/reporte")
    public Response generarReporte(@QueryParam("reporte") String nombreReporte){
        String pathReporte=null;
        HashMap parametros =  new HashMap();
//        nombreReporte = "tipo_espacio";
        switch (nombreReporte) {
            case "tipo_espacio":
                // URL urlreporte = getClass().getResource("/reportes/PRN335-2023/HolaMundo.jasper");
                
                // pathReporte = urlreporte.getPath();
                pathReporte = "/reportes/PRN335_2023/HolaMundo.jasper";
                parametros.put("FirmadoPor", "Rogelio Valiente");
                break;
            case "area":
                pathReporte = "/reportes/PRN335_2023/Area.jasper";
                URL pathSubreportes = ReporteResource.class.getResource(pathReporte);
                String pathSubreporteString = pathSubreportes.getPath();
                parametros.put("PATH_SUBREPORTES", pathSubreporteString.substring(0, pathSubreporteString.lastIndexOf("/"))+"/");
                
                break;    
            default:
                return Response.status(Response.Status.NOT_FOUND)
                        .header("report-not-found", nombreReporte)
                        .entity(Entity.text( "No se encuentra el reporte"))
                        .build();
        }
        if(pathReporte!=null){
        
            try {
                //Llenar el reporte
                InputStream fuenteReporte = ReporteResource.class.getResourceAsStream(pathReporte);
                JasperPrint impresor = JasperFillManager.fillReport(fuenteReporte, parametros, poolDeConexiones.getConnection());
                SimplePdfExporterConfiguration configuracion = new SimplePdfExporterConfiguration();
                configuracion.setMetadataAuthor("Universidad de El Salvador");
                configuracion.setMetadataCreator("Sistema de Parque UES");
                configuracion.setMetadataTitle("Reporte -"+nombreReporte);
                //exportar a un stream
                JRPdfExporter exportador = new JRPdfExporter();
                exportador.setConfiguration(configuracion);
                exportador.setExporterInput(new SimpleExporterInput(impresor));

                StreamingOutput stream = new StreamingOutput() {

                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        exportador.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
                        try{
                            exportador.exportReport();
                        }catch (Exception ex){
                            Logger.getLogger(ReporteResource.class.getName()).log(Level.SEVERE, "No pudo llenar el reporte", ex);
                        }
                    }                   
                };

                // Obtener un stream para rest

                // Devolver el stream para rest en el response
                return Response.ok(stream, "application/pdf").build();

            } catch (Exception ex) {
                Logger.getLogger(ReporteResource.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        return Response.serverError().build();
    }
}