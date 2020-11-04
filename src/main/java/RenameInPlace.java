import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameInPlace {
    public static int renamer(File song){
        int error=0;
        int boundVal = 1000000;
        Random rand = new Random();
        Pattern pattern = Pattern.compile("^\\s*([0-9]+)[\\s|\\-|_]+(.+)");
        Matcher matcher = pattern.matcher(song.getName());
        if(matcher.find()) {
            try {
                Files.move(song.toPath(), song.toPath().resolveSibling(String.format("%d %s",rand.nextInt(boundVal),matcher.group(2))));
            } catch (Exception e) {
                error++;
            }
        } else {
            try {
                Files.move(song.toPath(), song.toPath().resolveSibling(String.format("%d %s",rand.nextInt(boundVal),song.getName())));
            } catch (Exception e) {
                error++;
            }
        }
        return error;
    }

    public static int renameMp3s(final File folder) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newWorkStealingPool();
        int errors = -1;
        for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                renameMp3s(fileEntry);
            } else {
                String name = fileEntry.getName();
                if(name.substring(name.lastIndexOf('.') + 1).equals("mp3")){
                    if(errors == -1 ) errors = 0;
                    Future<Integer> fut = es.submit(() -> renamer(fileEntry));
                    errors += fut.get();
                }
            }
        }
        es.shutdown();
        return errors;
    }
    public static void main(String[] args) {
        try {
            File dir = new File(RenameInPlace.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            int errors = renameMp3s(dir);
            if(errors == 0) {
                System.out.println("Successfully renamed all files.");
            }
            else if(errors == -1){
                System.out.println("No mp3 files found in the directory.");
            }
            else
                System.out.println("Issue when renaming "+errors+" files.");
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
