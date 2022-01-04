package com.myorg;

import software.amazon.awscdk.App;

public class CursoAwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpcStack = new VpcStack(app, "vpc");

        ClusterStack clusterStack = new ClusterStack(app, "cluster", vpcStack.getVpc());
        clusterStack.addDependency(vpcStack);

        // as stacks tem uma ordem para serem criadas, conforme dependencias

        RdsStack rdsStack = new RdsStack(app, "Rds", vpcStack.getVpc());
        rdsStack.addDependency(vpcStack);

        Service01Stack service01Stack = new Service01Stack(app, "Service01", clusterStack.getCluster());
        service01Stack.addDependency(clusterStack);
        service01Stack.addDependency(rdsStack);


        app.synth();
    }
}

