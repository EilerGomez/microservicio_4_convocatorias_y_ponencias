package com.eiler.microservicio_4_convocatorias_ponencias;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Microservicio4ConvocatoriasPonenciasApplication {
        @PostConstruct
        public void init() {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Guatemala"));
        }

	public static void main(String[] args) {
		SpringApplication.run(Microservicio4ConvocatoriasPonenciasApplication.class, args);
	}

}
