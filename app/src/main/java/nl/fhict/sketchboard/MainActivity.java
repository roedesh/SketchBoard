package nl.fhict.sketchboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import nl.fhict.sketchboard.layers.LayerWrapper;
import nl.fhict.sketchboard.layers.Layerable;
import nl.fhict.sketchboard.utils.SaveAndLoadManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private boolean mFabIsInCrossState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SaveAndLoadManager.init();

        final AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.main_appbar_layout);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Bitmap> recents = new ArrayList<>();


        final TestRecyclerAdapter adapter = new TestRecyclerAdapter(recents);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TestRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Object object = SaveAndLoadManager.load(text);
                if (object != null) {
                    Intent intent = new Intent(MainActivity.this, CompositionActivity.class);
                    intent.putExtra("File", new LayerWrapper((List<Layerable>) object));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Openen mislukt.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        final RecyclerView overlay = (RecyclerView)findViewById(R.id.main_overlay_recycler);
        overlay.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<Integer> templates = new ArrayList<>();
        templates.add(R.drawable.skatetemplate);
        templates.add(R.drawable.skatetemplate2);
        templates.add(R.drawable.skatetemplate3);
        final NewBoardRecyclerAdapter adapternewb = new NewBoardRecyclerAdapter(templates);
        overlay.setAdapter(adapternewb);
        adapternewb.setOnItemClickListener(new NewBoardRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Intent intent = new Intent(MainActivity.this, CompositionActivity.class);
                intent.putExtra("NewBoard", templates.get(Integer.valueOf(text)));
                System.out.println(text);
                startActivity(intent);
            }
        });

        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFabIsInCrossState) {
                    appBarLayout.setExpanded(false);
                    mFabIsInCrossState = true;
                    overlay.setVisibility(View.VISIBLE);
                    fab.animate().rotation(-45f);
                } else {
                    mFabIsInCrossState = false;
                    overlay.setVisibility(View.GONE);
                    fab.animate().rotation(0f);
                }
            }
        });
    }
}
