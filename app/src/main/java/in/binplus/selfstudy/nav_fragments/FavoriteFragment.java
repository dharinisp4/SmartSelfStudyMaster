package in.binplus.selfstudy.nav_fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.binplus.selfstudy.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.TextSliderView;

import in.binplus.selfstudy.adapters.CommonGridAdapter;
import in.binplus.selfstudy.models.CommonModels;
import in.binplus.selfstudy.utils.ApiResources;
import in.binplus.selfstudy.utils.NetworkInst;
import in.binplus.selfstudy.utils.SpacingItemDecoration;
import in.binplus.selfstudy.utils.ToastMsg;
import in.binplus.selfstudy.utils.Tools;
import in.binplus.selfstudy.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private CommonGridAdapter mAdapter;
    private List<CommonModels> list =new ArrayList<>();
    SliderLayout movie_img_banner;

    private ApiResources apiResources;

    private boolean isLoading=false;
    private ProgressBar progressBar;
    private int pageCount=1,checkPass=0;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.fav));

        initComponent(view);

    }

    private void initComponent(View view) {

        apiResources=new ApiResources();
        swipeRefreshLayout=view.findViewById(R.id.swipe_layout);
        coordinatorLayout=view.findViewById(R.id.coordinator_lyt);
        progressBar=view.findViewById(R.id.item_progress_bar);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        tvNoItem=view.findViewById(R.id.tv_noitem);
        movie_img_banner = (SliderLayout) view.findViewById(R.id.movie_img_banner);


        SharedPreferences prefs = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        String id = prefs.getString("id", null);

        final String URl = apiResources.getFavoriteUrl()+"&&user_id="+id+"&&page=";


        //----movie's recycler view-----------------
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 12), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CommonGridAdapter(getContext(), list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !isLoading) {

                    pageCount=pageCount+1;
                    isLoading = true;

                    progressBar.setVisibility(View.VISIBLE);
                    getBanners();
                    getData(URl,pageCount);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.removeAllViews();
                pageCount=1;
                list.clear();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(getContext()).isNetworkAvailable()){
                    getBanners();
                    getData(URl,pageCount);
                }else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }

            }
        });



        if (new NetworkInst(getContext()).isNetworkAvailable()){
            getData(URl,pageCount);
        }else {
            getBanners();
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

    }

    private void getData(String url,int pageNum){

        String fullUrl = url+String.valueOf(pageNum);

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                isLoading=false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if (String.valueOf(response).length()<10 && pageCount==1){
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    tvNoItem.setText("No items here");
                }else {
                    coordinatorLayout.setVisibility(View.GONE);
                }

                Log.e("LOG::", String.valueOf(response));

                for (int i=0;i<response.length();i++){

                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("title"));
                        models.setQuality(jsonObject.getString("video_quality"));

                        if (jsonObject.getString("is_tvseries").equals("0")){
                            models.setVideoType("movie");
                        }else {
                            models.setVideoType("tvseries");
                        }
                        models.setId(jsonObject.getString("videos_id"));
                        list.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading=false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));

                if (pageCount==1){
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);

    }

    private void getBanners() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiResources.getSecond_banner(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("banner_image",response.toString());
                    if (response.getString("status").equals("success")) {
                        JSONArray array=response.getJSONArray("banner");
                        ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                        for(int i=0; i<array.length();i++)
                        {
                            JSONObject jsonObject=array.getJSONObject(i);
                            HashMap<String, String> url_maps = new HashMap<String, String>();
                            url_maps.put("title", jsonObject.getString("title"));
                            url_maps.put("description", jsonObject.getString("description"));
                            url_maps.put("video_link", jsonObject.getString("video_link"));
                            url_maps.put("image_link", jsonObject.getString("image_link"));
                            url_maps.put("slug", jsonObject.getString("slug"));
                            url_maps.put("publication", jsonObject.getString("publication"));
                            listarray.add(url_maps);
                        }
                        for (final HashMap<String, String> name : listarray) {
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo);

                            TextSliderView sliderView = new TextSliderView(getActivity());
                            // if you want show image only / without description text use DefaultSliderView instead

                            // initialize SliderLayout
                            sliderView
                                    .image(name.get("image_link"))
                                    .description("")
                                    .setRequestOption(requestOptions);
                            //.setProgressBarVisible(true);


                            //add your extra information
                            sliderView.bundle(new Bundle());
                            sliderView.getBundle().putString("extra", name.get("video_link"));
                            movie_img_banner.addSlider(sliderView);


                        }
                        // home_img_banner.setPresetTransformer(SliderLayout.Transformer.Accordion);

                        // home_img_banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        //  home_img_banner.setCustomAnimation(new DescriptionAnimation());
                        movie_img_banner.setDuration(4000);
                        movie_img_banner.stopCyclingWhenTouch(false);

                    } else if (response.getString("status").equals("error")) {
                        new ToastMsg(getActivity()).toastIconError(response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(getActivity()).toastIconError(getString(R.string.error_toast));
            }
        });
        new VolleySingleton(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onStop() {
        movie_img_banner.stopAutoCycle();
        super.onStop();
    }

}