package com.tmobile.pacbot.gcp.inventory.auth;

import com.google.api.apikeys.v2.ApiKeysClient;
import com.google.api.apikeys.v2.ApiKeysSettings;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudtasks.v2.CloudTasks;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.sqladmin.SQLAdmin;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.compute.v1.*;
import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.cloud.container.v1.ClusterManagerSettings;
import com.google.cloud.dataproc.v1.ClusterControllerClient;
import com.google.cloud.dataproc.v1.ClusterControllerSettings;
import com.google.cloud.dns.Dns;
import com.google.cloud.dns.DnsOptions;
import com.google.cloud.functions.v1.CloudFunctionsServiceClient;
import com.google.cloud.functions.v1.CloudFunctionsServiceSettings;
import com.google.cloud.functions.v2.FunctionServiceClient;
import com.google.cloud.functions.v2.FunctionServiceSettings;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
@Component
public class GCPCredentialsProvider {

    private static final Logger logger = LoggerFactory.getLogger(GCPCredentialsProvider.class);

    private InstancesClient instancesClient;
    private FirewallsClient firewallsClient;

    private BigQueryOptions.Builder bigQueryBuilder;

    private TopicAdminClient topicAdminClient;

    private Storage storageClient;

    private SQLAdmin sqlAdmin;

    private KeyManagementServiceClient kmsKeyServiceClient;
    private ClusterManagerClient clusterManagerClient;
    private ZonesClient zonesClient;
    private Dns dns;
    private NetworksClient networksClient;
    private CloudResourceManager cloudResourceManager;
    private Iam iamService;
    private UrlMapsClient urlMap;
    private TargetHttpProxiesClient targetHttpProxiesClient;

    private BackendServicesClient backendService;
    private TargetSslProxiesClient targetSslProxiesClient;

    private TargetHttpsProxiesClient targetHttpsProxiesClient;

    private SslPoliciesClient sslPoliciesClient;

    private ApiKeysClient apiKeysClient;

    // If you don't specify credentials when constructing the client, the client
    // library will
    // look for credentials via the environment variable
    // GOOGLE_APPLICATION_CREDENTIALS.

    private GoogleCredentials getCredentials() throws IOException {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise, credentials are read from the GOOGLE_APPLICATION_CREDENTIALS
        // environment variable.

        // print the path to the credential file

        String cred = System.getProperty("gcp.credentials");
        if (cred.isEmpty()) {
            logger.error("GCP cred string is null!!!!!!!");
        }

        return GoogleCredentials.fromStream(new ByteArrayInputStream(cred.getBytes()))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
    }

    public String getAccessToken() throws IOException {
        String cred = System.getProperty("gcp.credentials");
        String token=GoogleCredentials.fromStream(new ByteArrayInputStream(cred.getBytes()))
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
                .refreshAccessToken().getTokenValue();
        return token.trim().replaceAll("\\.+$", "").toString();
    }

