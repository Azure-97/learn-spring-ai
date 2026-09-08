package com.example.moremodelstructuredagent;

import java.util.Map;

public class AiJob {
     record Job(JobType jobType, Map keyInfos) {
    }
    public enum JobType{
        CANCEL,
        QUERY,
        OTHER,
    }
}