package model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class ComparatorFile {

    private static final Status orp = Status.ORPHAN;

    private static Map<Path, Abstract_File> left = new HashMap<>();
    private static Map<Path, Abstract_File> right = new HashMap<>();

    public ComparatorFile(Abstract_File file1, Abstract_File file2) {
        fileComparator(file1, file2);
    }

    private static void fileComparator(Abstract_File f1, Abstract_File f2) {
        left = f1.toMap();
        right = f2.toMap();

        left.keySet().stream().filter((name) -> (left.get(name).getContent().isEmpty())).forEachOrdered((name) -> {
            if (right.containsKey(name)) {
                left.get(name).compareAndSetStatus(right.get(name));
            } else {
                left.get(name).setStatus(orp);
            }
        });

        right.keySet().stream().filter((name) -> (right.get(name).getContent().isEmpty())).forEachOrdered((name) -> {
            if (left.containsKey(name)) {
                right.get(name).compareAndSetStatus(left.get(name));
            } else {
                right.get(name).setStatus(orp);
            }
        });

        left.keySet().stream().filter((name) -> (!left.get(name).isFile())).forEachOrdered((name) -> {
            left.get(name).compareAndSetStatus(right.get(name));
        });

        right.keySet().stream().filter((name) -> (!right.get(name).isFile())).forEachOrdered((name) -> {
            right.get(name).compareAndSetStatus(left.get(name));
        });
    }

}
