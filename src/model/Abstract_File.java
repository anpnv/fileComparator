package model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.time.LocalDateTime;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public abstract class Abstract_File extends TreeItem<Abstract_File> {

    protected final Status orp = Status.ORPHAN;
    protected final Status same = Status.SAME;
    protected final Status newer = Status.NEWER;
    protected final Status older = Status.OLDER;
    protected final Status ptsame = Status.PARTIAL_SAME;

    private final Map<Path, Abstract_File> map = new HashMap<>();

    private final StringProperty name;
    private final StringProperty type = new SimpleStringProperty();
    private final IntegerProperty size;
    private final ObjectProperty<LocalDateTime> lastModif;

    private ObjectProperty<Status> status = new SimpleObjectProperty(Status.UNDEFINED);
    private final StringProperty path;
    private final BooleanProperty isFile;

    private boolean selected = false;

    public boolean getSelected() {
        return selected;
    }

    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }

    Abstract_File(String name, String path, int size, boolean isFile, LocalDateTime lastModif) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleIntegerProperty(size);
        this.lastModif = new SimpleObjectProperty<>(lastModif);

        this.path = new SimpleStringProperty(path);
        this.isFile = new SimpleBooleanProperty(isFile);

        setExpanded(true);
        setValue(this);
    }

    public String getName() {
        return name.get();
    }

    public ObservableStringValue nameProperty() {
        return name;
    }

    public String getPath() {
        return path.get();
    }

    public ObservableStringValue pathProperty() {
        return path;
    }

    public boolean isFile() {
        return isFile.get();
    }

    public ObservableStringValue typeProperty() {
        if (isFile()) {
            type.set("F");
        } else {
            type.set("D");
        }
        return type;
    }

    public ReadOnlyBooleanProperty isFileProperty() {
        return isFile;
    }

    public int getSize() {
        return size.get();
    }

    public void setSize(int size) {
        this.size.set(size);
    }

    public ReadOnlyIntegerProperty sizeProperty() {
        return size;
    }

    public void setStatus(Status s) {
        this.status.set(s);
    }

    public Status getStatus() {
        return status.get();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.lastModif.setValue(dateTime);
    }

    public ReadOnlyObjectProperty<Status> statusProperty() {
        return status;
    }

    public LocalDateTime getLastModif() {
        return lastModif.getValue();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastModifProperty() {
        return lastModif;
    }

    protected String displayFormat(int shift) {
        String res = " ";
        for (int i = 0; i < shift; ++i) {
            res += "\t";
        }
        return res;
    }

    @Override
    public String toString() {
        return displayFormat(0);
    }

    public static LocalDateTime lastModificationTime(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        LocalDateTime result = attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (Files.isDirectory(path)) {

            try (DirectoryStream<Path> dir = Files.newDirectoryStream(path)) {

                for (Path p : dir) {
                    LocalDateTime tmp = lastModificationTime(p);

                    if (tmp.isAfter(result)) {
                        result = tmp;
                    }
                }
            }
        }

        return result;
    }

    final void _addFolder(Abstract_File f) {
        super.getChildren().add(f);
    }

    List<Abstract_File> getContent() {
        return getChildren().stream().map(f -> f.getValue()).collect(toList());
    }

    @Override
    public final ObservableList<TreeItem<Abstract_File>> getChildren() {
        return FXCollections.unmodifiableObservableList(super.getChildren());
    }

    final void bindSizeTo(ObservableValue<Integer> value) {
        size.bind(value);
    }

    final void bindDateTimeTo(ObservableValue<LocalDateTime> value) {
        lastModif.bind(value);
    }

    public ArrayList<Abstract_File> lister() {
        ArrayList<Abstract_File> list = new ArrayList<>();
        createList(list);

        return list;
    }

    public Map< Path, Abstract_File> toMap() {
        this.lister().forEach((f) -> map.put(Paths.get(f.getPath()), f));

        return map;
    }

    public abstract void createList(List<Abstract_File> list);

    public abstract void addFolder(Abstract_File f);

    public abstract void compareAndSetStatus(Abstract_File file);

    public abstract Iterable<Abstract_File> fichiers();

    public abstract void setContenu(String newContenu);

    public abstract String getContenu();

    public abstract boolean isFichierTexte();

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Abstract_File other = (Abstract_File) obj;

        return Objects.equals(this.path, other.path);
    }

}
