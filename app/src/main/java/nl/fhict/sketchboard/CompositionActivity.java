package nl.fhict.sketchboard;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import nl.fhict.sketchboard.layers.DrawingLayer;
import nl.fhict.sketchboard.layers.DrawingPoint;
import nl.fhict.sketchboard.layers.ImageLayer;
import nl.fhict.sketchboard.layers.Layerable;
import nl.fhict.sketchboard.layers.TextLayer;
import nl.fhict.sketchboard.utils.RecentWrapper;
import nl.fhict.sketchboard.utils.SaveAndLoadManager;

public class CompositionActivity extends AppCompatActivity implements ColorPicker.OnColorChangedListener {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final int RESULT_LOAD_IMAGE = 1;

    List<Layerable> layers = new ArrayList<>();
    Layerable activeLayer;

    DrawingView drawingView;
    int yourStep = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_linear_layout);

        // Creates a new drawing view and adds it to the main linear layout.
        drawingView = new DrawingView(getApplicationContext());
        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (activeLayer != null && activeLayer instanceof DrawingLayer) {
                    float touchX = event.getX();
                    float touchY = event.getY();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Paint p = new Paint();
                            p.setColor(Color.BLACK);
                            p.setAntiAlias(true);
                            p.setStrokeWidth(20);
                            p.setStrokeCap(Paint.Cap.ROUND);
                            ((DrawingLayer) activeLayer).addPoint(new DrawingPoint(touchX, touchY, p));
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        default:
                            return false;
                    }
                    drawLayers();
                }
                return true;
            }
        });
        mainLayout.addView(drawingView);

        if (getIntent().hasExtra("File")) {
            RecentWrapper rw = MainActivity.recentDesign;
            //RecentWrapper rw = (RecentWrapper) getIntent().getSerializableExtra("File");

            if (rw != null){
                this.layers = rw.getLayers();
            }
        } else if (getIntent().hasExtra("NewBoard")) {
            Bitmap skateboard = null;
            DisplayMetrics display = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(display);
            int chosenBoard = getIntent().getIntExtra("NewBoard", 1);
            if(chosenBoard == 0){
                skateboard = getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.longboard2), display.heightPixels, display.widthPixels);
            }else if(chosenBoard == 1){
                skateboard = getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.longbord1), display.heightPixels, display.widthPixels);
            }else if(chosenBoard == 2){
                skateboard = getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.skateboard1), display.heightPixels, display.widthPixels);
            }
            layers.add(new ImageLayer(skateboard, new PointF(0, 0)));
        }

        //draw layers nadat canvas gevuld is.
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(drawingView.getCanvas() == null){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawLayers();
                    }
                });
            }
        }).start();

//        // Creates a new drawing view and adds it to the main linear layout.
//        drawingView = new DrawingView(getApplicationContext());
//        drawingView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (activeLayer != null && activeLayer instanceof DrawingLayer){
//                    float touchX = event.getX();
//                    float touchY = event.getY();
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            Paint p = new Paint();
//                            p.setColor(Color.BLACK);
//                            p.setAntiAlias(true);
//                            p.setStrokeWidth(20);
//                            p.setStrokeCap(Paint.Cap.ROUND);
//                            ((DrawingLayer) activeLayer).addPoint(new DrawingPoint(touchX, touchY, p));
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            break;
//                        default:
//                            return false;
//                    }
//                    drawLayers();
//                }
//                return true;
//            }
//        });
//        mainLayout.addView(drawingView);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        final FloatingActionButton drawerbutton = (FloatingActionButton) findViewById(R.id.drawerbutton);

        drawerbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        final FloatingActionButton layoutbutton = (FloatingActionButton) findViewById(R.id.layerbutton);

        layoutbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

