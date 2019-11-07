package de.basgrau.transporter.transport2008;

import java.util.Arrays;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Path("/blob")
@Singleton
public class BlobGetter {

    @GET
    @Path("{id}/{usecase}")
    public String getBlob(@PathParam("id") String id, @PathParam("usecase") int usecase) {
        byte[] blob = DBUtil.getBlob(usecase, id);

        return new String(blob);
    }

}
