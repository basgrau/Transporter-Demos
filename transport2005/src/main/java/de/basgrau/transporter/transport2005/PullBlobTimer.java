package de.basgrau.transporter.transport2005;

import java.sql.Blob;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import de.basgrau.transporter.shared.model.Message;

/**
 * PullBlobTimer
 */

@Singleton
public class PullBlobTimer {

    @PostConstruct
    public void setup() {
        if (DBUtil.createDBTable()) {
            System.out.println("created DB-Table.");
        }
        System.out.println("DoneSetup.");
    }

    @Schedule(minute = "*/1", hour = "*", persistent = false)
    public void getMissingBlobs() {
        
        String[] ids = DBUtil.getIds(DBUtil.GRUND_OHNE_FILEDATA);
        for (String id : ids) {
            System.out.println("GetBlob für id: " + id + " START");
            final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            Response response = client.target(Constants.BASE_WEB_TARGET_2002).path(Constants.BASE_UC3_PATH + "/{id}")
                    .resolveTemplate("id", id).request().get();
            System.out.println("For id: " + response.getStatus());
            if (response.getStatus() == 200) {
                byte[] blob = response.readEntity(byte[].class);
                System.out.println("Für id: " + id + ((blob != null) ? " Blob bekommen" : " Blob nicht bekommen."));
                if (blob != null) {
                    if (DBUtil.writeBlobUC3(id, blob)) {
                        System.out.println("Blob für id: " + id + " erfolgreich geschrieben.");
                        Response responseInform = client.target(Constants.BASE_WEB_TARGET_2002)
                                .path(Constants.BASE_UC3_PATH + "/{id}").resolveTemplate("id", id).request().delete();
                        if (responseInform.readEntity(String.class).equals("200")) {
                            System.out.println("Transport2002 erfolgreich zur Löschung informiert.");
                        }
                    } else {
                        System.err.println("Blob für id: " + id + " nicht erfolgreich geschrieben.");
                    }
                }
            } else {
                System.err.println("Error.");
            }
            System.out.println("GetBlob für id: " + id + " ENDE");
        }

    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void informTransport2002() {
        String[] ids = DBUtil.getIds(DBUtil.GRUND_BEREITS_ABGERUFEN);
        for (String id : ids) {
            System.out.println("informTransport2002 für id: " + id + " START");
            final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            Response response = client.target(Constants.BASE_WEB_TARGET_2002).path(Constants.BASE_UC3_PATH + "/{id}")
                    .resolveTemplate("id", id).request().delete();
            if (response.readEntity(String.class).equals("200")) {
                System.out.println("Transport2002 erfolgreich zur Löschung informiert.");
            }
            System.out.println("informTransport2002 für id: " + id + " ENDE");
        }
    }

    @Schedule(minute = "*/1", hour = "*", persistent = false)
    public void informTransport2008() {
        String[] ids = DBUtil.getIds(DBUtil.GRUND_NICHT_ABGERUFEN);
        System.out.println("Es ist/sind " + ids.length + " offne(r) Auftrag/Aufträge noch abzuarbeiten.");
        for (String id : ids) {
            System.out.println("informTransport2002 für id: " + id + " START");
            Message message = DBUtil.getMessage(id);
            if (message != null) {
                final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
                Response response = client.target(Constants.BASE_WEB_TARGET).path(Constants.BASE_UC3_PATH).request()
                        .post(Entity.entity(message, MediaType.APPLICATION_JSON));
                if (response.readEntity(String.class).equals("200")) {
                    System.out.println("Transport2002 erfolgreich zur Löschung informiert.");
                }
            } else {
                System.err.println("Message für id : " + id + " konnte nicht aus DB ausgelesen werden.");
            }

            System.out.println("informTransport2002 für id: " + id + " ENDE");
        }
    }
}