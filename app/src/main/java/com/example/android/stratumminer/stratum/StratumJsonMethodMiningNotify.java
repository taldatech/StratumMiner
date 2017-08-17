package com.example.android.stratumminer.stratum;

import com.example.android.stratumminer.HexArray;
import com.example.android.stratumminer.MinyaException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumJsonMethodMiningNotify extends StratumJsonMethod {
    public final static String TEST_PATT = "{\"params\":"
            + "[\"113341302436276469056253036457324159787\","
            + " \"7bbb491d8da308c42bb84c28dd6c8f77aa0102a676b266bec530c737959ec4b4\","
            + " \"01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0b03485215062f503253482fffffffff110024f400000000001976a9140833120f05dc473d8d3e4b7f0a7174207e62144588acfd718006000000001976a9147077906a5f88819a036bbb804c2435e239b384ef88aceea18106000000001976a91483eb72afc57909a4b44430e9783b2d18cb24315b88ac83708706000000001976a914a1d2c08641709fb25bbd5f35a3de1eae2fff161f88ac43ef9c06000000001976a9149d5584f55d593958a20e6b8f7353345551f8fd3d88ac05c1c406000000001976a914147a6c5a927d9422a819b58cb6a4803523b1c08888ac063dc706000000001976a9142fe80ea5aaf8dbfc819265dd2e6d8a56b5979b0f88ac401bb20b000000001976a9143bc4089dbb0a83c69365926434ccba8b8001626088acec522b0d000000001976a914ce1f15f063a332c71b9030a7ecffc4f22dfc635888ac86142e0d000000001976a914a68ce3ed72d8b8bd695129c97ff276ea673b179c88acf0ac300d000000001976a91420ac5a56c73a88e6492556d438c27d6844746e0388ac50f2330d000000001976a9148639380456dec002ed1238eabdabfb25cf8e88b988ac20f46b0d000000001976a914df5a67b33cb8c08693c0dcbbfd5a619632f9e66688ac757f451a000000001976a9149bd07713fd6c44bb2a0652f5c21f654420448e1b88acb7f4f32d000000001976a914b553762cbd73a73ed3def6f7fb48eec16a88191788ac0600000000000000434104ffd03de44a6e11b9917f3a29f9443283d9871c9d743ef30d5eddcd37094b64d1b3d8090496b53256786bf5c82932ec23c3b74d9f05a6f95a8b5529352656664bac00000000000000002a6a287cf05a8a20076248b28f7963992718a0dd3765ea76c77c8656dcf6ff985c4eb9000000000100\","
            + " \"00000000\", [], \"00000001\", \"1d00bf80\", \"52ac5e55\", true], \"jsonrpc\": \"2.0\", \"method\": \"mining.notify\", \"id\": 764659215}";
    // public
    public final String job_id;
    public final HexArray version;
    public final HexArray[] merkle_arr;
    public final HexArray ntime;
    public final HexArray nbit;
    public final boolean clean;
    public final HexArray prev_hash;
    public final HexArray coinb1;
    public final HexArray coinb2;

    public StratumJsonMethodMiningNotify(JsonNode i_json_node) throws MinyaException {
        super(i_json_node);
        {
            String s = i_json_node.get("method").asText();
            if (s.compareTo("mining.notify") != 0) {
                throw new MinyaException();
            }
        }
        JsonNode params = i_json_node.get("params");
        // job_id
        {
            this.job_id = params.get(0).asText();
        }
        {
            this.prev_hash = toHexArray(params.get(1).asText(), 64);
        }
        {
            this.coinb1 = new HexArray(params.get(2).asText());
        }
        {
            this.coinb2 = new HexArray(params.get(3).asText());
        }
        // merkle_arr
        {
            JsonNode merkle_arr = params.get(4);
            if (!merkle_arr.isArray()) {
                throw new MinyaException();
            }
            ArrayList<HexArray> l=new ArrayList<HexArray>();
            for (Iterator<JsonNode> i = merkle_arr.iterator(); i.hasNext();) {
                l.add(toHexArray(i.next().asText(), 64));
            }
            this.merkle_arr = l.toArray(new HexArray[l.size()]);
        }
        // version
        {
            this.version = toHexArray(params.get(5).asText(), 8);
        }
        // nbit
        {
            this.nbit = toHexArray(params.get(6).asText(), 8);
        }
        // ntime
        {
            this.ntime = toHexArray(params.get(7).asText(), 8);
        }
        // clean
        {
            this.clean = params.get(8).asBoolean();
        }
    }
    public HexArray getXnonce2(StratumJsonResultSubscribe i_subscribe)
    {
        //xnonce2
        HexArray xnonce2=new HexArray(new byte[i_subscribe.xnonce2_size]);
        return xnonce2;
    }
    public HexArray getCoinbase(StratumJsonResultSubscribe i_subscribe)
    {
        //coinbase
        HexArray coinbase=new HexArray(this.coinb1);
        coinbase.append(i_subscribe.xnonce1);
        coinbase.append(this.getXnonce2(i_subscribe));
        coinbase.append(this.coinb2);
        return coinbase;
//		coinb1_size = strlen(coinb1) / 2;
//		coinb2_size = strlen(coinb2) / 2;
//		sctx->job.coinbase_size = coinb1_size + sctx->xnonce1_size +sctx->xnonce2_size + coinb2_size;
//		sctx->job.coinbase = realloc(sctx->job.coinbase, sctx->job.coinbase_size);
//		hex2bin(sctx->job.coinbase, coinb1, coinb1_size);
//		memcpy(sctx->job.coinbase + coinb1_size, sctx->xnonce1, sctx->xnonce1_size);

//		sctx->job.xnonce2 = sctx->job.coinbase + coinb1_size + sctx->xnonce1_size;
//		if (!sctx->job.job_id || strcmp(sctx->job.job_id, job_id))
//		memset(sctx->job.xnonce2, 0, sctx->xnonce2_size);
//		hex2bin(sctx->job.xnonce2 + sctx->xnonce2_size, coinb2, coinb2_size);
    }
}