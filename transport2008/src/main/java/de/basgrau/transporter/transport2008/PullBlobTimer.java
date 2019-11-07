package de.basgrau.transporter.transport2008;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * PullBlobTimer
 */

@Singleton
public class PullBlobTimer {

    @PostConstruct
    public void setup() {
        if (DBUtil.createDBTableUC3()) {
            System.out.println("created DB-Table.");
        }
        System.out.println("DoneSetup.");
    }

    @Schedule(minute = "*/1", hour = "*", persistent = false)
    public void getMissingBlobs() {
        String[] ids = DBUtil.getIdsUC3(DBUtil.GRUND_OHNE_FILEDATA);
        System.out.println("Es ist/sind " + ids.length + " offne(r) Auftrag/Aufträge noch abzuarbeiten. Blobs fehlen!");
        for (String id : ids) {
            System.out.println("GetBlob für id: " + id + " START");
            final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            Response response = client.target(Constants.BASE_WEB_TARGET_2005).path(Constants.BASE_UC3_PATH + "/{id}")
                    .resolveTemplate("id", id).request().get();
            System.out.println("For id: " + response.getStatus());
            if (response.getStatus() == 200) {
                byte[] blob = response.readEntity(byte[].class);
                System.out.println("Für id: " + id + ((blob != null) ? " Blob bekommen" : " Blob nicht bekommen."));
                if (blob != null) {
                    if (DBUtil.writeBlobUC3(id, blob)) {
                        System.out.println("Blob für id: " + id + " erfolgreich geschrieben.");
                        Response responseInform = client.target(Constants.BASE_WEB_TARGET_2005)
                                .path(Constants.BASE_UC3_PATH + "/{id}").resolveTemplate("id", id).request().delete();
                        if (responseInform.readEntity(String.class).equals("200")) {
                            DBUtil.writeStatusBerabeitet(id);
                            System.out.println("Komplette Kette erfolgreich durchlaufen. Evtl. Restbestände vorhanden?");
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
    public void informTransport2005() {
        String[] ids = DBUtil.getIdsUC3(DBUtil.GRUND_NICHT_ABGERUFEN);
        for (String id : ids) {
            System.out.println("informTransport2002 für id: " + id + " START");
            final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            Response response = client.target(Constants.BASE_WEB_TARGET_2005).path(Constants.BASE_UC3_PATH + "/{id}")
                    .resolveTemplate("id", id).request().delete();
            if (response.readEntity(String.class).equals("200")) {
                System.out.println("Transport2005 erfolgreich zur Löschung informiert.");
                DBUtil.writeStatusBerabeitet(id);
                System.out.println("Komplette Kette erfolgreich durchlaufen. Evtl. Restbestände vorhanden?");
            }
            System.out.println("informTransport2002 für id: " + id + " ENDE");
        }
    }

}