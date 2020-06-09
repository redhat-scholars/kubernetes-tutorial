package org.acme;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.quarkus.runtime.StartupEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class PizzaResourceWatcher {

    @Inject
    KubernetesClient defaultClient;

    @Inject
    NonNamespaceOperation<PizzaResource, PizzaResourceList, PizzaResourceDoneable, Resource<PizzaResource, PizzaResourceDoneable>> crClient;

    void onStartup(@Observes StartupEvent event) {
        System.out.println("Startup");
        crClient.watch(new Watcher<PizzaResource>() {
            @Override
            public void eventReceived(Action action, PizzaResource resource) {
                System.out.println("Event " + action.name());
                if (action == Action.ADDED) {
                    final String app = resource.getMetadata().getName();
                    final String sauce = resource.getSpec().getSauce();
                    final List<String> toppings = resource.getSpec().getToppings();
                    final Map<String, String> labels = new HashMap<>();
                    labels.put("app", app);
                    final ObjectMetaBuilder objectMetaBuilder = new ObjectMetaBuilder().withName(app + "-pod")
                            .withNamespace(resource.getMetadata().getNamespace()).withLabels(labels);
                    final ContainerBuilder containerBuilder = new ContainerBuilder().withName("pizza-maker")
                            .withImage("quay.io/lordofthejars/pizza-maker:1.0.0").withCommand("/work/application")
                            .withArgs("--sauce=" + sauce, "--toppings=" + String.join(",", toppings));
                    final PodSpecBuilder podSpecBuilder = new PodSpecBuilder().withContainers(containerBuilder.build())
                            .withRestartPolicy("Never");
                    final PodBuilder podBuilder = new PodBuilder().withMetadata(objectMetaBuilder.build())
                            .withSpec(podSpecBuilder.build());
                    final Pod pod = podBuilder.build();
                    defaultClient.resource(pod).createOrReplace();

                }
            }

            @Override
            public void onClose(KubernetesClientException e) {
            }
        });
    }

}