package myproject.pecintakucinglampung.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Perawatan implements Serializable {
    public String idPerawatan;
    public String idUser;
    public String nama;
    public String nope;
    public String foto;
    public String deskripsi;
    public String harga;
    public String rating;

    public Perawatan(){

    }

    public Perawatan(String idPerawatan,String idUser, String nama, String nope, String foto, String deskripsi, String harga,String rating) {
        this.idPerawatan = idPerawatan;
        this.idUser = idUser;
        this.nama = nama;
        this.nope = nope;
        this.foto = foto;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.rating = rating;
    }

    public String getIdPerawatan() {
        return idPerawatan;
    }

    public void setIdPerawatan(String idPerawatan) {
        this.idPerawatan = idPerawatan;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNope() {
        return nope;
    }

    public void setNope(String nope) {
        this.nope = nope;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }
}
