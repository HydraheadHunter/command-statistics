package hydraheadhunter.cmdstats.command.argument.custom_stat;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;

public class CustomStatArgument implements ArgumentType<Identifier> {
    private final Identifier identifier;
    
    public CustomStatArgument(Identifier identifier) {
        this.identifier = identifier;
    }
    
    public Identifier getID() {
        return this.identifier;
    }
    
    @Override
    public Identifier parse( StringReader reader ) throws CommandSyntaxException {
        return null;
    }
}
