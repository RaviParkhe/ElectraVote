package com.electrovotesuperx.service.OfflineService;

import com.electrovotesuperx.config.firebaseConfig.FirebaseConfig;
import com.google.gson.JsonObject;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class EmailService {

    // Default sender credentials / fallback SMTP configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    // Sender email address for automated notifications
    private static final String SENDER_EMAIL = "electrovote.system@gmail.com";
    private static final String SENDER_PASSWORD = ""; // Optional SMTP App Password if configured

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    /**
     * Sends the 6-digit Secret Approval PIN to the chief approver
     * (ravi.parkhe2006@gmail.com).
     */
    public static boolean sendApprovalPinEmail(
            String uid,
            String approverEmail,
            String officerName,
            String officerEmail,
            String stationName,
            String phone,
            String approvalPin,
            String idToken) {

        System.out.println("[EmailService] Preparing email dispatch to: " + approverEmail);
        System.out.println("[EmailService] Generated Approval PIN: " + approvalPin);

        boolean smtpSuccess = false;

        // 1. Try sending via JavaMail SMTP if sender password is configured
        if (SENDER_PASSWORD != null && !SENDER_PASSWORD.isBlank()) {
            smtpSuccess = sendViaSmtp(approverEmail, officerName, officerEmail, stationName, phone, approvalPin);
        }

        // 2. Also record in Firestore collection 'mail' for Firebase Trigger Email
        // Extension
        boolean firestoreQueueSuccess = queueInFirestore(approverEmail, officerName, officerEmail, stationName, phone,
                approvalPin, idToken);

        System.out.println("[EmailService] Dispatch completed. SMTP: " + smtpSuccess + ", Firestore mail trigger: "
                + firestoreQueueSuccess);
        return smtpSuccess || firestoreQueueSuccess;
    }

    private static boolean sendViaSmtp(
            String toEmail,
            String officerName,
            String officerEmail,
            String stationName,
            String phone,
            String approvalPin) {

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "ElectraVote Automated System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("ElectraVote: Polling Officer Approval PIN [" + approvalPin + "]");

            String htmlContent = buildEmailHtml(officerName, officerEmail, stationName, phone, approvalPin);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[EmailService] SMTP email sent successfully to " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("[EmailService] SMTP sending note (falling back to cloud trigger): " + e.getMessage());
            return false;
        }
    }

    private static boolean queueInFirestore(
            String toEmail,
            String officerName,
            String officerEmail,
            String stationName,
            String phone,
            String approvalPin,
            String idToken) {

        try {
            String url = "https://firestore.googleapis.com/v1/projects/"
                    + FirebaseConfig.PROJECT_ID
                    + "/databases/(default)/documents/mail";

            JsonObject fields = new JsonObject();

            // to: ["ravi.parkhe2006@gmail.com"]
            JsonObject arrayVal = new JsonObject();
            com.google.gson.JsonArray values = new com.google.gson.JsonArray();
            JsonObject strVal = new JsonObject();
            strVal.addProperty("stringValue", toEmail);
            values.add(strVal);
            arrayVal.add("values", values);
            JsonObject toField = new JsonObject();
            toField.add("arrayValue", arrayVal);
            fields.add("to", toField);

            // message: { subject, html, text }
            JsonObject msgMap = new JsonObject();
            JsonObject msgFields = new JsonObject();

            JsonObject subj = new JsonObject();
            subj.addProperty("stringValue", "ElectraVote: Polling Officer Approval PIN [" + approvalPin + "]");
            msgFields.add("subject", subj);

            JsonObject txt = new JsonObject();
            txt.addProperty("stringValue",
                    "A new Polling Officer requires your approval:\n\n"
                            + "Officer Name: " + officerName + "\n"
                            + "Officer Email: " + officerEmail + "\n"
                            + "Station: " + stationName + "\n"
                            + "Phone: " + phone + "\n\n"
                            + "SECRET APPROVAL PIN: " + approvalPin + "\n\n"
                            + "Enter this 6-digit PIN in the ElectraVote Polling Officer Gateway to approve access.");
            msgFields.add("text", txt);

            JsonObject html = new JsonObject();
            html.addProperty("stringValue", buildEmailHtml(officerName, officerEmail, stationName, phone, approvalPin));
            msgFields.add("html", html);

            msgMap.add("fields", msgFields);
            JsonObject msgField = new JsonObject();
            msgField.add("mapValue", msgMap);
            fields.add("message", msgField);

            JsonObject body = new JsonObject();
            body.add("fields", fields);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("[EmailService] Error queueing in Firestore: " + e.getMessage());
            return false;
        }
    }

    private static String buildEmailHtml(
            String officerName, String officerEmail, String stationName, String phone, String approvalPin) {

        return "<div style='font-family: Arial, sans-serif; max-width: 560px; margin: 0 auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 12px; background-color: #ffffff;'>"
                + "<div style='text-align: center; margin-bottom: 20px;'>"
                + "<h2 style='color: #059669; margin: 0;'>🗳️ ElectraVote Verification</h2>"
                + "<p style='color: #64748b; font-size: 14px;'>Polling Officer Access Approval Request</p>"
                + "</div>"
                + "<p style='color: #1e293b; font-size: 15px;'>Hello Administrator,</p>"
                + "<p style='color: #334155; font-size: 14px;'>A user has registered to be a <strong>Polling Officer</strong> for Offline Voting and requires your authorization:</p>"
                + "<div style='background-color: #f8fafc; padding: 14px; border-radius: 8px; margin: 16px 0;'>"
                + "<p style='margin: 4px 0;'><strong>Officer Name:</strong> " + officerName + "</p>"
                + "<p style='margin: 4px 0;'><strong>Officer Email:</strong> " + officerEmail + "</p>"
                + "<p style='margin: 4px 0;'><strong>Station / College:</strong> " + stationName + "</p>"
                + "<p style='margin: 4px 0;'><strong>Contact Phone:</strong> " + phone + "</p>"
                + "</div>"
                + "<div style='text-align: center; margin: 20px 0; padding: 16px; background-color: #ecfdf5; border: 2px dashed #059669; border-radius: 10px;'>"
                + "<p style='margin: 0 0 6px 0; font-size: 12px; font-weight: bold; color: #065f46;'>SECRET 6-DIGIT APPROVAL PIN</p>"
                + "<div style='font-size: 30px; font-weight: 900; letter-spacing: 6px; color: #047857;'>" + approvalPin
                + "</div>"
                + "</div>"
                + "<p style='color: #475569; font-size: 12px; text-align: center;'>"
                + "Enter or share this 6-digit PIN in the ElectraVote Polling Officer Gateway to approve terminal access."
                + "</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;'>"
                + "<p style='color: #94a3b8; font-size: 11px; text-align: center;'>© 2026 ElectraVote Secure Voting System</p>"
                + "</div>";
    }
}
