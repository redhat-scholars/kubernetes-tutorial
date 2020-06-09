package org.acme;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResource;

@JsonDeserialize
public class PizzaResource extends CustomResource {

    private PizzaResourceSpec spec;
    private PizzaResourceStatus status;
    // getters/setters

    public PizzaResourceSpec getSpec() {
        return spec;
    }

    public void setSpec(PizzaResourceSpec spec) {
        this.spec = spec;
    }

    public PizzaResourceStatus getStatus() {
        return status;
    }

    public void setStatus(PizzaResourceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String name = getMetadata() != null ? getMetadata().getName() : "unknown";
        String version = getMetadata() != null ? getMetadata().getResourceVersion() : "unknown";
        return "name=" + name + " version=" + version + " value=" + spec;
    }
}