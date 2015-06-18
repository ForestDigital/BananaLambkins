package forestnympho.thefuckingweather;


/**
 * Created by jay-t on 5/14/15.
 * Container class for weather data
 */
public class Weather {
    private String location;
    private String temp;
    private String condition;
    private String kelvins;
    private String tomorrowIcon;
    private String tomorrowHiLow;
    private String nextDayIcon;
    private String nextDayHiLow;
    private boolean celcius;
    private String lat;
    private String lon;

    public Weather(boolean celcius) {
        location = "0.0";
        temp = "0.0";
        condition = "NAN";
        kelvins = "0.0";
        this.celcius = celcius;
    }

    public String getTomorrowIcon() {
        return tomorrowIcon.substring(0, 2);
    }

    public void setTomorrowIcon(String tomorrowIcon) {
        this.tomorrowIcon = tomorrowIcon;
    }

    public String getTomorrowHiLow() {
        return tomorrowHiLow;
    }

    public void setTomorrowHiLow(String tomorrowHiLow) {
        String hl[] = tomorrowHiLow.split("\n");
        float h, l;
        h = Float.parseFloat(hl[0]);
        l = Float.parseFloat(hl[1]);

        if (celcius) {
            h = (h - 273.15f);
            l = (l - 273.15f);
        } else {
            h = (h - 273.15f);
            l = (l - 273.15f);
            h = h * (1.8f);
            l = l * (1.8f);
            h = h + 32f;
            l = l + 32f;
        }

        this.tomorrowHiLow = (Integer.toString((int) h) + "\n" + Integer.toString((int) l));

    }

    public String getNextDayIcon() {
        return nextDayIcon.substring(0, 2);
    }

    public void setNextDayIcon(String nextDayIcon) {
        this.nextDayIcon = nextDayIcon;
    }

    public String getNextDayHiLow() {
        return nextDayHiLow;
    }

    public void setNextDayHiLow(String nextDayHiLow) {
        String hl[] = nextDayHiLow.split("\n");
        float h, l;
        h = Float.parseFloat(hl[0]);
        l = Float.parseFloat(hl[1]);

        if (celcius) {
            h = (h - 273.15f);
            l = (l - 273.15f);
        } else {
            h = (h - 273.15f);
            l = (l - 273.15f);
            h = h * (1.8f);
            l = l * (1.8f);
            h = h + 32f;
            l = l + 32f;
        }

        this.nextDayHiLow = (Integer.toString((int) h) + "\n" + Integer.toString((int) l));
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getCondition() {
        return condition.substring(0, 2);
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIcon() {
        return condition;
    }//Method wil be used to fix depricated issues

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        float t = Float.parseFloat(temp);

        if (celcius) {
            t = (t - 273.15f);
        } else {
            t = (t - 273.15f);
            t = t * (1.8f);
            t = t + 32f;
        }

        this.temp = Integer.toString((int) t);
    }

    public double getKelvins() {
        return Double.parseDouble(kelvins);

    }

    public void setKelvins(String kelvins) {
        this.kelvins = kelvins;
        setTemp(kelvins);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
