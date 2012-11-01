package com.rt75.mangogps;

import org.apache.commons.cli.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.*;

/**

 */
public class GpsDownload {


    public static void main(String... args) throws IOException, InterruptedException, ParseException {


        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("days")
                .hasArgs()
                .withDescription("how many days ago")
                .create("d"))
                .addOption(OptionBuilder.withLongOpt("gpsFileName")
                        .hasArgs()
                        .withDescription("gpsFileName (port)")
                        .create("p"));

        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = parser.parse(options, args);

        int minusDays = Integer.parseInt(cmdLine.getOptionValue("days", "5"));

        MangoGpsController mangoGpsController = new MangoGpsController();


        while (minusDays > 0) {
            String fileName = DateTimeFormat.forPattern("yyMMdd").print(
                    new DateTime().minusDays(--minusDays)) + ".TXT";
            String gpsFileName = cmdLine.getOptionValue("gpsFileName", "/dev/ttyUSB0");

            if (!new File(fileName).exists()) {
                mangoGpsController.sendDownloadCommand(fileName, gpsFileName);
                mangoGpsController.downloadFile(fileName, gpsFileName);
            }

        }


    }


}
