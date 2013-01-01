package com.rt75.mangogps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class MangoGpsController {
    private static Logger logger = LoggerFactory.getLogger(MangoGpsController.class);

    private static final String END_SIGNATURE = "@end#";
    private static final int TRYREADDELAY = 30;
    private static final int TRYREADTIMEOUTT = 10000;

    public void downloadFile(String fileName, String gpsFileName) throws IOException, InterruptedException {
        File gps = new File(gpsFileName);

        long num = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        InputStream is = new BufferedInputStream(new FileInputStream(gps));

        try {
            while (System.currentTimeMillis() - num < TRYREADTIMEOUTT) {
                int b = is.read();
                if (b == -1) {
                    Thread.sleep(TRYREADDELAY);
                    continue;
                }

                sb.append((char) b);

                if (sb.indexOf(END_SIGNATURE) != -1) {
                    break;
                }
            }
        } finally {
            is.close();
        }

        if (sb.length() > 0 && sb.toString().startsWith("$")) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName)));
            try {
                writer.write(sb.substring(0, sb.length() - END_SIGNATURE.length()));
            } finally {
                writer.close();
            }
        }

    }


    public String[] getFilesNames(String gpsFileName) throws IOException, InterruptedException {
        File gps = new File(gpsFileName);

        OutputStream outputStream = new FileOutputStream(gps);
        try {
            outputStream.write(("@R,Date#").getBytes());
            outputStream.flush();
        } finally {
            outputStream.close();
        }

        long num = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        InputStream is = new BufferedInputStream(new FileInputStream(gps));
        try {
            while (System.currentTimeMillis() - num < TRYREADTIMEOUTT) {
                int b = is.read();
                if (b == -1) {
                    Thread.sleep(TRYREADDELAY);
                    continue;
                }

                sb.append((char) b);

                if (sb.indexOf(END_SIGNATURE) != -1) {
                    break;
                }

            }
        } finally {
            is.close();
        }

        if (sb.length() > END_SIGNATURE.length()) {
            return sb.substring(0, sb.length() - END_SIGNATURE.length()).split("\n");
        }
        return null;
    }


    public void sendDownloadCommand(String fileName, String gpsFileName) throws IOException {
        File gps = new File(gpsFileName);
        OutputStream outputStream = new FileOutputStream(gps);
        try {
            outputStream.write(("@R,GPRMC," + fileName + "#").getBytes());
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }

    public String getVerionCommand(String gpsFileName) throws IOException, InterruptedException {
        File gps = new File(gpsFileName);
        OutputStream outputStream = new FileOutputStream(gps);
        try {
            outputStream.write(("@R,VER#").getBytes());
            outputStream.flush();
        } finally {
            outputStream.close();
        }

        long num = System.currentTimeMillis();
        String line = null;
        BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(gps)));
        while (System.currentTimeMillis() - num < TRYREADTIMEOUTT) {
            line = buf.readLine();
            if (line != null) {
                break;
            }
            Thread.sleep(TRYREADDELAY);
        }
        return line;
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

     */


}