package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Collections;

public class RdsStack extends Stack {
    public RdsStack(final Construct scope, final String id, Vpc vpc) {

        this(scope, id, null, vpc);
    }

    public RdsStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        // usuario e senha do DB

        CfnParameter databasePassword = CfnParameter.Builder.create(this, "databasePassword")
                .type("String")
                .description("The RDS Instance password")
                .build();

        // Porta de acesso

        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306));

        // criar instancia RDS

        DatabaseInstance databaseInstance = DatabaseInstance.Builder
                // nome da instancia a ser criada
                .create(this, "Rds01")
                .instanceIdentifier("aws-project01-db")
                // engine de banco de dados a ser utilizado
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.VER_5_7)
                        .build()))
                // essa instancia do RDS sera criada dentro na nossa vpc
                .vpc(vpc)
                // usaurio e senha da instncia RDS
                .credentials(Credentials.fromUsername("admin",
                        CredentialsFromUsernameOptions.builder()
                        .password(SecretValue.plainText(databasePassword.getValueAsString()))
                        .build()))
                // tamanho da maquina que vai executar a instancia
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                // permite varia zonas
                .multiAz(false)
                // tamanho do disco, 10GB
                .allocatedStorage(10)
                // Definido o security group da instancia
                .securityGroups(Collections.singletonList(iSecurityGroup))
                // subrede da instancia
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPrivateSubnets())
                        .build())
                .build();

        // Expondo o endpoint do banco de dados - Sera utilizado pelo spring boot
        // para conectar ao banco

        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress())
                .build();

        // Exportando a senha de acesso

        CfnOutput.Builder.create(this, "rds-passoword")
                .exportName("rds-password")
                .value(databasePassword.getValueAsString())
                .build();

    }
}
