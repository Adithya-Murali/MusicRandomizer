import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameToOneFolder {
    public static int renamer(String dir, File song){
        int error=0;
        int boundVal = 1000000;
        Random rand = new Random();
        Pattern pattern = Pattern.compile("^\\s*([0-9]+)[\\s|\\-|_]+(.+)");
        Matcher matcher = pattern.matcher(song.getName());
        if(matcher.find()) {
            File newname = new File(String.format("%s%s%d %s",dir,File.separator,rand.nextInt(boundVal),matcher.group(2)));
            while(newname.exists()){
                newname = new File(String.format("%s%s%d %s",dir,File.separator,rand.nextInt(boundVal),matcher.group(2)));
            }
            try {
                Files.move(song.toPath(), newname.toPath());
            } catch (Exception e) {
                error++;
            }
        } else {
            File newname = new File(String.format("%s%s%d %s",dir,File.separator,rand.nextInt(boundVal),song.getName()));
            while(newname.exists()){
                newname = new File(String.format("%s%s%d %s",dir,File.separator,rand.nextInt(boundVal),song.getName()));
            }
            try {
                Files.move(song.toPath(), newname.toPath());
            } catch (Exception e) {
                error++;
            }
        }
        return error;
    }

    public static int renameMp3sWithExclude(String dir, final File folder,List<String> excludes) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newWorkStealingPool();
        int errors = -1;
        try {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (fileEntry.isDirectory()) {
                    if(!excludes.contains(fileEntry.getName())) {
                        renameMp3s(dir, fileEntry);
                        Files.delete(fileEntry.toPath());
                    }
                } else {
                    String name = fileEntry.getName();
                    if (name.substring(name.lastIndexOf('.') + 1).equals("mp3")) {
                        if (errors == -1) errors = 0;
                        Future<Integer> fut = es.submit(() -> renamer(dir, fileEntry));
                        errors += fut.get();
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            es.shutdown();
            return errors;
        }
    }
    public static int renameMp3s(String dir, final File folder) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newWorkStealingPool();
        int errors = -1;
        try {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (fileEntry.isDirectory()) {
                    renameMp3s(dir, fileEntry);
                    Files.delete(fileEntry.toPath());
                } else {
                    String name = fileEntry.getName();
                    if (name.substring(name.lastIndexOf('.') + 1).equals("mp3")) {
                        if (errors == -1) errors = 0;
                        Future<Integer> fut = es.submit(() -> renamer(dir, fileEntry));
                        errors += fut.get();
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            es.shutdown();
            return errors;
        }
    }
    public static void main(String[] args) {
        try {
            File dir = new File(RenameToOneFolder.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            int errors;
            if(args.length != 0){
                Path exPath = Paths.get(args[0]);
                if(Files.exists(exPath)) {
                    List<String> excludeList = Files.readAllLines(exPath);
                    errors = renameMp3sWithExclude(dir.getPath(), dir,excludeList);
                }
                else {
                    System.out.println("\nInvalid argument found. Try again.\nThe argument should be the path to .txt file containing names of directories to be excluded.");
                    return;
                }
            }
            else {
                errors = renameMp3s(dir.getPath(), dir);
            }
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
