package in.binplus.selfstudy.utils;

import in.binplus.selfstudy.Config;

public class ApiResources {

    public static String adStatus="0",adMobBannerId="null", adMobInterstitialId="null", adMobPublisherId="null";


    String URL = Config.API_SERVER_URL;

    String API_SECRECT_KEY = "api_secret_key="+Config.API_KEY;


    String slider = URL+"get_slider?"+API_SECRECT_KEY;
    String latest_movie = URL+"get_latest_movies?"+API_SECRECT_KEY;
    String get_movie = URL+"get_movies?"+API_SECRECT_KEY+"&&page=";
    String get_featured_tv = URL+"get_featured_tv_channel?"+API_SECRECT_KEY;
    String get_live_tv = URL+"get_featured_tv_channel?"+API_SECRECT_KEY+"&&page=";

    String latestTvSeries = URL+"get_latest_tvseries?"+API_SECRECT_KEY;

    String tvSeries = URL+"get_tvseries?"+API_SECRECT_KEY+"&&page=";


    String allCountry = URL+"get_all_country?"+API_SECRECT_KEY;

    String allGenre = URL+"get_all_genre?"+API_SECRECT_KEY;

    String details = URL+"get_single_details?"+API_SECRECT_KEY;

    String movieByCountry = URL+"get_movie_by_country_id?"+API_SECRECT_KEY;
    String movieByGenre = URL+"get_movie_by_genre_id?"+API_SECRECT_KEY;
    String login = URL+"login?"+API_SECRECT_KEY;
    String signup = URL+"signup?"+API_SECRECT_KEY;

    String searchUrl = URL+"search?"+API_SECRECT_KEY;
    String favoriteUrl = URL+"get_favorite?"+API_SECRECT_KEY;

    String passResetUrl = URL+"password_reset?"+API_SECRECT_KEY+"&&email=";
    String profileUpdateURL = URL+"update_profile?"+API_SECRECT_KEY;
    String profileURL = URL+"get_user_details_by_email?"+API_SECRECT_KEY+"&&email=";
    String addFav = URL+"add_favorite?"+API_SECRECT_KEY;

    String favStatusURl = URL+"verify_favorite_list?"+API_SECRECT_KEY;
    String removeFav = URL+"remove_favorite?"+API_SECRECT_KEY;

    String addComment = URL+"add_comments?"+API_SECRECT_KEY;

    String commentsURL = URL+"get_all_comments?"+API_SECRECT_KEY;

    String addReplyURL = URL+"add_replay?"+API_SECRECT_KEY;
    String getAllReply = URL+"get_all_replay?"+API_SECRECT_KEY;
    String termsURL = Config.TERMS_URL;
    String genreMovieURL = URL+"/get_features_genre_and_movie?"+API_SECRECT_KEY;
    String adDetails = URL+"get_ads?"+API_SECRECT_KEY;

    //By Anas

    String pages=URL+"pages?"+API_SECRECT_KEY;
    String banners=URL+"banners?"+API_SECRECT_KEY;
    String second_banner=URL+"second_banner?"+API_SECRECT_KEY;
    String subscription=URL+"subscription?"+API_SECRECT_KEY;
    String bank_details=URL+"bank_details?"+API_SECRECT_KEY;
    String episode_details = URL+"get_episode_details?"+API_SECRECT_KEY;
    String get_moviex = URL+"get_moviesx?"+API_SECRECT_KEY+"&&page=";
    String user_payment_details = URL+"user_payment_details?"+API_SECRECT_KEY;

    public String getAdDetails() {
        return adDetails;
    }

    public String getGenreMovieURL() {
        return genreMovieURL;
    }

    public String getTermsURL() {
        return termsURL;
    }

    public String getGetAllReply() {
        return getAllReply;
    }

    public String getAddReplyURL() {
        return addReplyURL;
    }

    public String getCommentsURL() {
        return commentsURL;
    }

    public String getAddComment() {
        return addComment;
    }

    public String getRemoveFav() {
        return removeFav;
    }

    public String getFavStatusURl() {
        return favStatusURl;
    }

    public String getAddFav() {
        return addFav;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public String getProfileUpdateURL() {
        return profileUpdateURL;
    }

    public String getPassResetUrl() {
        return passResetUrl;
    }

    public String getFavoriteUrl() {
        return favoriteUrl;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getSignup() {
        return signup;
    }

    public String getMovieByCountry() {
        return movieByCountry;
    }

    public String getMovieByGenre() {
        return movieByGenre;
    }

    public String getDetails() {
        return details;
    }

    public String getAllGenre() {
        return allGenre;
    }

    public String getAllCountry() {
        return allCountry;
    }

    public String getTvSeries() {
        return tvSeries;
    }

    public String getLatestTvSeries() {
        return latestTvSeries;
    }

    public String getSlider() {
        return slider;
    }

    public String getGet_live_tv() {
        return get_live_tv;
    }

    public String getGet_featured_tv() {
        return get_featured_tv;
    }

    public String getLatest_movie() {
        return latest_movie;
    }

    public String getGet_movie() {
        return get_movie;
    }
    //By Anas

    public String getBanners() {
        return banners;
    }

    public String getSubscription() {
        return subscription;
    }

    public String getBank_details() {
        return bank_details;
    }

    public String getGet_moviex() {
        return get_moviex;
    }

    public String getSecond_banner() {
        return second_banner;
    }

    public String getPages() {
        return pages;
    }

    public String getEpisode_details() {
        return episode_details;
    }

    public String getUser_payment_details() {
        return user_payment_details;
    }
}
