package com.hiperium.city.tasks.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtil {

    public static final String AWS_ENDPOINT_OVERRIDE_PROPERTY = "aws.endpoint-override";
    public static final String TIME_ZONE_ID_PROPERTY = "city.time.zone.id";

    private static final String JDBC_SQL_CONNECTION = "jdbc:postgresql://{0}:{1}/{2}";

    public static void setApplicationProperties() throws JsonProcessingException {
        setDatasourceConnection();
        setIdentityProviderEndpoint();
        setApplicationTimeZoneId();
        setAwsEndpointOverride();
    }

    private static void setDatasourceConnection() throws JsonProcessingException {
        AuroraSecretsVO auroraSecretVO = EnvironmentUtil.getAuroraSecretVO();
        if (Objects.nonNull(auroraSecretVO)) {
            String sqlConnection = MessageFormat.format(JDBC_SQL_CONNECTION, auroraSecretVO.host(),
                    auroraSecretVO.port(), auroraSecretVO.dbname());
            log.debug("JDBC Connection: {}", sqlConnection);
            // Set Datasource connection for JPA.
            System.setProperty("spring.datasource.url", sqlConnection);
            System.setProperty("spring.datasource.username", auroraSecretVO.username());
            System.setProperty("spring.datasource.password", auroraSecretVO.password());
            // Set Datasource connection for Quartz.
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.URL", sqlConnection);
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.user", auroraSecretVO.username());
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.password", auroraSecretVO.password());
        }
    }

    private static void setIdentityProviderEndpoint() {
        String idpEndpoint = EnvironmentUtil.getIdpEndpoint();
        if (Objects.nonNull(idpEndpoint) && !idpEndpoint.isBlank()) {
            log.debug("IdP URI: {}", idpEndpoint);
            System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", idpEndpoint);
        }
    }

    private static void setApplicationTimeZoneId() {
        String timeZoneId = EnvironmentUtil.getTimeZoneId();
        if (Objects.nonNull(timeZoneId) && !timeZoneId.isBlank()) {
            log.debug("Time Zone ID: {}", timeZoneId);
            System.setProperty(TIME_ZONE_ID_PROPERTY, timeZoneId);
        }
    }

    private static void setAwsEndpointOverride() {
        String endpointOverrideURL = EnvironmentUtil.getAwsEndpointOverrideURL();
        if (Objects.isNull(endpointOverrideURL)) {
            endpointOverrideURL = System.getProperty(AWS_ENDPOINT_OVERRIDE_PROPERTY);
            if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isBlank()) {
                log.warn("AWS Endpoint Override URL detected. This will override the default AWS endpoint URL. " +
                                "Set the '{}' System Property to null or empty if you want to use the default AWS endpoint.",
                        AWS_ENDPOINT_OVERRIDE_PROPERTY);
            }
        } else if (!endpointOverrideURL.isBlank()) {
            System.setProperty(AWS_ENDPOINT_OVERRIDE_PROPERTY, endpointOverrideURL);
        }
    }
}
