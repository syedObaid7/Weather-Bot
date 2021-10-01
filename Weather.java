import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jibble.pircbot.PircBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;


public class Weather extends PircBot
{
    String zipCode = null;

    public Weather()
    {
        this.setName("Syed");
    }

    public void onMessage(String channel, String sender,
                          String login, String hostname, String message)
    {
        message = message.toLowerCase();
        String[] words = message.split(" ");
        String output = null;

        if (message.contains("weather"))
        {
            if (findZipCode(words))
            {
                try
                {
                    output = connect(zipCode, channel);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                sendMessage(channel, output);
            } else
            {
                sendMessage(channel, "ERROR, Zipcode not found");
            }
        } else
        {
            sendMessage(channel, "ERROR, Message must contain keyword \"weather\"");
        }
    }

    public String jsonParse(String s)
    {
        JsonObject obj = new JsonParser().parse(s).getAsJsonObject();

        String city = obj.get("name").getAsString();

        double temp = obj.getAsJsonObject("main").get("temp").getAsDouble();

        double maxTemp = obj.getAsJsonObject("main").get("temp_max").getAsDouble();

        double minTemp = obj.getAsJsonObject("main").get("temp_min").getAsDouble();

        temp = (temp - 273.15) * 1.8 + 32;
        maxTemp = (maxTemp - 273.15) * 1.8 + 32;
        minTemp = (minTemp - 273.15) * 1.8 + 32;

        DecimalFormat format = new DecimalFormat("###.##");

        return "The temperature in " + city + " is " + format.format(temp) + "°F. " +
                "The high for today is " + format.format(maxTemp) + "°F and the low is " + format.format(minTemp) + "°F.";
    }

    public boolean findZipCode(String[] array)
    {
        for (String s : array)
        {
            if (isInt(s) && s.length() == 5)
            {
                zipCode = s;
                return true;
            }
        }
        return false;
    }

    public boolean isInt(String s)
    {
        boolean isValidInteger = false;

        try
        {
            Integer.parseInt(s);

            // s is a valid integer

            isValidInteger = true;
        } catch (NumberFormatException ex)
        {
            // s is not an integer
        }
        return isValidInteger;
    }

    public String connect(String zipCode, String channel) throws IOException
    {
        try
        {
            // Creates URL object
            URL url = null;
            try
            {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + "&appid=67aff89e65e5839377d1f638b832f27f");
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            // Retrieves a URL connection object
            HttpURLConnection con = null;
            try
            {
                assert url != null;
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            // Use the object to create a GET Request
            try
            {
                assert con != null;
                con.setRequestMethod("GET");
            } catch (ProtocolException e)
            {
                e.printStackTrace();
            }

            // Adds header to the request
            con.setRequestProperty("Content-Type", "application/json");

            // Creates a BufferedReader on the input stream and reads from it.
            // Connection is opened implicitly by calling getInputStream which gets an input stream from the connection
            BufferedReader in = null;
            try
            {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            // Converts BufferReader to String and stores in a result variable
            String inputLine;
            StringBuilder content = new StringBuilder();
            while (true)
            {
                assert in != null;
                if ((inputLine = in.readLine()) == null) break;
                content.append(inputLine);
            }
            try
            {
                in.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return jsonParse(content.toString());
        } catch (Exception e)
        {
            return "Error! Exception : " + e;
        }

    }
}


