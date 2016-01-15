package nl.fhict.sketchboard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    private Bitmap boardtype;
    List<Layerable> layers = new ArrayList<>();
    Layerable activeLayer;

    DrawingView drawingView;
    int yourStep = 10;

    private DrawerLayout mDrawer;

    private boolean mIsInMovingMode = false;
    private boolean mIsFinishAllowed = false;

    private final PointF mInitialMovingLayerLocation = new PointF(),
            mInitialTouchLocation = new PointF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);
        ViewGroup mainLayout = (ViewGroup) findViewById(R.id.main_linear_layout);

        final FloatingActionButton moveFab = (FloatingActionButton)findViewById(R.id.move_toggle_button);
        moveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsInMovingMode = !mIsInMovingMode;
                moveFab.setImageResource(mIsInMovingMode ? R.drawable.ic_done_black_24dp : R.drawable.ic_open_with_black_24dp);
            }
        });

        // Creates a new drawing view and adds it to the main linear layout.
        drawingView = new DrawingView(getApplicationContext());
        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsInMovingMode) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mInitialTouchLocation.set(event.getX(), event.getY());
                            mInitialMovingLayerLocation.set(activeLayer.getX(), activeLayer.getY());
                            break;
                        case MotionEvent.ACTION_MOVE:
                            activeLayer.setPosition(
                                    mInitialMovingLayerLocation.x + event.getX() - mInitialTouchLocation.x,
                                    mInitialMovingLayerLocation.y + event.getY() - mInitialTouchLocation.y
                            );
                            drawLayers();
                            break;
                    }
                } else if (activeLayer != null && activeLayer instanceof DrawingLayer) {
                    float touchX = event.getX();
                    float touchY = event.getY();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Paint p = new Paint();
                            p.setColor(drawingView.getPaintColor());
                            p.setAntiAlias(true);
                            p.setStrokeWidth(drawingView.getStrokeWidth());
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
            this.boardtype = MainActivity.boardtype;
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



        mDrawer = (DrawerLayout) findViewById(R.id.main_drawer);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        final FloatingActionButton drawerbutton = (FloatingActionButton) findViewById(R.id.drawerbutton);

        drawerbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.RIGHT);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        final FloatingActionButton layoutbutton = (FloatingActionButton) findViewById(R.id.layerbutton);

        layoutbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        nl.fhict.sketchboard.RecyclerListViewFragment fragment = new nl.fhict.sketchboard.RecyclerListViewFragment();


        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, FRAGMENT_LIST_VIEW)
                .commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawer.closeDrawers();

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

                        addLayer(new DrawingLayer());
                        return true;
                    case R.id.menu_nav_switch_eraser:
                        if (drawingView.isInEraserMode()) {
                            drawingView.setEraserMode(false);
                        } else {
                            drawingView.setEraserMode(true);
                        }
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
                        previewimage.setImageBitmap( maskpreview(drawingView.getCanvasBitmap()));
                        previewDialog.show();


                        return true;
                    case R.id.menu_nav_delete:
                        new android.support.v7.app.AlertDialog.Builder(CompositionActivity.this)
                                .setTitle("Weet je zeker dat je deze laag wil verwijderen?")
                                .setNegativeButton("Annuleer", null)
                                .setPositiveButton("Verwijder", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeLayer(activeLayer);
                                    }
                                })
                                .create().show();
                        return true;
                    case R.id.menu_nav_rotate:
                        Dialog rotateDialog = new Dialog(CompositionActivity.this);
                        LayoutInflater rotateInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                        View rotateLayout = rotateInflater.inflate(R.layout.dialog_rotate,
                                (ViewGroup)findViewById(R.id.your_dialog_root_element));
                        rotateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        rotateDialog.setContentView(rotateLayout);


                        final TextView yourRotateDialogTextview = (TextView) rotateLayout.findViewById(R.id.your_dialog_textview);
                        final SeekBar yourRotateDialogSeekBar = (SeekBar) rotateLayout.findViewById(R.id.your_dialog_seekbar);
                        yourRotateDialogSeekBar.setProgress(activeLayer.getRotationAngle());
                        yourRotateDialogTextview.setText(String.format("Rotatie in graden: %d", activeLayer.getRotationAngle()));
                        rotateDialog.show();
                        SeekBar.OnSeekBarChangeListener yourRotateSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                                yourRotateDialogTextview.setText(String.format("Rotatie in graden: %d", progress));
                                activeLayer.setRotationAngle(progress);
                                drawLayers();
                            }
                        };
                        yourRotateDialogSeekBar.setOnSeekBarChangeListener(yourRotateSeekBarListener);
                        return true;
                    case R.id.menu_nav_scale:
                        if (activeLayer instanceof ImageLayer) {
                            Dialog scaleDialog = new Dialog(CompositionActivity.this);
                            LayoutInflater scaleInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                            View scaleLayout = scaleInflater.inflate(R.layout.dialog_scale,
                                    (ViewGroup)findViewById(R.id.your_dialog_root_element));
                            scaleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            scaleDialog.setContentView(scaleLayout);


                            final TextView yourScaleDialogTextview = (TextView) scaleLayout.findViewById(R.id.your_dialog_textview);
                            final SeekBar yourScaleDialogSeekBar = (SeekBar) scaleLayout.findViewById(R.id.your_dialog_seekbar);
                            yourScaleDialogSeekBar.setProgress(Math.round(((ImageLayer)activeLayer).getScale() * 100));
                            yourScaleDialogTextview.setText(String.format("Schaal in procent: %d", Math.round(((ImageLayer)activeLayer).getScale() * 100)));
                            scaleDialog.show();
                            SeekBar.OnSeekBarChangeListener yourScaleSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {}

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {}

                                @Override
                                public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                                    if (progress < 1) progress = 1;
                                    yourScaleDialogTextview.setText(String.format("Schaal in procent: %d", progress));
                                    ((ImageLayer)activeLayer).setScale(progress / 100f);
                                    drawLayers();
                                }
                            };
                            yourScaleDialogSeekBar.setOnSeekBarChangeListener(yourScaleSeekBarListener);
                        } else {
                            Toast.makeText(CompositionActivity.this, "Alleen Image Layers kunnen geschaald worden", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        if (layers != null || layers.isEmpty()) {
            activeLayer = layers.get(layers.size() - 1);
        }
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

            Bitmap map = BitmapFactory.decodeFile(picturePath);
            map = getResizedBitmap(map, map.getHeight()/5, map.getWidth()/5);

            addLayer(new ImageLayer(map, new PointF(5, 5)));
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsFinishAllowed) {
            super.onBackPressed();
            return;
        }

        mDrawer.closeDrawers();

        mIsFinishAllowed = true;
        Toast.makeText(this, "Druk nogmaals om te sluiten", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsFinishAllowed = false;
            }
        }, 4000);
    }

    @Override
    public void onColorChanged(int color) {

    }

    public void addLayer(Layerable layer) {
        this.layers.add(layer);
        reloadLayers();
    }

    public void removeLayer(Layerable layer) {
        this.layers.remove(layer);
        reloadLayers();
    }

    private void reloadLayers() {
        activeLayer = layers.get(layers.size() - 1);
        Fragment frg = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        this.drawLayers();
    }

    public void drawLayers() {
        final Canvas canvas = drawingView.getCanvas();
        if (canvas != null) {
            canvas.drawColor(getResources().getColor(R.color.palette_background));
            for (Layerable layer : layers) {
                if (layer.getRotationAngle() > 0) {
                    final PointF center = layer.getRotationCenter();
                    canvas.save();
                    canvas.rotate(layer.getRotationAngle(), center.x, center.y);
                    layer.draw(canvas);
                    canvas.restore();
                } else {
                    layer.draw(canvas);
                }
            }
        }
        drawingView.invalidate();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int actionBarHeight = 168;//default actionbar height bij mij
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight - (actionBarHeight / 2)) / height;
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
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public LayerListItemAdapter.OnItemClickListener getLayerListClickAdapter() {
        return new LayerListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Layerable layer) {
                Toast.makeText(CompositionActivity.this, layer.getName().getValue() +
                        " is geselecteerd als actieve laag", Toast.LENGTH_LONG).show();
                activeLayer = layer;
            }
        };
    }

    public Bitmap maskpreview(Bitmap s)
    {
        Bitmap original = s;
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        Bitmap mask = BitmapFactory.decodeResource(getResources(), R.drawable.longbord1);
        if(boardtype == null) {
            int chosenBoard = getIntent().getIntExtra("NewBoard", 1);
            if (chosenBoard == 0) {
                mask = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.longboard2), display.heightPixels, display.widthPixels);
            } else if (chosenBoard == 1) {
                mask = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.longbord1), display.heightPixels, display.widthPixels);
            } else if (chosenBoard == 2) {
                mask = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.skateboard1), display.heightPixels, display.widthPixels);
            }
        }
        else
        {
            System.out.println("niet null");
            mask = getResizedBitmap(boardtype, display.heightPixels, display.widthPixels);
        }

        //You can change original image here and draw anything you want to be masked on it.

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);


        Canvas tempCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        tempCanvas.drawBitmap(original, 0, 0, null);
        tempCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        return result;

    }
}