package org.acme;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class PizzaResourceDoneable extends CustomResourceDoneable<PizzaResource> {

    public PizzaResourceDoneable(PizzaResource resource, Function<PizzaResource, PizzaResource> function) {
        super(resource, function);
    }
}