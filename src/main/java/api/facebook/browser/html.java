package api.facebook.browser;

/**
 *
 * @author oswaldo
 */
public class html {

    public static String getSuccessful() {
        return "<html>"
                + "	<head>"
                + "		<meta charset=\"UTF-8\">"
                + "		<title>Login Successful</title>"
                + "		<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://facebookbrand.com/wp-content/uploads/2016/05/FB-fLogo-Blue-broadcast-2.png\"/>"
                + "		<style>"
                + "			body{background-color: #edeff2;}"
                + "			h1{"
                + "				color: white;"
                + "				font-family: Arial;"
                + "				margin-top: 0px;"
                + "				padding-top: 16px;"
                + "				padding-left: 10px;"
                + "			}"
                + "			div.top-container{"
                + "				width: 100%;"
                + "				height: 70px;"
                + "				background-color: #004b9c;"
                + "			}"
                + "		</style>"
                + "	</head>"
                + "	<body>"
                + "		<div class=\"top-container\">"
                + "			<h1>Facebook | Login Successful</h1>"
                + "		</div>	"
                + "	</body>"
                + "</html>";
    }

    public static String getFail() {
        return "<html>"
                + "	<head>"
                + "		<meta charset=\"UTF-8\">"
                + "		<title>Login Successful</title>"
                + "		<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://facebookbrand.com/wp-content/uploads/2016/05/FB-fLogo-Blue-broadcast-2.png\"/>"
                + "		<style>"
                + "			body{background-color: #edeff2;}"
                + "			h1{"
                + "				color: white;"
                + "				font-family: Arial;"
                + "				margin-top: 0px;"
                + "				padding-top: 16px;"
                + "				padding-left: 10px;"
                + "			}"
                + "			div.top-container{"
                + "				width: 100%;"
                + "				height: 70px;"
                + "				background-color: #004b9c;"
                + "			}"
                + "		</style>"
                + "	</head>"
                + "	<body>"
                + "		<div class=\"top-container\">"
                + "			<h1>Facebook | Login Fail</h1>"
                + "		</div>	"
                + "	</body>"
                + "</html>";
    }

}
