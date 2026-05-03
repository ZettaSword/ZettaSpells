package zettasword.zetta_spells.system.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.items.SpellWordItem;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWord;
import zettasword.zetta_spells.system.spellcreation.actions.SpellWords;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetSpellWordFunction extends LootItemConditionalFunction {
    
    @Nullable
    private final List<ResourceLocation> allowedSpellWords;
    private final boolean allowUnknown;

    protected SetSpellWordFunction(LootItemCondition[] conditions, 
                                   @Nullable List<ResourceLocation> allowedSpellWords,
                                   boolean allowUnknown) {
        super(conditions);
        this.allowedSpellWords = allowedSpellWords;
        this.allowUnknown = allowUnknown;
    }

    /**
     * Builder for datagen and JSON construction
     */
    public static LootItemConditionalFunction.Builder<?> setSpellWord(@Nullable List<ResourceLocation> spellWords, boolean allowUnknown) {
        return simpleBuilder(conditions -> new SetSpellWordFunction(conditions, spellWords, allowUnknown));
    }

    /**
     * Simple builder with no filters (random from all registered spellwords)
     */
    public static LootItemConditionalFunction.Builder<?> setSpellWord() {
        return setSpellWord(null, false);
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return ZSLootFunctions.SET_SPELLWORD.get();
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, @NotNull LootContext lootContext) {
        // Get candidate spellwords
        List<ResourceLocation> candidates;
        
        if (allowedSpellWords != null && !allowedSpellWords.isEmpty()) {
            // Use provided filter list
            candidates = allowedSpellWords.stream()
                .filter(id -> SpellWords.getWord(id).isPresent())
                .toList();
        } else {
            // Use all registered spellwords
            candidates = SpellWords.getWords().stream()
                    .filter(SpellWord::doesHaveItem)
                    .map(SpellWord::getRegistryName)
                    .filter(Objects::nonNull)
                    .toList();
        }

        if (candidates.isEmpty()) {
            if (allowUnknown) {
                // Fallback: set a dummy/unknown tag so item still displays something
                SpellWordItem.setSpellWord(stack, ResourceLocation.fromNamespaceAndPath(ZettaSpells.MODID, "unknown"));
            }
            return stack;
        }



        // Pick random spellword using loot context's RNG
        RandomSource random = lootContext.getRandom();
        ResourceLocation chosen = candidates.get(random.nextInt(candidates.size()));
        
        SpellWordItem.setSpellWord(stack, chosen);
        return stack;
    }

    // ─────────────────────────────────────────────────────────────
    // Serializer for JSON (de)serialization
    // ─────────────────────────────────────────────────────────────
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetSpellWordFunction> {
        
        @Override
        public void serialize(@NotNull JsonObject json, 
                             @NotNull SetSpellWordFunction function, 
                             @NotNull JsonSerializationContext context) {
            super.serialize(json, function, context);
            
            if (function.allowedSpellWords != null && !function.allowedSpellWords.isEmpty()) {
                var array = new com.google.gson.JsonArray();
                for (ResourceLocation id : function.allowedSpellWords) {
                    array.add(id.toString());
                }
                json.add("spellwords", array);
            }
            
            if (function.allowUnknown) {
                json.addProperty("allow_unknown", true);
            }
        }

        @Override
        public @NotNull SetSpellWordFunction deserialize(@NotNull JsonObject json, 
                                                        @NotNull JsonDeserializationContext context,
                                                        LootItemCondition @NotNull [] conditions) {
            List<ResourceLocation> spellWords = null;
            
            if (json.has("spellwords")) {
                var array = GsonHelper.getAsJsonArray(json, "spellwords");
                spellWords = new ArrayList<>();
                for (var element : array) {
                    String id = element.getAsString();
                    ResourceLocation loc = ResourceLocation.tryParse(id);
                    if (loc != null) spellWords.add(loc);
                }
            }
            
            boolean allowUnknown = GsonHelper.getAsBoolean(json, "allow_unknown", false);
            
            return new SetSpellWordFunction(conditions, spellWords, allowUnknown);
        }
    }
}