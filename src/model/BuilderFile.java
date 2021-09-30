package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.StringProperty;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class BuilderFile {

    private final String init;
    private final String leftBuild = "TestBC/RootBC_Left";
    private final String rightBuild = "TestBC/RootBC_Right";

    private Abstract_File left;
    private Abstract_File right;

    public BuilderFile(String path) {
        this.init = path;
    }

    public Abstract_File build() {
        return build(init);
    }

    public Abstract_File getLeft() {
        return left;
    }

    public Abstract_File getRight() {
        return right;
    }

    public BuilderFile() {
        init = null;
        left = build(leftBuild);
        right = build(rightBuild);
        new ComparatorFile(left, right);

//        System.out.println(left);
//        System.out.println(right);
    }

    private Abstract_File build(String path) {
        Abstract_File source = null;
        String route = "";

        try {
            route = Paths.get(path).getParent().getFileName().toString() + "/" + Paths.get(path).getFileName().toString();
            source = new Dossier(route, route, Abstract_File.lastModificationTime(Paths.get(path)));
            source = build(Paths.get(path).toRealPath(), source, "");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return source;
    }

    private Abstract_File build(Path path, Abstract_File source, String way) throws IOException {
        String route = "";
//        System.out.println("-------------------------------------------------");
//        System.out.println("path :: " + path);
//        System.out.println("source :: " + source);
//        System.out.println("way :: " + way);
//        System.out.println("$$$$$$$$$$$$");

        try (
                DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

            for (Path entry : stream) {
//                System.out.println("*** debut for ***");
                route = way + "/" + entry.getFileName().toString();
//                System.out.println("route == " + route);
//                System.out.println("+++++++++++");

                if (Files.isDirectory(entry)) {
                    source.addFolder(build(entry, new Dossier(entry.getFileName().toString(), route, Abstract_File.lastModificationTime(entry)), route));
                } else {
//                    System.out.println(path.toString());
                    if (route.endsWith(".txt")) {

//                        Path p = buildRelativePath(route);
//                        System.out.println("paaaaath : "+path);
                        String contenu = loadFile(path);//pas bon recup du path || pas bon path
//                        System.out.println("rooutte : "+route);

//                        System.out.println("txt");
                        source.addFolder(new FichierTexte(entry.getFileName().toString(), route, (int) entry.toFile().length(), Abstract_File.lastModificationTime(entry), contenu));
//                        System.out.println("Model Builder Contenu : " + contenu);
                    } else {
//                        System.out.println("simple");
                        source.addFolder(new FichierSimple(entry.getFileName().toString(), route, (int) entry.toFile().length(), Abstract_File.lastModificationTime(entry)));
                    }

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        return source;
    }

    private String loadFile(Path p) {
        try {
            return new String(Files.readAllBytes(p));
        } catch (IOException ex) {
            return "";
        }
    }

//    private static Path buildRelativePath(String nomPath) {
//        Path path = null;
//        try {
//            path = Paths.get(nomPath).toRealPath();
//            File p = new File("");
//            path = Paths.get(p.getAbsolutePath()).relativize(path);
//        } catch (IOException e) {
//            Logger.getLogger(BuilderFile.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return path;
//
//    }
}
