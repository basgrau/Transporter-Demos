package de.basgrau.transporter.transportClient.clients;

import javax.ws.rs.client.Client;

import de.basgrau.transporter.shared.model.Message;

/**
 * IClient
 */
public interface IClient {

    String sende(Message message, Client client);
}