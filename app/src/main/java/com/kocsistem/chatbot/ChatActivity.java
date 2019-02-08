package com.kocsistem.chatbot;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.kocsistem.chatbot.R.layout.activity_chat;

/**
 * A Chat Screen Activity
 */
public class ChatActivity extends AppCompatActivity {
    private EditText messageET;
    private RecyclerView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String localToken = "";
    private String conversationId = "";
    private String primaryToken = "";
    private String botName = "";
    private ProgressBar progressBar;

    //keep the last Response MsgId, to check if the last response is already printed or not
    private String lastResponseMsgId = "";

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            poolBotResponses();
        }
    };

    public static void setDefaultLocale(Context context, String locale) {
        Locale locJa = new Locale(locale.trim());
        Locale.setDefault(locJa);

        Configuration config = new Configuration();
        config.locale = locJa;

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        locJa = null;
        config = null;
    }

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_chat);
        setDefaultLocale(getApplicationContext(),"tr");
        initControls();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            messageET.setText(Html.fromHtml(messageET.getText().toString(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            messageET.setText(Html.fromHtml(messageET.getText().toString()));
        }

        primaryToken = getMetaData(getBaseContext(),"botPrimaryToken");
        botName = getMetaData(getBaseContext(),"botName").toLowerCase();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        runnable.run();
    }


    public void poolBotResponses()
    {
        //Toast.makeText(getBaseContext(),
         //       "test",
           //     Toast.LENGTH_SHORT).show();
        String botResponse = "";
        if(conversationId != "" && localToken != "") {

            new RequestTask().execute();
            /*
            botResponse = getBotResponse();
            if (botResponse != "") {
                try {
                    JSONObject jsonObject = new JSONObject(botResponse);

                    Integer arrayLength = jsonObject.getJSONArray("activities").length();
                    String msgFrom = jsonObject.getJSONArray("activities").getJSONObject(arrayLength-1).getJSONObject("from").get("id").toString();
                    final String curMsgId = jsonObject.getJSONArray("activities").getJSONObject(arrayLength-1).get("id").toString();

                    if(msgFrom.trim().toLowerCase().equals(botName)) {
                        if(lastResponseMsgId == "") {
                         final String  responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();

                            ChatActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    AddResponseToChat(responseMsg);
                                    lastResponseMsgId = curMsgId;
                                }
                            });

                        }
                        else if(!lastResponseMsgId.equals(curMsgId))
                        {

                            final String  responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();
                            ChatActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    AddResponseToChat(responseMsg);
                                    lastResponseMsgId = curMsgId;
                                }});


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            */
        }
        handler.postDelayed(runnable, 1000*5);
    }

    /*
     ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });

     */

    private void initControls() {
        messagesContainer = (RecyclerView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("Chat Bot");// Hard Coded
        sayHelloToClient();

        ChatActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                View view = ChatActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        View view = ChatActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });




                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");
                displayMessage(chatMessage);

                new SendMessageTask().execute(messageText);

                /*
                String conversationTokenInfo = startConversation();
                JSONObject jsonObject = null;

                if(conversationTokenInfo != "") {
                    try {
                        jsonObject = new JSONObject(conversationTokenInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //send message to bot and get the response using the api conversations/{conversationid}/activities
                if(jsonObject != null) {
                    try {
                        conversationId = jsonObject.get("conversationId").toString();
                        localToken = jsonObject.get("token").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(conversationId != "") {
                    sendMessageToBot(messageText);
                }

                */


            }
        });
    }


    //sends the message by making it an activity to the bot
    private void sendMessageToBot(String messageText) {
        //Only for demo sake, otherwise the com.kocsistem.chatbot.network work should be done over an asyns task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UrlText = "https://directline.botframework.com/v3/directline/conversations/" + conversationId + "/activities";
        URL url = null;

        try {
            url = new URL(UrlText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            String basicAuth = "Bearer " + localToken;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type","message");
                jsonObject.put("text",messageText);
                jsonObject.put("from",(new JSONObject().put("id","user1")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String postData = jsonObject.toString();

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length);
            OutputStream out = urlConnection.getOutputStream();
            out.write(postData.getBytes());

            int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            }
            else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String responseValue = readStream(in);
                Log.w("responseSendMsg ",responseValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

    }


    //read the chat bot response
    private String readStream(InputStream in) {
        char[] buf = new char[2048];
        Reader r = null;
        try {
            r = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = 0;
            try {
                n = r.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }

        Log.w("streamValue",s.toString());
        return s.toString();
    }


    public void displayMessage(final ChatMessage message) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                adapter.add(message);
                adapter.notifyDataSetChanged();
                scroll();
            }
        });
    }

    private void scroll() {
        messagesContainer.scrollToPosition(messagesContainer.getAdapter().getItemCount()-1);
       // messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void sayHelloToClient() {

        chatHistory = new ArrayList<ChatMessage>();

        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hello");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        messagesContainer.setLayoutManager(mLinearLayoutManagerVertical);

        messagesContainer.setItemAnimator(new DefaultItemAnimator());

        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
    }

    /*
    Add the bot response to chat window
     */
    private void AddResponseToChat(String botResponse)
    {
        ChatMessage message = new ChatMessage();
        //message.setId(2);
        message.setMe(false);
        message.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        message.setMessage(botResponse);
        displayMessage(message);


    }


    /*
    Get metadata from manifest file against a given key
     */
    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("Metadata", "Unable to load meta-data: " + e.getMessage());
        }
        return null;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Chat Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class RequestTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Integer doInBackground(Void... params) {
            Integer result = 0;
            String UrlText = "https://directline.botframework.com/v3/directline/conversations/" + conversationId + "/activities";
            URL url = null;
            String responseValue = "";

            try {
                url = new URL(UrlText);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                String basicAuth = "Bearer " + localToken;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", basicAuth);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                int responseCode = urlConnection.getResponseCode(); // con.connect()
                if (responseCode >= 400 && responseCode <= 499) {
                    result =0;
                    throw new Exception("Bad authentication status: " + responseCode); //exception message
                }
                else {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    responseValue = readStream(in);
                    result =1;
                    Log.w("responseSendMsg ",responseValue);
                    if (responseValue != "") {
                        try {
                            JSONObject jsonObject = new JSONObject(responseValue);

                            Integer arrayLength = jsonObject.getJSONArray("activities").length();
                            String msgFrom = jsonObject.getJSONArray("activities").getJSONObject(arrayLength-1).getJSONObject("from").get("id").toString();
                            final String curMsgId = jsonObject.getJSONArray("activities").getJSONObject(arrayLength-1).get("id").toString();

                            if(msgFrom.trim().toLowerCase().equals(botName)) {
                                if(lastResponseMsgId == "") {
                                    final String  responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();
                                    if (responseMsg.equalsIgnoreCase("")){
                                        final String responseAttachment =  jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("attachments").toString();
                                        ChatActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                AddResponseToChat(responseAttachment);
                                                lastResponseMsgId = curMsgId;
                                            }
                                        });
                                    }else
                                    {
                                        ChatActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                AddResponseToChat(responseMsg);
                                                lastResponseMsgId = curMsgId;
                                            }
                                        });
                                    }


                                }
                                else if(!lastResponseMsgId.equals(curMsgId))
                                {

                                    final String  responseMsg = jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("text").toString();
                                    if (responseMsg.equalsIgnoreCase("")){
                                        final String responseAttachment =  jsonObject.getJSONArray("activities").getJSONObject(arrayLength - 1).get("attachments").toString();
                                        ChatActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                AddResponseToChat(responseAttachment);
                                                lastResponseMsgId = curMsgId;
                                            }
                                        });
                                    }else
                                    {
                                        ChatActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                AddResponseToChat(responseMsg);
                                                lastResponseMsgId = curMsgId;
                                            }
                                        });
                                    }


                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
          //  progressBar.setVisibility(View.GONE);

            if (result == 1) {
                //adapter = new MyRecyclerViewAdapter(MainActivity.this, feedsList);
               // mRecyclerView.setAdapter(adapter);
            } else {
                //Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SendMessageTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Integer doInBackground(final String... params) {
            Integer result = 0;
            String UrlText = "https://directline.botframework.com/v3/directline/conversations";
            URL url = null;
            String responseValue = "";

            try {
                url = new URL(UrlText);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                String basicAuth = "Bearer "  + primaryToken;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", basicAuth);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                responseValue = readStream(in);
                JSONObject jsonObject = null;
                if(responseValue != "") {
                    try {
                        jsonObject = new JSONObject(responseValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //send message to bot and get the response using the api conversations/{conversationid}/activities
                if(jsonObject != null) {
                    try {
                        conversationId = jsonObject.get("conversationId").toString();
                        localToken = jsonObject.get("token").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(conversationId != "") {

                    ChatActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            sendMessageToBot(params != null ? params[0] :"");
                        }
                    });

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //  progressBar.setVisibility(View.GONE);

            if (result == 1) {
                //adapter = new MyRecyclerViewAdapter(MainActivity.this, feedsList);
                // mRecyclerView.setAdapter(adapter);
            } else {
                //Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

