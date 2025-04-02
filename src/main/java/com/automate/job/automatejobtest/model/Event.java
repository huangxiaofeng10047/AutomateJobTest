package com.automate.job.automatejobtest.model;

public interface Event {
  void addJob();
  void FinishOne(String jobName,String finishCost);
  void oneException(AbstractJobDefine jobDefine,Exception e);
  void  FinshAll();
}
