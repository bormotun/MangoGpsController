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
    private static Logger logger= LoggerFactory.getLogger(GpsDownload.class);

    public static void main(String... args) throws IOException, InterruptedException, ParseException {

        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("gpsFileName")
                        .hasArgs()
                        .withDescription("gpsFileName (port)")
                        .create("p"))
                .addOption(OptionBuilder.withLongOpt("logLevel")
                        .hasArgs()
                        .withDescription("log level DEBUG INFO ERROR")
                        .create("l"));


        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = parser.parse(options, args);

        String gpsFileName = cmdLine.getOptionValue("gpsFileName", "/dev/ttyUSB0");


        //TODO брать из параметров коммандной строки
        if(logger instanceof ch.qos.logback.classic.Logger){
            ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.valueOf(
             cmdLine.getOptionValue("logLevel", "ERROR")
            ));
        }


        if(!new File(gpsFileName).exists()){
            logger.error("Can not open gps-com file: {}",gpsFileName);
            return;
        }


        MangoGpsController mangoGpsController = new MangoGpsController();

        String[] filesNames=mangoGpsController.getFilesNames(gpsFileName);

      //  logger.debug(mangoGpsController.getVerionCommand(gpsFileName));
        logger.debug(Arrays.toString(filesNames));

        for (String fileName:filesNames) {
            fileName=fileName.substring(1,fileName.length()-1);
            logger.info("try process file {}:",fileName);

            if (!new File(fileName).exists()) {
                logger.info("process file {}:",fileName);
                mangoGpsController.sendDownloadCommand(fileName, gpsFileName);
                mangoGpsController.downloadFile(fileName, gpsFileName);
            }else{
               logger.info("file {} already exists:",fileName);
            }

        }
    }

}
