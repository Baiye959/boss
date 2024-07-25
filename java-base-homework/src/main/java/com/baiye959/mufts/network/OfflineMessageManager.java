package com.baiye959.mufts.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Baiye959
 */
public class OfflineMessageManager {
    private Map<String, List<byte[]>> offlineMessages = new HashMap<>();

    public void storeOfflineMessage(String username, byte[] message) {
        offlineMessages.computeIfAbsent(username, k -> new LinkedList<>()).add(message);
    }

    public List<byte[]> retrieveOfflineMessages(String username) {
        return offlineMessages.remove(username);
    }
}
