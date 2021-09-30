package view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import model.*;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class TableView extends VBox {

    private Label label = new Label();

    private final Image image = new Image("folder.png");
    private final ImageView imageView = new ImageView(image);
    private final Button load = new Button("", imageView);

    private final HBox sameLine = new HBox();

    private TreeTableView<Abstract_File> treeTableView = new TreeTableView<>();

    public TableView() {
        configColumns();
        configLayout();

    }

    private void configColumns() {
        TreeTableColumn<Abstract_File, String> nameCol = new TreeTableColumn<>("Nom");
        TreeTableColumn<Abstract_File, Status> typeCol = new TreeTableColumn<>("Type");
        TreeTableColumn<Abstract_File, LocalDateTime> timeCol = new TreeTableColumn<>("lastModif");
        TreeTableColumn<Abstract_File, Integer> sizeCol = new TreeTableColumn<>("size");

        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        timeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastModif"));
        sizeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));

        nameCol.setCellFactory(column -> {
            return new ElemCell<>();
        });
        typeCol.setCellFactory(column -> {
            return new ElemCell<>();
        });
        sizeCol.setCellFactory(column -> {
            return new ElemCell<>();
        });
        timeCol.setCellFactory(column -> {
            return new ElemDateTimeCell();
        });

        nameCol.setMinWidth(300);
        typeCol.setMinWidth(40);
        timeCol.setMinWidth(150);
        sizeCol.setMinWidth(40);

        treeTableView.getColumns().addAll(nameCol, typeCol, timeCol, sizeCol);
    }

    private void configLayout() {
        treeTableView.setShowRoot(false);
        treeTableView.setMinHeight(500);

        sameLine.getChildren().addAll(label, load);

        getChildren().addAll(sameLine, treeTableView);

        label.setPadding(new Insets(0, 10, 10, 10));
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
    }

    public Label getLabel() {
        return label;
    }

    public Button getLoad() {
        return load;
    }

    public TreeTableView<Abstract_File> getTable() {
        return treeTableView;
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }
}

class ElemDateTimeCell extends ElemCell<LocalDateTime> {

    @Override
    String texte(LocalDateTime elem) {
        return elem.format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss"));
    }
}

class ElemCell<T> extends TreeTableCell<Abstract_File, T> {

    public ElemCell() {
        getStylesheets().add("view/style.css");
    }

    @Override
    public void updateItem(T elem, boolean isEmpty) {
        super.updateItem(elem, isEmpty);

        if (isEmpty || elem == null) {
            setText("");
            return;
        }

        setText(texte(elem));

        TreeTableRow<Abstract_File> currentRow = getTreeTableRow();
        TreeItem<Abstract_File> treeItem = currentRow.treeItemProperty().getValue();

        if (treeItem == null) {
            return;
        }

        Abstract_File file = treeItem.getValue();
        this.getStyleClass().set(0, getColor(file.getStatus()).toUpperCase());
    }

    private String getColor(Status s) {
        String res = "";

        if (Status.NEWER == s) {
            res = "NEWER";
        } else if (Status.OLDER == s) {
            res = "OLDER";
        } else if (Status.ORPHAN == s) {
            res = "ORPHAN";
        } else if (Status.PARTIAL_SAME == s) {
            res = "PARTIAL_SAME";
        } else if (Status.SAME == s) {
            res = "SAME";
        }

        return res;
    }

    String texte(T elem) {
        return "" + elem;
    }
}
