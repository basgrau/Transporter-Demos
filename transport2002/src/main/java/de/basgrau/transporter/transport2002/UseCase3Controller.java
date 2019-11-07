package de.basgrau.transporter.transport2002;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("/uc3ctrlr")
@Singleton
public class UseCase3Controller {

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String receive(Message message) {
        System.out.println(sdf.format(new Date()) + ": START");

        if (DBUtil.createDBTable(DBUtil.UC3)) {
            System.out.println("created DB-Table.");
        }

        boolean success = DBUtil.writeClob(DBUtil.UC3, message);

        if (success) {
            message.setFiledata(null);
            int id = DBUtil.getId(DBUtil.UC3, message.getSenddate());
            System.out.println("FileId: " + id);
            message.setFileid("" + id);

            // Send to 2005
            final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            Response response = client.target(Constants.BASE_WEB_TARGET).path(Constants.BASE_UC3_PATH).request()
                    .post(Entity.entity(message, MediaType.APPLICATION_JSON));

            String result = response.readEntity(String.class);

            if(result.equals("200")){
                System.out.println(sdf.format(new Date()) + ": ENDE");
                return "200";
            }else{
                System.out.println(sdf.format(new Date()) + ": ENDE");
                return result;
            }
        } else {
            System.err.println("Konnte nicht in DB geschrieben werden!");
            System.out.println(sdf.format(new Date()) + ": ENDE");
            return "404";
        }
    }

    @GET
    @Path("{id}")
    public Response getBlob(@PathParam("id") String id) {
        System.out.println(sdf.format(new Date()) + ": START");
        System.out.println("Abruf durch 2005 für id: " + id);
        byte[] blob = DBUtil.getBlob(id);
        System.out.println(sdf.format(new Date()) + ": ENDE");
        return Response.ok(blob).build();
    }

    @DELETE
    @Path("{id}")
    public String deleteBlob(@PathParam("id") String id) {
        System.out.println("Abruf durch 2005.");
        if (DBUtil.delete(id)) {
            return "200";
        } else {
            // TODO: Eskalation müsste dann geschaffen werden.
            System.err.println("Konnte nicht gelöscht werden!");
            return "405";
        }
    }

    @GET
    public String sayHello() {
        return this.getClass().getName();
    }

}
