package com.aluracursos.spring.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ChatGPT {
    public static String obtenerTraduccion(String texto){
        // Retrieve the API key from the environment variable
        String apiKey = System.getenv("OPENAI_APIKEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API key is not set in the environment variable OPENAI_APIKEY.");
        }

        OpenAiService service = new OpenAiService(apiKey);

        CompletionRequest requisicion = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduce a espanol el siguiente texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var respuesta = service.createCompletion(requisicion);
        return respuesta.getChoices().getFirst().getText();
    }
}
