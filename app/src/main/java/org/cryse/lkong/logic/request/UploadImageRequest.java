package org.cryse.lkong.logic.request;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.tika.Tika;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.UploadImageResult;
import org.json.JSONObject;

import java.io.File;

public class UploadImageRequest extends AbstractAuthedHttpRequest<UploadImageResult> {
    private String mImagePath;
    private final Tika mTika = new Tika();
    public UploadImageRequest(LKAuthObject authObject, String imagePath) {
        super(authObject);
        this.mImagePath = imagePath;
    }

    public UploadImageRequest(HttpDelegate httpDelegate, LKAuthObject authObject, String imagePath) {
        super(httpDelegate, authObject);
        this.mImagePath = imagePath;

    }

    @Override
    protected Request buildRequest() throws Exception {
        File fileToUpload = new File(mImagePath);
        String mimeTypeString = mTika.detect(fileToUpload);

        RequestBody formBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", mImagePath.substring(mImagePath.lastIndexOf("/")), RequestBody
                        .create(MediaType.parse(mimeTypeString), fileToUpload))
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
