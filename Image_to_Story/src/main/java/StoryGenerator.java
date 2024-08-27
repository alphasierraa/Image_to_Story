import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class StoryGenerator {

    private static final String COHERE_API_KEY = "Cohere API key"; // Replace with your Cohere API key
    private static final String COHERE_API_URL = "https://api.cohere.ai/generate"; // Replace with your Cohere model endpoint

    public static String generateStory(String prompt) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(COHERE_API_URL);
            post.setHeader("Authorization", "Bearer " + COHERE_API_KEY);
            post.setHeader("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(mapper.createObjectNode().put("prompt", prompt));
            post.setEntity(new StringEntity(jsonBody));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                // Print raw response for debugging
                System.out.println("Raw API response: " + responseString);

                JsonNode jsonNode = mapper.readTree(responseString);

                // Check for error messages in the response
                if (jsonNode.has("error")) {
                    String errorMessage = jsonNode.get("error").asText();
                    throw new Exception("Error from API: " + errorMessage);
                }

                // Handle response assuming it contains a 'text' field
                if (jsonNode.has("text")) {
                    return jsonNode.get("text").asText();
                } else {
                    throw new Exception("Unexpected response format: response is missing 'text' field.");
                }
            }
        }
    }
}
