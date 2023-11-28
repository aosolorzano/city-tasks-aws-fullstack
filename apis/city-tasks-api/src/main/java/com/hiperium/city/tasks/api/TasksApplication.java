package com.hiperium.city.tasks.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.configurations.hints.HibernateProxyHints;
import com.hiperium.city.tasks.api.configurations.hints.QuartzHints;
import com.hiperium.city.tasks.api.configurations.hints.ResourceBundleHints;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.dto.TaskExecutionDTO;
import com.hiperium.city.tasks.api.executions.JobExecution;
import com.hiperium.city.tasks.api.utils.PropertiesUtil;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVO;
import com.hiperium.city.tasks.api.vo.AwsPropertiesVO;
import lombok.SneakyThrows;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.nio.charset.StandardCharsets;

@EnableWebFlux
@EnableScheduling
@EnableWebFluxSecurity
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties(AwsPropertiesVO.class)
@ImportRuntimeHints({QuartzHints.class, HibernateProxyHints.class, ResourceBundleHints.class})
@RegisterReflectionForBinding({AuroraSecretsVO.class, TaskExecutionDTO.class, ErrorDetailsDTO.class, JobExecution.class})
public class TasksApplication {

    @SneakyThrows(JsonProcessingException.class)
    public static void main(String[] args) {
        PropertiesUtil.setApplicationProperties();
        SpringApplication.run(TasksApplication.class, args);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.ISO_8859_1.name());
        return messageSource;
    }
}
