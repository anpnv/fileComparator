package model;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

public class Model {

    private String leftLabel;
    private String rightLabel;

    private Abstract_File rootLeft;
    private Abstract_File rootRight;

    public Model() {
        rootLeft = new BuilderFile("TestBC/RootBC_Left").build();
        rootRight = new BuilderFile("TestBC/RootBC_Right").build();

        leftLabel = setLabel(rootLeft);
        rightLabel = setLabel(rootRight);

        compare(rootLeft, rootRight);
    }

    public TreeItem<Abstract_File> getLeftRoot() {
        return setAll(rootLeft);
    }

    public TreeItem<Abstract_File> getRightRoot() {
        return setAll(rootRight);
    }

    public void compare(Abstract_File left, Abstract_File right) {
        new ComparatorFile(left, right);
    }

    public TreeItem<Abstract_File> setAll(Abstract_File folder) {
        TreeItem<Abstract_File> result = new TreeItem<>(folder);
        result.setExpanded(true);

        folder.getContent().stream().forEachOrdered((f) -> {
            if (!f.getSelected()) {
                result.getChildren().add(setAll(f));
            }
        });
        return result;
    }

    public TreeItem<Abstract_File> setRoot(Abstract_File folder) {
        TreeItem<Abstract_File> result = new TreeItem<>(folder);
        result.setExpanded(true);

        folder.getContent().stream().forEachOrdered((f) -> {
            if (f.getSelected()) {
                result.getChildren().add(setRoot(f));
            }
        });
        return result;
    }

    public void selectFolders(Abstract_File folder) {
        folder.lister().stream().filter((f) -> (!f.isFile())).forEach((f) -> {
            f.select();
        });
    }

    public void unSelectFolders(Abstract_File folder) {
        folder.lister().stream().filter((f) -> (!f.isFile())).forEach((f) -> {
            f.unselect();
        });
    }

    public void select(Abstract_File folder, ArrayList<Status> list, boolean folderPressed) {

        for (Abstract_File f : folder.lister()) {

            if (!f.getSelected()) {
                if (list.contains(f.newer)) {
                    selectStatus(f, f.newer);

                } else if (list.contains(f.older)) {
                    selectStatus(f, f.older);

                }
                if (list.contains(f.same)) {
                    selectStatus(f, f.same);
                    unselectUselessStatus(f, f.same);
                }
                if (list.contains(f.orp)) {
                    selectStatus(f, f.orp);
                    unselectUselessStatus(f, f.orp);
                }
                if (list.contains(f.getStatus())) {
                    f.select();
                }
            }
        }

        if (folderPressed && list.contains(Status.UNDEFINED)) {
            folder.lister().stream().filter((f) -> (f.isFile())).forEach((f) -> {
                f.unselect();
            });
        }
    }

    private void selectStatus(Abstract_File file, Status s) {
        if (file.getStatus() == s || (file.getStatus() == file.ptsame && file.getSize() != 0)) {
            file.select();
        }
    }

    private void unselectUselessStatus(Abstract_File f, Status s) {

        for (Abstract_File fichier : f.getContent()) {

            if (fichier.getSelected()) {

                if (!fichier.getContent().contains(f.getStatus() == s) && fichier.getStatus() == f.ptsame) {
                    fichier.unselect();
                }
            }
        }
    }

    public void unSelectAll(Abstract_File f) {
        for (Abstract_File elem : f.lister()) {
            elem.unselect();
        }
    }

    public void selectAll(Abstract_File f) {
        for (Abstract_File elem : f.lister()) {
            elem.select();
        }
    }

    public String setLabel(Abstract_File f) {
        return Paths.get(f.getName()).toAbsolutePath().toString();
    }

    public String getLeftLabel() {
        return leftLabel;
    }

    public String getRightLabel() {
        return rightLabel;
    }

    public TreeItem<Abstract_File> importFile(Boolean left, boolean right, String path) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose your file !");
        dc.setInitialDirectory(new File(path));
        File selectedDirectory = dc.showDialog(new Scene(new HBox(), 1200, 650).getWindow());

        if (selectedDirectory != null) {
            BuilderFile importedRoot = new BuilderFile(selectedDirectory.getAbsolutePath());
            Abstract_File newRoot = importedRoot.build();

            if (left & !right) {
                leftLabel = selectedDirectory.toString();
                rootLeft = newRoot;
            }
            if (right & !left) {
                rightLabel = selectedDirectory.toString();
                rootRight = newRoot;
            }

            compare(rootLeft, rootRight);

            return setAll(newRoot);
        }

        return null;
    }

    public void modif(Abstract_File f, int size, String newContenu) {
        f.setContenu(newContenu);
        f.setSize(size);
        f.setDateTime(LocalDateTime.now());
        
        //System.out.println("Model Modif newContenu == " + newContenu);
        //System.out.println("Model Modif getcontenu == " + f.getContenu());
              
    }

    public void transfert() {
        int cpt = 0;

        List<Abstract_File> gauche = rootLeft.getContent();
        List<Abstract_File> droit = rootRight.getContent();

        for (Abstract_File g : gauche) {

            if (g.getSelected()) {
                copyFolder(gauche.get(cpt), droit.get(cpt));
            }

            cpt++;
        }

    }

    private void copyFolder(Abstract_File sourceFolder, Abstract_File destinationFolder) {
        if (!sourceFolder.isFile()) {

            List<Abstract_File> files = sourceFolder.getContent();

            for (Abstract_File file : files) {

                if (file.getSelected()) {
                    Abstract_File srcFile = file;
                    Abstract_File destFile = file;
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            System.out.println(sourceFolder);
            System.out.println(destinationFolder);
        }

    }

    public TreeItem<Abstract_File> transfert(Abstract_File left, Abstract_File right) {
        Abstract_File res = null;

        for (Abstract_File f : left.getContent()) {
            if (f.getSelected()) {
                res = f;
            }
        }

        int rs = 0;

        for (Abstract_File f : right.getContent()) {
            if (f.equals(res)) {
                rs = rs;
                break;
            } else {
                rs++;
            }
        }

        right.getContent().set(rs, res);
        return right;
    }

}
