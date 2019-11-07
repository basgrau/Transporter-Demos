package de.basgrau.transporter.transport2005;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        if (DBUtil.createDBTable()) {
            System.out.println("created DB-Table.");
        } else {
            return "404";
        }

        if (DBUtil.insert(message)) {
            System.out.println(sdf.format(new Date()) + ": ENDE");
            return "200";
        } else {
            System.out.println(sdf.format(new Date()) + ": ENDE");
            return "404";
        }
    }

    @GET
    @Path("{id}")
    public Response getBlob(@PathParam("id") String id) {
        System.out.println(sdf.format(new Date()) + ": START");
        System.out.println("Abruf durch 2008 für id: " + id);
        byte[] blob = DBUtil.getBlob(id);
        System.out.println(sdf.format(new Date()) + ": ENDE");
        return Response.ok(blob).build();
    }

    @DELETE
    @Path("{id}")
    public String deleteMessage(@PathParam("id") String id) {
        System.out.println("DELETE durch 2008.");
        if (DBUtil.updateAbgerufen(id)) {
            return "200";
        } else {
            // TODO: Eskalation müsste dann geschaffen werden.
            System.err.println("Status der Message "+id+" konnte nicht auf 'abgerufen' gesetzt werden!");
            return "405";
        }
    }

    @GET
    public String sayHello() {
        return this.getClass().getName();
    }

}