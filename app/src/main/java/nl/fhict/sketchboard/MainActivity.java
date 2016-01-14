package nl.fhict.sketchboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.fhict.sketchboard.utils.RecentWrapper;
import nl.fhict.sketchboard.utils.SaveAndLoadManager;

public class MainActivity extends AppCompatActivity {
    private boolean mFabIsInCrossState = false;

    // Te groot om mee te geven via Intent dus dan maar zo.
    public static RecentWrapper recentDesign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SaveAndLoadManager.init();

        final AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.main_appbar_layout);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<BitmapWrapper> recents = new ArrayList<>();
        List<NameObject> objects = SaveAndLoadManager.loadAll(10);
        for(NameObject e : objects)
        {
            if(e.getObject() instanceof RecentWrapper)
            {
                   recents.add(new BitmapWrapper(e.getName(), ((RecentWrapper) e.getObject()).getRecentmap()));
            }
        }

        final TestRecyclerAdapter adapter = new TestRecyclerAdapter(recents, this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TestRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Object object = SaveAndLoadManager.load(text);
                if (object != null) {
                    Intent intent = new Intent(MainActivity.this, CompositionActivity.class);
                    recentDesign = (RecentWrapper) object;
                    intent.putExtra("File", "Lulz"/*(RecentWrapper) object*/);
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
        templates.add(R.drawable.longboard2);
        templates.add(R.drawable.longbord1);
        templates.add(R.drawable.skateboard1);
        final NewBoardRecyclerAdapter adapternewb = new NewBoardRecyclerAdapter(templates,this);
        overlay.setAdapter(adapternewb);
        adapternewb.setOnItemClickListener(new NewBoardRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Intent intent = new Intent(MainActivity.this, CompositionActivity.class);
                int newboard = Integer.valueOf(text);
                intent.putExtra("NewBoard", newboard);

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
