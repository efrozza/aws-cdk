package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class Service01Stack extends Stack {

    public Service01Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);
        final int LISTENER_PORT = 8080;

        // o Fargate nao usa instancias EC2, roda de acordo com os recursos alocados

        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService
                .Builder.create(this, "ALB01")
                .serviceName("service-01")
                .cluster(cluster)
                // qtde de cpu
                .cpu(512)
                // instancias da aplicacao
                .desiredCount(2)
                // qual a porta
                .listenerPort(LISTENER_PORT)
                // memoria
                .memoryLimitMiB(1024)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("awsprojeto01")
                                .image(ContainerImage.fromRegistry("efrozza/awsprojeto01:1.0.0"))
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "Service01Group")
                                                .logGroupName("Service01")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .streamPrefix("Service01")
                                        .build()))
                                .build()
                )
                .publicLoadBalancer(true)
                .build();
    }
}
