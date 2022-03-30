# JBang

## Create a new CLI script

To initiate a new script with a CLI template, run:
```shell
jbang  init -t cli YourScript.java
```

## Edit the script

To edit the script in your editor and make sure dependencies are reloaded, run:
```shell
jbang edit --open='/home/user/00-MN/bin/ideaIU-2021/bin/idea.sh' --live YourScript.java
```

To persist the default editor setting:
```shell
jbang config set edit.open "/home/user/00-MN/bin/ideaIU-2021/bin/idea.sh"
```

## Run the script

From local:
```shell
jbang YourScript.java
```

From remote:
```shell
jbang https://github.com/marcnuri-demo/kubernetes-for-java-developers/blob/main/jbang/PodList.java
```
