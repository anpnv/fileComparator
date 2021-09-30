package mvvm;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.scene.control.TreeItem;
import model.Abstract_File;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class EditVM {

    private final StringProperty text = new SimpleStringProperty();
    private final BooleanProperty showing = new SimpleBooleanProperty(false);

    private final VM vm;

    EditVM(VM vm) {
        this.vm = vm;
    }

    void setText(String newText) {
        text.setValue(newText);
//        System.out.println("Edit VM textProperty = " + textProperty().getValue());
    }

    public StringProperty textProperty() {
        return text;
    }

    public String getText() {
        return text.getValue();
    }

    public ObservableIntegerValue textLengthProperty() {
        return text.length();
    }

    public StringProperty fileNameProperty() {
        return vm.selectedFileNameProperty();
    }

    public StringProperty filePathProperty() {
        return vm.selectedFilePathProperty();
    }

    public void setVisible(boolean b) {
        showing.setValue(b);
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    public BooleanProperty saveButtonProperty() {
        return vm.getSaveButton();
    }

}
