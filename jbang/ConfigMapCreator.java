///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS io.fabric8:kubernetes-client:5.12.1

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.UUID;

public class ConfigMapCreator {

  public static void main(String... args) {
    try (KubernetesClient kc = new DefaultKubernetesClient()) {
      final ConfigMap cm = new ConfigMapBuilder()
        .withNewMetadata()
        .withName("cm-example-" + UUID.randomUUID())
        .endMetadata().build();
      kc.resource(cm).inNamespace("default").createOrReplace();
    }
  }
}
