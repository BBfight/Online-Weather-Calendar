package mymeteocal.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import mymeteocal.entity.Event;
import mymeteocal.entityManager.EventManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author cast13
 */
@Stateless
public class WeatherService {

    static final String URL_OpenWeatherMap_weather
            = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
    static final String URL_Ending_string = "&mode=xml&units=metric&cnt=16&mode=json";

    private Logger logger = Logger.getLogger("WeatherService");

    @EJB
    private EventManager evm;

    @EJB
    private NotificationHandler nh;

    /**
     *
     * @param city
     * @param time
     * @return the weather of that specific day
     */
//    @Asynchronous
    public String getWeather(String city, Date time) {
        city = city.replace(" ", "+");
        int day = getDifferenceInDays(time);

        String result = "";
        logger.log(Level.INFO, "Trying to connect to the weather provider");
        try {
            URL url_weather = new URL(URL_OpenWeatherMap_weather + city + URL_Ending_string);

            logger.log(Level.INFO, "Getting info from" + URL_OpenWeatherMap_weather + city + URL_Ending_string);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                logger.log(Level.INFO, "Connection successful!");
                InputStreamReader inputStreamReader
                        = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader
                        = new BufferedReader(inputStreamReader, 8192);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                logger.log(Level.INFO, "This is what you get:" + result);
                return getWeatherResults(result).get(day);

            } else {
                logger.log(Level.WARNING, "Error in httpURLConnection.getResponseCode()!!!");
                return null;
            }

        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (JSONException ex) {
        }
        return null;
    }

    /**
     *
     * @param json
     * @return a list of 16 predictions starting from *TODAY
     *
     * @throws JSONException
     */
    private List<String> getWeatherResults(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        JSONArray weatherForecasts = jsonObject.getJSONArray("list");
        List<String> predictions = new ArrayList<>();
        JSONObject actualObject;
        for (int i = 0; i < weatherForecasts.length(); i++) {
            actualObject = weatherForecasts.getJSONObject(i);

            predictions.add(actualObject.getJSONArray("weather").getJSONObject(0).getString("main"));

        }
        return predictions;
    }

    /**
     * forecasts are not available if they are further than 16 days from now, or
     * are before now
     *
     * @param startingDate
     * @return true if forecasts are available
     */
    public Boolean isForecastAvailable(Date startingDate) {
        if (startingDate == null) {
            logger.log(Level.WARNING, "Null date at forecastAvailable");
        }
//        Calendar start =Calendar.getInstance();
//        start.setTime(startingDate);

//        Calendar now = Calendar.getInstance();
        Date now = new Date();
        if (startingDate.before(now)) {
            return false;
        }
        return getDifferenceInDays(startingDate) < 16 ? true : false;
    }

    
    /**
     *
     * @param startingDate
     * @return the difference in days from now to the startingDate
     */
    public int getDifferenceInDays(Date startingDate) {
        if (startingDate == null) {
            logger.log(Level.WARNING, "Null date at getDifferenceInDays");
        }

        Calendar now = Calendar.getInstance();

        Calendar end = Calendar.getInstance();
        end.setTime(startingDate);

        int daysBetween = 0;
        while (now.before(end)) {
            now.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        if (now.get(Calendar.DAY_OF_MONTH) > end.get(Calendar.DAY_OF_MONTH)) {
            daysBetween--;
        }
        logger.log(Level.INFO, "The difference is " + daysBetween);
        return daysBetween;
    }

//    @Schedule(second = "0", minute = "*", hour = "*", persistent = false) //for testing
    @Schedule(second = "0", minute = "0", hour = "*/12", persistent = false)
    public void updateWeather() {
        List<Event> toBeUpdated = evm.findNotStarted();
        logger.log(Level.INFO, "There are " + toBeUpdated.size() + " events to check!");
        String newWeather;
        for (Event e : toBeUpdated) {
            logger.log(Level.INFO, "Updating an event!");
            newWeather = getWeather(e.getCity(), e.getStartingDate());
            if (newWeather != null && !newWeather.equals(e.getWeatherForecast())) {
                e.setWeatherForecast(newWeather);
                logger.log(Level.INFO, "Changed event forecast!");

                if(e.getIsOutdoor()){
                    nh.notifyWeatherChange(e,true);
                }
            }
        }
        logger.log(Level.INFO, "Got here!");

    }

    /**
     *
     * @param event
     * @return true if the weather forecast is of bad weather for that event
     */
    public boolean hasBadWeather(Event event) {
        switch (event.getWeatherForecast()) {
            case "Clear":
                return event.getSunnyFlag();
            case "Clouds":
                return event.getCloudyFlag();
            case "Rain":
                return event.getRainyFlag();
            case "Snow":
                return event.getSnowyFlag();
            default:
                return false;
        }
    }

    /**
     *
     * @param event
     * @param newForecast
     * @return true if the newForecast would be a bad weather for the event
     */
    public boolean hasBadWeather(Event event, String newForecast) {
        switch (newForecast) {
            case "Clear":
                return event.getSunnyFlag();
            case "Clouds":
                return event.getCloudyFlag();
            case "Rain":
                return event.getRainyFlag();
            case "Snow":
                return event.getSnowyFlag();
            default:
                return false;
        }
    }

    /**
     * 
     * @param event
     * @return the date of the closest future day with good condition, or null if none exists
     */
    public Date getBetterDay(Event event) {
        logger.log(Level.INFO, "Trying to get the better day for event "+ event.getIdEvent());
        String city = event.getCity().replace(" ", "+");

        List<String> predictions = new ArrayList<>();
        String result = "";
        logger.log(Level.INFO, "Trying to connect to the weather provider");
        try {
            URL url_weather = new URL(URL_OpenWeatherMap_weather + city + URL_Ending_string);

            logger.log(Level.INFO, "Getting info from" + URL_OpenWeatherMap_weather + city + URL_Ending_string);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                logger.log(Level.INFO, "Connection successful!");
                InputStreamReader inputStreamReader
                        = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader
                        = new BufferedReader(inputStreamReader, 8192);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                logger.log(Level.INFO, "This is what you get:" + result);
                predictions = getWeatherResults(result);

            } else {
                logger.log(Level.WARNING, "Error in httpURLConnection.getResponseCode()!!!");
                return null;
            }

        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (JSONException ex) {
        }
        // checking from 4th day from now if it has a good weather condition
        for (int i = 4; i < predictions.size(); i++) {
            if (!hasBadWeather(event, predictions.get(i))) {
                logger.log(Level.INFO, "Better day found:"+ i);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, i);
                return c.getTime();
            }
        }
        return null;

    }
}
