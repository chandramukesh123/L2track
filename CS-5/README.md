Sure! Below are the detailed steps to create an automation document in AWS Systems Manager to start and stop EC2 instances using the AWS Management Console:

### Step-by-Step Guide

#### 1. Open AWS Systems Manager
1. Sign in to the AWS Management Console.
2. Open the **AWS Systems Manager** console at [AWS Systems Manager Console](https://console.aws.amazon.com/systems-manager).

#### 2. Create an Automation Document
1. In the left navigation pane, choose **Documents**.
2. Click **Create automation**.
3. Choose **Automation document**.
4. Enter a **Name** for your document, for example, `StartEC2Instances` or `StopEC2Instances`.
5. (Optional) Enter a **Description** for your document.
6. For **Document type**, choose `Automation`.

#### 3. Define the Document Content
1. In the **Document content** section, you can define the document using either JSON or YAML format. Below are the YAML templates for starting and stopping instances:

**Start EC2 Instances**

```yaml
description: "Start EC2 instances at 9PM"
schemaVersion: "0.3"
assumeRole: "arn:aws:iam::<account-id>:role/AutomationRole"
mainSteps:
  - name: startInstances
    action: aws:changeInstanceState
    inputs:
      InstanceIds: ["<instance-id-1>", "<instance-id-2>"]
      DesiredState: started
```

**Stop EC2 Instances**

```yaml
description: "Stop EC2 instances at 6PM"
schemaVersion: "0.3"
assumeRole: "arn:aws:iam::<account-id>:role/AutomationRole"
mainSteps:
  - name: stopInstances
    action: aws:changeInstanceState
    inputs:
      InstanceIds: ["<instance-id-1>", "<instance-id-2>"]
      DesiredState: stopped
```

2. Replace `<account-id>`, `<instance-id-1>`, and `<instance-id-2>` with your actual AWS account ID and instance IDs.

#### 4. Save the Document
1. Click **Create document** to save the automation document.

### Step-by-Step Guide for Scheduling Automation

#### 1. Open Amazon CloudWatch
1. Open the **Amazon CloudWatch** console at [Amazon CloudWatch Console](https://console.aws.amazon.com/cloudwatch).

#### 2. Create a Rule
1. In the left navigation pane, choose **Rules**.
2. Click **Create rule**.

#### 3. Define the Event Source
1. For **Event Source**, choose `Event Source` and then `Schedule`.
2. For **Schedule Expression**, enter the desired CRON expression:

   **Start EC2 Instances at 9 PM UTC**: `cron(0 21 * * ? *)`

   **Stop EC2 Instances at 6 PM UTC**: `cron(0 18 * * ? *)`

#### 4. Add Target
1. Click **Add target**.
2. For **Target**, choose `SSM Automation`.
3. For **Document**, select the Automation document you created earlier (`StartEC2Instances` or `StopEC2Instances`).
4. For **Document version**, choose `Latest`.

#### 5. Configure Input Parameters
1. If your document requires input parameters (e.g., Instance IDs), configure them accordingly. 
2. Click **Configure details**.

#### 6. Define Rule Name and Description
1. Enter a **Name** and **Description** for your rule.
2. Ensure the rule is enabled.

#### 7. Create Rule
1. Click **Create rule**.

### Summary
1. **Systems Manager**: Create an automation document to start and stop EC2 instances.
2. **CloudWatch**: Create a scheduled rule to trigger the automation document.

This setup ensures that your EC2 instances are started and stopped automatically at the specified times, helping manage costs effectively. Let me know if you need further assistance!

============================================================================================================================================

Sure! Here are the detailed steps to configure Amazon CloudWatch Events to trigger a Lambda function when a new file is uploaded to an S3 bucket:

### Step-by-Step Guide

#### 1. Create a Lambda Function

1. **Open the AWS Lambda Console**:
   - Go to the AWS Management Console.
   - Open the **AWS Lambda** console at [AWS Lambda Console](https://console.aws.amazon.com/lambda).

2. **Create a New Lambda Function**:
   - Click **Create function**.
   - Choose **Author from scratch**.
   - Enter a function name, e.g., `ProcessS3File`.
   - Choose a runtime, e.g., `Python 3.9`.
   - Click **Create function**.

3. **Add the Lambda Code**:
   - In the function's code editor, add the code to process the file, count the words, and update the `count.txt` file. Here's a sample code snippet:

```python
import boto3
import os
from datetime import datetime

s3 = boto3.client('s3')

def lambda_handler(event, context):
    bucket_name = 'my-bucket'
    count_folder = 'count'
    out_folder = 'out'
    
    # Get the file key from the event
    file_key = event['Records'][0]['s3']['object']['key']
    
    if file_key.startswith(out_folder) and file_key.endswith('.txt'):
        # Read the file content
        file_obj = s3.get_object(Bucket=bucket_name, Key=file_key)
        file_content = file_obj['Body'].read().decode('utf-8')
        
        # Count words in the file
        word_count = len(file_content.split())
        
        # Prepare the count.txt content
        execution_date = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S')
        count_content = f"Word Count: {word_count}, Execution Date: {execution_date}\n"
        
        # Append to count.txt in /count folder
        count_key = f'{count_folder}/count.txt'
        try:
            count_obj = s3.get_object(Bucket=bucket_name, Key=count_key)
            existing_content = count_obj['Body'].read().decode('utf-8')
        except s3.exceptions.NoSuchKey:
            existing_content = ''
        
        new_content = existing_content + count_content
        s3.put_object(Bucket=bucket_name, Key=count_key, Body=new_content)

        return {
            'statusCode': 200,
            'body': 'File processed successfully'
        }

    return {
        'statusCode': 400,
        'body': 'File not processed'
    }
```

4. **Set Up IAM Role**:
   - Ensure that the Lambda function has the necessary IAM role with permissions to access S3 and CloudWatch logs. Attach the AWS managed policies `AWSLambdaBasicExecutionRole` and `AmazonS3ReadOnlyAccess`.

5. **Save the Function**:
   - Click **Deploy** to save the Lambda function.

#### 2. Configure S3 to Trigger the Lambda Function

1. **Open the S3 Console**:
   - Go to the AWS Management Console.
   - Open the **Amazon S3** console at [Amazon S3 Console](https://console.aws.amazon.com/s3).

2. **Select Your Bucket**:
   - Choose the bucket where you want to trigger the Lambda function on file uploads, e.g., `my-bucket`.

3. **Go to the Properties Tab**:
   - In the bucket's details page, select the **Properties** tab.

4. **Add Notification**:
   - Scroll down to the **Event notifications** section.
   - Click **Create event notification**.
   - Enter a name for the notification, e.g., `NewFileUpload`.
   - For **Event types**, select `All object create events`.
   - In the **Prefix** box, enter `out/` to specify that the trigger should only occur for uploads to the `out` folder.
   - Leave the **Suffix** box blank to trigger on all files, or specify `.txt` to only trigger on `.txt` files.

5. **Choose Lambda Function**:
   - For the **Send to** option, select **Lambda Function**.
   - In the **Lambda function** dropdown, select the `ProcessS3File` Lambda function you created.

6. **Save the Notification**:
   - Click **Save changes** to configure the event notification.

### Summary
1. **Lambda Function**: Create and deploy a Lambda function to process the S3 files.
2. **S3 Bucket Configuration**: Configure the S3 bucket to trigger the Lambda function upon file upload to the specified folder.
3. **IAM Role**: Ensure appropriate permissions are set for the Lambda function to access S3 and CloudWatch logs.

This setup ensures that every time a new file is uploaded to the specified folder in your S3 bucket, the Lambda function will be triggered to process the file.

