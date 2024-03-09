package de.charton;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * User: acharton
 * Date: 22.11.13
 */
class ElasticService {

    private final Node node;
    private final Client client;

    ElasticService() {
        this.node = NodeBuilder.nodeBuilder().clusterName("mycluster").client(true).node();
        this.client = node.client();
    }

    void shutdown() {
        this.node.stop();
    }

}
