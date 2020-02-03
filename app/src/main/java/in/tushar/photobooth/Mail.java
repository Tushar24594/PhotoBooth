package in.tushar.photobooth;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static in.tushar.photobooth.MainActivity.TAG;

public class Mail {
    private static final String YOUR_DOMAIN_NAME = "renaultautoexpo2020.com";
     // private static final String API_KEY = "key-dfd71076a70e3243ca628f8a223ebadb";
  private static final String API_KEY = "apikey";

    public JsonNode sendComplexMessage(String ImageName, String email) throws UnirestException {
        Log.e(TAG,"_MAIL_Sending............");
        HttpResponse<JsonNode> request = null;
        try {
            File image = new File(Environment.getExternalStorageDirectory() + "/PhotoBoxi/" + ImageName);
            Log.e(TAG, "File : " + image);
            request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                    .basicAuth("api", API_KEY)
                    .field("from", "Renault<Autoexpo2020@" + YOUR_DOMAIN_NAME + ">")
                    .field("to", email)
                    .field("subject", "Renault AutoExpo 2020")
//                    .field("text", "Testing out some Mailgun awesomeness!")
                    .field("html", "<p>Hi,</p>\n<p>Thank you for visiting Renault Stall at Autoexpo 2020.</p>\n<p>Here is your cool selfie at Renault Photobooth Station.</p>\n<br><p>Thanks and Regards Renault</p>")
                    .field("attachment", new File(String.valueOf(image)))
                    .asJson();
//            jsonObject.put("json",request.getCode());
            Log.e(TAG, " HTTP : " + request.getBody());
            Log.e(TAG, " Headers : " + request.getCode());
        } catch (Exception e) {
            Log.e(TAG, "EXception in mailling Function " + e);

        }

        if(request.getCode()!=200){
            return null;
        }else{
            return request.getBody();
        }
    }
}
