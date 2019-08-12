package myproject.pecintakucinglampung.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Kesehatan implements Serializable {

    public String idKesehatan;
    public String judul;
    public String deskripsi;
    public String solusi;

    public Kesehatan(){}

    public Kesehatan(String idKesehatan, String judul, String deskripsi, String solusi) {
        this.idKesehatan = idKesehatan;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.solusi = solusi;
    }

    public String getIdKesehatan() {
        return idKesehatan;
    }

    public void setIdKesehatan(String idKesehatan) {
        this.idKesehatan = idKesehatan;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getSolusi() {
        return solusi;
    }

    public void setSolusi(String solusi) {
        this.solusi = solusi;
    }
}
