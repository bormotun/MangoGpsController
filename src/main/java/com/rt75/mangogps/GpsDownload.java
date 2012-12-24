package com.rt75.mangogps;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**

 */
public class GpsDownload {
    private static Logger logger = LoggerFactory.getLogger(GpsDownload.class);

    public static void main(String... args) throws IOException, InterruptedException, ParseException {

        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("gpsFileName")
                .hasArg()
                .withDescription("gpsFileName (port). [/dev/ttyUSB0]")
                .create("p"))
                .addOption(OptionBuilder.withLongOpt("verbose")
                        .hasArg()
                        .withDescription("log level DEBUG | INFO | ERROR. [ERROR]")
                        .create("v"))
                .addOption(OptionBuilder.withLongOpt("readPOI")
                        .withDescription("read POI <1-16>")
                        .create("r"))
                .addOption(OptionBuilder.withLongOpt("writePOI")
                        .hasArgs(4)
                        .withDescription("write POI <index> <descr> <lon> <lat>")
                        .create("w"))
                .addOption(OptionBuilder.withLongOpt("help")
                        .withDescription("print this message")
                        .create("h"));


        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = parser.parse(options, args);

        if (cmdLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar jarName.jar", options);
            System.exit(0);
        }


        String gpsFileName = cmdLine.getOptionValue("gpsFileName", "/dev/ttyUSB0");


        if (logger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.valueOf(
                    cmdLine.getOptionValue("verbose", "ERROR")
            ));
        }


        if (!new File(gpsFileName).exists()) {
            logger.error("Can not open gps-com file: {}", gpsFileName);
            System.exit(1);
        }


        MangoGpsController mangoGpsController = new MangoGpsController();


        if (cmdLine.hasOption("writePOI")) {
            String[] poiArgs = cmdLine.getOptionValues("writePOI");
            if (poiArgs.length > 3) {
                 mangoGpsController.writePoi(poiArgs[0],poiArgs[1],poiArgs[2],poiArgs[3]);
            }//else Exception  System.exit(1)

            System.exit(0);
        }

        if (cmdLine.hasOption("readPOI")) {
            System.exit(0);
        }

        String[] filesNames = mangoGpsController.getFilesNames(gpsFileName);

        //  logger.debug(mangoGpsController.getVerionCommand(gpsFileName));
        logger.debug(Arrays.toString(filesNames));

        for (String fileName : filesNames) {
            fileName = fileName.substring(1, fileName.length() - 1);
            logger.info("try process file {}:", fileName);

            if (!new File(fileName).exists()) {
                logger.info("process file {}:", fileName);
                mangoGpsController.sendDownloadCommand(fileName, gpsFileName);
                mangoGpsController.downloadFile(fileName, gpsFileName);
            } else {
                logger.info("file {} already exists:", fileName);
            }

        }
    }

}
