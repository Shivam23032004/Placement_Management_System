package com.placement.dsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DSA: Trie (Prefix Tree)
 * Member: Shivam
 *
 * A Trie stores strings character by character.
 * Each TrieNode holds a HashMap of child characters.
 * - Insert: O(m) where m = length of string
 * - Prefix Search: O(m + k) where k = number of results
 *
 * Used for real-time company name prefix search in AdminDashboard.
 * Example: typing "TC" instantly returns ["TCS", "TCIL", ...]
 */
public class CompanyTrie {

    // ---- Inner TrieNode class ----
    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;
        String  fullName; // full company name stored at end node

        TrieNode() {
            children    = new HashMap<>();
            isEndOfWord = false;
            fullName    = null;
        }
    }

    private final TrieNode root;

    public CompanyTrie() {
        root = new TrieNode();
    }

    /**
     * Insert a company name into the Trie (case-insensitive internally).
     * Time complexity: O(m), m = name length
     */
    public void insert(String companyName) {
        if (companyName == null || companyName.isEmpty()) return;
        TrieNode current = root;
        String lower = companyName.toLowerCase();

        for (char c : lower.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
        current.fullName    = companyName; // preserve original casing
    }

    /**
     * Return all company names that start with the given prefix.
     * Time complexity: O(m + k), m = prefix length, k = results count
     */
    public List<String> searchByPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return results;

        TrieNode current = root;
        String lower = prefix.toLowerCase();

        // Traverse to the end of the prefix
        for (char c : lower.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return results; // prefix not found in trie
            }
            current = current.children.get(c);
        }

        // Collect all words in the subtree below this prefix node
        collectAllWords(current, results);
        return results;
    }

    /**
     * DFS to collect all full names stored below a given node.
     */
    private void collectAllWords(TrieNode node, List<String> results) {
        if (node.isEndOfWord) {
            results.add(node.fullName);
        }
        for (TrieNode child : node.children.values()) {
            collectAllWords(child, results);
        }
    }

    /**
     * Check if any company name starts with the given prefix.
     * Time complexity: O(m)
     */
    public boolean startsWith(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(c)) return false;
            current = current.children.get(c);
        }
        return true;
    }

    /**
     * Bulk insert from a list of names.
     */
    public void buildFromList(List<String> names) {
        for (String name : names) insert(name);
    }
}
