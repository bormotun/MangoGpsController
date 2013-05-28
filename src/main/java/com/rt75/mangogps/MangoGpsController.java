package com.rt75.mangogps;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MangoGpsController {
    private static Logger logger = LoggerFactory.getLogger(MangoGpsController.class);

    private static final String END_SIGNATURE = "@end#";

    public String[] getFilesNames(final SerialPort serialPort) throws IOException, InterruptedException, SerialPortException {
        final StringBuilder sb = new StringBuilder();
        serialPort.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        String data = serialPort.readString();

                        if (!data.contains(END_SIGNATURE)) {
                            sb.append(data);
                            return;
                        }
                        data = data.substring(0, data.indexOf(END_SIGNATURE));
                        sb.append(data);
                        serialPort.closePort();
                    } catch (SerialPortException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        serialPort.writeString("@R,Date#");

        while (serialPort.isOpened()) {
            Thread.sleep(200);
        }

        return sb.toString().split("\r\n");

    }


    public String getVerionCommand(final SerialPort serialPort) throws IOException, InterruptedException, SerialPortException {
        final String[] data = new String[1];
        serialPort.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        data[0] = serialPort.readString();
                        serialPort.closePort();
                    } catch (SerialPortException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        serialPort.writeString("@R,VER#");

        while (serialPort.isOpened()) {
            Thread.sleep(200);
        }

        return data[0];
    }


    public SerialPort getSerialPort(String serialPortName) throws SerialPortException {
        SerialPort serialPort = new SerialPort(serialPortName);
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_115200,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        return serialPort;
    }

    private void saveFile(String fileName, String data) throws IOException {
        if (data.length() > 0 && data.startsWith("$")) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName)));
            try {
                writer.write(data);
            } finally {
                writer.close();
            }
        }
    }

    public void downloadFile(final String fileName, final SerialPort serialPort) throws SerialPortException, InterruptedException {
        final StringBuilder sb = new StringBuilder();
        serialPort.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        String data = serialPort.readString();
                        if (!data.contains(END_SIGNATURE)) {
                            sb.append(data);
                            return;
                        }
                        data = data.substring(0, data.indexOf(END_SIGNATURE));
                        sb.append(data);
                        serialPort.closePort();
                        saveFile(fileName, sb.toString());
                    } catch (SerialPortException e) {
                        logger.error(e.getMessage(), e);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        serialPort.writeString("@R,GPRMC," + fileName + "#");

        while (serialPort.isOpened()) {
            Thread.sleep(200);
        }
    }


    public void writePoi(String index, String description, String lon, String lat) {
        System.out.println("index " + index);
        System.out.println("descr " + description);
        System.out.println("lon " + lon);
        System.out.println("lat " + lat);
    }

    /*  TODO
    "@RM," + str5 + ".TXT#" -удаление
   "@R,POI," + i + "#" - видимо чтение точек
     string.Format("@S,POI,{0},{1},{2},{3}#", new object[] { i, pOITextBoxByIndex.Text.Trim(), lngTextBoxByIndex.Text.Trim(), latTextBoxByIndex.Text.Trim() }); - запись


http://habrahabr.ru/post/133766/

     */


}