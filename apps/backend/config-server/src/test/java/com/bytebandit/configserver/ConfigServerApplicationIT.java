package com.bytebandit.configserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ConfigServerApplicationIT {

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

}
