import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String imagePath = "Path to your image";  // Path to your image
        String accessKeyId = "AWS Access Key ID";    // Replace with your AWS Access Key ID
        String secretAccessKey = "AWS Secret Access Key"; // Replace with your AWS Secret Access Key
        Region region = Region.US_WEST_2; // Example region (US West 2)

        // Initialize Rekognition client
        try (RekognitionClient rekognitionClient = RekognitionClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(region)
                .build()) {

            // Load image file
            File file = new File(imagePath);
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                SdkBytes sdkBytes = SdkBytes.fromByteArray(bytes);

                // Create image object
                Image image = Image.builder()
                        .bytes(sdkBytes)
                        .build();

                // Create request object
                DetectLabelsRequest request = DetectLabelsRequest.builder()
                        .image(image)
                        .maxLabels(10) // Request up to 10 labels
                        .build();

                // Make the API call
                DetectLabelsResponse response = rekognitionClient.detectLabels(request);

                // Collect only the first three labels
                List<Label> labels = response.labels();
                List<String> labelNames = labels.stream()
                        .limit(3) // Limit to the first 3 labels
                        .map(Label::name) // Extract label names
                        .collect(Collectors.toList());

                // Create prompt text
                String prompt = "Generate a story based on the following labels: " + String.join(", ", labelNames) + ". Make sure the story reflects the essence of these labels.";

                // Generate story based on labels
                try {
                    String story = StoryGenerator.generateStory(prompt);
                    System.out.println("Generated Story: " + story);
                } catch (Exception e) {
                    System.err.println("Error generating story: " + e.getMessage());
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
