package hydraheadhunter.cmdstats.command.suggestionprovider;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import hydraheadhunter.cmdstats.CommandStatistics;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BreakableItemSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
     
     public static final SuggestionProvider<ServerCommandSource> BREAKABLE_ITEMS = SuggestionProviders.register(Identifier.of(CommandStatistics.MOD_ID, "breakable_items"),
          (context, builder) -> CommandSource.suggestIdentifiers(
               Registries.ITEM.stream()
                    .filter(item -> item.isEnabled(context.getSource().getEnabledFeatures()) &&
                         isDamageable(item))
                    .map(Registries.ITEM::getId),
               builder
          ));
     
     public CompletableFuture<Suggestions> getSuggestions( CommandContext<ServerCommandSource> context, SuggestionsBuilder builder ) throws CommandSyntaxException {
          return null;
     }
     private static boolean isDamageable(Item item) throws NullPointerException{
          try { return item.getDefaultStack().getComponents().get(DataComponentTypes.DAMAGE)>0; }
          catch (NullPointerException e) {return false;}
     }
     
}