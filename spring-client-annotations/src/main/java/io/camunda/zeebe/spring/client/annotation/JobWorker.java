package io.camunda.zeebe.spring.client.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobWorker {

  String type() default ""; // set to empty string which leads to method name being used (if not ${zeebe.client.worker.default-type}" is configured) Implemented in ZeebeWorkerAnnotationProcessor

  String name() default ""; // set to empty string which leads to default from ZeebeClientBuilderImpl being used in ZeebeWorkerAnnotationProcessor

  /**
   * Set the time (in milliseconds) for how long a job is exclusively assigned for this worker.
   * In this time, the job can not be assigned by other workers to ensure that only one worker work on the job.
   * When the time is over then the job can be assigned again by this or other worker if it's not completed yet.
   * If no timeout is set, then the default is used from the configuration.
   */
  long timeout() default -1L;

  /**
   * Set the maximum number of jobs which will be exclusively activated for this worker at the same time.
   * This is used to control the backpressure of the worker. When the maximum is reached then the worker will stop activating new jobs
   * in order to not overwhelm the client and give other workers the chance to work on the jobs.
   * The worker will try to activate new jobs again when jobs are completed (or marked as failed).
   * If no maximum is set then the default, from the ZeebeClientConfiguration, is used.
   * <br/><br/>
   * Considerations:
   * A greater value can avoid situations in which the client waits idle for the broker to provide more jobs. This can improve the worker's throughput.
   * The memory used by the worker is linear with respect to this value.
   * The job's timeout starts to run down as soon as the broker pushes the job.
   * Keep in mind that the following must hold to ensure fluent job handling:
   * <pre>time spent in queue + time job handler needs until job completion < job timeout</pre>
   */
  int maxJobsActive() default -1;

  /**
   * Set the request timeout (in seconds) for activate job request used to poll for new job.
   * If no request timeout is set then the default is used from the {@link io.camunda.zeebe.client.ZeebeClientConfiguration ZeebeClientConfiguration}
   */
  long requestTimeout() default -1L;

  /**
   * Set the maximal interval (in milliseconds) between polling for new jobs.
   * A job worker will automatically try to always activate new jobs after completing jobs.
   * If no jobs can be activated after completing the worker will periodically poll for new jobs.
   * If no poll interval is set then the default is used from the {@link io.camunda.zeebe.client.ZeebeClientConfiguration ZeebeClientConfiguration}
   */
  long pollInterval() default -1L;

  /**
   * Set a list of variable names which should be fetch on job activation.
   * The jobs which are activated by this worker will only contain variables from this list.
   * This can be used to limit the number of variables of the activated jobs.
   */
  String[] fetchVariables() default {};

  /**
   * If set to true, all variables are fetched
   */
  boolean fetchAllVariables() default false;

  /**
   * If set to true, the job is automatically completed after the worker code has finished.
   * In this case, your worker code is not allowed to complete the job itself.
   *
   *  You can still throw exceptions if you want to raise a problem instead of job completion.
   *  You could also raise a BPMN problem throwing a {@link io.camunda.zeebe.spring.client.exception.ZeebeBpmnError}
   */
  boolean autoComplete() default true;

  boolean enabled() default true;
}
