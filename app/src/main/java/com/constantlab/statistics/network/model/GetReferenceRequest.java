package com.constantlab.statistics.network.model;

/**
 * Created by Hayk on 16/02/2018.
 */

public class GetReferenceRequest {
    private String key;
    private String ref_type;

    public GetReferenceRequest(String key, String ref_type) {
        this.key = key;
        this.ref_type = ref_type;
    }

}
