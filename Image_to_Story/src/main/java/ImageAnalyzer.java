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

public class ImageAnalyzer {

    private final RekognitionClient rekognitionClient;

    public ImageAnalyzer(String accessKeyId, String secretAccessKey, Region region) {
        rekognitionClient = RekognitionClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(region)
                .build();
    }

    public List<Label> analyzeImage(String imagePath) {
        try {
            File file = new File(imagePath);
            byte[] bytes = Files.readAllBytes(file.toPath());
            SdkBytes sdkBytes = SdkBytes.fromByteArray(bytes);

            Image image = Image.builder()
                    .bytes(sdkBytes)
                    .build();

            DetectLabelsRequest request = DetectLabelsRequest.builder()
                    .image(image)
                    .maxLabels(10)
                    .build();

            DetectLabelsResponse response = rekognitionClient.detectLabels(request);
            return response.labels();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
