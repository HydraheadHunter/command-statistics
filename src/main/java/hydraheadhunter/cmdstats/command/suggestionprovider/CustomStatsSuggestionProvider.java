package hydraheadhunter.cmdstats.command.suggestionprovider;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class CustomStatsSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
     @Override
     public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
          // Add all Stats.CUSTOM stat ids to the suggestions.
          for (Identifier id : Registries.CUSTOM_STAT.getIds())
               builder.suggest(id.getNamespace() + ":" + id.getPath() );
          // Lock the suggestions after we've modified them.
          return builder.buildFuture();
     }
}