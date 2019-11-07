package de.basgrau.transporter.transport2002;

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
 * InfoTimer
 */

@Singleton
public class InfoTimer {

    @PostConstruct
    public void setup() {
        System.out.println("DoneSetup.");
    }

    @Schedule(minute = "*/1", hour = "*", persistent = false)
    public void doWork() {
        String[] ids = DBUtil.getIds();
        System.out.println("Es ist/sind " + ids.length + " offne(r) Auftrag/Aufträge noch abzuarbeiten.");

        for (String id : ids) {
            if (informTransporter2005(id)) {
                System.out.println("2005 informiert und Eintrag gelöscht.");
            }
        }

    }

    private boolean informTransporter2005(String id) {
        Message message = DBUtil.getMessage(id);
        final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        Response response = client.target(Constants.BASE_WEB_TARGET).path(Constants.BASE_UC3_PATH).request()
                .post(Entity.entity(message, MediaType.APPLICATION_JSON));

        if (response.readEntity(String.class).equals("200")) {
            return true;
        }

        return false;
    }
}