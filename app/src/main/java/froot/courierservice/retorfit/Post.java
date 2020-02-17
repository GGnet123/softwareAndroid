package froot.courierservice.retorfit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("success")
    private String success;
    ////login
    @SerializedName("token")
    private String token;

    private String login;
    private String password;

    public Post(String login, String password){
        this.login = login;
        this.password = password;
    }
    public String getToken(){
        return token;
    }
    public String getSuccess(){
        return success;
    }


}
