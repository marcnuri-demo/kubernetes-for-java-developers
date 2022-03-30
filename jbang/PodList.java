///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.5.0
//DEPS io.fabric8:kubernetes-client:5.12.1


import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "PodList", mixinStandardHelpOptions = true, version = "PodList 0.1",
        description = "PodList made with jbang")
class PodList implements Callable<Integer> {

    public static void main(String... args) {
        int exitCode = new CommandLine(new PodList()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try (KubernetesClient kc = new DefaultKubernetesClient()) {
            System.out.println("Listing Pods");
            kc.pods().inAnyNamespace().list().getItems().forEach(pod ->
                System.out.printf("%s - %s%n", pod.getMetadata().getNamespace(), pod.getMetadata().getName())
            );
        }
        return 0;
    }
}
