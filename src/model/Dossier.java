package model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class Dossier extends Abstract_File {

    private final SizeBinding sizeBinding = new SizeBinding();
    private final DateTimeBinding dateTimeBinding = new DateTimeBinding();

    private final List<Abstract_File> files = new ArrayList<>();

    public Dossier(String name, String path, LocalDateTime lastModif) {
        super(name, path, 0, false, lastModif);

        addToSizeBinding(getChildren());
        addToDateTimeBinding(getChildren());

        bindSizeTo(sizeBinding);
        bindDateTimeTo(dateTimeBinding);
    }

    @Override
    protected String displayFormat(int shift) {
        StringBuilder res = new StringBuilder();
        res.append(super.displayFormat(shift))
                .append(getName());
        if (getStatus() != Status.UNDEFINED) {
            res.append(" D ")
                    .append(getLastModif().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .append("  ").append(getSize()).append(" " + getStatus());
        }
        res.append("\n");
        files.forEach((f) -> {
            res.append(f.displayFormat(shift + 1));
        });
        return res.toString();
    }

    @Override
    public void addFolder(Abstract_File file) {
        files.add(file);

        addToSizeBinding(file.sizeProperty());
        addToDateTimeBinding(file.lastModifProperty());
        _addFolder(file);
    }

    @Override
    public void createList(List<Abstract_File> list) {
        for (Abstract_File f : files) {
            f.createList(list);
            list.add(f);
        }
    }

    @Override
    public Iterable<Abstract_File> fichiers() {
        return files;
    }

    private void addToSizeBinding(Observable obs) {
        sizeBinding.addBinding(obs);
        sizeBinding.invalidate();
    }

    private void addToDateTimeBinding(Observable obs) {
        dateTimeBinding.addBinding(obs);
        dateTimeBinding.invalidate();
    }

    @Override
    public void setContenu(String newContenu) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFichierTexte() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class SizeBinding extends ObjectBinding<Integer> {

        protected Integer computeValue() {
            return getChildren().stream().map(f -> f.getValue().getSize()).reduce(0, (s1, s2) -> s1 + s2);
        }

        void addBinding(Observable obs) {
            super.bind(obs);
        }
    }

    // Un Binding pour le recalcul de la date
    private class DateTimeBinding extends ObjectBinding<LocalDateTime> {

        @Override
        protected LocalDateTime computeValue() {
            return getChildren().isEmpty() ? LocalDateTime.now() : getChildren().stream().map(f -> f.getValue().getLastModif()).max(LocalDateTime::compareTo).get();
        }

        void addBinding(Observable obs) {
            super.bind(obs);
        }
    }

    public int countStates(Status s, int res) {

        for (Abstract_File f : files) {

            if (!f.isFile()) {
                res += ((Dossier) f).countStates(s, res);

            } else if (f.getStatus() == s) {
                res++;
            }
        }

        return res;
    }

    private int nbOlder() {
        return countStates(older, 0);
    }

    private int nbNewer() {
        return countStates(newer, 0);
    }

    private int nbSame() {
        return countStates(same, 0);
    }

    private int nbOrp() {
        return countStates(orp, 0);
    }

    public boolean same(Abstract_File f) {
        return f.getPath().equals(this.getPath()) && (!this.getContent().isEmpty() && !f.getContent().isEmpty());
    }

    @Override
    public void compareAndSetStatus(Abstract_File file) {
        this.setStatus(ptsame);

        if (file == null || !same(file)) {
            this.setStatus(orp);

        } else {
            if (nbSame() == 0 && nbOrp() > 0 && nbNewer() == 0 && nbOlder() == 0) {
                this.setStatus(orp);

            } else if (nbSame() > 0 && nbOrp() == 0 && nbNewer() == 0 && nbOlder() == 0) {
                this.setStatus(same);

            } else if (nbNewer() > nbOlder() && nbSame() >= 0 && nbOrp() == 0) {
                this.setStatus(newer);

            } else if (nbNewer() < nbOlder() && nbSame() >= 0 && nbOrp() == 0) {
                this.setStatus(older);
            }
        }

        //pour dossierVIDE 
        if (this.getContent().isEmpty() && file.getChildren().isEmpty() && (!this.isFile() && !file.isFile())) {
            //suppression des nano secondes 
            LocalDateTime fichierThis = this.getLastModif().minusNanos(this.getLastModif().getNano());
            LocalDateTime fichierFile = file.getLastModif().minusNanos(file.getLastModif().getNano());

            if (fichierThis.isBefore(fichierFile)) {
                this.setStatus(older);

            } else if (fichierThis.isAfter(fichierFile)) {
                this.setStatus(newer);

            } else if (fichierThis.isEqual(fichierFile)) {
                this.setStatus(same);
            }
        }
    }
}
