package com.azs.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

@Configuration
public class Configs {

    /**
     * Bean for ModelMapper.
     * ModelMapper is used for object mapping between DTOs and Entities.
     * @return ModelMapper instance.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Gson Bean with custom LocalDate deserialization.
     * @return Gson instance.
     */
    @Bean
    public Gson gson() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        JsonDeserializer<LocalDate> localDateDeserializer = (json, type, context) -> 
                LocalDate.parse(json.getAsString(), formatter);

        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, localDateDeserializer)
                .setPrettyPrinting() // Optional: for formatted JSON output
                .create();
    }

    /**
     * ObjectMapper Bean for Jackson.
     * @return ObjectMapper instance.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();

        // Register a custom deserializer for LocalDate with the desired format
        module.addDeserializer(LocalDate.class, 
            new LocalDateDeserializer(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        objectMapper.registerModule(module);
        objectMapper.findAndRegisterModules(); // Ensure all modules are loaded
        return objectMapper;
    }
}
