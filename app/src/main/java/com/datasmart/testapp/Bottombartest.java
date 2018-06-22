package com.datasmart.testapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Bottombartest extends AppCompatActivity {
    Button myButton;
    View myView;
    boolean isUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottombartest);
        myView = findViewById(R.id.my_view);
        myButton = findViewById(R.id.my_button);

        // initialize as invisible (could also do in xml)
//        myView.setVisibility(View.INVISIBLE);
        myButton.setText("Slide up");
        isUp = true;
    }

    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void onSlideViewButtonClick(View view) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.car);
        getdatasend(icon);
//        if (isUp) {
//            slideDown(myView);
//            myButton.setText("Slide up");
//        } else {
//            slideUp(myView);
//            myButton.setText("Slide down");
//        }
//        isUp = !isUp;
    }


    public void getdatasend(final Bitmap bitmap) {


//            final ProgressDialog loading = ProgressDialog.show(this, "", "Please wait...", false, false);
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpstarinteriors.com/prime/api/user/uploadUserImage", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);

                    JSONObject jobj = new JSONObject(response.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                    loading.dismiss();
                Log.e("Volley Error", String.valueOf(error));
                Toast.makeText(Bottombartest.this, "Error" + String.valueOf(error), Toast.LENGTH_SHORT).show();

            }
        }) {

            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                params.put("userImage", new DataPart(getFileDataFromDrawable(bitmap)));

                return params;
            }

            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("userId", "a54cf1ad679aeb59f44b1e7929303211");
                params.put("accessToken", "a7573c926765440f4c6fc97885357965f15d56912e3c496398812251e3137509");
                params.put("userTypeId", "11dcc765ca10dfdc96c73d242676d7d4");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-type", "application/x-www-form-urlencoded");
                params.put("user-agent", "primeapp");

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "multipart/form-data";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                try {
                    // populate text payload
                    Map<String, String> params = getParams();



                    if (params != null && params.size() > 0) {

                        textParse(dos, params, getParamsEncoding());
                    }

                    // populate data byte payload
                    Map<String, DataPart> data = getByteData();
                    if (data != null && data.size() > 0) {
                        dataParse(dos, data);
                    }

                    // close multipart form data after text and file data

                    return bos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy1);
        rq.add(stringRequest);


    }

    class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart() {
        }

        DataPart(byte[] data) {
            content = data;
        }

        String getFileName() {
            return fileName;
        }

        byte[] getContent() {
            return content;
        }

        String getType() {
            return type;
        }

    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {

            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
//        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
//        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
//        dataOutputStream.writeBytes(lineEnd);
//        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }
    private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }

    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
//        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
//        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
//                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
//        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
//            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
//        }
//        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

//        dataOutputStream.writeBytes(lineEnd);
    }


}