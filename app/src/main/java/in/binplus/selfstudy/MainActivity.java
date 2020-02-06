package in.binplus.selfstudy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.binplus.selfstudy.R;

import in.binplus.selfstudy.adapters.NavigationAdapter;
import in.binplus.selfstudy.fragments.AboutUsFragment;
import in.binplus.selfstudy.fragments.ContactUsFragment;
import in.binplus.selfstudy.fragments.HomeFragment;
import in.binplus.selfstudy.fragments.LiveTvFragment;
import in.binplus.selfstudy.fragments.MoviesFragment;
import in.binplus.selfstudy.fragments.OrderHistoryFragment;
import in.binplus.selfstudy.fragments.PrivacyFragment;
import in.binplus.selfstudy.fragments.TermsFragment;
import in.binplus.selfstudy.fragments.TvSeriesFragment;
import in.binplus.selfstudy.models.NavigationModel;
import in.binplus.selfstudy.nav_fragments.CountryFragment;
import in.binplus.selfstudy.nav_fragments.FavoriteFragment;
import in.binplus.selfstudy.nav_fragments.GenreFragment;
import in.binplus.selfstudy.nav_fragments.MainHomeFragment;
import in.binplus.selfstudy.utils.Constants;
import in.binplus.selfstudy.utils.SpacingItemDecoration;
import in.binplus.selfstudy.utils.Tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Serializable {


    Fragment f;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private LinearLayout navHeaderLayout;

    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list =new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status=false;

    private FirebaseAnalytics mFirebaseAnalytics;
    boolean isDark;
    private String navMenuStyle;

    private Switch themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        isDark=false;
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navMenuStyle = Constants.NAV_MENU_STYLE;

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //----dark mode----------

        if (sharedPreferences.getBoolean("firstTime", true) == true) {
            showTermServicesDialog();
        }




        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navHeaderLayout = findViewById(R.id.nav_head_layout);
        themeSwitch = findViewById(R.id.theme_switch);

        if (isDark) {
            themeSwitch.setChecked(true);
        }else {
            themeSwitch.setChecked(false);
        }


        //----navDrawer------------------------
        toolbar = findViewById(R.id.toolbar);
        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
        }
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        navItemImage=getResources().getStringArray(R.array.nav_item_image);
        navItemImage2=getResources().getStringArray(R.array.nav_item_image_2);


        navItemName2=getResources().getStringArray(R.array.nav_item_name_2);




        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
//        if (navMenuStyle.equals("grid")) {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//            recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
//        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
      //  }
        recyclerView.setHasFixedSize(true);


        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        status = prefs.getBoolean("status",false);

        if (status){
            for (int i = 0; i< navItemName.length; i++){
                NavigationModel models =new NavigationModel(navItemImage[i], navItemName[i]);
                list.add(models);
            }
        }else {
            for (int i=0;i<navItemName2.length;i++){
                NavigationModel models =new NavigationModel(navItemImage2[i],navItemName2[i]);
                list.add(models);
            }
        }


        //set data and list adapter
        mAdapter = new NavigationAdapter(this, list, navMenuStyle);
        recyclerView.setAdapter(mAdapter);


        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {

                Log.e("POSITION OF NAV:::", String.valueOf(position));

                //----action for click items nav---------------------

                if (position==0){
                    loadFragment(new MainHomeFragment());
                }
                else if (position==7){

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
//                    String shareBody = HomeFragment.share_string;
//                    String shareSub = HomeFragment.share_string;
                    String shareBody = "share";
                    String shareSub = "Share";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));

