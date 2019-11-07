package de.basgrau.transporter.transport2002;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import de.basgrau.transporter.shared.model.Message;

/**
 *
 */
@Path("/uc1ctrlr")
@Singleton
public class UseCase1Controller {

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String receive(Message message) {
        System.out.println(sdf.format(new Date()) + ": START");

        // Send to 2005
        final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        Response response = client.target(Constants.BASE_WEB_TARGET).path(Constants.BASE_UC1_PATH).request()
        .post(Entity.entity(message, MediaType.APPLICATION_JSON));
        String result = response.readEntity(String.class);

        if(result.equals("200")){
            System.out.println(sdf.format(new Date()) + ": ENDE");
            return "200";
        }else{
            System.out.println(sdf.format(new Date()) + ": ENDE");
            return result;
        }
    }

    @GET
    public String sayHello() {
        return this.getClass().getName();
    }

}
