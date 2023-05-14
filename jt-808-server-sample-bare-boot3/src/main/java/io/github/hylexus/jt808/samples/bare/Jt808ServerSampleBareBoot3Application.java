package io.github.hylexus.jt808.samples.bare;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Jt808ServerSampleBareBoot3Application {

    public static void main(String[] args) {
        SpringApplication.run(Jt808ServerSampleBareBoot3Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(Environment environment) {
        return args -> {
            System.out.println(environment);
        };
    }

}
