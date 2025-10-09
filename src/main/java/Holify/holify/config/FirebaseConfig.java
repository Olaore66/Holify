//package Holify.holify.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//public class FirebaseConfig {
//    @Bean
//    public FirebaseApp firebaseApp() throws Exception {
//        ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
//                .setStorageBucket("holify-57161.appspot.com") // Your bucket name
//                .build();
//        if (FirebaseApp.getApps().isEmpty()) {
//            return FirebaseApp.initializeApp(options);
//        }
//        return FirebaseApp.getInstance();
//    }
//
//    @Bean
//    public Storage storage() throws Exception {
//        ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
//        return StorageOptions.newBuilder()
//                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
//                .build()
//                .getService();
//    }
//}