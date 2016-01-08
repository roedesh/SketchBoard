package nl.fhict.sketchboard;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.util.ArrayList;
import java.util.List;

public class CompositionActivity extends AppCompatActivity  implements ColorPicker.OnColorChangedListener {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    List<StableString> strings = new ArrayList<>();

    DrawingView drawingView;
    int yourStep = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_linear_layout);

        // Creates a new drawing view and adds it to the main linear layout.
        drawingView = new DrawingView(getApplicationContext());
        mainLayout.addView(drawingView);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);

        strings.add(new StableString("Layer 1"));
        strings.add(new StableString("Layer 2"));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new nl.fhict.sketchboard.RecyclerListViewFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();

                // Handle item selection
                switch (item.getItemId()) {
                    case R.id.menu_nav_change_color:
                        final Dialog cpdialog = new Dialog(CompositionActivity.this);
                        LayoutInflater cpinflater = (LayoutInflater)CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View cplayout = cpinflater.inflate(R.layout.dialog_pickcolor, (ViewGroup)findViewById(R.id.your_dialog_root_element));
                        cpdialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                        cpdialog.setContentView(cplayout);
                        cpdialog.setCancelable(false);

                        final ColorPicker picker = (ColorPicker) cplayout.findViewById(R.id.picker);
                        SVBar svBar = (SVBar) cplayout.findViewById(R.id.svbar);
                        OpacityBar opacityBar = (OpacityBar) cplayout.findViewById(R.id.opacitybar);
                        SaturationBar saturationBar = (SaturationBar) cplayout.findViewById(R.id.saturationbar);
                        ValueBar valueBar = (ValueBar) cplayout.findViewById(R.id.valuebar);
                        Button cpaccept = (Button) cplayout.findViewById(R.id.colorchoiceaccept);
                        Button cpdecline = (Button) cplayout.findViewById(R.id.colorchoicedecline);

                        picker.addSVBar(svBar);
                        picker.addOpacityBar(opacityBar);
                        picker.addSaturationBar(saturationBar);
                        picker.addValueBar(valueBar);

                        picker.setColor(drawingView.getPaintColor());

                        picker.setOldCenterColor(drawingView.getPaintColor());

                        picker.setOnColorChangedListener(CompositionActivity.this);

                        picker.setShowOldCenterColor(true);

                        cpaccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawingView.setPaintColor(picker.getColor());
                                cpdialog.cancel();
                            }
                        });

                        cpdecline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cpdialog.cancel();
                            }
                        });

                        cpdialog.show();
                        return true;

                    case R.id.menu_nav_change_size:
                        Dialog yourDialog = new Dialog(CompositionActivity.this);
                        LayoutInflater inflater = (LayoutInflater)CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialog_changesize, (ViewGroup)findViewById(R.id.your_dialog_root_element));
                        yourDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        yourDialog.setContentView(layout);


                        final TextView yourDialogTextview = (TextView)layout.findViewById(R.id.your_dialog_textview);
                        final SeekBar yourDialogSeekBar = (SeekBar)layout.findViewById(R.id.your_dialog_seekbar);
                        yourDialogSeekBar.setProgress(drawingView.getStrokeWidth() - 10);
                        yourDialogTextview.setText("Line Width : " + drawingView.getStrokeWidth());
                        yourDialog.show();
                        SeekBar.OnSeekBarChangeListener yourSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                drawingView.setStrokeWidth(yourDialogSeekBar.getProgress() + 10);
                                //add code here
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                //add code here
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                                progress = ((int)Math.round(progress/yourStep ))*yourStep;
                                yourDialogSeekBar.setProgress(progress);
                                yourDialogTextview.setText("Line Width : " + (10 + progress));
                                //add code here
                            }
                        };
                        yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);

                        return true;
                    case R.id.menu_nav_switch_eraser:
                        if (drawingView.isInEraserMode()){
                            drawingView.setEraserMode(false);
                        }
                        else {
                            drawingView.setEraserMode(true);
                        }
                        return true;
                    case R.id.menu_nav_save:
                        Toast.makeText(getApplicationContext(), "Test saving.",
                                Toast.LENGTH_LONG).show();
                    case R.id.menu_nav_load_image:
                        
                        return true;
                    default:
                        return true;
                }
            }
        });




    }

    @Override
    public void onColorChanged(int color) {

    }

    public List<StableString> getStableString(){
        return strings;
    }

}