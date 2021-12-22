package com.myorg;

import software.amazon.awscdk.App;

public class CursoAwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpcStack = new VpcStack(app, "vpc");

        ClusterStack clusterStack = new ClusterStack(app, "cluster", vpcStack.getVpc());

        clusterStack.addDependency(vpcStack);

        app.synth();
    }
}

