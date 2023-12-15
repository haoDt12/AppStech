package com.datn.shopsale.socketio;

import android.app.Application;

import com.datn.shopsale.utils.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatApplication extends Application {
    private final Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constants.URL_API);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