//                    loadFragment(new MoviesFragment());
                }
                else if (position==4){
                    if (isLogedIn())
                    {
                        loadFragment(new AboutUsFragment());
                    }
                    else
                    {

                        loadFragment(new TermsFragment());
                    }
                }
                else if (position==5){
                    if(isLogedIn())
                    {
//                        Intent intent=new Intent(MainActivity.this,EnquiryActivity.class);
//                        startActivity(intent);
                        // loadFragment(new ConatctUsFragment());
                    }
                    else
                    {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        //String shareBody = HomeFragment.share_string;
//                    String shareSub = HomeFragment.share_string;
                        String shareBody = "share";
                        String shareSub = "Share";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));

                    }
                }
                else if (position==6){
                    if(isLogedIn())
                    {
                        loadFragment(new TermsFragment());
                    }
                    else
                    {
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }
                }
                else if (position==12){
                    loadFragment(new PrivacyFragment());
                }else if (position==3){
                    if(isLogedIn())
                    {
                        loadFragment(new OrderHistoryFragment());
                    }
                    else
                    {
                        loadFragment(new ContactUsFragment());


                    }
                }
                else if (position==2){
                    if(isLogedIn())
                    {
                        loadFragment(new FavoriteFragment());

                    }
                    else
                    {
                        loadFragment(new AboutUsFragment());
                    }

                }
                else {
                    if (status){

                        if (position==1){
                            if(isLogedIn())
                            {
                                Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                                startActivity(intent);

                            }
                            else
                            {
                                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);

                            }
                        }

                        else if (position==8){
                            new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure to logout ?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                            editor.putBoolean("status",false);
                                            editor.apply();

                                            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                        }
                        else if (position==11){
                            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }else {
                        // if (position==6){
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
//                        }
//                        else if (position==7){
//                            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
//                            startActivity(intent);
//                            MainActivity.this.finish();
//                        }
                    }

                }



                //----behaviour of bg nav items-----------------
                if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")){

                    if (isDark){
                        mAdapter.chanColor(viewHolder[0],position,R.color.nav_bg);
                    }else {
                        mAdapter.chanColor(viewHolder[0],position,R.color.white);
                    }


                    if (navMenuStyle.equals("grid123")) {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        holder.name.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        holder.selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_grey_transparent));
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }

                    viewHolder[0] =holder;
                }

                mDrawerLayout.closeDrawers();
            }
        });

        //----external method call--------------
        loadFragment(new MainHomeFragment());

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark",true);
                    editor.apply();

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark",false);
                    editor.apply();
                }

                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        if(savedInstanceState ==null)
        {
            MainHomeFragment fragment=new MainHomeFragment();
            FragmentManager fragmentManager= getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    android.app.Fragment fr = getFragmentManager().findFragmentById(R.id.fragment_container);

                    final String fm_name = fr.getClass().getSimpleName();
                    Log.e("backstack: ", ": " + fm_name);

                    if (fm_name.contentEquals("MainHomeFragment")) {

                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        toggle.setDrawerIndicatorEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();

                    } else {

                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        toggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toggle.syncState();

                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //onBackPressed();
                            }
                        });
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }


    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }
        return false;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                        intent.putExtra("q",s);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
//            mDrawerLayout.closeDrawers();
//        }else {
//
//            new AlertDialog.Builder(MainActivity.this).setMessage("Do you want to exit ?")
//                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                            System.exit(0);
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    }).create().show();
//
//        }
//    }


    @Override
    public void onBackPressed() {
        f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (f instanceof MainHomeFragment) {
//
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Smart Self Study");
            builder.setIcon( R.drawable.icon );
            builder.setMessage("Are you sure want to exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                    //  getActivity().finishAffinity();


                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            android.app.AlertDialog dialog=builder.create();
            dialog.show();

        } else {
            super.onBackPressed();
        }
    }


    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_of_services);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        Button declineBt = dialog.findViewById(R.id.bt_decline);
        Button acceptBt = dialog.findViewById(R.id.bt_accept);

        if (isDark) {
            declineBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline));
            acceptBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
        }

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        acceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                editor.putBoolean("firstTime",false);
                editor.apply();
                dialog.dismiss();
            }
        });

        declineBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public boolean isLogedIn() {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        return preferences.getBoolean("status", false);

    }
}
