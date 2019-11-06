package de.basgrau.transporter.shared.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Message
 */

public class Message  implements Serializable {

    private String sender;
    private String senddate;
    private byte[] filedata;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenddate() {
        return senddate;
    }

    public void setSenddate(String senddate) {
        this.senddate = senddate;
    }

    public byte[] getFiledata() {
        return filedata;
    }

    public void setFiledata(byte[] filedata) {
        this.filedata = filedata;
    }

}