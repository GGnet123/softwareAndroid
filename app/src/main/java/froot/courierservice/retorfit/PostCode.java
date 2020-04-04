package froot.courierservice.retorfit;

public class PostCode {
    boolean success;
    String code;

    public PostCode(String code) {
        this.code = code;
    }

    public boolean getSuccess() {
        return success;
    }
}
