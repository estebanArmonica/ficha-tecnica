package cl.grupobios.fichatecnica.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registrar módulo para manejar Hibernate
        mapper.registerModule(new Hibernate5JakartaModule()
            .configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false)
            .configure(Hibernate5JakartaModule.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true)
            .configure(Hibernate5JakartaModule.Feature.WRITE_MISSING_ENTITIES_AS_NULL, true
        ));
        
        // 2. Módulo para Java Time (LocalDate, LocalDateTime, etc.)
        mapper.registerModule(new JavaTimeModule());

        // Configuraciones adicionales
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        return mapper;
    }
}
