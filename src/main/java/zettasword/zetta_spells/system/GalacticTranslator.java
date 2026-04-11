package zettasword.zetta_spells.system;

public class GalacticTranslator {

    // Mapping array corresponding to A-Z
    private static final String[] GALACTIC_ALPHABET = {
            "ᔑ", // A
            "ʖ", // B
            "ᓵ", // C
            "↸", // D
            "ᒷ", // E
            "⎓", // F
            "⊣", // G
            "⍑", // H
            "╎", // I
            "⋮", // J
            "ꖌ", // K
            "ꖎ", // L
            "ᒲ", // M
            "リ", // N
            "𝙹", // O
            "!¡", // P
            "ᑑ", // Q
            "∷", // R
            "ᓭ", // S
            "ℸ ̣̣", // T
            "⚍", // U
            "⍊", // V
            "∴", // W
            "̇̇/", // X
            "||", // Y
            "⨅"  // Z
    };

    /**
     * Translates a standard English String into the Minecraft Galactic Alphabet.
     * Non-alphabetic characters (spaces, numbers, punctuation) are preserved.
     * Lowercase letters are automatically converted to uppercase for translation.
     *
     * @param input The English string to translate.
     * @return The translated Galactic string.
     */
    public static String toGalactic(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());

        for (char c : input.toCharArray()) {
            // Normalize to uppercase to match array indices (A=0, B=1, ...)
            char upperC = Character.toUpperCase(c);

            if (upperC >= 'A' && upperC <= 'Z') {
                int index = upperC - 'A';
                result.append(GALACTIC_ALPHABET[index]);
            } else {
                // Keep numbers, spaces, and other symbols as-is
                result.append(c);
            }
        }

        return result.toString();
    }
}