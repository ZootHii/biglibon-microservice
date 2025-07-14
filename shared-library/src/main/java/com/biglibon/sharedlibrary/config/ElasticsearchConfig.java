package com.biglibon.sharedlibrary.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
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
import java.time.Instant;

@Configuration
@Profile("elasticsearch")
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
        try (InputStream caInput = caCert.getInputStream()) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
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
    public RestClient restClient() {
        HttpHost host = HttpHost.create(elasticUris);

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        return RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder
                                .setSSLContext(sslContext)
                                .setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        // ObjectMapper'a JavaTimeModule ekliyoruz Instant parse etme sorunu için jackson-datatype-jsr310
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        objectMapper.registerModule(javaTimeModule);

        JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper(objectMapper);

        RestClientTransport transport = new RestClientTransport(restClient, jacksonJsonpMapper);
        return new ElasticsearchClient(transport);
    }
}
