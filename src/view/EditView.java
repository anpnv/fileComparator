package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mvvm.*;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class EditView extends Stage {

    private Button saveButton = new Button("save");

    private TextArea textArea = new TextArea();
    private StackPane stackPane = new StackPane(textArea);

    private final VBox root = new VBox();
    
    public EditView(Stage primaryStage, EditVM editVM) {
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);

        setScene(new Scene(root, 600, 400));
        configLayout(editVM);
        binding(editVM);
        listeners(editVM);

    }

    private void configLayout(EditVM editVM) {
        HBox btn = new HBox();

        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(10, 0, 0, 0));
        btn.getChildren().add(saveButton);

        stackPane.setMinHeight(360);
        root.getChildren().addAll(stackPane, btn);
        textArea.setWrapText(true);
        setOnHiding((e) -> editVM.setVisible(false));
    }

    private void binding(EditVM editVM) {
        textArea.textProperty().bindBidirectional(editVM.textProperty());
        editVM.saveButtonProperty().bind(saveButton.armedProperty());
        titleProperty().bind(editVM.fileNameProperty().concat(" : ").concat(editVM.textLengthProperty()).concat(" octets"));
    }

    private void listeners(EditVM editVM) {
        editVM.showingProperty().addListener((obj, old, act) -> {
            if (act) {
                showAndWait();
            }
        });

        editVM.saveButtonProperty().addListener((o, old, newVal) -> {
            if (!old && newVal) {
                close();
                System.out.println("Modification effecutée avec succès...");
            }
        });

    }

}
