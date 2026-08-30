package com.electrovotesuperx.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;

public class FirebaseConfig {

    private static boolean isInitialized = false;

    public static void initFirebase() {
        if (isInitialized) return;

        try {
            // NOTE: You must provide a valid serviceAccountKey.json in the resources or project root folder.
            // Currently looking for it in the root directory.
            FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
            isInitialized = true;
            System.out.println("Firebase successfully initialized.");

        } catch (Exception e) {
            System.err.println("Firebase initialization failed! Please ensure serviceAccountKey.json exists.");
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
