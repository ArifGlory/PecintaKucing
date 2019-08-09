package myproject.pecintakucinglampung.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Slider implements Serializable {

    public String urlGambar;
    public String link;
    public String idSlider;

    public Slider(){
    }

    public Slider(String urlGambar, String link) {
        this.urlGambar = urlGambar;
        this.link = link;
    }

    public String getIdSlider() {
        return idSlider;
    }

    public void setIdSlider(String idSlider) {
        this.idSlider = idSlider;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
