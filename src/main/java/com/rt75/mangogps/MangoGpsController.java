package com.rt75.mangogps;

import java.io.*;

public class MangoGpsController {
    void downloadFile(String fileName, String gpsFileName) throws IOException, InterruptedException {
        File gps = new File(gpsFileName);

        long num = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        InputStream is = new BufferedInputStream(new FileInputStream(gps));

        try {   //TODO наверное неплохо эти задержки передавать в параметрах коммандной строки ?
            while (System.currentTimeMillis() - num < 10000) {
                int b = is.read();
                if (b == -1) {
                    Thread.sleep(50);//TODO этого времени может быть много
                    continue;
                }

                if (((char) b) == '@') {
                    break;
                }

                if (((char) b) == '$' && sb.length() > 0) {
                    sb.append("\r\n");   //todo это может быть лишним (посмотреть не будет ли лишних пустых строк)
                }

                sb.append((char) b);
            }
        } finally {
            is.close();
        }


        /* меня терзают смутные сомненья что читать надо побайтно см выше
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
           */

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

    /*  TODO
    "@RM," + str5 + ".TXT#" -удаление
    "@R,Date#"   - помоему это чтение списка файлов ? (не надо будет тупо перебирать ))) )
     @R,VER#
     "@R,POI," + i + "#" - видимо чтение точек
     string.Format("@S,POI,{0},{1},{2},{3}#", new object[] { i, pOITextBoxByIndex.Text.Trim(), lngTextBoxByIndex.Text.Trim(), latTextBoxByIndex.Text.Trim() }); - запись

     */


}