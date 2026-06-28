package com.tiji.silcef.internals.utils;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record SentenceStructure(Map<String, String> verb, Map<String, String> rest, Format format) {
    public record Format(String one, String two, String three, @SerializedName("verb_and_rest") String verbAndRest,
                         @SerializedName("full") String fullSentence) {
    }

    public String getVerb(String key) {
        return verb.get(key);
    }

    public String getRest(String key) {
        return rest.get(key);
    }

    public String getListedItems(String... keys) {
        if (keys.length == 1) {
            return format.one.formatted(keys[0]);
        } else if (keys.length == 2) {
            return format.two.formatted(keys[0], keys[1]);
        } else if (keys.length >= 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < keys.length - 2; i++) {
                sb.append(keys[i]).append(", ");
            }
            return format.three.formatted(sb.toString(), keys[keys.length - 2], keys[keys.length - 1]);
        }
        throw new IllegalArgumentException("Invalid number of keys provided");
    }

    public String mergeVerbAndRest(String verb, String rest) {
        return format.verbAndRest.formatted(verb, rest);
    }

    public String getFullSentence(String origin, String contents) {
        return format.fullSentence.formatted(origin, contents);
    }
}
