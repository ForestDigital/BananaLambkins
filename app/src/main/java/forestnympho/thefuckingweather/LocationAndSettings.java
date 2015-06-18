package forestnympho.thefuckingweather;

//***************************
//Created by Josiah Parrish
//May 18, 2015
//Locations and Settings activity for the TFW app
//
//  Current Issues(bugs):
//      -SharedPreferences does not save the entered location in the text field
//          When activity is reopened, user has to re-enter location and set
//          location type every time.
//      -Apparently I can't spell CELSIUS
//
//  TODO: -create key-value pairs to save user location
//  TODO: -add security checks for location
//          -eg. Only numbers for US zip codes etc.
//  TODO: -add other settings like language preferences
//****************************

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LocationAndSettings extends AppCompatActivity {
    private Button go;
    private RadioGroup group;
    private RadioButton zip, city;
    private EditText EditTextLocation;
    private ToggleButton celciusOrF;
    private TextView banner;
    private Typeface customFont;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_and_settings);
        banner = (TextView) findViewById(R.id.Banner);
        customFont = Typeface.createFromAsset(getAssets(), "Fonts/mainFont.ttf");
        //Custom font from dafont.com public domain fonts

        //Lets the phone choose the input-method, in case of physical keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(EditTextLocation, InputMethodManager.SHOW_IMPLICIT);

        group = (RadioGroup) findViewById(R.id.radioGroup);
        zip = (RadioButton) findViewById(R.id.RBZip);
        city = (RadioButton) findViewById(R.id.RBCity);
        celciusOrF = (ToggleButton) findViewById(R.id.unitsButton);
        go = (Button) findViewById(R.id.button);
        EditTextLocation = (EditText) findViewById(R.id.editTextCity);
        banner.setTypeface(customFont);
        go.setTypeface(customFont);
        zip.setTypeface(customFont);
        city.setTypeface(customFont);
        EditTextLocation.setTypeface(customFont);

        go.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
                int SelectedRadioButton = group.getCheckedRadioButtonId(); //Zip or city
                SharedPreferences.Editor editor = sharedPreferences.edit(); //writes the key value pair
                String location = EditTextLocation.getText().toString();//user input

                if (SelectedRadioButton == zip.getId())
                    editor.putString("location", "zip=" + location);
                else
                    editor.putString("location", "q=" + location);

                sharedPreferences = getSharedPreferences("celcius", Context.MODE_MULTI_PROCESS);

                if (celciusOrF.isChecked())
                    editor.putBoolean("celcius", true);
                else
                    editor.putBoolean("celcius", false);

                editor.commit();//Finalize key value pairs for the parent activity
                finish();
                startActivity(new Intent(getApplicationContext(), MainDisplay.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_and_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
