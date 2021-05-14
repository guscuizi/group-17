package com.example.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//MQTT connection part of the code is built on https://github.com/DIT112-V21/smartcar-mqtt-controller.git

public class ManualActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SmartcarMqttController";
    private static final String EXTERNAL_MQTT_BROKER = "aerostun.dev";
    private static final String LOCALHOST = "10.0.2.2";
    //private static final String TA_SERVER = "aerostun.dev";
    private static final String MQTT_SERVER = "tcp://" + EXTERNAL_MQTT_BROKER + ":1883";
    private static final String THROTTLE_CONTROL = "/smartcar/control/throttle";
    private static final String STEERING_CONTROL = "/smartcar/control/steering";

    private static final int MOVEMENT_SPEED = 70;
    private static final int IDLE_SPEED = 0;
    private static final int STRAIGHT_ANGLE = 0;
    private static final int STEERING_ANGLE = 50;
    //private static final String CAMERA_TOPIC = "Camera_Stream";
    private static final int QOS = 1;
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    Context context;

    private MqttClient mMqttClient;
    private boolean isConnected = false;
    private ImageView mCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        mMqttClient = new MqttClient(getApplicationContext(), MQTT_SERVER, TAG);
        mCameraView = findViewById(R.id.imageView);

        connectToMqttBroker();

        Button forward = findViewById(R.id.Forward);
        Button left = findViewById(R.id.Left);
        Button stop = findViewById(R.id.Stop);
        Button right = findViewById(R.id.Right);
        Button backward = findViewById(R.id.Backward);
        forward.setOnClickListener(this);
        left.setOnClickListener(this);
        stop.setOnClickListener(this);
        right.setOnClickListener(this);
        backward.setOnClickListener(this);

    }

    private void connectToMqttBroker() {
        if (!isConnected) {
            mMqttClient.connect(TAG, "", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnected = true;

                    final String successfulConnection = "Connected to MQTT broker";
                    Log.i(TAG, successfulConnection);
                    Toast.makeText(getApplicationContext(), successfulConnection, Toast.LENGTH_SHORT).show();

                    mMqttClient.subscribe("/smartcar/ultrasound/front", QOS, null);
                    mMqttClient.subscribe("/smartcar/camera", QOS, null);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    final String failedConnection = "Failed to connect to MQTT broker";
                    Log.e(TAG, failedConnection);
                    Toast.makeText(getApplicationContext(), failedConnection, Toast.LENGTH_SHORT).show();
                }
            }, new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    isConnected = false;

                    final String connectionLost = "Connection to MQTT broker lost";
                    Log.w(TAG, connectionLost);
                    Toast.makeText(getApplicationContext(), connectionLost, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.equals("/smartcar/camera")) {
                        final Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

                        final byte[] payload = message.getPayload();
                        final int[] colors = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
                        for (int ci = 0; ci < colors.length; ++ci) {
                            final byte r = payload[3 * ci];
                            final byte g = payload[3 * ci + 1];
                            final byte b = payload[3 * ci + 2];
                            colors[ci] = Color.rgb(r, g, b);
                        }
                        bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

                        mCameraView.setImageBitmap(bm);
                    } else {

                        Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Message delivered");
                }
            });
        }

    }
    void drive(int throttleSpeed, int steeringAngle, String actionDescription) {
        if (!isConnected) {
            final String notConnected = "Not connected (yet)";
            Log.e(TAG, notConnected);
            Toast.makeText(getApplicationContext(), notConnected, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, actionDescription);
        mMqttClient.publish(THROTTLE_CONTROL, Integer.toString(throttleSpeed), QOS, null);
        mMqttClient.publish(STEERING_CONTROL, Integer.toString(steeringAngle), QOS, null);
    }

    public void forward() {
        drive(MOVEMENT_SPEED, STRAIGHT_ANGLE, "Moving forward");
    }

    public void left() {
        drive(MOVEMENT_SPEED, -STEERING_ANGLE, "Moving forward left");
    }

    public void stop() {
        drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
    }

    public void right() {
        drive(MOVEMENT_SPEED, STEERING_ANGLE, "Moving forward left");
    }

    public void backward() {
        drive(-MOVEMENT_SPEED, STRAIGHT_ANGLE, "Moving backward");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Forward:
                forward();
                break;
            case R.id.Left:
                left();
                break;
            case R.id.Stop:
                stop();
                break;
            case R.id.Right:
                right();
                break;
            case R.id.Backward:
                backward();
                break;
        }
    }
}