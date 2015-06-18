package forestnympho.thefuckingweather;

//**********************************
//Created by Josiah Parrish
//May 14, 2015
//Main Activity for the TFW app
//
//  Known Issues:
//      Openweathermap.org fucking sucks
//          10% of pulls are server fails
//      The method for getting the drawables is depricated
//      Some phone compatibility errors
//      MP and eventually app crashes if you spam the refresh button
//          seems like there might be some memory leakage
//
//  TODO: Add activity for user input so users can add witty remarks
//  TODO:   and send it as a message via email to me
//  TODO: Fix depricated parts
//************************************


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class MainDisplay extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;
    protected MediaPlayer mp;
    boolean foundIt = false;
    private Weather currentWeather;
    private TextView temperatureDisplay;
    private TextView remarkDisplay;
    private TextView weatherDisplay;
    private TextView locationDisplay;
    private TextView todayWeather;
    private TextView tomorrowWeather;
    private TextView logoDisplay;
    private TextView todayHiLow;
    private TextView tomorrowHiLow;
    private ImageView todayIcon;
    private ImageView tomorrowIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "Fonts/mainFont.ttf");
        Typeface logoFont = Typeface.createFromAsset(getAssets(), "Fonts/logoFont.ttf");
        //Custom fonts from dafont.com public domain fonts

        temperatureDisplay = (TextView) findViewById(R.id.temperatureDisplay);
        remarkDisplay = (TextView) findViewById(R.id.remarkDisplay);
        weatherDisplay = (TextView) findViewById(R.id.weatherDisplay);
        locationDisplay = (TextView) findViewById(R.id.locationDisplay);
        logoDisplay = (TextView) findViewById(R.id.LogoTextView);
        todayHiLow = (TextView) findViewById(R.id.tomorrowHiLow);
        todayIcon = (ImageView) findViewById(R.id.tomorrowIcon);
        tomorrowIcon = (ImageView) findViewById(R.id.nextDayIcon);
        tomorrowHiLow = (TextView) findViewById(R.id.nextDayHiLow);
        todayWeather = (TextView) findViewById(R.id.tomorrowWeather);
        tomorrowWeather = (TextView) findViewById(R.id.nextdayWeather);

        locationDisplay.setTypeface(customFont);
        remarkDisplay.setTypeface(customFont);
        temperatureDisplay.setTypeface(customFont);
        weatherDisplay.setTypeface(customFont);
        logoDisplay.setTypeface(logoFont);
        todayHiLow.setTypeface(customFont);
        tomorrowHiLow.setTypeface(customFont);
        todayWeather.setTypeface(customFont);
        tomorrowWeather.setTypeface(customFont);

        final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?";
        final String urlEnd = "&APPID=d6764eefbb013e4d7c46bae243ab46d9&mode=xml";

        sharedPreferences = getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
        String locationZip = sharedPreferences.getString("location", "zip=73132,us");
        boolean celcius = sharedPreferences.getBoolean("celcius", false);

        //together this makes the full URL that the xml is pulled from
        String URL = weatherUrl + locationZip + urlEnd;
        currentWeather = new Weather(celcius);

        //Start the asyncTask that kicks off the activity
        new DownloadXML().execute(URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_display, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {

        mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.MLocation:
                finish();
                startActivity(new Intent(this, LocationAndSettings.class));
                return true;
            case R.id.action_refresh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }


    public void setVisibleDisplay() {

        final Random rand = new Random();
        final Resources r = getResources();
        Handler handler = new Handler();
        //Since using handler for pauses, have to have the variables as finals since they are in
        //an inner class

        /////////////////////////
        //Screen order
        // This method uses handlers to load the screen elements in the correct order
        // after the correct time
        // During a good xmlPull-
        //  1.Location
        //  2.Temp
        //  3.Main description
        //  4.Witty remark
        //  5.2-Day Outlook
        //
        //  When each element loads, there is also a bang from the media player
        /////////////////////////

        //Location
        locationDisplay.setText(currentWeather.getLocation());
        mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
        mp.start();

        //Temperature
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                temperatureDisplay.setText(currentWeather.getTemp() + (char) 0x00B0 + "?!");

            }
        }, 650);//in milliseconds


        //Description
        //And witty remark is nested since it will be different with each weather pattern
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                switch (currentWeather.getCondition()) {
                    case "01":
                    case "02":
                        // case "10": //Sky is clear
                        //First check for extreme temps
                        if (currentWeather.getKelvins() < 273.15) {//too cold
                            weatherIsCold(rand, r);
                        } else if (currentWeather.getKelvins() > 305.4) {//too hot
                            weatherIsHot(rand, r);
                        } else {//just right :)
                            weatherIsNice(rand, r);
                        }
                        break;
                    case "03":
                    case "04":
                        //First check for extreme temps
                        if (currentWeather.getKelvins() < 273.15) {//too cold
                            weatherIsCold(rand, r);
                        } else if (currentWeather.getKelvins() > 305.4) {//too hot
                            weatherIsHot(rand, r);
                        } else {//just right :)
                            weatherIsCloudy(rand, r);
                        }
                        break;
                    case "09":
                    case "10":
                        weatherIsRainy(rand, r);
                        break;
                    case "11":
                        weatherIsStormy(rand, r);
                        break;
                    case "13":
                        weatherIsSnowy(rand, r);
                        break;
                    case "50":
                        weatherIsFoggy(rand, r);
                        break;
                    default:
                        //System.exit(0);//sys error!!!
                }
            }
        }, 1500); //ms
    }


    public void weatherIsNice(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING\n...NICE");
        final String[] remark = r.getStringArray(R.array.nice);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(remark[rand.nextInt(remark.length)]);
            }
        }, 750);
    }

    public void weatherIsCold(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING COLD!!!");
        final String[] remark = r.getStringArray(R.array.cold);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(remark[rand.nextInt(remark.length)]);
            }
        }, 750);
    }

    public void weatherIsHot(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING HOT!!!");
        final String[] remark = r.getStringArray(R.array.hot);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(remark[rand.nextInt(remark.length)]);
            }
        }, 750);
    }

    public void weatherIsCloudy(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING CLOUDY!!!");
        final String[] remark = r.getStringArray(R.array.overcast);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(remark[rand.nextInt(remark.length)]);
            }
        }, 750);
    }

    public void weatherIsRainy(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING RAINING!!!");
        final String[] remark = r.getStringArray(R.array.rain);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(remark[rand.nextInt(remark.length)]);
            }
        }, 750);
    }

    public void weatherIsStormy(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING STORMING!!!");
        final String[] remark = r.getStringArray(R.array.storms);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(remark[rand.nextInt(remark.length)]);
            }
        }, 750);

    }

    public void weatherIsSnowy(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING SNOWING!!!");
        final String[] remark = r.getStringArray(R.array.snow);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(rand.nextInt(remark.length));
            }
        }, 750);
    }

    public void weatherIsFoggy(final Random rand, final Resources r) {
        Handler handler = new Handler();
        weatherDisplay.setText("IT\'S FUCKING FOGGY!!!");
        final String[] remark = r.getStringArray(R.array.fog);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(getApplicationContext(), R.raw.fire);
                mp.start();
                remarkDisplay.setText(rand.nextInt(remark.length));
            }
        }, 750);
    }

    private void parseXML(InputStream is) throws XmlPullParserException, IOException {
        //parser to pull the weather data from openweather
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        try {
            xpp.setInput(new InputStreamReader(is));
            int startTag = xpp.getEventType();

            while (startTag != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (startTag) {
                    case XmlPullParser.START_TAG:
                        switch (tagName) {
                            case "city":
                                currentWeather.setLocation(xpp.getAttributeValue(null, "name"));
                                break;
                            case "coord":
                                currentWeather.setLat(xpp.getAttributeValue(null, "lat"));
                                currentWeather.setLon(xpp.getAttributeValue(null, "lon"));
                                break;
                            case "temperature":
                                currentWeather.setKelvins(xpp.getAttributeValue(null, "value"));
                                break;
                            case "weather":
                                currentWeather.setCondition(xpp.getAttributeValue(null, "icon"));
                                foundIt = true;
                                break;
                        }
                        break;
                }
                startTag = xpp.next();
            }
        } catch (Exception e) {
        }
    }

    private void parseForecastXML(InputStream is) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        try {
            xpp.setInput(new InputStreamReader(is));
            String HiLow;
            boolean tomorrowOrNextDay = true; //True for Tomorrow, False for nextDay
            int startTag = xpp.getEventType();

            while (startTag != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (startTag) {
                    case XmlPullParser.START_TAG:
                        switch (tagName) {
                            case "symbol": {
                                if (tomorrowOrNextDay)
                                    currentWeather.setTomorrowIcon(xpp.getAttributeValue(null, "var"));
                                else
                                    currentWeather.setNextDayIcon(xpp.getAttributeValue(null, "var"));
                                break;
                            }
                            case "temperature": {
                                if (tomorrowOrNextDay) {
                                    HiLow = (xpp.getAttributeValue(null, "max") + "\n" + xpp.getAttributeValue(null, "min"));
                                    currentWeather.setTomorrowHiLow(HiLow);
                                    tomorrowOrNextDay = false;
                                } else {
                                    HiLow = (xpp.getAttributeValue(null, "max") + "\n" + xpp.getAttributeValue(null, "min"));
                                    currentWeather.setNextDayHiLow(HiLow);
                                }
                                break;
                            }
                        }
                }
                startTag = xpp.next();
            }
        } catch (Exception e) {
        }
    }

    private class DownloadXML extends AsyncTask<String, Void, Void> {

        private Handler handler = new Handler(); //used for pauses

        @Override
        protected void onPreExecute() {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.cock);
            mp.start();

            temperatureDisplay.setText(" ");
            weatherDisplay.setText(" ");
            remarkDisplay.setText(" ");
            locationDisplay.setText(" ");

            //Little spin animation to show loading
            logoDisplay.setVisibility(View.VISIBLE);
            RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            logoDisplay.setAnimation(rotate);
        }

        @Override
        protected Void doInBackground(String... Url) {
            try {
                InputStream is;
                URL url = new URL(Url[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                parseXML(is);

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    logoDisplay.clearAnimation();
                    logoDisplay.setVisibility(View.INVISIBLE);

                    if (foundIt) {//If the parse was successful
                        setVisibleDisplay();
                        //After display is set, show forecast
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String urlBegin = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                                String urlEnd = "&cnt=2&mode=xml";
                                String urlLocation = ("lat=" + currentWeather.getLat() + "&lon=" + currentWeather.getLon());
                                //Same idea as before, just different url
                                new downloadForecast().execute(urlBegin + urlLocation + urlEnd);
                            }
                        }, 4000);
                    } else {
                        temperatureDisplay.setText(" ");
                        weatherDisplay.setText("I CAN\'T FIND THAT SHIT!!!");
                        locationDisplay.setText(" ");
                        remarkDisplay.setText(" ");
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.shell);
                        mp.start();
                        //be sure to release when done
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mp.release();
                            }
                        }, 2000);
                    }
                }
            }, 2000);
        }
    }

    private class downloadForecast extends AsyncTask<String, Void, Void> {
        private Handler handler = new Handler(); //used for pauses

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... Url) {
            try {
                InputStream is;
                URL url = new URL(Url[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                parseForecastXML(is);
            } catch (Exception e) {
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Void args) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.shell);
            mp.start();

            Resources res = getResources();
            todayHiLow.setText(currentWeather.getTomorrowHiLow());
            tomorrowHiLow.setText(currentWeather.getNextDayHiLow());

            todayHiLow.setVisibility(View.VISIBLE);
            tomorrowHiLow.setVisibility(View.VISIBLE);
            todayWeather.setVisibility(View.VISIBLE);
            tomorrowWeather.setVisibility(View.VISIBLE);

            //These have been depricated. Next release will pull the images from online
            //For code 501 - moderate rain icon = "10d"
            //URL is
            //http://openweathermap.org/img/w/10d.png

            switch (currentWeather.getTomorrowIcon()) {
                case "01":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.a));
                    break;
                case "02":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.b));
                    break;
                case "10":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.c));
                    break;
                case "03":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.d));
                    break;
                case "04":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.e));
                    break;
                case "09":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.f));
                    break;
                case "11":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.g));
                    break;
                case "13":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.h));
                    break;
                case "50":
                    todayIcon.setImageDrawable(res.getDrawable(R.drawable.i));
                    break;
                default:
            }
            switch (currentWeather.getNextDayIcon()) {
                case "01":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.a));
                    break;
                case "02":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.b));
                    break;
                case "10":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.c));
                    break;
                case "03":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.d));
                    break;
                case "04":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.e));
                    break;
                case "09":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.f));
                    break;
                case "11":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.g));
                    break;
                case "13":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.h));
                    break;
                case "50":
                    tomorrowIcon.setImageDrawable(res.getDrawable(R.drawable.i));
                    break;
                default:
            }
            //Give the forecast elements a cute little fade animaton
            Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in);

            todayHiLow.setAnimation(fadein);
            tomorrowHiLow.setAnimation(fadein);
            todayIcon.setAnimation(fadein);
            tomorrowIcon.setAnimation(fadein);
            todayWeather.setAnimation(fadein);
            tomorrowWeather.setAnimation(fadein);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mp.release();
                }
            }, 2000);
        }
    }
}



