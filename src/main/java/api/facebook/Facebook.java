package api.facebook;

import api.facebook.browser.html;
import api.facebook.gson.AppToken;
import api.facebook.gson.UserAlbum;
import api.facebook.gson.UserData;
import api.facebook.gson.UserToken;
import api.facebook.gson.UserTokenLong;
import com.google.gson.Gson;
import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static spark.Spark.*;

/**
 *
 * @author oswaldo
 */
public class Facebook {

    public final static String REDITECT_URL = "http://localhost:4567/facebook/oauth";
    private static final Gson GSON = new Gson();

    public static String APP_ID = "";
    public static String APP_SECRECT = "";

    private static Semaphore semaphore = new Semaphore(0);
    private static String code = null;

    static {
        get("/facebook/oauth", (req, res) -> {
            code = req.queryMap().get("code").value();
            semaphore.release();
            if (code != null) {
                return html.getSuccessful();
            } else {
                return html.getFail();
            }
        });
    }

    public static Optional<String> userCode(String scope) {
        semaphore = new Semaphore(0);
        try {
            if (scope == null || scope.isEmpty()) {
                scope = "public_profile";
            }
            Desktop.getDesktop().browse(new URI(URLs.logInURL(APP_ID, REDITECT_URL, scope)));
            semaphore.acquire();
            return Optional.ofNullable(code);
        } catch (InterruptedException | URISyntaxException | IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }

    }

    public static Optional<UserToken> userToken(String code) {

        try {

            final URL url = new URL(URLs.userTokenURL(APP_ID, REDITECT_URL, APP_SECRECT, code));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            UserToken tokenResponse = null;

            if (connection.getResponseCode() == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String data = bufferedReader.lines().collect(Collectors.toList()).stream().collect(Collectors.joining());
                    tokenResponse = GSON.fromJson(data, UserToken.class);
                }
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines().forEach(System.out::println);
            }

            connection.disconnect();
            return Optional.ofNullable(tokenResponse);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.empty();
    }

