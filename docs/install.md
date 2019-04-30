### Platform Installation

1. **Create AWS Account**
    If you already have an AWS account, skip this step. Reference the [tutorial](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/).

2. **Choose an AWS Region where to deploy**

    Choose a region for your main deployment. Some AWS Services required by RTF are not available in all AWS Regions. Check this [table](<https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services>) and choose an AWS Region which supports:

    ECS, ECR, EFS and SQS, Fargate for the new upcoming RTF release.

3. **(Optional) Register a New Domain** 
    To run RTF you will need to have a domain, the ability to create subdomains and manage DNS records.  If you already have a domain name, skip this step. AWS Route 53 provides this [service](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/domain-register.html) as well.

4. **Get SSL/TLS Certificate for Domain**
    You can request a publicly trusted certificate issued by AWS Certificate Manager or import an existing certificate. Reference the [tutorial](https://docs.aws.amazon.com/acm/latest/userguide/gs-acm-request-public.html).

    ![arn_acm_cert](img/arn_acm_cert.png)

    ###### Save the ARN for the generated/imported certificate

5. **(Optional) Create Key Pair**
    If you require SSH access to the EC2 instances created as part of the RTF deployment, generate a Key Pair. Reference the [tutorial](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#having-ec2-create-your-key-pair).

6. **Import and tweak AWS CloudFormation template**
    On the AWS console, browse to CloudFormation. You will need to import, tweak and run the [RTF master template](https://s3-eu-west-1.amazonaws.com/rtf-public-templates/rtf-template.yaml). This will automatically create the RTF infrastructure and deploy all services. Reference the [tutorial](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-using-console-create-stack-template.html).

   - Click on "Create New Stack"

   - Select "Specify an Amazon S3 Template Url" and enter the RTF public template:

     ```
     https://s3-eu-west-1.amazonaws.com/rtf-public-templates/rtf-template.yaml
     ```

     ![select_template](img/select_template.png)

   - Click on "View/Edit template in Designer"

   - Tweak the templated editing these items to match your configuration:

    ```
    ALB:
     #Replace with AWS ACM TLS Certificate ARN (From Step 3)
     LoadBalancerCertificateArn: arn:aws:elasticloadbalancing:us-west-2:123456789012:certificate/50dc6c495c0c9188/f2f7dc8efc522ab2
    ServicesECS:
     #Choose EC2 Instance Type to run RTF-Services (https://aws.amazon.com/ec2/instance-types/)
     InstanceType: t2.medium
     #EC2 Autoscaling Min/Max Instances to launch in the RTF Services ECS Cluster
     MinClusterSize: 1
     MaxClusterSize: 4
    ExercisesECS:
      #Choose EC2 Instance Type to run RTF-Exercises, more memory = more concurrent RTF exercises (https://aws.amazon.com/ec2/instance-types/)
     InstanceType: m4.large
      #EC2 Autoscaling Min/Max Instances to launch in the RTF Exercises ECS Cluster, more memory = more concurrent RTF exercises
     MinClusterSize: 1
     MaxClusterSize: 4
    MysqlService:
     #Replace Passwords, match them in the other services in this template
     GuacRTFAdminUserPassword: REPLACE-GUAC-ADMIN-PASSWORD
     GlobalRTFAdminUserPassword: REPLACE-GLOBAL-ADMIN-PASSWORD
     MysqlGlobalUserPassword: REPLACE-MYSQL-GLOBAL-PASSWORD
     MysqlGuacUserPassword: REPLACE-MYSQL-GUAC-PASSWORD
     MysqlRootUserPassword: REPLACE-MYSQL-ROOT-PASSWORD
    GlobalService:
     #ECS Service Autoscaling, Min/Max number of concurrent ECS tasks (Docker containers) for the RTF-Global (Portal) Service
     DesiredCount: 1
     MaxCount: 4
     #Replace Passwords, match them in the other services in this template
     GuacRTFAdminUserPassword: REPLACE-GUAC-ADMIN-PASSWORD
     MysqlGlobalUserPassword: REPLACE-MYSQL-GLOBAL-PASSWORD
     GatewayAgentPassword: REPLACE-GATEWAY-AGENT-PASSWORD
     #Choose hostname for RTF deployment (e.g remediatetheflag.net)
     GlobalHostname: REPLACE-PORTAL-FQDN
    GatewayService:
     #ECS Service Autoscaling, Min/Max number of concurrent ECS tasks (Docker containers) for Gateway Service
     DesiredCount: 1
     MaxCount: 4
     #Choose hostname for the RTF Gateway deployment (e.g emea.remediatetheflag.net)
     GatewayHostname: REPLACE-GATEWAY-FQDN
     #Replace Passwords, match them in the other services in this template
     MysqlGuacUserPassword: REPLACE-MYSQL-GUAC-PASSWORD
     GatewayAgentPassword: REPLACE-GATEWAY-AGENT-PASSWORD
    ```

6. **Run AWS CloudFormation template**

      - Click on "Create Stack", then click on "Next", 

      - Give a name to the stack (e.g. RTF-Deployment-US-East), click on "Next"

      - Scroll down and click on "Next"

      - Scroll down and check "I acknowledge that AWS CloudFormation might create IAM resources with custom names."

      - Click on "Create"

      - Wait ~ 11 minutes

      - When the stack is created, click on the root stack name (e.g. RTF-Deployment-US-East), click on "Outputs" and note the URL for the Application Load Balancer.

        ![ALB Output](img/alb_output.png)

        

      - Modify the DNS records for your domain, adding a CNAME to the ALB Url. Add a CNAME for the RTF-PORTAL (e.g. www.remediatetheflag.com) and the RTF-GATEWAY (emea.remediatetheflag.com) pointing to the ALB url.



### Additional Regional Clusters / Gateways (Optional)

The RTF Gateway mediates the access to an RTF Exercise running in an ECS cluster. To increase concurrent exercise capacity, deploy additional Exercise Clusters / Regional Gateways in AWS regions geographically close to your user - this helps reduce the latency of the underlying RDP connection between RTF-Gateway and RTF-Exercise.

  ![regional_gateways](img/regions_rtf.png)

  

It's possible to deploy additional Regional Clusters/Gateways using the public CloudFormation [regional template](https://s3-eu-west-1.amazonaws.com/rtf-public-templates/rtf-template-regional-gateway.yaml). Tweak the configuration of the template referencing the instructions in the [Installation](install.md) section (steps 6-7). A valid domain and TLS certificate ARN are required before running the template.

- Run the CloudFormation template in a different region from the main RTF deployment, the template configures the following:
    - RTF ECS Exercise Cluster: runs RTF Exercises

    - RTF ECS Service Cluster 
      * RTF Gateway: mediates access to the RTF Exercises run in the ECS Exercise cluster.

      - RTF Database (ephemeral): provides database for RTF Gateway, data is temporary.
      - Application Load Balancer (ALB) to route the traffic to the RTF Gateway, exposes 80 and 443.
- After creation, <u>modify the DNS entry for the hostname of the new RTF Gateway with the Application Load Balancer URL</u>, reference the instructions in the [Installation](install.md) section (step 7) of the documentation.
- To enable the newly deployed RTF Exercise Cluster you need to <u>onboard the RTF Gateway in the management console of the RTF Portal</u>, reference the instructions in the [Configuration](configure.md) (step 4) section of the documentation .

