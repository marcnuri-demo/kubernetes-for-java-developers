///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.fabric8:kubernetes-client:5.12.1


import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class YamlSucks {

    public static void main(String... args) {
        final String namespace = "chuck-norris";
        final String appName = "chuck-norris-quotes";
        System.out.println("YAML sucks, let's use Java instead!");
        try (KubernetesClient kc = new DefaultKubernetesClient()) {
            final Namespace existent = kc.namespaces().withName(namespace).get();
            if (existent != null) {
                System.out.println("Previous deployment found, deleting...");
                kc.resource(existent).delete();
                try {
                    kc.namespaces().withName(namespace).waitUntilCondition(Objects::isNull, 30, TimeUnit.SECONDS);
                } catch (KubernetesClientException e) {
                    System.out.println("Error while deleting namespace");
                    System.exit(1);
                }
            }
            final Namespace ns = new NamespaceBuilder()
              .withNewMetadata().withName(namespace).endMetadata().build();
            kc.resource(ns).createOrReplace();
            System.out.println("Namespace created");
            Deployment deploy = new DeploymentBuilder()
              .withNewMetadata().withName(appName).endMetadata()
              .withNewSpec()
              .withReplicas(1)
              .withNewSelector()
              .addToMatchLabels("app", appName)
              .addToMatchLabels("group", namespace)
              .endSelector()
              .withNewTemplate()
              .withNewMetadata()
              .addToLabels("app", appName)
              .addToLabels("group", namespace)
              .endMetadata()
              .withNewSpec()
              .addNewContainer()
              .withName(appName)
              .withImage("marcnuri/chuck-norris:latest")
              .addNewPort()
              .withContainerPort(8080)
              .endPort()
              .endContainer()
              .endSpec()
              .endTemplate()
              .endSpec()
              .build();
            deploy = kc.resource(deploy).inNamespace(namespace).createOrReplace();
            System.out.println("Deployment created");
            Service svc = new ServiceBuilder()
              .withNewMetadata()
              .withName(appName)
              .endMetadata()
              .withNewSpec()
              .withType("NodePort")
              .addToSelector("app", appName)
              .addToSelector("group", namespace)
              .addNewPort()
              .withPort(8080)
              .withNewTargetPort(8080)
              .endPort()
              .endSpec()
              .build();
            svc = kc.resource(svc).inNamespace(namespace).createOrReplace();
            System.out.println("Service created");
            kc.resource(deploy).waitUntilReady(30, TimeUnit.SECONDS);
            System.out.println("Deployment is ready");
            final Optional<NodeAddress> address = kc.pods().inNamespace(namespace)
              .withLabel("app", appName).withLabel("group", namespace).list()
              .getItems().stream().map(p -> kc.nodes().withName(p.getSpec().getNodeName()).get())
              .flatMap(node -> node.getStatus().getAddresses().stream().filter(a -> a.getType().equals("InternalIP")))
              .findAny();
            if (address.isPresent()) {
                System.out.printf("Service accessible at: http://%s:%d%n",
                  address.get().getAddress(), svc.getSpec().getPorts().get(0).getNodePort());
            }
        }
    }
}
