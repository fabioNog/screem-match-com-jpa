package br.com.alura.screenmatch.service;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class ConsultaGemini {
    private static final Dotenv dotenv = Dotenv.load(); // Carrega o arquivo .env
    private static final String API_KEY = dotenv.get("GEMINI_API_KEY");
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public static String obterTraducao(String texto) {
        OkHttpClient client = new OkHttpClient();

        // Montando o corpo da requisição
        JSONObject part = new JSONObject();
        part.put("text", "traduza para o português o texto: " + texto);

        JSONObject content = new JSONObject();
        content.put("parts", new JSONArray().put(part));

        JSONObject body = new JSONObject();
        body.put("contents", new JSONArray().put(content));

        // Configurando a requisição HTTP
        RequestBody requestBody = RequestBody.create(
                body.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(GEMINI_URL + "?key=" + API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                // Parse da resposta (ajustado para lidar com a estrutura esperada)
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Acessando o campo "candidates" da resposta
                if (jsonResponse.has("candidates")) {
                    JSONArray candidatesArray = jsonResponse.getJSONArray("candidates");
                    JSONObject firstCandidate = candidatesArray.getJSONObject(0);

                    // Acessando o campo "content" do primeiro candidato
                    if (firstCandidate.has("content")) {
                        JSONObject contentObj = firstCandidate.getJSONObject("content");

                        // Acessando o campo "parts" dentro do "content"
                        if (contentObj.has("parts")) {
                            JSONArray partsArray = contentObj.getJSONArray("parts");
                            JSONObject firstPart = partsArray.getJSONObject(0);

                            // Retorne o texto traduzido
                            return firstPart.getString("text");
                        } else {
                            System.err.println("Campo 'parts' não encontrado na resposta.");
                            return "Tradução indisponível no momento.";
                        }
                    } else {
                        System.err.println("Campo 'content' não encontrado na resposta.");
                        return "Tradução indisponível no momento.";
                    }
                } else {
                    System.err.println("Campo 'candidates' não encontrado na resposta.");
                    return "Tradução indisponível no momento.";
                }
            } else {
                System.err.println("Erro ao consultar a API Gemini: " + response.message());
                return "Tradução indisponível no momento.";
            }
        } catch (IOException e) {
            System.err.println("Erro de comunicação com a API Gemini: " + e.getMessage());
            return "Tradução indisponível no momento.";
        }
    }

}
