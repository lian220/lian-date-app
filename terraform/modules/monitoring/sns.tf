# SNS Topic for CloudWatch Alarms
resource "aws_sns_topic" "alarms" {
  name = "${var.name_prefix}-alarms"

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-alarms-topic"
    }
  )
}

# SNS Topic Subscription (Email)
resource "aws_sns_topic_subscription" "alarms_email" {
  topic_arn = aws_sns_topic.alarms.arn
  protocol  = "email"
  endpoint  = var.alarm_email
}
