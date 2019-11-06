package de.basgrau.transporter.transportClient;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import de.basgrau.transporter.shared.model.Message;
import de.basgrau.transporter.transportClient.clients.UC1Client;
import de.basgrau.transporter.transportClient.clients.UC2Client;

public class StartApp {

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");

    public static void main(String[] args) {
        System.out.println("Start.");

        Message message = new Message();
        message.setFiledata(generateBytes(15));
        message.setSender("16");
        message.setSenddate(sdf.format(new Date()));

        final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);

        // sendeUC1(client, message);
        System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        sendeUC2(client, message);
    }

    private static void sendeUC1(Client client, Message message) {
        Date startDate = new Date();
        System.out.println(sdf.format(startDate) + ": Use Case 1 START:");
        UC1Client uc1 = new UC1Client();
        System.out.println("Response from the Server: " + uc1.sende(message, client));
        Date endeDate = new Date();
        System.out.println(sdf.format(endeDate) + ": Use Case 1 ENDE");

        long diffInMillies = Math.abs(endeDate.getTime() - startDate.getTime());

        if (diffInMillies > 60000) {
            double min = ((double) diffInMillies / 60000);
            System.out.println("Gesamtdauer UC1: " + min + "M");
        } else if (diffInMillies > 1000) {
            double sek = ((double) diffInMillies) / 1000;
            System.out.println("Gesamtdauer UC1: " + sek + "s");
        } else {
            System.out.println("Gesamtdauer UC1: " + diffInMillies + "MS");
        }
    }

    private static void sendeUC2(Client client, Message message) {
        Date startDate = new Date();
        System.out.println(sdf.format(startDate) + ": Use Case 2 START:");
        UC2Client uc2 = new UC2Client();
        System.out.println("Response from the Server: " + uc2.sende(message, client));
        Date endeDate = new Date();
        System.out.println(sdf.format(endeDate) + ": Use Case 2 ENDE");

        long diffInMillies = Math.abs(endeDate.getTime() - startDate.getTime());

        if (diffInMillies > 60000) {
            double min = ((double) diffInMillies / 60000);
            System.out.println("Gesamtdauer UC1: " + min + "M");
        } else if (diffInMillies > 1000) {
            double sek = ((double) diffInMillies) / 1000;
            System.out.println("Gesamtdauer UC1: " + sek + "s");
        } else {
            System.out.println("Gesamtdauer UC1: " + diffInMillies + "MS");
        }
    }

    private static byte[] generateBytes(int size) {
        char[] chars = new char[size * 1024 * 1024];
        Arrays.fill(chars, 'a');

        return new String(chars).getBytes();
    }

}
