package family.kuziki.yaBR;

import org.json.JSONObject;

public class RequestResponse{

    private boolean isDone;
    private JSONObject receivedResponse;

    public RequestResponse (){
        isDone = false;
        receivedResponse = null;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setIsDone() {
        this.isDone = true;
    }

    public JSONObject getResponse() {
        return receivedResponse;
    }

    public void setResponse(JSONObject receivedResponse) {
        this.receivedResponse = receivedResponse;
    }





}