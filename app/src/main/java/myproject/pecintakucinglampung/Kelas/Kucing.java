package myproject.pecintakucinglampung.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Kucing implements Serializable {
    public String nama;
    public String idPemilik;
    public String umur;
    public String ras;
    public String urlGambar;
    public String idKucing;
    public String isAdopsi;
    public String isDijual;

    public Kucing(){

    }

    public Kucing(String nama, String idPemilik, String umur, String ras, String urlGambar) {
        this.nama = nama;
        this.idPemilik = idPemilik;
        this.umur = umur;
        this.ras = ras;
        this.urlGambar = urlGambar;
    }

    public String getIsAdopsi() {
        return isAdopsi;
    }

    public void setIsAdopsi(String isAdopsi) {
        this.isAdopsi = isAdopsi;
    }

    public String getIsDijual() {
        return isDijual;
    }

    public void setIsDijual(String isDijual) {
        this.isDijual = isDijual;
    }

    public String getIdKucing() {
        return idKucing;
    }

    public void setIdKucing(String idKucing) {
        this.idKucing = idKucing;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(String idPemilik) {
        this.idPemilik = idPemilik;
    }

    public String getUmur() {
        return umur;
    }

    public void setUmur(String umur) {
        this.umur = umur;
    }

    public String getRas() {
        return ras;
    }

    public void setRas(String ras) {
        this.ras = ras;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }
}
