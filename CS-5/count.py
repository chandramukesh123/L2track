import boto3
import os
from datetime import datetime

s3 = boto3.client('s3')

def lambda_handler(event, context):
    bucket_name = 'mukesh-bucket-count34rdyh'
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
        
        # Prepare the count.txt execution date
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
