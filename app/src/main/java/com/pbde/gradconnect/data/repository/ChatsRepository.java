package com.pbde.gradconnect.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.Message;
import com.pbde.gradconnect.data.models.enums.UserRole;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatsRepository {
    private final FirebaseFirestore db;
    private static final String COLLECTION_PATH = "chats";
    private static final String MESSAGES_COLLECTION = "messages";

    public ChatsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Chat>> getEmployerChats(String employerId) {
        MutableLiveData<List<Chat>> chatsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_PATH)
            .whereEqualTo("employerId", employerId)
//            .orderBy("lastMessageAt", Query.Direction.DESCENDING) // Requires indexing
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Chat> chatsList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Chat chat = documentToChat(document);
                    if (chat != null) {
                        chatsList.add(chat);
                    }
                }
                chatsLiveData.setValue(chatsList);
            })
            .addOnFailureListener(e -> {
                // Handle error
                chatsLiveData.setValue(new ArrayList<>());
            });

        return chatsLiveData;
    }

    public LiveData<List<Chat>> getCandidateChats(String candidateId) {
        MutableLiveData<List<Chat>> chatsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_PATH)
            .whereEqualTo("candidateId", candidateId)
//            .orderBy("lastMessageAt", Query.Direction.DESCENDING)  // Requires indexing
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Chat> chatsList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Chat chat = documentToChat(document);
                    if (chat != null) {
                        chatsList.add(chat);
                    }
                }
                chatsLiveData.setValue(chatsList);
            })
            .addOnFailureListener(e -> {
                // Handle error
                chatsLiveData.setValue(new ArrayList<>());
            });

        return chatsLiveData;
    }

    public Task<Void> createChat(String candidateId, String employerId) {
        String chatId = db.collection(COLLECTION_PATH).document().getId();
        Date now = new Date();

        Chat newChat = new Chat(chatId, now, now, candidateId, employerId);
        
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("id", newChat.getId());
        chatData.put("candidateId", newChat.getCandidateId());
        chatData.put("employerId", newChat.getEmployerId());
        chatData.put("createdAt", now);
        chatData.put("updatedAt", now);

        return db.collection(COLLECTION_PATH).document(chatId).set(chatData);
    }

    public LiveData<Chat> createChat(Chat chat) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();
        
        db.collection(COLLECTION_PATH)
            .document(chat.getId())
            .set(chatToMap(chat))
            .addOnSuccessListener(aVoid -> chatLiveData.setValue(chat))
            .addOnFailureListener(e -> chatLiveData.setValue(null));

        return chatLiveData;
    }

    public LiveData<Chat> getChat(String chatId) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_PATH)
            .document(chatId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    chatLiveData.setValue(documentToChat(document));
                } else {
                    chatLiveData.setValue(null);
                }
            })
            .addOnFailureListener(e -> chatLiveData.setValue(null));

        return chatLiveData;
    }

    public LiveData<Chat> getChatByEmployerIdAndCandidateId(String employerId, String candidateId) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_PATH)
            .whereEqualTo("employerId", employerId)
            .whereEqualTo("candidateId", candidateId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    chatLiveData.setValue(documentToChat(queryDocumentSnapshots.getDocuments().get(0)));
                } else {
                    chatLiveData.setValue(null);
                }
            })
            .addOnFailureListener(e -> chatLiveData.setValue(null));

        return chatLiveData;
    }

    public void updateChat(String chatId, Map<String, Object> updates) {
        db.collection(COLLECTION_PATH)
            .document(chatId)
            .update(updates);
    }

    public LiveData<Chat> findOrCreateChat(String employerId, String candidateId) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();

        getChatByEmployerIdAndCandidateId(employerId, candidateId)
            .observeForever(existingChat -> {
                if (existingChat != null) {
                    chatLiveData.setValue(existingChat);
                } else {
                    // Create new chat
                    Date now = new Date();
                    String chatId = UUID.randomUUID().toString();
                    Chat newChat = new Chat(chatId, now, now, candidateId, employerId);
                    createChat(newChat).observeForever(chatLiveData::setValue);
                }
            });

        return chatLiveData;
    }

    // Message operations
    public LiveData<Message> createMessage(Message message) {
        MutableLiveData<Message> messageLiveData = new MutableLiveData<>();

        db.collection(MESSAGES_COLLECTION)
            .document(message.getId())
            .set(messageToMap(message))
            .addOnSuccessListener(aVoid -> {
                // Update chat's last message info
                Map<String, Object> updates = new HashMap<>();
                updates.put("lastMessage", message.getContent());
                updates.put("lastMessageAt", dateToIso8601Format(message.getTimestamp()));
                updateChat(message.getChatId(), updates);
                messageLiveData.setValue(message);
            })
            .addOnFailureListener(e -> messageLiveData.setValue(null));

        return messageLiveData;
    }

    public LiveData<List<Message>> getChatMessages(String chatId) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        db.collection(MESSAGES_COLLECTION)
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Message> messages = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    messages.add(documentToMessage(document));
                }
                messagesLiveData.setValue(messages);
            })
            .addOnFailureListener(e -> messagesLiveData.setValue(new ArrayList<>()));

        return messagesLiveData;
    }

    public LiveData<List<Message>> getRecentMessages(String chatId, int limit) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        db.collection(MESSAGES_COLLECTION)
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Message> messages = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    messages.add(documentToMessage(document));
                }
                Collections.reverse(messages);
                messagesLiveData.setValue(messages);
            })
            .addOnFailureListener(e -> messagesLiveData.setValue(new ArrayList<>()));

        return messagesLiveData;
    }

    private Map<String, Object> chatToMap(Chat chat) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", chat.getId());
        map.put("jobId", chat.getJobId());
        map.put("candidateId", chat.getCandidateId());
        map.put("employerId", chat.getEmployerId());
        map.put("lastMessage", chat.getLastMessage());
        map.put("lastMessageAt", dateToIso8601Format(chat.getLastMessageAt()));
        map.put("createdAt", dateToIso8601Format(chat.getCreatedAt()));
        map.put("updatedAt", dateToIso8601Format(chat.getUpdatedAt()));
        return map;
    }

    private Chat documentToChat(DocumentSnapshot document) {
        if (document == null) return null;
        
        Chat chat = new Chat();
        
        // Set ID
        chat.setId(document.getId());
        
        // Set String fields
        if (document.contains("candidateId")) {
            chat.setCandidateId(document.getString("candidateId"));
        }
        
        if (document.contains("employerId")) {
            chat.setEmployerId(document.getString("employerId"));
        }
        
        if (document.contains("jobId")) {
            chat.setJobId(document.getString("jobId"));
        }
        
        if (document.contains("lastMessage")) {
            chat.setLastMessage(document.getString("lastMessage"));
        }
        
        if (document.contains("createdAt")) {
            chat.setCreatedAt(parseDate(document.getString("createdAt")));
        }
        
        if (document.contains("updatedAt")) {
            chat.setUpdatedAt(parseDate(document.getString("updatedAt")));
        }
        
        if (document.contains("lastMessageAt")) {
            chat.setLastMessageAt(parseDate(document.getString("lastMessageAt")));
        }
        
        return chat;
    }

    private Map<String, Object> messageToMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", message.getId());
        map.put("chatId", message.getChatId());
        map.put("senderId", message.getSenderId());
        map.put("senderRole", message.getSenderRole().getRoleString());
        map.put("content", message.getContent());
        map.put("timestamp", dateToIso8601Format(message.getTimestamp()));
        map.put("createdAt", dateToIso8601Format(message.getCreatedAt()));
        map.put("updatedAt", dateToIso8601Format(message.getUpdatedAt()));
        return map;
    }

    private Message documentToMessage(DocumentSnapshot document) {
        if (document == null) return null;
        
        String id = document.getId();
        String chatId = document.getString("chatId");
        String senderId = document.getString("senderId");
        String senderRoleString = document.getString("senderRole");
        UserRole senderRole = senderRoleString != null ? UserRole.fromString(senderRoleString) : UserRole.GUEST;
        String content = document.getString("content");
        
        // Handle timestamps properly - they could be stored as Timestamp or String
        Date timestamp = null;
        Date createdAt = null;
        Date updatedAt = null;
        
        if (document.contains("timestamp") && document.get("timestamp") instanceof com.google.firebase.Timestamp) {
            timestamp = document.getTimestamp("timestamp").toDate();
        } else {
            String timestampString = document.getString("timestamp");
            timestamp = parseDate(timestampString);
        }
        
        if (document.contains("createdAt") && document.get("createdAt") instanceof com.google.firebase.Timestamp) {
            createdAt = document.getTimestamp("createdAt").toDate();
        } else {
            String createdAtString = document.getString("createdAt");
            createdAt = parseDate(createdAtString);
        }
        
        if (document.contains("updatedAt") && document.get("updatedAt") instanceof com.google.firebase.Timestamp) {
            updatedAt = document.getTimestamp("updatedAt").toDate();
        } else {
            String updatedAtString = document.getString("updatedAt");
            updatedAt = parseDate(updatedAtString);
        }

        return new Message(id, createdAt, updatedAt, chatId, senderId, senderRole, content, timestamp);
    }
    
    private Date parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return iso8601Format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String dateToIso8601Format(Date date) {
        if (date == null) return null;
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format.format(date);
    }
}
