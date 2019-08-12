package myproject.pecintakucinglampung.Kelas;

import java.io.Serializable;

public class ChatModel implements Serializable {

    public String fromId;
    public String toId;
    public String pesan;
    public String time;
    public String nama;
    public String idChat;

    public ChatModel(String fromId, String toId, String pesan, String time,String nama) {
        this.fromId = fromId;
        this.toId = toId;
        this.pesan = pesan;
        this.time = time;
        this.nama = nama;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public ChatModel(){}

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
