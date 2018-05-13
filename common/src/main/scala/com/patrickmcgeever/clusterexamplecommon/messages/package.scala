package com.patrickmcgeever.clusterexamplecommon

package object messages {
  final case class WorkerJob(number: Long)
  final case class TransformationResult(originalNum: Long, result: Long)
  final case class JobFailed(reason: String, job: WorkerJob)
  case object BackendRegistration
}
