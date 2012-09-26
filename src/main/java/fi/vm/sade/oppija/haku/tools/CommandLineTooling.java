package fi.vm.sade.oppija.haku.tools;

/**
 * @author jukka
 * @version 9/11/123:27 PM}
 * @since 1.1
 */
public class CommandLineTooling {


    private CommandLineTooling() {}

    public static void main(final String[] args) {
        final CommandExecutor commandExecutor = new CommandExecutor(args);
        commandExecutor.execute();
    }

}
