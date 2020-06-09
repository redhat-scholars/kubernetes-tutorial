package org.acme;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize
@RegisterForReflection
public class PizzaResourceSpec {

    @JsonProperty("toppings")
    private List<String> toppings = new ArrayList<>();
    @JsonProperty("sauce")
    private String sauce;
    // getters/setters

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

    public String getSauce() {
        return sauce;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }
}