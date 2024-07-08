description: "Stop EC2 instances at 6PM"
schemaVersion: "0.3"
assumeRole: "arn:aws:iam::992382822158:role/AutomationRole"
mainSteps:
  - name: stopInstances
    action: aws:changeInstanceState
    inputs:
      InstanceIds: ["i-0abcdef1234567890"]
      DesiredState: stopped
