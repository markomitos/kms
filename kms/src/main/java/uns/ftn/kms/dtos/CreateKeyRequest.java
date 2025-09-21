package uns.ftn.kms.dtos;

import lombok.Data;

@Data
public class CreateKeyRequest {
    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}