import boto3
from datetime import datetime, timedelta

def lambda_handler(event, context):
    # Define the age threshold
    days_threshold = 14
    threshold_date = datetime.utcnow() - timedelta(days=days_threshold)
    
    # Initialize EC2 client
    ec2 = boto3.client('ec2')
    
    # Describe snapshots
    snapshots = ec2.describe_snapshots(OwnerIds=['self'])['Snapshots']
    
    # Filter and delete old snapshots
    for snapshot in snapshots:
        start_time = snapshot['StartTime']
        if start_time < threshold_date:
            snapshot_id = snapshot['SnapshotId']
            ec2.delete_snapshot(SnapshotId=snapshot_id)
            print(f"Deleted snapshot: {snapshot_id}")
    
    return {
        'statusCode': 200,
        'body': 'Old snapshots deleted successfully'
    }
