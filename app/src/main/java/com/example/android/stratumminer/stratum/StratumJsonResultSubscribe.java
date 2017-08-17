package com.example.android.stratumminer.stratum;

import com.example.android.stratumminer.HexArray;
import com.example.android.stratumminer.MinyaException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumJsonResultSubscribe extends StratumJsonResult {
    public final String session_id;
    public final HexArray xnonce1;
    public final int xnonce2_size;
    public final static String TEST_PATT ="{\"id\":1,\"result\":[[\"mining.notify\",\"b86c07fd6cc70b367b61669fb5e91bfa\"],\"f8000105\",4],\"error\":null}";
    public StratumJsonResultSubscribe(JsonNode i_json_node) throws MinyaException {
        super(i_json_node);
        //エラー理由がある場合
        if(this.error!=null){
            throw new MinyaException(this.error.asText());
        }
        JsonNode n = i_json_node.get("result");
        if (!n.isArray()) {
            throw new MinyaException();
        }
        // sessionID
        if ((n.get(0).get(0).get(0) != null )) {
            if (n.get(0).get(0).get(0).asText().compareTo("mining.notify") != 0) {
                throw new MinyaException();
            }
        } else {
            if (n.get(0).get(0).asText().compareTo("mining.notify") != 0) {
                throw new MinyaException();
            }
        }

        this.session_id = n.get(0).get(1).asText();
        // xnonce1
        this.xnonce1 = new HexArray(n.get(1).asText());
        //xnonce2_size
        this.xnonce2_size = n.get(2).asInt();
        return;
    }
}