    public static boolean logOut(String token, String userId) {
        try {

            final String post = "";
            final URL url = new URL(URLs.logOutURL(token, userId));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", post.length() + "");
            conn.setUseCaches(false);

            try (BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                os.write(post.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().forEach(t -> {
                });
                conn.disconnect();
                return true;
            } else {
                new BufferedReader(new InputStreamReader(conn.getErrorStream())).lines().forEach(System.out::println);
                conn.disconnect();
                return false;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static Optional<UserTokenLong> longUserToken(String shortToken) {
        try {
            final URL url = new URL(URLs.longLiveTokenURL(APP_SECRECT, APP_SECRECT, shortToken));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            UserTokenLong tokenLong = null;
            if (connection.getResponseCode() == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String data = bufferedReader.lines().collect(Collectors.toList()).stream().collect(Collectors.joining());
                    tokenLong = GSON.fromJson(data, UserTokenLong.class);
                }
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines().forEach(System.out::println);
            }
            connection.disconnect();
            return Optional.ofNullable(tokenLong);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.empty();
    }

    /**
     *
     * @param tokenToInspect
     * @return
     */
    public static boolean validUserToken(String tokenToInspect) {

        try {

            Optional<AppToken> appToken = appToken();

            if (appToken.isPresent()) {
                final URL url = new URL(URLs.tokerValidatorURL(tokenToInspect, appToken.get().getAccessToken()));
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == 200) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        for (String line : bufferedReader.lines().collect(Collectors.toList())) {
                            if (line.contains("error")) {
                                connection.disconnect();
                                return false;
                            }
                        }
                        connection.disconnect();
                        return true;
                    }

                } else {
                    new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines().forEach(System.out::println);
                    connection.disconnect();
                    return false;
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static Optional<AppToken> appToken() {
        try {
            final URL url = new URL(URLs.appTokenURL(APP_SECRECT, APP_SECRECT));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            AppToken appToken = null;
            if (connection.getResponseCode() == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String data = bufferedReader.lines().collect(Collectors.toList()).stream().collect(Collectors.joining());
                    appToken = GSON.fromJson(data, AppToken.class);
                }
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines().forEach(System.out::println);
            }
            connection.disconnect();
            return Optional.ofNullable(appToken);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.empty();
    }

    public static Optional<UserData> userInfo(String token) {

        try {

            final URL url = new URL(URLs.userDataURL(token));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            UserData userData = null;

            if (connection.getResponseCode() == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String data = bufferedReader.lines().collect(Collectors.toList()).stream().collect(Collectors.joining());
                    userData = GSON.fromJson(data, UserData.class);
                }
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines().forEach(System.out::println);
            }

            connection.disconnect();
            return Optional.ofNullable(userData);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }

    public static boolean isValidUserAlbum(String userToken, String albumId) {

        try {

            final URL url = new URL(URLs.userAlbumURL(userToken, albumId));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    bufferedReader.lines().forEach(t -> {
                    });
                    connection.disconnect();
                    return true;
                }
            } else {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    bufferedReader.lines().forEach(System.out::println);
                    connection.disconnect();
                    return false;
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean publishUserFeed(String message, String userId, String userToken) {

        try {

            final String post = "message=" + message;
            final URL url = new URL(URLs.publishFeedURL(userId, userToken));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", post.length() + "");
            conn.setUseCaches(false);

            try (BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                os.write(post.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().forEach(t -> {
                });
                conn.disconnect();
                return true;
            } else {
                new BufferedReader(new InputStreamReader(conn.getErrorStream())).lines().forEach(System.out::println);
                conn.disconnect();
                return false;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static boolean publishUserPhoto(String caption, String urlPhoto, String userId, String userToken) {

        try {

            final String post = "url=" + urlPhoto + "&caption=" + caption;
            final URL url = new URL(URLs.publishPhotoURL(userId, userToken));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", post.length() + "");
            conn.setUseCaches(false);

            try (BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                os.write(post.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().forEach(t -> {
                });
                conn.disconnect();
                return true;
            } else {
                new BufferedReader(new InputStreamReader(conn.getErrorStream())).lines().forEach(System.out::println);
                conn.disconnect();
                return false;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public static Optional<UserAlbum> createUserAlbum(String name, String userId, String userToken) {
        try {

            final String post = "name=" + name;
            final URL url = new URL(URLs.createAlbumURL(userId, userToken));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", post.length() + "");
            conn.setUseCaches(false);

            try (BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                os.write(post.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String data = bufferedReader.lines().collect(Collectors.toList()).stream().collect(Collectors.joining());
                    UserAlbum album = GSON.fromJson(data, UserAlbum.class);
                    conn.disconnect();
                    return Optional.ofNullable(album);
                }
            } else {
                new BufferedReader(new InputStreamReader(conn.getErrorStream())).lines().forEach(System.out::println);
                conn.disconnect();
                return Optional.empty();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.empty();
    }

    public static boolean publishAlbumPhotosFile(File photoFile, String message, String userAlbumId, String userToken) {

        try {

            final String CRLF = "\r\n";
            final String twoHyphens = "--";
            final String boundary = "*****";
            final int maxBufferSize = 1 * 1024 * 1024;

            final FileInputStream fileInputStream = new FileInputStream(photoFile);
            final URL url = new URL(URLs.uploadPhotoURL(userAlbumId, userToken));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream())) {

                dataOutputStream.writeBytes(twoHyphens + boundary + CRLF);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"message\"" + CRLF);
                dataOutputStream.writeBytes(CRLF);
                dataOutputStream.writeBytes(message);
                dataOutputStream.writeBytes(CRLF);
                dataOutputStream.writeBytes(twoHyphens + boundary + CRLF);

                dataOutputStream.writeBytes(twoHyphens + boundary + CRLF);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\\" + photoFile.getName() + "\\;filename=\\" + photoFile.getAbsolutePath() + "\\" + CRLF);
                dataOutputStream.writeBytes(CRLF);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(CRLF);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);

                if (conn.getResponseCode() == 200) {
                    new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().forEach(System.out::println);
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    fileInputStream.close();
                    conn.disconnect();
                    return true;
                } else {
                    new BufferedReader(new InputStreamReader(conn.getErrorStream())).lines().forEach(System.out::println);
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    fileInputStream.close();
                    conn.disconnect();
                    return false;
                }

            }

        } catch (FileNotFoundException | MalformedURLException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Facebook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

}
