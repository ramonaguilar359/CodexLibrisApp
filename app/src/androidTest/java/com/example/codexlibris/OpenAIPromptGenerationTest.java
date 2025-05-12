package com.example.codexlibris;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test unitari per verificar la generació del JSON de la petició a OpenAI.
 */
public class OpenAIPromptGenerationTest {

    @Test
    public void testJsonGeneratCorrectament() throws Exception {
        String prompt = "Llibres llegits: \n- 1984 de George Orwell\n- El conte de la serventa de Margaret Atwood";

        // Simulem la construcció del JSON tal com fa l'app
        JSONObject message = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONArray messages = new JSONArray();

        message.put("role", "user");
        message.put("content", prompt);
        messages.put(message);

        payload.put("model", "gpt-3.5-turbo");
        payload.put("messages", messages);

        // Validacions
        assertEquals("gpt-3.5-turbo", payload.getString("model"));

        JSONArray messagesArray = payload.getJSONArray("messages");
        assertEquals(1, messagesArray.length());

        JSONObject msg = messagesArray.getJSONObject(0);
        assertEquals("user", msg.getString("role"));
        assertEquals(prompt, msg.getString("content"));
    }
}