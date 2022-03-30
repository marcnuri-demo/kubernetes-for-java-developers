///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-bom:2.7.5.Final@pom
//DEPS io.quarkus:quarkus-core
//DEPS io.quarkus:quarkus-arc
//DEPS io.quarkus:quarkus-kubernetes-client


import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class ConfigMapController implements QuarkusApplication {

  private static final String ANNOTATION = "com.example/cm";
  @Inject
  KubernetesClient kc;

  @Override
  public int run(String... args) {
    kc.configMaps().inNamespace("default").inform(new ResourceEventHandler<ConfigMap>() {
      @Override
      public void onAdd(ConfigMap configMap) {
        addAnnotation(configMap);
      }

      @Override
      public void onUpdate(ConfigMap oldConfigMap, ConfigMap configMap) {
        addAnnotation(configMap);
      }

      @Override
      public void onDelete(ConfigMap configMap, boolean b) {

      }

      private void addAnnotation(ConfigMap original) {
        if (original.getMetadata().getAnnotations() == null || !original.getMetadata().getAnnotations().containsKey(ANNOTATION)) {
          System.out.printf("Adding annotation to %s%n", original.getMetadata().getName());
          kc.resource(original).edit(cm -> new ConfigMapBuilder(cm)
            .editOrNewMetadata().addToAnnotations(ANNOTATION, "true")
            .endMetadata().build());
        }
      }
    });
    System.out.println("ConfigMap controller started");
    Quarkus.waitForExit();
    return 0;
  }
}
