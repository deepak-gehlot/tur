package tutorialance.widevision.com.tutorialance.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import tutorialance.widevision.com.tutorialance.util.Constant;
import tutorialance.widevision.com.tutorialance.webservices.GsonClass;


public class AddFriend extends QueryManager<GsonClass> {
    String user_id, friend_id, type;


    public AddFriend(String user_id, String friend_id) {

        this.user_id = user_id;
        this.friend_id = friend_id;
    }

    @Override
    protected Request.Builder buildRequest() {


        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).build();


        return request;
    }


    @Override
    protected GsonClass parseResponse(String jsonResponse) {

        GsonClass agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonClass.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agents;
    }
}