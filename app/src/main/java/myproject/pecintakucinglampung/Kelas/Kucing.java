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
    public String jenisKelamin;
    public String nmDokterLangganan;
    public String kondisiKesehatan;
    public String jenisMakanan;
    public String susu;
    public String shampo;
    public String deskripsiPerawatan;

    public Kucing(){

    }

    public Kucing(String nama, String idPemilik, String umur, String ras, String urlGambar
    ,String jenisKelamin,String nmDokterLangganan,String kondisiKesehatan,String jenisMakanan
    ,String susu,String shampo,String deskripsiPerawatan) {
        this.nama = nama;
        this.idPemilik = idPemilik;
        this.umur = umur;
        this.ras = ras;
        this.urlGambar = urlGambar;
        this.jenisKelamin = jenisKelamin;
        this.nmDokterLangganan = nmDokterLangganan;
        this.kondisiKesehatan = kondisiKesehatan;
        this.jenisMakanan = jenisMakanan;
        this.susu = susu;
        this.shampo = shampo;
        this.deskripsiPerawatan = deskripsiPerawatan;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getNmDokterLangganan() {
        return nmDokterLangganan;
    }

    public void setNmDokterLangganan(String nmDokterLangganan) {
        this.nmDokterLangganan = nmDokterLangganan;
    }

    public String getKondisiKesehatan() {
        return kondisiKesehatan;
    }

    public void setKondisiKesehatan(String kondisiKesehatan) {
        this.kondisiKesehatan = kondisiKesehatan;
    }

    public String getJenisMakanan() {
        return jenisMakanan;
    }

    public void setJenisMakanan(String jenisMakanan) {
        this.jenisMakanan = jenisMakanan;
    }

    public String getSusu() {
        return susu;
    }

    public void setSusu(String susu) {
        this.susu = susu;
    }

    public String getShampo() {
        return shampo;
    }

    public void setShampo(String shampo) {
        this.shampo = shampo;
    }

    public String getDeskripsiPerawatan() {
        return deskripsiPerawatan;
    }

    public void setDeskripsiPerawatan(String deskripsiPerawatan) {
        this.deskripsiPerawatan = deskripsiPerawatan;
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
