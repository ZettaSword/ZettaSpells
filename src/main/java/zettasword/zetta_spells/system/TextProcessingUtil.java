package zettasword.zetta_spells.system;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for text processing in chat/command contexts.
 */
public class TextProcessingUtil {

    // Updated Regex: Matches alphanumerics and underscores, allowing them to be chained by colons
    // (e.g., minecraft:zombie, mod:item_name).
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}_]+(:[\\p{L}\\p{N}_]+)*");

    /**
     * Extracts alphanumeric words from the input string, filters out symbols/punctuation,
     * and converts them to lowercase for consistent detection. Preserves colons within
     * resource locations (e.g., minecraft:zombie).
     *
     * @param text   The raw input string (e.g., chat message or command argument)
     * @return A list of cleaned, lowercased words. Returns an empty list for null/empty input.
     *
     * Example:
     *   Input:  "Hello, World! 123 minecraft:zombie test-case"
     *   Output: ["hello", "world", "123", "minecraft:zombie", "test", "case"]
     */
    public static @NotNull List<String> extractWords(@NotNull String text) {
        List<String> words = new ArrayList<>();

        if (text.isEmpty()) {
            return words;
        }

        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            // Convert to lowercase using English locale for consistency (avoids Turkish 'i' issues)
            String word = matcher.group().toLowerCase(Locale.ENGLISH);
            words.add(word);
        }

        return words;
    }
}