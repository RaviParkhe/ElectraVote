package com.electrovotesuperx.dao.QuickPollDAO;

import com.electrovotesuperx.config.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;

public class QuickPollDAO {

    private static final String COLLECTION_NAME = "quick_polls";

    public static boolean savePoll(String pollCode, String question, String creatorName, List<String> options) {
        return savePoll(pollCode, question, creatorName, options, "");
    }

    public static boolean savePoll(String pollCode, String question, String creatorName, List<String> options, String joinCode) {
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db == null) {
                System.err.println("[QuickPollDAO] Firestore instance is null!");
                return false;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("pollCode", pollCode != null ? pollCode.trim().toUpperCase() : "");
            data.put("question", question);
            data.put("creatorName", creatorName);
            data.put("joinCode", joinCode != null ? joinCode.trim() : "");
            data.put("createdAt", System.currentTimeMillis());

            Map<String, Object> optionsMap = new HashMap<>();
            for (String opt : options) {
                optionsMap.put(opt, 0L);
            }
            data.put("options", optionsMap);
            data.put("voters", new HashMap<String, Object>());

            DocumentReference docRef = db.collection(COLLECTION_NAME).document(pollCode.toUpperCase());
            ApiFuture<WriteResult> future = docRef.set(data);
            future.get(); // Wait for write confirmation
            System.out.println("[QuickPollDAO] Poll #" + pollCode + " for Org [" + joinCode + "] saved to Firestore successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("[QuickPollDAO] Failed to save poll to Firestore: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<Map<String, Object>> getAllPolls() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db == null)
                return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get();

            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                Map<String, Object> data = doc.getData();
                list.add(data);
            }
            System.out.println("[QuickPollDAO] Loaded " + list.size() + " polls from Firestore.");
        } catch (Exception e) {
            System.err.println("[QuickPollDAO] Error fetching polls from Firestore: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<Map<String, Object>> getPollsByOrganization(String joinCode) {
        if (joinCode == null || joinCode.isBlank()) {
            return getAllPolls();
        }

        List<Map<String, Object>> list = new ArrayList<>();
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db == null)
                return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("joinCode", joinCode.trim())
                    .get();

            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                Map<String, Object> data = doc.getData();
                list.add(data);
            }

            // Sort newest first
            list.sort((a, b) -> {
                long t1 = a.get("createdAt") instanceof Number ? ((Number) a.get("createdAt")).longValue() : 0L;
                long t2 = b.get("createdAt") instanceof Number ? ((Number) b.get("createdAt")).longValue() : 0L;
                return Long.compare(t2, t1);
            });

            System.out.println("[QuickPollDAO] Loaded " + list.size() + " polls for organization " + joinCode + " from Firestore.");
        } catch (Exception e) {
            System.err.println("[QuickPollDAO] Error fetching polls for organization " + joinCode + " from Firestore: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean recordVote(String pollCode, String voterId, String voterName, String option) {
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db == null)
                return false;

            DocumentReference docRef = db.collection(COLLECTION_NAME).document(pollCode.toUpperCase());

            Map<String, Object> voterInfo = new HashMap<>();
            voterInfo.put("voterName", voterName);
            voterInfo.put("option", option);
            voterInfo.put("timestamp", System.currentTimeMillis());

            Map<String, Object> updates = new HashMap<>();
            updates.put("voters." + voterId, voterInfo);
            updates.put("options." + option, FieldValue.increment(1));

            ApiFuture<WriteResult> future = docRef.update(updates);
            future.get();
            System.out.println(
                    "[QuickPollDAO] Vote recorded in Firestore for voter: " + voterName + " on poll #" + pollCode);
            return true;
        } catch (Exception e) {
            System.err.println("[QuickPollDAO] Error recording vote in Firestore: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}