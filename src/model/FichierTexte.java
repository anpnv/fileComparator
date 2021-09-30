package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Julien ROBERFROID <1706juroberfroid>
 */
public class FichierTexte extends FichierSimple {

    StringProperty contenu = new SimpleStringProperty();

    public FichierTexte(String name, String path, int size, LocalDateTime lastModif, String contenuParam) {
        super(name, path, size, lastModif);
        setContenu(contenuParam);

    }

    @Override
    public String getContenu() {
        return contenu.getValue();
    }

    public ReadOnlyStringProperty contenuProperty() {
        return contenu;
    }

    @Override
    public void setContenu(String newContenu) {
        this.contenu.setValue(newContenu);
        //this.setSize(newContenu.length());
        //this.setDateTime(LocalDateTime.now());

        //System.out.println("FichierTxt newContenu == " + newContenu);
    }

    @Override
    public boolean isFichierTexte() {
        return true;
    }

}
