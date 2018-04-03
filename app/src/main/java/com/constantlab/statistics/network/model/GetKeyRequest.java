package com.constantlab.statistics.network.model;

import java.io.Serializable;

/**
 * Created by Hayk on 16/02/2018.
 */

public class GetKeyRequest implements Serializable {
    private String user;
    private String password;

    public GetKeyRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
