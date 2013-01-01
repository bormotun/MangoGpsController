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
    private static final Logger logger = LoggerFactory.getLogger(GpsDownload.class);
    private static MangoGpsController mangoGpsController;
    private static CommandLine cmdLine;
    private static String gpsFileName;

    private static final int ERRORCODE = 1;
    private static final int SUCCESSCODE = 0;

    private static final int MINPOIINDEX = 1;
    private static final int MAXPOIINDEX = 16;
    public static final String READ_POI = "readPOI";
    public static final String WRITE_POI = "writePOI";
    public static final String DWN_TRKS = "dwnTrks";
    public static final String RM_TRKS = "rmTrks";
    public static final String RM_TRK = "rmTrk";
    public static final String DWN_TRK = "dwnTrk";

    public static void main(String... args) {
        try {
            run(args);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(ERRORCODE);
        }
    }

    private static void run(String... args) throws ParseException, IOException, InterruptedException, MangoException {

        Options options = new Options();
        //OptionGroup group = new OptionGroup();

        /* это если взаимоисключающие опции
        group.addOption(OptionBuilder.withLongOpt("readPOI")
                .hasArg()
                .withArgName("index [" + MINPOIINDEX + "-" + MAXPOIINDEX + "]")
                .withDescription("read POI")
                .create("r")).addOption(OptionBuilder.withLongOpt("writePOI")
                .hasArgs(4).withArgName("index descr lon lat")
                .withDescription("write POI")
                .create("w"))
                .addOption(OptionBuilder.withLongOpt("help")
                        .withDescription("print this message")
                        .create("h"));
        */

        options.addOption(OptionBuilder.withLongOpt("gpsFileName")
                .hasArg()
                .withArgName("[/dev/ttyUSB0]")
                .withDescription("gpsFileName (port).")
                .create("p"))
                .addOption(OptionBuilder.withLongOpt("verbose")
                        .hasArg()
                        .withArgName("[DEBUG | INFO | ERROR] ERROR")
                        .withDescription("log level")
                        .create("v"))
                .addOption(OptionBuilder.withLongOpt(READ_POI)
                        .hasArg()
                        .withArgName("index [" + MINPOIINDEX + "-" + MAXPOIINDEX + "]")
                        .withDescription("read POI")
                        .create("r")).addOption(OptionBuilder.withLongOpt(WRITE_POI)
                .hasArgs(4).withArgName("index descr lon lat")
                .withDescription("write POI")
                .create("w"))
                .addOption(OptionBuilder.withLongOpt("help")
                        .withDescription("print this message")
                        .create("h"))
                .addOption(OptionBuilder.withLongOpt(DWN_TRKS)
                        .withDescription("download all tracks from device")
                        .create("ds"))
                .addOption(OptionBuilder.withLongOpt(DWN_TRK)
                        .hasArg()
                        .withArgName("fileName")
                        .withDescription("download track from device")
                        .create("d"))
                .addOption(OptionBuilder.withLongOpt(RM_TRK)
                        .hasArg()
                        .withArgName("fileName")
                        .withDescription("remove track from device")
                        .create("rm"))
                .addOption(OptionBuilder.withLongOpt(RM_TRKS)
                        .withDescription("remove all tracks from device")
                        .create("rms"));
        //   .addOptionGroup(group);

        CommandLineParser parser = new PosixParser();
        cmdLine = parser.parse(options, args);

        if (cmdLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar jarName.jar", options);
            return;
        }


        if (logger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.valueOf(
                    cmdLine.getOptionValue("verbose", "ERROR")
            ));
        }

        gpsFileName = cmdLine.getOptionValue("gpsFileName", "/dev/ttyUSB0");
        if (!new File(gpsFileName).exists()) {
            throw new MangoException(String.format("Can not open gps-com file: %s", gpsFileName));
        }

        mangoGpsController = new MangoGpsController();
        GpsDownload gpsDownload = new GpsDownload();

        if (cmdLine.hasOption(READ_POI)) {
            gpsDownload.readPoi();
        }
        if (cmdLine.hasOption(WRITE_POI)) {
            gpsDownload.writePoi();
        }
        if (cmdLine.hasOption(DWN_TRKS) ||
                (!cmdLine.hasOption(WRITE_POI)
                        && !cmdLine.hasOption(READ_POI)
                        && !cmdLine.hasOption(RM_TRKS)
                        && !cmdLine.hasOption(RM_TRK)
                        && !cmdLine.hasOption(DWN_TRK))) {
            gpsDownload.downloadTracks();
        }
        if (cmdLine.hasOption(DWN_TRK)) {
            gpsDownload.downloadTrack();
        }
        if (cmdLine.hasOption(RM_TRK)) {
            gpsDownload.removeTrack();
        }
        if (cmdLine.hasOption(RM_TRKS)) {
            gpsDownload.removeTracks();
        }

    }


    private void readPoi() throws IOException, InterruptedException, MangoException {
        String poiArg = cmdLine.getOptionValue(READ_POI);
        if (poiArg != null && Integer.parseInt(poiArg) >= MINPOIINDEX
                && Integer.parseInt(poiArg) <= MAXPOIINDEX) {
            throw new RuntimeException("not implemented yet");
        } else {
            throw new MangoException("bad POI index");
        }
    }

    private void writePoi() throws IOException, InterruptedException, MangoException {
        String[] poiArgs = cmdLine.getOptionValues(WRITE_POI);
        if (poiArgs != null && poiArgs.length > 3) {
            mangoGpsController.writePoi(poiArgs[0], poiArgs[1], poiArgs[2], poiArgs[3]);
            throw new RuntimeException("not implemented yet");
        } else {
            throw new MangoException("bad arguments");
        }
    }

    private void removeTracks() throws IOException, InterruptedException {
        String[] filesNames = mangoGpsController.getFilesNames(gpsFileName);
        for (String fileName : filesNames) {
            fileName = fileName.substring(1, fileName.length() - 1);
            removeTrack(fileName);
        }
    }

    private void downloadTrack() throws IOException, InterruptedException, MangoException {
        String fileName = cmdLine.getOptionValue(DWN_TRK);
        if (fileName != null) {
            downloadTrack(fileName);
        } else {
            throw new MangoException("bad arguments");
        }
    }

    private void removeTrack() throws MangoException {
        String fileName = cmdLine.getOptionValue(RM_TRK);
        if (fileName != null) {
            removeTrack(fileName);
        } else {
            throw new MangoException("bad arguments");
        }
    }

    private void removeTrack(String fileName) {
        throw new RuntimeException("not implemented yet");
    }

    private void downloadTracks() throws IOException, InterruptedException {
        String[] filesNames = mangoGpsController.getFilesNames(gpsFileName);

        //  logger.debug(mangoGpsController.getVerionCommand(gpsFileName));
        logger.debug(Arrays.toString(filesNames));

        for (String fileName : filesNames) {
            fileName = fileName.substring(1, fileName.length() - 1);
            downloadTrack(fileName);
        }
    }

    private void downloadTrack(String fileName) throws IOException, InterruptedException {
        logger.info("try process file {}:", fileName);

        if (!new File(fileName).exists()) {
            logger.info("process file {}:", fileName);
            mangoGpsController.sendDownloadCommand(fileName, gpsFileName);
            mangoGpsController.downloadFile(fileName, gpsFileName);
        } else {
            logger.error("file {} already exists:", fileName);
        }
    }

}
