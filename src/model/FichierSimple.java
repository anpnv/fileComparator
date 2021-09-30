package model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class FichierSimple extends Abstract_File {

    public FichierSimple(String name, String path, int size, LocalDateTime lastModif) {
        super(name, path, size, true, lastModif);
    }

    @Override
    protected String displayFormat(int shift) {
        StringBuilder res = new StringBuilder();
        res.append(super.displayFormat(shift))
                .append(getName())
                .append(" F ")
                .append(getLastModif().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .append(" " + getSize())
                .append(" " + getStatus() + "\n");
        return res.toString();
    }

    @Override
    public void addFolder(Abstract_File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createList(List<Abstract_File> list) {
        list.add(this);
    }

    @Override
    public Iterable<Abstract_File> fichiers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void compareAndSetStatus(Abstract_File file) {
        if (file == null || (file.isFile() && this.isFile())) {

            if (this.getName().equals(file.getName())) {

                if (this.getLastModif().isBefore(file.getLastModif())) {
                    this.setStatus(older);

                } else if (this.getLastModif().isAfter(file.getLastModif())) {
                    this.setStatus(newer);

                } else if (this.getLastModif().isEqual(file.getLastModif())) {
                    this.setStatus(same);
                }
            }
        } else {
            this.setStatus(orp);
        }
    }

    @Override
    public void setContenu(String newContenu) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFichierTexte() {
        return false;
    }

    @Override
    public String getContenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
