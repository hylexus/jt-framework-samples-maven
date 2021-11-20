package io.github.hylexus.jt808.samples.mixedversion;

import io.github.hylexus.jt808.boot.annotation.EnableJt808ServerAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hylexus
 */
@SpringBootApplication
@EnableJt808ServerAutoConfig
public class Jt808ServerSampleMixedVersionApplication {

    public static void main(String[] args) {
        SpringApplication.run(Jt808ServerSampleMixedVersionApplication.class, args);
    }

}
