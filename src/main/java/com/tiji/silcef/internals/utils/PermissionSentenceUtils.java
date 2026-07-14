package com.tiji.silcef.internals.utils;

import com.google.gson.Gson;
import com.tiji.silcef.Silcef;
import org.cef.handler.CefPermissionRequestType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PermissionSentenceUtils {
    private static SentenceStructure sentenceStructure;

    public static void load(String locale) {
        try (InputStream inputStream = Silcef.class.getResourceAsStream("/sentences/%s.json".formatted(locale))) {
            if (inputStream == null && !locale.equals("en_us")) {
                load("en_us");
                Silcef.LOGGER.warn("Using fallback English as requested locale ({}) is not found.", locale);
                return;
            } else if (inputStream == null) {
                Silcef.LOGGER.error("Fallback English is not found; " +
                        "this mod is probably corrupted. Redownload this mod and try again.");
                throw new FileNotFoundException("See above");
            }

            String json = new String(inputStream.readAllBytes());
            sentenceStructure = new Gson().fromJson(json, SentenceStructure.class);
        } catch (IOException e) {
            sentenceStructure = null;
            Silcef.LOGGER.error("Failed to load permission sentence structure for locale {}: {}", locale, e);
        }
    }

    public static String getText(String origin, Set<CefPermissionRequestType> permissions) {
        Map<String, Set<CefPermissionRequestType>> groupByVerb = new HashMap<>();
        for (CefPermissionRequestType permission : permissions) {
            if (groupByVerb.containsKey(sentenceStructure.getVerb(permission.name()))) {
                groupByVerb.get(sentenceStructure.getVerb(permission.name())).add(permission);
            } else {
                groupByVerb.put(sentenceStructure.getVerb(permission.name()),
                    new HashSet<>(Collections.singleton(permission)));
            }
        }

        List<String> sections = new ArrayList<>();
        for (Map.Entry<String, Set<CefPermissionRequestType>> entry : groupByVerb.entrySet()) {
            String[] toMerge = entry.getValue().stream()
                .map(CefPermissionRequestType::name)
                .map(sentenceStructure::getRest)
                .toArray(String[]::new);

            sections.add(sentenceStructure.mergeVerbAndRest(
                entry.getKey(),
                sentenceStructure.getListedItems(toMerge)));
        }

        return sentenceStructure.getFullSentence(origin,
            sentenceStructure.getListedItems(sections.toArray(String[]::new)));
    }
}
