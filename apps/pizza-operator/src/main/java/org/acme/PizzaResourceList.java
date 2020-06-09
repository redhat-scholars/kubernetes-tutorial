package org.acme;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.client.CustomResourceList;

@JsonSerialize
public class PizzaResourceList extends CustomResourceList<PizzaResource> {

}