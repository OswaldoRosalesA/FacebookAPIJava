package api.facebook;

/**
 *
 * @author oswaldo
 */
public class URLs {

    //LogIn URLs
    public static String logInURL(String appId, String redirectURL, String scope) {
        return "https://www.facebook.com/dialog/oauth?"
                + "client_id=" + appId
                + "&redirect_uri=" + redirectURL
                + "&auth_type=rerequest"
                + "&scope=" + scope;
    }

    //User Token URLs
    public static String userTokenURL(String appId, String redirectURL, String appSecret, String code) {
        return "https://graph.facebook.com/oauth/access_token?"
                + "client_id=" + appId
                + "&redirect_uri=" + redirectURL
                + "&client_secret=" + appSecret
                + "&code=" + code;
    }

    public static String longLiveTokenURL(String appId, String appSecret, String shortToken) {
        return "https://graph.facebook.com/oauth/access_token?"
                + "grant_type=fb_exchange_token"
                + "&client_id=" + appId
                + "&client_secret=" + appSecret
                + "&fb_exchange_token=" + shortToken;
    }

    public static String tokerValidatorURL(String tokenToInspect, String appToken) {
        return "https://graph.facebook.com/debug_token?"
                + "input_token=" + tokenToInspect
                + "&access_token=" + appToken;
    }

    //Application Token
    public static String appTokenURL(String appId, String appSecret) {
        return "https://graph.facebook.com/oauth/access_token?"
                + "client_id=" + appId
                + "&client_secret=" + appSecret
                + "&grant_type=client_credentials";
    }

    //User Information URLs
    public static String userDataURL(String userToken) {
        return "https://graph.facebook.com/me"
                + "?access_token=" + userToken;
    }

    public static String logOutURL(String userToken, String userId) {
        return "https://graph.facebook.com/" + userId + "/permissions?access_token=" + userToken;
    }

    public static String userAlbumURL(String userToken, String albumId) {
        return "https://graph.facebook.com/" + albumId
                + "?access_token=" + userToken;
    }

    //Publish functions
    public static String publishFeedURL(String userID, String userToken) {
        return "https://graph.facebook.com/" + userID + "/feed?"
                + "access_token=" + userToken;
    }

    public static String publishPhotoURL(String userId, String userToken) {
        return "https://graph.facebook.com/" + userId + "/photos?"
                + "access_token=" + userToken;
    }

    public static String createAlbumURL(String userId, String userToken) {
        return "https://graph.facebook.com/" + userId + "/albums?"
                + "access_token=" + userToken;
    }

    public static String uploadPhotoURL(String userAlbumId, String userToken) {
        return "https://graph.facebook.com/" + userAlbumId + "/photos?"
                + "access_token=" + userToken;
    }

}
