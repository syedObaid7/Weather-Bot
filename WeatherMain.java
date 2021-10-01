public class WeatherMain
{

    public static void main(String[] args) throws Exception
    {
        // Now start our bot up.
        Weather bot = new Weather();

        // Enable debugging output.
        bot.setVerbose(true);

        // Connect to the IRC server.
        bot.connect("irc.freenode.net");

        // Join the #pircbot channel.
        bot.joinChannel("#obaid");

        bot.sendMessage("#obaid", "Where would you like to know the weather for?");
    }
}
