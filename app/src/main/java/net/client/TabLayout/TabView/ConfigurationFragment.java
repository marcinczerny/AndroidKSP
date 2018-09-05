package net.client.TabLayout.TabView;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.client.CustomClass.MyRequest;
import net.client.CustomClass.MyResponse;
import net.client.CustomClass.Timespan;
import net.client.Database.DaoSession;
import net.client.Database.Measure;
import net.client.MainActivity;
import net.client.R;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eneter.messaging.diagnostic.EneterTrace;
import eneter.messaging.endpoints.typedmessages.DuplexTypedMessagesFactory;
import eneter.messaging.endpoints.typedmessages.IDuplexTypedMessageSender;
import eneter.messaging.endpoints.typedmessages.IDuplexTypedMessagesFactory;
import eneter.messaging.endpoints.typedmessages.TypedResponseReceivedEventArgs;
import eneter.messaging.messagingsystems.messagingsystembase.DuplexChannelEventArgs;
import eneter.messaging.messagingsystems.messagingsystembase.IDuplexOutputChannel;
import eneter.messaging.messagingsystems.messagingsystembase.IMessagingSystemFactory;
import eneter.messaging.messagingsystems.tcpmessagingsystem.TcpMessagingSystemFactory;
import eneter.net.system.EventHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigurationFragment extends Fragment {


    // UI controls
    private Handler myRefresh = new Handler();
    private Button mySendRequestBtn;
    private Button myConnectBtn;
    private Button myDisconnectBtn;
    private TextView textPressure;
    private TextView textTemperature;
    private TextView textHumidity;
    private TextView textTime;
    private ImageView imageViewConnectionStatusLine;
    private Context context;
    // Sender sending MyRequest and as a response receiving MyResponse.
    private IDuplexTypedMessageSender<MyResponse, MyRequest> mySender;
    private Boolean isConnected;

    private TextView textViewDay;
    private TextView textViewMonth;
    private TextView textViewYear;
    private TextView textViewHour;
    private TextView textViewMinute;
    private TextView textViewSecond;

    private Runnable runnableRequest;
    private Handler handler;

    private Boolean isRunning;

    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_configuration, container, false);
        this.context = getContext();
        this.isConnected = false;
        this.isRunning = false;

        imageViewConnectionStatusLine = (ImageView) view.findViewById(R.id.status_connection_line);

        textViewDay = (TextView) view.findViewById(R.id.txtDay);
        textViewMonth = (TextView) view.findViewById(R.id.txtMonth);
        textViewYear = (TextView) view.findViewById(R.id.txtYear);
        textViewHour = (TextView) view.findViewById(R.id.txtHour);
        textViewMinute = (TextView) view.findViewById(R.id.txtMinute);
        textViewSecond = (TextView) view.findViewById(R.id.txtSeconds);

        // Get UI widgets.
        mySendRequestBtn = (Button) view.findViewById(R.id.sendRequestBtn);
        myConnectBtn = (Button) view.findViewById(R.id.btnConnect);
        myDisconnectBtn = (Button) view.findViewById(R.id.btnDisconnect);

        textHumidity = (TextView) view.findViewById(R.id.textHumidity);
        textPressure = (TextView) view.findViewById(R.id.txtPressure);
        textTemperature = (TextView) view.findViewById(R.id.txtTemperature);
