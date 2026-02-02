package com.biglibon.sharedlibrary.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Slf4j
@Configuration
@Profile("elasticsearch")
// it will work only microservices which has elasticsearch profile on application.yaml
// spring:
//  profiles:
//    include: elasticsearch
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticUris;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.elasticsearch.ssl.certificate-authorities}")
    private Resource caCert;

    private SSLContext sslContext;

    @PostConstruct
    public void init() {
        // Read CA cert from .elasticsearch/certs path
        try (InputStream caInput = caCert.getInputStream()) {
            // Create CertificateFactory in X.509 standard
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // Create CA Certificate using CertificateFactory and CA in .elasticsearch/certs path
            Certificate ca = cf.generateCertificate(caInput);

            // Create trustStore by using JVM default KeyStore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // Create empty KeyStore
            trustStore.load(null, null);

            // Set elasticsearch CA cert to JVM trust store so JVM can recognize otherwise app will refuse elasticsearch
            trustStore.setCertificateEntry("ca-cert", ca);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (Exception e) {
            throw new IllegalStateException("SSL sertifikası yüklenemedi", e);
        }
    }

    @Bean(destroyMethod = "close")
    public ElasticsearchClient elasticsearchClient() {
        HttpHost host = HttpHost.create(elasticUris);

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClient restClient = RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder
                                .setSSLContext(sslContext)
                                .setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();

        // Add JavaTimeModule to ObjectMapper to fix Instant parse issue - jackson-datatype-jsr310
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        // Create the transport with a Jackson mapper which maps classes to json
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
        return new ElasticsearchClient(transport);
    }
}
