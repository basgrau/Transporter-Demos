package de.basgrau.transporter.transportClient.clients;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.basgrau.transporter.shared.model.Message;
import de.basgrau.transporter.transportClient.Constants;

/**
 * UC3Client
 */
public class UC3Client implements IClient {

    public String sende(Message message, Client client) {

        Response response = client.target(Constants.BASE_WEB_TARGET).path(Constants.BASE_UC3_PATH).request()
                .post(Entity.entity(message, MediaType.APPLICATION_JSON));

        String result = response.readEntity(String.class);
        return result;
    }

}