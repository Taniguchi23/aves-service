package com.utpsistemas.distribuidoraavesservice.auth.helper;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import org.hashids.Hashids;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Hashid {
    private final Hashids hashids;

    public Hashid() {
        this.hashids = new Hashids("avedistri-secret-key", 6);
    }

    public String encode(Long id) {
        return hashids.encode(id);
    }

    public Long decode(String hashedId) {
        long[] decoded = hashids.decode(hashedId);
        if (decoded.length == 0) throw  new ApiException("ID inválido o malformado", HttpStatus.BAD_REQUEST);
        return decoded[0];
    }
}