//        Bitmap skateboard = null;
//        DisplayMetrics display = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(display);
//        int chosenBoard = getIntent().getIntExtra("NewBoard", 1);
//        if(chosenBoard == 0){
//            skateboard = getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.longboard2), display.heightPixels, display.widthPixels);
//        }else if(chosenBoard == 1){
//            skateboard = getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.longbord1), display.heightPixels, display.widthPixels);
//        }else if(chosenBoard == 2){
//            skateboard = getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.skateboard1), display.heightPixels, display.widthPixels);
//        }
//        layers.add(new ImageLayer(skateboard, new PointF(0, 0)));
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(drawingView.getCanvas() == null){
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        drawLayers();
//                    }
//                });
//            }
//        }).start();

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
                        LayoutInflater cpinflater = (LayoutInflater) CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View cplayout = cpinflater.inflate(R.layout.dialog_pickcolor, (ViewGroup) findViewById(R.id.your_dialog_root_element));
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
                        LayoutInflater inflater = (LayoutInflater) CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialog_changesize, (ViewGroup) findViewById(R.id.your_dialog_root_element));
                        yourDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        yourDialog.setContentView(layout);


                        final TextView yourDialogTextview = (TextView) layout.findViewById(R.id.your_dialog_textview);
                        final SeekBar yourDialogSeekBar = (SeekBar) layout.findViewById(R.id.your_dialog_seekbar);
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
                                progress = ((int) Math.round(progress / yourStep)) * yourStep;
                                yourDialogSeekBar.setProgress(progress);
                                yourDialogTextview.setText("Line Width : " + (10 + progress));
                                //add code here
                            }
                        };
                        yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);

                        return true;
                    case R.id.menu_nav_switch_eraser:
                        if (drawingView.isInEraserMode()) {
                            drawingView.setEraserMode(false);
                        } else {
                            drawingView.setEraserMode(true);
                        }
                        return true;
                    case R.id.menu_nav_new_drawing_layer:
                        addLayer(new DrawingLayer());
                        return true;
                    case R.id.menu_nav_save:
                        final Dialog saveDialog = new Dialog(CompositionActivity.this);
                        LayoutInflater saveInflater = (LayoutInflater) CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View saveLayout = saveInflater.inflate(R.layout.dialog_save, (ViewGroup) findViewById(R.id.your_dialog_root_element));
                        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        saveDialog.setContentView(saveLayout);

                        final EditText saveEditText = (EditText) saveLayout.findViewById(R.id.saveEnteredText);
                        final Button saveAccept = (Button) saveLayout.findViewById(R.id.saveChoiceAccept);
                        final Button saveDecline = (Button) saveLayout.findViewById(R.id.saveChoiceDecline);


                        saveAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String fileName = saveEditText.getText().toString();
                                if (isAlpha(fileName)) {

                                    if (SaveAndLoadManager.save(fileName + ".sb", new RecentWrapper(layers, drawingView.getCanvasBitmap()))) {
                                        Toast.makeText(getApplicationContext(), "Succesvol opgeslagen.",
                                                Toast.LENGTH_LONG).show();


                                    } else {
                                        Toast.makeText(getApplicationContext(), "Er ging iets fout tijdens het opslaan.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Alleen letters zijn toegestaan.",
                                            Toast.LENGTH_LONG).show();
                                }
                                saveDialog.cancel();
                            }
                        });

                        saveDecline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveDialog.cancel();
                            }
                        });

                        saveDialog.show();

                        return true;
                    case R.id.menu_nav_load_image:
                        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE);
                        return true;
                    case R.id.menu_nav_insert_text:
                        final Dialog textDialog = new Dialog(CompositionActivity.this);
                        final LayoutInflater textInflator = (LayoutInflater) CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View textLayout = textInflator.inflate(R.layout.dialog_text, (ViewGroup) findViewById(R.id.your_dialog_root_element));
                        textDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        textDialog.setContentView(textLayout);

                        final Button textAccept = (Button) textLayout.findViewById(R.id.textChoiceAccept);
                        final Button textDecline = (Button) textLayout.findViewById(R.id.textChoiceDecline);
                        final EditText editText = (EditText) textLayout.findViewById(R.id.enteredText);


                        final TextView textDialogTextView = (TextView) textLayout.findViewById(R.id.textSizeTextView);
                        final SeekBar textDialogSeekbar = (SeekBar) textLayout.findViewById(R.id.textSizeSeekbar);
                        drawingView.setTextSize(80);//set default
                        textDialogSeekbar.setMax(290);
                        textDialogSeekbar.setProgress((int) (drawingView.getTextSize() - 10));
                        textDialogTextView.setText("Text size : " + drawingView.getTextSize());
                        SeekBar.OnSeekBarChangeListener SeekBarListener = new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                drawingView.setTextSize(textDialogSeekbar.getProgress() + 10);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                                textDialogSeekbar.setProgress(progress);
                                textDialogTextView.setText("Text size : " + (10 + progress));
                            }
                        };
                        textDialogSeekbar.setOnSeekBarChangeListener(SeekBarListener);

                        final TextView textDialogTextViewWidth = (TextView) textLayout.findViewById(R.id.widthSizeTextView);
                        final SeekBar textDialogSeekbarWidth = (SeekBar) textLayout.findViewById(R.id.widthSizeSeekbar);
                        textDialogSeekbarWidth.setMax(39);
                        drawingView.setStrokeWidth(10);
                        textDialogSeekbarWidth.setProgress(drawingView.getStrokeWidth() - 1);
                        textDialogTextViewWidth.setText("Stroke width : " + drawingView.getStrokeWidth());

                        SeekBar.OnSeekBarChangeListener SeekBarListenerWidth = new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                drawingView.setStrokeWidth(textDialogSeekbarWidth.getProgress() + 1);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                                textDialogSeekbarWidth.setProgress(progress);
                                textDialogTextViewWidth.setText("Stroke width : " + (1 + progress));
                            }
                        };
                        textDialogSeekbarWidth.setOnSeekBarChangeListener(SeekBarListenerWidth);


                        textAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Paint p = new Paint();
                                p.setColor(drawingView.getPaintColor());
                                p.setTextSize(textDialogSeekbar.getProgress());
                                p.setStrokeWidth(textDialogSeekbarWidth.getProgress());
                                addLayer(new TextLayer(editText.getText().toString(), p));
                                textDialog.cancel();
                            }
                        });

                        textDecline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                textDialog.cancel();
                            }
                        });

                        textDialog.show();

                        return true;
                    case R.id.menu_nav_preview:
                        Toast.makeText(getApplicationContext(), "Preview",
                                Toast.LENGTH_LONG).show();
                        Dialog previewDialog = new Dialog(CompositionActivity.this);
                        LayoutInflater previewinflater = (LayoutInflater) CompositionActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View previewlayout = previewinflater.inflate(R.layout.dialog_preview, (ViewGroup) findViewById(R.id.your_dialog_root_element));
                        previewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        previewDialog.setContentView(previewlayout);

                        final ImageView previewimage = (ImageView) previewlayout.findViewById(R.id.previewdialog_image);
                        previewimage.setImageBitmap(drawingView.getCanvasBitmap());
                        previewDialog.show();


                        return true;
                    default:
                        return true;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            addLayer(new ImageLayer(BitmapFactory.decodeFile(picturePath), new PointF(5, 5)));
        }
    }


    @Override
    public void onColorChanged(int color) {

    }

    public void addLayer(Layerable layer) {
        this.layers.add(layer);
        activeLayer = layers.get(layers.size() - 1);
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        this.drawLayers();
    }

    public void drawLayers() {
        if(drawingView.getCanvas() != null) {
            for (Layerable layer : this.layers) {
                layer.draw(this.drawingView.getCanvas());
            }
        }
        drawingView.invalidate();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int actionBarHeight = 168;//default actionbar height bij mij
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight - (actionBarHeight/2)) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public List<Layerable> getLayers() {
        return layers;
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
}