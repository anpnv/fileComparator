package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.*;
import mvvm.*;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class View extends HBox {

    private final Button all = new Button("All");
    private final ToggleButton newLeft = new ToggleButton("Newer Left");
    private final ToggleButton newRight = new ToggleButton("Newer Right");
    private final ToggleButton orphan = new ToggleButton("Orphans");
    private final ToggleButton same = new ToggleButton("Same");
    private final ToggleButton folders = new ToggleButton("Folder Only");
    private final ToggleGroup orphanGroup = new ToggleGroup();
    private final ToggleGroup sameGroup = new ToggleGroup();
    private final ToggleGroup folderGroup = new ToggleGroup();
    private final ToggleGroup newerGroup = new ToggleGroup();

    private final HBox boxButtons = new HBox();
    private final VBox root = new VBox();
    private final HBox fileColums = new HBox();
    private final HBox footer = new HBox();
    private final HBox secondFooter = new HBox();
    private final HBox topButton = new HBox();

    private final TableView left = new TableView();
    private final TableView right = new TableView();

    private final LabelStatus ColorIndicator = new LabelStatus();
    private final Image image = new Image("folder.png");
    private TreeItem selected = new TreeItem();

    private final Button leftToRight = new Button("Move Left -> Right");

    MenuBar menuBar = new MenuBar();
    Menu menuFile = new Menu("File");

    private final VM vm;

    public View(Stage primaryStage, VM vm) {
        this.vm = vm;
        new EditView(primaryStage, vm.getEditVM());
        setBindings(vm);
        listeners();
        setupScene(primaryStage);
    }

    private void listeners() {
        leftTableItem();
        rightTableItem();
    }

    private void setupScene(Stage primaryStage) {
        configLayout();

        primaryStage.getIcons().add(image);
        primaryStage.setTitle("File Manager ");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();
    }

    private void setBindings(VM vm) {
        left.getTable().rootProperty().bindBidirectional(vm.rootLeftProperty());
        right.getTable().rootProperty().bindBidirectional(vm.rootRightProperty());

        left.getLabel().textProperty().bindBidirectional(vm.leftLabel());
        right.getLabel().textProperty().bindBidirectional(vm.rightLabel());

        folders.selectedProperty().bindBidirectional(vm.getFoldersPressed());
        newLeft.selectedProperty().bindBidirectional(vm.getNewLeft());
        newRight.selectedProperty().bindBidirectional(vm.getNewRight());
        orphan.selectedProperty().bindBidirectional(vm.getOrphan());
        same.selectedProperty().bindBidirectional(vm.getSame());

        vm.bindToAll(all.armedProperty());
        vm.bindLeftBtn(left.getLoad().armedProperty());
        vm.bindRightBtn(right.getLoad().armedProperty());
        vm.itemSelectedProperty().bind(selected.valueProperty());

        leftToRight.disableProperty().bindBidirectional(vm.leftToRight());
        vm.getTransfert().bind(leftToRight.armedProperty());
    }

    private void configLayout() {
        setBtns();

        left.setMinWidth(530);
        left.setMinHeight(550);
        right.setMinWidth(530);
        right.setMinHeight(550);

        footer.setAlignment(Pos.TOP_CENTER);
        footer.setSpacing(10);

        fileColums.getChildren().addAll(left, right);
        fileColums.setSpacing(20);

        topButton.getChildren().add(boxButtons);
        topButton.setAlignment(Pos.CENTER);
        topButton.setPadding(new Insets(10, 0, 10, 0));

        secondFooter.getChildren().addAll(leftToRight);
        secondFooter.setAlignment(Pos.CENTER);
        secondFooter.setSpacing(10);

        menuBar.getMenus().add(menuFile);

        footer.getChildren().add(ColorIndicator);

        root.setAlignment(Pos.TOP_CENTER);
        root.getStylesheets().add("view/style.css");
        root.getChildren().addAll(menuBar, topButton, fileColums, footer, secondFooter);
    }

    public void setBtns() {
        newLeft.setToggleGroup(newerGroup);
        newRight.setToggleGroup(newerGroup);
        orphan.setToggleGroup(orphanGroup);
        same.setToggleGroup(sameGroup);
        folders.setToggleGroup(folderGroup);

        boxButtons.getChildren().addAll(all, newLeft, newRight, orphan, same, folders);
        boxButtons.setSpacing(20);
    }

    public Image getImage() {
        return image;
    }

    class LabelStatus extends HBox {

        private final HBox boxEnum = new HBox();

        private final Label orphan = new Label(Status.ORPHAN.toString());
        private final Label same = new Label(Status.SAME.toString());
        private final Label partial_same = new Label(Status.PARTIAL_SAME.toString());
        private final Label newer = new Label(Status.NEWER.toString());
        private final Label older = new Label(Status.OLDER.toString());

        private static final String CSSPATH = "view/style.css";

        {
            getChildren().addAll(boxEnum);
        }

        public LabelStatus() {
            getStylesheets().add(CSSPATH);
            config();
        }

        private void config() {
            boxEnum.getChildren().addAll(orphan, same, partial_same, newer, older);
            boxEnum.setPadding(new Insets(5));
            boxEnum.setSpacing(20);
            boxEnum.setStyle("-fx-font-weight: bold");

            orphan.getStyleClass().set(0, "ORPHAN");
            same.getStyleClass().set(0, "SAME");
            partial_same.getStyleClass().set(0, "PARTIAL_SAME");
            newer.getStyleClass().set(0, "NEWER");
            older.getStyleClass().set(0, "OLDER");
        }
    }

    private void leftTableItem() {
        left.getTable().setOnMousePressed(e -> {

            if (e.getClickCount() == 2) {

                right.getTable().getSelectionModel().clearSelection();
                Abstract_File item = left.getTable().getSelectionModel().getSelectedItem().getValue();
//                System.out.println(item);

                if (item.getName().endsWith(".txt")) {

                    selected.setValue(item);
                    item.getContenu();

                    vm.fireAction(true);
                }
            }
        });
    }

    private void rightTableItem() {
        right.getTable().setOnMousePressed(e -> {

            if (e.getClickCount() == 2) {

                left.getTable().getSelectionModel().clearSelection();
                Abstract_File item = right.getTable().getSelectionModel().getSelectedItem().getValue();

                if (item.getName().endsWith(".txt")) {
                    selected.setValue(item);

                    vm.fireAction(false);

                }
            }
        });
    }
}
