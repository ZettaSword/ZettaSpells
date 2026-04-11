package zettasword.zetta_spells.system.commands;

import com.binaris.wizardry.api.content.spell.NoneSpell;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import zettasword.zetta_spells.capability.RaceDataHolder;

import java.util.Collection;

public class SpellKnowledgeCommand {

    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(
                Commands.literal("setspellknowledge")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("spell", SpellArgument.spell())
                                        .then(Commands.argument("knowledge", IntegerArgumentType.integer(0))
                                                .executes((ctx) -> execute(ctx, SpellArgument.getSpell(ctx, "spell")))
                                        )
                                )
                        )
        );
    }

    public static int execute(CommandContext<CommandSourceStack> context, Spell spell) {
        Collection<ServerPlayer> targets;
        try {
            targets = EntityArgument.getPlayers(context, "targets");
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("target"));
            throw new RuntimeException(e);
        }
        int knowledge = IntegerArgumentType.getInteger(context, "knowledge");
        if (spell == null)
            throw new IllegalArgumentException("Invalid Spell provided: This spell does not exist");

        if (spell instanceof NoneSpell) {
            context.getSource().sendFailure(Component.literal("Cannot set knowledge for this spell"));
            return 0;
        }

        for (ServerPlayer player : targets) {
            player.getCapability(RaceDataHolder.INSTANCE).ifPresent(cap -> {
                cap.setSpellKnowledge(spell, knowledge);
            });
        }

        context.getSource().sendSuccess(
            () -> Component.literal("Successfully set spell knowledge for " + spell.getLocation().toString()),
            true
        );
        return targets.size();
    }
}