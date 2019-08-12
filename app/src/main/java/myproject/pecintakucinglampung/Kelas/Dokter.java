package myproject.pecintakucinglampung.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Dokter implements Serializable {

    public String idDokter;
    public String nama;
    public String phone;
    public String alamat;
    public String jadwal;
    public String bidang;
    public String urlGambar;
    public String lat;
    public String lon;

    public Dokter(){

    }

    public Dokter(String nama, String phone, String alamat, String jadwal, String bidang, String urlGambar,String lat,String lon) {
        this.nama = nama;
        this.phone = phone;
        this.alamat = alamat;
        this.jadwal = jadwal;
        this.bidang = bidang;
        this.urlGambar = urlGambar;
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(String idDokter) {
        this.idDokter = idDokter;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJadwal() {
        return jadwal;
    }

    public void setJadwal(String jadwal) {
        this.jadwal = jadwal;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }
}
