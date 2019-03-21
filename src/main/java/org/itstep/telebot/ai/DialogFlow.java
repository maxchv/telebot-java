package org.itstep.telebot.ai;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.*;
import com.google.protobuf.Struct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class DialogFlow {

    @Value("${google.application.credentials}")
    String dilogFlowJson;

    @Value("${dialogflow.language}")
    String lang;

    private SessionsClient sessionsClient;
    private SessionName session;

    @PreDestroy
    public void destroy() {
        if (sessionsClient != null) {
            sessionsClient.close();
        }
    }

    @PostConstruct
    public void init() {
        ServiceAccountCredentials accountCredentials = null;
        try {
            accountCredentials = ServiceAccountCredentials
                    .fromStream(new FileInputStream(ResourceUtils.getFile("classpath:" + dilogFlowJson)));
        } catch (IOException e) {
            log.error("Create service account", e);
        }
        String projectId = accountCredentials != null ? accountCredentials.getProjectId() : null;
        log.info(String.format("ProjectId: %s", projectId));

        SessionsSettings.Builder sessionSettingsBuilder = SessionsSettings.newBuilder();
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(accountCredentials);

        try {
            SessionsSettings sessionsSettings = sessionSettingsBuilder
                    .setCredentialsProvider(credentialsProvider).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
        } catch (IOException e) {
            log.error("Create session client", e);
        }

        log.info(String.format("SessionClient: %s", sessionsClient));

        String sessionId = UUID.randomUUID().toString();

        session = SessionName.of(projectId, sessionId);
        log.info(String.format("Created session: %s", session.toString()));
    }

    public Mono<String> request(String text) {
        return Mono.fromCallable(() -> {
            log.info("Thread: " + Thread.currentThread().getName());
            TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(lang);

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            QueryResult queryResult = response.getQueryResult();

            log.info(String.format("Query Text: '%s'", queryResult.getQueryText()));
            log.info(String.format("Detected Intent: %s (confidence: %f)",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence()));
            log.info(String.format("Fulfillment Text: '%s'", queryResult.getFulfillmentText()));

            Struct parameters = queryResult.getParameters();
            parameters.getFieldsMap().forEach((parameter, value) -> {
                log.info(String.format("Parameter %s: %s", parameter, value.getStringValue()));
            });

            return queryResult.getFulfillmentText();
        }).subscribeOn(Schedulers.elastic());
    }
}
