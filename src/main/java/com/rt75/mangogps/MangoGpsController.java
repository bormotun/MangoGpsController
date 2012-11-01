package com.rt75.mangogps;

import java.io.*;

public class MangoGpsController {
    void downloadFile(String fileName, String gpsFileName) throws IOException, InterruptedException {
        File gps = new File(gpsFileName);

        BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(gps)));
        StringBuilder sb = new StringBuilder();
        try {

            long num = System.currentTimeMillis();
            String line;

            while (System.currentTimeMillis() - num < 10000) {
                line = buf.readLine();

               // System.out.println(line);

                if (line == null) {
                    Thread.sleep(50);
                    continue;
                }

                if (line.startsWith("$") && sb.length() > 0) {
                    sb.append("\r\n");
                }

                if (line.startsWith("@")) {
                    break;
                }

                sb.append(line);
            }

        } finally {
            buf.close();
        }


        if (sb.length() > 0 && sb.toString().startsWith("$")) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName)));
            try {
                writer.write(sb.toString());
            } finally {
                writer.close();
            }
        }
    }

    void sendDownloadCommand(String fileName, String gpsFileName) throws IOException {
        File gps = new File(gpsFileName);
        OutputStream outputStream = new FileOutputStream(gps);
        try {
            outputStream.write(("@R,GPRMC," + fileName + "#").getBytes());
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }
}