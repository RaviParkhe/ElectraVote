package com.electrovotesuperx.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FirebaseConfig {

    private static boolean isInitialized = false;

    public static void initFirebase() {
        if (isInitialized)
            return;

        try {
            InputStream serviceAccount = null;

            // 1. Try loading from classpath resources
            serviceAccount = FirebaseConfig.class.getResourceAsStream("/serviceAccountKey.json");
            if (serviceAccount == null) {
                serviceAccount = FirebaseConfig.class.getClassLoader().getResourceAsStream("serviceAccountKey.json");
            }

            // 2. Try looking in standard filesystem locations
            if (serviceAccount == null) {
                String[] candidatePaths = new String[] {
                        "serviceAccountKey.json",
                        "electrovote/serviceAccountKey.json",
                        "src/main/resources/serviceAccountKey.json",
                        "electrovote/src/main/resources/serviceAccountKey.json",
                        "../serviceAccountKey.json",
                        "../electrovote/serviceAccountKey.json"
                };

                for (String path : candidatePaths) {
                    File file = new File(path);
                    if (file.exists() && file.isFile()) {
                        serviceAccount = new FileInputStream(file);
                        break;
                    }
                }
            }

            if (serviceAccount == null) {
                System.err.println("Firebase initialization failed! Please ensure serviceAccountKey.json exists.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            isInitialized = true;
            System.out.println("Firebase successfully initialized.");

        } catch (Exception e) {
            System.err.println("Firebase initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Firestore getFirestore() {
        if (!isInitialized) {
            System.err.println("Warning: Firebase not initialized. Firestore operations will fail.");
            return null;
        }
        return FirestoreClient.getFirestore();
    }
}
