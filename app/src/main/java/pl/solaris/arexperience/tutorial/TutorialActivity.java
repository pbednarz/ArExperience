package pl.solaris.arexperience.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import pl.solaris.arexperience.R;
import pl.solaris.arexperience.metaio.ContentLoaderActivity;
import pl.solaris.arexperience.view.CircleIndicator;


public class TutorialActivity extends ActionBarActivity {

    private ViewPager defaultViewpager;
    private View rlRoot;
    private List<Integer> colorList;
    private int time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        rlRoot = findViewById(R.id.root);
        colorList = new ArrayList<>();
        rlRoot.setBackgroundColor(Color.rgb(0, 187, 211));
        colorList.add(Color.rgb(0, 187, 211));
        colorList.add(Color.rgb(255, 167, 37));
        colorList.add(Color.rgb(52, 172, 113));
        defaultViewpager = (ViewPager) findViewById(R.id.pager);
        defaultViewpager.setOffscreenPageLimit(2);
        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator);
        final DemoPagerAdapter defaultPagerAdapter = new DemoPagerAdapter(getSupportFragmentManager());
        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(defaultViewpager);
        defaultIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                time++;
                if (positionOffset == 0) {
                    rlRoot.setBackgroundColor(colorList.get(position));
                } else if (time % 3 == 0) {
                    rlRoot.setBackgroundColor(blendColors(colorList.get(position + 1), colorList.get(position), positionOffset));
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });
        defaultViewpager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                if (position > -1 && position < 1) {
                    view.findViewById(R.id.image1).setTranslationX((float) (position * 0.1 * pageWidth));
                    view.findViewById(R.id.image2).setTranslationX((float) (position * 0.2 * pageWidth));
                    view.findViewById(R.id.image3).setTranslationX((float) (position * 0.5 * pageWidth));
                }
            }
        });

        findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this, ContentLoaderActivity.class));
                finish();
            }
        });
    }

    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        final int r = (int) ((((color1 >> 16) & 0xFF) * ratio) + (((color2 >> 16) & 0xFF) * inverseRation));
        final int g = (int) ((((color1 >> 8) & 0xFF) * ratio) + (((color2 >> 8) & 0xFF) * inverseRation));
        final int b = (int) (((color1 & 0xFF) * ratio) + ((color2 & 0xFF) * inverseRation));
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public class DemoPagerAdapter extends FragmentPagerAdapter {

        public DemoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return TutorialFragment.newInstance(i);
        }

        @Override
        public int getCount() {
            return colorList.size();
        }
    }
}
