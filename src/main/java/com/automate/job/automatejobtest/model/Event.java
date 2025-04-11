package com.automate.job.automatejobtest.model;

import com.google.gson.JsonArray;
import java.util.List;

public interface Event {
  void addJob();
  void FinishOne(String jobName,String finishCost);
  void oneException(AbstractJobDefine jobDefine,Exception e);
  void  FinshAll();
  List<TestCaseModel> toJsonString();
  void clear();
}
