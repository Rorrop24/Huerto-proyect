package cl.duoc.huerto.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

// Levanta el proyecto completo con una base H2 en memoria (modo MySQL) en vez de XAMPP.
// Así se comprueba que Flyway crea las tablas, que las entidades coinciden con ellas y que los endpoints responden.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:huerto;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
@DirtiesContext
class HuertoApiIntegracionTest {
    @Autowired MockMvc mvc;

    @Test
    void flywayCargaLosDatosInicialesYLosEndpointsResponden() throws Exception {
        mvc.perform(get("/api/v1/sectores")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/api/v1/cultivos").param("sectorId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/v1/tareas").param("responsableId", "USR-002")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void crudCompletoDeUnSector() throws Exception {
        String creado = mvc.perform(post("/api/v1/sectores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Sector de prueba\",\"ubicacion\":\"Patio sur\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("EN_USO")))
                .andReturn().getResponse().getContentAsString();
        String id = creado.replaceAll(".*\"id\":(\\d+).*", "$1");

        mvc.perform(put("/api/v1/sectores/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Sector cambiado\",\"ubicacion\":\"Patio sur\",\"estado\":\"FUERA_DE_USO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Sector cambiado")));
        mvc.perform(delete("/api/v1/sectores/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/sectores/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void respondeConElMensajeDeLaReglaDeNegocio() throws Exception {
        mvc.perform(post("/api/v1/cultivos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreComun\":\"Papa\",\"sectorId\":1,\"frecuenciaRiegoDias\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", is("Usa un número entre 1 y 30.")));
        mvc.perform(delete("/api/v1/sectores/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje", is("Primero elimina los cultivos, tareas y problemas de este sector.")));
        mvc.perform(put("/api/v1/tareas/2/entrega").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fotografia\":\"content://foto/2\",\"comentario\":\"Saqué las malezas\",\"estudianteId\":\"USR-003\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje", is("Solo el estudiante asignado puede entregar esta tarea.")));
    }

    @Test
    void flujoDeEntregaYRevisionDeUnaTarea() throws Exception {
        mvc.perform(put("/api/v1/tareas/1/entrega").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fotografia\":\"content://foto/1\",\"comentario\":\"Regué las lechugas\",\"estudianteId\":\"USR-002\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("PENDIENTE_DE_VALIDACION")));
        mvc.perform(put("/api/v1/tareas/1/revision").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aprobada\":true,\"docenteId\":\"USR-001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("VALIDADA")));
    }

    @Test
    void reporteYSeguimientoDeUnProblema() throws Exception {
        mvc.perform(post("/api/v1/problemas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sectorId\":2,\"cultivoId\":2,\"tipo\":\"FALTA_AGUA\",\"descripcion\":\"Tierra muy seca\",\"autorId\":\"USR-002\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("INFORMADO")));
        mvc.perform(put("/api/v1/problemas/1/seguimiento").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ATENDIDO\",\"comentario\":\"Se regó el sector\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ATENDIDO")));
    }
}
