package com.mycompany.demo.warehouse;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
public class SensorSender {

    public static void main(String[] args)  {

        sendSignal("t1", 35.3, 3344);
        sendSignal("t1", 32.3, 3344);

        sendSignal("h1", 60.2, 3355);
        sendSignal("h1", 30.2, 3355);
    }

    private static void sendSignal(String sensorId, double value, int port)   {
        try {
            String msg = "sensor_id=" + sensorId + "; value=" + value;

            DatagramSocket socket = new DatagramSocket();
            byte[] buf = msg.getBytes();

            DatagramPacket packet = new DatagramPacket(
                    buf, buf.length,
                    InetAddress.getByName("127.0.0.1"),
                    port
            );

            log.info("Sending to port {} → {}", port, msg);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            log.error("Sending signal error",e);
        }
    }
}
