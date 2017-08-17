package com.example.android.stratumminer.stratum;

import com.example.android.stratumminer.MinyaException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumJsonMethodReconnect extends StratumJsonMethod
{
    //{"method":"client.reconnect",params:["host",1]}
    public final static String TEST_PATT = "{\"params\": [\"host\",80], \"jsonrpc\": \"2.0\", \"method\": \"client.reconnect\", \"id\": null}";

    // public parameter
    public int port;
    public String host;

    public StratumJsonMethodReconnect(JsonNode i_json_node) throws MinyaException {
        super(i_json_node);
        String s = i_json_node.get("method").asText();
        if (s.compareTo("client.reconnect") != 0) {
            throw new MinyaException();
        }
        JsonNode p=i_json_node.get("params");
        this.host=p.get(0).asText();
        this.port=p.get(1).asInt();
        return;
    }
}