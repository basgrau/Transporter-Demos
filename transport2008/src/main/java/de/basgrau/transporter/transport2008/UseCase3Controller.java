package de.basgrau.transporter.transport2008;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

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
//TODO
        byte[] file = message.getFiledata();

        System.out.println("done...");
        System.out.println(sdf.format(new Date()) + ": ENDE");
        return "200";
    }

    @GET
    public String sayHello() {
        return this.getClass().getName();
    }

}
