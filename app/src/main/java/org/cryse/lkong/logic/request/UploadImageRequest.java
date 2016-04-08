package org.cryse.lkong.logic.request;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.UploadImageResult;
import org.json.JSONObject;

import java.io.File;

public class UploadImageRequest extends AbstractAuthedHttpRequest<UploadImageResult> {
    private String mImagePath;
    private String mMimeType;
    public UploadImageRequest(LKAuthObject authObject, String imagePath, String mimeType) {
        super(authObject);
        this.mImagePath = imagePath;
        this.mMimeType = mimeType;
    }

    public UploadImageRequest(HttpDelegate httpDelegate, LKAuthObject authObject, String imagePath, String mimeType) {
        super(httpDelegate, authObject);
        this.mImagePath = imagePath;
        this.mMimeType = mimeType;

    }

    @Override
    protected Request buildRequest() throws Exception {
        File fileToUpload = new File(mImagePath);

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", mImagePath.substring(mImagePath.lastIndexOf("/")), RequestBody
                        .create(MediaType.parse(mMimeType), fileToUpload))
                .build();
        String url = "http://lkong.cn:1337/upload";
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .post(formBody)
                .build();
    }

    @Override
    protected UploadImageResult parseResponse(Response response) throws Exception {
        String responseString = response.body().string();
        JSONObject jsonObject = new JSONObject(responseString);
        UploadImageResult result = new UploadImageResult();
        if(!jsonObject.has("error") && jsonObject.has("filelink")) {
            result.setSuccess(true);
            result.setImageUrl(jsonObject.getString("filelink"));
        } else {
            result.setSuccess(false);
            result.setErrorMessage(jsonObject.getString("error"));
        }
        return result;
    }
}