    public NetworksClient getNetworksClient() throws Exception{
        if(networksClient==null){
            NetworksSettings networksSettings=NetworksSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            networksClient=NetworksClient.create(networksSettings);

        }


        return networksClient;

    }
    public InstancesClient getInstancesClient() throws IOException {

        if (instancesClient == null) {
            // pass authentication credentials to the client
            InstancesSettings instancesSettings = InstancesSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials()))
                    .build();
            instancesClient = InstancesClient.create(instancesSettings);
        }
        return instancesClient;
    }

    public FirewallsClient getFirewallsClient() throws IOException {
        if (firewallsClient == null) {
            // pass authentication credentials to the client
            FirewallsSettings firewallsSettings = FirewallsSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials()))
                    .build();
            firewallsClient = FirewallsClient.create(firewallsSettings);
        }
        return firewallsClient;
    }

    public BigQueryOptions.Builder getBigQueryOptions() throws IOException {
        if (bigQueryBuilder == null) {
            bigQueryBuilder = BigQueryOptions.newBuilder().setCredentials(this.getCredentials());
        }
        return bigQueryBuilder;
    }

    public Storage getStorageClient() throws IOException {
        if (storageClient == null) {
            storageClient = StorageOptions.newBuilder().setCredentials(this.getCredentials()).build().getService();
        }

        return storageClient;
    }

    public SQLAdmin getSqlAdminService() throws IOException, GeneralSecurityException {
        if (sqlAdmin == null) {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            sqlAdmin = new SQLAdmin.Builder(httpTransport, jsonFactory,
                    new HttpCredentialsAdapter(this.getCredentials()))
                    .build();
        }
        return sqlAdmin;
    }

    public KeyManagementServiceClient getKmsKeyServiceClient() throws IOException {
        if (kmsKeyServiceClient == null) {
            KeyManagementServiceSettings keyManagementServiceSettings = KeyManagementServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials()))
                    .build();
            kmsKeyServiceClient = KeyManagementServiceClient.create(keyManagementServiceSettings);
        }
        return kmsKeyServiceClient;
    }

    public TopicAdminClient getTopicClient() throws IOException {
        if (topicAdminClient == null) {
            TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            topicAdminClient = TopicAdminClient.create(topicAdminSettings);
        }

        return topicAdminClient;
    }

    public FunctionServiceClient getFunctionClient() throws IOException {
        FunctionServiceSettings functionServiceSettings=FunctionServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
        return FunctionServiceClient.create(functionServiceSettings);
    }

    public ClusterControllerClient getDataProcClient(String region) throws IOException {
        String url = region + "-dataproc.googleapis.com:443";
        ClusterControllerSettings clusterControllerSettings = ClusterControllerSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).setEndpoint(url)
                .build();
        return ClusterControllerClient.create(clusterControllerSettings);
    }

    public CloudTasks createCloudTasksService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        return new CloudTasks.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(this.getCredentials()))
                .build();
    }

    public ZonesClient Zonesclient() throws IOException, GeneralSecurityException {
        if (zonesClient == null) {
            ZonesSettings zonesSettings = ZonesSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            zonesClient = ZonesClient.create(zonesSettings);
        }

        return zonesClient;
    }

    public ClusterManagerClient getClusterManagerClient() throws IOException {

        if (clusterManagerClient == null) {
            ClusterManagerSettings clusterManagerSettings = ClusterManagerSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            clusterManagerClient = ClusterManagerClient.create(clusterManagerSettings);

        }
        return clusterManagerClient;
    }
    public Dns createCloudDNSServices() throws IOException {
        if (dns == null) {
            dns = DnsOptions.newBuilder().setCredentials(this.getCredentials()).build().getService();

        }
        return dns;
    }

    public CloudResourceManager getCloudResourceManager() throws IOException, GeneralSecurityException {
        if (cloudResourceManager == null) {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            cloudResourceManager= new CloudResourceManager.Builder(httpTransport,
                    jsonFactory, new HttpCredentialsAdapter(this.getCredentials())).build();
        }
        return cloudResourceManager;
    }

    public  Iam getIamService() throws  IOException, GeneralSecurityException{
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        if(iamService==null){
         iamService = new Iam.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(this.getCredentials())).build();
        }
       return  iamService;
    }

    public ApiKeysClient getApiKeysService() throws Exception{
        if(apiKeysClient==null){
            ApiKeysSettings apiKeysSettings = ApiKeysSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
             apiKeysClient=   ApiKeysClient.create(apiKeysSettings);
        }
        return apiKeysClient;
    }

    public UrlMapsClient getURLMap() throws IOException {
        if(urlMap==null)
        {
           UrlMapsSettings urlMapsSettings= UrlMapsSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            urlMap=UrlMapsClient.create(urlMapsSettings);
        }
        return urlMap;
    }

    public TargetHttpProxiesClient getTargetHttpProxiesClient() throws IOException {
        if (targetHttpProxiesClient == null) {
            TargetHttpProxiesSettings targetHttpProxiesSettings = TargetHttpProxiesSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            targetHttpProxiesClient = TargetHttpProxiesClient.create(targetHttpProxiesSettings);
        }
        return targetHttpProxiesClient;
    }

    public BackendServicesClient getBackendServiceClient() throws IOException {
        if(backendService==null)
        {
           BackendServicesSettings backendServicesSettings= BackendServicesSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            backendService=BackendServicesClient.create(backendServicesSettings);
        }
        return backendService;
    }

    public TargetSslProxiesClient getTargetSslProxiesClient() throws IOException{
        if(targetSslProxiesClient == null){
            TargetSslProxiesSettings targetSslProxiesSettings=TargetSslProxiesSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            targetSslProxiesClient=TargetSslProxiesClient.create(targetSslProxiesSettings);
        }
        return targetSslProxiesClient;
    }

    public TargetHttpsProxiesClient getTargetHttpsProxiesClient() throws IOException{
        if(targetHttpsProxiesClient == null){
            TargetHttpsProxiesSettings targetHttpsProxiesSettings=TargetHttpsProxiesSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            targetHttpsProxiesClient=TargetHttpsProxiesClient.create(targetHttpsProxiesSettings);
        }
        return  targetHttpsProxiesClient;
    }

    public  SslPoliciesClient getSslPoliciesClient() throws  IOException{
        if(sslPoliciesClient == null){
            SslPoliciesSettings sslPoliciesSettings=SslPoliciesSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
            sslPoliciesClient=SslPoliciesClient.create(sslPoliciesSettings);
        }
        return sslPoliciesClient;
    }



    public CloudFunctionsServiceClient getFunctionClientGen1() throws IOException {
        CloudFunctionsServiceSettings functionsServiceSettings = CloudFunctionsServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(this.getCredentials())).build();
        return CloudFunctionsServiceClient.create(functionsServiceSettings);
    }

    // close the client in destroy method
    @PreDestroy
    public void destroy() {
        if (instancesClient != null) {
            logger.debug("closing client");

            instancesClient.close();
        }
    }
}
