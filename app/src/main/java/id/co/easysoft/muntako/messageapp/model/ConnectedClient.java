package id.co.easysoft.muntako.messageapp.model;

/**
 * Created by ADMIN on 05-Sep-17.
 *
 */

public class ConnectedClient {
    private int id;
    private String nama;
    private String ipAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