//        textTime = (EditText) view.findViewById(R.id.txtTime);

        //Initialize color of status connection line
        imageViewConnectionStatusLine.setBackgroundColor(Color.rgb(255, 0, 0));

        //Lock Buttons
        mySendRequestBtn.setClickable(false);
        myConnectBtn.setClickable(true);
        myDisconnectBtn.setClickable(false);

        // Subscribe to handle the button click.

        mySendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendRequest(view);
            }
        });

        handler = new Handler();

        runnableRequest = new Runnable() {
            @Override
            public void run() {
                onSendRequest(view);
                handler.postDelayed(this, 2500);
            }
        };

        myConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnectRequest(view);

            }
        });

        myDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnDisconnectRequest(view);
            }
        });

        // Open the connection in another thread.
        // Note: From Android 3.1 (Honeycomb) or higher
        //       it is not possible to open TCP connection
        //       from the main thread.
        return view;
    }

    @Override
    public void onDestroy() {
        // Stop listening to response messages.
        if (this.isRunning) {
            this.isRunning = false;
            handler.removeCallbacks(runnableRequest);
        }
        mySender.detachDuplexOutputChannel();

        super.onDestroy();
    }

    private void openConnection() throws Exception {

        EditText editText = (EditText) getView().findViewById(R.id.enterIPAdressEditText);
        String adressIP = editText.getText().toString();

        Matcher matcher = IP_ADDRESS.matcher(adressIP);
        if (matcher.matches()) {

                // Create sender sending MyRequest and as a response receiving MyResponse
                IDuplexTypedMessagesFactory aSenderFactory = new DuplexTypedMessagesFactory();
                mySender = aSenderFactory.createDuplexTypedMessageSender(MyResponse.class, MyRequest.class);

                // Subscribe to receive response messages and information about disconnection.
                mySender.connectionClosed().subscribe(myOnClosedHandler);
                mySender.responseReceived().subscribe(myOnResponseHandler);


                IMessagingSystemFactory aMessaging = new TcpMessagingSystemFactory();
                IDuplexOutputChannel anOutputChannel = aMessaging.createDuplexOutputChannel("tcp://" + adressIP + "/");

                if (this.isRunning == false) {
                    handler.post(runnableRequest);
                    this.isRunning = true;
                }

                mySender.attachDuplexOutputChannel(anOutputChannel);
                this.isConnected = true;



        } else {
            this.isConnected = false;
            this.isRunning = false;
        }
    }

    private void onSendRequest(View v) {
        // Create the request message.
        final MyRequest aRequestMsg = new MyRequest();
        aRequestMsg.Ack = false;
        aRequestMsg.numberOfTimeUnitsBack = 3;
        aRequestMsg.TimeUnit = Timespan.sample;
        //aRequestMsg.Text = myMessageTextEditText.getText().toString();

        // Send the request message.
        try {
            mySender.sendRequestMessage(aRequestMsg);
        } catch (Exception err) {
            EneterTrace.error("Sending the message failed.", err);
        }

    }


    private void onConnectRequest(View v) {
        Thread anOpenConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    myDisconnectBtn.setClickable(true);
                    myConnectBtn.setClickable(false);
                    imageViewConnectionStatusLine.setBackgroundColor(Color.rgb(0, 0, 255));

                } catch (Exception err) {
                    imageViewConnectionStatusLine.setBackgroundColor(Color.rgb(255, 0, 0));
                    EneterTrace.error("Open connection failed.", err);
                }
            }
        });
        anOpenConnectionThread.start();
    }

    private void OnDisconnectRequest(View v) {

        if (isConnected) {
            if (this.isRunning) {
                this.isRunning = false;
                handler.removeCallbacks(runnableRequest);
            }
            mySender.detachDuplexOutputChannel();
            myDisconnectBtn.setClickable(false);
            myConnectBtn.setClickable(true);
//        imageViewConnectionStatusLine.setBackgroundColor(Color.rgb(255,0,0));
        }
    }

    private void onClosedReceived(Object sender, final DuplexChannelEventArgs ex){
        myDisconnectBtn.setClickable(false);
        myConnectBtn.setClickable(true);
        imageViewConnectionStatusLine.setBackgroundColor(Color.rgb(255, 0, 0));
    }
    private void onResponseReceived(Object sender,
                                    final TypedResponseReceivedEventArgs<MyResponse> e) {
        // Display the result - returned number of characters.
        // Note: Marshal displaying to the correct UI thread.
        myRefresh.post(new Runnable() {
            @Override
            public void run() {
                java.util.Date time = new java.util.Date((long) e.getResponseMessage().Time * 1000);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);

                textViewDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                textViewMonth.setText(String.valueOf(calendar.get(Calendar.MONTH)));
                textViewYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

                textViewHour.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
                textViewMinute.setText(String.valueOf(calendar.get(Calendar.MINUTE)));
                textViewSecond.setText(String.valueOf(calendar.get(Calendar.SECOND)));

                textHumidity.setText(Float.toString(e.getResponseMessage().Humidity));
                textPressure.setText(Float.toString(e.getResponseMessage().Pressure));
                textTemperature.setText(Float.toString(e.getResponseMessage().Temperature));

                final DaoSession daoSession = ((MainActivity) context).getDatabaseHelper().getDaoSession();
                daoSession.getMeasureDao().insert(new Measure(null, time.getTime(), e.getResponseMessage().Humidity, e.getResponseMessage().Pressure, e.getResponseMessage().Temperature));

                final MyRequest aRequestMsg = new MyRequest();
                aRequestMsg.Ack = true;
                aRequestMsg.numberOfTimeUnitsBack = 0;
                aRequestMsg.TimeUnit = Timespan.sample;
                try {
                    mySender.sendRequestMessage(aRequestMsg);
                } catch (Exception err) {
                    EneterTrace.error("Sending the message failed.", err);
                }
            }
        });
    }

    private EventHandler<TypedResponseReceivedEventArgs<MyResponse>> myOnResponseHandler
            = new EventHandler<TypedResponseReceivedEventArgs<MyResponse>>() {
        @Override
        public void onEvent(Object sender,
                            TypedResponseReceivedEventArgs<MyResponse> e) {
            onResponseReceived(sender, e);
        }
    };

    private EventHandler<DuplexChannelEventArgs> myOnClosedHandler
            = new EventHandler<DuplexChannelEventArgs>() {
        @Override
        public void onEvent(Object sender,
                            DuplexChannelEventArgs e) {
            onClosedReceived(sender, e);
        }
    };

    private static final Pattern IP_ADDRESS
            = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}[:][0-9]{1,4}");
}
