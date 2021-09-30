package mvvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import model.*;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class VM {

    private BooleanProperty leftImportPressed = new SimpleBooleanProperty(false);
    private BooleanProperty rightImportPressed = new SimpleBooleanProperty(false);

    private BooleanProperty allPressed = new SimpleBooleanProperty(false);
    private BooleanProperty newLeftPressed = new SimpleBooleanProperty(false);
    private BooleanProperty newRightPressed = new SimpleBooleanProperty(false);
    private BooleanProperty orphanPressed = new SimpleBooleanProperty(false);
    private BooleanProperty samePressed = new SimpleBooleanProperty(false);
    private BooleanProperty foldersPressed = new SimpleBooleanProperty(false);

    private BooleanProperty leftToRightPressed = new SimpleBooleanProperty(true);
    private BooleanProperty transfertPressed = new SimpleBooleanProperty(false);

    private BooleanProperty saveContentPressed = new SimpleBooleanProperty(false);

    private StringProperty leftLabel = new SimpleStringProperty();
    private StringProperty rightLabel = new SimpleStringProperty();

    private ObjectProperty<TreeItem<Abstract_File>> rootLeft = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<Abstract_File>> rootRight = new SimpleObjectProperty<>();
    private final ObjectProperty<TreeItem<Abstract_File>> itemSelectedProperty = new SimpleObjectProperty<>();

    private StringProperty selectedFileName = new SimpleStringProperty();
    private StringProperty selectedFilePath = new SimpleStringProperty();
    private StringProperty completePath = new SimpleStringProperty();

    private ArrayList<Status> leftList = new ArrayList<>();
    private ArrayList<Status> rightList = new ArrayList<>();

    private final Status orp = Status.ORPHAN;
    private final Status same = Status.SAME;
    private final Status newer = Status.NEWER;
    private final Status older = Status.OLDER;
    private final Status ud = Status.UNDEFINED;

    private BooleanProperty isNew = new SimpleBooleanProperty(false);

    private final EditVM editVM;
    private final Model model;

    public VM(Model model) {
        this.model = model;
        editVM = new EditVM(this);
        setCols();
        listeners();
    }

    private void listeners() {
        btnAll();
        btnNewerLeft();
        btnNewerRight();
        btnSame();
        btnOrphan();
        btnFolderOnly();

        importButton();
        btnSave();
        transfertAction();
    }

    private void setLeftRoot() {
        model.unSelectAll(model.getLeftRoot().getValue());

        rootLeft.setValue(model.setAll(model.getLeftRoot().getValue()));
        leftLabel.setValue(model.getLeftLabel());
    }

    private void setRightRoot() {
        model.unSelectAll(model.getRightRoot().getValue());

        rootRight.setValue(model.setAll(model.getRightRoot().getValue()));
        rightLabel.setValue(model.getRightLabel());
    }

    public ObjectProperty<TreeItem<Abstract_File>> rootLeftProperty() {
        return rootLeft;
    }

    public StringProperty leftLabel() {
        return leftLabel;
    }

    public StringProperty rightLabel() {
        return rightLabel;
    }

    public ObjectProperty<TreeItem<Abstract_File>> rootRightProperty() {
        return rootRight;
    }

    public BooleanProperty getLeftBtn() {
        return leftImportPressed;
    }

    public BooleanProperty getRightBtn() {
        return rightImportPressed;
    }

    public BooleanProperty getIsNew() {
        return isNew;
    }

    public void setIsNew(BooleanProperty isNew) {
        this.isNew = isNew;
    }

    public BooleanProperty getTransfert() {
        return transfertPressed;
    }

    public BooleanProperty leftToRight() {
        return leftToRightPressed;
    }

    public BooleanProperty getAll() {
        return allPressed;
    }

    public BooleanProperty getNewLeft() {
        return newLeftPressed;
    }

    public BooleanProperty getNewRight() {
        return newRightPressed;
    }

    public BooleanProperty getOrphan() {
        return orphanPressed;
    }

    public BooleanProperty getSame() {
        return samePressed;
    }

    public BooleanProperty getFoldersPressed() {
        return foldersPressed;
    }

    public BooleanProperty getSaveButton() {
        return saveContentPressed;
    }

    public void bindToAll(ReadOnlyBooleanProperty armedProperty) {
        allPressed.bind(armedProperty);
    }

    public void bindLeftBtn(ReadOnlyBooleanProperty armedProperty) {
        leftImportPressed.bind(armedProperty);
    }

    public void bindSaveBtn(ReadOnlyBooleanProperty armedProperty) {
        saveContentPressed.bind(armedProperty);
    }

    public void bindRightBtn(ReadOnlyBooleanProperty armedProperty) {
        rightImportPressed.bind(armedProperty);
    }

    private void listAdd(Status left, Status right) {
        leftList.add(left);
        rightList.add(right);
    }

    private void listRemove(Status left, Status right) {
        model.unSelectAll(model.getLeftRoot().getValue());
        model.unSelectAll(model.getRightRoot().getValue());

        leftList.remove(left);
        rightList.remove(right);
    }

    private void selectFolder() {
        model.selectFolders(model.getLeftRoot().getValue());
        model.selectFolders(model.getRightRoot().getValue());
    }

    private void unselectFolder() {
        model.unSelectFolders(model.getLeftRoot().getValue());
        model.unSelectFolders(model.getRightRoot().getValue());
    }

    private void setFilterClicked() {
        selectFilters();

        rootLeft.setValue(model.setRoot(model.getLeftRoot().getValue()));
        rootRight.setValue(model.setRoot(model.getRightRoot().getValue()));
    }

    private void unSelectBtn() {
        newLeftPressed.setValue(false);
        newRightPressed.setValue(false);
        samePressed.setValue(false);
        orphanPressed.setValue(false);
        foldersPressed.setValue(false);
    }

    private void selectFilters() {
        model.select(model.getLeftRoot().getValue(), leftList, foldersPressed.getValue());
        model.select(model.getRightRoot().getValue(), rightList, foldersPressed.getValue());
    }

    private void setCols() {
        model.compare(model.getLeftRoot().getValue(), model.getRightRoot().getValue());

        setLeftRoot();
        setRightRoot();
    }

    private void btnOrphan() {
        orphanPressed.addListener((o, old, newVal) -> {

            if (!old && newVal) {
                listAdd(orp, orp);
            } else {
                listRemove(orp, orp);
            }

            setFilterClicked();

            if (leftList.isEmpty() && rightList.isEmpty()) {
                setCols();
            }

            setEnable();
        });
    }

    private void btnFolderOnly() {
        foldersPressed.addListener((o, old, newVal) -> {

            if (!old && newVal) {

                listAdd(ud, ud);
                if (leftList.size() == 1 && rightList.size() == 1) {
                    selectFolder();
                } else {
                    selectFilters();
                }

            } else {
                if (leftList.size() == 1 && rightList.size() == 1) {
                    unselectFolder();
                } else {
                    selectFilters();
                }

                listRemove(ud, ud);
            }

            setFilterClicked();

            if (leftList.isEmpty() && rightList.isEmpty()) {
                setCols();
            }

            setEnable();
        });
    }

    private void btnSame() {
        samePressed.addListener((o, old, newVal) -> {

            if (!old && newVal) {
                listAdd(same, same);
            } else {
                listRemove(same, same);
            }

            setFilterClicked();

            if (leftList.isEmpty() && rightList.isEmpty()) {
                setCols();
            }

            setEnable();
        });
    }

    private void btnAll() {
        allPressed.addListener((o, old, newVal) -> {

            leftList.clear();
            rightList.clear();

            unSelectBtn();
            setCols();
        });
    }

    private void btnNewerLeft() {
        newLeftPressed.addListener((o, old, newVal) -> {

            if (!old && newVal) {
                listAdd(newer, older);
            } else {
                listRemove(newer, older);
            }

            setFilterClicked();

            if (leftList.isEmpty() && rightList.isEmpty()) {
                setCols();
            }

            setEnable();
        });
    }

    private void btnNewerRight() {
        newRightPressed.addListener((o, old, newVal) -> {

            if (!old && newVal) {
                listAdd(older, newer);

            } else {
                listRemove(older, newer);
            }

            setFilterClicked();

            if (leftList.isEmpty() && rightList.isEmpty()) {
                setCols();
            }

            setEnable();
        });
    }

    private void importButton() {
        leftImportPressed.addListener((o, old, newVal) -> {

            if (!old & newVal) {
                rootLeft.setValue(model.importFile(true, false, leftLabel.get().toString()));
                leftLabel.setValue(model.getLeftLabel());

                setCols();
            }
        });

        rightImportPressed.addListener((o, old, newVal) -> {

            if (!old & newVal) {
                rootRight.setValue(model.importFile(false, true, rightLabel.get().toString()));
                rightLabel.setValue(model.getRightLabel());

                setCols();
            }
        });
    }

    public ObjectProperty<TreeItem<Abstract_File>> itemSelectedProperty() {
        return itemSelectedProperty;
    }

    public StringProperty selectedFileNameProperty() {
        return selectedFileName;
    }

    public StringProperty selectedFilePathProperty() {
        return selectedFilePath;
    }

    public void openSelectedFile(Boolean left) {
        //!
        editVM.setText(itemSelectedProperty.getValue().getValue().getContenu());
        //System.out.println("VM selectedFilePath.getValue() == " + selectedFilePath.getValue());
        
        editVM.setVisible(true);
    }
    
//    private String loadFile(String fileName, boolean left) {
//        String debut = "";
//
//        if (left) {
//            debut = leftLabel.getValue();
//            //System.out.println("loadFile debut left == " + leftLabel.getValue() );
//        } else {
//            debut = rightLabel.getValue();
//        }
//
//        Path path = Paths.get(debut + "/" + fileName);
//        System.out.println("loadFile path == " + path);
//        completePath.setValue(path.toString());
//        //System.out.println("completePath.setValue == " + path.toString());
//
//        try {
//            System.out.println("loadFile TRY= " + new String(Files.readAllBytes(path)));
//            return new String(Files.readAllBytes(path));
//
//        } catch (IOException ex) {
//            return "";
//        }
//    }

    public EditVM getEditVM() {
        return editVM;
    }

    private void btnSave() {
        saveContentPressed.addListener((o, old, newVal) -> {

            if (!old & newVal) {
//                System.out.println(" VM : editVM.textProperty()  avant == " + editVM.textProperty().getValue());
//                System.out.println("fichier == " + itemSelectedProperty.getValue().getValue());

                model.modif(itemSelectedProperty.getValue().getValue(), editVM.textLengthProperty().get(), editVM.textProperty().getValue());

//                System.out.println(" VM : editVM.textProperty()  apres == " + editVM.textProperty().getValue());

                setCols();

            }
        });
    }

    public void fireAction(boolean left) {
        selectedFileName.setValue(itemSelectedProperty.getValue().getValue().getName());
        selectedFilePath.setValue(itemSelectedProperty.getValue().getValue().getPath());

        this.openSelectedFile(left);
    }

    private void setEnable() {
        boolean isEnable;

        if (newLeftPressed.getValue() || orphanPressed.getValue() || newRightPressed.get()) {
            isEnable = false;
        } else {
            isEnable = true;
        }

        if (foldersPressed.getValue() || samePressed.getValue()) {
            isEnable = true;
        }

        leftToRightPressed.set(isEnable);
    }

    private void transfertAction() {
        transfertPressed.addListener((o, old, newVal) -> {

            if (newVal) {
                unSelectBtn();
                setCols();

            }
        });
    }

}
