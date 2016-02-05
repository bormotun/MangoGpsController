package com.rt75.mangogps;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import jssc.SerialPortException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    public static class SortListString {

        public static List<String> sort(List<String> st){
            Collections.sort(st, String::compareTo);

            st.stream().distinct().filter(a -> a.startsWith("A")).forEach(System.out::println);

            st.stream().distinct().forEach(System.out::println);


            st.stream().distinct().forEach(System.out::println);

            List<String> sttt= st.stream().distinct().collect(Collectors.toList());
            sttt.stream().forEach(System.out::println);
            sttt.stream().forEach((s) -> {
                String ss = "aaaa" + s;
                System.out.println(ss);
            });



            st.stream().distinct().forEach(System.out::println);

            return st;
        }

    }





    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
      // load client secrets
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
          new InputStreamReader(new FileInputStream("/home/bormotun/Загрузки/client_secret.json")));
      // set up authorization code flow
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
              GoogleNetHttpTransport.newTrustedTransport(),
              JacksonFactory.getDefaultInstance(), clientSecrets,
          Collections.singleton(DriveScopes.DRIVE_FILE)).
              setDataStoreFactory(new FileDataStoreFactory(new File("/home/bormotun/ds")))
         .build();
      // authorize
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

       Drive drive = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport()
               , JacksonFactory.getDefaultInstance(), credential).setApplicationName(
            "test").build();


        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName("1.txt");

        FileContent mediaContent = new FileContent("image/jpeg", new File("/home/bormotun/filename1.txt"));

        Drive.Files.Create insert = drive.files().create(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(true);
      //  uploader.setProgressListener(new FileUploadProgressListener());
        insert.execute();



        FileList result = drive.files().list()
             .execute();
        List<com.google.api.services.drive.model.File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (com.google.api.services.drive.model.File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }


//        File f=new java.io.File("/home/bormotun/", "recPrinter.txt");
//
//        OutputStream out = new FileOutputStream(f);
//
//        Drive.Files.Get request = drive.files().get("recPrinter.txt");
//        request.getMediaHttpDownloader().setDirectDownloadEnabled(true);
//
//        request.executeMediaAndDownloadTo(out);



       return credential;
   }


    public static void main(String... args) throws InterruptedException, MangoException, SerialPortException, ParseException, IOException {




        try {
            authorize();

            if(true){
                return;
            }


            String aaaaa="12345678";
            Optional<String> optional = Optional.ofNullable(aaaaa);

//            Function<String,Optional<String>> f=(s)->{
//                return Optional.of(s.substring(2));
//            };

            Function<String,Optional<String>> f=(s)->Optional.of(s.substring(2));

            //flatMap не оборачивает в Optional map - оборачивает
            System.out.println(optional.map(String::length).map(i -> i+1).orElse(-1));
            System.out.println(optional.
                    flatMap(s -> {return Optional.of(s.length());}).map(i -> i+1).orElse(-1));




       //     Function<String,Optional<String>> f= Optional::of;

            System.out.println(optional.flatMap(f).flatMap(f).flatMap(f).orElse("null value"));

           System.out.println(optional.orElse("null value"));



           System.out.println(SortListString.sort(Arrays.asList("Steve","Steve", "Alex", "Jim", "Bob")));

           // java.util.functions.Predicates;


            Predicate<String> predicate=
                    ((Predicate<String>)(s)-> s!=null)  //не работает, проверяет все условия в цепочке
                    .and((s)->!s.isEmpty())
                    .and((s)->s.startsWith("a"))
                    .or((s) -> s.startsWith("b"));

            System.out.println(predicate.test("aaaaa"));
            System.out.println(predicate.test("baaaa"));
            System.out.println(predicate.test("caaaa"));
            System.out.println(predicate.test(null));
            System.out.println(predicate.test(""));

          //  run(args);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(ERRORCODE);
        }
    }

    private static void run(String... args) throws ParseException, IOException, InterruptedException, MangoException, SerialPortException {

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

    private void removeTracks() throws IOException, InterruptedException, SerialPortException {
        String[] filesNames = mangoGpsController.getFilesNames(mangoGpsController.getSerialPort(gpsFileName));
        for (String fileName : filesNames) {
            fileName = fileName.substring(1, fileName.length() - 1);
            removeTrack(fileName);
        }
    }

    private void downloadTrack() throws IOException, InterruptedException, MangoException, SerialPortException {
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

    private void downloadTracks() throws IOException, InterruptedException, SerialPortException {
        String[] filesNames = mangoGpsController.getFilesNames(mangoGpsController.getSerialPort(gpsFileName));

        //  logger.debug(mangoGpsController.getVerionCommand(gpsFileName));
        logger.debug(Arrays.toString(filesNames));

        for (String fileName : filesNames) {
            fileName = fileName.trim();
            if (!fileName.isEmpty()) {
                fileName = fileName.substring(1, fileName.length() - 1);
                Thread.sleep(2000);  //без этой задержки бывает подвисает наглухо
                downloadTrack(fileName);
            }
        }
    }

    private void downloadTrack(String fileName) throws IOException, InterruptedException, SerialPortException {
        logger.info("try process file {}:", fileName);

        if (!new File(fileName).exists()) {
            logger.info("process file {}:", fileName);
            mangoGpsController.downloadFile(fileName, mangoGpsController.getSerialPort(gpsFileName));
        } else {
            logger.error("file {} already exists:", fileName);
        }
    }

}
